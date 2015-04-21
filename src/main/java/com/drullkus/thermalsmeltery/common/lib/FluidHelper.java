package com.drullkus.thermalsmeltery.common.lib;

import com.drullkus.thermalsmeltery.ThermalSmeltery;
import com.drullkus.thermalsmeltery.common.plugins.eio.smeltery.EnderIOSmeltery;
import com.drullkus.thermalsmeltery.common.plugins.eio.smeltery.FilledBucket;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidHelper {

    public static Fluid registerFluid(String name) {
        return registerFluid(name, "liquid_" + name);
    }

    public static Fluid registerFluid(String name, String texture) {
        return registerFluid(name, name + ".molten", "fluid.molten." + name, texture, 3000, 6000, 1300, Material.lava);
    }

    public static Fluid registerFluid(String name, String fluidName, String blockName, String texture, int density, int viscosity, int temperature, Material material)
    {
        Fluid fluid = new Fluid(fluidName).setDensity(density).setViscosity(viscosity).setTemperature(temperature);

        if(material == Material.lava)
            fluid.setLuminosity(12);

        boolean isFluidPreRegistered = !FluidRegistry.registerFluid(fluid);

        TSmeltFluid block = new TSmeltFluid(fluid, material, texture);
        block.setBlockName(blockName);
        GameRegistry.registerBlock(block, blockName);

        fluid.setBlock(block);
        block.setFluid(fluid);

        if (isFluidPreRegistered)
        {
            fluid = FluidRegistry.getFluid(fluidName);

            // don't change the fluid icons of already existing fluids
            if(fluid.getBlock() != null)
                block.suppressOverwritingFluidIcons();
                // if no block is registered with an existing liquid, we set our own
            else
                fluid.setBlock(block);
        }

        if (FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, 1000), new ItemStack(Items.bucket)) == null) {
            boolean reg = false;
            for(int i = 0; i < FilledBucket.textureNames.length; i++)
                if(FilledBucket.textureNames[i].equals(name)) {
                    FluidContainerRegistry.registerFluidContainer(new FluidContainerRegistry.FluidContainerData(new FluidStack(fluid, 1000), new ItemStack(EnderIOSmeltery.buckets, 1, i), new ItemStack(Items.bucket)));
                    reg = true;
                }

            if(!reg)
                ThermalSmeltery.logger.error("Couldn't register fluid container for " + name);
        }

        return fluid;
    }
}
