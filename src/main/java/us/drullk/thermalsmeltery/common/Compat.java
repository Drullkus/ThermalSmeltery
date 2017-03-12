package us.drullk.thermalsmeltery.common;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.library.tools.ToolPart;
import java.util.List;
import static slimeknights.tconstruct.library.TinkerRegistry.getAllMeltingRecipies;

public class Compat {
    public static void letsGetCooking() {
        List<MeltingRecipe> smelteryMap = getAllMeltingRecipies();

        for (MeltingRecipe recipe : smelteryMap) {
            for (ItemStack entry : recipe.input.getInputs())
            {
                if (entry != null && !(entry.getItem() instanceof ToolPart))
                {
                    int energy = recipe.getTemperature() * TSmeltConfig.rfCostMultiplier; // Calculate temperature to energy here
                    addCrucibleRecipe(energy, entry, recipe.getResult());
                }
            }
        }
    }

    private static void addCrucibleRecipe(int energy, ItemStack input, FluidStack output)
    {
        if (input == null || output == null || FluidRegistry.getFluidName(output) == null)
        {
            return;
        }
        
        NBTTagCompound message = new NBTTagCompound();

        message.setInteger("energy", energy);
        message.setTag("input", new NBTTagCompound());
        message.setTag("output", new NBTTagCompound());

        input.writeToNBT(message.getCompoundTag("input"));
        output.writeToNBT(message.getCompoundTag("output"));

        FMLInterModComms.sendMessage("thermalexpansion", "addcruciblerecipe", message);
    }
}
