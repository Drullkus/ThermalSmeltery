package com.drullkus.thermalsmeltery.common.blocks;

import buildcraft.api.transport.IInjectable;
import buildcraft.api.transport.IPipeTile;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cofh.api.item.IAugmentItem;
import cofh.api.tileentity.*;
import cofh.api.transport.IItemDuct;
import cofh.asm.relauncher.CoFHSide;
import cofh.asm.relauncher.Implementable;
import cofh.asm.relauncher.Strippable;
import cofh.core.CoFHProps;
import cofh.core.block.TileCoFHBase;
import cofh.core.network.*;
import cofh.core.util.fluid.FluidTankAdv;
import cofh.lib.audio.ISoundSource;
import cofh.lib.audio.SoundTile;
import cofh.lib.util.helpers.*;
import com.drullkus.thermalsmeltery.ThermalSmeltery;
import com.drullkus.thermalsmeltery.common.core.Props;
import com.drullkus.thermalsmeltery.common.network.PacketTSBase;
import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import java.util.UUID;

@Implementable({"buildcraft.api.tiles.IHasWork"})
@Strippable(
        value = {"cofh.lib.audio.ISoundSource"},
        side = CoFHSide.SERVER
)
public class TileTS extends TileCoFHBase implements
        ITileInfoPacketHandler,
        ITilePacketHandler,
        IPortableData,
        IAugmentable,
        IEnergyInfo,
        ISidedInventory,
        IReconfigurableFacing,
        IReconfigurableSides,
        ISidedTexture,
        IEnergyReceiver,
        IRedstoneControl,
        ISoundSource,
        IInventory,
        ISecurable {

    protected String tileName = "";

    protected GameProfile owner = CoFHProps.DEFAULT_OWNER;
    protected AccessMode access = AccessMode.PUBLIC;
    protected boolean canAccess = true;
    protected boolean inWorld = false;
    public ItemStack[] inventory = new ItemStack[0];

    private static boolean bcPipeExists = false;

    public boolean isActive;
    protected boolean isPowered;
    protected boolean wasPowered;
    protected ControlMode rsMode = ControlMode.DISABLED;

    protected EnergyStorage energyStorage = new EnergyStorage(0);

    protected byte facing = 3;
    public byte[] sideCache = new byte[]{(byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0};

    protected TileTS.SideConfig sideConfig;
    protected boolean[] augmentStatus = new boolean[3];
    protected ItemStack[] augments = new ItemStack[3];
    public boolean augmentAutoTransfer;
    public boolean augmentReconfigSides;
    public boolean augmentRedstoneControl;

    public boolean setInvName(String name)
    {
        if(name.isEmpty())
        {
            return false;
        }
        else
        {
            tileName = name;
            return true;
        }
    }

    @Override
    public void installAugments()
    {
        resetAugments();

        for(int c = 0; c < augments.length; ++c) {

            augmentStatus[c] = false;

            if (isAugmentItem(augments[c]))
            {
                augmentStatus[c] = installAugment(c);
            }
        }

        if(worldObj != null && ServerHelper.isServerWorld(worldObj)) {
            onInstalled();
            sendUpdatePacket(Side.CLIENT);
        }
    }

    @Override
    public ItemStack[] getAugmentSlots() {
        return this.augments;
    }

    @Override
    public boolean[] getAugmentStatus() {
        return this.augmentStatus;
    }

    @Override
    public int receiveEnergy(ForgeDirection forgeDirection, int i, boolean b) {
        return this.energyStorage.receiveEnergy(i, b);
    }

    @Override
    public int getEnergyStored(ForgeDirection forgeDirection) {
        return this.energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection forgeDirection) {
        return this.energyStorage.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection forgeDirection) {
        return this.energyStorage.getMaxEnergyStored() > 0;
    }

    @Override
    public int getInfoEnergyPerTick() {
        return 0;
    }

    @Override
    public int getInfoMaxEnergyPerTick() {
        return 0;
    }

    @Override
    public int getInfoEnergyStored() {
        return 0;
    }

    @Override
    public int getInfoMaxEnergyStored() {
        return 0;
    }

    @Override
    public int getSizeInventory() {
        return this.inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int p_70301_1_)  {
        return this.inventory[p_70301_1_];
    }

    @Override
    public ItemStack decrStackSize(int slot, int p_70298_2_)
    {
        if(this.inventory[slot] == null) {
            return null;
        } else {
            if(this.inventory[slot].stackSize <= p_70298_2_) {
                p_70298_2_ = this.inventory[slot].stackSize;
            }

            ItemStack var3 = this.inventory[slot].splitStack(p_70298_2_);
            if(this.inventory[slot].stackSize <= 0) {
                this.inventory[slot] = null;
            }

            return var3;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if(this.inventory[slot] == null)
        {
            return null;
        }
        else
        {
            ItemStack stack = this.inventory[slot];
            this.inventory[slot] = null;
            return stack;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.inventory[slot] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        if (this.inWorld)
        {
            this.markChunkDirty();
        }
    }

    @Override
    public String getInventoryName()
    {
        return this.tileName.isEmpty() ? this.getName() : this.tileName;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return !this.tileName.isEmpty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.isUseable(player);
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public String getDataType()
    {
        return getName();
    }

    @Override
    public void readPortableData(EntityPlayer entityPlayer, NBTTagCompound nbtTagCompound)
    {
        if(canPlayerAccess(entityPlayer))
        {
            if(readPortableTagInternal(entityPlayer, nbtTagCompound))
            {
                markDirty();
                sendUpdatePacket(Side.CLIENT);
            }
        }
    }

    @Override
    public void writePortableData(EntityPlayer paramEntityPlayer, NBTTagCompound paramNBTTagCompound)
    {
        if (!canPlayerAccess(paramEntityPlayer))
        {
            return;
        }
        if (writePortableTagInternal(paramEntityPlayer, paramNBTTagCompound));
    }

    public static class SideConfig {
        public int numConfig;
        public int[][] slotGroups;
        public boolean[] allowInsertionSide;
        public boolean[] allowExtractionSide;
        public boolean[] allowInsertionSlot;
        public boolean[] allowExtractionSlot;
        public int[] sideTex;
        public byte[] defaultSides;

        public SideConfig() {
        }
    }

    protected boolean readPortableTagInternal(EntityPlayer paramEntityPlayer, NBTTagCompound paramNBTTagCompound)
    {
        if (this.augmentRedstoneControl)
        {
            this.rsMode = RedstoneControlHelper.getControlFromNBT(paramNBTTagCompound);
        }

        if(this.augmentReconfigSides)
        {
            byte face = getFacingFromNBT(paramNBTTagCompound);
            byte[] faceArray = getSideCacheFromNBT(paramNBTTagCompound, this.getDefaultSides());

            this.sideCache[0] = faceArray[0];
            this.sideCache[1] = faceArray[1];
            this.sideCache[this.facing] = faceArray[face];
            this.sideCache[BlockHelper.getLeftSide(this.facing)] = faceArray[BlockHelper.getLeftSide(face)];
            this.sideCache[BlockHelper.getRightSide(this.facing)] = faceArray[BlockHelper.getRightSide(face)];
            this.sideCache[BlockHelper.getOppositeSide(this.facing)] = faceArray[BlockHelper.getOppositeSide(face)];

            for (int var5 = 0; var5 < 6; ++var5)
            {
                if (this.sideCache[var5] >= this.getNumConfig(var5))
                {
                    this.sideCache[var5] = 0;
                }
            }
        }

        return true;
    }

    protected boolean writePortableTagInternal(EntityPlayer paramEntityPlayer, NBTTagCompound paramNBTTagCompound)
    {
        RedstoneControlHelper.setItemStackTagRS(paramNBTTagCompound, this);
        setItemStackTagReconfig(paramNBTTagCompound, this);

        return true;
    }

    // TeamCoFH, if you're going to poke at me for 'copying code', at least tell me why there's a subclass...
    public static class EnergyConfig {
        public int minPower = 8;
        public int maxPower = 80;
        public int maxEnergy = 40000;
        public int minPowerLevel;
        public int maxPowerLevel;
        public int energyRamp;

        public EnergyConfig() {
            minPowerLevel = 1 * maxEnergy / 10;
            maxPowerLevel = 9 * maxEnergy / 10;
            energyRamp = maxPowerLevel / maxPower;
        }

        public EnergyConfig(TileTS.EnergyConfig var1) {
            minPowerLevel = 1 * maxEnergy / 10;
            maxPowerLevel = 9 * maxEnergy / 10;
            energyRamp = maxPowerLevel / maxPower;
            minPower = var1.minPower;
            maxPower = var1.maxPower;
            maxEnergy = var1.maxEnergy;
            minPowerLevel = var1.minPowerLevel;
            maxPowerLevel = var1.maxPowerLevel;
            energyRamp = var1.energyRamp;
        }

        public TileTS.EnergyConfig copy() {
            return new TileTS.EnergyConfig( this );
        }

        public boolean setParams(int var1, int var2, int var3) {
            minPower = var1;
            maxPower = var2;
            maxEnergy = var3;
            maxPowerLevel = var3 * 8 / 10;
            energyRamp = var2 > 0?maxPowerLevel / var2:0;
            minPowerLevel = var1 * energyRamp;
            return true;
        }

        public boolean setParamsPower(int var1) {
            return setParams(var1 / 4, var1, var1 * 1200);
        }

        public boolean setParamsPower(int var1, int var2) {
            return setParams(var1 / 4, var1, var1 * 1200 * var2);
        }

        public boolean setParamsEnergy(int var1) {
            return setParams(var1 / 4800, var1 / 1200, var1);
        }

        public boolean setParamsEnergy(int var1, int var2) {
            var1 *= var2;
            return setParams(var1 / 4800, var1 / 1200, var1);
        }

        public boolean setParamsDefault(int var1) {
            maxPower = var1;
            minPower = var1 / 10;
            maxEnergy = var1 * 500;
            minPowerLevel = 1 * maxEnergy / 10;
            maxPowerLevel = 9 * maxEnergy / 10;
            energyRamp = maxPowerLevel / var1;
            return true;
        }
    }

    @Override
    public int getFacing() {
        return this.facing;
    }

    @Override
    public boolean allowYAxisFacing() {
        return false;
    }

    @Override
    public boolean rotateBlock() {

        byte[] faceArray;
        int faceValue;

        if(this.allowYAxisFacing()) {
            faceArray = new byte[6];
            label68:
            switch(this.facing) {
                case 0:
                    faceValue = 0;

                    while(true) {
                        if(faceValue >= 6) {
                            break label68;
                        }

                        faceArray[faceValue] = this.sideCache[BlockHelper.INVERT_AROUND_X[faceValue]];
                        ++faceValue;
                    }
                case 1:
                    faceValue = 0;

                    while(true) {
                        if(faceValue >= 6) {
                            break label68;
                        }

                        faceArray[faceValue] = this.sideCache[BlockHelper.ROTATE_CLOCK_X[faceValue]];
                        ++faceValue;
                    }
                case 2:
                    faceValue = 0;

                    while(true) {
                        if(faceValue >= 6) {
                            break label68;
                        }

                        faceArray[faceValue] = this.sideCache[BlockHelper.INVERT_AROUND_Y[faceValue]];
                        ++faceValue;
                    }
                case 3:
                    faceValue = 0;

                    while(true) {
                        if(faceValue >= 6) {
                            break label68;
                        }

                        faceArray[faceValue] = this.sideCache[BlockHelper.ROTATE_CLOCK_Y[faceValue]];
                        ++faceValue;
                    }
                case 4:
                    faceValue = 0;

                    while(true) {
                        if(faceValue >= 6) {
                            break label68;
                        }

                        faceArray[faceValue] = this.sideCache[BlockHelper.INVERT_AROUND_Z[faceValue]];
                        ++faceValue;
                    }
                case 5:
                    for(faceValue = 0; faceValue < 6; ++faceValue) {
                        faceArray[faceValue] = this.sideCache[BlockHelper.ROTATE_CLOCK_Z[faceValue]];
                    }
            }

            this.sideCache = (byte[])faceArray.clone();

            ++this.facing;

            this.facing = (byte)(this.facing % 6);

            this.markDirty();
            this.sendUpdatePacket(Side.CLIENT);

            return true;

        }
        else if(this.isActive)
        {
            return false;
        }
        else
        {
            faceArray = new byte[6];

            for(faceValue = 0; faceValue < 6; ++faceValue) {
                faceArray[faceValue] = this.sideCache[BlockHelper.ROTATE_CLOCK_Y[faceValue]];
            }

            this.sideCache = (byte[])faceArray.clone();
            this.facing = BlockHelper.SIDE_LEFT[this.facing];
            this.markDirty();
            this.sendUpdatePacket(Side.CLIENT);
            return true;
        }
    }

    @Override
    public boolean setFacing(int i)
    {
        if (i >= 0 && i <= 5)
        {
            if (!this.allowYAxisFacing() && i < 2)
            {
                return false;
            }
            else
            {
                this.facing = (byte)i;

                this.markDirty();
                this.sendUpdatePacket(Side.CLIENT);

                return true;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean decrSide(int i) {

        if (this.augmentReconfigSides)
        {
            if(i == this.facing)
            {
                return false;
            }
            else
            {
                this.sideCache[i] = (byte)(this.sideCache[i] + (this.getNumConfig(i) - 1));
                this.sideCache[i] = (byte)(this.sideCache[i] % this.getNumConfig(i));

                this.sendUpdatePacket(Side.SERVER);

                return true;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean incrSide(int i)
    {
        if (this.augmentReconfigSides)
        {
            if (i == this.facing)
            {
                return false;
            }
            else
            {
                ++this.sideCache[i];

                this.sideCache[i] = (byte) (this.sideCache[i] % this.getNumConfig(i));

                this.sendUpdatePacket(Side.SERVER);

                return true;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean setSide(int face, int i1)
    {
        if (this.augmentReconfigSides)
        {
            if (face != this.facing && this.sideCache[face] != i1 && i1 < this.getNumConfig(i1))
            {
                this.sideCache[face] = (byte)i1;
                this.sendUpdatePacket(Side.SERVER);

                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean resetSides()
    {

        if (augmentReconfigSides) {
            boolean var1 = false;

            for (int c = 0; c < 6; ++c) {
                if (this.sideCache[c] > 0) {
                    this.sideCache[c] = 0;
                    var1 = true;
                }
            }

            if (var1) {
                this.sendUpdatePacket(Side.SERVER);
            }

            return var1;
        }
        else
        {
            return false;
        }
    }

    @Override
    public int getNumConfig(int i) {
        return this.sideConfig.numConfig;
    }

    @Override
    public void setControl(ControlMode controlMode)
    {
        this.rsMode = controlMode;

        if (ServerHelper.isClientWorld(this.worldObj))
        {
            PacketTSBase.sendRSConfigUpdatePacketToServer(this, this.xCoord, this.yCoord, this.zCoord);
        }
        else
        {
            this.sendUpdatePacket(Side.CLIENT);
        }
    }

    @Override
    public ControlMode getControl() {
        return this.rsMode;
    }

    @Override
    public void setPowered(boolean b)
    {
        this.wasPowered = this.isPowered;
        this.isPowered = b;

        if (ServerHelper.isClientWorld(this.worldObj))
        {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    @Override
    public boolean isPowered() {
        return this.isPowered;
    }

    @Override
    public boolean setAccess(AccessMode accessMode) {

        this.access = accessMode;
        this.sendUpdatePacket(Side.SERVER);
        return true;

    }

    @Override
    public boolean setOwnerName(String s) {
        return false;
    }

    @Override
    public boolean setOwner(GameProfile gameProfile)
    {
        if (SecurityHelper.isDefaultUUID(this.owner.getId()))
        {
            this.owner = gameProfile;

            if(!SecurityHelper.isDefaultUUID(this.owner.getId()))
            {
                if(MinecraftServer.getServer() != null)
                {
                    (new Thread("CoFH User Loader")
                    {
                        public void run()
                        {
                            TileTS.this.owner = SecurityHelper.getProfile(TileTS.this.owner.getId(), TileTS.this.owner.getName());
                        }

                    }).start();
                }

                if (this.inWorld)
                {
                    this.markChunkDirty();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public AccessMode getAccess() {
        return this.access;
    }

    @Override
    public String getOwnerName() {
        String ownerName = this.owner.getName();
        return ownerName == null ? StringHelper.localize("info.cofh.anotherplayer") : ownerName;
    }

    @Override
    public GameProfile getOwner() {
        return this.owner;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        return this.sideConfig.slotGroups[this.sideCache[p_94128_1_]];
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack item, int side) {
        return this.sideConfig.allowInsertionSide[this.sideCache[side]] && this.sideConfig.allowInsertionSlot[slot]?this.isItemValid(item, slot, side):false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack item, int side) {
        return this.sideConfig.allowExtractionSide[this.sideCache[side]] && this.sideConfig.allowExtractionSlot[slot];
    }

    @Override
    public IIcon getTexture(int i, int i1) {
        return null;
    }

    @Override
    public ISound getSound() {
        return new SoundTile(this, this.getSoundName(), 1.0F, 1.0F, true, 0, (double)this.xCoord, (double)this.yCoord, (double)this.zCoord);
    }

    @Override
    public boolean shouldPlaySound() {
        return false;
    }

    @Override
    public void handleTileInfoPacket(PacketCoFHBase packetCoFHBase, boolean b, EntityPlayer entityPlayer) {
        switch(Props.PacketID.values()[packetCoFHBase.getByte()])
        {
            case GUI:
                handleGuiPacket(packetCoFHBase);
                return;
            case FLUID:
                handleFluidPacket(packetCoFHBase);
                return;
            case MODE:
                handleModePacket(packetCoFHBase);
                return;
            default:
        }
    }

    @Override
    public void handleTilePacket(PacketCoFHBase packetCoFHBase, boolean b) {

        if (ServerHelper.isClientWorld(worldObj))
        {
            tileName = packetCoFHBase.getString();
        }
        else
        {
            packetCoFHBase.getString();
        }

        this.access = AccessMode.values()[packetCoFHBase.getByte()];
        if (!b)
        {
            this.owner = CoFHProps.DEFAULT_OWNER;
            this.setOwner(new GameProfile(packetCoFHBase.getUUID(), packetCoFHBase.getString()));
        }
        else
        {
            packetCoFHBase.getUUID();
            packetCoFHBase.getString();
        }


        this.isPowered = packetCoFHBase.getBool();
        this.rsMode = ControlMode.values()[packetCoFHBase.getByte()];

        if(!b)
        {
            boolean var3 = this.isActive;
            this.isActive = packetCoFHBase.getBool();
            if (this.isActive && !var3 && this.getSoundName() != null && !this.getSoundName().isEmpty())
            {
                SoundHelper.playSound(this.getSound());
            }
        }
        else
        {
            packetCoFHBase.getBool();
        }

        int energy = packetCoFHBase.getInt();
        if(!b) {
            this.energyStorage.setEnergyStored(energy);
        }

        packetCoFHBase.getByteArray(this.sideCache);

        for(int var3 = 0; var3 < 6; ++var3) {
            if(this.sideCache[var3] >= this.getNumConfig(var3)) {
                this.sideCache[var3] = 0;
            }
        }

        if(!b) {
            this.facing = packetCoFHBase.getByte();
        } else {
            packetCoFHBase.getByte();
        }

        if(!b) {
            this.augmentReconfigSides = packetCoFHBase.getBool();
            this.augmentRedstoneControl = packetCoFHBase.getBool();
        } else {
            packetCoFHBase.getBool();
            packetCoFHBase.getBool();
        }

        this.callNeighborTileChange();

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getType() {
        return 0;
    }

    public boolean hasGui() {
        return true;
    }

    public boolean openGui(EntityPlayer var1) {
        if (canPlayerAccess(var1))
        {
            if (hasGui())
            {
                var1.openGui(ThermalSmeltery.instance, 0, worldObj, xCoord, yCoord, zCoord);
            }

            return hasGui();
        }
        else
        {
            if (ServerHelper.isServerWorld(worldObj))
            {
                var1.addChatMessage(new ChatComponentText(StringHelper.localize("chat.cofh.secure.1") + " " + getOwnerName() + "! " + StringHelper.localize("chat.cofh.secure.2")));
            }

            return false;
        }
    }

    public void sendGuiNetworkData(Container container, ICrafting crafting)
    {
        super.sendGuiNetworkData(container, crafting);
        crafting.sendProgressBarUpdate(container, 0, canPlayerAccess((EntityPlayer) crafting) ? 1 : 0);
    }

    public PacketCoFHBase getGuiPacket()
    {
        PacketTileInfo packet = PacketTileInfo.newPacket(this);

        packet.addByte(Props.PacketID.GUI.ordinal());

        packet.addBool(this.isActive);
        packet.addInt(this.energyStorage.getMaxEnergyStored());
        packet.addInt(this.energyStorage.getEnergyStored());
        packet.addBool(this.augmentReconfigSides);
        packet.addBool(this.augmentRedstoneControl);

        return packet;
    }

    public PacketCoFHBase getFluidPacket() {
        PacketTileInfo packet = PacketTileInfo.newPacket(this);
        packet.addByte(Props.PacketID.FLUID.ordinal());
        return packet;
    }

    public PacketCoFHBase getModePacket() {
        PacketTileInfo packet = PacketTileInfo.newPacket(this);
        packet.addByte(Props.PacketID.MODE.ordinal());
        return packet;
    }

    protected void handleGuiPacket(PacketCoFHBase packet) {
        this.isActive = packet.getBool();
        this.energyStorage.setCapacity(packet.getInt());
        this.energyStorage.setEnergyStored(packet.getInt());
        boolean var2 = this.augmentReconfigSides;
        boolean var3 = this.augmentRedstoneControl;
        this.augmentReconfigSides = packet.getBool();
        this.augmentRedstoneControl = packet.getBool();
        if(this.augmentReconfigSides != var2 || this.augmentRedstoneControl != var3)
        {
            this.onInstalled();
        }
    }

    protected void handleFluidPacket(PacketCoFHBase packet) {}

    protected void handleModePacket(PacketCoFHBase packet) {
        markChunkDirty();
    }

    public void sendFluidPacket() {
        PacketHandler.sendToDimension(getFluidPacket(), worldObj.provider.dimensionId);
    }

    public void sendModePacket() {
        if(ServerHelper.isClientWorld(worldObj)) {
            PacketHandler.sendToServer(getModePacket());
        }
    }

    protected boolean installAugment(int slot)
    {

        IAugmentItem var2 = (IAugmentItem) augments[slot].getItem();

        boolean var3 = false;

        if (var2.getAugmentLevel(augments[slot], "generalAutoTransfer") > 0)
        {
            augmentAutoTransfer = true;
            var3 = true;
        }

        if (var2.getAugmentLevel(augments[slot], "generalReconfigSides") > 0)
        {
            augmentReconfigSides = true;
            var3 = true;
        }

        if (var2.getAugmentLevel(augments[slot], "generalRedstoneControl") > 0)
        {
            augmentRedstoneControl = true;
            var3 = true;
        }

        return var3;
    }

    protected void onInstalled() {
        if (!augmentReconfigSides)
        {
            setDefaultSides();
            sideCache[facing] = 0;
        }

        if(!augmentRedstoneControl)
        {
            rsMode = ControlMode.DISABLED;
        }
    }

    public static boolean isAugmentItem(ItemStack item) {
        return item != null && item.getItem() instanceof IAugmentItem;
    }

    private void resetAugments() {
        augmentAutoTransfer = false;
        augmentReconfigSides = false;
        augmentRedstoneControl = false;
    }

    public void cofh_validate() {
        inWorld = true;
    }

    public boolean canAccess() {
        return canAccess;
    }

    public boolean isSecured() {
        return !SecurityHelper.isDefaultUUID(owner.getId());
    }

    public boolean enableSecurity() {
        return true;
    }

    public boolean extractItem(int slot, int var2, int side) {
        if (slot > inventory.length)
        {
            return false;
        }
        else
        {
            BlockHelper.getAdjacentTileEntity( this, side);
            return false;
        }
    }

    public boolean transferItem(int var1, int var2, int var3)
    {
        if (inventory[var1] != null && var1 <= inventory.length)
        {
            ItemStack item = inventory[var1].copy();
            var2 = Math.min(var2, item.stackSize);
            item.stackSize = var2;
            boolean var5 = false;
            TileEntity tileEntity = BlockHelper.getAdjacentTileEntity( this, var3);
            int var7;
            if (isAccessibleInventory(tileEntity, var3))
            {
                var7 = addToInventory(tileEntity, var3, item);

                if(var7 >= var2)
                {
                    return false;
                }
                else
                {
                    inventory[var1].stackSize -= var2 - var7;

                    if (inventory[var1].stackSize <= 0)
                    {
                        inventory[var1] = null;
                    }

                    return true;
                }
            }
            else
            {
                var5 = false;
                if(isPipeTile(tileEntity))
                {
                    var7 = addToPipeTile(tileEntity, var3, item);
                    if(var7 <= 0)
                    {
                        return false;
                    }
                    else
                    {
                        inventory[var1].stackSize -= var7;

                        if (inventory[var1].stackSize <= 0)
                        {
                            inventory[var1] = null;
                        }

                        return true;
                    }
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }

    public static boolean isAccessibleInventory(TileEntity tileEntity, int var1)
    {
        return tileEntity instanceof ISidedInventory && ((ISidedInventory)tileEntity).getAccessibleSlotsFromSide(BlockHelper.SIDE_OPPOSITE[var1]).length <= 0 ? false : (tileEntity instanceof IInventory && ((IInventory)tileEntity).getSizeInventory() > 0 ? true : tileEntity instanceof IItemDuct);
    }

    public static int addToInventory(TileEntity tileEntity, int var1, ItemStack item)
    {
        if(!InventoryHelper.isInsertion(tileEntity))
        {
            return item.stackSize;
        }
        else
        {
            item = InventoryHelper.addToInsertion(tileEntity, var1, item);
            return item == null ? 0 : item.stackSize;
        }
    }

    public static boolean isPipeTile(TileEntity tileEntity) {
        return bcPipeExists && isPipeTile_do(tileEntity);
    }

    private static boolean isPipeTile_do(TileEntity tileEntity) {
        return tileEntity instanceof IPipeTile;
    }

    static {

        try {
            Class.forName("buildcraft.api.transport.IPipeTile");
            bcPipeExists = true;
        } catch (Throwable throwable) {
        }
    }

    public static int addToPipeTile(TileEntity tileEntity, int var1, ItemStack var2) {
        return bcPipeExists ? addToPipeTile_do(tileEntity, var1, var2) : 0;
    }

    private static int addToPipeTile_do(TileEntity tileEntity, int var1, ItemStack var2) {
        if (tileEntity instanceof IPipeTile)
        {
            int var3 = ((IInjectable)tileEntity).injectItem(var2, true, ForgeDirection.VALID_DIRECTIONS[var1 ^ 1], null);
            return var3;
        }
        else
        {
            return 0;
        }
    }

    public int getInvSlotCount() {
        return inventory.length;
    }

    public void receiveGuiNetworkData(int var1, int var2) {
        if(var2 == 0) {
            canAccess = false;
        } else {
            canAccess = true;
        }
    }

    public void readFromNBT(NBTTagCompound tag) {

        super.readFromNBT(tag);

        owner = CoFHProps.DEFAULT_OWNER;
        access = AccessMode.values()[tag.getByte("Access")];

        String OwnerUUID = tag.getString("OwnerUUID");
        String Owner = tag.getString("Owner");

        if(!Strings.isNullOrEmpty(OwnerUUID)) {
            setOwner(new GameProfile(UUID.fromString(OwnerUUID), Owner));
        } else {
            setOwnerName(Owner);
        }

        if(!enableSecurity()) {
            access = AccessMode.PUBLIC;
        }

        this.isActive = tag.getBoolean("Active");

        NBTTagCompound tagCompound = tag.getCompoundTag("RS");

        this.isPowered = tagCompound.getBoolean("Power");
        this.rsMode = ControlMode.values()[tagCompound.getByte("Mode")];

        this.energyStorage.readFromNBT(tag);

        this.facing = getFacingFromNBT(tag);
        this.sideCache = getSideCacheFromNBT(tag, this.getDefaultSides());

        for(int c = 0; c < 6; ++c) {
            if(this.sideCache[c] >= this.getNumConfig(c)) {
                this.sideCache[c] = 0;
            }
        }

        this.readAugmentsFromNBT(tag);
        this.installAugments();
        this.energyStorage.readFromNBT(tag);

        readInventoryFromNBT(tag);
    }

    public void readInventoryFromNBT(NBTTagCompound tag)
    {
        NBTTagList list = tag.getTagList("Inventory", 10);
        inventory = new ItemStack[inventory.length];

        for(int c = 0; c < list.tagCount(); ++c)
        {
            NBTTagCompound tagCompound = list.getCompoundTagAt(c);

            // Ah yes, the perverse way of how items are stored.
            int slot = tagCompound.getInteger("Slot");
            if(slot >= 0 && slot < inventory.length) {
                inventory[slot] = ItemStack.loadItemStackFromNBT(tagCompound);
            }
        }
    }

    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setByte("Access", (byte)access.ordinal());
        tag.setString("OwnerUUID", owner.getId().toString());
        tag.setString("Owner", owner.getName());

        tag.setBoolean("Active", this.isActive);

        NBTTagCompound tagCompound = new NBTTagCompound();

        tagCompound.setBoolean("Power", this.isPowered);
        tagCompound.setByte("Mode", (byte) this.rsMode.ordinal());

        tag.setTag("RS", tagCompound);

        this.energyStorage.writeToNBT(tag);

        tag.setByte("Facing", this.facing);
        tag.setByteArray("SideCache", this.sideCache);

        this.writeAugmentsToNBT(tag);

        writeInventoryToNBT(tag);
    }

    public void writeInventoryToNBT(NBTTagCompound tag) {

        if(inventory.length > 0) {
            NBTTagList list = new NBTTagList();

            for(int c = 0; c < inventory.length; ++c)
            {
                if (inventory[c] != null)
                {
                    NBTTagCompound tagCompound = new NBTTagCompound();

                    tagCompound.setInteger("Slot", c);

                    inventory[c].writeToNBT(tagCompound);

                    list.appendTag(tagCompound);
                }
            }

            tag.setTag("Inventory", list);
        }
    }

    public PacketCoFHBase getPacket() {

        PacketCoFHBase packet = super.getPacket();


        packet.addByte((byte) this.access.ordinal());
        packet.addUUID(this.owner.getId());
        packet.addString(this.owner.getName());


        packet.addBool(this.isPowered);
        packet.addByte(this.rsMode.ordinal());
        packet.addBool(this.isActive);


        packet.addInt(this.energyStorage.getEnergyStored());


        packet.addByteArray(this.sideCache);
        packet.addByte(this.facing);


        packet.addBool(this.augmentReconfigSides);
        packet.addBool(this.augmentRedstoneControl);


        return packet;
    }

    public void onNeighborBlockChange() {

        this.wasPowered = this.isPowered;
        this.isPowered = this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);

        if (this.wasPowered != this.isPowered && this.sendRedstoneUpdates())
        {
            PacketTSBase.sendRSPowerUpdatePacketToClients(this, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            this.onRedstoneUpdate();
        }
    }

    private boolean sendRedstoneUpdates() {
        return false;
    }

    public void onRedstoneUpdate() {
    }

    public final boolean redstoneControlOrDisable() {
        return this.rsMode.isDisabled() || this.isPowered == this.rsMode.getState();
    }

    public String getSoundName() {
        return "";
    }

    public boolean hasWork() {
        return this.isActive;
    }

    public int getChargeSlot() {
        return this.inventory.length - 1;
    }

    public boolean hasChargeSlot() {
        return true;
    }

    private boolean hasEnergy(int energy) {
        return this.energyStorage.getEnergyStored() >= energy;
    }

    private boolean drainEnergy(int energy) {
        return this.hasEnergy(energy) && this.energyStorage.extractEnergy(energy, false) == energy;
    }

    protected void chargeEnergy()
    {
        int var1 = this.getChargeSlot();

        if (this.hasChargeSlot() && EnergyHelper.isEnergyContainerItem(this.inventory[var1]))
        {
            int var2 = Math.min(this.energyStorage.getMaxReceive(), this.energyStorage.getMaxEnergyStored() - this.energyStorage.getEnergyStored());
            this.energyStorage.receiveEnergy(((IEnergyContainerItem)this.inventory[var1].getItem()).extractEnergy(this.inventory[var1], var2, false), false);

            if (this.inventory[var1].stackSize <= 0)
            {
                this.inventory[var1] = null;
            }
        }
    }

    public final void setEnergyStored(int energy) {
        this.energyStorage.setEnergyStored(energy);
    }

    public IEnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    public int getScaledEnergyStored(int energy) {
        return MathHelper.round((double) ((long) this.energyStorage.getEnergyStored() * (long) energy / (long) this.energyStorage.getMaxEnergyStored()));
    }

    public boolean onWrench(EntityPlayer player, int face) {
        return this.rotateBlock();
    }

    public byte[] getDefaultSides() {
        return new byte[]{(byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0};
    }

    public boolean isItemValid(ItemStack var1, int var2, int var3) {
        return true;
    }

    public static byte getFacingFromNBT(NBTTagCompound tag) {
        return !tag.hasKey("Facing") ? 3 : tag.getByte("Facing");
    }

    public static byte[] getSideCacheFromNBT(NBTTagCompound tag, byte[] faceArray) {
        if(tag == null)
        {
            return (byte[])faceArray.clone();
        }
        else
        {
            byte[] var2 = tag.getByteArray("SideCache");
            return var2.length < 6?(byte[])faceArray.clone():var2;
        }
    }

    public static NBTTagCompound setItemStackTagReconfig(NBTTagCompound tag, TileTS tileEntity) {
        if(tileEntity == null)
        {
            return null;
        }
        else
        {
            if(tag == null) {
                tag = new NBTTagCompound();
            }

            tag.setByte("Facing", (byte)tileEntity.getFacing());
            tag.setByteArray("SideCache", tileEntity.sideCache);
            return tag;
        }
    }

    public int getScaledProgress(int var1) {
        return 0;
    }

    public int getScaledSpeed(int var1) {
        return 0;
    }

    public FluidTankAdv getTank() {
        return null;
    }

    public FluidStack getTankFluid() {
        return null;
    }

    public void readAugmentsFromNBT(NBTTagCompound tag)
    {
        NBTTagList list = tag.getTagList("Augments", 10);

        for (int c = 0; c < list.tagCount(); ++c)
        {
            NBTTagCompound var4 = list.getCompoundTagAt(c);

            int var5 = var4.getInteger("Slot");

            if (var5 >= 0 && var5 < this.augments.length)
            {
                this.augments[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
    }

    public void writeAugmentsToNBT(NBTTagCompound tag) {

        if (this.augments.length > 0)
        {
            NBTTagList list = new NBTTagList();

            for (int c = 0; c < this.augments.length; ++c)
            {
                if (this.augments[c] != null)
                {
                    NBTTagCompound tagCompound = new NBTTagCompound();

                    tagCompound.setInteger("Slot", c);

                    this.augments[c].writeToNBT(tagCompound);

                    list.appendTag(tagCompound);
                }
            }
            tag.setTag("Augments", list);
        }
    }

    public void setDefaultSides() {
        this.sideCache = this.getDefaultSides();
    }

    protected boolean hasAugment(String var1, int var2) {
        for(int var3 = 0; var3 < this.augments.length; ++var3) {
            if(isAugmentItem(this.augments[var3]) && ((IAugmentItem)this.augments[var3].getItem()).getAugmentLevel(this.augments[var3], var1) == var2) {
                return true;
            }
        }

        return false;
    }

    protected boolean hasDuplicateAugment(String var1, int var2, int var3) {
        for(int var4 = 0; var4 < this.augments.length; ++var4) {
            if(var4 != var3 && isAugmentItem(this.augments[var4]) && ((IAugmentItem)this.augments[var4].getItem()).getAugmentLevel(this.augments[var4], var1) == var2) {
                return true;
            }
        }

        return false;
    }

    protected boolean hasAugmentChain(String var1, int var2) {
        boolean var3 = true;

        for(int var4 = 1; var4 < var2; ++var4) {
            var3 = var3 && this.hasAugment(var1, var4);
        }

        return var3;
    }
}
