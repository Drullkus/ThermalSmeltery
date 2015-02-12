package com.drullkus.thermalsmeltery.addons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.tools.ToolCore;

public class TConActiveToolMod extends ActiveToolMod {

    @Override
    public boolean beforeBlockBreak (ToolCore tool, ItemStack item, int X, int Y, int Z, EntityLivingBase player)
    {
        NBTTagCompound tags = item.getTagCompound().getCompoundTag("InfiTool");
        return false;
    }
}
