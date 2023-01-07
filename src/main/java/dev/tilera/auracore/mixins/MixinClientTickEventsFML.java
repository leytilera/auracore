package dev.tilera.auracore.mixins;

import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.client.FMLClientHandler;
import dev.tilera.auracore.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.ClientTickEventsFML;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.research.ScanManager;

@Mixin(ClientTickEventsFML.class)
public abstract class MixinClientTickEventsFML {

    @Shadow(remap = false)
    protected abstract boolean isMouseOverSlot(Slot par1Slot, int par2, int par3, int par4, int par5);
    
    /**
     * @author tilera
     * @reason Show aspects without scanning
     */
    @Overwrite(remap = false)
    public void renderAspectsInGui(GuiContainer gui, EntityPlayer player) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        ScaledResolution var13 = new ScaledResolution(Minecraft.getMinecraft(), mc.displayWidth, mc.displayHeight);
        int var14 = var13.getScaledWidth();
        int var15 = var13.getScaledHeight();
        int var16 = Mouse.getX() * var14 / mc.displayWidth;
        int var17 = var15 - Mouse.getY() * var15 / mc.displayHeight - 1;
        GL11.glPushMatrix();
        GL11.glPushAttrib(1048575);
        GL11.glDisable(2896);
  
        for(int var20 = 0; var20 < gui.inventorySlots.inventorySlots.size(); ++var20) {
           int xs = UtilsFX.getGuiXSize(gui);
           int ys = UtilsFX.getGuiYSize(gui);
           int shift = 0;
           int shift2 = 0;
           int shiftx = -8;
           int shifty = -8;
           if (Thaumcraft.instance.aspectShift) {
              shiftx -= 8;
              shifty -= 8;
           }
  
           Slot var23 = (Slot)gui.inventorySlots.inventorySlots.get(var20);
           int guiLeft = shift + (gui.width - xs - shift2) / 2;
           int guiTop = (gui.height - ys) / 2;
           if (this.isMouseOverSlot(var23, var16, var17, guiLeft, guiTop) && var23.getStack() != null) {
              int h = ScanManager.generateItemHash(var23.getStack().getItem(), var23.getStack().getItemDamage());
              List<String> list = Thaumcraft.proxy.getScannedObjects().get(player.getCommandSenderName());
              if (Config.noScanning() || (list != null && (list.contains("@" + h) || list.contains("#" + h)))) {
                 AspectList tags = ThaumcraftCraftingManager.getObjectTags(var23.getStack());
                 tags = ThaumcraftCraftingManager.getBonusTags(var23.getStack(), tags);
                 if (tags != null) {
                    int x = var16 + 17;
                    int y = var17 + 7 - 33;
                    GL11.glDisable(2929);
                    int index = 0;
                    if (tags.size() > 0) {
                       Aspect[] arr$ = tags.getAspectsSortedAmount();
                       int len$ = arr$.length;
  
                       for(int i$ = 0; i$ < len$; ++i$) {
                          Aspect tag = arr$[i$];
                          if (tag != null) {
                             x = var16 + 17 + index * 18;
                             y = var17 + 7 - 33;
                             UtilsFX.bindTexture("textures/aspects/_back.png");
                             GL11.glPushMatrix();
                             GL11.glEnable(3042);
                             GL11.glBlendFunc(770, 771);
                             GL11.glTranslated((double)(x + shiftx - 2), (double)(y + shifty - 2), 0.0D);
                             GL11.glScaled(1.25D, 1.25D, 0.0D);
                             UtilsFX.drawTexturedQuadFull(0, 0, (double)UtilsFX.getGuiZLevel(gui));
                             GL11.glDisable(3042);
                             GL11.glPopMatrix();
                             if (Thaumcraft.proxy.playerKnowledge.hasDiscoveredAspect(player.getCommandSenderName(), tag)) {
                                UtilsFX.drawTag(x + shiftx, y + shifty, tag, (float)tags.getAmount(tag), 0, (double)UtilsFX.getGuiZLevel(gui));
                             } else {
                                UtilsFX.bindTexture("textures/aspects/_unknown.png");
                                GL11.glPushMatrix();
                                GL11.glEnable(3042);
                                GL11.glBlendFunc(770, 771);
                                GL11.glTranslated((double)(x + shiftx), (double)(y + shifty), 0.0D);
                                UtilsFX.drawTexturedQuadFull(0, 0, (double)UtilsFX.getGuiZLevel(gui));
                                GL11.glDisable(3042);
                                GL11.glPopMatrix();
                             }
  
                             ++index;
                          }
                       }
                    }
  
                    GL11.glEnable(2929);
                 }
              }
           }
        }
  
        GL11.glPopAttrib();
        GL11.glPopMatrix();
     }

}
