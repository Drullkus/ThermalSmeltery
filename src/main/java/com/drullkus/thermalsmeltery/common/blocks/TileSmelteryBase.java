package com.drullkus.thermalsmeltery.common.blocks;

import cofh.core.render.IconRegistry;
import com.drullkus.thermalsmeltery.ThermalSmeltery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import thermalexpansion.block.machine.TileMachineBase;
import thermalexpansion.core.TEProps;

public abstract class TileSmelteryBase extends TileMachineBase
{
    public static final SideConfig[] defaultSideConfigSmeltery = new SideConfig[BlockSmeltery.Types.values().length];
    public static final EnergyConfig[] defaultEnergyConfigSmeltery = new EnergyConfig[BlockSmeltery.Types.values().length];
    public static final String[] soundsSmeltery = new String[BlockSmeltery.Types.values().length];
    protected static final boolean[] enableSoundSmeltery = new boolean[]{false, false};
    protected static final int[] lightValueSmeltery = new int[]{7, 7};
    public static boolean[] enableSecurity = new boolean[]{true, true};
    int outputTracker;

    public TileSmelteryBase()
    {
        this.sideConfig = defaultSideConfigSmeltery[this.getType()];
        this.energyConfig = defaultEnergyConfigSmeltery[this.getType()].copy();
        this.setDefaultSides();
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        if(this.hasGui()) {
            player.openGui(ThermalSmeltery.instance, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void transferProducts() {
        if(this.augmentAutoTransfer)
        {
            out: for (int slot = this.getMaxInputSlot() + 1; slot<inventory.length-1;slot++)
            {
                if (this.inventory[slot] != null)
                {
                    for (int side = this.outputTracker + 1; side <= this.outputTracker + 6; ++side)
                    {
                        int pushSide = side % 6;
                        if (isOutput(pushSide) && this.transferItem(slot, 4, pushSide))
                        {
                            this.outputTracker = pushSide;
                            break out;
                        }
                    }
                }
            }
        }
    }

    public boolean isOutput(int side)
    {
        return this.sideCache[side] == 2;
    }

    @Override
    public void readFromNBT(NBTTagCompound var1) {
        super.readFromNBT(var1);
        this.outputTracker = var1.getInteger("Tracker");
    }

    @Override
    public void writeToNBT(NBTTagCompound var1) {
        super.writeToNBT(var1);
        var1.setInteger("Tracker", this.outputTracker);
    }



    @Override
    public String getName()
    {
        return "tile.thermalsmeltery.machine." + BlockSmeltery.NAMES[this.getType()] + ".name";
    }

    @Override
    public IIcon getTexture(int face, int pass)
    {
        return pass == 0?(face == 0? IconRegistry.getIcon("SmelteryBottom"):(face == 1?IconRegistry.getIcon("SmelteryTop"):(face != this.facing?IconRegistry.getIcon("SmelterySide"):(this.isActive?IconRegistry.getIcon("SmelteryActive", this.getType()):IconRegistry.getIcon("SmelteryFace", this.getType()))))):(face < 6?IconRegistry.getIcon(getFaceString(face) + TEProps.textureSelection, this.sideConfig.sideTex[this.sideCache[face]]):IconRegistry.getIcon("SmelterySide"));
    }

    private String getFaceString(int face)
    {
        if (face == 0)return "bottom";
        if (face == 1)return "top";
        if (face == this.facing) return "";
        return "side";
    }

    @Override
    public boolean enableSecurity()
    {
        return enableSecurity[getType()];
    }

    @Override
    public int getLightValue()
    {
        return isActive? lightValueSmeltery[getType()]:0;
    }

    @Override
    public String getSoundName()
    {
        return soundsSmeltery[getType()];
    }
}
