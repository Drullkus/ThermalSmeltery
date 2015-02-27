package com.drullkus.thermalsmeltery.common.items;

import cofh.api.tileentity.IRedstoneControl;
import cofh.lib.util.helpers.*;
import com.drullkus.thermalsmeltery.common.blocks.BlockSmeltery;
import com.drullkus.thermalsmeltery.common.blocks.TileSmelteryBase;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import thermalexpansion.block.machine.BlockMachine;
import thermalexpansion.block.machine.ItemBlockMachine;
import thermalexpansion.util.ReconfigurableHelper;

import java.util.List;

public class ItemBlockSmeltery extends ItemBlockMachine
{
    public static final String[] NAMES = new String[]{"basic", "hardened", "reinforced", "resonant"};

    public ItemBlockSmeltery(Block block)
    {
        super(block);
    }

    public static ItemStack setDefaultTag(ItemStack var0)
    {
        return setDefaultTag(var0, (byte)0);
    }

    public static ItemStack setDefaultTag(ItemStack var0, byte var1)
    {
        ReconfigurableHelper.setFacing(var0, 3);
        ReconfigurableHelper.setSideCache(var0, TileSmelteryBase.defaultSideConfigSmeltery[var0.getItemDamage()].defaultSides);
        RedstoneControlHelper.setControl(var0, IRedstoneControl.ControlMode.DISABLED);
        EnergyHelper.setDefaultEnergyTag(var0, 0);
        var0.stackTagCompound.setByte("Level", var1);
        AugmentHelper.writeAugments(var0, BlockMachine.defaultAugments);
        return var0;
    }

    public String getItemStackDisplayName(ItemStack var1)
    {
        return StringHelper.localize(this.getUnlocalizedName(var1)) + " (" + StringHelper.localize("info.thermalexpansion." + NAMES[getLevel(var1)]) + ")";
    }

    public String getUnlocalizedName(ItemStack var1)
    {
        return "tile.thermalsmeltery.machine." + BlockSmeltery.NAMES[ItemHelper.getItemDamage(var1)] + ".name";
    }

    public EnumRarity getRarity(ItemStack var1)
    {
        switch (getLevel(var1))
        {
            case 2:
                return EnumRarity.uncommon;
            case 3:
                return EnumRarity.rare;
            default:
                return EnumRarity.common;
        }
    }

    public void addInformation(ItemStack var1, EntityPlayer var2, List var3, boolean var4)
    {
        SecurityHelper.addOwnerInformation(var1, var3);
        if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown())
        {
            var3.add(StringHelper.shiftForDetails());
        }

        if (StringHelper.isShiftKeyDown())
        {
            SecurityHelper.addAccessInformation(var1, var3);
            var3.add(StringHelper.getInfoText("info.thermalsmeltery.machine." + BlockSmeltery.NAMES[ItemHelper.getItemDamage(var1)]));
        }
    }
}
