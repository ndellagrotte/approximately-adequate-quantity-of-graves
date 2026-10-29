package com.ndellagrotte.aaqg.network;

import com.ndellagrotte.aaqg.AAQG;
import com.ndellagrotte.aaqg.util.LogHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;

public class GravePosTogglePacket extends BaseThreadsafePacket{

    public GravePosTogglePacket()
    {

    }

    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler) {
        AAQG.proxy.toggleGravePositionRendering();
    }

    @Override
    public void handleServerSafe(NetHandlerPlayServer netHandler) {

        LogHelper.error("Attempting to handle rendering packet on server!");
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
}
