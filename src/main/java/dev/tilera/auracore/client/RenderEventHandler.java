package dev.tilera.auracore.client;

import java.util.Collection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dev.tilera.auracore.client.AuraManagerClient.NodeStats;
import dev.tilera.auracore.helper.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import thaumcraft.client.lib.UtilsFX;

public class RenderEventHandler {

    ResourceLocation tx1 = new ResourceLocation("auracore", "textures/misc/aura_1.png");
    ResourceLocation tx2 = new ResourceLocation("auracore", "textures/misc/aura_2.png");
    ResourceLocation tx3 = new ResourceLocation("auracore", "textures/misc/aura_3.png");
    ResourceLocation tx3p = new ResourceLocation("auracore", "textures/misc/pure.png");
    ResourceLocation tx3d = new ResourceLocation("auracore", "textures/misc/vortex.png");
    ResourceLocation tx3c = new ResourceLocation("auracore", "textures/misc/chaos.png");
    ResourceLocation txlock = new ResourceLocation("auracore", "textures/misc/aura_lock.png");
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderLast(RenderWorldLastEvent event) {
        float partialTicks = event.partialTicks;
        Minecraft mc = Minecraft.getMinecraft();
        if (Minecraft.getMinecraft().renderViewEntity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().renderViewEntity;
            long time = mc.theWorld.getTotalWorldTime();
            if (Utils.hasGoggles(player)) {
                GL11.glPushMatrix();
                GL11.glDepthMask((boolean)false);
                GL11.glEnable((int)3042);
                GL11.glBlendFunc((int)770, (int)1);
                GL11.glDisable((int)2884);
                GL11.glDisable((int)2896);
                this.renderAuraNodes(event, partialTicks, player, time);
                GL11.glDisable((int)3042);
                GL11.glEnable((int)2896);
                GL11.glDepthMask((boolean)true);
                GL11.glPopMatrix();
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void renderAuraNodes(RenderWorldLastEvent event, float partialTicks, EntityPlayer player, long time) {
        GL11.glPushMatrix();
        int limit = 0;
        Collection<NodeStats> col = AuraManagerClient.auraClientList.values();
        for (NodeStats l : col) {
            float px = (float) l.x;
            float py = (float) l.y;
            float pz = (float) l.z;
            int dim = l.dimension;
            short lvl = l.level;
            int key = l.key;
            boolean lock = l.lock;
            byte type = l.type;
            if (limit >= 10 || player.dimension != dim || !(player.getDistanceSq((double)px, (double)py, (double)pz) < 4096.0)) continue;
            ++limit;
            AuraManagerClient.NodeRenderInfo nri = (AuraManagerClient.NodeRenderInfo)AuraManagerClient.auraClientMovementList.get(key);
            if (nri == null) {
                nri = new AuraManagerClient.NodeRenderInfo(px, py, pz);
                AuraManagerClient.auraClientMovementList.put(key, nri);
            }
            float bscale = (float)lvl / 1000.0f;
            nri.x += (px - nri.x) / 50.0f * partialTicks;
            nri.y += (py - nri.y) / 50.0f * partialTicks;
            nri.z += (pz - nri.z) / 50.0f * partialTicks;
            AuraManagerClient.auraClientMovementList.put(key, nri);
            float rad = (float)Math.PI * 2;
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)0.1f);
            GL11.glPushMatrix();
            UtilsFX.bindTexture(tx1);
            float scale = MathHelper.sin((float)(((float)time + nri.x) / 14.0f)) * bscale + bscale * 2.0f;
            float angle = (float)(time % 500L) / 500.0f * rad;
            UtilsFX.renderFacingQuad(nri.x, nri.y, nri.z, angle, scale, 1, 1, 0, partialTicks, 0xFFFFFF);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            UtilsFX.bindTexture(tx2);
            angle = (float)(time % 400L) / -400.0f * rad;
            scale = MathHelper.sin((float)(((float)time + nri.y) / 11.0f)) * bscale + bscale * 2.0f;
            UtilsFX.renderFacingQuad(nri.x, nri.y, nri.z, angle, scale, 1, 1, 0, partialTicks, 0xFFFFFF);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            UtilsFX.bindTexture(tx3);
            angle = (float)(time % 300L) / 300.0f * rad;
            scale = MathHelper.sin((float)(((float)time + nri.z) / 9.0f)) * bscale + bscale * 2.0f;
            UtilsFX.renderFacingQuad(nri.x, nri.y, nri.z, angle, scale, 1, 1, 0, partialTicks, 0xFFFFFF);
            GL11.glPopMatrix();
            if (type == 0) {
                GL11.glPushMatrix();
                UtilsFX.bindTexture(tx1);
                angle = (float)(time % 200L) / -200.0f * rad;
                scale = MathHelper.sin((float)(((float)time + nri.x) / 7.0f)) * bscale / 2.0f + bscale * 2.0f;
                UtilsFX.renderFacingQuad(nri.x, nri.y, nri.z, angle, scale, 1, 1, 0, partialTicks, 0xFFFFFF);
                GL11.glPopMatrix();
            } else {
                GL11.glPushMatrix();
                switch (type) {
                    case 1: {
                        UtilsFX.bindTexture(tx3p);
                        break;
                    }
                    case 2: {
                        GL11.glBlendFunc((int)770, (int)771);
                        UtilsFX.bindTexture(tx3d);
                        break;
                    }
                    case 3: {
                        UtilsFX.bindTexture(tx3c);
                    }
                }
                angle = (float)(time % 90L) / -90.0f * rad;
                scale = MathHelper.sin((float)(((float)time + nri.x) / 10.0f)) * bscale / 4.0f + bscale * 1.75f;
                UtilsFX.renderFacingQuad(nri.x, nri.y, nri.z, angle, scale, 1, 1, 0, partialTicks, 0xFFFFFF);
                GL11.glPopMatrix();
                GL11.glBlendFunc((int)770, (int)1);
            }
            if (!lock) continue;
            GL11.glPushMatrix();
            UtilsFX.bindTexture(txlock);
            UtilsFX.renderFacingQuad(nri.x, nri.y, nri.z, 0.0f, bscale * 3.5f, 1, 1, 0, partialTicks, 0xFFFFFF);
            GL11.glPopMatrix();
        }
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopMatrix();
    }

}
