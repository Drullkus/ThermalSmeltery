package com.drullkus.thermalsmeltery.common.lib;

import com.drullkus.thermalsmeltery.ThermalSmeltery;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.smeltery.TinkerSmeltery;

public class TConFluidRegistrationHelper {

    private static ItemStack ingotcast = new ItemStack(TinkerSmeltery.metalPattern, 1, 0);
    private static LiquidCasting tableCasting = TConstructRegistry.getTableCasting();
    private static LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();

    public static void fluidHandler(Fluid fluid, String oreDictName)
    {
        if (checkOreEntry("ingot" + oreDictName))
        {
            tableCasting.addCastingRecipe(
                    OreDictionary.getOres("ingot" + oreDictName).get(0),
                    new FluidStack(FluidRegistry.getFluid(oreDictName), TConstruct.ingotLiquidValue),
                    ingotcast, 50);
        }

        if (checkOreEntry("block" + oreDictName))
        {
            basinCasting.addCastingRecipe(
                    OreDictionary.getOres("block" + oreDictName).get(0),
                    new FluidStack(FluidRegistry.getFluid(oreDictName), TConstruct.blockLiquidValue), 150);
        }
        else
        {
            ThermalSmeltery.logger.info("Skipping registration of casting block" + oreDictName);
        }
    }

    public static boolean checkOreEntry(String name)
    {
        return OreDictionary.doesOreNameExist(name);
    }

}
