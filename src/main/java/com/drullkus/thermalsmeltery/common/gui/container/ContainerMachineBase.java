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

            for (int slot = 0; slot < this.augmentSlots.length; ++slot)
            {
                this.augmentSlots[slot] = this.addSlotToContainer(new SlotAugment((IAugmentable)this.baseTile, null, slot, 0, 0));
            }
        }

    }

    protected void addPlayerInventory(InventoryPlayer player)
    {
        int i;
        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 142));
        }

    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return this.baseTile == null || this.baseTile.isUseable(player);
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
    public void updateProgressBar(int i, int j)
    {
        if (this.baseTile != null)
        {
            this.baseTile.receiveGuiNetworkData(i, j);
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot)
    {
        ItemStack stack = null;
        Slot slotObj = (Slot)this.inventorySlots.get(slot);
        int aug = this.augmentSlots.length;
        int playerMain = aug + 27;
        int playerTool = playerMain + 9;
        int tileInv = playerTool + (this.baseTile == null ? 0 : this.baseTile.getInvSlotCount());
        if (slotObj != null && slotObj.getHasStack())
        {
            ItemStack slotStack = slotObj.getStack();
            stack = slotStack.copy();
            if (slot < aug)
            {
                if (!this.mergeItemStack(slotStack, aug, playerTool, true))
                {
                    return null;
                }
            } else if (slot < playerTool)
            {
                if (!this.augmentLock && aug > 0 && AugmentHelper.isAugmentItem(slotStack))
                {
                    if (!this.mergeItemStack(slotStack, 0, aug, false))
                    {
                        return null;
                    }
                } else if (!this.mergeItemStack(slotStack, playerTool, tileInv, false))
                {
                    return null;
                }
            } else if (!this.mergeItemStack(slotStack, aug, playerTool, true))
            {
                return null;
            }

            if (slotStack.stackSize <= 0)
            {
                slotObj.putStack(null);
            } else
            {
                slotObj.onSlotChanged();
            }

            if (slotStack.stackSize == stack.stackSize)
            {
                return null;
            }
        }

        return stack;
    }

    @Override
    public ItemStack slotClick(int slot, int mouse, int var3, EntityPlayer player)
    {
        Slot slotObj = slot < 0 ? null : (Slot)this.inventorySlots.get(slot);
        if (slotObj instanceof SlotFalseCopy)
        {
            if (mouse == 2)
            {
                slotObj.putStack(null);
                slotObj.onSlotChanged();
            } else
            {
                slotObj.putStack(player.inventory.getItemStack() == null ? null : player.inventory.getItemStack().copy());
            }

            return player.inventory.getItemStack();
        } else
        {
            return super.slotClick(slot, mouse, var3, player);
        }
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int start, int end, boolean reverse)
    {
        boolean merge = false;
        int slot = reverse ? end - 1 : start;
        Slot slotObj;
        ItemStack slotStack;
        if (stack.isStackable())
        {
            for (; stack.stackSize > 0 && (!reverse && slot < end || reverse && slot >= start); slot += reverse ? -1 : 1)
            {
                slotObj = (Slot)this.inventorySlots.get(slot);
                slotStack = slotObj.getStack();
                if (slotObj.isItemValid(stack) && ItemHelper.itemsEqualWithMetadata(stack, slotStack, true))
                {
                    int stackAmount = slotStack.stackSize + stack.stackSize;
                    int maxAmount = Math.min(stack.getMaxStackSize(), slotObj.getSlotStackLimit());
                    if (stackAmount <= maxAmount)
                    {
                        stack.stackSize = 0;
                        slotStack.stackSize = stackAmount;
                        slotObj.onSlotChanged();
                        merge = true;
                    } else if (slotStack.stackSize < maxAmount)
                    {
                        stack.stackSize -= maxAmount - slotStack.stackSize;
                        slotStack.stackSize = maxAmount;
                        slotObj.onSlotChanged();
                        merge = true;
                    }
                }
            }
        }

        if (stack.stackSize > 0)
        {
            for (slot = reverse ? end - 1 : start; !reverse && slot < end || reverse && slot >= start; slot += reverse ? -1 : 1)
            {
                slotObj = (Slot)this.inventorySlots.get(slot);
                slotStack = slotObj.getStack();
                if (slotObj.isItemValid(stack) && slotStack == null)
                {
                    slotObj.putStack(ItemHelper.cloneStack(stack, Math.min(stack.stackSize, slotObj.getSlotStackLimit())));
                    slotObj.onSlotChanged();
                    if (slotObj.getStack() != null)
                    {
                        stack.stackSize -= slotObj.getStack().stackSize;
                        merge = true;
                    }
                    break;
                }
            }
        }

        return merge;
    }

    @Override
    public void setAugmentLock(boolean lock)
    {
        this.augmentLock = lock;
        if (ServerHelper.isClientWorld(this.baseTile.getWorldObj()))
        {
//            TODO: Fix this
//            PacketTEBase.sendTabAugmentPacketToServer(lock);
        }

    }

    @Override
    public Slot[] getAugmentSlots()
    {
        return this.augmentSlots;
    }
}
