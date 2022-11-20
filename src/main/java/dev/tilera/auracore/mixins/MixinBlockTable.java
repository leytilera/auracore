package dev.tilera.auracore.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import dev.tilera.auracore.AuraCore;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockTable;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileArcaneWorkbench;
import thaumcraft.common.tiles.TileDeconstructionTable;
import thaumcraft.common.tiles.TileResearchTable;

@Mixin(BlockTable.class)
public abstract class MixinBlockTable extends BlockContainer {
    
    protected MixinBlockTable(Material p_i45386_1_) {
        super(p_i45386_1_);
    }

    /**
     * @author tilera
     * @reason Old workbench GUI
     */
    @Overwrite(remap = false)
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int idk, float what, float these, float are) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        int md = world.getBlockMetadata(x, y, z);
        if (md > 1 && tileEntity != null && !player.isSneaking()) {
           if (world.isRemote) {
              return true;
           } else if (tileEntity instanceof TileArcaneWorkbench) {
              TileArcaneWorkbench workbench = (TileArcaneWorkbench) tileEntity;
              if (workbench.getStackInSlot(10) != null && workbench.getStackInSlot(10).getItem() instanceof ItemWandCasting) {
                player.openGui(Thaumcraft.instance, 13, world, x, y, z);
              } else {
                player.openGui(AuraCore.INSTANCE, 0, world, x, y, z);
              }
              return true;
           } else if (tileEntity instanceof TileDeconstructionTable) {
              player.openGui(Thaumcraft.instance, 8, world, x, y, z);
              return true;
           } else {
              if (tileEntity instanceof TileResearchTable) {
                 player.openGui(Thaumcraft.instance, 10, world, x, y, z);
              } else {
                 for(int a = 2; a < 6; ++a) {
                    TileEntity tile = world.getTileEntity(x + ForgeDirection.getOrientation(a).offsetX, y + ForgeDirection.getOrientation(a).offsetY, z + ForgeDirection.getOrientation(a).offsetZ);
                    if (tile != null && tile instanceof TileResearchTable) {
                       player.openGui(Thaumcraft.instance, 10, world, x + ForgeDirection.getOrientation(a).offsetX, y + ForgeDirection.getOrientation(a).offsetY, z + ForgeDirection.getOrientation(a).offsetZ);
                       break;
                    }
                 }
              }
  
              return true;
           }
        } else {
           return false;
        }
     }

}
