package com.drullkus.thermalsmeltery;

import com.drullkus.thermalsmeltery.addons.DoStuff;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(modid = "thermalsmeltery", name = "Thermal Smeltery", version = "0.0.0", dependencies = "required-after:ThermalExpansion;required-after:TConstruct")
public class ThermalSmeltery {

    public static final String modID = "Thermal Smeltery";
    public static final Logger logger = LogManager.getLogger(modID);

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        DoStuff.init();
        logger.info("Help me, I'm smelting! I'm smeeeelting...");
    }

}
