package com.drullkus.thermalsmeltery.common.plugins.eio.smeltery;

import com.drullkus.thermalsmeltery.ThermalSmeltery;
import com.drullkus.thermalsmeltery.common.core.handler.TSmeltConfig;
import com.drullkus.thermalsmeltery.common.lib.FluidType;
import com.drullkus.thermalsmeltery.common.lib.LibMisc;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import mantle.blocks.BlockUtils;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.TinkerSmeltery;

@GameRegistry.ObjectHolder(LibMisc.MOD_ID)
@Pulse(id = "TSmelt EIO Smeltery", description = "TCon Smeltery Integration for EnderIO", modsRequired = "TConstruct;EnderIO")
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
    public void preInit(FMLPreInitializationEvent event)
    {
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

        fluids = new Fluid[] { moltenEnergeticFluid, moltenVibrantFluid, moltenConductiveIronFluid, moltenPulsatingIronFluid, moltenDarkSteelFluid};
        fluidBlocks = new Block[] { moltenEnergetic, moltenVibrant, moltenConductiveIron, moltenPulsatingIron, moltenDarkSteel};

        FluidType.registerFluidType("EnergeticAlloy", GameRegistry.findBlock("EnderIO", "blockIngotStorage"), 1, 650, moltenEnergeticFluid, false);
        FluidType.registerFluidType("PhasedGold", GameRegistry.findBlock("EnderIO", "blockIngotStorage"), 2, 750, moltenVibrantFluid, false);
        FluidType.registerFluidType("ConductiveIron", GameRegistry.findBlock("EnderIO", "blockIngotStorage"), 4, 500, moltenConductiveIronFluid, false);
        FluidType.registerFluidType("PhasedIron", GameRegistry.findBlock("EnderIO", "blockIngotStorage"), 5, 500, moltenPulsatingIronFluid, false);
        FluidType.registerFluidType("DarkSteel", GameRegistry.findBlock("EnderIO", "blockIngotStorage"), 6, 850, moltenDarkSteelFluid, false);
    }

    @Handler
    public void init(FMLInitializationEvent event)
    {
        if (TConstruct.pulsar.isPulseLoaded("Tinkers' Smeltery"))
        {
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

            if (TSmeltConfig.EIOElectricalSteelCasting && Loader.isModLoaded("EnderIO"))
            {
                // Making Electrical Steel ingots
                tableCasting.addCastingRecipe(
                        new ItemStack(GameRegistry.findItem("EnderIO", "itemAlloy"), 1, 0), // Electrical Steel
                        moltenSteelIngot,
                        itemSiliconStack,
                        true, 50);
            }

            if (TSmeltConfig.EIOEnergeticAlloyRecipe && Loader.isModLoaded("EnderIO"))
            {
                // Energetic Alloying
                Smeltery.addAlloyMixing(
                        new FluidStack(moltenEnergeticFluid, TConstruct.ingotLiquidValue),
                        moltenGoldIngot,
                        moltenRedstoneDust,
                        moltenGlowstoneDust);
            }

            if (TSmeltConfig.EIOVibrantAlloyRecipe && Loader.isModLoaded("EnderIO"))
            {
                // Vibrant Alloying
                Smeltery.addAlloyMixing(
                        new FluidStack(moltenVibrantFluid, TConstruct.ingotLiquidValue),
                        new FluidStack(moltenEnergeticFluid, TConstruct.ingotLiquidValue),
                        moltenEnder);
            }

            if (TSmeltConfig.EIORedstoneAlloyCasting && Loader.isModLoaded("EnderIO"))
            {
                // Making Redstone Alloy ingots
                tableCasting.addCastingRecipe(
                        new ItemStack(GameRegistry.findItem("EnderIO", "itemAlloy"), 1, 3), // Redstone Alloy
                        moltenRedstoneDust,
                        itemSiliconStack,
                        true, 50);
            }

            if (TSmeltConfig.EIOConductiveIronRecipe && Loader.isModLoaded("EnderIO"))
            {
                // Conductive Iron Alloying
                Smeltery.addAlloyMixing(
                        new FluidStack(moltenConductiveIronFluid, TConstruct.ingotLiquidValue),
                        moltenIronIngot,
                        moltenRedstoneDust);
            }

            if (TSmeltConfig.EIOPulsatingIronRecipe && Loader.isModLoaded("EnderIO"))
            {
                // Pulsating Iron Alloying
                Smeltery.addAlloyMixing(
                        new FluidStack(moltenPulsatingIronFluid, TConstruct.ingotLiquidValue),
                        moltenIronIngot,
                        moltenEnder);
            }

            if (TSmeltConfig.EIODarkSteelRecipe && Loader.isModLoaded("EnderIO"))
            {
                // Dark Steel Alloying
                Smeltery.addAlloyMixing(
                        new FluidStack(moltenDarkSteelFluid, TConstruct.ingotLiquidValue),
                        moltenSteelIngot,
                        moltenEnder);
            }

            if (TSmeltConfig.EIOSoulariumCasting && Loader.isModLoaded("EnderIO"))
            {
                // Making Soularium ingots
                tableCasting.addCastingRecipe(
                        new ItemStack(GameRegistry.findItem("EnderIO", "itemAlloy"), 1, 7),
                        moltenGoldIngot,
                        new ItemStack(Blocks.soul_sand, 1, 0), true, 50);
            }
        }
        else
        {
            ThermalSmeltery.logger.warn("Tinker's Smeltery is disabled, Adding EIO alloy mixing and casting disabled.");
        }
    }
}
