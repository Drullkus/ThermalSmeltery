package com.drullkus.thermalsmeltery.common.network;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PacketTSBase extends PacketCoFHBase
{
    public PacketTSBase() {
    }

    public static void initialize() {
        PacketHandler.instance.registerPacket(PacketTSBase.class);
    }

    @Override
    public void handlePacket(EntityPlayer entityPlayer, boolean b) {

    }

    public static void sendRSConfigUpdatePacketToServer(IRedstoneControl var0, int var1, int var2, int var3) {
        PacketHandler.sendToServer(getPacket(PacketTSBase.PacketTypes.RS_CONFIG_UPDATE).addCoords(var1, var2, var3).addByte(var0.getControl().ordinal()));
    }

    public static void sendRSPowerUpdatePacketToClients(IRedstoneControl var0, World var1, int var2, int var3, int var4) {
        PacketHandler.sendToAllAround(getPacket(PacketTSBase.PacketTypes.RS_POWER_UPDATE).addCoords(var2, var3, var4).addBool(var0.isPowered()), var1, var2, var3, var4);
    }

    public static PacketCoFHBase getPacket(PacketTSBase.PacketTypes var0) {
        return (new PacketTSBase()).addByte(var0.ordinal());
    }

    public static enum PacketTypes {
        RS_POWER_UPDATE,
        RS_CONFIG_UPDATE,
        SECURITY_UPDATE,
        TAB_AUGMENT,
        TAB_SCHEMATIC,
        CONFIG_SYNC;
    }
}