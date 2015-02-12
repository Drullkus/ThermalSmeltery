package com.drullkus.thermalsmeltery.addons;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.event.FMLInterModComms;

public class TE4Helper {

    public static void addPulverizerRecipe (int energy, ItemStack input, ItemStack primaryOutput)
    {

        addPulverizerRecipe(energy, input, primaryOutput, null, 0);
    }

    public static void addPulverizerRecipe (int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput)
    {

        addPulverizerRecipe(energy, input, primaryOutput, secondaryOutput, 100);
    }

    public static void addPulverizerRecipe (int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance)
    {

        if (input == null || primaryOutput == null || secondaryOutput == null)
        {
            return;
        }
        NBTTagCompound toSend = new NBTTagCompound();

        toSend.setInteger("energy", energy);
        toSend.setTag("input", new NBTTagCompound());
        toSend.setTag("primaryOutput", new NBTTagCompound());
        toSend.setTag("secondaryOutput", new NBTTagCompound());

        input.writeToNBT(toSend.getCompoundTag("input"));
        primaryOutput.writeToNBT(toSend.getCompoundTag("primaryOutput"));
        secondaryOutput.writeToNBT(toSend.getCompoundTag("secondaryOutput"));
        toSend.setInteger("secondaryChance", secondaryChance);

        FMLInterModComms.sendMessage("ThermalExpansion", "PulverizerRecipe", toSend);
    }

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