package dev.tilera.auracore.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dev.tilera.auracore.client.AuraManagerClient;
import dev.tilera.auracore.client.AuraManagerClient.NodeHistoryStats;
import dev.tilera.auracore.client.AuraManagerClient.NodeStats;
import net.minecraft.world.World;

public class AuraPacketHandler implements IMessageHandler<AuraPacket, IMessage> {

    @Override
    public IMessage onMessage(AuraPacket message, MessageContext ctx) {
        World world = FMLClientHandler.instance().getWorldClient();
        if (AuraManagerClient.auraClientHistory.get(message.key) != null) {
            AuraManagerClient.auraClientHistory.put(message.key, new NodeHistoryStats(AuraManagerClient.auraClientList.get(message.key)));
        }
        AuraManagerClient.auraClientList.put(message.key, new NodeStats(message, world.provider.dimensionId));
        if (AuraManagerClient.auraClientHistory.get(message.key) == null) {
            AuraManagerClient.auraClientHistory.put(message.key, new NodeHistoryStats(message.level, message.flux));
        }
        return null;
    }
    
}
