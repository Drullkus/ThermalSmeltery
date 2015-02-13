package com.drullkus.thermalsmeltery.addons;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.tools.ToolCore;

public class TConActiveToolMod extends ActiveToolMod {

    @Override
    public boolean beforeBlockBreak (ToolCore tool, ItemStack item, int x, int y, int z, EntityLivingBase player)
    {
        /*if (player instanceof EntityPlayer && ((EntityPlayer) player).capabilities.isCreativeMode)
            return false;
        TContent.modLapis.midStreamModify(item, tool);//*/

        NBTTagCompound tags = item.getTagCompound().getCompoundTag("InfiTool");
        World world = player.worldObj;
        int bID = player.worldObj.getBlockId(x, y, z);
        Block block = Block.blocksList[bID];
        if (block == null || bID < 1 || bID > 4095)
            return false;

        if (tags.hasKey("Void Touch") && block.quantityDropped(meta, 0, random) != 0)
        {
            world.setBlockToAir(x, y, z);
            if (player instanceof EntityPlayer && !((EntityPlayer) player).capabilities.isCreativeMode)
                tool.onBlockDestroyed(item, world, bID, x, y, z, player);
            if (!world.isRemote)
            {
                world.playAuxSFX(2001, x, y, z, bID + (meta << 12));
            }
            return true;
        }
        return false;
    }
}
