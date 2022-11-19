package dev.tilera.auracore.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import thaumcraft.client.fx.bolt.FXLightningBolt;

public class NodeZapPacketHandler implements IMessageHandler<NodeZapPacket, IMessage> {

    @Override
    public IMessage onMessage(NodeZapPacket message, MessageContext ctx) {
        World world = Minecraft.getMinecraft().theWorld;
        Entity targetedEntity = world.getEntityByID(message.entityID);
        if (targetedEntity != null) {
            FXLightningBolt bolt = new FXLightningBolt(world, message.x, message.y, message.z, targetedEntity.posX, targetedEntity.posY, targetedEntity.posZ, world.rand.nextLong(), 10, 2.0f, 5);
            bolt.defaultFractal();
            bolt.setType(3);
            bolt.finalizeBolt();
        }
        return null;
    }
    
}
