package com.drullkus.thermalsmeltery;

import mantle.pulsar.config.ForgeCFG;
import mantle.pulsar.control.PulseManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.drullkus.thermalsmeltery.common.core.handler.ModCreativeTab;
import com.drullkus.thermalsmeltery.common.core.handler.TSmeltConfig;
import com.drullkus.thermalsmeltery.common.items.ModItems;
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

	public static ModCreativeTab itemTab;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		TSmeltConfig.initProps(event.getModConfigurationDirectory());

		pulsar.registerPulse(new TSmeltTE());
		pulsar.registerPulse(new TConSmeltery());
		pulsar.registerPulse(new TConToolModifiers());

		this.itemTab = new ModCreativeTab("ThermalSmeltery");

		pulsar.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		ModItems.init();

		pulsar.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		pulsar.postInit(event);

		// logger.info("Oh no... I'm smelting! I better call Saul!"); RIP
	}

}
