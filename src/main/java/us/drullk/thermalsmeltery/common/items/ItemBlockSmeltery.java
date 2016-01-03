package us.drullk.thermalsmeltery.common.items;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.item.ItemBlockBase;
import cofh.lib.util.helpers.*;
import us.drullk.thermalsmeltery.common.blocks.BlockMachine;
import us.drullk.thermalsmeltery.common.blocks.MachineHelper;
import us.drullk.thermalsmeltery.common.blocks.TileMachineBase;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockSmeltery extends ItemBlockBase
{
    public static final String[] NAMES = new String[]{"basic", "hardened", "reinforced", "resonant"};

    public static ItemStack setDefaultTag(ItemStack stack)
    {
        return setDefaultTag(stack, (byte)0);
    }

    public static ItemStack setDefaultTag(ItemStack stack, byte level)
    {
        MachineHelper.setFacing(stack, 3);
        MachineHelper.setSideCache(stack, TileMachineBase.defaultSideConfigSmeltery[stack.getItemDamage() % 2].defaultSides);
        RedstoneControlHelper.setControl(stack, IRedstoneControl.ControlMode.DISABLED);
        EnergyHelper.setDefaultEnergyTag(stack, 0);
        stack.stackTagCompound.setByte("Level", level);
        AugmentHelper.writeAugments(stack, BlockMachine.defaultAugments);
        return stack;
    }

    public ItemBlockSmeltery(Block block)
    {
        super(block);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setNoRepair();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        return StringHelper.localize(this.getUnlocalizedName(stack)) + " (" + StringHelper.localize("info.thermalexpansion." + NAMES[getLevel(stack)]) + ")";
    }

    public String getUnlocalizedName(ItemStack stack)
    {
        return "tile.thermalsmeltery.machine." + BlockMachine.NAMES[ItemHelper.getItemDamage(stack)] + ".name";
    }

    public static byte getLevel(ItemStack stack) {
        if(stack.stackTagCompound == null) {
            setDefaultTag(stack);
        }

        return stack.stackTagCompound.getByte("Level");
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        switch (getLevel(stack))
        {
            case 2:
                return EnumRarity.uncommon;
            case 3:
                return EnumRarity.rare;
            default:
                return EnumRarity.common;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean var4)
    {
        SecurityHelper.addOwnerInformation(stack, list);
        if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown())
        {
            list.add(StringHelper.shiftForDetails());
        }

        if (StringHelper.isShiftKeyDown())
        {
            SecurityHelper.addAccessInformation(stack, list);
            list.add(StringHelper.getInfoText("info.thermalsmeltery.machine." + BlockMachine.NAMES[ItemHelper.getItemDamage(stack)]));
        }
    }
}
