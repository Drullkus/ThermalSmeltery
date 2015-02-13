package com.drullkus.thermalsmeltery.addons;

import com.drullkus.thermalsmeltery.Config;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.crafting.ModifyBuilder;

public class TConToolModifiers {
    public static void init()
    {
        if(Config.TConModifiers)
        {
            addModifiers();
            //ThermalSmeltery.logger.info("TCon Modifiers added!");
        }
    }

    static void addModifiers()
    {
        ModifyBuilder.registerModifier(new ModVoidTouch(new ItemStack[]{new ItemStack(GameRegistry.findItem("ThermalExpansion", "Device"), 1, 5)}, 171));
        TConstructClientRegistry.addEffectRenderMapping(171, "ThermalExpansion", "Device", true);

        TConstructRegistry.registerActiveToolMod(new TConActiveToolMod());
    }
}
