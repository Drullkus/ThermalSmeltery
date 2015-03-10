package com.drullkus.thermalsmeltery.common.gui.client;

import cofh.lib.gui.element.ElementDualScaled;
import cofh.lib.gui.element.ElementEnergyStored;
import com.drullkus.thermalsmeltery.common.gui.container.ContainerStamper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import cofh.thermalexpansion.gui.client.GuiAugmentableBase;
import cofh.thermalexpansion.gui.element.ElementSlotOverlay;

public class GuiStamper extends GuiAugmentableBase
{
    public static final ResourceLocation TEXTURE = new ResourceLocation("thermalsmeltery:textures/gui/stamperGui.png");
    ElementSlotOverlay[] slotPrimaryInput = new ElementSlotOverlay[2];
    ElementSlotOverlay[] slotSecondaryInput = new ElementSlotOverlay[2];
    ElementSlotOverlay[] slotPrimaryOutput = new ElementSlotOverlay[2];
    ElementSlotOverlay[] slotSecondaryOutput = new ElementSlotOverlay[2];
    ElementDualScaled progress;
    ElementDualScaled speed;

    public GuiStamper(InventoryPlayer inventoryPlayer, TileEntity tileEntity)
    {
        super(new ContainerStamper(inventoryPlayer, tileEntity), tileEntity, inventoryPlayer.player, TEXTURE);
        this.generateInfo("tab.thermalsmeltery.machine.stamper", 3);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.slotPrimaryInput[0] = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 55, 29)).setSlotInfo(0, 0, 2));
        this.slotPrimaryInput[1] = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 55, 29)).setSlotInfo(4, 0, 1));
        this.slotSecondaryInput[0] = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 55, 49)).setSlotInfo(0, 0, 2));
        this.slotSecondaryInput[1] = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 55, 49)).setSlotInfo(5, 0, 1));
        this.slotPrimaryOutput[0] = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 123, 25)).setSlotInfo(3, 1, 2));
        this.slotPrimaryOutput[1] = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 123, 25)).setSlotInfo(1, 1, 1));
        this.slotSecondaryOutput[0] = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 127, 54)).setSlotInfo(3, 0, 2));
        this.slotSecondaryOutput[1] = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 127, 54)).setSlotInfo(2, 0, 1));
        this.addElement(new ElementEnergyStored(this, 8, 8, this.myTile.getEnergyStorage()));
        this.progress = (ElementDualScaled)this.addElement((new ElementDualScaled(this, 84, 34)).setMode(1).setSize(24, 16).setTexture("cofh:textures/gui/elements/Progress_Arrow_Right.png", 48, 16));
        //this.speed = (ElementDualScaled)this.addElement((new ElementDualScaled(this, 44, 44)).setSize(16, 16).setTexture("cofh:textures/gui/elements/Scale_Flame.png", 32, 16));
    }

    @Override
    protected void updateElementInformation() {
        super.updateElementInformation();
        this.slotPrimaryInput[0].setVisible(this.myTile.hasSide(1));
        this.slotPrimaryInput[1].setVisible(this.myTile.hasSide(5));
        this.slotSecondaryInput[0].setVisible(this.myTile.hasSide(1));
        this.slotSecondaryInput[1].setVisible(this.myTile.hasSide(6));
        this.slotPrimaryOutput[0].setVisible(this.myTile.hasSide(4));
        this.slotPrimaryOutput[1].setVisible(this.myTile.hasSide(2));
        this.slotSecondaryOutput[0].setVisible(this.myTile.hasSide(4));
        this.slotSecondaryOutput[1].setVisible(this.myTile.hasSide(3));
        if(!this.myTile.hasSide(1)) {
            this.slotPrimaryInput[1].slotRender = 2;
            this.slotSecondaryInput[1].slotRender = 2;
        } else {
            this.slotPrimaryInput[1].slotRender = 1;
            this.slotSecondaryInput[1].slotRender = 1;
        }

        if(!this.myTile.hasSide(4)) {
            this.slotPrimaryOutput[1].slotRender = 2;
            this.slotSecondaryOutput[1].slotRender = 2;
        } else {
            this.slotPrimaryOutput[1].slotRender = 1;
            this.slotSecondaryOutput[1].slotRender = 1;
        }

        this.progress.setQuantity(this.myTile.getScaledProgress(24));
        //this.speed.setQuantity(this.myTile.getScaledSpeed(16));
    }
}
