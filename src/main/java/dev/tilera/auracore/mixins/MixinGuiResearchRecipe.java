package dev.tilera.auracore.mixins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import dev.tilera.auracore.api.Aspects;
import dev.tilera.auracore.api.crafting.CrucibleRecipe;
import dev.tilera.auracore.api.crafting.IInfusionRecipe;
import dev.tilera.auracore.api.crafting.ShapedInfusionCraftingRecipe;
import dev.tilera.auracore.api.crafting.ShapelessInfusionCraftingRecipe;
import dev.tilera.auracore.api.research.ResearchPageCrucible;
import dev.tilera.auracore.api.research.ResearchPageInfusion;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.research.ResearchPage.PageType;
import thaumcraft.client.gui.GuiResearchRecipe;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.lib.utils.InventoryUtils;

@Mixin(GuiResearchRecipe.class)
public abstract class MixinGuiResearchRecipe extends GuiScreen {

    @Shadow(remap = false)
    private ResearchItem research;
    @Shadow(remap = false)
    String tex2;
    @Shadow(remap = false)
    private int cycle;
    @Shadow(remap = false)
    ArrayList<List> reference;
    @Shadow(remap = false)
    private int page;
    @Shadow(remap = false)
    private long lastCycle;
    @Shadow(remap = false)
    protected static RenderItem itemRenderer;
    @Shadow(remap = false)
    public abstract void drawCustomTooltip(GuiScreen gui, RenderItem itemRenderer, FontRenderer fr, List var4, int par2, int par3, int subTipColor);
    @Shadow(remap = false)
    public abstract Object[] findRecipeReference(ItemStack item);
    @Shadow(remap = false)
    protected abstract void drawAspectPage(int side, int x, int y, int mx, int my, AspectList aspects);
    @Shadow(remap = false)
    protected abstract void drawCompoundCraftingPage(int side, int x, int y, int mx, int my, ResearchPage page);
    @Shadow(remap = false)
    protected abstract void drawCruciblePage(int side, int x, int y, int mx, int my, ResearchPage pageParm);
    @Shadow(remap = false)
    protected abstract void drawCraftingPage(int side, int x, int y, int mx, int my, ResearchPage pageParm);
    @Shadow(remap = false)
    protected abstract void drawArcaneCraftingPage(int side, int x, int y, int mx, int my, ResearchPage pageParm);
    @Shadow(remap = false)
    protected abstract void drawInfusionPage(int side, int x, int y, int mx, int my, ResearchPage pageParm);
    @Shadow(remap = false)
    protected abstract void drawInfusionEnchantingPage(int side, int x, int y, int mx, int my, ResearchPage pageParm);
    @Shadow(remap = false)
    protected abstract void drawSmeltingPage(int side, int x, int y, int mx, int my, ResearchPage pageParm);
    @Shadow(remap = false)
    protected abstract void drawTextPage(int side, int x, int y, String text);

