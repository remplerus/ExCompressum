package net.blay09.mods.excompressum.compat.jei;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.blay09.mods.excompressum.ExCompressum;
import net.blay09.mods.excompressum.api.ExNihiloProvider;
import net.blay09.mods.excompressum.api.LootTableProvider;
import net.blay09.mods.excompressum.api.sievemesh.SieveMeshRegistryEntry;
import net.blay09.mods.excompressum.block.HeavySieveBlock;
import net.blay09.mods.excompressum.block.ModBlocks;
import net.blay09.mods.excompressum.item.ModItems;
import net.blay09.mods.excompressum.newregistry.compressedhammer.CompressedHammerRecipe;
import net.blay09.mods.excompressum.newregistry.hammer.HammerRecipe;
import net.blay09.mods.excompressum.registry.ExNihilo;
import net.blay09.mods.excompressum.registry.ExRegistries;
import net.blay09.mods.excompressum.api.Hammerable;
import net.blay09.mods.excompressum.newregistry.heavysieve.HeavySieveRegistry;
import net.blay09.mods.excompressum.newregistry.heavysieve.GeneratedHeavySieveRecipe;
import net.blay09.mods.excompressum.newregistry.heavysieve.HeavySieveRecipe;
import net.blay09.mods.excompressum.registry.sievemesh.SieveMeshRegistry;
import net.blay09.mods.excompressum.registry.woodencrucible.WoodenCrucibleMeltable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.loot.LootTable;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@JeiPlugin
public class JEIAddon implements IModPlugin {

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        List<JeiHeavySieveRecipe> jeiHeavySieveRecipes = new ArrayList<>();

        RecipeManager recipeManager = Minecraft.getInstance().world.getRecipeManager();

        List<HeavySieveRecipe> heavySieveRecipes = recipeManager.getRecipesForType(HeavySieveRecipe.TYPE);
        for (HeavySieveRecipe recipe : heavySieveRecipes) {
            jeiHeavySieveRecipes.add(new JeiHeavySieveRecipe(recipe));
        }

        List<GeneratedHeavySieveRecipe> generatedHeavySieveRecipes = recipeManager.getRecipesForType(GeneratedHeavySieveRecipe.TYPE);
        for (GeneratedHeavySieveRecipe recipe : generatedHeavySieveRecipes) {
            loadGeneratedHeavySieveRecipe(false, recipe, jeiHeavySieveRecipes);
            loadGeneratedHeavySieveRecipe(true, recipe, jeiHeavySieveRecipes);
        }

        registry.addRecipes(jeiHeavySieveRecipes, HeavySieveRecipeCategory.UID);

        List<JeiCompressedHammerRecipe> jeiCompressedHammerRecipes = new ArrayList<>();
        List<CompressedHammerRecipe> compressedHammerRecipes = recipeManager.getRecipesForType(CompressedHammerRecipe.TYPE);
        for (CompressedHammerRecipe recipe : compressedHammerRecipes) {
            jeiCompressedHammerRecipes.add(new JeiCompressedHammerRecipe(recipe));
        }
        registry.addRecipes(jeiCompressedHammerRecipes, CompressedHammerRecipeCategory.UID);

        List<JeiHammerRecipe> jeiHammerRecipes = new ArrayList<>();
        List<HammerRecipe> hammerRecipes = recipeManager.getRecipesForType(HammerRecipe.TYPE);
        for (HammerRecipe recipe : hammerRecipes) {
            jeiHammerRecipes.add(new JeiHammerRecipe(recipe));
        }
        /* TODO for (Hammerable hammerable : ExNihilo.getInstance().getHammerables()) {
            jeiHammerRecipes.add(new JeiHammerRecipe(hammerable));
        }*/
        registry.addRecipes(jeiHammerRecipes, HammerRecipeCategory.UID);

        ArrayListMultimap<ResourceLocation, WoodenCrucibleMeltable> fluidOutputMap = ArrayListMultimap.create();
        for (WoodenCrucibleMeltable entry : ExRegistries.getWoodenCrucibleRegistry().getEntries()) {
            fluidOutputMap.put(entry.getFluid(), entry);
        }

