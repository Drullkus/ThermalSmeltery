package com.drullkus.thermalsmeltery.common.blocks;

import cofh.core.network.PacketCoFHBase;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileExtruder extends TileSmelteryBase
{
    static final int TYPE = BlockSmeltery.Types.EXTRUDER.ordinal();
    private boolean block;

    public static void initialize() {
        defaultSideConfigSmeltery[TYPE] = new SideConfig();
        defaultSideConfigSmeltery[TYPE].numGroup = 5;
        defaultSideConfigSmeltery[TYPE].slotGroups = new int[][]{new int[0], {0}, {1}, new int[0], {1}};
        defaultSideConfigSmeltery[TYPE].allowInsertion = new boolean[]{false, true, false, false, false};
        defaultSideConfigSmeltery[TYPE].allowExtraction = new boolean[]{false, true, true, false, true};
        defaultSideConfigSmeltery[TYPE].sideTex = new int[]{0, 1, 2, 3, 4};
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
        tagCompound.setBoolean("craftBlock",block);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);
        block = tagCompound.getBoolean("craftBlock");
    }

    @Override
    protected void handleGuiPacket(PacketCoFHBase packet)
    {
        super.handleGuiPacket(packet);
        block = packet.getBool();
    }

    @Override
    public PacketCoFHBase getGuiPacket()
    {
        PacketCoFHBase packet = super.getGuiPacket();
        packet.addBool(block);
        return packet;
    }

    @Override
    public int getType()
    {
        return 0;
    }

    protected boolean canStart() {
        return false;
    }

    protected boolean hasValidInput() {
        return false;
    }

    protected void processStart() {
    }

    protected void processFinish() {
    }
}
