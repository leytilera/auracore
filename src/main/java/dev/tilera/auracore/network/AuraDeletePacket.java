package dev.tilera.auracore.network;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dev.tilera.auracore.api.AuraNode;
import dev.tilera.auracore.client.AuraManagerClient;
import io.netty.buffer.ByteBuf;
import net.anvilcraft.anvillib.network.AnvilPacket;
import net.anvilcraft.anvillib.network.IAnvilPacket;

@AnvilPacket(Side.CLIENT)
public class AuraDeletePacket implements IAnvilPacket {

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

    @SideOnly(Side.CLIENT)
    @Override
    public void handle(MessageContext ctx) {
        AuraManagerClient.auraClientList.remove(this.key);
        AuraManagerClient.auraClientHistory.remove(this.key);
    }
    
}
