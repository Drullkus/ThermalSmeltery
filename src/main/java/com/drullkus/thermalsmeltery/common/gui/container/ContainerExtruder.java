package com.drullkus.thermalsmeltery.common.gui.container;

import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotRemoveOnly;
import com.drullkus.thermalsmeltery.common.blocks.TileExtruder;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class ContainerExtruder extends ContainerMachineBase
{
    public TileExtruder extruder;

    public ContainerExtruder(InventoryPlayer inventoryPlayer, TileEntity entity)
    {
        super(inventoryPlayer, entity);
        this.extruder = (TileExtruder)entity;
        this.addSlotToContainer(new SlotRemoveOnly(this.extruder, 0, 126, 30));
        this.addSlotToContainer(new SlotEnergy(this.extruder, this.extruder.getChargeSlot(), 8, 53));
    }
}
