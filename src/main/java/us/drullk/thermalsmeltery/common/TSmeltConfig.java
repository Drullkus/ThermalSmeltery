package us.drullk.thermalsmeltery.common;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class TSmeltConfig {
    private static final String CATEGORY_TE = "Thermal Expansion";

    static int rfCostMultiplier;

    public static void initProps(File location)
    {
        File mainFile = new File(location + "/thermal_smeltery.cfg");

        Configuration config = new Configuration(mainFile);

        config.addCustomCategoryComment(CATEGORY_TE, "Thermal Expansion related options");

        rfCostMultiplier = config.get(CATEGORY_TE, "The Multiplier for RF Cost for Magma Crucible recipe adaptation", 5).getInt(5);

        if (config.hasChanged())
            config.save();
    }
}
