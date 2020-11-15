package net.blay09.mods.excompressum.compat.exnihilosequentia;

import com.google.common.collect.Maps;
import com.novamachina.exnihilosequentia.common.api.ExNihiloRegistries;
import com.novamachina.exnihilosequentia.common.block.InfestedLeavesBlock;
import com.novamachina.exnihilosequentia.common.item.mesh.EnumMesh;
import com.novamachina.exnihilosequentia.common.item.resources.EnumResource;
import com.novamachina.exnihilosequentia.common.registries.crook.CrookDropEntry;
import com.novamachina.exnihilosequentia.common.registries.sieve.SieveDropEntry;
import com.novamachina.exnihilosequentia.common.utility.Config;
import net.blay09.mods.excompressum.ExCompressum;
import net.blay09.mods.excompressum.api.ExNihiloProvider;
import net.blay09.mods.excompressum.api.heavysieve.HeavySieveReward;
import net.blay09.mods.excompressum.api.sievemesh.SieveMeshRegistryEntry;
import net.blay09.mods.excompressum.compat.Compat;
import net.blay09.mods.excompressum.config.ExCompressumConfig;
import net.blay09.mods.excompressum.registry.ExNihilo;
import net.blay09.mods.excompressum.registry.sievemesh.SieveMeshRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.RandomChance;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class ExNihiloSequentiaAddon implements ExNihiloProvider {

    private final EnumMap<NihiloItems, ItemStack> itemMap = Maps.newEnumMap(NihiloItems.class);

    public ExNihiloSequentiaAddon() {
        MinecraftForge.EVENT_BUS.register(this);

        ExNihilo.instance = this;

        itemMap.put(NihiloItems.HAMMER_WOODEN, findItem("hammer_wood"));
        itemMap.put(NihiloItems.HAMMER_STONE, findItem("hammer_stone"));
        itemMap.put(NihiloItems.HAMMER_IRON, findItem("hammer_iron"));
        itemMap.put(NihiloItems.HAMMER_GOLD, findItem("hammer_gold"));
        itemMap.put(NihiloItems.HAMMER_DIAMOND, findItem("hammer_diamond"));
        itemMap.put(NihiloItems.CROOK_WOODEN, findItem("crook_wood"));
        itemMap.put(NihiloItems.SILK_MESH, findItem("mesh_string"));
        itemMap.put(NihiloItems.IRON_MESH, findItem("mesh_iron"));

        itemMap.put(NihiloItems.DUST, findBlock("dust"));
        itemMap.put(NihiloItems.SIEVE, findBlock("sieve"));
        itemMap.put(NihiloItems.INFESTED_LEAVES, findBlock("infested_leaves"));
        itemMap.put(NihiloItems.NETHER_GRAVEL, findBlock("crushed_netherrack"));
        itemMap.put(NihiloItems.ENDER_GRAVEL, findBlock("crushed_end_stone"));


        ItemStack stringMeshItem = getNihiloItem(NihiloItems.SILK_MESH);
        if (!stringMeshItem.isEmpty()) {
            SieveMeshRegistryEntry stringMesh = new SieveMeshRegistryEntry(stringMeshItem, EnumMesh.STRING);
            stringMesh.setMeshLevel(1);
            stringMesh.setSpriteLocation(new ResourceLocation(ExCompressum.MOD_ID, "blocks/string_mesh"));
            SieveMeshRegistry.add(stringMesh);
        }

        ItemStack flintMeshItem = findItem("mesh_flint");
        if (!flintMeshItem.isEmpty()) {
            SieveMeshRegistryEntry flintMesh = new SieveMeshRegistryEntry(flintMeshItem, EnumMesh.FLINT);
            flintMesh.setMeshLevel(2);
            flintMesh.setSpriteLocation(new ResourceLocation(ExCompressum.MOD_ID, "blocks/flint_mesh"));
            SieveMeshRegistry.add(flintMesh);
        }

        ItemStack ironMeshItem = getNihiloItem(NihiloItems.IRON_MESH);
        if (!ironMeshItem.isEmpty()) {
            SieveMeshRegistryEntry ironMesh = new SieveMeshRegistryEntry(ironMeshItem, EnumMesh.IRON);
            ironMesh.setMeshLevel(3);
            ironMesh.setHeavy(true);
            ironMesh.setSpriteLocation(new ResourceLocation(ExCompressum.MOD_ID, "blocks/iron_mesh"));
            SieveMeshRegistry.add(ironMesh);
        }

        ItemStack diamondMeshItem = findItem("mesh_diamond");
        if (!diamondMeshItem.isEmpty()) {
            SieveMeshRegistryEntry diamondMesh = new SieveMeshRegistryEntry(diamondMeshItem, EnumMesh.DIAMOND);
            diamondMesh.setMeshLevel(4);
            diamondMesh.setHeavy(true);
            diamondMesh.setSpriteLocation(new ResourceLocation(ExCompressum.MOD_ID, "blocks/diamond_mesh"));
            SieveMeshRegistry.add(diamondMesh);
        }
    }

    private ItemStack findItem(String name) {
        ResourceLocation location = new ResourceLocation(Compat.EXNIHILO_SEQUENTIA, name);
        if (ForgeRegistries.ITEMS.containsKey(location)) {
            return new ItemStack(ForgeRegistries.ITEMS.getValue(location), 1);
        }

        return ItemStack.EMPTY;
    }

    private ItemStack findBlock(String name) {
        ResourceLocation location = new ResourceLocation(Compat.EXNIHILO_SEQUENTIA, name);
        if (ForgeRegistries.BLOCKS.containsKey(location)) {
            return new ItemStack(ForgeRegistries.BLOCKS.getValue(location), 1);
        }

        return ItemStack.EMPTY;
    }


    @Override
    public ItemStack getNihiloItem(NihiloItems type) {
        ItemStack itemStack = itemMap.get(type);
        return itemStack != null ? itemStack : ItemStack.EMPTY;
    }

    @Override
    public boolean isHammerable(BlockState state) {
        return ExNihiloRegistries.HAMMER_REGISTRY.isHammerable(state.getBlock().getRegistryName());
    }

    @Override
    public List<ItemStack> rollHammerRewards(BlockState state, int miningLevel, float luck, Random rand) {
        return Collections.singletonList(new ItemStack(ExNihiloRegistries.HAMMER_REGISTRY.getResult(state.getBlock().getRegistryName())));
    }

    @Override
    public boolean isSiftable(BlockState state) {
        return ExNihiloRegistries.SIEVE_REGISTRY.isBlockSiftable(state.getBlock(), EnumMesh.NONE, false); // TODO waterlogged support
    }

    @Override
    public boolean isSiftableWithMesh(BlockState state, SieveMeshRegistryEntry sieveMesh) {
        EnumMesh mesh = sieveMesh != null ? (EnumMesh) sieveMesh.getBackingMesh() : EnumMesh.NONE;
        return ExNihiloRegistries.SIEVE_REGISTRY.isBlockSiftable(state.getBlock(), mesh, false); // TODO waterlogged support
    }

    @Override
    public Collection<ItemStack> rollSieveRewards(BlockState state, SieveMeshRegistryEntry sieveMesh, float luck, Random rand) {
        List<SieveDropEntry> rewards = ExNihiloRegistries.SIEVE_REGISTRY.getDrops(state.getBlock(), ((EnumMesh) sieveMesh.getBackingMesh()), false); // TODO waterlogged support
        if (rewards != null) {
            List<ItemStack> list = new ArrayList<>();
            for (SieveDropEntry reward : rewards) {
                if (reward.getResult() == null) {
                    ExCompressum.logger.error("Tried to roll sieve rewards from a null reward entry: {} (base chance: {}, mesh level: {})", state.getBlock().getRegistryName(), reward.getRarity(), sieveMesh.getMeshLevel());
                    continue;
                }

                int tries = rand.nextInt((int) luck + 1) + 1;
                for (int i = 0; i < tries; i++) {
                    if (rand.nextDouble() < (double) reward.getRarity()) {
                        ItemStack itemStack = createItemStack(reward.getResult());
                        if (!itemStack.isEmpty()) {
                            list.add(itemStack);
                        }
                    }
                }
            }
            return list;
        }

        return Collections.emptyList();
    }

    private ItemStack createItemStack(ResourceLocation registryName) {
        return new ItemStack(ForgeRegistries.ITEMS.getValue(registryName));
    }

    @Override
    public List<ItemStack> rollCrookRewards(ServerWorld world, BlockPos pos, BlockState state, @Nullable Entity entity, ItemStack tool, Random rand) {
        final float luck = getLuckFromTool(tool);
        if (state.getBlock() instanceof InfestedLeavesBlock) {
            List<ItemStack> list = new ArrayList<>();
            list.add(new ItemStack(Items.STRING, rand.nextInt(Config.MAX_BONUS_STRING_COUNT.get()) + Config.MIN_STRING_COUNT.get()));
            if (rand.nextDouble() <= 0.8) {
                list.add(new ItemStack(EnumResource.SILKWORM.getRegistryObject().get()));
            }
            return list;
        } else if (!BlockTags.LEAVES.contains(state.getBlock())) {
            return Collections.emptyList();
        }

        List<CrookDropEntry> rewards = ExNihiloRegistries.CROOK_REGISTRY.getDrops();
        if (rewards != null) {
            List<ItemStack> list = new ArrayList<>();

            for (int i = 0; i < Config.VANILLA_SIMULATE_DROP_COUNT.get(); i++) {
                List<ItemStack> items = Block.getDrops(state, world, pos, null);
                list.addAll(items);
            }

            for (CrookDropEntry reward : rewards) {
                float fortuneChanceBonus = 0.1f;
                if (rand.nextFloat() <= reward.getRarity() + fortuneChanceBonus * luck) {
                    ItemStack itemStack = createItemStack(reward.getItem());
                    if (!itemStack.isEmpty()) {
                        list.add(itemStack);
                    }
                }
            }
            return list;
        }
        return Collections.emptyList();
    }

    private float getLuckFromTool(ItemStack tool) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool);
    }

    @Override
    public LootTable generateHeavySieveLootTable(ResourceLocation source, int times) {
        LootTable.Builder tableBuilder = LootTable.builder();
        for (EnumMesh mesh : EnumMesh.values()) {
            LootPool.Builder poolBuilder = LootPool.builder();
            poolBuilder.rolls(ConstantRange.of(times));
            if (!ExCompressumConfig.COMMON.flattenSieveRecipes.get()) {
                // TODO sieve mesh poolBuilder.acceptCondition();
            }

            List<SieveDropEntry> drops = ExNihiloRegistries.SIEVE_REGISTRY.getDrops(source, mesh, false);// TODO support waterlogged
            for (SieveDropEntry drop : drops) {
                Item item = ForgeRegistries.ITEMS.getValue(drop.getResult());
                if (item != null) {
                    StandaloneLootEntry.Builder<?> entryBuilder = ItemLootEntry.builder(item);
                    entryBuilder.acceptCondition(RandomChance.builder(drop.getRarity()));
                    poolBuilder.addEntry(entryBuilder);
                }
            }
            tableBuilder.addLootPool(poolBuilder);
        }
        return tableBuilder.build();
    }

    @Override
    public boolean doMeshesHaveDurability() {
        return false;
    }

    @Override
    public boolean doMeshesSplitLootTables() {
        return true;
    }

    @Override
    public int getMeshFortune(ItemStack meshStack) {
        return 0;
    }

    @Override
    public int getMeshEfficiency(ItemStack meshStack) {
        return 0;
    }

    @Override
    public boolean isCompressableOre(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean isHammerableOre(ItemStack itemStack) {
        return false;
    }

    //	@SubscribeEvent Wood Chippings won't work in Adscensio until it ports the RegistryReloadedEvent to 1.11.2
//	public void onRegistryReload(RegistryReloadedEvent event) {
//		if(ExCompressumConfig.enableWoodChippings) {
//			HammerRegistry.register(Blocks.LOG.getDefaultState(), new ItemStack(ModItems.woodChipping), 0, 1f, 0f, true);
//			HammerRegistry.register(Blocks.LOG.getDefaultState(), new ItemStack(ModItems.woodChipping), 0, 0.75f, 0f, true);
//			HammerRegistry.register(Blocks.LOG.getDefaultState(), new ItemStack(ModItems.woodChipping), 0, 0.5f, 0f, true);
//			HammerRegistry.register(Blocks.LOG.getDefaultState(), new ItemStack(ModItems.woodChipping), 0, 0.25f, 0f, true);
//
//			HammerRegistry.register(Blocks.LOG2.getDefaultState(), new ItemStack(ModItems.woodChipping), 0, 1f, 0f, true);
//			HammerRegistry.register(Blocks.LOG2.getDefaultState(), new ItemStack(ModItems.woodChipping), 0, 0.75f, 0f, true);
//			HammerRegistry.register(Blocks.LOG2.getDefaultState(), new ItemStack(ModItems.woodChipping), 0, 0.5f, 0f, true);
//			HammerRegistry.register(Blocks.LOG2.getDefaultState(), new ItemStack(ModItems.woodChipping), 0, 0.25f, 0f, true);
//
//			List<ItemStack> oreDictStacks = OreDictionary.getOres("dustWood", false);
//			for (ItemStack itemStack : oreDictStacks) {
//				CompostRegistry.register(itemStack.getItem(), itemStack.getItemDamage(), 0.125f, Blocks.DIRT.getDefaultState(), new Color(0xFFC77826));
//			}
//		}
//	}
}
