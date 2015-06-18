package com.drullkus.thermalsmeltery.common.blocks;

import cofh.core.block.BlockCoFHBase;
import com.drullkus.thermalsmeltery.common.core.Props;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BlockTSMachine extends BlockCoFHBase
{

    public static String[] machineNames;

    public BlockTSMachine(String[] machineVariants, String name, CreativeTabs creativeTab) {
        super(Material.iron);
        machineNames = machineVariants;
        setBlockName(name);
        setCreativeTab(creativeTab);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (meta >= TSBlocks.Types.values().length)
        {
            return null;
        }
        else
        {
            switch(TSBlocks.Types.values()[meta])
            {
                case EXTRUDER:
                    return new TileCastExtruder();
                case STAMPER:
                    return new TilePatternStamper();
                default:
                    return null;
            }
        }
    }

    @Override
    public ArrayList<ItemStack> dismantleBlock(EntityPlayer entityPlayer, NBTTagCompound nbtTagCompound, World world, int i, int i1, int i2, boolean b, boolean b1) {
        return null;
    }

    @Override
    public boolean initialize() {
        return false;
    }

    @Override
    public boolean postInit() {
        return false;
    }
}
