package us.drullk.thermalsmeltery;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.drullk.thermalsmeltery.common.Compat;
import us.drullk.thermalsmeltery.common.LibMisc;
import us.drullk.thermalsmeltery.common.TSmeltConfig;

@Mod(modid = LibMisc.MOD_ID, name = LibMisc.MOD_NAME, dependencies = LibMisc.DEPENDENCIES)
public class ThermalSmeltery {
    private static final String modID = "Thermal Smeltery";
    private static final Logger logger = LogManager.getLogger(modID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        TSmeltConfig.initProps(event.getModConfigurationDirectory());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        Compat.letsGetCooking();
        logger.info("*Ding* Dinner's ready!");
    }
}