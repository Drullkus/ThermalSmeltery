package com.drullkus.thermalsmeltery.common.core.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class TSmeltConfig
{

    public static void initProps (File location)
    {

        /* Here we will set up the config file for the mod
         * First: Create a folder inside the config folder
         * Make sure to read any old configs file if they exist
         * Second: Create the actual config file
         */
        File mainFile = new File(location + "/ThermalSmeltery.cfg");

        Configuration config = new Configuration(mainFile);

        multiplier = config.get("Thermal Expansion", "The Multiplier for RF Cost for Magma Crucible recipe adaptation", 5, "Only used if the Thermal Expansion Module on.").getInt(5);
        stamperMultiplier = config.get("Thermal Expansion", "The Multiplier for RF Cost for Pattern Stamper recipe adaptation", 5, "Only used if the Thermal Expansion Module on.").getInt(5);
        extruderMultiplier = config.get("Thermal Expansion", "The Multiplier for RF Cost for Auto-Caster recipe adaptation", 5, "Only used if the Thermal Expansion Module on.").getInt(5);

        tConSteelRecipe = config.get("Tinkers Smeltery", "Allow Steel to be made in the Smeltery", true, "Only used if the Tinker's construct Smeltery Module is enabled.").getBoolean(true);
        tConYelloriumCasting = config.get("Tinkers Smeltery", "Allow Yellorium to be casted into the casting table/basin.", true, "Only used if the Tinker's construct Smeltery Module is enabled.")
                .getBoolean(true);

        /* Save the configuration file only if it has changed */
        if (config.hasChanged())
            config.save();
    }

    // # Part of TE4
    public static int multiplier;

    // # Thermal Smeltery Machines
    public static int stamperMultiplier;
    public static int extruderMultiplier;

    // # TconSmeltery Addons
    public static boolean tConSteelRecipe;
    public static boolean tConYelloriumCasting;

}
