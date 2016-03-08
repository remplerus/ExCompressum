package net.blay09.mods.excompressum;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.blay09.mods.excompressum.block.*;
import net.blay09.mods.excompressum.item.*;
import net.blay09.mods.excompressum.tile.*;
import net.minecraftforge.common.config.Configuration;

public class ModBlocks {
    public static BlockCompressed compressedBlock;
    public static BlockHeavySieve heavySieve;
    public static BlockWoodenCrucible woodenCrucible;
    public static BlockBait bait;
    public static BlockAutoCompressedHammer autoCompressedHammer;
    public static BlockAutoHeavySieve autoHeavySieve;
    public static BlockAutoCompressor autoCompressor;

    public static void init() {
        compressedBlock = new BlockCompressed();
        GameRegistry.registerBlock(compressedBlock, ItemBlockCompressed.class, "compressed_dust"); // god damn it Blay. can't rename because already released
        heavySieve = new BlockHeavySieve();
        GameRegistry.registerBlock(heavySieve, ItemBlockHeavySieve.class, "heavySieve");
        woodenCrucible = new BlockWoodenCrucible();
        GameRegistry.registerBlock(woodenCrucible, ItemBlockWoodenCrucible.class, "woodenCrucible");
        bait = new BlockBait();
        GameRegistry.registerBlock(bait, ItemBlockBait.class, "bait");

        autoCompressedHammer = new BlockAutoCompressedHammer();
        autoHeavySieve = new BlockAutoHeavySieve();
        autoCompressor = new BlockAutoCompressor();
        if(Loader.isModLoaded("CoFHCore")) {
            GameRegistry.registerBlock(autoCompressedHammer, "autoCompressedHammer");
            GameRegistry.registerBlock(autoHeavySieve, ItemBlockAutoHeavySieve.class, "autoHeavySieve");
            GameRegistry.registerBlock(autoCompressor, "autoCompressor");
        }

        GameRegistry.registerTileEntity(TileEntityWoodenCrucible.class, "woodenCrucible");
        GameRegistry.registerTileEntity(TileEntityHeavySieve.class, ExCompressum.MOD_ID + ":heavy_sieve");
        GameRegistry.registerTileEntity(TileEntityBait.class, "bait");

        if(Loader.isModLoaded("CoFHCore")) {
            GameRegistry.registerTileEntity(TileEntityAutoCompressedHammer.class, "autoCompressedHammer");
            GameRegistry.registerTileEntity(TileEntityAutoHeavySieve.class, "autoHeavySieve");
            GameRegistry.registerTileEntity(TileEntityAutoCompressor.class, "autoCompressor");
        }
    }

    public static void registerRecipes(Configuration config) {
        BlockHeavySieve.registerRecipes(config);
        BlockWoodenCrucible.registerRecipes(config);
        BlockCompressed.registerRecipes(config);
        BlockBait.registerRecipes(config);
        BlockAutoCompressor.registerRecipes(config);
        BlockAutoCompressedHammer.registerRecipes(config);
        BlockAutoHeavySieve.registerRecipes(config);
    }
}
