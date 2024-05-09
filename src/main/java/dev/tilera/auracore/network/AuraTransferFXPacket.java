package dev.tilera.auracore.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dev.tilera.auracore.api.AuraNode;
import dev.tilera.auracore.client.FXSparkle;
import dev.tilera.auracore.helper.Utils;
import io.netty.buffer.ByteBuf;
import net.anvilcraft.anvillib.network.AnvilPacket;
import net.anvilcraft.anvillib.network.IAnvilPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

@AnvilPacket(Side.CLIENT)
public class AuraTransferFXPacket implements IAnvilPacket {

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

    @SideOnly(Side.CLIENT)
    @Override
    public void handle(MessageContext ctx) {
        EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
        if (Utils.hasGoggles(player)) {
            double var7 = this.x - this.targetX;
            double var9 = this.y - this.targetY;
            double var11 = this.z - this.targetZ;
            int distance = (int)MathHelper.sqrt_double((double)(var7 * var7 + var9 * var9 + var11 * var11));
            FXSparkle fx = new FXSparkle(player.worldObj, this.x, this.y, this.z, this.targetX, this.targetY, this.targetZ, 2.5f, 0, distance / 2);
            fx.slowdown = false;
            fx.noClip = true;
            fx.leyLineEffect = true;
            fx.shrink = false;
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
        }
    }
    
}
