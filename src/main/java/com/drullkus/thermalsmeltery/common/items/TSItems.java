package com.drullkus.thermalsmeltery.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

import com.drullkus.thermalsmeltery.ThermalSmeltery;

import cofh.core.item.ItemBase;
import cpw.mods.fml.common.registry.GameRegistry;

public class TSItems
{
	public static void preInit()
	{
		itemBase = (ItemBase) new ItemBase("thermalsmeltery").setUnlocalizedName("misc").setCreativeTab(ThermalSmeltery.itemTab);
		itemPotatoMashed = new ItemFood(8, 0.6F, false).setUnlocalizedName("thermalsmeltery.misc.potatoMashed").setTextureName("thermalsmeltery:misc/PotatoMashed").setCreativeTab(ThermalSmeltery.itemTab);
		itemPotatoWedge = new ItemFood(2, 0.6F, false).setUnlocalizedName("thermalsmeltery.misc.potatoWedge").setTextureName("thermalsmeltery:misc/PotatoWedge").setCreativeTab(ThermalSmeltery.itemTab);

		GameRegistry.registerItem(itemPotatoMashed, "misc.potatoMashed");
		GameRegistry.registerItem(itemPotatoWedge, "misc.potatoWedge");
	}

	public static void initialize()
	{
		toolPartVoid = itemBase.addItem(0, "voiding");

		potatoesMashed = new ItemStack(itemPotatoMashed);
		potatoesWedge = new ItemStack(itemPotatoWedge);

		GameRegistry.registerCustomItemStack("potatoesMashed", potatoesMashed);
		GameRegistry.registerCustomItemStack("potatoesWedge", potatoesWedge);
	}

	public static void postInit()
	{

	}

	public static ItemBase itemBase;

	public static Item itemPotatoMashed, itemPotatoWedge;

	public static ItemStack potatoesMashed, potatoesWedge, toolPartVoid;

}
