package us.drullk.thermalsmeltery.common.plugins.tcon.engineering;

import mantle.utils.ItemMetaWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import us.drullk.thermalsmeltery.common.core.handler.TSmeltConfig;
import us.drullk.thermalsmeltery.common.plugins.te.TE4Helper;

import java.util.HashMap;
import java.util.Map;

public class SmelteryRecipeHandler
{
	//private static Map<ItemMetaWrapper, FluidStack> meltingRecipes = new HashMap<ItemMetaWrapper, FluidStack>();
	//private static Map<ItemMetaWrapper, Integer> powerCosts = new HashMap<ItemMetaWrapper, Integer>();

	void initalizeRFSmelteryRecipes()
	{
		/*Map<ItemMetaWrapper, FluidStack> smelteryMap = tconstruct.library.crafting.Smeltery.getSmeltingList();
		Map<ItemMetaWrapper, Integer> tempMap = tconstruct.library.crafting.Smeltery.getTemperatureList();

		for(Map.Entry<ItemMetaWrapper, FluidStack> entry : smelteryMap.entrySet())
		{
			ItemStack input = new ItemStack(entry.getKey().item, 1, entry.getKey().meta);
			int energy = tempMap.get(entry.getKey()) * TSmeltConfig.multiplier;
		}//*/
	}
}
