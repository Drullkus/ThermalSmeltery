package com.drullkus.thermalsmeltery.common.gui.client;

import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementDualScaled;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.gui.element.ElementFluid;
import com.drullkus.thermalsmeltery.common.blocks.TileExtruder;
import com.drullkus.thermalsmeltery.common.gui.container.ContainerExtruder;
import com.drullkus.thermalsmeltery.common.gui.elements.ElementTinkersTank;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import thermalexpansion.gui.client.GuiAugmentableBase;
import thermalexpansion.gui.element.ElementSlotOverlay;

public class GuiExtruder extends GuiAugmentableBase
{
    public static final ResourceLocation TEXTURE = new ResourceLocation("thermalsmeltery:textures/gui/castingExtruderGui.png");
    ElementSlotOverlay[] tankOverlay = new ElementSlotOverlay[2];
    ElementSlotOverlay outputOverlay;
    ElementButton block;
    ElementFluid progressFluid;
    ElementDualScaled progressOverlay;
    TileExtruder extruder;

    public GuiExtruder(InventoryPlayer inventoryPlayer, TileEntity tileEntity)
    {
        super(new ContainerExtruder(inventoryPlayer, tileEntity), tileEntity, inventoryPlayer.player, TEXTURE);
        this.generateInfo("tab.thermalsmeltery.machine.extruder", 3);
        this.extruder = (TileExtruder)tileEntity;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.addElement(new ElementEnergyStored(this, 8, 8, this.extruder.getEnergyStorage()));
        this.tankOverlay[0] = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 40, 9)).setSlotInfo(0, 3, 2).setVisible(false));
        this.tankOverlay[1] = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 40, 9)).setSlotInfo(2, 3, 1).setVisible(false));
        this.addElement((new ElementTinkersTank(this, 40, 9, this.extruder.getTank())).setGauge(1));
        this.outputOverlay = (ElementSlotOverlay)this.addElement((new ElementSlotOverlay(this, 122, 26)).setSlotInfo(1, 1, 2).setVisible(true));
        this.block = (ElementButton)this.addElement(new ElementButton(this, 125, 54, "Block", 184, 0, 184, 20, 184, 40, 20, 20, "thermalsmeltery:textures/gui/castingExtruderGui.png"));
        this.progressFluid = (ElementFluid)this.addElement((new ElementFluid(this, 76, 27)).setFluid(extruder.getTank().getFluid()).setSize(24, 16));
        this.progressOverlay = (ElementDualScaled)this.addElement((new ElementDualScaled(this, 76, 27)).setMode(1).setBackground(false).setSize(24, 16).setTexture("cofh:textures/gui/elements/Progress_Fluid_Right.png", 48, 16));
    }

    @Override
    protected void updateElementInformation()
    {
        super.updateElementInformation();
        tankOverlay[0].setVisible(this.extruder.hasSide(1));
        tankOverlay[1].setVisible(this.extruder.hasSide(3));
        outputOverlay.setVisible(this.extruder.hasSide(2));
        if(!this.extruder.hasSide(1)) {
            this.tankOverlay[1].slotRender = 2;
        } else {
            this.tankOverlay[1].slotRender = 1;
        }
        if(this.extruder.block)
        {
            if(!this.extruder.blockFlag)
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
        } else if(this.extruder.blockFlag)
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
        this.progressFluid.setFluid(this.extruder.getTank().getFluid());
        this.progressFluid.setSize(this.extruder.getScaledProgress(24), 16);
        this.progressOverlay.setQuantity(this.extruder.getScaledProgress(24));
    }

    @Override
    public void handleElementButtonClick(String var1, int var2) {
        if(var1.equals("Block") && this.extruder.block == this.extruder.blockFlag) {
            if(this.extruder.block) {
                playSound("random.click", 1.0F, 0.8F);
            } else {
                playSound("random.click", 1.0F, 0.6F);
            }

            this.extruder.setMode(!this.extruder.block);
        }

    }
}
