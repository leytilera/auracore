package dev.tilera.auracore.client.gui;

import org.lwjgl.opengl.GL11;

import dev.tilera.auracore.api.IWand;
import dev.tilera.auracore.container.ContainerWorkbench;
import dev.tilera.auracore.crafting.AuracoreCraftingManager;
import dev.tilera.auracore.helper.Utils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class GuiArcaneWorkbench extends GuiContainer {
    private TileArcaneWorkbench tileEntity;
    private InventoryPlayer ip;

    public GuiArcaneWorkbench(InventoryPlayer par1InventoryPlayer, TileArcaneWorkbench e) {
        super(new ContainerWorkbench(par1InventoryPlayer, e));
        this.tileEntity = e;
        this.ip = par1InventoryPlayer;
        this.ySize += 10;
    }

    protected void drawGuiContainerForegroundLayer() {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        this.mc.renderEngine.bindTexture(new ResourceLocation("auracore", "textures/gui/gui_arcaneworkbench.png"));
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glEnable((int)3042);
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        GL11.glDisable((int)3042);
        if (this.tileEntity.getStackInSlot(10) != null && this.tileEntity.getStackInSlot(10).getItem() instanceof IWand) {
            IWand wandImpl = (IWand) this.tileEntity.getStackInSlot(10).getItem();
            int charge = wandImpl.getVis(this.tileEntity.getStackInSlot(10));
            if (charge > 0) {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(var5 + 132), (float)(var6 + 85), (float)505.0f);
                GL11.glScalef((float)0.5f, (float)0.5f, (float)0.0f);
                String text = charge + " vis";
                int ll = this.fontRendererObj.getStringWidth(text) / 2;
                this.fontRendererObj.drawStringWithShadow(text, -ll, -16, 0xFFFFFF);
                GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                GL11.glPopMatrix();
            }
            IArcaneRecipe recipe = AuracoreCraftingManager.findMatchingArcaneRecipe(this.tileEntity, this.ip.player);
            if (null != null) {
                int ll;
                String text;
                int cost = AuracoreCraftingManager.getArcaneRecipeVisCost(recipe, this.tileEntity);
                int discount = 100 - Math.min(50, Utils.getTotalVisDiscount(this.ip.player));
                if (charge < (cost = Math.round((float)cost * ((float)discount / 100.0f)))) {
                    GL11.glPushMatrix();
                    RenderHelper.enableGUIStandardItemLighting();
                    GL11.glDisable((int)2896);
                    GL11.glEnable((int)32826);
                    GL11.glEnable((int)2903);
                    GL11.glEnable((int)2896);
                    itemRender.renderItemIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, recipe.getCraftingResult(this.tileEntity), var5 + 124, var6 + 28);
                    itemRender.renderItemOverlayIntoGUI(this.mc.fontRenderer, this.mc.renderEngine, recipe.getCraftingResult(this.tileEntity), var5 + 124, var6 + 28);
                    GL11.glDisable((int)2896);
                    GL11.glDepthMask((boolean)true);
                    GL11.glEnable((int)2929);
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)(var5 + 132), (float)(var6 + 82), (float)0.0f);
                    GL11.glScalef((float)0.5f, (float)0.5f, (float)0.0f);
                    text = "Insufficient charge";
                    if (cost > wandImpl.getMaxVis(this.tileEntity.getStackInSlot(10))) {
                        text = "This wand is too weak";
                    }
                    ll = this.fontRendererObj.getStringWidth(text) / 2;
                    this.fontRendererObj.drawString(text, -ll, 0, 0xEE6E6E);
                    GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                    GL11.glPopMatrix();
                }
                if (cost > 0) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)(var5 + 132), (float)(var6 + 81), (float)0.0f);
                    GL11.glScalef((float)0.5f, (float)0.5f, (float)0.0f);
                    text = cost + " vis";
                    ll = this.fontRendererObj.getStringWidth(text) / 2;
                    this.fontRendererObj.drawStringWithShadow(text, -ll, -64, 0xEEEEEE);
                    GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                    GL11.glPopMatrix();
                }
            }
        }
    }
}


