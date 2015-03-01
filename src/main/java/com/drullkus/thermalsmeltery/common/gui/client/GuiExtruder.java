package com.drullkus.thermalsmeltery.common.gui.client;

import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.gui.element.ElementFluidTank;
import com.drullkus.thermalsmeltery.common.gui.container.ContainerExtruder;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thermalexpansion.gui.client.GuiAugmentableBase;
import thermalexpansion.gui.element.ElementSlotOverlay;

public class GuiExtruder extends GuiAugmentableBase
{
    public static final ResourceLocation TEXTURE = new ResourceLocation("thermalsmeltery:textures/gui/castingExtruderGui.png");
    ElementSlotOverlay[] tankOverlay = new ElementSlotOverlay[2];
    ElementSlotOverlay outputOverlay;

    public GuiExtruder(InventoryPlayer inventoryPlayer, TileEntity tileEntity)
    {
        super(new ContainerExtruder(inventoryPlayer, tileEntity), tileEntity, inventoryPlayer.player, TEXTURE);
        this.generateInfo("tab.thermalsmeltery.machine.extruder", 3);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.addElement(new ElementEnergyStored(this, 8, 8, this.myTile.getEnergyStorage()));
        this.tankOverlay[0] = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 56, 9)).setSlotInfo(0, 3, 2).setVisible(false));
        this.tankOverlay[1] = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 56, 9)).setSlotInfo(2, 3, 1).setVisible(false));
        this.addElement((new ElementFluidTank(this, 56, 9, this.myTile.getTank())).setGauge(1));
        this.outputOverlay = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 113, 26)).setSlotInfo(1, 1, 2).setVisible(true));
    }

    @Override
    protected void updateElementInformation()
    {
        super.updateElementInformation();
        tankOverlay[0].setVisible(this.myTile.hasSide(1));
        tankOverlay[1].setVisible(this.myTile.hasSide(3));
        outputOverlay.setVisible(this.myTile.hasSide(2));
        if(!this.myTile.hasSide(1)) {
            this.tankOverlay[1].slotRender = 2;
        } else {
            this.tankOverlay[1].slotRender = 1;
        }
    }
}
