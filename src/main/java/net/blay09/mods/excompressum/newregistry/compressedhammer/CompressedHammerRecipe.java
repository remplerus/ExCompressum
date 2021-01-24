package net.blay09.mods.excompressum.newregistry.compressedhammer;

import net.blay09.mods.excompressum.ExCompressum;
import net.blay09.mods.excompressum.api.LootTableProvider;
import net.blay09.mods.excompressum.api.sievemesh.MeshType;
import net.blay09.mods.excompressum.newregistry.ExCompressumRecipe;
import net.blay09.mods.excompressum.newregistry.ModRecipeTypes;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Set;

public class CompressedHammerRecipe extends ExCompressumRecipe {
    public static final IRecipeType<CompressedHammerRecipe> TYPE = IRecipeType.register(ExCompressum.MOD_ID + ":compressed_hammer");

    private final Ingredient input;
    private final LootTableProvider lootTable;

    public CompressedHammerRecipe(ResourceLocation id, Ingredient input, LootTableProvider lootTable) {
        super(id, TYPE);
        this.input = input;
        this.lootTable = lootTable;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.compressedHammerRecipe;
    }

    public Ingredient getInput() {
        return input;
    }

    public LootTableProvider getLootTable() {
        return lootTable;
    }

}
