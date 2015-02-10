package com.drullkus.thermalsmeltery;

import net.minecraftforge.common.config.Configuration;

public class Config {
    public static int multiplier;

    public Config(Configuration config)
    {
        config.load();

        multiplier = config.get("Magma Crucible","Rf Cost Multiplier", 1).getInt();

        if(config.hasChanged())
        {
            config.save();
        }
    }
}
