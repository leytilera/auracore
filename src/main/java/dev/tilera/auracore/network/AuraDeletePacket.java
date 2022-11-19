package dev.tilera.auracore.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import dev.tilera.auracore.api.AuraNode;
import io.netty.buffer.ByteBuf;

public class AuraDeletePacket implements IMessage {

    int key;

    public AuraDeletePacket() {}

    public AuraDeletePacket(AuraNode node) {
        this.key = node.key;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        key = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(key);
    }
    
}
