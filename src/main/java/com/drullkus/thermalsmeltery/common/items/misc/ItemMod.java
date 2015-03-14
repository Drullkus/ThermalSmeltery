package com.drullkus.thermalsmeltery.common.items.misc;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

import com.drullkus.thermalsmeltery.ThermalSmeltery;
import com.drullkus.thermalsmeltery.client.IconHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMod extends Item
{
	public ItemMod()
	{
		super();
		this.setCreativeTab(ThermalSmeltery.itemTab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.itemIcon = IconHelper.forItem(par1IconRegister, this);
	}

}
