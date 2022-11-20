package dev.tilera.auracore.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import dev.tilera.auracore.api.AuraNode;
import io.netty.buffer.ByteBuf;

public class AuraPacket implements IMessage {

    public int key;
    public double x;
    public double y;
    public double z;
    public short level;
    public short base;
    public short taint;
    public int flux;
    public boolean lock;
    public byte type;

    public AuraPacket() {}

    public AuraPacket(AuraNode node) {
        this.key = node.key;
        this.x = node.xPos;
        this.y = node.yPos;
        this.z = node.zPos;
        this.level = node.level;
        this.base = node.baseLevel;
        this.taint = node.taint;
        this.flux = node.flux.visSize();
        this.lock = node.locked;
        this.type = (byte) node.type.ordinal();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
       this.key = buf.readInt();
       this.x = buf.readDouble();
       this.y = buf.readDouble();
       this.z = buf.readDouble();
       this.level = buf.readShort();
       this.base = buf.readShort();
       this.taint = buf.readShort();
       this.flux = buf.readInt();
       this.lock = buf.readBoolean();
       this.type = buf.readByte(); 
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(key);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeShort(level);
        buf.writeShort(base);
        buf.writeShort(taint);
        buf.writeInt(flux);
        buf.writeBoolean(lock);
        buf.writeByte(type);
    }
    
}
