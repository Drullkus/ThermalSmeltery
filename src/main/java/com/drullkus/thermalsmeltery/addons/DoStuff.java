package com.drullkus.thermalsmeltery.addons;

import mantle.pulsar.pulse.Handler;
import mantle.utils.ItemMetaWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

public class DoStuff {

    public static int RFMultiplier = 24;

    @Handler
    public static void postInit()
    {
        // Someone better call Saul
        letsGetCooking();
    }

    private static void letsGetCooking() {
        Map<ItemMetaWrapper, FluidStack> smelteryMap = tconstruct.library.crafting.Smeltery.getSmeltingList();
        Map<ItemMetaWrapper, Integer> tempMap = tconstruct.library.crafting.Smeltery.getTemperatureList();

        for (Map.Entry<ItemMetaWrapper, FluidStack> entry : smelteryMap.entrySet())
        {
            ItemStack input = new ItemStack(entry.getKey().item, 1, entry.getKey().meta);
            int energy = tempMap.get(entry.getKey()) * 100; // Calculate temperature to energy here
            TE4Helper.addCrucibleRecipe(energy, input, entry.getValue());
        }
    }

}