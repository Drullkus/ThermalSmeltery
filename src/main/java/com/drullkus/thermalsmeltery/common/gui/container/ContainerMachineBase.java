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

public class ContainerMachineBase extends Container implements IAugmentableContainer
{
    TileCoFHBase baseTile;
    protected Slot[] augmentSlots = new Slot[0];
    protected boolean[] augmentStatus = new boolean[0];
    protected boolean augmentLock = true;

    public ContainerMachineBase(InventoryPlayer player, TileEntity tile)
    {
        if (tile instanceof TileCoFHBase)
        {
            this.baseTile = (TileCoFHBase)tile;
        }

        this.addAugmentSlots();
        this.addPlayerInventory(player);
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

    protected void addPlayerInventory(InventoryPlayer inventory)
    {
        int i;
        for (i = 0; i < 3; ++i)
        {
            for (int column = 0; column < 9; ++column)
            {
                this.addSlotToContainer(new Slot(inventory, column + i * 9 + 9, 8 + column * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
        }

    }

    @Override
    public boolean canInteractWith(EntityPlayer var1)
    {
        return this.baseTile == null || this.baseTile.isUseable(var1);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        if (this.baseTile != null)
        {
            for (Object crafter : this.crafters)
            {
                this.baseTile.sendGuiNetworkData(this, (ICrafting)crafter);
            }

        }
    }

    @Override
    public void updateProgressBar(int var1, int var2)
    {
        if (this.baseTile != null)
        {
            this.baseTile.receiveGuiNetworkData(var1, var2);
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID)
    {
        ItemStack stack = null;
        Slot slot = (Slot)this.inventorySlots.get(slotID);
        int augLength = this.augmentSlots.length;
        int playerMain = augLength + 27;
        int playerTool = playerMain + 9;
        int tileMain = playerTool + (this.baseTile == null ? 0 : this.baseTile.getInvSlotCount());
        if (slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (slotID < augLength)
            {
                if (!this.mergeItemStack(slotStack, augLength, playerTool, true))
                {
                    return null;
                }
            } else if (slotID < playerTool)
            {
                if (!this.augmentLock && augLength > 0 && AugmentHelper.isAugmentItem(slotStack))
                {
                    if (!this.mergeItemStack(slotStack, 0, augLength, false))
                    {
                        return null;
                    }
                } else if (!this.mergeItemStack(slotStack, playerTool, tileMain, false))
                {
                    return null;
                }
            } else if (!this.mergeItemStack(slotStack, augLength, playerTool, true))
            {
                return null;
            }

            if (slotStack.stackSize <= 0)
            {
                slot.putStack(null);
            } else
            {
                slot.onSlotChanged();
            }

            if (slotStack.stackSize == stack.stackSize)
            {
                return null;
            }
        }

        return stack;
    }

    @Override
    public ItemStack slotClick(int slotID, int mouse, int var3, EntityPlayer player)
    {
        Slot slot = slotID < 0 ? null : (Slot)this.inventorySlots.get(slotID);
        if (slot instanceof SlotFalseCopy)
        {
            if (mouse == 2)
            {
                slot.putStack(null);
                slot.onSlotChanged();
            } else
            {
                slot.putStack(player.inventory.getItemStack() == null ? null : player.inventory.getItemStack().copy());
            }

            return player.inventory.getItemStack();
        } else
        {
            return super.slotClick(slotID, mouse, var3, player);
        }
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int var2, int var3, boolean var4)
    {
        boolean var5 = false;
        int var6 = var4 ? var3 - 1 : var2;
        Slot var7;
        ItemStack var8;
        if (stack.isStackable())
        {
            for (; stack.stackSize > 0 && (!var4 && var6 < var3 || var4 && var6 >= var2); var6 += var4 ? -1 : 1)
            {
                var7 = (Slot)this.inventorySlots.get(var6);
                var8 = var7.getStack();
                if (var7.isItemValid(stack) && ItemHelper.itemsEqualWithMetadata(stack, var8, true))
                {
                    int var9 = var8.stackSize + stack.stackSize;
                    int var10 = Math.min(stack.getMaxStackSize(), var7.getSlotStackLimit());
                    if (var9 <= var10)
                    {
                        stack.stackSize = 0;
                        var8.stackSize = var9;
                        var7.onSlotChanged();
                        var5 = true;
                    } else if (var8.stackSize < var10)
                    {
                        stack.stackSize -= var10 - var8.stackSize;
                        var8.stackSize = var10;
                        var7.onSlotChanged();
                        var5 = true;
                    }
                }
            }
        }

        if (stack.stackSize > 0)
        {
            for (var6 = var4 ? var3 - 1 : var2; !var4 && var6 < var3 || var4 && var6 >= var2; var6 += var4 ? -1 : 1)
            {
                var7 = (Slot)this.inventorySlots.get(var6);
                var8 = var7.getStack();
                if (var7.isItemValid(stack) && var8 == null)
                {
                    var7.putStack(ItemHelper.cloneStack(stack, Math.min(stack.stackSize, var7.getSlotStackLimit())));
                    var7.onSlotChanged();
                    if (var7.getStack() != null)
                    {
                        stack.stackSize -= var7.getStack().stackSize;
                        var5 = true;
                    }
                    break;
                }
            }
        }

        return var5;
    }

    public void setAugmentLock(boolean lock)
    {
        this.augmentLock = lock;
        if (ServerHelper.isClientWorld(this.baseTile.getWorldObj()))
        {
            //TODO: Fix this
//            PacketTEBase.sendTabAugmentPacketToServer(var1);
        }

    }

    public Slot[] getAugmentSlots()
    {
        return this.augmentSlots;
    }
}
