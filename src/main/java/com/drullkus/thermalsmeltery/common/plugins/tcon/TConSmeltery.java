package com.drullkus.thermalsmeltery.common.plugins.tcon;

import com.drullkus.thermalsmeltery.common.core.handler.Config;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.TinkerSmeltery;

public class TConSmeltery {
    public static void addSmelteryAlloy() {
        if (Config.TConSteelRecipe)
        {
            //Steel Alloying
            Smeltery.addAlloyMixing(new FluidStack(TinkerSmeltery.moltenSteelFluid, TConstruct.ingotLiquidValue), new FluidStack(FluidRegistry.getFluid("coal"), 200), new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.ingotLiquidValue));
        }
    }

    public static void addSmelteryCasting() {

        ItemStack ingotcast = new ItemStack(TinkerSmeltery.metalPattern, 1, 0);
        LiquidCasting tableCasting = TConstructRegistry.getTableCasting();
        LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();

        if (Config.TConYelloriumCasting && FluidRegistry.getFluid("yellorium") != null)
        {
            //Yellorium Casting
            //Ingot
            tableCasting.addCastingRecipe(new ItemStack(GameRegistry.findItem("BigReactors", "BRIngot"), 1, 0), new FluidStack(FluidRegistry.getFluid("yellorium"), 1000), ingotcast, 50);
            //Basin
            basinCasting.addCastingRecipe(new ItemStack(GameRegistry.findBlock("BigReactors", "BRMetalBlock"), 1, 0), new FluidStack(FluidRegistry.getFluid("yellorium"), 9000), 450);
        }
    }
}
