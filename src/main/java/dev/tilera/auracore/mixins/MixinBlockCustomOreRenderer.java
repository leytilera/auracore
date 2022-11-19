package dev.tilera.auracore.mixins;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.awt.Color;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import dev.tilera.auracore.api.CrystalColors;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import thaumcraft.client.renderers.block.BlockCustomOreRenderer;
import thaumcraft.client.renderers.block.BlockRenderer;
import thaumcraft.common.blocks.BlockCustomOre;

@Mixin(BlockCustomOreRenderer.class)
public abstract class MixinBlockCustomOreRenderer extends BlockRenderer implements ISimpleBlockRenderingHandler {
    
    /**
     * @author tilera
     * @reason Vis, Tainted and Dull ores
     */
    @Overwrite(remap = false)
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderer.setRenderBoundsFromBlock(block);
        if (metadata == 0) {
           drawFaces(renderer, block, ((BlockCustomOre)block).icon[0], false);
        } else if (metadata == 7) {
           drawFaces(renderer, block, ((BlockCustomOre)block).icon[3], false);
        } else {
           drawFaces(renderer, block, ((BlockCustomOre)block).icon[1], false);
           Color c = new Color(CrystalColors.getColorForOre(metadata));
           float r = (float)c.getRed() / 255.0F;
           float g = (float)c.getGreen() / 255.0F;
           float b = (float)c.getBlue() / 255.0F;
           GL11.glColor3f(r, g, b);
           block.setBlockBounds(0.005F, 0.005F, 0.005F, 0.995F, 0.995F, 0.995F);
           renderer.setRenderBoundsFromBlock(block);
           drawFaces(renderer, block, ((BlockCustomOre)block).icon[2], false);
           GL11.glColor3f(1.0F, 1.0F, 1.0F);
        }
  
     }
  
     /**
     * @author tilera
     * @reason Vis, Tainted and Dull ores
     */
    @Overwrite(remap = false)
     public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        int bb = setBrightness(world, x, y, z, block);
        int metadata = world.getBlockMetadata(x, y, z);
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderer.setRenderBoundsFromBlock(block);
        renderer.renderStandardBlock(block, x, y, z);
        if (metadata != 0 && metadata != 7) {
           Tessellator t = Tessellator.instance;
           t.setColorOpaque_I(CrystalColors.getColorForOre(metadata));
           t.setBrightness(Math.max(bb, 160));
           renderAllSides(world, x, y, z, block, renderer, ((BlockCustomOre)block).icon[2], false);
           if (Minecraft.getMinecraft().gameSettings.anisotropicFiltering > 1) {
              block.setBlockBounds(0.005F, 0.005F, 0.005F, 0.995F, 0.995F, 0.995F);
              renderer.setRenderBoundsFromBlock(block);
              t.setBrightness(bb);
              renderAllSides(world, x, y, z, block, renderer, Blocks.stone.getIcon(0, 0), false);
           }
        }
  
        renderer.clearOverrideBlockTexture();
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        renderer.setRenderBoundsFromBlock(block);
        return true;
     }

}