    /**
     * @author tilera
     * @reason Custom pages
     */
    @Overwrite(remap = false)
    private void drawPage(ResearchPage pageParm, int side, int x, int y, int mx, int my) {
        GL11.glPushAttrib(1048575);
        if (this.lastCycle < System.currentTimeMillis()) {
           ++this.cycle;
           this.lastCycle = System.currentTimeMillis() + 1000L;
        }
  
        if (this.page == 0 && side == 0) {
           this.drawTexturedModalRect(x + 4, y - 13, 24, 184, 96, 4);
           this.drawTexturedModalRect(x + 4, y + 4, 24, 184, 96, 4);
           int offset = super.fontRendererObj.getStringWidth(this.research.getName());
           if (offset <= 130) {
              super.fontRendererObj.drawString(this.research.getName(), x + 52 - offset / 2, y - 6, 3158064);
           } else {
              float vv = 130.0F / (float)offset;
              GL11.glPushMatrix();
              GL11.glTranslatef((float)(x + 52) - (float)(offset / 2) * vv, (float)y - 6.0F * vv, 0.0F);
              GL11.glScalef(vv, vv, vv);
              super.fontRendererObj.drawString(this.research.getName(), 0, 0, 3158064);
              GL11.glPopMatrix();
           }
  
           y += 25;
        }
  
        GL11.glAlphaFunc(516, 0.003921569F);
        if (pageParm instanceof ResearchPageInfusion) {
            this.drawInfusionCraftingPage(side, x, y, mx, my, pageParm);
        } else if (pageParm instanceof ResearchPageCrucible) {
            this.drawLegacyCruciblePage(side, x, y, mx, my, pageParm);
        } else if (pageParm.type != PageType.TEXT && pageParm.type != PageType.TEXT_CONCEALED) {
           if (pageParm.type == PageType.ASPECTS) {
              this.drawAspectPage(side, x - 8, y - 8, mx, my, pageParm.aspects);
           } else if (pageParm.type == PageType.CRUCIBLE_CRAFTING) {
              this.drawCruciblePage(side, x - 4, y - 8, mx, my, pageParm);
           } else if (pageParm.type == PageType.NORMAL_CRAFTING) {
              this.drawCraftingPage(side, x - 4, y - 8, mx, my, pageParm);
           } else if (pageParm.type == PageType.ARCANE_CRAFTING) {
              this.drawArcaneCraftingPage(side, x - 4, y - 8, mx, my, pageParm);
           } else if (pageParm.type == PageType.COMPOUND_CRAFTING) {
              this.drawCompoundCraftingPage(side, x - 4, y - 8, mx, my, pageParm);
           } else if (pageParm.type == PageType.INFUSION_CRAFTING) {
              this.drawInfusionPage(side, x - 4, y - 8, mx, my, pageParm);
           } else if (pageParm.type == PageType.INFUSION_ENCHANTMENT) {
              this.drawInfusionEnchantingPage(side, x - 4, y - 8, mx, my, pageParm);
           } else if (pageParm.type == PageType.SMELTING) {
              this.drawSmeltingPage(side, x - 4, y - 8, mx, my, pageParm);
           }
        } else {
           this.drawTextPage(side, x, y - 10, pageParm.getTranslatedText());
        }
  
        GL11.glAlphaFunc(516, 0.1F);
        GL11.glPopAttrib();
     }

