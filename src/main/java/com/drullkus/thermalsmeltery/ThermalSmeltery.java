package com.drullkus.thermalsmeltery;

import com.drullkus.thermalsmeltery.addons.DoStuff;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(modid = "thermalsmeltery", name = "Thermal Smeltery", version = "1.0.0", dependencies = "required-after:ThermalExpansion;required-after:TConstruct")
public class ThermalSmeltery {

    public static final String modID = "Thermal Smeltery";
    public static final Logger logger = LogManager.getLogger(modID);

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        DoStuff.postInit();
        logger.info("Oh no... I'm smelting! I better call Saul!");
    }

}
