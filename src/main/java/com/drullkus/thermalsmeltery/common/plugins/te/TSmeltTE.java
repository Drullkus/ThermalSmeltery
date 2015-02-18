package com.drullkus.thermalsmeltery.common.plugins.te;

import java.util.Map;

import com.drullkus.thermalsmeltery.common.items.ModItems;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import mantle.utils.ItemMetaWrapper;

import com.drullkus.thermalsmeltery.common.core.handler.TSmeltConfig;
import com.drullkus.thermalsmeltery.common.lib.LibMisc;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibMisc.MOD_ID)
@Pulse(id = "TSmelt TE", description = "Thermal Expansion Integration", modsRequired = "ThermalExpansion")
public class TSmeltTE
{

    @Handler
    public void preInit (FMLPostInitializationEvent event) {
        Map<ItemMetaWrapper, FluidStack> smelteryMap = tconstruct.library.crafting.Smeltery.getSmeltingList();
        Map<ItemMetaWrapper, Integer> tempMap = tconstruct.library.crafting.Smeltery.getTemperatureList();

        for (Map.Entry<ItemMetaWrapper, FluidStack> entry : smelteryMap.entrySet()) {
            ItemStack input = new ItemStack(entry.getKey().item, 1, entry.getKey().meta);
            int energy = tempMap.get(entry.getKey()) * 10 * TSmeltConfig.multiplier;
            TE4Helper.addCrucibleRecipe(energy, input, entry.getValue());
        }

        TE4Helper.addPulverizerRecipe(20000, new ItemStack(GameRegistry.findBlock("ThermalExpansion", "Device"), 1, 5), new ItemStack(ModItems.Tool_Mod_Void, 1, 0), null, 0);
    }
}