    private void drawInfusionCraftingPage(int side, int x, int y, int mx, int my, ResearchPage pageParm) {
        IInfusionRecipe recipe = null;
        Object tr = null;
        if (pageParm.recipe instanceof Object[]) {
           try {
              tr = ((Object[])((Object[])pageParm.recipe))[this.cycle];
           } catch (Exception var22) {
              this.cycle = 0;
              tr = ((Object[])((Object[])pageParm.recipe))[this.cycle];
           }
        } else {
           tr = pageParm.recipe;
        }
  
        if (tr instanceof ShapedInfusionCraftingRecipe) {
           recipe = (ShapedInfusionCraftingRecipe)tr;
        } else if (tr instanceof ShapelessInfusionCraftingRecipe) {
           recipe = (ShapelessInfusionCraftingRecipe)tr;
        }
  
        if (recipe != null) {
           GL11.glPushMatrix();
           int start = side * 152;
           UtilsFX.bindTexture(this.tex2);
           GL11.glPushMatrix();
           GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
           GL11.glEnable(3042);
           GL11.glTranslatef((float)(x + start), (float)y, 0.0F);
           GL11.glScalef(2.0F, 2.0F, 1.0F);
           this.drawTexturedModalRect(2, 27, 112, 15, 52, 52);
           this.drawTexturedModalRect(20, 7, 20, 3, 16, 16);
           GL11.glPopMatrix();
           GL11.glPushMatrix();
           GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);
           GL11.glEnable(3042);
           GL11.glTranslatef((float)(x + start), (float)(y + 164), 0.0F);
           GL11.glScalef(2.0F, 2.0F, 1.0F);
           this.drawTexturedModalRect(0, 0, 68, 76, 12, 12);
           GL11.glPopMatrix();
           int mposx = mx;
           int mposy = my;
           AspectList tags = ((IInfusionRecipe)recipe).getAspects();
           int rw;
           int i;
           int j;
           if (tags != null && tags.size() > 0) {
              int count = 0;
              Aspect[] aspects = tags.getAspectsSortedAmount();
              rw = aspects.length;
  
              Aspect tag;
              for(int k = 0; k < rw; ++k) {
                 tag = aspects[k];
                 i = x + start + 14 + 18 * count + (5 - tags.size()) * 8;
                 j = y + 172;
                 UtilsFX.drawTag(x + start + 14 + 18 * count + (5 - tags.size()) * 8, y + 172, tag, (float)tags.getAmount(tag), 0, 0.0D, 771, 1.0F);
                 if (mposx >= i && mposy >= j && mposx < i + 16 && mposy < j + 16) {
                     this.drawCustomTooltip(this, itemRenderer, super.fontRendererObj, Arrays.asList(tag.getName(), tag.getLocalizedDescription()), mx, my - 8, 11);
                 }
                 ++count;
              }

           }
           UtilsFX.drawTag(x + 48 + start + 32, y + 22, Aspects.VIS, recipe.getCost(), 0, 0.0D, 771, 1.0F);
           if (mposx >= x + 48 + start + 32 && mposy >= y + 27 && mposx < x + 48 + start + 48 && mposy < y + 27 + 16) {
            this.drawCustomTooltip(this, itemRenderer, super.fontRendererObj, Arrays.asList(Aspects.VIS.getName(), Aspects.VIS.getLocalizedDescription()), mx, my - 8, 11);
           }
           GL11.glPushMatrix();
           GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
           GL11.glTranslated(0.0D, 0.0D, 100.0D);
           RenderHelper.enableGUIStandardItemLighting();
           GL11.glEnable(2884);
           itemRenderer.renderItemAndEffectIntoGUI(super.mc.fontRenderer, super.mc.renderEngine, InventoryUtils.cycleItemStack(((IInfusionRecipe)recipe).getRecipeOutput()), x + 48 + start, y + 22);
           itemRenderer.renderItemOverlayIntoGUI(super.mc.fontRenderer, super.mc.renderEngine, InventoryUtils.cycleItemStack(((IInfusionRecipe)recipe).getRecipeOutput()), x + 48 + start, y + 22);
           RenderHelper.disableStandardItemLighting();
           GL11.glEnable(2896);
           GL11.glPopMatrix();
           if (mposx >= x + 48 + start && mposy >= y + 27 && mposx < x + 48 + start + 16 && mposy < y + 27 + 16) {
              this.drawCustomTooltip(this, itemRenderer, super.fontRendererObj, InventoryUtils.cycleItemStack(((IInfusionRecipe)recipe).getRecipeOutput()).getTooltip(super.mc.thePlayer, super.mc.gameSettings.advancedItemTooltips), mx, my, 11);
           }
  
           String text = StatCollector.translateToLocal("auracore.research_title.infusion");
           int offset = super.fontRendererObj.getStringWidth(text);
           super.fontRendererObj.drawString(text, x + start + 56 - offset / 2, y, 5263440);
           if (recipe != null && recipe instanceof ShapedInfusionCraftingRecipe) {
              rw = ((ShapedInfusionCraftingRecipe)recipe).recipeWidth;
              int h = ((ShapedInfusionCraftingRecipe)recipe).recipeHeight;
              Object[] items = ((ShapedInfusionCraftingRecipe)recipe).recipeItems;
  
              for(i = 0; i < rw && i < 3; ++i) {
                 for(j = 0; j < h && j < 3; ++j) {
                    if (items[i + j * rw] != null) {
                       GL11.glPushMatrix();
                       GL11.glTranslated(0.0D, 0.0D, 100.0D);
                       GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                       RenderHelper.enableGUIStandardItemLighting();
                       GL11.glEnable(2884);
                       itemRenderer.renderItemAndEffectIntoGUI(super.mc.fontRenderer, super.mc.renderEngine, InventoryUtils.cycleItemStack(items[i + j * rw]), x + start + 16 + i * 32, y + 66 + j * 32);
                       itemRenderer.renderItemOverlayIntoGUI(super.mc.fontRenderer, super.mc.renderEngine, InventoryUtils.cycleItemStack(items[i + j * rw]).copy().splitStack(1), x + start + 16 + i * 32, y + 66 + j * 32);
                       RenderHelper.disableStandardItemLighting();
                       GL11.glEnable(2896);
                       GL11.glPopMatrix();
                    }
                 }
              }
  
              for(i = 0; i < rw && i < 3; ++i) {
                 for(j = 0; j < h && j < 3; ++j) {
                    if (items[i + j * rw] != null && mposx >= x + 16 + start + i * 32 && mposy >= y + 66 + j * 32 && mposx < x + 16 + start + i * 32 + 16 && mposy < y + 66 + j * 32 + 16) {
                       List addtext = InventoryUtils.cycleItemStack(items[i + j * rw]).getTooltip(super.mc.thePlayer, super.mc.gameSettings.advancedItemTooltips);
                       Object[] ref = this.findRecipeReference(InventoryUtils.cycleItemStack(items[i + j * rw]));
                       if (ref != null && !((String)ref[0]).equals(this.research.key)) {
                          addtext.add("\u00a78\u00a7o" + StatCollector.translateToLocal("recipe.clickthrough"));
                          this.reference.add(Arrays.asList(mx, my, (String)ref[0], (Integer)ref[1]));
                       }
  
                       this.drawCustomTooltip(this, itemRenderer, super.fontRendererObj, addtext, mx, my, 11);
                    }
                 }
              }
           }
  
           if (recipe != null && recipe instanceof ShapelessInfusionCraftingRecipe) {
              List<ItemStack> items = ((ShapelessInfusionCraftingRecipe)recipe).recipeItems;
  
              for(i = 0; i < items.size() && i < 9; ++i) {
                 if (items.get(i) != null) {
                    GL11.glPushMatrix();
                    GL11.glTranslated(0.0D, 0.0D, 100.0D);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderHelper.enableGUIStandardItemLighting();
                    GL11.glEnable(2884);
                    itemRenderer.renderItemAndEffectIntoGUI(super.mc.fontRenderer, super.mc.renderEngine, InventoryUtils.cycleItemStack(items.get(i)), x + start + 16 + i % 3 * 32, y + 66 + i / 3 * 32);
                    itemRenderer.renderItemOverlayIntoGUI(super.mc.fontRenderer, super.mc.renderEngine, InventoryUtils.cycleItemStack(items.get(i)), x + start + 16 + i % 3 * 32, y + 66 + i / 3 * 32);
                    RenderHelper.disableStandardItemLighting();
                    GL11.glEnable(2896);
                    GL11.glPopMatrix();
                 }
              }
  
              for(i = 0; i < items.size() && i < 9; ++i) {
                 if (items.get(i) != null && mposx >= x + 16 + start + i % 3 * 32 && mposy >= y + 66 + i / 3 * 32 && mposx < x + 16 + start + i % 3 * 32 + 16 && mposy < y + 66 + i / 3 * 32 + 16) {
                    List addtext = InventoryUtils.cycleItemStack(items.get(i)).getTooltip(super.mc.thePlayer, super.mc.gameSettings.advancedItemTooltips);
                    Object[] ref = this.findRecipeReference(InventoryUtils.cycleItemStack(items.get(i)));
                    if (ref != null && !((String)ref[0]).equals(this.research.key)) {
                       addtext.add("\u00a78\u00a7o" + StatCollector.translateToLocal("recipe.clickthrough"));
                       this.reference.add(Arrays.asList(mx, my, (String)ref[0], (Integer)ref[1]));
                    }
  
                    this.drawCustomTooltip(this, itemRenderer, super.fontRendererObj, addtext, mx, my, 11);
                 }
              }
           }
  
           GL11.glPopMatrix();
        }
     }

     private void drawLegacyCruciblePage(int side, int x, int y, int mx, int my, ResearchPage pageParm) {
      CrucibleRecipe rc = null;
      Object tr = null;
      if (pageParm.recipe instanceof Object[]) {
         try {
            tr = ((Object[])((Object[])pageParm.recipe))[this.cycle];
         } catch (Exception var26) {
            this.cycle = 0;
            tr = ((Object[])((Object[])pageParm.recipe))[this.cycle];
         }
      } else {
         tr = pageParm.recipe;
      }

      if (tr instanceof CrucibleRecipe) {
         rc = (CrucibleRecipe)tr;
      }

      if (rc != null) {
         GL11.glPushMatrix();
         int start = side * 152;
         String text = StatCollector.translateToLocal("recipe.type.crucible");
         int offset = super.fontRendererObj.getStringWidth(text);
         super.fontRendererObj.drawString(text, x + start + 56 - offset / 2, y, 5263440);
         UtilsFX.bindTexture(this.tex2);
         GL11.glPushMatrix();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glEnable(3042);
         GL11.glTranslatef((float)(x + start), (float)(y + 28), 0.0F);
         GL11.glScalef(2.0F, 2.0F, 1.0F);
         this.drawTexturedModalRect(0, 0, 0, 3, 56, 17);
         GL11.glTranslatef(0.0F, 32.0F, 0.0F);
         this.drawTexturedModalRect(0, 0, 0, 20, 56, 48);
         GL11.glTranslatef(21.0F, -8.0F, 0.0F);
         this.drawTexturedModalRect(0, 0, 100, 84, 11, 13);
         GL11.glPopMatrix();
         int mposx = mx;
         int mposy = my;
         int total = 0;
         int rows = (rc.aspects.size() - 1) / 3;
         int shift = (3 - rc.aspects.size() % 3) * 10;
         int sx = x + start + 28;
         int sy = y + 96 + 32 - 10 * rows;
         Aspect[] arr$ = rc.aspects.getAspectsSorted();
         int len$ = arr$.length;

         int i$;
         Aspect tag;
         byte m;
         int vx;
         int vy;
         for(i$ = 0; i$ < len$; ++i$) {
            tag = arr$[i$];
            m = 0;
            if (total / 3 >= rows && (rows > 1 || rc.aspects.size() < 3)) {
               m = 1;
            }

            vx = sx + total % 3 * 20 + shift * m;
            vy = sy + total / 3 * 20;
            UtilsFX.drawTag(vx, vy, tag, (float)rc.aspects.getAmount(tag), 0, (double)super.zLevel);
            ++total;
         }

         GL11.glPushMatrix();
         GL11.glTranslated(0.0D, 0.0D, 100.0D);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderHelper.enableGUIStandardItemLighting();
         GL11.glEnable(2884);
         itemRenderer.renderItemAndEffectIntoGUI(super.mc.fontRenderer, super.mc.renderEngine, rc.recipeOutput, x + 48 + start, y + 36);
         itemRenderer.renderItemOverlayIntoGUI(super.mc.fontRenderer, super.mc.renderEngine, rc.recipeOutput, x + 48 + start, y + 36);
         RenderHelper.disableStandardItemLighting();
         GL11.glEnable(2896);
         GL11.glPopMatrix();
         GL11.glPushMatrix();
         GL11.glTranslated(0.0D, 0.0D, 100.0D);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderHelper.enableGUIStandardItemLighting();
         GL11.glEnable(2884);
         UtilsFX.drawTag(x + 26 + start, y + 72, Aspects.VIS, rc.cost, 0, (double)super.zLevel);
         RenderHelper.disableStandardItemLighting();
         GL11.glEnable(2896);
         GL11.glPopMatrix();
         if (mx >= x + 48 + start && my >= y + 36 && mx < x + 48 + start + 16 && my < y + 36 + 16) {
            this.drawCustomTooltip(this, itemRenderer, super.fontRendererObj, rc.recipeOutput.getTooltip(super.mc.thePlayer, super.mc.gameSettings.advancedItemTooltips), mx, my, 11);
         }

         if (mx >= x + 26 + start && my >= y + 72 && mx < x + 26 + start + 16 && my < y + 72 + 16) {
            List addtext = Arrays.asList(Aspects.VIS.getName(), Aspects.VIS.getLocalizedDescription());

            this.drawCustomTooltip(this, itemRenderer, super.fontRendererObj, addtext, mx, my, 11);
         }

         total = 0;
         arr$ = rc.aspects.getAspectsSorted();
         len$ = arr$.length;

         for(i$ = 0; i$ < len$; ++i$) {
            tag = arr$[i$];
            m = 0;
            if (total / 3 >= rows && (rows > 1 || rc.aspects.size() < 3)) {
               m = 1;
            }

            vx = sx + total % 3 * 20 + shift * m;
            vy = sy + total / 3 * 20;
            if (mposx >= vx && mposy >= vy && mposx < vx + 16 && mposy < vy + 16) {
               this.drawCustomTooltip(this, itemRenderer, super.fontRendererObj, Arrays.asList(tag.getName(), tag.getLocalizedDescription()), mx, my, 11);
            }

            ++total;
         }

         GL11.glPopMatrix();
      }

   }

}
