package com.drullkus.thermalsmeltery.common.blocks;

import cofh.api.core.IInitializer;
import com.drullkus.thermalsmeltery.common.items.ItemBlockSmeltery;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.Iterator;

public class ModBlocks
{
    public static ArrayList<IInitializer> blockList = new ArrayList<IInitializer>();
    public static Block blockMachine;

    private ModBlocks() {
    }

    public static void preInit() {
    }

    public static void initialize() {
        blockMachine = addBlock(new BlockSmeltery());
        GameRegistry.registerBlock(blockMachine, ItemBlockSmeltery.class, "SmelteryMachine");

        Iterator var0 = blockList.iterator();

        while(var0.hasNext()) {
            IInitializer var1 = (IInitializer)var0.next();
            var1.initialize();
        }

    }

    public static void postInit() {
        Iterator var0 = blockList.iterator();

        while(var0.hasNext()) {
            IInitializer var1 = (IInitializer)var0.next();
            var1.postInit();
        }

        blockList.clear();
    }

    public static Block addBlock(Block var0) {
        blockList.add((IInitializer)var0);
        return var0;
    }
}
