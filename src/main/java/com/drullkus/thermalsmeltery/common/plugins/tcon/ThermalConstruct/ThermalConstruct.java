package com.drullkus.thermalsmeltery.common.plugins.tcon.ThermalConstruct;

import com.drullkus.thermalsmeltery.common.plugins.tcon.ThermalConstruct.blocks.SmelteryMachine;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraft.block.Block;

@Pulse(id = "ThermalConstruct", description = "RF Smeltery for your TCon needs", modsRequired = "TConstruct;CoFHCore")
public class ThermalConstruct {

    public static Block smelteryMachine;

    @Handler
    public void preInit(FMLPreInitializationEvent event)
    {
        //smelteryMachine = new SmelteryMachine();
    }
}