        List<WoodenCrucibleRecipe> woodenCrucibleRecipes = new ArrayList<>();
        for (ResourceLocation fluidName : fluidOutputMap.keySet()) {
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
            if (fluid == null) {
                continue;
            }

            List<WoodenCrucibleMeltable> meltables = fluidOutputMap.get(fluidName);
            List<Pair<WoodenCrucibleMeltable, ItemStack>> inputs = new ArrayList<>();
            for (WoodenCrucibleMeltable meltable : meltables) {
                for (ItemStack matchingStack : meltable.getSource().getMatchingStacks()) {
                    inputs.add(Pair.of(meltable, matchingStack));
                }
            }

            inputs.sort(Comparator.comparingInt((Pair<WoodenCrucibleMeltable, ItemStack> pair) -> pair.getFirst().getAmount()).reversed());

            final int pageSize = 45;
            List<List<Pair<WoodenCrucibleMeltable, ItemStack>>> pages = Lists.partition(inputs, pageSize);
            for (List<Pair<WoodenCrucibleMeltable, ItemStack>> page : pages) {
                woodenCrucibleRecipes.add(new WoodenCrucibleRecipe(fluid, page));
            }
        }

        registry.addRecipes(woodenCrucibleRecipes, WoodenCrucibleRecipeCategory.UID);

        registry.addRecipes(Lists.newArrayList(new ChickenStickRecipe()), ChickenStickRecipeCategory.UID);
    }

    private void loadGeneratedHeavySieveRecipe(boolean waterlogged, GeneratedHeavySieveRecipe generatedRecipe, List<JeiHeavySieveRecipe> outRecipes) {
        BlockState waterLoggedState = ModBlocks.heavySieves[0].getDefaultState().with(HeavySieveBlock.WATERLOGGED, waterlogged);
        for (SieveMeshRegistryEntry mesh : SieveMeshRegistry.getEntries().values()) {
            int rolls = HeavySieveRegistry.getGeneratedRollCount(generatedRecipe);
            IItemProvider source = ForgeRegistries.ITEMS.getValue(generatedRecipe.getSource());
            LootTable lootTable = ExNihilo.getInstance().generateHeavySieveLootTable(waterLoggedState, source, rolls, mesh);
            if (!LootTableUtils.isLootTableEmpty(lootTable)) {
                HeavySieveRecipe recipe = new HeavySieveRecipe(generatedRecipe.getId(), generatedRecipe.getInput(), new LootTableProvider(lootTable), waterlogged, null, Sets.newHashSet(mesh.getMeshType()));
                outRecipes.add(new JeiHeavySieveRecipe(recipe));
            }
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        for (Block heavySieve : ModBlocks.heavySieves) {
            registry.addRecipeCatalyst(new ItemStack(heavySieve), HeavySieveRecipeCategory.UID);
        }
        for (Block woodenCrucible : ModBlocks.woodenCrucibles) {
            registry.addRecipeCatalyst(new ItemStack(woodenCrucible), WoodenCrucibleRecipeCategory.UID);
        }
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.autoCompressedHammer), CompressedHammerRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModItems.compressedHammerNetherite), CompressedHammerRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModItems.compressedHammerDiamond), CompressedHammerRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModItems.compressedHammerGold), CompressedHammerRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModItems.compressedHammerIron), CompressedHammerRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModItems.compressedHammerStone), CompressedHammerRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModItems.compressedHammerWood), CompressedHammerRecipeCategory.UID);

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.autoHammer), HammerRecipeCategory.UID);
        registry.addRecipeCatalyst(ExNihilo.getInstance().getNihiloItem(ExNihiloProvider.NihiloItems.HAMMER_NETHERITE), HammerRecipeCategory.UID);
        registry.addRecipeCatalyst(ExNihilo.getInstance().getNihiloItem(ExNihiloProvider.NihiloItems.HAMMER_DIAMOND), HammerRecipeCategory.UID);
        registry.addRecipeCatalyst(ExNihilo.getInstance().getNihiloItem(ExNihiloProvider.NihiloItems.HAMMER_GOLD), HammerRecipeCategory.UID);
        registry.addRecipeCatalyst(ExNihilo.getInstance().getNihiloItem(ExNihiloProvider.NihiloItems.HAMMER_IRON), HammerRecipeCategory.UID);
        registry.addRecipeCatalyst(ExNihilo.getInstance().getNihiloItem(ExNihiloProvider.NihiloItems.HAMMER_STONE), HammerRecipeCategory.UID);
        registry.addRecipeCatalyst(ExNihilo.getInstance().getNihiloItem(ExNihiloProvider.NihiloItems.HAMMER_WOODEN), HammerRecipeCategory.UID);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ExCompressum.MOD_ID, "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
                new HeavySieveRecipeCategory(registry.getJeiHelpers()),
                new HammerRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new CompressedHammerRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new WoodenCrucibleRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new ChickenStickRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

}
