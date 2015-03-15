package com.drullkus.thermalsmeltery.common.core.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class TSmeltConfig
{

	public static void initProps(File location)
	{
		File mainFile = new File(location + "/ThermalSmeltery.cfg");

		Configuration config = new Configuration(mainFile);

		multiplier = config.get("Thermal Expansion", "The Multiplier for RF Cost for Magma Crucible recipe adaptation", 5, "Only used if the Thermal Expansion Module on.").getInt(5);

		TConSteelRecipe = config.get("Tinkers Smeltery", "Allow Steel to be made in the Smeltery", true, "Only used if the Tinker's construct Smeltery Module is enabled.").getBoolean(true);
		TConYelloriumCasting = config.get("Tinkers Smeltery", "Allow Yellorium to be casted into the casting table/basin.", true, "Only used if the Tinker's construct Smeltery Module is enabled.").getBoolean(true);

		/* Save the configuration file only if it has changed */
		if (config.hasChanged())
		{
			config.save();
		}
	}

	public static int multiplier;

	public static boolean TConSteelRecipe, TConYelloriumCasting;

}
