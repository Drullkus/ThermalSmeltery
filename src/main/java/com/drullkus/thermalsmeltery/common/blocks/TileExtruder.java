package com.drullkus.thermalsmeltery.common.blocks;

import cofh.api.tileentity.ITileInfo;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.StringHelper;
import com.drullkus.thermalsmeltery.common.gui.client.GuiExtruder;
import com.drullkus.thermalsmeltery.common.gui.client.GuiStamper;
import com.drullkus.thermalsmeltery.common.gui.container.ContainerExtruder;
import com.drullkus.thermalsmeltery.common.gui.container.ContainerStamper;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

public class TileExtruder extends TileSmelteryBase implements IFluidHandler, ITileInfo
{
    static final int TYPE = BlockSmeltery.Types.EXTRUDER.ordinal();
    FluidTankAdv tank = new FluidTankAdv(10000);
    FluidStack renderFluid;
    private boolean block;

    public static void initialize() {
        defaultSideConfigSmeltery[TYPE] = new SideConfig();
        defaultSideConfigSmeltery[TYPE].numGroup = 4;
        defaultSideConfigSmeltery[TYPE].slotGroups = new int[][]{new int[0], {0}, new int[0]};
        defaultSideConfigSmeltery[TYPE].allowInsertion = new boolean[]{false, true, false, false, false};
        defaultSideConfigSmeltery[TYPE].allowExtraction = new boolean[]{false, true, true, false, true};
        defaultSideConfigSmeltery[TYPE].sideTex = new int[]{0, 1, 2, 3};
        defaultSideConfigSmeltery[TYPE].defaultSides = new byte[]{(byte)3, (byte)1, (byte)2, (byte)2, (byte)2, (byte)2};
//        int basePower = MathHelper.clampI(ThermalExpansion.config.get("block.tweak", "Machine.Crucible.BasePower", 400), 100, 500);
//        ThermalExpansion.config.set("block.tweak", "Machine.Crucible.BasePower", var0);
        int basePower = 400;
        defaultEnergyConfigSmeltery[TYPE] = new EnergyConfig();
        defaultEnergyConfigSmeltery[TYPE].setParams(basePower / 10, basePower, Math.max(480000, basePower * 1200));
//        sounds[TYPE] = CoreUtils.getSoundName("ThermalExpansion", "blockMachineCrucible");
//        enableSound[TYPE] = CoFHCore.configClient.get("sound", "Machine.Crucible", true);
        GameRegistry.registerTileEntity(TileExtruder.class, "thermalsmeltery.Extruder");
    }

    public TileExtruder()
    {
        this.inventory = new ItemStack[3];
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);
        tagCompound.setBoolean("craftBlock",block);
        this.tank.writeToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);
        block = tagCompound.getBoolean("craftBlock");
        this.tank.readFromNBT(tagCompound);
        if(this.tank.getFluid() != null)
        {
            this.renderFluid = this.tank.getFluid();
        }
    }

    @Override
    protected void handleGuiPacket(PacketCoFHBase packet)
    {
        super.handleGuiPacket(packet);
        this.tank.setFluid(packet.getFluidStack());
        block = packet.getBool();
    }

    @Override
    public PacketCoFHBase getGuiPacket()
    {
        PacketCoFHBase packet = super.getGuiPacket();
        if(this.tank.getFluid() == null) {
            packet.addFluidStack(this.renderFluid);
        } else {
            packet.addFluidStack(this.tank.getFluid());
        }
        packet.addBool(block);
        return packet;
    }

    @Override
    public PacketCoFHBase getFluidPacket() {
        PacketCoFHBase var1 = super.getFluidPacket();
        var1.addFluidStack(this.renderFluid);
        return var1;
    }

    @Override
    protected void handleFluidPacket(PacketCoFHBase var1) {
        super.handleFluidPacket(var1);
        this.renderFluid = var1.getFluidStack();
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public PacketCoFHBase getPacket()
    {
        PacketCoFHBase packet = super.getPacket();
        packet.addFluidStack(this.renderFluid);
        return packet;
    }

    @Override
    public void handleTilePacket(PacketCoFHBase var1, boolean var2) {
        super.handleTilePacket(var1, var2);
        if(!var2) {
            this.renderFluid = var1.getFluidStack();
        } else {
            var1.getFluidStack();
        }
    }

    @Override
    public int getType()
    {
        return 0;
    }

    @Override
    protected boolean canStart() {
        return false;
    }

    @Override
    protected boolean hasValidInput() {
        return false;
    }

    @Override
    protected void processStart() {
    }

    @Override
    protected void processFinish() {
    }


    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        return from == ForgeDirection.UNKNOWN || this.sideCache[from.ordinal()] == 1?this.tank.fill(resource, doFill):0;
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
    public void getTileInfo(List<IChatComponent> list, ForgeDirection forgeDirection, EntityPlayer entityPlayer, boolean b)
    {
        if(!b) {
            if(this.tank.getFluid() != null) {
                list.add(new ChatComponentText(StringHelper.localize("info.cofh.fluid") + ": " + StringHelper.getFluidName(this.tank.getFluid())));
                list.add(new ChatComponentText(StringHelper.localize("info.cofh.amount") + ": " + this.tank.getFluidAmount() + "/" + this.tank.getCapacity() + " mB"));
            } else {
                list.add(new ChatComponentText(StringHelper.localize("info.cofh.fluid") + ": " + StringHelper.localize("info.cofh.empty")));
            }

        }
    }

    @Override
    public FluidTankAdv getTank()
    {
        return tank;
    }

    @Override
    public Object getGuiClient(InventoryPlayer inventoryPlayer)
    {
        return new GuiExtruder(inventoryPlayer, this);
    }

    @Override
    public Object getGuiServer(InventoryPlayer inventoryPlayer)
    {
        return new ContainerExtruder(inventoryPlayer, this);
    }
}
