package com.drullkus.thermalsmeltery;

import net.minecraftforge.common.config.Configuration;

public class Config {
    public static int multiplier;
    public static boolean TConModifiers;
    public static boolean TConSteelRecipe;
    public static boolean TConYelloriumCasting;

    public Config(Configuration config)
    {
        config.load();

        multiplier = config.get("Thermal Expansion","The Multiplier for RF Cost for Magma Crucible recipe adaptation", 5).getInt();

        TConModifiers = config.get("Tinker's Construct", "Enable TCon Modifiers", true).getBoolean();
        TConSteelRecipe = config.get("Tinker's Construct", "Enable creation of Molten Steel by mixing TCon's Molten Iron and TE's Liquid Coal in Smeltery", true).getBoolean();

        TConYelloriumCasting = config.get("Big Reactors", "Enable casting of Liquid Yellorium onto TCon casting tables/basins", true).getBoolean();

        if(config.hasChanged())
        {
            config.save();
        }
    }
}
