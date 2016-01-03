package us.drullk.thermalsmeltery.common.plugins.eio.smeltery;

import us.drullk.thermalsmeltery.ThermalSmeltery;
import us.drullk.thermalsmeltery.common.core.handler.TSmeltConfig;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mantle.blocks.BlockUtils;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.FluidType;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.TinkerSmeltery;

//@GameRegistry.ObjectHolder(LibMisc.MOD_ID)
@Pulse(id = "TSmelt EIO Smeltery",
	description = "TCon Smeltery Integration for EnderIO",
	modsRequired = "TConstruct;EnderIO")
public class EnderIOSmeltery {

	public static Item buckets;

	public static Fluid moltenEnergeticFluid;

	public static Fluid moltenVibrantFluid;

	public static Fluid moltenConductiveIronFluid;

	public static Fluid moltenPulsatingIronFluid;

	public static Fluid moltenDarkSteelFluid;

	public static Block moltenEnergetic;

	public static Block moltenVibrant;

	public static Block moltenConductiveIron;

	public static Block moltenPulsatingIron;

	public static Block moltenDarkSteel;

	public static Fluid[] fluids = new Fluid[5];

	public static Block[] fluidBlocks = new Block[5];

	private static FluidStack moltenRedstoneDust;

	private static FluidStack moltenGlowstoneDust;

	private static FluidStack moltenEnder;

	private static FluidStack moltenIronIngot;

	private static FluidStack moltenGoldIngot;

	private static FluidStack moltenSteelIngot;

	private static ItemStack itemSiliconStack;

	@Handler
	public void preInit(FMLPreInitializationEvent event) {
		buckets = new FilledBucket(BlockUtils.getBlockFromItem(buckets));
		GameRegistry.registerItem(buckets, "buckets");

		moltenEnergeticFluid = FluidHelper.registerFluid("EnergeticAlloy");
		moltenEnergetic = moltenEnergeticFluid.getBlock();

		moltenVibrantFluid = FluidHelper.registerFluid("PhasedGold");
		moltenVibrant = moltenVibrantFluid.getBlock();

		moltenConductiveIronFluid = FluidHelper.registerFluid("ConductiveIron");
		moltenConductiveIron = moltenConductiveIronFluid.getBlock();

		moltenPulsatingIronFluid = FluidHelper.registerFluid("PhasedIron");
		moltenPulsatingIron = moltenPulsatingIronFluid.getBlock();

		moltenDarkSteelFluid = FluidHelper.registerFluid("DarkSteel");
		moltenDarkSteel = moltenDarkSteelFluid.getBlock();

		fluids = new Fluid[] { moltenEnergeticFluid, moltenVibrantFluid, moltenConductiveIronFluid, moltenPulsatingIronFluid, moltenDarkSteelFluid };
		fluidBlocks = new Block[] { moltenEnergetic, moltenVibrant, moltenConductiveIron, moltenPulsatingIron, moltenDarkSteel };

		FluidType.registerFluidType("EnergeticAlloy", GameRegistry.findBlock("EnderIO", "blockIngotStorage"), 1, 650, moltenEnergeticFluid, false);
		FluidType.registerFluidType("PhasedGold", GameRegistry.findBlock("EnderIO", "blockIngotStorage"), 2, 750, moltenVibrantFluid, false);
		FluidType.registerFluidType("ConductiveIron", GameRegistry.findBlock("EnderIO", "blockIngotStorage"), 4, 500, moltenConductiveIronFluid, false);
		FluidType.registerFluidType("PhasedIron", GameRegistry.findBlock("EnderIO", "blockIngotStorage"), 5, 500, moltenPulsatingIronFluid, false);
		FluidType.registerFluidType("DarkSteel", GameRegistry.findBlock("EnderIO", "blockIngotStorage"), 6, 850, moltenDarkSteelFluid, false);
	}

