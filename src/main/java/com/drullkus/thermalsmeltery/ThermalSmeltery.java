package com.drullkus.thermalsmeltery;

import com.drullkus.thermalsmeltery.addons.DoStuff;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(modid = "thermalsmeltery", name = "Thermal Smeltery", dependencies = "after:ThermalExpansion;required-after:TConstruct")
public class ThermalSmeltery {

    public static final String modID = "Thermal Smeltery";
    public static final Logger logger = LogManager.getLogger(modID);

    public static Config config;

    @EventHandler
    public void preInit(FMLPreInitializationEvent fEvent)
    {
        config = new Config(new Configuration(fEvent.getSuggestedConfigurationFile()));
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        logger.info("Oh no... I'm smelting! I better call Saul!");
        DoStuff.postInit();
    }

}
