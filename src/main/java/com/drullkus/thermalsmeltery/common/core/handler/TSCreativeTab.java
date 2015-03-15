package com.drullkus.thermalsmeltery.common.core.handler;

import com.drullkus.thermalsmeltery.common.items.TSItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TSCreativeTab extends CreativeTabs
{
	private final String label;

	public TSCreativeTab()
	{
		this("");
	}

	public TSCreativeTab(String paramString)
	{
		super("ThermalSmeltery" + paramString);
		this.label = paramString;
	}

	protected ItemStack getStack()
	{
		return TSItems.potatoesMashed;
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack()
	{
		return getStack();
	}

	@SideOnly(Side.CLIENT)
	public Item getTabIconItem()
	{
		return getIconItemStack().getItem();
	}

	@SideOnly(Side.CLIENT)
	public String getTabLabel()
	{
		return "thermalsmeltery.creativeTab" + this.label;
	}
}
