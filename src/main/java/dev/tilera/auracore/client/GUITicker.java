package dev.tilera.auracore.client;

import java.util.Collection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dev.tilera.auracore.client.AuraManagerClient.NodeStats;
import dev.tilera.auracore.helper.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.fx.bolt.FXLightningBolt;
import thaumcraft.client.lib.UtilsFX;

public class GUITicker {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void hudTick(RenderTickEvent event) {
        if (event.phase == Phase.END && event.side == Side.CLIENT) {
            EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
            Minecraft mc = FMLClientHandler.instance().getClient();
            long time = System.currentTimeMillis();
            if (player != null && mc.inGameHasFocus && Minecraft.isGuiEnabled()) {
                if (Utils.hasGoggles(player)) {
                    renderGogglesHUD(event.renderTickTime, player, time);
                    int limit = 0;
                Collection<NodeStats> col = AuraManagerClient.auraClientList.values();
                for (NodeStats l : col) {
                    float px = (float) l.x;
                    float py = (float) l.y;
                    float pz = (float) l.z;
                    int dim = l.dimension;
                    short lvl = l.level;
                    int key = l.key;
                    int flux = l.flux;
                    if (flux <= 0 || limit >= 10 || player.dimension != dim) continue;
                    ++limit;
                    AuraManagerClient.NodeRenderInfo nri = (AuraManagerClient.NodeRenderInfo)AuraManagerClient.auraClientMovementList.get(key);
                    if (nri == null) {
                        nri = new AuraManagerClient.NodeRenderInfo(px, py, pz);
                        AuraManagerClient.auraClientMovementList.put(key, nri);
                    }
                    nri.x += (px - nri.x) / 50.0f;
                    nri.y += (py - nri.y) / 50.0f;
                    nri.z += (pz - nri.z) / 50.0f;
                    AuraManagerClient.auraClientMovementList.put(key, nri);
                    if (player.worldObj.rand.nextInt(1000) >= flux) continue;
                    FXLightningBolt bolt = new FXLightningBolt(player.worldObj, nri.x, nri.y, nri.z, nri.x + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 5.0f, nri.y + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 5.0f, nri.z + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 5.0f, player.worldObj.rand.nextLong(), 10, 2.0f, 5);
                    bolt.defaultFractal();
                    bolt.setType(5);
                    bolt.finalizeBolt();
                }
                }
            }
        }
    }
    
    @SideOnly(value=Side.CLIENT)
    public void renderGogglesHUD(float partialTicks, EntityPlayer player, long time) {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushMatrix();
        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        GL11.glClear((int)256);
        GL11.glMatrixMode((int)5889);
        GL11.glLoadIdentity();
        GL11.glOrtho((double)0.0, (double)sr.getScaledWidth_double(), (double)sr.getScaledHeight_double(), (double)0.0, (double)1000.0, (double)3000.0);
        GL11.glMatrixMode((int)5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef((float)0.0f, (float)0.0f, (float)-2000.0f);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3008);
        int k = sr.getScaledWidth();
        int l = sr.getScaledHeight();
        double closestX = 0.0f;
        double closestY = 0.0f;
        double closestZ = 0.0f;
        double closestDistance = Double.MAX_VALUE;
        short closestLevel = 0; 
        int closestBase = 0;
        int closestKey = 0; 
        int closestFlux = 0; 
        boolean foundSomething = false;
        Collection<NodeStats> col = AuraManagerClient.auraClientList.values();
        for (NodeStats stats : col) {
            int dim = stats.dimension;
            if (player.dimension != dim) continue;
            double px = stats.x;
            double py = stats.y;
            double pz = stats.z;
            short lvl = stats.level;
            short base = stats.base;
            int key = stats.key;
            int flux = stats.flux;
            double xd = px - player.posX;
            double yd = py - player.posY;
            double zd = pz - player.posZ;
            double distSq = xd * xd + yd * yd + zd * zd;
            if (!(distSq < closestDistance)) continue;
            closestDistance = distSq;
            closestX = px;
            closestY = py;
            closestZ = pz;
            closestLevel = lvl;
            closestBase = base;
            closestKey = key;
            closestFlux = flux;
            foundSomething = true;
        }
        if (foundSomething) {
            int h = (int)((float)closestLevel / ((float)closestBase * 2.0f) * 48.0f);
            mc.ingameGUI.drawString(mc.fontRenderer, "A: " + closestLevel + "/" + closestBase, 18, l - 28, 0xFFFFFF);
            String msg = "None";
            int color = 0x888888;
            if (closestFlux > 0) {
                msg = "Minimal";
                color = 0x8888AA;
            }
            if (closestFlux > 50) {
                msg = "Moderate";
                color = 0xAA8888;
            }
            if (closestFlux > 150) {
                msg = "High";
                color = 0xFF8888;
            }
            if (closestFlux > 500) {
                msg = "Dangerous";
                color = 0xFF1111;
            }
            mc.ingameGUI.drawString(mc.fontRenderer, "F: " + msg, 18, l - 18, color);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            mc.renderEngine.bindTexture(new ResourceLocation("auracore", "textures/misc/particles.png"));
            UtilsFX.drawTexturedQuad(6, l - 9 - h, 224, 48 - h, 8, h, -91.0);
            UtilsFX.drawTexturedQuad(5, l - 61, 240, 0, 10, 56, -90.0);
            short prevLevel = (AuraManagerClient.auraClientHistory.get(closestKey)).level;
            int prevFlux = (AuraManagerClient.auraClientHistory.get(closestKey)).flux;
            if (prevLevel < closestLevel) {
                UtilsFX.drawTexturedQuad(6, l - 37, 208, 0, 8, 8, -90.0);
            } else if (prevLevel > closestLevel) {
                UtilsFX.drawTexturedQuad(6, l - 37, 216, 0, 8, 8, -90.0);
            }
            if (prevFlux < closestFlux) {
                UtilsFX.drawTexturedQuad(2, l - (65 - (int)(Minecraft.getSystemTime() % 1250L) / 50 * 2), 16 * ((int)(Minecraft.getSystemTime() % 700L) / 50), 32, 16, 16, -90.0);
            }
        }
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2929);
        GL11.glEnable((int)3008);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopMatrix();
    }

}
