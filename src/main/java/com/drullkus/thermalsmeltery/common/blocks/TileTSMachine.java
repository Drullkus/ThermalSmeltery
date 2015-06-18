package com.drullkus.thermalsmeltery.common.blocks;

import cofh.api.energy.EnergyStorage;
import cofh.lib.util.TimeTracker;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemStack;

public abstract class TileTSMachine extends TileTS {

    protected static final TileTS.SideConfig[] defaultSideConfig = new TileTS.SideConfig[TSBlocks.Types.values().length];
    protected static final TileTS.EnergyConfig[] defaultEnergyConfig = new TileTS.EnergyConfig[TSBlocks.Types.values().length];
    protected static final String[] sounds = new String[TSBlocks.Types.values().length];
    protected static final int[] lightValue = new int[]{15, 15};
    public static boolean[] enableSecurity = new boolean[]{true, true};
    protected static final int RATE = 500;
    protected static final int[] AUGMENT_COUNT = new int[]{3, 4, 5, 6};
    protected static final int[] ENERGY_CAPACITY = new int[]{2, 3, 4, 5};
    protected static final int[] ENERGY_TRANSFER = new int[]{3, 6, 12, 24};
    protected static final int[] AUTO_EJECT = new int[]{8, 16, 32, 64};
    protected static final int[] FLUID_CAPACITY = new int[]{1, 2, 4, 8};
    int processMax;
    int processRem;
    boolean wasActive;
    protected TileTS.EnergyConfig energyConfig;
    protected TimeTracker tracker = new TimeTracker();
    byte level = 0;
    int processMod = 1;
    int energyMod = 1;
    int secondaryChance = 100;

    public TileTSMachine() {
        sideConfig = defaultSideConfig[this.getType()];
        energyConfig = defaultEnergyConfig[this.getType()].copy();
        energyStorage = new EnergyStorage(this.energyConfig.maxEnergy, this.energyConfig.maxPower * ENERGY_TRANSFER[this.level]);
        setDefaultSides();
    }

    public String getName() {
        return "tile.thermalexpansion.machine." + TSBlocks.machineTypes[this.getType()] + ".name";
    }

    public int getLightValue() {
        return this.isActive ? lightValue[this.getType()] : 0;
    }

    public boolean enableSecurity() {
        return enableSecurity[this.getType()];
    }

    public void updateEntity() {
        if(!ServerHelper.isClientWorld(this.worldObj))
        {
            boolean activity = this.isActive;
            int energy;

            if(activity) {
                if(this.processRem > 0) {
                    energy = this.calcEnergy();
                    this.energyStorage.modifyEnergyStored(-energy * this.energyMod);
                    this.processRem -= energy * this.processMod;
                }

                if(this.canFinish()) {
                    this.processFinish();
                    this.transferProducts();
                    this.energyStorage.modifyEnergyStored(-this.processRem * this.energyMod / this.processMod);
                    if(this.redstoneControlOrDisable() && this.canStart()) {
                        this.processStart();
                    } else {
                        this.isActive = false;
                        this.wasActive = true;
                        this.tracker.markTime(this.worldObj);
                    }
                }
            } else if(this.redstoneControlOrDisable()) {
                if(this.timeCheck()) {
                    this.transferProducts();
                }

                if(this.timeCheckEighth() && this.canStart()) {
                    this.processStart();
                    energy = this.calcEnergy();
                    this.energyStorage.modifyEnergyStored(-energy * this.energyMod);
                    this.processRem -= energy * this.processMod;
                    this.isActive = true;
                }
            }

            this.updateIfChanged(activity);
            this.chargeEnergy();
        }
    }

    protected int calcEnergy() {
        return !this.isActive?0:(this.energyStorage.getEnergyStored() > this.energyConfig.maxPowerLevel?this.energyConfig.maxPower:(this.energyStorage.getEnergyStored() < this.energyConfig.minPowerLevel?this.energyConfig.minPower:this.energyStorage.getEnergyStored() / this.energyConfig.energyRamp));
    }

    protected boolean canFinish() {
        return this.processRem > 0 ? false : this.hasValidInput();
    }

    protected boolean hasValidInput() {
        return true;
    }

    protected void processFinish() {}

    protected void processStart() {}

    protected void transferProducts() {}

    protected boolean canStart() {
        return false;
    }

    protected void updateIfChanged(boolean var1)
    {
        if(var1 != this.isActive && !this.wasActive) {
            this.sendUpdatePacket(Side.CLIENT);
        } else if(this.wasActive && this.tracker.hasDelayPassed(this.worldObj, 100)) {
            this.wasActive = false;
            this.sendUpdatePacket(Side.CLIENT);
        }
    }

    protected void onLevelChange() {
        this.augments = new ItemStack[AUGMENT_COUNT[this.level]];
        this.augmentStatus = new boolean[this.augments.length];
        this.energyConfig.setParams(this.energyConfig.minPower, this.energyConfig.maxPower, this.energyConfig.maxEnergy * ENERGY_CAPACITY[this.level] / 2);
        this.energyStorage.setCapacity(this.energyConfig.maxEnergy);
        this.energyStorage.setMaxTransfer(this.energyConfig.maxPower * ENERGY_TRANSFER[this.level]);
    }
}
