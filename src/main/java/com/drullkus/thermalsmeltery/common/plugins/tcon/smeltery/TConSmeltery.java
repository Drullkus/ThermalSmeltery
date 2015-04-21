package com.drullkus.thermalsmeltery.common.plugins.tcon.smeltery;

import cpw.mods.fml.common.Loader;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.TinkerSmeltery;

import com.drullkus.thermalsmeltery.ThermalSmeltery;
import com.drullkus.thermalsmeltery.common.core.handler.TSmeltConfig;
import com.drullkus.thermalsmeltery.common.lib.LibMisc;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibMisc.MOD_ID)
@Pulse(id = "TSmelt TCon Smeltery", description = "Tinkers Construct's Smeltery Integration", modsRequired = "TConstruct")
public class TConSmeltery
{
	@Handler
	public void init(FMLInitializationEvent event)
	{
		if (TConstruct.pulsar.isPulseLoaded("Tinkers' Smeltery"))
		{
			ItemStack ingotcast = new ItemStack(TinkerSmeltery.metalPattern, 1, 0);
			LiquidCasting tableCasting = TConstructRegistry.getTableCasting();
			LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();

			if (TSmeltConfig.TConYelloriumCasting && FluidRegistry.getFluid("yellorium") != null)
			{
				//Yellorium Casting
				//Ingot
				tableCasting.addCastingRecipe(
                        new ItemStack(GameRegistry.findItem("BigReactors", "BRIngot"), 1, 0),
                        new FluidStack(FluidRegistry.getFluid("yellorium"), 1000), ingotcast, 50);
				//Basin
				basinCasting.addCastingRecipe(
                        new ItemStack(GameRegistry.findBlock("BigReactors", "BRMetalBlock"), 1, 0),
                        new FluidStack(FluidRegistry.getFluid("yellorium"), 9000), 450);
			}

			if (TSmeltConfig.TConSteelRecipe&& FluidRegistry.getFluid("coal") != null)
			{
				//Steel Alloying
				Smeltery.addAlloyMixing(
                        new FluidStack(FluidRegistry.getFluid("steel.molten"), TConstruct.ingotLiquidValue),
                        new FluidStack(FluidRegistry.getFluid("coal"), 200),
                        new FluidStack(FluidRegistry.getFluid("iron.molten"), TConstruct.ingotLiquidValue));
			}
		}
		else
		{
			ThermalSmeltery.logger.warn("Tinker's Smeltery is disabled, Adding alloy mixing and casting disabled.");
		}
	}
}
