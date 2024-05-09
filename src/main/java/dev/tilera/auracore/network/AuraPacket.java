package dev.tilera.auracore.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dev.tilera.auracore.api.AuraNode;
import dev.tilera.auracore.client.AuraManagerClient;
import dev.tilera.auracore.client.AuraManagerClient.NodeHistoryStats;
import dev.tilera.auracore.client.AuraManagerClient.NodeStats;
import io.netty.buffer.ByteBuf;
import net.anvilcraft.anvillib.network.AnvilPacket;
import net.anvilcraft.anvillib.network.IAnvilPacket;
import net.minecraft.world.World;

@AnvilPacket(Side.CLIENT)
public class AuraPacket implements IAnvilPacket {

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
    public boolean virtual;

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
        this.virtual = node.isVirtual;
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
       this.virtual = buf.readBoolean();
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
        buf.writeBoolean(virtual);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handle(MessageContext ctx) {
        World world = FMLClientHandler.instance().getWorldClient();
        if (AuraManagerClient.auraClientHistory.get(this.key) != null) {
            AuraManagerClient.auraClientHistory.put(this.key, new NodeHistoryStats(AuraManagerClient.auraClientList.get(this.key)));
        }
        AuraManagerClient.auraClientList.put(this.key, new NodeStats(this, world.provider.dimensionId));
        if (AuraManagerClient.auraClientHistory.get(this.key) == null) {
            AuraManagerClient.auraClientHistory.put(this.key, new NodeHistoryStats(this.level, this.flux, this.taint));
        }
    }
    
}
