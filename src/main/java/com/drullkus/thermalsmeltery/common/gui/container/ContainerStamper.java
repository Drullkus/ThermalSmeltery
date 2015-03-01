package com.drullkus.thermalsmeltery.common.gui.container;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotRemoveOnly;
import cofh.lib.gui.slot.SlotValidated;
import com.drullkus.thermalsmeltery.common.blocks.TileStamper;
import com.drullkus.thermalsmeltery.common.plugins.tcon.smeltery.StampingRegistry;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import thermalexpansion.gui.container.ContainerTEBase;

public class ContainerStamper extends ContainerTEBase implements ISlotValidator
{
    public TileStamper stamper;
    
    public ContainerStamper(InventoryPlayer inventoryPlayer, TileEntity entity)
    {
        super(inventoryPlayer, entity);
        this.stamper = (TileStamper)entity;
        this.addSlotToContainer(new SlotValidated(this, this.stamper, 0, 55, 29));
        this.addSlotToContainer(new SlotValidated(this, this.stamper, 1, 55, 47));
        this.addSlotToContainer(new SlotRemoveOnly(this.stamper, 2, 127, 29));
        this.addSlotToContainer(new SlotRemoveOnly(this.stamper, 3, 127, 54));
        this.addSlotToContainer(new SlotEnergy(this.stamper, this.stamper.getChargeSlot(), 8, 53));
    }
    
    @Override
    public boolean isItemValid(ItemStack itemStack)
    {
        return StampingRegistry.isValidCast(itemStack) || StampingRegistry.isValidMetal(itemStack);
    }
}
