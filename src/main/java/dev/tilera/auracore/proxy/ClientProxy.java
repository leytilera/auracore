package dev.tilera.auracore.proxy;

import dev.tilera.auracore.client.gui.GuiArcaneWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class ClientProxy extends CommonProxy {
    
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == 0) {
            return new GuiArcaneWorkbench(player.inventory, (TileArcaneWorkbench) world.getTileEntity(x, y, z));
        }
        return null;
    }

}
