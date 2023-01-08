package dev.tilera.auracore.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dev.tilera.auracore.client.FXSparkle;
import dev.tilera.auracore.helper.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class AuraTransferFXPacketHandler implements IMessageHandler<AuraTransferFXPacket, IMessage> {

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(AuraTransferFXPacket message, MessageContext ctx) {
        EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
        if (Utils.hasGoggles(player)) {
            double var7 = message.x - message.targetX;
            double var9 = message.y - message.targetY;
            double var11 = message.z - message.targetZ;
            int distance = (int)MathHelper.sqrt_double((double)(var7 * var7 + var9 * var9 + var11 * var11));
            FXSparkle fx = new FXSparkle(player.worldObj, message.x, message.y, message.z, message.targetX, message.targetY, message.targetZ, 2.5f, 0, distance / 2);
            fx.slowdown = false;
            fx.noClip = true;
            fx.leyLineEffect = true;
            fx.shrink = false;
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
        }
        return null;
    }
    
}
