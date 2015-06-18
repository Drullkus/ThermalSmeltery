package com.drullkus.thermalsmeltery.common.blocks;

import com.drullkus.thermalsmeltery.ThermalSmeltery;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

public class TSBlocks {

    public static Block TSMachine;
    public static String[] machineTypes = {"extruder", "stamper"};

    public static enum Types {
        EXTRUDER,
        STAMPER
    }

    public static void preInit() {}

    public static void init()
    {
        TSMachine = new BlockTSMachine( machineTypes, "tile.machine", ThermalSmeltery.itemTab);

        GameRegistry.registerBlock(TSMachine, ItemBlockTSMachine.class, "Machine");
    }

    public static void postInit() {}

}
