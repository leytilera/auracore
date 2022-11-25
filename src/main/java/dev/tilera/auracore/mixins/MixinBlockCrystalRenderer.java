package dev.tilera.auracore.mixins;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import thaumcraft.client.renderers.block.BlockCrystalRenderer;
import thaumcraft.client.renderers.block.BlockRenderer;
import thaumcraft.common.tiles.TileCrystal;
import thaumcraft.common.tiles.TileEldritchCrystal;

@Mixin(BlockCrystalRenderer.class)
public abstract class MixinBlockCrystalRenderer extends BlockRenderer {
    
    /**
     * @author tilera
     * @reason Render old crystals
     */
    @Overwrite(remap = false)
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        if (metadata == 7) {
           GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
           TileEntityRendererDispatcher.instance.renderTileEntityAt(new TileEldritchCrystal(), 0.0D, 0.0D, 0.0D, 0.0F);
           GL11.glEnable(32826);
        } else {
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            TileCrystal tc = new TileCrystal();
            tc.blockMetadata = metadata;
            TileEntityRendererDispatcher.instance.renderTileEntityAt(tc, 0.0D, 0.0D, 0.0D, 0.0F);
            GL11.glEnable(32826);
        }
  
     }

}
