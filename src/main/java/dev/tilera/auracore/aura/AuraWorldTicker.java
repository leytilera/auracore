package dev.tilera.auracore.aura;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import dev.tilera.auracore.api.AuraNode;
import dev.tilera.auracore.helper.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.INode;

public class AuraWorldTicker {
    public static volatile long timeThisTick = 0L;
    private int age = 0;

    @SubscribeEvent
    public void tickEnd(TickEvent.WorldTickEvent e) {
        if (e.side == Side.SERVER && e.phase == Phase.END) {
        WorldServer world = (WorldServer)e.world;
        int dim = world.provider.dimensionId;
        timeThisTick = System.currentTimeMillis();
        ++this.age;
        List<Integer> updateList = AuraManager.auraUpdateList.get(dim);
        if (updateList == null || updateList.size() == 0) {
            this.populateAuraUpdateList(dim);
            updateList = AuraManager.auraUpdateList.get(dim);
        }
        if (updateList != null && updateList.size() >= 1 + this.age % 20) {
            int limit = Math.max(1, updateList.size() / 20);
            for (int a = 0; a < limit; ++a) {
                int nk = (Integer)updateList.get(0);
                AuraNode node = AuraManager.copyNode(AuraManager.getNode(nk));
                if (node != null) {
                    if (Utils.isChunkLoaded((World)world, MathHelper.floor_double((double)node.xPos), MathHelper.floor_double((double)node.zPos))) {
                        long time;
                        AuraManager.auraCalcQueue.offer(Arrays.asList(new Object[]{node.key, world}));
                        if (AuraManager.markedForTransmission.get(node.key) == null) {
                            AuraManager.markedForTransmission.put(node.key, timeThisTick + (long)world.rand.nextInt(/*Config.nodeRefresh*/ 10 * 1000));
                        }
                        if ((time = ((Long)AuraManager.markedForTransmission.get(node.key)).longValue()) <= timeThisTick) {
                            AuraManager.sendNodePacket(node);
                            AuraManager.markedForTransmission.put(node.key, timeThisTick + (long)(/*Config.nodeRefresh*/ 10 * 1000));
                        }
                    }
                    updateList.remove(0);
                    if (!node.isVirtual || checkLinkedAura(node, world)) {
                        updateList.add(nk);
                    } else{
                        AuraManager.deleteNode(node);
                    }
                    continue;
                }
                updateList.remove(0);
            }
        }
        if (AuraManager.fluxEventList == null) {
            AuraManager.fluxEventList = new ArrayList<>();
        }
        try {
            if (AuraManager.fluxEventList != null && AuraManager.fluxEventList.size() > 0 && AuraManager.fluxEventList.get(0) != null) {
                switch ((Integer)AuraManager.fluxEventList.get(0).get(3)) {
                    case 0: {
                        AuraManager.spawnMinorFluxEvent((World)AuraManager.fluxEventList.get(0).get(0), (AuraNode)AuraManager.fluxEventList.get(0).get(1), (Aspect)AuraManager.fluxEventList.get(0).get(2));
                        break;
                    }
                    case 1: {
                        AuraManager.spawnModerateFluxEvent((World)AuraManager.fluxEventList.get(0).get(0), (AuraNode)AuraManager.fluxEventList.get(0).get(1), (Aspect)AuraManager.fluxEventList.get(0).get(2));
                        break;
                    }
                    case 2: {
                        AuraManager.spawnMajorFluxEvent((World)AuraManager.fluxEventList.get(0).get(0), (AuraNode)AuraManager.fluxEventList.get(0).get(1), (Aspect)AuraManager.fluxEventList.get(0).get(2));
                    }
                }
                AuraManager.fluxEventList.remove(0);
            }
        }
        catch (Exception ex) {
            
        }
    }
    }

    private synchronized void populateAuraUpdateList(int dim) {
        LinkedList<Integer> temp = new LinkedList<Integer>();
        Collection<AuraNode> nodes = AuraManager.auraNodes.values();
        for (AuraNode node : nodes) {
            if (node.dimension != dim) continue;
            temp.add(node.key);
        }
        AuraManager.auraUpdateList.put(dim, temp);
    }

    private boolean checkLinkedAura(AuraNode node, World world) {
        int x = (int) Math.floor(node.xPos);
        int y = (int) Math.floor(node.yPos);
        int z = (int) Math.floor(node.zPos);
        if (!Utils.isChunkLoaded(world, x, z)) return true;
        TileEntity te = world.getTileEntity(x, y, z);
        return te instanceof INode;
    }

}
