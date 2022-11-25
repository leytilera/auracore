package dev.tilera.auracore.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockCrystalItem;
import thaumcraft.common.tiles.TileCrystal;

@Mixin(BlockCrystalItem.class)
public abstract class MixinBlockCrystalItem extends ItemBlock {

    public MixinBlockCrystalItem(Block p_i45328_1_) {
        super(p_i45328_1_);
    }
    
    /**
     * @author tilera
     * @reason Place old crystals
     */
    @Overwrite(remap = false)
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        boolean placed = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
        if (placed) {
           try {
              TileCrystal ts = (TileCrystal)world.getTileEntity(x, y, z);
              ts.orientation = (short)side;
           } catch (Exception var14) {
           }
        }
  
        return placed;
    }
}
