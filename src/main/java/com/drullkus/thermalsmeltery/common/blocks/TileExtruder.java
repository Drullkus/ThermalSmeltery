package com.drullkus.thermalsmeltery.common.blocks;

import cofh.api.tileentity.ITileInfo;
import cofh.core.network.PacketCoFHBase;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.util.helpers.StringHelper;
import com.drullkus.thermalsmeltery.common.core.handler.TSmeltConfig;
import com.drullkus.thermalsmeltery.common.gui.client.GuiExtruder;
import com.drullkus.thermalsmeltery.common.gui.container.ContainerExtruder;
import com.drullkus.thermalsmeltery.common.plugins.tcon.smeltery.MachineRecipeRegistry;
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
import tconstruct.library.crafting.CastingRecipe;
import thermalexpansion.block.machine.MachineHelper;

import java.util.List;

public class TileExtruder extends TileSmelteryBase implements IFluidHandler, ITileInfo
{
    static final int TYPE = BlockSmeltery.Types.EXTRUDER.ordinal();
    FluidTankAdv tank = new FluidTankAdv(10000);
    FluidStack renderFluid;
    public boolean block;
    public boolean blockFlag;

    public static void initialize()
    {
        defaultSideConfigSmeltery[TYPE] = new SideConfig();
        defaultSideConfigSmeltery[TYPE].numGroup = 4;
        defaultSideConfigSmeltery[TYPE].slotGroups = new int[][]{new int[0], new int[0], {0}, new int[0]};
        defaultSideConfigSmeltery[TYPE].allowInsertion = new boolean[]{false, false, false, false};
        defaultSideConfigSmeltery[TYPE].allowExtraction = new boolean[]{false, false, true, true};
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
        this.inventory = new ItemStack[2];
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);
        tagCompound.setBoolean("craftBlock", block);
        this.tank.writeToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);
        block = tagCompound.getBoolean("craftBlock");
        blockFlag = this.block;
        this.tank.readFromNBT(tagCompound);
        if (this.tank.getFluid() != null)
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
        blockFlag = packet.getBool();
    }

    @Override
    public PacketCoFHBase getGuiPacket()
    {
        PacketCoFHBase packet = super.getGuiPacket();
        if (this.tank.getFluid() == null)
        {
            packet.addFluidStack(this.renderFluid);
        } else
        {
            packet.addFluidStack(this.tank.getFluid());
        }
        packet.addBool(block);
        packet.addBool(blockFlag);
        return packet;
    }

    @Override
    public PacketCoFHBase getFluidPacket()
    {
        PacketCoFHBase var1 = super.getFluidPacket();
        var1.addFluidStack(this.renderFluid);
        return var1;
    }

    @Override
    protected void handleFluidPacket(PacketCoFHBase packet)
    {
        super.handleFluidPacket(packet);
        this.renderFluid = packet.getFluidStack();
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
    public void handleTilePacket(PacketCoFHBase packet, boolean var2)
    {
        super.handleTilePacket(packet, var2);
        if (!var2)
        {
            this.renderFluid = packet.getFluidStack();
        } else
        {
            packet.getFluidStack();
        }
    }

    @Override
    public PacketCoFHBase getModePacket()
    {
        PacketCoFHBase var1 = super.getModePacket();
        var1.addBool(this.blockFlag);
        return var1;
    }

    @Override
    protected void handleModePacket(PacketCoFHBase packet)
    {
        super.handleModePacket(packet);
        this.blockFlag = packet.getBool();
        if (!this.isActive)
        {
            this.block = this.blockFlag;
        }
        this.callNeighborTileChange();
    }

    public void setMode(boolean block)
    {
        boolean var2 = this.blockFlag;
        this.blockFlag = block;
        this.sendModePacket();
        this.blockFlag = var2;
    }

    @Override
    public int getType()
    {
        return 0;
    }

    @Override
    protected int getMaxInputSlot()
    {
        return -1;
    }

    @Override
    protected boolean hasRoomForOutput()
    {
        CastingRecipe recipe = getRecipe();
        return canFit(recipe.output, 0);
    }

    @Override
    protected boolean hasValidInput()
    {
        CastingRecipe recipe = getRecipe();
        return recipe != null && this.tank.getFluidAmount() >= recipe.castingMetal.amount;
    }

    @Override
    protected void processStart()
    {
        MachineHelper.setProcessMax(this, getRecipeTime(getRecipe()));
    }

    @Override
    protected void processFinish()
    {
        CastingRecipe recipe = getRecipe();
        ItemStack output = recipe.getResult();

        if (this.inventory[0] == null)
        {
            this.inventory[0] = output;
        } else
        {
            this.inventory[0].stackSize += output.stackSize;
        }

        this.tank.drain(recipe.castingMetal, true);

        this.block = this.blockFlag;
    }

    public CastingRecipe getRecipe()
    {
        if (this.tank.getFluid() == null) return null;
        return MachineRecipeRegistry.getExtruderRecipe(this.getTank().getFluid().getFluid(), block);
    }

    private int getRecipeTime(CastingRecipe recipe)
    {
        if (recipe == null) return 0;
        return recipe.coolTime * 1000 * TSmeltConfig.StamperMultiplier;

        /**
        Controls the speed of the machine
        */
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (resource == null || !MachineRecipeRegistry.isValidFluid(resource.getFluid())) return 0;
        return from == ForgeDirection.UNKNOWN || this.sideCache[from.ordinal()] == 1 ? this.tank.fill(resource, doFill) : 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return from != ForgeDirection.UNKNOWN && this.sideCache[from.ordinal()] == 2 ? (resource != null && resource.isFluidEqual(this.tank.getFluid()) ? this.tank.drain(resource.amount, doDrain) : null) : null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return from != ForgeDirection.UNKNOWN && this.sideCache[from.ordinal()] != 2 ? null : this.tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return MachineRecipeRegistry.isValidFluid(fluid) && (from == ForgeDirection.UNKNOWN || this.sideCache[from.ordinal()] == 1);
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
        if (!b)
        {
            if (this.tank.getFluid() != null)
            {
                list.add(new ChatComponentText(StringHelper.localize("info.cofh.fluid") + ": " + StringHelper.getFluidName(this.tank.getFluid())));
                list.add(new ChatComponentText(StringHelper.localize("info.cofh.amount") + ": " + this.tank.getFluidAmount() + "/" + this.tank.getCapacity() + " mB"));
            } else
            {
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