	@Handler
	public void init(FMLInitializationEvent event) {
		if (TConstruct.pulsar.isPulseLoaded("Tinkers' Smeltery")) {
			moltenRedstoneDust = new FluidStack(FluidRegistry.getFluid("redstone"), 100);
			moltenGlowstoneDust = new FluidStack(FluidRegistry.getFluid("glowstone"), 250);
			moltenEnder = new FluidStack(FluidRegistry.getFluid("ender"), 250);
			moltenIronIngot = new FluidStack(FluidRegistry.getFluid("iron.molten"), TConstruct.ingotLiquidValue);
			moltenGoldIngot = new FluidStack(FluidRegistry.getFluid("gold.molten"), TConstruct.ingotLiquidValue);
			moltenSteelIngot = new FluidStack(FluidRegistry.getFluid("steel.molten"), TConstruct.ingotLiquidValue);
			itemSiliconStack = new ItemStack(GameRegistry.findItem("EnderIO", "itemMaterial"), 1, 0);

			ItemStack ingotcast = new ItemStack(TinkerSmeltery.metalPattern, 1, 0);
			LiquidCasting tableCasting = TConstructRegistry.getTableCasting();
			LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();

			String[] orePrefix = new String[] { "block", "nugget", "ingot" };
			int[] oreAmounts = new int[] { TConstruct.blockLiquidValue, TConstruct.nuggetLiquidValue, TConstruct.ingotLiquidValue };
			String[] fluidNames = new String[] { "EnergeticAlloy", "PhasedGold", "ConductiveIron", "PhasedIron", "DarkSteel" };

			if (TSmeltConfig.EIOAddMetalCasting) {
				for (int c = 0; c < fluidNames.length; c++) {
					if (OreDictionary.doesOreNameExist("ingot" + fluidNames[c])) {
						tableCasting.addCastingRecipe(OreDictionary.getOres("ingot" + fluidNames[c]).get(0), new FluidStack(fluids[c], TConstruct.ingotLiquidValue), ingotcast, 50);

						ThermalSmeltery.logger.info("Added block" + fluidNames[c] + " to TCon Casting Table");
					}
					else {
						ThermalSmeltery.logger.info("Skipping registration of casting ingot" + fluidNames[c]);
					}

					if (OreDictionary.doesOreNameExist("block" + fluidNames[c])) {
						basinCasting.addCastingRecipe(OreDictionary.getOres("block" + fluidNames[c]).get(0), new FluidStack(fluids[c], TConstruct.blockLiquidValue), 150);

						ThermalSmeltery.logger.info("Added block" + fluidNames[c] + " to TCon Casting Basin");
					}
					else {
						ThermalSmeltery.logger.info("Skipping registration of casting block" + fluidNames[c]);
					}

					for (int i = 0; i < orePrefix.length; i++) {
						if (OreDictionary.doesOreNameExist(orePrefix[i] + fluidNames[c])) {
							//ThermalSmeltery.logger.info("Going to use " + fluids[c].getName());

							Smeltery.addDictionaryMelting(orePrefix[i] + fluidNames[c], tconstruct.library.crafting.FluidType.getFluidType(fluids[c]), 0, oreAmounts[i]);

							//ThermalSmeltery.logger.info("Added " + orePrefix[i] + fluidNames[c] + " to TCon melting maps to give " + oreAmounts[i]);
						}
					}

					// Making Buckets!
					tableCasting.addCastingRecipe(new ItemStack(GameRegistry.findItem("ThermalSmeltery", "buckets"), 1, c), // Buckets
						new FluidStack(fluids[c], 1000), new ItemStack(Items.bucket, 1, 0), true, 50);
				}
			}

			if (TSmeltConfig.EIOElectricalSteelCasting && Loader.isModLoaded("EnderIO")) {
				// Making Electrical Steel ingots
				tableCasting.addCastingRecipe(new ItemStack(GameRegistry.findItem("EnderIO", "itemAlloy"), 1, 0), // Electrical Steel
					moltenSteelIngot, itemSiliconStack, true, 60);
			}

			if (TSmeltConfig.EIOEnergeticAlloyRecipe && Loader.isModLoaded("EnderIO")) {
				// Energetic Alloying
				Smeltery.addAlloyMixing(new FluidStack(moltenEnergeticFluid, TConstruct.ingotLiquidValue), moltenGoldIngot, moltenRedstoneDust, moltenGlowstoneDust);
			}

			if (TSmeltConfig.EIOVibrantAlloyRecipe && Loader.isModLoaded("EnderIO")) {
				// Vibrant Alloying
				Smeltery.addAlloyMixing(new FluidStack(moltenVibrantFluid, TConstruct.ingotLiquidValue), new FluidStack(moltenEnergeticFluid, TConstruct.ingotLiquidValue), moltenEnder);
			}

			if (TSmeltConfig.EIORedstoneAlloyCasting && Loader.isModLoaded("EnderIO")) {
				// Making Redstone Alloy ingots
				tableCasting.addCastingRecipe(new ItemStack(GameRegistry.findItem("EnderIO", "itemAlloy"), 1, 3) /*Redstone Alloy*/, moltenRedstoneDust, itemSiliconStack, true, 50);
			}

			if (TSmeltConfig.EIOConductiveIronRecipe && Loader.isModLoaded("EnderIO")) {
				// Conductive Iron Alloying
				Smeltery.addAlloyMixing(new FluidStack(moltenConductiveIronFluid, TConstruct.ingotLiquidValue), moltenIronIngot, moltenRedstoneDust);
			}

			if (TSmeltConfig.EIOPulsatingIronRecipe && Loader.isModLoaded("EnderIO")) {
				// Pulsating Iron Alloying
				Smeltery.addAlloyMixing(new FluidStack(moltenPulsatingIronFluid, TConstruct.ingotLiquidValue), moltenIronIngot, moltenEnder);
			}

			if (TSmeltConfig.EIODarkSteelRecipe && Loader.isModLoaded("EnderIO")) {
				// Dark Steel Alloying
				Smeltery.addAlloyMixing(new FluidStack(moltenDarkSteelFluid, TConstruct.ingotLiquidValue), moltenSteelIngot,
					new FluidStack(FluidRegistry.getFluid("obsidian.molten"), TConstruct.ingotLiquidValue * 2));
				//Obby is 288mb per block
			}

			if (TSmeltConfig.EIOSoulariumCasting && Loader.isModLoaded("EnderIO")) {
				// Making Soularium ingots
				tableCasting.addCastingRecipe(new ItemStack(GameRegistry.findItem("EnderIO", "itemAlloy"), 1, 7), moltenGoldIngot, new ItemStack(Blocks.soul_sand, 1, 0), true, 75);
			}
		}
		else {
			ThermalSmeltery.logger.warn("Tinker's Smeltery is disabled, Adding EIO alloy mixing and casting disabled.");
		}
	}
}
