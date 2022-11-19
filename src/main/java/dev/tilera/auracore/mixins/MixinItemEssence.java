package dev.tilera.auracore.mixins;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import dev.tilera.auracore.api.IEssenceContainer;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.ItemEssence;
import thaumcraft.common.tiles.TileJarFillable;

@Mixin(ItemEssence.class)
public abstract class MixinItemEssence extends Item implements IEssentiaContainerItem {
    
    /**
     * @author tilera
     * @reason API for essence containers
     */
    @Overwrite(remap = false)
    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float f1, float f2, float f3) {
        Block bi = world.getBlock(x, y, z);
        int md = world.getBlockMetadata(x, y, z);
        TileEntity te = world.getTileEntity(x, y, z);
        if (itemstack.getItemDamage() == 0 && te instanceof IEssenceContainer) {
           IEssenceContainer tile = (IEssenceContainer) te;
           if (tile.getAmount() >= 8) {
              if (world.isRemote) {
                 player.swingItem();
                 return false;
              }
  
              ItemStack phial = new ItemStack(this, 1, 1);
              this.setAspects(phial, (new AspectList()).add(tile.getAspect(), 8));
              if (tile.takeFromContainer(tile.getAspect(), 8)) {
                 --itemstack.stackSize;
                 if (!player.inventory.addItemStackToInventory(phial)) {
                    world.spawnEntityInWorld(new EntityItem(world, (double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), phial));
                 }
  
                 world.playSoundAtEntity(player, "game.neutral.swim", 0.25F, 1.0F);
                 player.inventoryContainer.detectAndSendChanges();
                 return true;
              }
           }
        }
  
        AspectList al = this.getAspects(itemstack);
        if (al != null && al.size() == 1) {
           Aspect aspect = al.getAspects()[0];
           if (itemstack.getItemDamage() != 0 && bi == ConfigBlocks.blockJar && (md == 0 || md == 3)) {
              TileJarFillable tile = (TileJarFillable)world.getTileEntity(x, y, z);
              if (tile.amount <= tile.maxAmount - 8 && tile.doesContainerAccept(aspect)) {
                 if (world.isRemote) {
                    player.swingItem();
                    return false;
                 }
  
                 if (tile.addToContainer(aspect, 8) == 0) {
                    world.markBlockForUpdate(x, y, z);
                    tile.markDirty();
                    --itemstack.stackSize;
                    if (!player.inventory.addItemStackToInventory(new ItemStack(this, 1, 0))) {
                       world.spawnEntityInWorld(new EntityItem(world, (double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), new ItemStack(this, 1, 0)));
                    }
  
                    world.playSoundAtEntity(player, "game.neutral.swim", 0.25F, 1.0F);
                    player.inventoryContainer.detectAndSendChanges();
                    return true;
                 }
              }
           }
        }
  
        return false;
     }

}
