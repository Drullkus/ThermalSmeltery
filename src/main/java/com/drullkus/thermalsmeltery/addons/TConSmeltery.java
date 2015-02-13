package com.drullkus.thermalsmeltery.addons;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.TinkerSmeltery;

public class TConSmeltery {
    public static void addSmelteryAlloy() {
        Smeltery.addAlloyMixing(new FluidStack(TinkerSmeltery.moltenSteelFluid, TConstruct.ingotLiquidValue), new FluidStack(FluidRegistry.getFluid("coal"), 200), new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.ingotLiquidValue)); //Obsidian
    }

}
