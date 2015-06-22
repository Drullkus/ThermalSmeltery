package com.drullkus.thermalsmeltery.common.items.misc;

import com.drullkus.thermalsmeltery.ThermalSmeltery;
import com.drullkus.thermalsmeltery.client.IconHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public class ItemMod extends Item
{
	public ItemMod()
	{
		super();
		setCreativeTab(ThermalSmeltery.itemTab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister)
	{
		itemIcon = IconHelper.forItem(par1IconRegister, this);
	}

}
