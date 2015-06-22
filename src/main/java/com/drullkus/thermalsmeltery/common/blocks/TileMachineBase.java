package com.drullkus.thermalsmeltery.common.blocks;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cofh.api.item.IAugmentItem;
import cofh.api.tileentity.*;
import cofh.asm.relauncher.CoFHSide;
import cofh.asm.relauncher.Strippable;
import cofh.core.block.TileCoFHBase;
import cofh.core.network.*;
import cofh.core.render.IconRegistry;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.audio.ISoundSource;
import cofh.lib.audio.SoundTile;
import cofh.lib.util.TimeTracker;
import cofh.lib.util.helpers.*;
import com.drullkus.thermalsmeltery.ThermalSmeltery;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

@Strippable(
        value = {"cofh.api.audio.ISoundSource"},
        side = CoFHSide.SERVER
)
public abstract class TileMachineBase extends TileCoFHBase implements ITileInfoPacketHandler, ITilePacketHandler, IPortableData, ISidedInventory,
                                                                      ISecurable, IRedstoneControl, ISoundSource, IEnergyReceiver, IReconfigurableFacing,
                                                                      IReconfigurableSides, ISidedTexture, IAugmentable, IEnergyInfo
{
    protected static final int RATE = 500;
    protected static final int[] AUGMENT_COUNT = new int[]{3, 4, 5, 6};
    protected static final int[] ENERGY_CAPACITY = new int[]{2, 3, 4, 5};
    protected static final int[] ENERGY_TRANSFER = new int[]{3, 6, 12, 24};
    public static final SideConfig[] defaultSideConfigSmeltery = new SideConfig[2];
    public static final EnergyConfig[] defaultEnergyConfigSmeltery = new EnergyConfig[2];
    public static final String[] soundsSmeltery = new String[2];
    protected static final boolean[] enableSoundSmeltery = new boolean[]{false, false};
    protected static final int[] lightValueSmeltery = new int[]{7, 7};
    public static boolean[] enableSecurity = new boolean[]{true, true};
    int outputTracker;
    protected String tileName = "";
    protected String owner = "[None]";
    protected AccessMode access;
    protected boolean canAccess;
    public ItemStack[] inventory;
    public boolean isActive;
    protected boolean isPowered;
    protected boolean wasPowered;
    protected ControlMode rsMode;
    protected EnergyStorage energyStorage = new EnergyStorage(0);
    protected byte facing = 3;
    public byte[] sideCache = new byte[]{(byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0};
    protected SideConfig sideConfig;
    protected boolean[] augmentStatus = new boolean[3];
    protected ItemStack[] augments = new ItemStack[3];
    public boolean augmentAutoTransfer;
    public boolean augmentReconfigSides;
    public boolean augmentRedstoneControl;
    int processMax;
    int processRem;
    boolean wasActive;
    protected EnergyConfig energyConfig;
    protected TimeTracker tracker = new TimeTracker();
    byte level = 0;
    int processMod = 1;
    int energyMod = 1;
    int secondaryChance = 100;

    public TileMachineBase()
    {
        this.sideConfig = getDefaultSideConfig();
        this.energyConfig = getDefaultEnergyConfig().copy();
        this.setDefaultSides();
        this.rsMode = ControlMode.DISABLED;
        this.access = AccessMode.PUBLIC;
        this.canAccess = true;
        this.inventory = new ItemStack[getInventorySize()];
    }

    @Override
    public boolean openGui(EntityPlayer player)
    {
        if (this.canPlayerAccess( player.getCommandSenderName() ))
        {
            player.openGui(ThermalSmeltery.instance, 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        }
        else
        {
            if (ServerHelper.isServerWorld(this.worldObj))
            {
                player.addChatMessage(new ChatComponentText(StringHelper.localize("chat.cofh.secure1") + " " + this.owner + "! " + StringHelper.localize("chat.cofh.secure2")));
            }
        }

        return true;
    }

    protected int getInventorySize()
    {
        return 0;
    }

    protected SideConfig getDefaultSideConfig()
    {
        return defaultSideConfigSmeltery[getType()];
    }

    protected EnergyConfig getDefaultEnergyConfig()
    {
        return defaultEnergyConfigSmeltery[getType()];
    }

    protected void transferProducts()
    {
        if (this.augmentAutoTransfer)
        {
            out:
            for (int slot = this.getMaxInputSlot() + 1; slot < inventory.length - 1; slot++)
            {
                if (this.inventory[slot] != null)
                {
                    for (int side = this.outputTracker + 1; side <= this.outputTracker + 6; ++side)
                    {
                        int pushSide = side % 6;
                        if (isOutputForSlot(pushSide, slot) && this.transferItem(slot, 4, pushSide))
                        {
                            this.outputTracker = pushSide;
                            break out;
                        }
                    }
                }
            }
        }
    }

    public boolean isOutputForSlot(int side, int slot)
    {
        if (!isOutput(side)) return false;
        for (int i : sideConfig.slotGroups[this.sideCache[side]])
        {
            if (i == slot) return true;
        }
        return false;
    }

    public boolean isOutput(int side)
    {
        return this.sideConfig.allowExtraction[this.sideCache[side]];
    }

    public void readAugmentsFromNBT(NBTTagCompound tag)
    {
        this.level = tag.getByte("Level");
        this.energyConfig.setParams(this.energyConfig.minPower, this.energyConfig.maxPower, this.energyConfig.maxEnergy * ENERGY_CAPACITY[this.level] / 2);
        this.energyStorage.setCapacity(this.energyConfig.maxEnergy);
        this.energyStorage.setMaxTransfer(this.energyConfig.maxPower * ENERGY_TRANSFER[this.level]);
        this.augments = new ItemStack[AUGMENT_COUNT[this.level]];
        this.augmentStatus = new boolean[this.augments.length];
        this.readInventoryFromNBT(tag, augments, "Augments");
    }

    public void readInventoryFromNBT(NBTTagCompound tag, ItemStack[] inventory, String tagName)
    {
        NBTTagList tagList = tag.getTagList(tagName, 10);

        for (int i = 0; i < tagList.tagCount(); ++i)
        {
            NBTTagCompound slotTag = tagList.getCompoundTagAt(i);
            int slot = slotTag.getInteger("Slot");
            if (slot >= 0 && slot < inventory.length)
            {
                inventory[slot] = ItemStack.loadItemStackFromNBT(slotTag);
            }
        }
    }

    public void writeAugmentsToNBT(NBTTagCompound tag)
    {
        tag.setByte("Level", this.level);
        this.writeInventoryToNBT(tag, augments, "Augments");
    }

    public void writeInventoryToNBT(NBTTagCompound tag, ItemStack[] inventory, String tagName)
    {
        if (inventory.length > 0)
        {
            NBTTagList tagList = new NBTTagList();

            for (int slot = 0; slot < inventory.length; ++slot)
            {
                if (inventory[slot] != null)
                {
                    NBTTagCompound slotTag = new NBTTagCompound();
                    slotTag.setInteger("Slot", slot);
                    inventory[slot].writeToNBT(slotTag);
                    tagList.appendTag(slotTag);
                }
            }

            tag.setTag(tagName, tagList);
        }
    }

    protected abstract boolean hasRoomForOutput();

    protected boolean canFit(ItemStack stack, int slot)
    {
        return stack == null || inventory[slot] == null || stack.isItemEqual(inventory[slot]) && stack.stackSize + inventory[slot].stackSize <= stack.getMaxStackSize();
    }

    @Override
    public void updateEntity()
    {
        if (!ServerHelper.isClientWorld(this.worldObj))
        {
            boolean var1 = this.isActive;
            int var2;
            if (this.isActive)
            {
                if (this.processRem > 0)
                {
                    var2 = this.calcEnergy();
                    this.energyStorage.modifyEnergyStored(-var2 * this.energyMod);
                    this.processRem -= var2 * this.processMod;
                }

                if (this.canFinish())
                {
                    this.processFinish();
                    this.transferProducts();
                    this.energyStorage.modifyEnergyStored(-this.processRem * this.energyMod / this.processMod);
                    if (this.redstoneControlOrDisable() && this.canStart())
                    {
                        this.processStart();
                    } else
                    {
                        this.isActive = false;
                        this.wasActive = true;
                        this.tracker.markTime(this.worldObj);
                    }
                }
            } else if (this.redstoneControlOrDisable())
            {
                if (this.timeCheck())
                {
                    this.transferProducts();
                }

                if (this.timeCheckEighth() && this.canStart())
                {
                    this.processStart();
                    var2 = this.calcEnergy();
                    this.energyStorage.modifyEnergyStored(-var2 * this.energyMod);
                    this.processRem -= var2 * this.processMod;
                    this.isActive = true;
                }
            }

            this.updateIfChanged(var1);
            this.chargeEnergy();
        }
    }

    protected int calcEnergy()
    {
        return !this.isActive ? 0 : (this.energyStorage.getEnergyStored() > this.energyConfig.maxPowerLevel ? this.energyConfig.maxPower : (this.energyStorage.getEnergyStored() < this.energyConfig.minPowerLevel ? this.energyConfig.minPower : this.energyStorage.getEnergyStored() / this.energyConfig.energyRamp));
    }

    protected int getMaxInputSlot()
    {
        return 0;
    }

    protected boolean canStart()
    {
        return hasValidInput() && hasRoomForOutput();
    }

    protected boolean canFinish()
    {
        return this.processRem <= 0 && this.hasValidInput();
    }

    protected boolean hasValidInput()
    {
        return true;
    }

    protected void processStart()
    {
    }

    protected void processFinish()
    {
    }

    protected void updateIfChanged(boolean var1)
    {
        if (var1 != this.isActive && this.isActive)
        {
            this.sendUpdatePacket(Side.CLIENT);
        } else if (this.tracker.hasDelayPassed(this.worldObj, 200) && this.wasActive)
        {
            this.wasActive = false;
            this.sendUpdatePacket(Side.CLIENT);
        }

    }

    public int getScaledProgress(int var1)
    {
        return this.isActive && this.processMax > 0 && this.processRem > 0 ? var1 * (this.processMax - this.processRem) / this.processMax : 0;
    }

    public int getScaledSpeed(int var1)
    {
        if (!this.isActive)
        {
            return 0;
        } else
        {
            double var2 = (double)(this.energyStorage.getEnergyStored() / this.energyConfig.energyRamp);
            var2 = MathHelper.clip(var2, (double)this.energyConfig.minPower, (double)this.energyConfig.maxPower);
            return MathHelper.round((double)var1 * var2 / (double)this.energyConfig.maxPower);
        }
    }

    @Override
    public String getName()
    {
        return "tile.thermalsmeltery.machine." + BlockMachine.NAMES[this.getType()] + ".name";
    }


    private String getFaceString(int face)
    {
        switch (face)
        {
            case 0:
                return "bottom";
            case 1:
                return "top";
        }
        return "side";
    }

    public boolean enableSecurity()
    {
        return enableSecurity[getType()];
    }

    @Override
    public int getLightValue()
    {
        return isActive ? lightValueSmeltery[getType()] : 0;
    }

    public boolean setInvName(String var1)
    {
        if (var1.isEmpty())
        {
            return false;
        } else
        {
            this.tileName = var1;
            return true;
        }
    }

    @Override
    public int getInvSlotCount()
    {
        return inventory.length;
    }

    @Override
    public void sendGuiNetworkData(Container container, ICrafting player)
    {
        if (player instanceof EntityPlayer)
        {
            PacketCoFHBase packet = this.getGuiPacket();
            if (packet != null)
            {
                PacketHandler.sendTo(packet, (EntityPlayer)player);
            }
            player.sendProgressBarUpdate(container, 0, this.canPlayerAccess( ((EntityPlayer) player).getCommandSenderName()) ? 1 : 0);
        }
    }

    //TODO @Hilburn
    private boolean canPlayerAccess(String commandSenderName) {
        return false;
    }

    public PacketCoFHBase getGuiPacket()
    {
        PacketTileInfo packet = PacketTileInfo.newPacket(this);
        packet.addByte(1);
        packet.addBool(this.isActive);
        packet.addInt(this.energyStorage.getMaxEnergyStored());
        packet.addInt(this.energyStorage.getEnergyStored());
        packet.addBool(this.augmentReconfigSides);
        packet.addBool(this.augmentRedstoneControl);
        packet.addInt(this.processMax);
        packet.addInt(this.processRem);
        packet.addInt(this.processMod);
        packet.addInt(this.energyMod);
        return packet;
    }

    public PacketCoFHBase getFluidPacket()
    {
        PacketTileInfo packet = PacketTileInfo.newPacket(this);
        packet.addByte(2);
        return packet;
    }

    public PacketCoFHBase getModePacket()
    {
        PacketTileInfo packet = PacketTileInfo.newPacket(this);
        packet.addByte(3);
        return packet;
    }

    protected void handleGuiPacket(PacketCoFHBase packet)
    {
        this.isActive = packet.getBool();
        this.energyStorage.setCapacity(packet.getInt());
        this.energyStorage.setEnergyStored(packet.getInt());
        boolean augSides = this.augmentReconfigSides;
        boolean augRS = this.augmentRedstoneControl;
        this.augmentReconfigSides = packet.getBool();
        this.augmentRedstoneControl = packet.getBool();
        if (this.augmentReconfigSides != augSides || this.augmentRedstoneControl != augRS)
        {
            this.onInstalled();
            this.sendUpdatePacket(Side.SERVER);
        }
        this.processMax = packet.getInt();
        this.processRem = packet.getInt();
        this.processMod = packet.getInt();
        this.energyMod = packet.getInt();
    }

    protected void handleFluidPacket(PacketCoFHBase packet)
    {
    }

    protected void handleModePacket(PacketCoFHBase packet)
    {
    }

    public void sendFluidPacket()
    {
        PacketHandler.sendToDimension(this.getFluidPacket(), this.worldObj.provider.dimensionId);
    }

    public void sendModePacket()
    {
        if (ServerHelper.isClientWorld(this.worldObj))
        {
            PacketHandler.sendToServer(this.getModePacket());
        }
    }


    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        if (tag.hasKey("Name"))
        {
            this.tileName = tag.getString("Name");
        }
        this.access = AccessMode.values()[tag.getByte("Access")];
        this.owner = tag.getString("Owner");
        if (!this.enableSecurity())
        {
            this.access = AccessMode.PUBLIC;
        }

        this.readInventoryFromNBT(tag, inventory, "Inventory");
        this.outputTracker = tag.getInteger("Tracker");
        this.isActive = tag.getBoolean("Active");
        NBTTagCompound rsTag = tag.getCompoundTag("RS");
        this.isPowered = rsTag.getBoolean("Power");
        this.rsMode = ControlMode.values()[rsTag.getByte("Mode")];
        this.readAugmentsFromNBT(tag);
        this.installAugments();
        this.energyStorage.readFromNBT(tag);
        this.facing = MachineHelper.getFacingFromNBT(tag);
        this.sideCache = MachineHelper.getSideCacheFromNBT(tag, this.getDefaultSides());

        for (int side = 0; side < 6; ++side)
        {
            if (this.sideCache[side] >= this.getNumConfig(side))
            {
                this.sideCache[side] = 0;
            }
        }
        this.processMax = tag.getInteger("ProcMax");
        this.processRem = tag.getInteger("ProcRem");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        if (!this.tileName.isEmpty())
        {
            tag.setString("Name", this.tileName);
        }
        tag.setByte("Access", (byte)this.access.ordinal());
        tag.setString("Owner", this.owner);
        this.writeInventoryToNBT(tag, inventory, "Inventory");
        tag.setInteger("Tracker", this.outputTracker);
        tag.setBoolean("Active", this.isActive);
        NBTTagCompound rsTag = new NBTTagCompound();
        rsTag.setBoolean("Power", this.isPowered);
        rsTag.setByte("Mode", (byte)this.rsMode.ordinal());
        tag.setTag("RS", rsTag);
        this.energyStorage.writeToNBT(tag);
        tag.setByte("Facing", this.facing);
        tag.setByteArray("SideCache", this.sideCache);
        writeAugmentsToNBT(tag);
        tag.setInteger("ProcMax", this.processMax);
        tag.setInteger("ProcRem", this.processRem);
    }


    @Override
    public PacketCoFHBase getPacket()
    {
        PacketCoFHBase packet = super.getPacket();
        packet.addString(this.tileName);
        packet.addByte((byte)this.access.ordinal());
        packet.addString(this.owner);
        packet.addBool(this.isPowered);
        packet.addByte(this.rsMode.ordinal());
        packet.addBool(this.isActive);
        packet.addInt(this.energyStorage.getEnergyStored());
        packet.addByteArray(this.sideCache);
        packet.addByte(this.facing);
        packet.addBool(this.augmentReconfigSides);
        packet.addBool(this.augmentRedstoneControl);
        packet.addByte(this.level);
        return packet;
    }

    @Override
    public void handleTilePacket(PacketCoFHBase packet, boolean isServer)
    {
        if (ServerHelper.isClientWorld(this.worldObj))
        {
            this.tileName = packet.getString();
        } else
        {
            packet.getString();
        }
        this.access = AccessMode.values()[packet.getByte()];
        if (!isServer)
        {
            this.owner = packet.getString();
        } else
        {
            packet.getString();
        }
        this.isPowered = packet.getBool();
        this.rsMode = ControlMode.values()[packet.getByte()];
        if (!isServer)
        {
            boolean active = this.isActive;
            this.isActive = packet.getBool();
            if (this.isActive && !active && this.getSoundName() != null && !this.getSoundName().isEmpty())
            {
                SoundHelper.playSound(this.getSound());
            }
        } else
        {
            packet.getBool();
        }
        int energy = packet.getInt();
        if (!isServer)
        {
            this.energyStorage.setEnergyStored(energy);
        }
        packet.getByteArray(this.sideCache);

        for (int var3 = 0; var3 < 6; ++var3)
        {
            if (this.sideCache[var3] >= this.getNumConfig(var3))
            {
                this.sideCache[var3] = 0;
            }
        }

        if (!isServer)
        {
            this.facing = packet.getByte();
            this.augmentReconfigSides = packet.getBool();
            this.augmentRedstoneControl = packet.getBool();
            byte oldLevel = this.level;
            this.level = packet.getByte();
            if (oldLevel != this.level)
            {
                this.augments = new ItemStack[AUGMENT_COUNT[this.level]];
                this.augmentStatus = new boolean[this.augments.length];
                this.energyConfig.setParams(this.energyConfig.minPower, this.energyConfig.maxPower, this.energyConfig.maxEnergy * ENERGY_CAPACITY[this.level] / 2);
                this.energyStorage.setCapacity(this.energyConfig.maxEnergy);
            }
        } else
        {
            packet.getByte();
            packet.getBool();
            packet.getBool();
            packet.getByte();
        }
    }

    @Override
    public void handleTileInfoPacket(PacketCoFHBase packet, boolean isServer, EntityPlayer player)
    {
        switch (packet.getByte())
        {
            case 1:
                this.handleGuiPacket(packet);
                return;
            case 2:
                this.handleFluidPacket(packet);
                return;
            case 3:
                this.handleModePacket(packet);
                return;
            default:
        }
    }

    @Override
    public String getDataType()
    {
        return this.getName();
    }

    @Override
    public int getInfoEnergyPerTick()
    {
        return this.calcEnergy() * this.energyMod;
    }

    @Override
    public int getInfoMaxEnergyPerTick()
    {
        return this.energyConfig.maxPower * this.energyMod;
    }

    @Override
    public int getInfoEnergyStored()
    {
        return this.energyStorage.getEnergyStored();
    }

    @Override
    public int getInfoMaxEnergyStored()
    {
        return this.energyStorage.getMaxEnergyStored();
    }

    @Override
    public void readPortableData(EntityPlayer player, NBTTagCompound tag)
    {
        if (this.canPlayerAccess(player.getCommandSenderName()))
        {
            if (this.augmentRedstoneControl)
            {
                this.rsMode = RedstoneControlHelper.getControlFromNBT(tag);
            }

            if (this.augmentReconfigSides)
            {
                byte var3 = MachineHelper.getFacingFromNBT(tag);
                byte[] var4 = MachineHelper.getSideCacheFromNBT(tag, this.getDefaultSides());
                this.sideCache[0] = var4[0];
                this.sideCache[1] = var4[1];
                this.sideCache[this.facing] = var4[var3];
                this.sideCache[BlockHelper.getLeftSide(this.facing)] = var4[BlockHelper.getLeftSide(var3)];
                this.sideCache[BlockHelper.getRightSide(this.facing)] = var4[BlockHelper.getRightSide(var3)];
                this.sideCache[BlockHelper.getOppositeSide(this.facing)] = var4[BlockHelper.getOppositeSide(var3)];

                for (int var5 = 0; var5 < 6; ++var5)
                {
                    if (this.sideCache[var5] >= this.getNumConfig(var5))
                    {
                        this.sideCache[var5] = 0;
                    }
                }

                this.markDirty();
                this.sendUpdatePacket(Side.CLIENT);
            }

        }
    }

    @Override
    public void writePortableData(EntityPlayer player, NBTTagCompound tag)
    {
        if (this.canPlayerAccess(player.getCommandSenderName()))
        {
            RedstoneControlHelper.setItemStackTagRS(tag, this);
            MachineHelper.setItemStackTagReconfig(tag, this);
        }
    }

    public boolean canAccess()
    {
        return this.canAccess;
    }

    public boolean isSecured()
    {
        return !this.owner.equals("[None]");
    }

    public boolean transferItem(int slot, int amount, int side)
    {
        if (this.inventory[slot] != null && slot <= this.inventory.length)
        {
            ItemStack var4 = this.inventory[slot].copy();
            amount = Math.min(amount, var4.stackSize);
            var4.stackSize = amount;
            TileEntity tile = BlockHelper.getAdjacentTileEntity(this, side);
            if (MachineHelper.isInventory(tile, side))
            {
                this.inventory[slot].stackSize -= amount - MachineHelper.addToInventory(tile, side, var4);
                if (this.inventory[slot].stackSize <= 0)
                {
                    this.inventory[slot] = null;
                }

                return true;
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    @Override
    public void receiveGuiNetworkData(int i, int j)
    {
        this.canAccess = j != 0;
    }

    @Override
    public int getSizeInventory()
    {
        return this.inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return this.inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        ItemStack result;
        if (this.inventory[slot] == null)
        {
            result = null;
        } else
        {
            if (this.inventory[slot].stackSize <= amount)
            {
                amount = this.inventory[slot].stackSize;
            }

            ItemStack stack = this.inventory[slot].splitStack(amount);
            if (this.inventory[slot].stackSize <= 0)
            {
                this.inventory[slot] = null;
            }

            result = stack;
        }

        if (ServerHelper.isServerWorld(this.worldObj) && slot <= this.getMaxInputSlot() && this.isActive && (this.inventory[slot] == null || !this.hasValidInput()))
        {
            this.isActive = false;
            this.wasActive = true;
            this.tracker.markTime(this.worldObj);
            this.processRem = 0;
        }
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (this.inventory[slot] == null)
        {
            return null;
        } else
        {
            ItemStack stack = this.inventory[slot];
            this.inventory[slot] = null;
            return stack;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (ServerHelper.isServerWorld(this.worldObj) && slot <= this.getMaxInputSlot() && this.isActive && this.inventory[slot] != null && (stack == null || !stack.isItemEqual(this.inventory[slot]) || !this.hasValidInput()))
        {
            this.isActive = false;
            this.wasActive = true;
            this.tracker.markTime(this.worldObj);
            this.processRem = 0;
        }

        this.inventory[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markChunkDirty();
    }

    @Override
    public String getInventoryName()
    {
        return this.tileName.isEmpty() ? this.getName() : this.tileName;
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return !this.tileName.isEmpty();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return this.isUseable(player);
    }

    @Override
    public void openInventory()
    {
    }

    @Override
    public void closeInventory()
    {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return this.sideConfig.slotGroups[this.sideCache[side]];
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return this.sideConfig.allowInsertion[this.sideCache[side]] && this.isItemValid(stack, slot, side);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        return this.sideConfig.allowExtraction[this.sideCache[side]];
    }

    public void markDirty()
    {
        if (this.isActive && !this.hasValidInput())
        {
            this.isActive = false;
            this.wasActive = true;
            this.tracker.markTime(this.worldObj);
            this.processRem = 0;
        }

        super.markDirty();
    }

    @Override
    public boolean setAccess(AccessMode access)
    {
        this.access = access;
        this.sendUpdatePacket(Side.SERVER);
        return true;
    }

    @Override
    public AccessMode getAccess()
    {
        return this.access;
    }

    @Override
    public boolean setOwnerName(String owner)
    {
        if (this.owner.equals("[None]"))
        {
            this.owner = owner;
            this.markChunkDirty();
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public String getOwnerName()
    {
        return this.owner;
    }

    @Override
    public final void setPowered(boolean var1)
    {
        this.wasPowered = this.isPowered;
        this.isPowered = var1;
        if (ServerHelper.isClientWorld(this.worldObj))
        {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }

    }

    @Override
    public final boolean isPowered()
    {
        return this.isPowered;
    }

    @Override
    public final void setControl(ControlMode var1)
    {
        this.rsMode = var1;
        if (ServerHelper.isClientWorld(this.worldObj))
        {
            //TODO: Fix this
//            PacketTEBase.sendRSConfigUpdatePacketToServer(this, this.xCoord, this.yCoord, this.zCoord);
        } else
        {
            this.sendUpdatePacket(Side.CLIENT);
        }

    }

    @Override
    public final ControlMode getControl()
    {
        return this.rsMode;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ISound getSound()
    {
        return new SoundTile(this, this.getSoundName(), 1.0F, 1.0F, true, 0, (double)this.xCoord, (double)this.yCoord, (double)this.zCoord);
    }

    public String getSoundName()
    {
        return soundsSmeltery[getType()];
    }

    @Override
    public boolean shouldPlaySound()
    {
        return !this.tileEntityInvalid && this.isActive;
    }

    @Override
    public void onNeighborBlockChange()
    {
        this.wasPowered = this.isPowered;
        this.isPowered = this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
        if (this.wasPowered != this.isPowered && this.sendRedstoneUpdates())
        {
//            PacketTEBase.sendRSPowerUpdatePacketToClients(this, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            //TODO: Fix this
            this.onRedstoneUpdate();
        }

    }

    protected boolean sendRedstoneUpdates()
    {
        return false;
    }

    public final boolean redstoneControlOrDisable()
    {
        return this.rsMode.isDisabled() || this.isPowered == this.rsMode.getState();
    }

    public void onRedstoneUpdate()
    {
    }

    public int getChargeSlot()
    {
        return this.inventory.length - 1;
    }

    public boolean hasChargeSlot()
    {
        return true;
    }

    protected void chargeEnergy()
    {
        int slot = this.getChargeSlot();
        if (this.hasChargeSlot() && EnergyHelper.isEnergyContainerItem(this.inventory[slot]))
        {
            int energy = Math.min(this.energyStorage.getMaxReceive(), this.energyStorage.getMaxEnergyStored() - this.energyStorage.getEnergyStored());
            this.energyStorage.receiveEnergy(((IEnergyContainerItem)this.inventory[slot].getItem()).extractEnergy(this.inventory[slot], energy, false), false);
            if (this.inventory[slot].stackSize <= 0)
            {
                this.inventory[slot] = null;
            }
        }

    }

    public final void setEnergyStored(int var1)
    {
        this.energyStorage.setEnergyStored(var1);
    }

    public IEnergyStorage getEnergyStorage()
    {
        return this.energyStorage;
    }

    public boolean isItemValid(ItemStack var1, int var2, int var3)
    {
        return true;
    }

    public int getScaledEnergyStored(int height)
    {
        return this.energyStorage.getEnergyStored() * height / this.energyStorage.getMaxEnergyStored();
    }

    @Override
    public int receiveEnergy(ForgeDirection direction, int amount, boolean simulate)
    {
        return this.energyStorage.receiveEnergy(amount, simulate);
    }

    @Override
    public int getEnergyStored(ForgeDirection direction)
    {
        return this.energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection direction)
    {
        return this.energyStorage.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection direction)
    {
        return this.energyStorage.getMaxEnergyStored() > 0;
    }

    @Override
    public boolean onWrench(EntityPlayer var1, int var2)
    {
        return this.rotateBlock();
    }

    public byte[] getDefaultSides()
    {
        return new byte[]{(byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0};
    }

    public void setDefaultSides()
    {
        this.sideCache = this.getDefaultSides();
    }

    public final boolean hasSide(int type)
    {
        for (int side = 0; side < 6; ++side)
        {
            if (this.sideCache[side] == type)
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public final int getFacing()
    {
        return this.facing;
    }

    @Override
    public boolean allowYAxisFacing()
    {
        return false;
    }

    @Override
    public boolean rotateBlock()
    {
        if (this.isActive)
        {
            return false;
        } else
        {
            byte[] newSideCache = new byte[6];

            for (int side = 0; side < 6; ++side)
            {
                newSideCache[side] = this.sideCache[BlockHelper.ROTATE_CLOCK_Y[side]];
            }

            this.sideCache = newSideCache.clone();
            this.facing = BlockHelper.SIDE_LEFT[this.facing];
            this.markDirty();
            this.sendUpdatePacket(Side.CLIENT);
            return true;
        }
    }

    @Override
    public boolean setFacing(int side)
    {
        if (side >= 0 && side <= 5)
        {
            if (!this.allowYAxisFacing() && side < 2)
            {
                return false;
            } else
            {
                this.sideCache[side] = 0;
                this.facing = (byte)side;
                this.markDirty();
                this.sendUpdatePacket(Side.CLIENT);
                return true;
            }
        } else
        {
            return false;
        }
    }

    @Override
    public boolean decrSide(int side)
    {
        if (side == this.facing || !this.augmentReconfigSides)
        {
            return false;
        } else
        {
            this.sideCache[side] = (byte)((this.sideCache[side] + (this.getNumConfig(side) - 1)) % this.getNumConfig(side));
            this.sendUpdatePacket(Side.SERVER);
            return true;
        }
    }

    @Override
    public boolean incrSide(int side)
    {
        if (side == this.facing || !this.augmentReconfigSides)
        {
            return false;
        } else
        {
            ++this.sideCache[side];
            this.sideCache[side] = (byte)(this.sideCache[side] % this.getNumConfig(side));
            this.sendUpdatePacket(Side.SERVER);
            return true;
        }
    }

    @Override
    public boolean setSide(int side, int type)
    {
        if (this.augmentReconfigSides && side != this.facing && this.sideCache[side] != type && type < this.getNumConfig(side))
        {
            this.sideCache[side] = (byte)type;
            this.sendUpdatePacket(Side.SERVER);
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public boolean resetSides()
    {
        boolean didReset = false;

        if (this.augmentReconfigSides)
        {
            for (int side = 0; side < 6; ++side)
            {
                if (this.sideCache[side] > 0)
                {
                    this.sideCache[side] = 0;
                    didReset = true;
                }
            }

            if (didReset)
            {
                this.sendUpdatePacket(Side.SERVER);
            }
        }

        return didReset;
    }

    public int getNumConfig(int var1)
    {
        return this.sideConfig.numGroup;
    }

    @Override
    public IIcon getTexture(int face, int pass)
    {
        return pass == 0 ? (face == 0 ? IconRegistry.getIcon("SmelteryBottom") : (face == 1 ? IconRegistry.getIcon("SmelteryTop") : (face != this.facing ? IconRegistry.getIcon("SmelterySide") : (this.isActive ? IconRegistry.getIcon("SmelteryActive", this.getType()) : IconRegistry.getIcon("SmelteryFace", this.getType()))))) : (face < 6 ? IconRegistry.getIcon(getFaceString(face) + "Config_", this.sideConfig.sideTex[this.sideCache[face]]) : IconRegistry.getIcon("SmelterySide"));
    }

    public ItemStack[] getAugmentSlots()
    {
        return this.augments;
    }

    public boolean[] getAugmentStatus()
    {
        return this.augmentStatus;
    }

    public void installAugments()
    {
        this.resetAugments();

        for (int var1 = 0; var1 < this.augments.length; ++var1)
        {
            this.augmentStatus[var1] = false;
            if (MachineHelper.isAugmentItem(this.augments[var1]))
            {
                this.augmentStatus[var1] = this.installAugment(var1);
            }
        }

        if (this.worldObj != null && ServerHelper.isServerWorld(this.worldObj))
        {
            this.onInstalled();
            this.sendUpdatePacket(Side.CLIENT);
        }

    }

    protected boolean hasAugment(String type, int level)
    {
        for (ItemStack augment : this.augments)
        {
            if (MachineHelper.isAugmentItem(augment) && ((IAugmentItem)augment.getItem()).getAugmentLevel(augment, type) == level)
            {
                return true;
            }
        }

        return false;
    }

    protected boolean hasDuplicateAugment(String type, int level, int slot)
    {
        for (int i = 0; i < this.augments.length; ++i)
        {
            if (i != slot && MachineHelper.isAugmentItem(this.augments[i]) && ((IAugmentItem)this.augments[i].getItem()).getAugmentLevel(this.augments[i], type) == level)
            {
                return true;
            }
        }

        return false;
    }

    protected boolean hasAugmentChain(String type, int level)
    {
        boolean found = true;

        for (int i = 1; i < level; ++i)
        {
            found = found && this.hasAugment(type, i);
        }

        return found;
    }

    protected boolean installAugment(int slot)
    {
        IAugmentItem item = (IAugmentItem)this.augments[slot].getItem();
        boolean install = false;
        int augmentLevel;
        if (item.getAugmentLevel(this.augments[slot], MachineHelper.MACHINE_SECONDARY) > 0)
        {
            augmentLevel = Math.min(MachineHelper.NUM_MACHINE_SECONDARY, item.getAugmentLevel(this.augments[slot], MachineHelper.MACHINE_SECONDARY));
            if (augmentLevel > this.level)
            {
                return false;
            }

            if (this.hasDuplicateAugment(MachineHelper.MACHINE_SECONDARY, augmentLevel, slot))
            {
                return false;
            }

            if (!this.hasAugmentChain(MachineHelper.MACHINE_SECONDARY, augmentLevel))
            {
                return false;
            }

            this.secondaryChance -= MachineHelper.MACHINE_SECONDARY_MOD[augmentLevel];
            install = true;
        }

        if (item.getAugmentLevel(this.augments[slot], MachineHelper.MACHINE_SPEED) > 0)
        {
            augmentLevel = Math.min(MachineHelper.NUM_MACHINE_SPEED, item.getAugmentLevel(this.augments[slot], MachineHelper.MACHINE_SPEED));
            if (augmentLevel > this.level)
            {
                return false;
            }

            if (this.hasDuplicateAugment(MachineHelper.MACHINE_SPEED, augmentLevel, slot))
            {
                return false;
            }

            if (!this.hasAugmentChain(MachineHelper.MACHINE_SPEED, augmentLevel))
            {
                return false;
            }

            this.secondaryChance += MachineHelper.MACHINE_SPEED_SECONDARY_MOD[augmentLevel];
            this.processMod = Math.max(this.processMod, MachineHelper.MACHINE_SPEED_PROCESS_MOD[augmentLevel]);
            this.energyMod = Math.max(this.energyMod, MachineHelper.MACHINE_SPEED_ENERGY_MOD[augmentLevel]);
            install = true;
        }

        if (item.getAugmentLevel(this.augments[slot], MachineHelper.ENERGY_STORAGE) > 0)
        {
            augmentLevel = Math.min(MachineHelper.NUM_ENERGY_STORAGE, item.getAugmentLevel(this.augments[slot], MachineHelper.ENERGY_STORAGE));
            if (augmentLevel > this.level)
            {
                return false;
            }

            if (this.hasDuplicateAugment(MachineHelper.ENERGY_STORAGE, augmentLevel, slot))
            {
                return false;
            }

            if (!this.hasAugmentChain(MachineHelper.ENERGY_STORAGE, augmentLevel))
            {
                return false;
            }

            this.energyStorage.setCapacity(Math.max(this.energyStorage.getMaxEnergyStored(), this.energyConfig.maxEnergy * MachineHelper.ENERGY_STORAGE_MOD[augmentLevel]));
            install = true;
        }

        if (item.getAugmentLevel(this.augments[slot], MachineHelper.GENERAL_AUTO_TRANSFER) > 0)
        {
            this.augmentAutoTransfer = true;
            install = true;
        }

        if (item.getAugmentLevel(this.augments[slot], MachineHelper.GENERAL_RECONFIG_SIDES) > 0)
        {
            this.augmentReconfigSides = true;
            install = true;
        }

        if (item.getAugmentLevel(this.augments[slot], MachineHelper.GENERAL_REDSTONE_CONTROL) > 0)
        {
            this.augmentRedstoneControl = true;
            install = true;
        }

        return install;
    }

    protected void onInstalled()
    {
        if (!this.augmentReconfigSides)
        {
            this.setDefaultSides();
            this.sideCache[this.facing] = 0;
        }

        if (!this.augmentRedstoneControl)
        {
            this.rsMode = ControlMode.DISABLED;
        }

        if (this.isActive && this.energyStorage.getMaxEnergyStored() > 0 && this.processRem * this.energyMod / this.processMod > this.energyStorage.getEnergyStored())
        {
            this.processRem = 0;
            this.isActive = false;
            this.wasActive = true;
            this.tracker.markTime(this.worldObj);
        }
    }

    protected void resetAugments()
    {
        this.processMod = 1;
        this.energyMod = 1;
        this.secondaryChance = 100;
        this.augmentAutoTransfer = false;
        this.augmentReconfigSides = false;
        this.augmentRedstoneControl = false;
    }

    public FluidTankAdv getTank()
    {
        return null;
    }

    public FluidStack getTankFluid()
    {
        return null;
    }

    public static class SideConfig
    {
        public int numGroup;
        public int[][] slotGroups;
        public boolean[] allowInsertion;
        public boolean[] allowExtraction;
        public int[] sideTex;
        public byte[] defaultSides;

        public SideConfig()
        {
        }
    }

    public static class EnergyConfig
    {
        public int minPower = 8;
        public int maxPower = 80;
        public int maxEnergy = '鱀';
        public int minPowerLevel;
        public int maxPowerLevel;
        public int energyRamp;

        public EnergyConfig()
        {
            this.minPowerLevel = this.maxEnergy / 10;
            this.maxPowerLevel = 9 * this.maxEnergy / 10;
            this.energyRamp = this.maxPowerLevel / this.maxPower;
        }

        public EnergyConfig(TileMachineBase.EnergyConfig energyConfig)
        {
            this.minPowerLevel = this.maxEnergy / 10;
            this.maxPowerLevel = 9 * this.maxEnergy / 10;
            this.energyRamp = this.maxPowerLevel / this.maxPower;
            this.minPower = energyConfig.minPower;
            this.maxPower = energyConfig.maxPower;
            this.maxEnergy = energyConfig.maxEnergy;
            this.minPowerLevel = energyConfig.minPowerLevel;
            this.maxPowerLevel = energyConfig.maxPowerLevel;
            this.energyRamp = energyConfig.energyRamp;
        }

        public TileMachineBase.EnergyConfig copy()
        {
            return new TileMachineBase.EnergyConfig(this);
        }

        public boolean setParams(int minPower, int maxPower, int maxEnergy)
        {
            this.minPower = minPower;
            this.maxPower = maxPower;
            this.maxEnergy = maxEnergy;
            this.maxPowerLevel = maxEnergy * 8 / 10;
            this.energyRamp = maxPower > 0 ? this.maxPowerLevel / maxPower : 0;
            this.minPowerLevel = minPower * this.energyRamp;
            return true;
        }

        public boolean setParamsPower(int maxPower)
        {
            return this.setParams(maxPower / 4, maxPower, maxPower * 1200);
        }

        public boolean setParamsPower(int maxPower, int energyMult)
        {
            return this.setParams(maxPower / 4, maxPower, maxPower * 1200 * energyMult);
        }

        public boolean setParamsEnergy(int maxEnergy)
        {
            return this.setParams(maxEnergy / 4800, maxEnergy / 1200, maxEnergy);
        }

        public boolean setParamsEnergy(int maxEnergy, int energyMult)
        {
            maxEnergy *= energyMult;
            return this.setParams(maxEnergy / 4800, maxEnergy / 1200, maxEnergy);
        }

        public boolean setParamsDefault(int maxPower)
        {
            this.maxPower = maxPower;
            this.minPower = maxPower / 10;
            this.maxEnergy = maxPower * 500;
            this.minPowerLevel = this.maxEnergy / 10;
            this.maxPowerLevel = 9 * this.maxEnergy / 10;
            this.energyRamp = this.maxPowerLevel / maxPower;
            return true;
        }
    }
}