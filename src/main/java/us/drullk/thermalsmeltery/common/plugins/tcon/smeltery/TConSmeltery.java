package us.drullk.thermalsmeltery.common.plugins.tcon.smeltery;

import us.drullk.thermalsmeltery.ThermalSmeltery;
import us.drullk.thermalsmeltery.common.core.handler.TSmeltConfig;
import us.drullk.thermalsmeltery.common.lib.LibMisc;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.CastingRecipe;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;

@ObjectHolder(LibMisc.MOD_ID)
@Pulse(id = "TSmelt TCon Smeltery",
	description = "Tinkers Construct's Smeltery Integration",
	modsRequired = "TConstruct")
public class TConSmeltery {
	public static String CASTING_BASIN = "castingTable";
	public static String SMELTERY_BRICK = "smelteryBrick";
	public static String TOOL_FORGE = "toolForge";

	@Handler
	public void init(FMLInitializationEvent event) {
		if (TConstruct.pulsar.isPulseLoaded("Tinkers' Smeltery")) {
			ItemStack ingotcast = new ItemStack(TinkerSmeltery.metalPattern, 1, 0);
			LiquidCasting tableCasting = TConstructRegistry.getTableCasting();
			LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
			if (TSmeltConfig.tConYelloriumCasting && FluidRegistry.getFluid("yellorium") != null) {
				//Yellorium Casting
				//Ingot
				tableCasting.addCastingRecipe(new ItemStack(GameRegistry.findItem("BigReactors", "BRIngot"), 1, 0), new FluidStack(FluidRegistry.getFluid("yellorium"), 1000), ingotcast, 50);
				//Basin
				basinCasting.addCastingRecipe(new ItemStack(GameRegistry.findBlock("BigReactors", "BRMetalBlock"), 1, 0), new FluidStack(FluidRegistry.getFluid("yellorium"), 9000), 450);
			}

			if (TSmeltConfig.tConSteelRecipe && FluidRegistry.getFluid("coal") != null) {
				//Steel Alloying
				Smeltery.addAlloyMixing(new FluidStack(TinkerSmeltery.moltenSteelFluid, TConstruct.ingotLiquidValue), new FluidStack(FluidRegistry.getFluid("coal"), 200),
					new FluidStack(TinkerSmeltery.moltenIronFluid, TConstruct.ingotLiquidValue));
			}

			OreDictionary.registerOre(CASTING_BASIN, new ItemStack(TinkerSmeltery.searedBlock, 1, 2));
			OreDictionary.registerOre(CASTING_BASIN, new ItemStack(TinkerSmeltery.searedBlockNether, 1, 2));
			OreDictionary.registerOre(SMELTERY_BRICK, new ItemStack(TinkerSmeltery.smeltery, 1, 2));
			OreDictionary.registerOre(SMELTERY_BRICK, new ItemStack(TinkerSmeltery.smelteryNether, 1, 2));
			for (int i = 0; i < 14; i++) {
				OreDictionary.registerOre(TOOL_FORGE, new ItemStack(TinkerTools.toolForge, 1, i));
			}
			OreDictionary.registerOre(TOOL_FORGE, new ItemStack(TinkerTools.craftingSlabWood, 1, 5));
		}
		else {
			ThermalSmeltery.logger.warn("Tinker's Smeltery is disabled, Adding alloy mixing and casting disabled.");
		}
	}

	@Handler
	public void postInit(FMLPostInitializationEvent event) {
		LiquidCasting tableCasting = TConstructRegistry.getTableCasting();

		for (CastingRecipe recipe : tableCasting.getCastingRecipes()) {
			MachineRecipeRegistry.registerStampingRecipe(tableCasting, recipe);
			MachineRecipeRegistry.registerIngotRecipe(recipe);
		}

		LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
		for (CastingRecipe recipe : basinCasting.getCastingRecipes()) {
			MachineRecipeRegistry.registerBlockRecipe(recipe);
		}
	}
}
