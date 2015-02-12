package com.drullkus.thermalsmeltery;

import net.minecraftforge.common.config.Configuration;

public class Config {
    public static int multiplier;
    public static boolean TConModifiers;

    public Config(Configuration config)
    {
        config.load();

        multiplier = config.get("Magma Crucible Recipe Adaptation from Smeltery","RF Cost Multiplier", 5).getInt();

        TConModifiers = config.get("Tinker's Construct","Enable TCon Modifiers", true).getBoolean();

        if(config.hasChanged())
        {
            config.save();
        }
    }
}
