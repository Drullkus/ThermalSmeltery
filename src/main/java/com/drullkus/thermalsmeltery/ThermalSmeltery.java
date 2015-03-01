package com.drullkus.thermalsmeltery;

import com.drullkus.thermalsmeltery.common.core.handler.TSmeltConfig;
import com.drullkus.thermalsmeltery.common.core.handler.ModCreativeTab;
import com.drullkus.thermalsmeltery.common.gui.GuiHandler;
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
import cpw.mods.fml.common.network.NetworkRegistry;
import mantle.pulsar.config.ForgeCFG;
import mantle.pulsar.control.PulseManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = LibMisc.MOD_ID, name = LibMisc.MOD_NAME, dependencies = LibMisc.DEPENDENCIES)
public class ThermalSmeltery
{
    @Mod.Instance(value = LibMisc.MOD_ID)
    public static ThermalSmeltery instance = new ThermalSmeltery();

    public static final Logger logger = LogManager.getLogger(LibMisc.MOD_ID);

    public static PulseManager pulsar = new PulseManager(LibMisc.MOD_ID, new ForgeCFG("TSmeltModules", "Modules: Disabling these will disable a chunk of the mod"));

    public static ModCreativeTab itemTab;

    public static final GuiHandler guiHandler = new GuiHandler();

    @EventHandler
    public void preInit (FMLPreInitializationEvent event)
    {
        TSmeltConfig.initProps(event.getModConfigurationDirectory());

        pulsar.registerPulse(new TSmeltTE());
        pulsar.registerPulse(new TConSmeltery());
        pulsar.registerPulse(new TConToolModifiers());

        itemTab = new ModCreativeTab("ThermalSmeltery");

        pulsar.preInit(event);
    }

    @EventHandler
    public void init (FMLInitializationEvent event)
    {
        ModItems.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(ThermalSmeltery.instance, guiHandler);
        pulsar.init(event);
    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent event)
    {
        pulsar.postInit(event);

        // logger.info("Oh no... I'm smelting! I better call Saul!"); RIP
    }

}
