package dev.tilera.auracore.network;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.anvilcraft.anvillib.network.AnvilPacket;
import net.anvilcraft.anvillib.network.IAnvilPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import thaumcraft.client.fx.bolt.FXLightningBolt;

@AnvilPacket(Side.CLIENT)
public class NodeZapPacket implements IAnvilPacket {

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

    @SideOnly(Side.CLIENT)
    @Override
    public void handle(MessageContext ctx) {
        World world = Minecraft.getMinecraft().theWorld;
        Entity targetedEntity = world.getEntityByID(this.entityID);
        if (targetedEntity != null) {
            FXLightningBolt bolt = new FXLightningBolt(world, this.x, this.y, this.z, targetedEntity.posX, targetedEntity.posY, targetedEntity.posZ, world.rand.nextLong(), 10, 2.0f, 5);
            bolt.defaultFractal();
            bolt.setType(3);
            bolt.finalizeBolt();
        }
    }
    
}
