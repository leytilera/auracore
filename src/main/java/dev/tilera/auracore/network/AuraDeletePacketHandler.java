package dev.tilera.auracore.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dev.tilera.auracore.client.AuraManagerClient;

public class AuraDeletePacketHandler implements IMessageHandler<AuraDeletePacket, IMessage> {

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(AuraDeletePacket message, MessageContext ctx) {
        AuraManagerClient.auraClientList.remove(message.key);
        AuraManagerClient.auraClientHistory.remove(message.key);
        return null;
    }
    
}
