package dev.tilera.auracore.mixins;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import dev.tilera.auracore.api.CrystalColors;
import dev.tilera.auracore.api.ICrystal;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.tile.TileCrystalRenderer;
import thaumcraft.common.tiles.TileCrystal;

@Mixin(TileCrystalRenderer.class)
public abstract class MixinTileCrystalRenderer extends TileEntitySpecialRenderer {

    @Shadow(remap = false)
    abstract void drawCrystal(int ori, float x, float y, float z, float a1, float a2, Random rand, int color, float size);

    /**
     * @author tilera
     * @reason Old crystals
     */
    @Overwrite(remap = false)
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
        GL11.glPushMatrix();
        TileCrystal tco = (TileCrystal)te;
        int md = tco.getBlockMetadata();
        int color = CrystalColors.colors[5];
        if (md != 6) {
           color = CrystalColors.getColorForCrystal(md);
        }
        if (md == 9) {
           color = CrystalColors.colors[7];
        }
  
        UtilsFX.bindTexture("textures/models/crystal.png");
        Random rand = new Random((long)(tco.getBlockMetadata() + tco.xCoord + tco.yCoord * tco.zCoord));
        this.drawCrystal(tco.orientation, (float)x, (float)y, (float)z, (rand.nextFloat() - rand.nextFloat()) * 5.0F, (rand.nextFloat() - rand.nextFloat()) * 5.0F, rand, color, 1.1F);
  
        ICrystal ic = (ICrystal) tco;
        for(int a = 1; a < ic.getCrystalCount(md); ++a) {
           if (md == 6 || md == 9) {
              color = CrystalColors.colors[a == 5 ? 6 : a];
           }
  
           int angle1 = rand.nextInt(36) + 72 * a;
           int angle2 = 15 + rand.nextInt(15);
           this.drawCrystal(tco.orientation, (float)x, (float)y, (float)z, (float)angle1, (float)angle2, rand, color, 1.0F);
        }
  
        GL11.glPopMatrix();
        GL11.glDisable(3042);
     }
    
}
