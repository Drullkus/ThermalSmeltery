package com.drullkus.thermalsmeltery;

import com.drullkus.thermalsmeltery.common.blocks.TSBlocks;
import com.drullkus.thermalsmeltery.common.plugins.eio.smeltery.EnderIOSmeltery;
import com.drullkus.thermalsmeltery.common.plugins.tcon.ThermalConstruct.ThermalConstruct;
import mantle.pulsar.config.ForgeCFG;
import mantle.pulsar.control.PulseManager;
import net.minecraft.creativetab.CreativeTabs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.drullkus.thermalsmeltery.common.core.handler.TSCreativeTab;
import com.drullkus.thermalsmeltery.common.core.handler.TSmeltConfig;
import com.drullkus.thermalsmeltery.common.items.TSItems;
import com.drullkus.thermalsmeltery.common.lib.LibMisc;
import com.drullkus.thermalsmeltery.common.plugins.tcon.smeltery.TConSmeltery;
import com.drullkus.thermalsmeltery.common.plugins.tcon.tools.TConToolModifiers;
import com.drullkus.thermalsmeltery.common.plugins.te.TSmeltTE;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = LibMisc.MOD_ID, name = LibMisc.MOD_NAME, dependencies = LibMisc.DEPENDENCIES)
public class ThermalSmeltery
{
	public static final Logger logger = LogManager.getLogger(LibMisc.MOD_ID);

	public static PulseManager pulsar = new PulseManager(LibMisc.MOD_ID, new ForgeCFG("TSmeltModules", "Modules: Disabling these will disable a chunk of the mod"));

	public static CreativeTabs itemTab = new TSCreativeTab("Items");

    @Mod.Instance(LibMisc.MOD_ID)
    public static ThermalSmeltery instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
        TSmeltConfig.initProps(event.getModConfigurationDirectory());
		TSItems.preInit();

		pulsar.registerPulse(new TSmeltTE());
		pulsar.registerPulse(new TConSmeltery());
		pulsar.registerPulse(new TConToolModifiers());
        pulsar.registerPulse(new EnderIOSmeltery());
        pulsar.registerPulse(new ThermalConstruct());

		pulsar.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
        TSBlocks.init();
        TSItems.initialize();

		pulsar.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
        TSBlocks.postInit();
		TSItems.postInit();
		pulsar.postInit(event);

		logger.info("Oh no... I'm smelting!");
	}

}
