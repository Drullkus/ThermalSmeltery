package com.drullkus.thermalsmeltery.common.core.handler;

import java.io.File;

import com.drullkus.thermalsmeltery.common.blocks.MachineHelper;
import net.minecraftforge.common.config.Configuration;

public class TSmeltConfig
{

    public static final String CATEGORY_TE = "Thermal Expansion";
    
    public static void initProps (File location)
    {

        /* Here we will set up the config file for the mod
         * First: Create a folder inside the config folder
         * Make sure to read any old configs file if they exist
         * Second: Create the actual config file
         */
        File mainFile = new File(location + "/ThermalSmeltery.cfg");

        Configuration config = new Configuration(mainFile);

        config.addCustomCategoryComment(CATEGORY_TE,    "Only used if Thermal Expansion module is on.\n" +
                                                        "Do not modify the internal names lightly.");

        multiplier = config.get(CATEGORY_TE, "The Multiplier for RF Cost for Magma Crucible recipe adaptation", 5).getInt(5);
        stamperMultiplier = config.get(CATEGORY_TE, "The Multiplier for RF Cost for Pattern Stamper recipe adaptation", 5).getInt(5);
        extruderMultiplier = config.get(CATEGORY_TE, "The Multiplier for RF Cost for Auto-Caster recipe adaptation", 5).getInt(5);
        MachineHelper.ENERGY_STORAGE = config.get(CATEGORY_TE, "The internal name for the Energy Storage TE augment", MachineHelper.ENERGY_STORAGE).getString();
        MachineHelper.GENERAL_AUTO_OUTPUT = config.get(CATEGORY_TE, "The internal name for the Auto-Output TE augment", MachineHelper.GENERAL_AUTO_OUTPUT).getString();
        MachineHelper.GENERAL_RECONFIG_SIDES = config.get(CATEGORY_TE, "The internal name for the Side Config TE augment", MachineHelper.GENERAL_RECONFIG_SIDES).getString();
        MachineHelper.GENERAL_REDSTONE_CONTROL = config.get(CATEGORY_TE, "The internal name for the generalRedstoneControl TE augment", MachineHelper.GENERAL_REDSTONE_CONTROL).getString();
        MachineHelper.MACHINE_SECONDARY = config.get(CATEGORY_TE, "The internal name for the Secondary Output TE augment", MachineHelper.MACHINE_SECONDARY).getString();
        MachineHelper.MACHINE_SPEED = config.get(CATEGORY_TE, "The internal name for the Processing Speed TE augment", MachineHelper.MACHINE_SPEED).getString();
        MachineHelper.TOOL_MULTIMETER = config.get(CATEGORY_TE, "The internal name for the Multimeter TE item", MachineHelper.TOOL_MULTIMETER).getString();
        MachineHelper.TOOL_DEBUGGER = config.get(CATEGORY_TE, "The internal name for the Debugger TE item", MachineHelper.TOOL_DEBUGGER).getString();
        MachineHelper.GOLD_COIL = config.get(CATEGORY_TE, "The internal name for the Reception Coil item", MachineHelper.GOLD_COIL).getString();

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
