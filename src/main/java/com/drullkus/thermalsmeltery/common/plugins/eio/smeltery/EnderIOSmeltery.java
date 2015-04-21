package com.drullkus.thermalsmeltery.common.plugins.eio.smeltery;

import com.drullkus.thermalsmeltery.ThermalSmeltery;
import com.drullkus.thermalsmeltery.common.core.handler.TSmeltConfig;
import com.drullkus.thermalsmeltery.common.lib.FluidHelper;
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
import tconstruct.smeltery.TinkerSmeltery;

@GameRegistry.ObjectHolder(LibMisc.MOD_ID)
@Pulse(id = "TSmelt EIO Smeltery", description = "TCon Smeltery Integration for EnderIO", modsRequired = "TConstruct; EnderIO")
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

    @Handler
    public void preInit(FMLPreInitializationEvent event)
    {
        ThermalSmeltery.logger.info("Ender IO module activated!");

        buckets = new FilledBucket(BlockUtils.getBlockFromItem(buckets));
        GameRegistry.registerItem(buckets, "buckets");

        moltenEnergeticFluid = FluidHelper.registerFluid("energeticAlloy");
        moltenEnergetic = moltenEnergeticFluid.getBlock();

        moltenVibrantFluid = FluidHelper.registerFluid("vibrantAlloy");
        moltenVibrant = moltenVibrantFluid.getBlock();

        moltenConductiveIronFluid = FluidHelper.registerFluid("conductiveIron");
        moltenConductiveIron = moltenConductiveIronFluid.getBlock();

        moltenPulsatingIronFluid = FluidHelper.registerFluid("pulsatingIron");
        moltenPulsatingIron = moltenPulsatingIronFluid.getBlock();

        moltenDarkSteelFluid = FluidHelper.registerFluid("darkSteel");
        moltenDarkSteel = moltenDarkSteelFluid.getBlock();

        fluids = new Fluid[] { moltenEnergeticFluid, moltenVibrantFluid, moltenConductiveIronFluid, moltenDarkSteelFluid};
        fluidBlocks = new Block[] { moltenEnergetic, moltenVibrant, moltenConductiveIron, moltenDarkSteel};

        FluidType.registerFluidType("Energetic", GameRegistry.findBlock("EnderIO", "blockIngotStorage"), 1, 650, moltenEnergeticFluid, false);
        FluidType.registerFluidType("Vibrant", GameRegistry.findBlock("EnderIO", "blockIngotStorage"), 2, 750, moltenVibrantFluid, false);
        FluidType.registerFluidType("ConductiveIron", GameRegistry.findBlock("EnderIO", "blockIngotStorage"), 4, 500, moltenConductiveIronFluid, false);
        FluidType.registerFluidType("PulsatingIron", GameRegistry.findBlock("EnderIO", "blockIngotStorage"), 5, 500, moltenPulsatingIronFluid, false);
        FluidType.registerFluidType("DarkSteel", GameRegistry.findBlock("EnderIO", "blockIngotStorage"), 6, 850, moltenDarkSteelFluid, false);
    }

    @Handler
    public void init(FMLInitializationEvent event)
    {
        if (TConstruct.pulsar.isPulseLoaded("Tinkers' Smeltery"))
        {
            ThermalSmeltery.logger.info("Ender IO module activated!");
            ItemStack ingotcast = new ItemStack(TinkerSmeltery.metalPattern, 1, 0);
            LiquidCasting tableCasting = TConstructRegistry.getTableCasting();
            LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();

            if (TSmeltConfig.EIOElectricalSteelCasting && Loader.isModLoaded("EnderIO"))
            {
                // Making Electrical Steel ingots
                tableCasting.addCastingRecipe(
                        new ItemStack(GameRegistry.findItem("EnderIO", "itemAlloy"), 1, 0),
                        new FluidStack(FluidRegistry.getFluid("steel.molten"), TConstruct.ingotLiquidValue),
                        new ItemStack(GameRegistry.findItem("EnderIO", "itemMaterial"), 1, 0), true, 50);
            }

            if (TSmeltConfig.EIOEnergeticAlloyRecipe && Loader.isModLoaded("EnderIO"))
            {
                // Energetic Alloying

            }

            if (TSmeltConfig.EIOSoulariumCasting && Loader.isModLoaded("EnderIO"))
            {
                // Making Soularium ingots
                tableCasting.addCastingRecipe(
                        new ItemStack(GameRegistry.findItem("EnderIO", "itemAlloy"), 1, 7),
                        new FluidStack(FluidRegistry.getFluid("gold.molten"), TConstruct.ingotLiquidValue),
                        new ItemStack(Blocks.soul_sand, 1, 0), true, 50);
            }
        }
        else
        {
            ThermalSmeltery.logger.warn("Tinker's Smeltery is disabled, Adding EIO alloy mixing and casting disabled.");
        }
    }
}
