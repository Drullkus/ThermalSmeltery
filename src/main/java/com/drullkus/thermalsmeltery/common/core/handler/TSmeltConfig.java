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

        // Base
		TConSteelRecipe = config.get("Tinkers Smeltery", "Allow Steel to be made in the Smeltery", true, "Only used if the Tinker's construct Smeltery Module is enabled.").getBoolean(true);

        // Big Reactors
        TConYelloriumCasting = config.get("Tinkers Smeltery", "Allow Yellorium to be casted into the casting table/basin.", true, "Only used if the Tinker's construct Smeltery Module is enabled.").getBoolean(true);

        // EnderIO
        EIOElectricalSteelCasting = config.get("Tinkers Smeltery", "Allow Steel to be casted onto Silicon, creating EnderIO's Electrical Steel ingot", true, "Only used if the Tinker's construct Smeltery Module is enabled.").getBoolean(true);
        EIOEnergeticAlloyRecipe = config.get("Tinkers Smeltery", "Allow Destabilized Redstone, Glowstone, and Molten Gold to be mixed, creating molten Energetic Alloy", true, "Only used if the Tinker's construct Smeltery Module is enabled.").getBoolean(true);
        EIOVibrantAlloyRecipe = config.get("Tinkers Smeltery", "Allow Destabilized Redstone, Glowstone, and Molten Gold to be mixed, creating molten Energetic Alloy", true, "Only used if the Tinker's construct Smeltery Module is enabled.").getBoolean(true);
        EIORedstoneAlloyCasting = config.get("Tinkers Smeltery", "Allow Destabilized Redstone to be casted onto Silicon, creating EnderIO's Electrical Steel ingot", true, "Only used if the Tinker's construct Smeltery Module is enabled.").getBoolean(true);
        EIOConductiveIronRecipe = config.get("Tinkers Smeltery", "Allow Destabilized Redstone and Molten Iron to be mixed, creating molten Energetic Alloy", true, "Only used if the Tinker's construct Smeltery Module is enabled.").getBoolean(true);
        EIOPulsatingIronRecipe = config.get("Tinkers Smeltery", "Allow Resonant Ender Fluid and Molten Iron to be mixed, creating molten Energetic Alloy", true, "Only used if the Tinker's construct Smeltery Module is enabled.").getBoolean(true);
        EIODarkSteelRecipe = config.get("Tinkers Smeltery", "Allow Molten Steel and Molten Obsidian to be mixed, creating molten Energetic Alloy", true, "Only used if the Tinker's construct Smeltery Module is enabled.").getBoolean(true);
        EIOSoulariumCasting = config.get("Tinkers Smeltery", "Allow Molten Gold to be casted onto a Soulsand Block, creating EnderIO's Soularium ingot", true, "Only used if the Tinker's construct Smeltery Module is enabled.").getBoolean(true);
        EIOAddMetalCasting = config.get("Tinkers Smeltery", "Allow all EnderIO Metals to be casted into Casting Table/Basins", true, "Only used if the Tinker's construct Smeltery Module is enabled.").getBoolean(true);

		/* Save the configuration file only if it has changed */
		if (config.hasChanged())
		{
			config.save();
		}
	}

	public static int multiplier;

	public static boolean
            TConSteelRecipe,
            TConYelloriumCasting,
            EIOElectricalSteelCasting,
            EIOEnergeticAlloyRecipe,
            EIOVibrantAlloyRecipe,
            EIORedstoneAlloyCasting,
            EIOConductiveIronRecipe,
            EIOPulsatingIronRecipe,
            EIODarkSteelRecipe,
            EIOSoulariumCasting,
            EIOAddMetalCasting;

}
