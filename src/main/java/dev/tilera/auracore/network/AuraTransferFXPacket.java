package dev.tilera.auracore.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import dev.tilera.auracore.api.AuraNode;
import io.netty.buffer.ByteBuf;

public class AuraTransferFXPacket implements IMessage {

    double x;
    double y;
    double z;
    double targetX;
    double targetY;
    double targetZ;

    public AuraTransferFXPacket() {}

    public AuraTransferFXPacket(AuraNode node, AuraNode targetNode) {
        this.x = node.xPos;
        this.y = node.yPos;
        this.z = node.zPos;
        this.targetX = targetNode.xPos;
        this.targetY = targetNode.yPos;
        this.targetZ = targetNode.zPos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        targetX = buf.readDouble();
        targetY = buf.readDouble();
        targetZ = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeDouble(targetX);
        buf.writeDouble(targetY);
        buf.writeDouble(targetZ);
    }
    
}
