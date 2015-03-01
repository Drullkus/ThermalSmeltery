package com.drullkus.thermalsmeltery.common.gui.client;

import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementEnergyStored;
import com.drullkus.thermalsmeltery.common.blocks.TileExtruder;
import com.drullkus.thermalsmeltery.common.gui.container.ContainerExtruder;
import com.drullkus.thermalsmeltery.common.gui.elements.ElementTinkersTank;
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
    ElementButton block;
    TileExtruder myTile;

    public GuiExtruder(InventoryPlayer inventoryPlayer, TileEntity tileEntity)
    {
        super(new ContainerExtruder(inventoryPlayer, tileEntity), tileEntity, inventoryPlayer.player, TEXTURE);
        this.generateInfo("tab.thermalsmeltery.machine.extruder", 3);
        this.myTile = (TileExtruder)tileEntity;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.addElement(new ElementEnergyStored(this, 8, 8, this.myTile.getEnergyStorage()));
        this.tankOverlay[0] = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 56, 9)).setSlotInfo(0, 3, 2).setVisible(false));
        this.tankOverlay[1] = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 56, 9)).setSlotInfo(2, 3, 1).setVisible(false));
        this.addElement((new ElementTinkersTank(this, 56, 9, this.myTile.getTank())).setGauge(1));
        this.outputOverlay = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 113, 26)).setSlotInfo(1, 1, 2).setVisible(true));
        this.block = (ElementButton)this.addElement(new ElementButton(this, 116, 54, "Block", 184, 0, 184, 20, 184, 40, 20, 20, "thermalsmeltery:textures/gui/castingExtruderGui.png"));
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
        if(this.myTile.block)
        {
            if(!this.myTile.blockFlag)
            {
                this.block.setToolTip("info.thermalexpansion.toggleWait");
                this.block.setDisabled();
            } else
            {
                this.block.setToolTip("info.thermalsmeltery.toggleIngot");
                this.block.setSheetX(184);
                this.block.setHoverX(184);
                this.block.setActive();
            }
        } else if(this.myTile.blockFlag)
        {
            this.block.setToolTip("info.thermalexpansion.toggleWait");
            this.block.setDisabled();
        } else
        {
            this.block.setToolTip("info.thermalsmeltery.toggleBlock");
            this.block.setSheetX(204);
            this.block.setHoverX(204);
            this.block.setActive();
        }
    }

    @Override
    public void handleElementButtonClick(String var1, int var2) {
        if(var1.equals("Block") && this.myTile.block == this.myTile.blockFlag) {
            if(this.myTile.block) {
                playSound("random.click", 1.0F, 0.8F);
            } else {
                playSound("random.click", 1.0F, 0.6F);
            }

            this.myTile.setMode(!this.myTile.block);
        }

    }
}
