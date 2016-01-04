package us.drullk.thermalsmeltery.common.items;

import cofh.core.item.ItemBase;
import net.minecraft.item.ItemFood;
import us.drullk.thermalsmeltery.ThermalSmeltery;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TSItems
{

	public static ItemBase itemBase;

	public static Item itemPotatoMashed, itemPotatoWedge;
	public static ItemStack potatoesMashed, potatoesWedge, toolPartVoid;

	public static void preInit()
	{
		itemBase = (ItemBase) new ItemBase("thermalsmeltery").setUnlocalizedName("misc").setCreativeTab(ThermalSmeltery.itemTab);
		itemPotatoMashed = new ItemFood(8, 0.6F, false).setUnlocalizedName("thermalsmeltery.misc.potatoMashed").setTextureName("thermalsmeltery:misc/PotatoMashed").setCreativeTab(ThermalSmeltery.itemTab);
		itemPotatoWedge = new ItemFood(2, 0.6F, false).setUnlocalizedName("thermalsmeltery.misc.potatoWedge").setTextureName("thermalsmeltery:misc/PotatoWedge").setCreativeTab(ThermalSmeltery.itemTab);

		GameRegistry.registerItem(itemPotatoMashed, "misc.potatoMashed");
		GameRegistry.registerItem(itemPotatoWedge, "misc.potatoWedge");
	}

	public static void init() {
		toolPartVoid = itemBase.addItem(0, "voiding");

		potatoesMashed = new ItemStack(itemPotatoMashed);
		potatoesWedge = new ItemStack(itemPotatoWedge);

		GameRegistry.registerCustomItemStack("potatoesMashed", potatoesMashed);
		GameRegistry.registerCustomItemStack("potatoesWedge", potatoesWedge);
    }
}
