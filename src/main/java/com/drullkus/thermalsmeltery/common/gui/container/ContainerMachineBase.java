package com.drullkus.thermalsmeltery.common.gui.container;

import cofh.api.tileentity.IAugmentable;
import cofh.core.block.TileCoFHBase;
import cofh.core.gui.slot.SlotAugment;
import cofh.lib.gui.container.IAugmentableContainer;
import cofh.lib.gui.slot.SlotFalseCopy;
import cofh.lib.util.helpers.AugmentHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.ServerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import thermalexpansion.network.PacketTEBase;

public class ContainerMachineBase extends Container implements IAugmentableContainer
{
    TileCoFHBase baseTile;
    protected Slot[] augmentSlots = new Slot[0];
    protected boolean[] augmentStatus = new boolean[0];
    protected boolean augmentLock = true;

    public ContainerMachineBase()
    {
    }

    public ContainerMachineBase(TileEntity var1)
    {
        this.baseTile = (TileCoFHBase)var1;
    }

    public ContainerMachineBase(InventoryPlayer var1, TileEntity var2)
    {
        if (var2 instanceof TileCoFHBase)
        {
            this.baseTile = (TileCoFHBase)var2;
        }

        this.addAugmentSlots();
        this.addPlayerInventory(var1);
    }

    protected void addAugmentSlots()
    {
        if (this.baseTile instanceof IAugmentable)
        {
            this.augmentSlots = new Slot[((IAugmentable)this.baseTile).getAugmentSlots().length];

            for (int var1 = 0; var1 < this.augmentSlots.length; ++var1)
            {
                this.augmentSlots[var1] = this.addSlotToContainer(new SlotAugment((IAugmentable)this.baseTile, (IInventory)null, var1, 0, 0));
            }
        }

    }

    protected void addPlayerInventory(InventoryPlayer var1)
    {
        int var2;
        for (var2 = 0; var2 < 3; ++var2)
        {
            for (int var3 = 0; var3 < 9; ++var3)
            {
                this.addSlotToContainer(new Slot(var1, var3 + var2 * 9 + 9, 8 + var3 * 18, 84 + var2 * 18));
            }
        }

        for (var2 = 0; var2 < 9; ++var2)
        {
            this.addSlotToContainer(new Slot(var1, var2, 8 + var2 * 18, 142));
        }

    }

    public boolean canInteractWith(EntityPlayer var1)
    {
        return this.baseTile == null ? true : this.baseTile.isUseable(var1);
    }

    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        if (this.baseTile != null)
        {
            for (int var1 = 0; var1 < this.crafters.size(); ++var1)
            {
                this.baseTile.sendGuiNetworkData(this, (ICrafting)this.crafters.get(var1));
            }

        }
    }

    public void updateProgressBar(int var1, int var2)
    {
        if (this.baseTile != null)
        {
            this.baseTile.receiveGuiNetworkData(var1, var2);
        }
    }

    public ItemStack transferStackInSlot(EntityPlayer var1, int var2)
    {
        ItemStack var3 = null;
        Slot var4 = (Slot)this.inventorySlots.get(var2);
        int var5 = this.augmentSlots.length;
        int var6 = var5 + 27;
        int var7 = var6 + 9;
        int var8 = var7 + (this.baseTile == null ? 0 : this.baseTile.getInvSlotCount());
        if (var4 != null && var4.getHasStack())
        {
            ItemStack var9 = var4.getStack();
            var3 = var9.copy();
            if (var2 < var5)
            {
                if (!this.mergeItemStack(var9, var5, var7, true))
                {
                    return null;
                }
            } else if (var2 < var7)
            {
                if (!this.augmentLock && var5 > 0 && AugmentHelper.isAugmentItem(var9))
                {
                    if (!this.mergeItemStack(var9, 0, var5, false))
                    {
                        return null;
                    }
                } else if (!this.mergeItemStack(var9, var7, var8, false))
                {
                    return null;
                }
            } else if (!this.mergeItemStack(var9, var5, var7, true))
            {
                return null;
            }

            if (var9.stackSize <= 0)
            {
                var4.putStack((ItemStack)null);
            } else
            {
                var4.onSlotChanged();
            }

            if (var9.stackSize == var3.stackSize)
            {
                return null;
            }
        }

        return var3;
    }

    public ItemStack slotClick(int var1, int var2, int var3, EntityPlayer var4)
    {
        Slot var5 = var1 < 0 ? null : (Slot)this.inventorySlots.get(var1);
        if (var5 instanceof SlotFalseCopy)
        {
            if (var2 == 2)
            {
                var5.putStack((ItemStack)null);
                var5.onSlotChanged();
            } else
            {
                var5.putStack(var4.inventory.getItemStack() == null ? null : var4.inventory.getItemStack().copy());
            }

            return var4.inventory.getItemStack();
        } else
        {
            return super.slotClick(var1, var2, var3, var4);
        }
    }

    protected boolean mergeItemStack(ItemStack var1, int var2, int var3, boolean var4)
    {
        boolean var5 = false;
        int var6 = var4 ? var3 - 1 : var2;
        Slot var7;
        ItemStack var8;
        if (var1.isStackable())
        {
            for (; var1.stackSize > 0 && (!var4 && var6 < var3 || var4 && var6 >= var2); var6 += var4 ? -1 : 1)
            {
                var7 = (Slot)this.inventorySlots.get(var6);
                var8 = var7.getStack();
                if (var7.isItemValid(var1) && ItemHelper.itemsEqualWithMetadata(var1, var8, true))
                {
                    int var9 = var8.stackSize + var1.stackSize;
                    int var10 = Math.min(var1.getMaxStackSize(), var7.getSlotStackLimit());
                    if (var9 <= var10)
                    {
                        var1.stackSize = 0;
                        var8.stackSize = var9;
                        var7.onSlotChanged();
                        var5 = true;
                    } else if (var8.stackSize < var10)
                    {
                        var1.stackSize -= var10 - var8.stackSize;
                        var8.stackSize = var10;
                        var7.onSlotChanged();
                        var5 = true;
                    }
                }
            }
        }

        if (var1.stackSize > 0)
        {
            for (var6 = var4 ? var3 - 1 : var2; !var4 && var6 < var3 || var4 && var6 >= var2; var6 += var4 ? -1 : 1)
            {
                var7 = (Slot)this.inventorySlots.get(var6);
                var8 = var7.getStack();
                if (var7.isItemValid(var1) && var8 == null)
                {
                    var7.putStack(ItemHelper.cloneStack(var1, Math.min(var1.stackSize, var7.getSlotStackLimit())));
                    var7.onSlotChanged();
                    if (var7.getStack() != null)
                    {
                        var1.stackSize -= var7.getStack().stackSize;
                        var5 = true;
                    }
                    break;
                }
            }
        }

        return var5;
    }

    public void setAugmentLock(boolean var1)
    {
        this.augmentLock = var1;
        if (ServerHelper.isClientWorld(this.baseTile.getWorldObj()))
        {
            PacketTEBase.sendTabAugmentPacketToServer(var1);
        }

    }

    public Slot[] getAugmentSlots()
    {
        return this.augmentSlots;
    }
}
