package com.drullkus.thermalsmeltery.common.plugins.tcon;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.tools.ToolCore;
import java.util.Random;

public class TConActiveToolMod extends ActiveToolMod
{
    Random random = new Random();

    @Override
    public boolean beforeBlockBreak (ToolCore tool, ItemStack item, int x, int y, int z, EntityLivingBase player)
    {
        /*if (player instanceof EntityPlayer && ((EntityPlayer) player).capabilities.isCreativeMode)
            return false;
        TContent.modLapis.midStreamModify(item, tool);//*/

        NBTTagCompound tags = item.getTagCompound().getCompoundTag("InfiTool");
        World world = player.worldObj;
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (block == null)
            return false;

        if (tags.hasKey("Void Touch") && block.quantityDropped(meta, 0, random) != 0)
        {
            world.setBlockToAir(x, y, z);
            if (player instanceof EntityPlayer && !((EntityPlayer) player).capabilities.isCreativeMode)
                tool.onBlockDestroyed(item, world, block, x, y, z, player);
            if (!world.isRemote)
            {
                world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
            }
            return true;
        }
        return false;
    }
}
