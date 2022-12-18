package dev.tilera.auracore.proxy;

import cpw.mods.fml.relauncher.Side;
import dev.tilera.auracore.AuraCore;
import dev.tilera.auracore.client.gui.GuiArcaneWorkbench;
import dev.tilera.auracore.network.AuraDeletePacket;
import dev.tilera.auracore.network.AuraDeletePacketHandler;
import dev.tilera.auracore.network.AuraPacket;
import dev.tilera.auracore.network.AuraPacketHandler;
import dev.tilera.auracore.network.AuraTransferFXPacket;
import dev.tilera.auracore.network.AuraTransferFXPacketHandler;
import dev.tilera.auracore.network.NodeZapPacket;
import dev.tilera.auracore.network.NodeZapPacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        int pktID = 0;
        AuraCore.CHANNEL.registerMessage(AuraPacketHandler.class, AuraPacket.class, pktID++, Side.CLIENT);
        AuraCore.CHANNEL.registerMessage(AuraDeletePacketHandler.class, AuraDeletePacket.class, pktID++, Side.CLIENT);
        AuraCore.CHANNEL.registerMessage(AuraTransferFXPacketHandler.class, AuraTransferFXPacket.class, pktID++, Side.CLIENT);
        AuraCore.CHANNEL.registerMessage(NodeZapPacketHandler.class, NodeZapPacket.class, pktID++, Side.CLIENT);
        super.preInit();
    }
    
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == 0) {
            return new GuiArcaneWorkbench(player.inventory, (TileArcaneWorkbench) world.getTileEntity(x, y, z));
        }
        return null;
    }

}
