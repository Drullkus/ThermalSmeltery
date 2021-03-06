package us.drullk.thermalsmeltery.common.plugins.te;

import java.util.Map;

import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.init.Items;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.CastingRecipe;
import tconstruct.library.crafting.LiquidCasting;
import us.drullk.thermalsmeltery.ThermalSmeltery;
import us.drullk.thermalsmeltery.common.blocks.MachineHelper;
import us.drullk.thermalsmeltery.common.blocks.ModBlocks;
import us.drullk.thermalsmeltery.common.gui.GuiHandler;
import us.drullk.thermalsmeltery.common.items.TSItems;
import us.drullk.thermalsmeltery.common.network.PacketThermalSmeltery;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import mantle.utils.ItemMetaWrapper;

import us.drullk.thermalsmeltery.common.core.handler.TSmeltConfig;
import us.drullk.thermalsmeltery.common.lib.LibMisc;

import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(LibMisc.MOD_ID)
@Pulse(id = "TSmelt TE", description = "Thermal Expansion Integration", modsRequired = "ThermalExpansion")
public class TSmeltTE
{
	//public static final GuiHandler guiHandler = new GuiHandler();
	ItemStack nullifier;

	@Handler
	public void preInit(FMLPostInitializationEvent event)
	{
		MachineHelper.initialize();
		ModBlocks.initialize();
		PacketThermalSmeltery.initialize();
	}

	@Handler
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(ThermalSmeltery.instance, new GuiHandler());
	}

	@Handler
	public void postInit(FMLPostInitializationEvent event)
	{
		Map<ItemMetaWrapper, FluidStack> smelteryMap = tconstruct.library.crafting.Smeltery.getSmeltingList();
		Map<ItemMetaWrapper, Integer> tempMap = tconstruct.library.crafting.Smeltery.getTemperatureList();

		for(Map.Entry<ItemMetaWrapper, FluidStack> entry : smelteryMap.entrySet())
		{
			ItemStack input = new ItemStack(entry.getKey().item, 1, entry.getKey().meta);
			int energy = tempMap.get(entry.getKey()) * TSmeltConfig.multiplier;
			TE4Helper.addCrucibleRecipe(energy, input, entry.getValue());
		}

		this.nullifier = new ItemStack(GameRegistry.findItem("ThermalExpansion", "Device"), 1, 5);
		TE4Helper.addPulverizerRecipe(20000, this.nullifier, new ItemStack(TSItems.itemBase, 1, 0));
		TE4Helper.addPulveriserRecipe(1337, new ItemStack(Items.potato), TSItems.potatoesMashed, ItemHelper.cloneStack(TSItems.potatoesWedge, 2), 10);

		LiquidCasting tableCasting = TConstructRegistry.getTableCasting();
		for(CastingRecipe recipe : tableCasting.getCastingRecipes())
		{
			MachineRecipeRegistry.registerStampingRecipe(tableCasting, recipe);
			MachineRecipeRegistry.registerIngotRecipe(recipe);
		}

		LiquidCasting basinCasting = TConstructRegistry.getBasinCasting();
		for(CastingRecipe recipe : basinCasting.getCastingRecipes())
		{
			MachineRecipeRegistry.registerBlockRecipe(recipe);
		}

		ModBlocks.postInit();
	}
}
