package com.drullkus.thermalsmeltery.addons;

import com.drullkus.thermalsmeltery.Config;
import com.drullkus.thermalsmeltery.addons.TE4Helper;
import mantle.utils.ItemMetaWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import java.util.Map;


public class MagmaCrucibleAdaptation {

    public static void letsGetCooking() {
        Map<ItemMetaWrapper, FluidStack> smelteryMap = tconstruct.library.crafting.Smeltery.getSmeltingList();
        Map<ItemMetaWrapper, Integer> tempMap = tconstruct.library.crafting.Smeltery.getTemperatureList();

        for (Map.Entry<ItemMetaWrapper, FluidStack> entry : smelteryMap.entrySet())
        {
            ItemStack input = new ItemStack(entry.getKey().item, 1, entry.getKey().meta);
            int energy = tempMap.get(entry.getKey()) * 10 * Config.multiplier; // Calculate temperature to energy here
            TE4Helper.addCrucibleRecipe(energy, input, entry.getValue());
        }

        //
    }
}