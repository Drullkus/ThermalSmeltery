package com.drullkus.thermalsmeltery.common.blocks;

import cofh.core.item.ItemBlockBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockTSMachine extends ItemBlockBase {

    public static String[] NAMES = new String[]{"basic", "hardened", "reinforced", "resonant"};

    public ItemBlockTSMachine(Block block)
    {
        super(block);
    }

    public String getUnlocalizedName(ItemStack item) {
        return "tile.thermalsmeltery.machine." + BlockTSMachine.machineNames[ItemHelper.getItemDamage(item)] + ".name";
    }

    public String getItemStackDisplayName(ItemStack item) {
        return StringHelper.localize(this.getUnlocalizedName(item)) + " (" + StringHelper.localize("info.thermalsmeltery." + "WIP") + ")";
    }

}
