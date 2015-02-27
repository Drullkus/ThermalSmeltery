package com.drullkus.thermalsmeltery.common.blocks;

import cofh.core.network.PacketCoFHBase;
import cofh.core.render.IconRegistry;
import cofh.core.util.fluid.FluidTankAdv;
import com.drullkus.thermalsmeltery.ThermalSmeltery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import thermalexpansion.block.machine.TileMachineBase;
import thermalexpansion.core.TEProps;

public abstract class TileSmelteryBase extends TileMachineBase implements IFluidHandler
{
    protected static final SideConfig[] defaultSideConfigSmeltery = new SideConfig[BlockSmeltery.Types.values().length];
    protected static final EnergyConfig[] defaultEnergyConfigSmeltery = new EnergyConfig[BlockSmeltery.Types.values().length];
    protected static final String[] soundsSmeltery = new String[BlockSmeltery.Types.values().length];
    protected static final boolean[] enableSoundSmeltery = new boolean[]{false, false};
    protected static final int[] lightValueSmeltery = new int[]{7, 7};
    public static boolean[] enableSecurity = new boolean[]{true, true};
    FluidTankAdv tank = new FluidTankAdv(10000);
    FluidStack renderFluid;
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

    protected void transferProducts() {
        if(this.augmentAutoTransfer) {
            if(this.inventory[1] != null) {
                for(int side = this.outputTracker + 1; side <= this.outputTracker + 6; ++side) {
                    int var1 = side % 6;
                    if(this.sideCache[var1] == 2 && this.transferItem(1, 4, var1)) {
                        this.outputTracker = var1;
                        break;
                    }
                }
            }
        }
    }

    public void readFromNBT(NBTTagCompound var1) {
        super.readFromNBT(var1);
        this.outputTracker = var1.getInteger("Tracker");
        this.tank.readFromNBT(var1);
        if(this.tank.getFluid() != null)
        {
            this.renderFluid = this.tank.getFluid();
        }
    }

    public void writeToNBT(NBTTagCompound var1) {
        super.writeToNBT(var1);
        var1.setInteger("Tracker", this.outputTracker);
        this.tank.writeToNBT(var1);
    }

    public PacketCoFHBase getPacket() {
        PacketCoFHBase var1 = super.getPacket();
        var1.addFluidStack(this.renderFluid);
        return var1;
    }

    public PacketCoFHBase getGuiPacket() {
        PacketCoFHBase var1 = super.getGuiPacket();
        if(this.tank.getFluid() == null) {
            var1.addFluidStack(this.renderFluid);
        } else {
            var1.addFluidStack(this.tank.getFluid());
        }

        return var1;
    }

    public PacketCoFHBase getFluidPacket() {
        PacketCoFHBase var1 = super.getFluidPacket();
        var1.addFluidStack(this.renderFluid);
        return var1;
    }

    protected void handleGuiPacket(PacketCoFHBase var1) {
        super.handleGuiPacket(var1);
        this.tank.setFluid(var1.getFluidStack());
    }

    protected void handleFluidPacket(PacketCoFHBase var1) {
        super.handleFluidPacket(var1);
        this.renderFluid = var1.getFluidStack();
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public void handleTilePacket(PacketCoFHBase var1, boolean var2) {
        super.handleTilePacket(var1, var2);
        if(!var2) {
            this.renderFluid = var1.getFluidStack();
        } else {
            var1.getFluidStack();
        }
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        return from != ForgeDirection.UNKNOWN && this.sideCache[from.ordinal()] == 1?this.tank.fill(resource, doFill):0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return from != ForgeDirection.UNKNOWN && this.sideCache[from.ordinal()] == 2?(resource != null && resource.isFluidEqual(this.tank.getFluid())?this.tank.drain(resource.amount, doDrain):null):null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return from != ForgeDirection.UNKNOWN && this.sideCache[from.ordinal()] != 2?null:this.tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return from == ForgeDirection.UNKNOWN || this.sideCache[from.ordinal()] == 1;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return from == ForgeDirection.UNKNOWN || this.sideCache[from.ordinal()] == 2;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[]{this.tank.getInfo()};
    }

    @Override
    public String getName()
    {
        return "tile.thermalsmeltery.machine." + BlockSmeltery.NAMES[this.getType()] + ".name";
    }

    @Override
    public IIcon getTexture(int var1, int var2)
    {
        return var2 == 0?(var1 == 0? IconRegistry.getIcon("MachineBottom"):(var1 == 1?IconRegistry.getIcon("MachineTop"):(var1 != this.facing?IconRegistry.getIcon("MachineSide"):(this.isActive?IconRegistry.getIcon("MachineActive", this.getType()):IconRegistry.getIcon("MachineFace", this.getType()))))):(var1 < 6?IconRegistry.getIcon(TEProps.textureSelection, this.sideConfig.sideTex[this.sideCache[var1]]):IconRegistry.getIcon("MachineSide"));
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
