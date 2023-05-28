package dev.tilera.auracore.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IWandableBlock {
    
    /**
     * Called by wands when interacting with the block
     * @param world The World of the block
     * @param stack The ItemStack of the wand
     * @param impl The IWand implementation for the wand
     * @param player The player
     * @param x xCoord of the block
     * @param y yCoord of the block
     * @param z zCoord of the block
     * @param side The side, which was activated
     * @param md The metadata of the block
     * @return true, if block was successfully wanded
     */
    boolean onWandRightClick(World world, ItemStack stack, IWand impl, EntityPlayer player, int x, int y, int z, ForgeDirection side, int md);

}
