package dev.tilera.auracore.proxy;

import cpw.mods.fml.common.network.IGuiHandler;
import dev.tilera.auracore.container.ContainerWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class CommonProxy implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == 0) {
            return new ContainerWorkbench(player.inventory, (TileArcaneWorkbench) world.getTileEntity(x, y, z));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }
    
}
