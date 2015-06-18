package com.drullkus.thermalsmeltery.common.blocks;

import cofh.core.block.BlockCoFHBase;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BlockTSMachine extends BlockCoFHBase {

    public static String[] machineNames;

    public BlockTSMachine(String[] machineVariants, String name, CreativeTabs creativeTab) {
        super(Material.iron);
        machineNames = machineVariants;
        setBlockName(name);
        setCreativeTab(creativeTab);
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

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return null;
    }
}
