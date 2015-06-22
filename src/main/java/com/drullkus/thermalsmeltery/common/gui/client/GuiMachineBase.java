package com.drullkus.thermalsmeltery.common.gui.client;

import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.*;
import cofh.lib.gui.container.IAugmentableContainer;
import cofh.lib.gui.element.TabBase;
import cofh.lib.util.helpers.StringHelper;
import com.drullkus.thermalsmeltery.common.blocks.TileMachineBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.UUID;

public class GuiMachineBase extends GuiBaseAdv
{
    protected TileMachineBase myTile;
    protected UUID playerUUID;
    public String myInfo = "";
    public String myTutorial = StringHelper.tutorialTabAugment();
    protected TabBase redstoneTab;
    protected TabBase configTab;

    public GuiMachineBase(Container var1, TileEntity var2, EntityPlayer var3, ResourceLocation var4)
    {
        super(var1, var4);
        this.myTile = (TileMachineBase)var2;
        this.name = this.myTile.getInventoryName();
        this.playerUUID = var3.getGameProfile().getId();
        if (this.myTile.enableSecurity() && this.myTile.isSecured())
        {
            this.myTutorial = this.myTutorial + "\n\n" + StringHelper.tutorialTabSecurity();
        }

        if (this.myTile.augmentRedstoneControl)
        {
            this.myTutorial = this.myTutorial + "\n\n" + StringHelper.tutorialTabRedstone();
        }

        if (this.myTile.augmentReconfigSides)
        {
            this.myTutorial = this.myTutorial + "\n\n" + StringHelper.tutorialTabConfiguration();
        }

        if (this.myTile.getMaxEnergyStored(ForgeDirection.UNKNOWN) > 0)
        {
            this.myTutorial = this.myTutorial + "\n\n" + StringHelper.tutorialTabFluxRequired();
        }

    }

    protected void generateInfo(String var1, int var2)
    {
        this.myInfo = StringHelper.localize(var1 + "." + 0);

        for (int var3 = 1; var3 < var2; ++var3)
        {
            this.myInfo = this.myInfo + "\n\n" + StringHelper.localize(var1 + "." + var3);
        }

    }

    public void initGui()
    {
        super.initGui();
        this.addTab(new TabAugment(this, (IAugmentableContainer)this.inventorySlots));
        if (this.myTile.enableSecurity() && this.myTile.isSecured())
        {
            this.addTab(new TabSecurity(this, this.myTile, this.playerUUID));
        }

        this.redstoneTab = this.addTab(new TabRedstone(this, this.myTile));
        this.configTab = this.addTab(new TabConfiguration(this, this.myTile));
        if (this.myTile.getMaxEnergyStored(ForgeDirection.UNKNOWN) > 0)
        {
            this.addTab(new TabEnergy(this, this.myTile, false));
        }

        if (!this.myInfo.isEmpty())
        {
            this.addTab(new TabInfo(this, this.myInfo));
        }

        this.addTab(new TabTutorial(this, this.myTutorial));
    }

    public void updateScreen()
    {
        super.updateScreen();
        if (!this.myTile.canAccess())
        {
            this.mc.thePlayer.closeScreen();
        }

    }

    protected void updateElementInformation()
    {
        super.updateElementInformation();
        this.redstoneTab.setVisible(this.myTile.augmentRedstoneControl);
        this.configTab.setVisible(this.myTile.augmentReconfigSides);
    }
}
