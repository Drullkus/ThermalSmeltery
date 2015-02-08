package com.drullkus.thermalsmeltery.addons;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.event.FMLInterModComms;

public class TE4Helper {

    public static void addCrucibleRecipe (int energy, ItemStack input, FluidStack output)
    {

        if (input == null || output == null)
        {
            return;
        }

        NBTTagCompound toSend = new NBTTagCompound();

        toSend.setInteger("energy", energy);
        toSend.setTag("input", new NBTTagCompound());
        toSend.setTag("output", new NBTTagCompound());

        input.writeToNBT(toSend.getCompoundTag("input"));
        output.writeToNBT(toSend.getCompoundTag("output"));

        FMLInterModComms.sendMessage("ThermalExpansion", "CrucibleRecipe", toSend);

    }

}