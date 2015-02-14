package com.drullkus.thermalsmeltery;

import com.drullkus.thermalsmeltery.common.core.handler.Config;
import com.drullkus.thermalsmeltery.common.core.handler.ModCreativeTab;
import com.drullkus.thermalsmeltery.common.items.ModItems;
import com.drullkus.thermalsmeltery.common.lib.LibMisc;
import com.drullkus.thermalsmeltery.common.plugins.tc4.MagmaCrucibleAdaptation;
import com.drullkus.thermalsmeltery.common.plugins.tcon.TConSmeltery;
import com.drullkus.thermalsmeltery.common.plugins.tcon.TConToolModifiers;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(modid = LibMisc.MOD_ID, name = LibMisc.MOD_NAME, version = LibMisc.VERSION, dependencies = LibMisc.DEPENDENCIES)
public class ThermalSmeltery {

    public static final Logger logger = LogManager.getLogger(LibMisc.MOD_ID);

    public static Config config;
    
    public static ModCreativeTab itemTab;

    @EventHandler
    public void preInit(FMLPreInitializationEvent fEvent)
    {
        config = new Config(new Configuration(fEvent.getSuggestedConfigurationFile()));
        this.itemTab = new ModCreativeTab("thermalsmeltery");
    }

    @EventHandler
    public void init(FMLInitializationEvent fEvent)
    {
        TConSmeltery.addSmelteryAlloy();
        TConSmeltery.addSmelteryCasting();
        
        ModItems.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        logger.info("Oh no... I'm smelting! I better call Saul!");
        MagmaCrucibleAdaptation.letsGetCooking();
        TConToolModifiers.init();
        logger.info("Let's get cooking.");
    }

}
