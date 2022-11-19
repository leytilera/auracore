package dev.tilera.auracore.mixins;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tilera.auracore.api.IWand;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import thaumcraft.client.renderers.tile.TileArcaneWorkbenchRenderer;
import thaumcraft.common.tiles.TileArcaneWorkbench;

@Mixin(TileArcaneWorkbenchRenderer.class)
public abstract class MixinTileArcaneWorkbenchRenderer extends TileEntitySpecialRenderer {
    
    @Inject(method = "renderTileEntityAt", at = @At("TAIL"), remap = false)
    protected void onRender(TileArcaneWorkbench table, double par2, double par4, double par6, float par8, CallbackInfo ci) {
        if (table.getWorldObj() != null && table.getStackInSlot(10) != null && table.getStackInSlot(10).getItem() instanceof IWand) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)par2 + 0.65F, (float)par4 + 1.0625F, (float)par6 + 0.25F);
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F); //TODO: render in middle
            //GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            ItemStack is = table.getStackInSlot(10).copy();
            is.stackSize = 1;
            EntityItem entityitem = new EntityItem(table.getWorldObj(), 0.0D, 0.0D, 0.0D, is);
            entityitem.hoverStart = 0.0F;
            RenderItem.renderInFrame = true;
            RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
            RenderItem.renderInFrame = false;
            GL11.glPopMatrix();
         }
    }

}
