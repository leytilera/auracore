package dev.tilera.auracore.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dev.tilera.auracore.client.AuraManagerClient;

public class AuraDeletePacketHandler implements IMessageHandler<AuraDeletePacket, IMessage> {

    @Override
    public IMessage onMessage(AuraDeletePacket message, MessageContext ctx) {
        AuraManagerClient.auraClientList.remove(message.key);
        AuraManagerClient.auraClientHistory.remove(message.key);
        return null;
    }
    
}
