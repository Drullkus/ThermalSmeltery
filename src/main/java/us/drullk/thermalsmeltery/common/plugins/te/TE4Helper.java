package us.drullk.thermalsmeltery.common.plugins.te;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.event.FMLInterModComms;

public class TE4Helper
{

	public static void addPulverizerRecipe(int energy, ItemStack input, ItemStack primaryOutput)
	{
		addPulveriserRecipe(energy, input, primaryOutput, null, 0);
	}

	public static void addPulverizerRecipe(int energy, ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput)
	{
		addPulveriserRecipe(energy, input, primaryOutput, secondaryOutput, 100);
	}

	public static void addPulveriserRecipe(int energy, ItemStack input, ItemStack output, ItemStack bonus, int chance)
	{
		NBTTagCompound data = new NBTTagCompound();

		data.setInteger("energy", energy);

		NBTTagCompound inputCompound = new NBTTagCompound();
		input.writeToNBT(inputCompound);
		data.setTag("input", inputCompound);

		NBTTagCompound outputCompound = new NBTTagCompound();
		output.writeToNBT(outputCompound);
		data.setTag("primaryOutput", outputCompound);

		if(bonus != null)
		{
			NBTTagCompound outputCompound2 = new NBTTagCompound();
			bonus.writeToNBT(outputCompound2);
			data.setTag("secondaryOutput", outputCompound2);

			data.setInteger("secondaryChance", chance);
		}

		FMLInterModComms.sendMessage("ThermalExpansion", "PulverizerRecipe", data);
	}

	public static void addCrucibleRecipe(int energy, ItemStack input, FluidStack output)
	{
		if(input == null || output == null)
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
