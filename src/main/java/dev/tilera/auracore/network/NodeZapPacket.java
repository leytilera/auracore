package dev.tilera.auracore.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

public class NodeZapPacket implements IMessage {

    public double x;
    public double y;
    public double z;
    public int entityID;

    public NodeZapPacket() {}

    public NodeZapPacket(double x, double y, double z, Entity target) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.entityID = target.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.entityID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(entityID);
    }
    
}
