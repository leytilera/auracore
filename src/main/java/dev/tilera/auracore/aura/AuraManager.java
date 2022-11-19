package dev.tilera.auracore.aura;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import dev.tilera.auracore.AuraCore;
import dev.tilera.auracore.api.Aspects;
import dev.tilera.auracore.api.AuraNode;
import dev.tilera.auracore.api.EnumNodeType;
import dev.tilera.auracore.helper.Utils;
import dev.tilera.auracore.network.AuraDeletePacket;
import dev.tilera.auracore.network.AuraPacket;
import dev.tilera.auracore.network.AuraTransferFXPacket;
import dev.tilera.auracore.network.NodeZapPacket;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityFireBat;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.lib.world.biomes.BiomeHandler;

public class AuraManager {
    public static ConcurrentHashMap<Integer, AuraNode> auraNodes = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, List<Integer>> auraUpdateList = new ConcurrentHashMap<>();
    public static volatile ConcurrentHashMap<Integer, Collection<Integer>> nodeNeighbours = new ConcurrentHashMap<>();
    public static volatile ConcurrentHashMap<List<Integer>, List<Integer>> nodeChunks = new ConcurrentHashMap<>();
    public static volatile ConcurrentHashMap<Integer, Long> markedForTransmission = new ConcurrentHashMap<>();
    public static volatile ArrayList<List<Object>> fluxEventList = new ArrayList<>();
    public static LinkedBlockingQueue<List<Object>> auraCalcQueue = new LinkedBlockingQueue<>();
    public static LinkedBlockingQueue<Integer> auraDeleteQueue = new LinkedBlockingQueue<>();
    public static LinkedBlockingQueue<NodeChanges> auraUpdateQueue = new LinkedBlockingQueue<>();
    public static Object saveLock = new Object();
    public static NodeIdStorage nodeIdStore = null;

    public static int registerAuraNode(World world, short lvl, EnumNodeType type, int dim, int x, int y, int z) {
        return registerAuraNode(world, lvl, type, dim, x, y, z, false);
    }

    public static int registerAuraNode(World world, short lvl, EnumNodeType type, int dim, int x, int y, int z, boolean virtual) {
        if (nodeIdStore == null) {
            nodeIdStore = new NodeIdStorage(world.getSaveHandler());
        }
        int key = nodeIdStore.getUniqueDataId("tcnode");
        AuraNode node = new AuraNode(key, lvl, type, dim, x, y, z);
        if (virtual) {
            node.isVirtual = true;
            
        }
        auraNodes.put(node.key, node);
        LinkedList<Integer> temp = (LinkedList<Integer>)auraUpdateList.get(dim);
        if (temp == null) {
            temp = new LinkedList<Integer>();
            auraUpdateList.put(dim, temp);
        }
        temp.add(node.key);
        return key;
    }

    public static List<Integer> getNodeNeighbours(int nkey) {
        ArrayList<Integer> neighbours = new ArrayList<>();
        if (nodeNeighbours.get(nkey) != null) {
            neighbours.addAll(nodeNeighbours.get(nkey));
        }
        return neighbours;
    }

    public static List<Integer> getNodeNeighboursNeighbours(int nkey) {
        List<Integer> neighbours = AuraManager.getNodeNeighbours(nkey);
        if (neighbours != null && neighbours.size() > 0) {
            ArrayList<Integer> neighboursNeighbours = new ArrayList<Integer>();
            neighboursNeighbours.addAll(neighbours);
            for (Integer key : neighbours) {
                try {
                    for (Integer key2 : AuraManager.getNodeNeighbours(key)) {
                        if (neighboursNeighbours.contains(key2)) continue;
                        neighboursNeighbours.add(key2);
                    }
                }
                catch (Exception e) {
                }
            }
            return neighboursNeighbours;
        }
        return null;
    }

    public static synchronized void addToAuraUpdateList(AuraNode node) {
        LinkedList<Integer> temp = (LinkedList<Integer>)auraUpdateList.get(node.dimension);
        if (temp == null) {
            temp = new LinkedList<Integer>();
            auraUpdateList.put(node.dimension, temp);
        }
        if (!temp.contains(node.key)) {
            temp.add(node.key);
        }
        auraUpdateList.put(node.dimension, temp);
    }

    public static void generateNodeNeighbours(AuraNode node) {
        int dim = node.dimension;
        synchronized (saveLock) {
            Integer[] updateList = AuraManager.getUpdateList(dim);
            ArrayList<Integer> neighbours = new ArrayList<Integer>();
            for (int a = 0; a < updateList.length && a < updateList.length; ++a) {
                double zd;
                double yd;
                double xd;
                double distSq;
                float influence;
                int nk;
                AuraNode targetNode;
                if (updateList[a] == null || updateList[a] == node.key || (targetNode = AuraManager.copyNode(AuraManager.getNode(nk = updateList[a].intValue()))) == null || !((double)((influence = Math.max((float)node.baseLevel / 4.0f, (float)targetNode.baseLevel / 4.0f)) * influence) >= (distSq = (xd = node.xPos - targetNode.xPos) * xd + (yd = node.yPos - targetNode.yPos) * yd + (zd = node.zPos - targetNode.zPos) * zd))) continue;
                neighbours.add(targetNode.key);
            }
            nodeNeighbours.put(node.key, neighbours);
            int cx = MathHelper.floor_double((double)node.xPos) / 16;
            int cz = MathHelper.floor_double((double)node.zPos) / 16;
            if (nodeChunks.get(Arrays.asList(dim, cx, cz)) != null) {
                ArrayList nds = (ArrayList)nodeChunks.get(Arrays.asList(dim, cx, cz));
                if (!nds.contains(node.key) && nds.size() > 0) {
                    nds.add(node.key);
                    nodeChunks.put(Arrays.asList(dim, cx, cz), nds);
                }
            } else {
                ArrayList<Integer> temp = new ArrayList<Integer>();
                temp.add(node.key);
                nodeChunks.put(Arrays.asList(dim, cx, cz), temp);
            }
        }
    }

    public static void updateNodeNeighbours(AuraNode node) {
        int dim = node.dimension;
        List<Integer> updateList = AuraManager.getNodeNeighboursNeighbours(node.key);
        ArrayList<Integer> newNeighbours = new ArrayList<Integer>();
        ArrayList deadNodes = new ArrayList();
        if (updateList != null && updateList.size() > 0) {
            for (Integer key : updateList) {
                List nlist;
                double zd;
                double yd;
                double xd;
                double distSq;
                AuraNode targetNode = AuraManager.copyNode(AuraManager.getNode(key));
                if (targetNode == null || key == node.key) continue;
                float influence = Math.max((float)node.baseLevel / 4.0f, (float)targetNode.baseLevel / 4.0f);
                if ((double)(influence * influence) >= (distSq = (xd = node.xPos - targetNode.xPos) * xd + (yd = node.yPos - targetNode.yPos) * yd + (zd = node.zPos - targetNode.zPos) * zd)) {
                    newNeighbours.add(targetNode.key);
                    nlist = AuraManager.getNodeNeighbours(targetNode.key);
                    if (nlist.contains(node.key)) continue;
                    nlist.add(node.key);
                    nodeNeighbours.put(targetNode.key, nlist);
                    continue;
                }
                nlist = AuraManager.getNodeNeighbours(targetNode.key);
                int index = nlist.indexOf(node.key);
                if (index <= -1) continue;
                nlist.remove(index);
                nodeNeighbours.put(targetNode.key, nlist);
            }
            nodeNeighbours.put(node.key, newNeighbours);
        } else {
            AuraManager.generateNodeNeighbours(node);
        }
    }

    public static int getClosestAuraWithinRange(World world, double x, double y, double z, double range) {
        int dim = world.provider.dimensionId;
        int cx = MathHelper.floor_double((double)x) / 16;
        int cz = MathHelper.floor_double((double)z) / 16;
        if (world.isRemote) {
            return -1;
        }
        int size = 5;
        int closest = -1;
        double clRange = Double.MAX_VALUE;
        synchronized (saveLock) {
            for (int xx = -size; xx <= size; ++xx) {
                for (int zz = -size; zz <= size; ++zz) {
                    List<Integer> nc = nodeChunks.get(Arrays.asList(dim, cx + xx, cz + zz));
                    if (nc == null || nc.size() <= 0) continue;
                    for (Integer key : nc) {
                        try {
                            double zd;
                            double yd;
                            double xd;
                            double distSq;
                            AuraNode node = AuraManager.copyNode(AuraManager.getNode(key));
                            if (node == null || node.locked || !Utils.isChunkLoaded(world, MathHelper.floor_double((double)node.xPos), MathHelper.floor_double((double)node.zPos)) || !(range * range >= (distSq = (xd = node.xPos - x) * xd + (yd = node.yPos - y) * yd + (zd = node.zPos - z) * zd)) || !(distSq < clRange)) continue;
                            closest = key;
                            clRange = distSq;
                        }
                        catch (Exception e) {}
                    }
                }
            }
        }
        return closest;
    }

    public static ArrayList<Integer> getAurasWithin(World world, double x, double y, double z) {
        int dim = world.provider.dimensionId;
        int cx = MathHelper.floor_double((double)x) / 16;
        int cz = MathHelper.floor_double((double)z) / 16;
        ArrayList<Integer> ret = new ArrayList<Integer>();
        if (world.isRemote) {
            return ret;
        }
        synchronized (saveLock) {
            for (int xx = -16; xx <= 16; ++xx) {
                for (int zz = -16; zz <= 16; ++zz) {
                    List<Integer> nc = nodeChunks.get(Arrays.asList(dim, cx + xx, cz + zz));
                    if (nc == null || nc.size() <= 0) continue;
                    for (Integer key : nc) {
                        try {
                            double zd;
                            double yd;
                            double xd;
                            double distSq;
                            float influence;
                            AuraNode node = AuraManager.copyNode(AuraManager.getNode(key));
                            if (node == null || !Utils.isChunkLoaded(world, MathHelper.floor_double((double)node.xPos), MathHelper.floor_double((double)node.zPos)) || !((double)((influence = (float)node.baseLevel) * influence) >= (distSq = (xd = node.xPos - x) * xd + (yd = node.yPos - y) * yd + (zd = node.zPos - z) * zd))) continue;
                            ret.add(key);
                        }
                        catch (Exception e) {}
                    }
                }
            }
        }
        return ret;
    }

    public static boolean decreaseClosestAura(World world, double x, double y, double z, int amount) {
        if (amount == 0) {
            return true;
        }
        return AuraManager.decreaseClosestAura(world, x, y, z, amount, true);
    }

    public static boolean decreaseClosestAura(World world, double x, double y, double z, int amount, boolean doit) {
        AuraNode node;
        if (world.isRemote) {
            return false;
        }
        int dim = world.provider.dimensionId;
        ArrayList<Integer> nodes = AuraManager.getAurasWithin(world, x, y, z);
        int total = 0;
        ArrayList<List<Number>> sortednodes = new ArrayList<List<Number>>();
        ArrayList<List<Integer>> depnodes = new ArrayList<List<Integer>>();
        for (Integer n : nodes) {
            int a;
            double zd;
            double yd;
            double xd;
            double distSq;
            float influence;
            node = AuraManager.getNode(n);
            if (node == null || node.level <= 0 || !((double)((influence = (float)node.baseLevel / 4.0f) * influence) >= (distSq = (xd = node.xPos - x) * xd + (yd = node.yPos - y) * yd + (zd = node.zPos - z) * zd))) continue;
            if (sortednodes.size() == 0) {
                sortednodes.add(Arrays.asList(distSq, node.key));
                continue;
            }
            for (a = 0; !(a > sortednodes.size() || a < sortednodes.size() && (Double)((List)sortednodes.get(a)).get(0) > distSq); ++a) {
            }
            if (a < sortednodes.size()) {
                sortednodes.add(a, Arrays.asList(distSq, node.key));
                continue;
            }
            sortednodes.add(Arrays.asList(distSq, node.key));
        }
        if (sortednodes.size() == 0) {
            return false;
        }
        for (List<Number> list : sortednodes) {
            node = AuraManager.getNode((Integer)list.get(1));
            if (node != null && node.level > 0) {
                if (node.level >= amount - total) {
                    depnodes.add(Arrays.asList(amount - total, node.key));
                    total += amount - total;
                    break;
                }
                depnodes.add(Arrays.asList((int)node.level, node.key));
                total += node.level;
            }
            if (amount - total != 0) continue;
            break;
        }
        if (total == amount) {
            for (List<Integer> list : depnodes) {
                int amt = (Integer)list.get(0);
                AuraNode node2 = AuraManager.getNode((Integer)list.get(1));
                if (node2 == null || !doit) continue;
                AuraManager.queueNodeChanges(node2.key, -amt, 0, false, null, 0.0f, 0.0f, 0.0f);
            }
            return true;
        }
        return false;
    }

    public static boolean increaseLowestAura(World world, double x, double y, double z, int amount) {
        if (world.isRemote) {
            return false;
        }
        int dim = world.provider.dimensionId;
        ArrayList<Integer> nodes = AuraManager.getAurasWithin(world, x, y, z);
        int n = Integer.MAX_VALUE;
        AuraNode lowest = null;
        for (Integer nk : nodes) {
            double zd;
            double yd;
            double xd;
            double distSq;
            float influence;
            short s = Short.MAX_VALUE;
            AuraNode node = AuraManager.getNode(nk);
            if (node == null || node.level >= s || !((double)((influence = (float)node.baseLevel / 4.0f) * influence) >= (distSq = (xd = node.xPos - x) * xd + (yd = node.yPos - y) * yd + (zd = node.zPos - z) * zd))) continue;
            lowest = node;
            s = node.level;
        }
        if (lowest != null) {
            AuraNode node = AuraManager.getNode(lowest.key);
            if (node != null) {
                AuraManager.queueNodeChanges(node.key, amount, 0, false, null, 0.0f, 0.0f, 0.0f);
            }
            return true;
        }
        return false;
    }

    public static boolean increaseLowestAuraWithLimit(World world, double x, double y, double z, int amount, float limit) {
        if (world.isRemote) {
            return false;
        }
        int dim = world.provider.dimensionId;
        ArrayList<Integer> nodes = AuraManager.getAurasWithin(world, x, y, z);
        int n = Integer.MAX_VALUE;
        AuraNode lowest = null;
        for (Integer nk : nodes) {
            double zd;
            double yd;
            double xd;
            double distSq;
            float influence;
            short s = Short.MAX_VALUE;
            AuraNode node = AuraManager.getNode(nk);
            if (node == null || node.level >= s || !((float)node.level < (float)node.baseLevel * limit) || !((double)((influence = (float)node.baseLevel / 4.0f) * influence) >= (distSq = (xd = node.xPos - x) * xd + (yd = node.yPos - y) * yd + (zd = node.zPos - z) * zd))) continue;
            lowest = node;
            s = node.level;
        }
        if (lowest != null) {
            AuraNode node = AuraManager.getNode(lowest.key);
            if (node != null) {
                AuraManager.queueNodeChanges(node.key, amount, 0, false, null, 0.0f, 0.0f, 0.0f);
            }
            return true;
        }
        return false;
    }

    public static boolean auraNearby(int dim, int x, int y, int z, int range) {
        Collection<AuraNode> col = auraNodes.values();
        for (AuraNode an : col) {
            float pz;
            double zd;
            float py;
            double yd;
            float px;
            double xd;
            double distSq;
            if (dim != an.dimension || !((distSq = (xd = (double)((px = (float)an.xPos) - (float)x + 0.5f)) * xd + (yd = (double)((py = (float)an.yPos) - (float)y + 0.5f)) * yd + (zd = (double)((pz = (float)an.zPos) - (float)z + 0.5f)) * zd) < (double)(range * range))) continue;
            return true;
        }
        return false;
    }

    public static boolean specificAuraTypeNearby(int dim, int x, int y, int z, EnumNodeType type, int range) {
        Collection<AuraNode> col = auraNodes.values();
        for (AuraNode an : col) {
            float pz;
            double zd;
            float py;
            double yd;
            float px;
            double xd;
            double distSq;
            if (dim != an.dimension || an.type != type || !((distSq = (xd = (double)((px = (float)an.xPos) - (float)x + 0.5f)) * xd + (yd = (double)((py = (float)an.yPos) - (float)y + 0.5f)) * yd + (zd = (double)((pz = (float)an.zPos) - (float)z + 0.5f)) * zd) < (double)(range * range))) continue;
            return true;
        }
        return false;
    }

    public static void sendNodePacket(AuraNode node) {
        AuraCore.CHANNEL.sendToAllAround(new AuraPacket(node), new TargetPoint(node.dimension, node.xPos, node.yPos, node.zPos, Math.max(32.0f, (float)node.baseLevel / 4.0f)));
    }

    public static void sendNodeTransferFXPacket(AuraNode node, AuraNode tnode, double distance) {
        double xx = (node.xPos + tnode.xPos) / 2.0;
        double yy = (node.yPos + tnode.yPos) / 2.0;
        double zz = (node.zPos + tnode.zPos) / 2.0;
        AuraCore.CHANNEL.sendToAllAround(new AuraTransferFXPacket(node, tnode), new TargetPoint(node.dimension, xx, yy, zz, MathHelper.sqrt_double((double)distance) + 32.0f));
    }

    public static void sendNodeDeletionPacket(AuraNode node) {
        AuraCore.CHANNEL.sendToAll(new AuraDeletePacket(node));
    }

    public static void addFluxToClosest(World world, float x, float y, float z, AspectList tags) {
        if (world.isRemote) {
            return;
        }
        int dim = world.provider.dimensionId;
        ArrayList<Integer> nodes = AuraManager.getAurasWithin(world, x, y, z);
        if (nodes == null || nodes.size() == 0) {
            return;
        }
        boolean total = false;
        double cDist = Double.MAX_VALUE;
        int cKey = -1;
        for (Integer nk : nodes) {
            double zd;
            double yd;
            double xd;
            double distSq;
            float influence;
            AuraNode node = AuraManager.getNode(nk);
            if (node == null || !((double)((influence = (float)node.baseLevel / 4.0f) * influence) >= (distSq = (xd = node.xPos - (double)x) * xd + (yd = node.yPos - (double)y) * yd + (zd = node.zPos - (double)z) * zd)) || !(distSq < cDist)) continue;
            cDist = distSq;
            cKey = nk;
        }
        if (cKey < 0) {
            return;
        }
        AuraNode node = AuraManager.getNode(cKey);
        if (node != null) {
            AspectList flux = new AspectList();
            for (Aspect tag : tags.getAspects()) {
                if (tags.getAmount(tag) <= 0) continue;
                flux.add(tag, tags.getAmount(tag));
            }
            if (flux.size() > 0) {
                AuraManager.queueNodeChanges(node.key, 0, 0, false, flux, 0.0f, 0.0f, 0.0f);
            }
        }
    }

    public static void removeRandomFlux(World world, AuraNode node, int amount) {
        AspectList flux = new AspectList();
        for (int a = 0; a < amount; ++a) {
            int i$ = 0;
            Aspect[] arr$ = node.flux.getAspects();
            int len$ = arr$.length;
            if (i$ >= len$) continue;
            Aspect tg = arr$[i$];
            if (world.rand.nextInt(5) != 0 || -flux.getAmount(tg) >= node.flux.getAmount(tg)) continue;
            flux.add(tg, -1);
        }
        if (flux.size() > 0) {
            AuraManager.queueNodeChanges(node.key, 0, 0, false, flux, 0.0f, 0.0f, 0.0f);
        }
    }

    public static void addRandomFlux(World world, AuraNode node, int amount) {
        AspectList flux = new AspectList();
        block20: for (int a = 0; a < amount; ++a) {
            if (world.rand.nextInt(5) != 0) continue;
            switch (world.rand.nextInt(3)) {
                case 0: {
                    int biome = world.getBiomeGenForCoords((int)((int)node.xPos), (int)((int)node.zPos)).biomeID;
                    flux.add(BiomeHandler.getRandomBiomeTag(biome, world.rand), 1);
                    continue block20;
                }
                case 1: {
                    switch (world.rand.nextInt(20)) {
                        case 0: 
                        case 1: {
                            flux.add(Aspect.AIR, 1);
                            continue block20;
                        }
                        case 2: {
                            flux.add(Aspect.MOTION, 1);
                            continue block20;
                        }
                        case 3: 
                        case 4: {
                            flux.add(Aspect.FIRE, 1);
                            continue block20;
                        }
                        case 5: {
                            flux.add(Aspect.ENERGY, 1);
                            continue block20;
                        }
                        case 6: 
                        case 7: {
                            flux.add(Aspect.WATER, 1);
                            continue block20;
                        }
                        case 8: {
                            flux.add(Aspect.COLD, 1);
                            continue block20;
                        }
                        case 9: 
                        case 10: {
                            flux.add(Aspect.EARTH, 1);
                            continue block20;
                        }
                        case 11: {
                            flux.add(Aspects.ROCK, 1);
                            continue block20;
                        }
                        case 12: {
                            flux.add(Aspect.POISON, 1);
                            continue block20;
                        }
                        case 13: {
                            flux.add(Aspect.PLANT, 1);
                            continue block20;
                        }
                        case 14: {
                            flux.add(Aspect.TREE, 1);
                            continue block20;
                        }
                        case 15: 
                        case 16: {
                            flux.add(Aspect.MAGIC, 1);
                            continue block20;
                        }
                        case 17: {
                            flux.add(Aspect.BEAST, 1);
                            continue block20;
                        }
                        case 18: {
                            flux.add(Aspect.DEATH, 1);
                            continue block20;
                        }
                    }
                    flux.add(Aspect.WEATHER, 1);
                    continue block20;
                }
                default: {
                    flux.add(Aspects.FLUX, 1);
                }
            }
        }
        if (flux.size() > 0) {
            AuraManager.queueNodeChanges(node.key, 0, 0, false, flux, 0.0f, 0.0f, 0.0f);
        }
    }

    public static void deleteNode(AuraNode node) {
        auraDeleteQueue.add(node.key);
        sendNodeDeletionPacket(node);
    }

    public static boolean spawnMajorFluxEvent(World world, AuraNode node, Aspect fluxTag) {
        boolean success = false;
        if (fluxTag == Aspects.PURE) {
            success = true;
        }
        if (success) {
            AuraManager.queueNodeChanges(node.key, 0, 0, false, new AspectList().add(fluxTag, -50), 0.0f, 0.0f, 0.0f);
        }
        return success;
    }

    public static boolean spawnModerateFluxEvent(World world, AuraNode node, Aspect fluxTag) {
        boolean success = false;
        if (fluxTag == Aspects.PURE) {
            success = true;
        } else if (fluxTag == Aspect.DEATH) {
            success = AuraManager.spawnGiant(world, node);
        }
        if (success) {
            AuraManager.queueNodeChanges(node.key, 0, 0, false, new AspectList().add(fluxTag, -25), 0.0f, 0.0f, 0.0f);
        }
        return success;
    }

    public static boolean spawnMinorFluxEvent(World world, AuraNode node, Aspect fluxTag) {
        boolean success = false;
        if (world.rand.nextInt(3) == 0) {
            success = AuraManager.spawnWisp(world, node, fluxTag);
        } else {
            if (fluxTag == Aspect.ENERGY || fluxTag == Aspects.DESTRUCTION) {
                success = AuraManager.spawnLightning(world, node);
            } else if (fluxTag == Aspect.POISON || fluxTag == Aspects.INSECT) {
                success = AuraManager.poisonCreature(world, node, Potion.poison.id);
            } else if (fluxTag == Aspect.DARKNESS || fluxTag == Aspect.VOID) {
                success = AuraManager.poisonCreature(world, node, Potion.blindness.id);
            } else if (fluxTag == Aspect.ARMOR) {
                success = AuraManager.poisonCreature(world, node, Potion.resistance.id);
            } else if (fluxTag == Aspect.MOTION) {
                success = AuraManager.poisonCreature(world, node, Potion.moveSpeed.id);
            } else if (fluxTag == Aspect.FLIGHT) {
                success = AuraManager.poisonCreature(world, node, Potion.jump.id);
            } else if (fluxTag == Aspect.TOOL) {
                success = AuraManager.poisonCreature(world, node, Potion.digSpeed.id);
            } else if (fluxTag == Aspects.ROCK) {
                success = AuraManager.poisonCreature(world, node, Potion.digSlowdown.id);
            } else if (fluxTag == Aspect.COLD) {
                success = AuraManager.poisonCreature(world, node, Potion.moveSlowdown.id);
            } else if (fluxTag == Aspects.SOUND || fluxTag == Aspect.MIND || fluxTag == Aspects.FUNGUS) {
                success = AuraManager.poisonCreature(world, node, Potion.confusion.id);
            } else if (fluxTag == Aspects.EVIL) {
                success = AuraManager.spawnEvil(world, node);
            } else if (fluxTag == Aspect.DEATH) {
                success = AuraManager.spawnDeath(world, node);
            } else if (fluxTag == Aspect.FIRE) {
                success = AuraManager.spawnFire(world, node);
            } else if (fluxTag == Aspect.CROP || fluxTag == Aspect.PLANT || fluxTag == Aspect.TREE) {
                success = AuraManager.promoteGrowth(world, node);
            } else if (fluxTag == Aspects.PURE) {
                success = true;
            }
        }
        if (success) {
            AuraManager.queueNodeChanges(node.key, 0, 0, false, new AspectList().add(fluxTag, -10), 0.0f, 0.0f, 0.0f);
        }
        return success;
    }

    private static boolean promoteGrowth(World world, AuraNode node) {
        int fuzz = (int)((float)node.baseLevel / 8.0f);
        double xx = node.xPos + (double)world.rand.nextInt(fuzz) - (double)world.rand.nextInt(fuzz);
        double zz = node.zPos + (double)world.rand.nextInt(fuzz) - (double)world.rand.nextInt(fuzz);
        double yy = Utils.getFirstUncoveredBlockHeight(world, (int)xx, (int)zz);
        if (!Utils.isChunkLoaded(world, MathHelper.floor_double((double)xx), MathHelper.floor_double((double)zz))) {
            return false;
        }
        return Utils.useBonemealAtLoc(world, (int)xx, (int)yy, (int)zz);
    }

    private static boolean poisonCreature(World world, AuraNode node, int type) {
        boolean did = false;
        List<EntityLivingBase> ents = world.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(node.xPos - 1.0, node.yPos - 1.0, node.zPos - 1.0, node.xPos + 1.0, node.yPos + 1.0, node.zPos + 1.0).expand((double)((float)node.baseLevel / 4.0f), (double)((float)node.baseLevel / 4.0f), (double)((float)node.baseLevel / 4.0f)));
        if (ents.size() > 0) {
            for (int a = 0; a < 3 && ents.size() >= 1; ++a) {
                int q = world.rand.nextInt(ents.size());
                EntityLivingBase el = ents.get(q);
                if (!Utils.isChunkLoaded(world, MathHelper.floor_double((double)el.posX), MathHelper.floor_double((double)el.posZ))) continue;
                el.addPotionEffect(new PotionEffect(type, 100 + world.rand.nextInt(200), 0));
                if (el instanceof EntityPlayer) {
                    ((EntityPlayer)el).addChatMessage(new ChatComponentText("\u00a72\u00a7oThe air around you suddenly becomes suffused with strange energies."));
                }
                did = true;
                ents.remove(q);
            }
        }
        return did;
    }

    private static boolean spawnLightning(World world, AuraNode node) {
        List<EntityLivingBase> ents = world.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(node.xPos - 1.0, node.yPos - 1.0, node.zPos - 1.0, node.xPos + 1.0, node.yPos + 1.0, node.zPos + 1.0).expand((double)((float)node.baseLevel / 4.0f / 2.0f), (double)((float)node.baseLevel / 4.0f / 2.0f), (double)((float)node.baseLevel / 4.0f / 2.0f)));
        if (ents.size() > 0) {
            for (EntityLivingBase ent : ents) {
                if (!Utils.isChunkLoaded(world, MathHelper.floor_double((double)ent.posX), MathHelper.floor_double((double)ent.posZ))) continue;
                AuraCore.CHANNEL.sendToAllAround(new NodeZapPacket(node.xPos, node.yPos, node.zPos, ent), new TargetPoint(ent.dimension, ent.posX, ent.posY, ent.posZ, 64.0));
                world.playSoundEffect(node.xPos, node.yPos, node.zPos, "thaumcraft.zap", 1.0f, 1.1f);
                ent.attackEntityFrom(DamageSource.magic, 5);
                return true;
            }
        }
        return false;
    }

    private static boolean spawnGiant(World world, AuraNode node) {
        boolean spawn;
        int fuzz = (int)((float)node.baseLevel / 4.0f) / 3;
        double xx = node.xPos + (double)world.rand.nextInt(fuzz) - (double)world.rand.nextInt(fuzz);
        double zz = node.zPos + (double)world.rand.nextInt(fuzz) - (double)world.rand.nextInt(fuzz);
        double yy = world.getHeightValue((int)xx, (int)zz) + 5;
        if (!Utils.isChunkLoaded(world, MathHelper.floor_double((double)xx), MathHelper.floor_double((double)zz))) {
            return false;
        }
        EntityGiantBrainyZombie zombie = new EntityGiantBrainyZombie(world);
        zombie.setLocationAndAngles(xx, yy, zz, world.rand.nextFloat() * 360.0f, 0.0f);
        boolean bl = spawn = zombie.getCanSpawnHere() && world.spawnEntityInWorld((Entity)zombie);
        if (spawn) {
            Utils.sendChatNearby(world, xx, yy, zz, 64.0, "\u00a75\u00a7oA nearby node spews forth something foul.");
        }
        return spawn;
    }

    private static boolean spawnFire(World world, AuraNode node) {
        int fuzz = (int)((float)node.baseLevel / 4.0f) / 3;
        double xx = node.xPos + (double)world.rand.nextInt(fuzz) - (double)world.rand.nextInt(fuzz);
        double yy = node.yPos + (double)world.rand.nextInt(fuzz) - (double)world.rand.nextInt(fuzz);
        double zz = node.zPos + (double)world.rand.nextInt(fuzz) - (double)world.rand.nextInt(fuzz);
        if (!Utils.isChunkLoaded(world, MathHelper.floor_double((double)xx), MathHelper.floor_double((double)zz))) {
            return false;
        }
        EntityFireBat firebat = new EntityFireBat(world);
        firebat.setLocationAndAngles(xx, yy, zz, world.rand.nextFloat() * 360.0f, 0.0f);
        firebat.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 32000, 0));
        boolean spawn = firebat.getCanSpawnHere() && world.spawnEntityInWorld((Entity)firebat);
        return spawn;
    }

    private static boolean spawnDeath(World world, AuraNode node) {
        double yy;
        int fuzz = (int)((float)node.baseLevel / 4.0f) / 3;
        double xx = node.xPos + (double)world.rand.nextInt(fuzz) - (double)world.rand.nextInt(fuzz);
        double zz = node.zPos + (double)world.rand.nextInt(fuzz) - (double)world.rand.nextInt(fuzz);
        if (!Utils.isChunkLoaded(world, MathHelper.floor_double((double)xx), MathHelper.floor_double((double)zz))) {
            return false;
        }
        EntityBrainyZombie zombie = new EntityBrainyZombie(world);
        for (yy = node.yPos + (double)world.rand.nextInt(fuzz) - (double)world.rand.nextInt(fuzz); world.isAirBlock((int)xx, (int)yy - 2, (int)zz) && yy > 10.0; yy -= 1.0) {
        }
        zombie.setLocationAndAngles(xx, yy, zz, world.rand.nextFloat() * 360.0f, 0.0f);
        zombie.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 32000, 0));
        boolean spawn = zombie.getCanSpawnHere() && world.spawnEntityInWorld((Entity)zombie);
        return spawn;
    }

    private static boolean spawnEvil(World world, AuraNode node) {
        double yy;
        int fuzz = (int)((float)node.baseLevel / 4.0f) / 3;
        double xx = node.xPos + (double)world.rand.nextInt(fuzz) - (double)world.rand.nextInt(fuzz);
        double zz = node.zPos + (double)world.rand.nextInt(fuzz) - (double)world.rand.nextInt(fuzz);
        if (!Utils.isChunkLoaded(world, MathHelper.floor_double((double)xx), MathHelper.floor_double((double)zz))) {
            return false;
        }
        EntityWitch witch = new EntityWitch(world);
        for (yy = node.yPos + (double)world.rand.nextInt(fuzz) - (double)world.rand.nextInt(fuzz); world.isAirBlock((int)xx, (int)yy - 2, (int)zz) && yy > 10.0; yy -= 1.0) {
        }
        witch.setLocationAndAngles(xx, yy + 0.5, zz, world.rand.nextFloat() * 360.0f, 0.0f);
        boolean spawn = witch.getCanSpawnHere() && world.spawnEntityInWorld((Entity)witch);
        return spawn;
    }

    private static boolean spawnWisp(World world, AuraNode node, Aspect type) {
        if (!Utils.isChunkLoaded(world, MathHelper.floor_double((double)node.xPos), MathHelper.floor_double((double)node.zPos))) {
            return false;
        }
        EntityWisp wisp = new EntityWisp(world);
        wisp.setLocationAndAngles(node.xPos, node.yPos, node.zPos, world.rand.nextFloat() * 360.0f, 0.0f);
        //wisp.type = type;
        wisp.playLivingSound();
        return wisp.getCanSpawnHere() && world.spawnEntityInWorld((Entity)wisp);
    }

    public static synchronized AuraNode getNode(int key) {
        return (AuraNode)auraNodes.get(key);
    }

    public static synchronized AuraNode getNodeCopy(int key) {
        return AuraManager.copyNode((AuraNode)auraNodes.get(key));
    }

    public static synchronized Integer[] getUpdateList(int dim) {
        int count = 0;
        while (auraUpdateList.get(dim) != null && count < 10) {
            try {
                ++count;
                return ((List<Integer>)auraUpdateList.get(dim)).toArray(new Integer[]{0});
            }
            catch (Exception exception) {
            }
        }
        return null;
    }

    public static void queueNodeChanges(int key, int levelMod, int baseMod, boolean toggleLock, AspectList flx, float x, float y, float z) {
        NodeChanges nc = new NodeChanges(key, levelMod, baseMod, toggleLock, flx, x, y, z);
        auraUpdateQueue.add(nc);
    }

    public static AuraNode copyNode(AuraNode in) {
        try {
            AuraNode out = new AuraNode();
            out.key = in.key;
            out.level = in.level;
            out.baseLevel = in.baseLevel;
            out.type = in.type;
            AspectList outflux = new AspectList();
            for (Aspect tag : in.flux.getAspects()) {
                outflux.add(tag, in.flux.getAmount(tag));
            }
            out.flux = outflux;
            out.dimension = in.dimension;
            out.xPos = in.xPos;
            out.yPos = in.yPos;
            out.zPos = in.zPos;
            out.locked = in.locked;
            out.isVirtual = in.isVirtual;
            return out;
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    public static void replaceNode(AuraNode in, AuraNode out) {
        out.key = in.key;
        out.level = in.level;
        out.baseLevel = in.baseLevel;
        out.type = in.type;
        out.flux = in.flux;
        out.dimension = in.dimension;
        out.xPos = in.xPos;
        out.yPos = in.yPos;
        out.zPos = in.zPos;
        out.locked = in.locked;
        out.isVirtual = in.isVirtual;
    }

    public static class NodeChanges {
        int key = 0;
        int levelMod = 0;
        int baseMod = 0;
        boolean lock = false;
        AspectList flux = null;
        float motionX;
        float motionY;
        float motionZ;

        NodeChanges(int k, int l, int b, boolean lo, AspectList ot, float x, float y, float z) {
            this.key = k;
            this.levelMod = l;
            this.baseMod = b;
            this.lock = lo;
            this.flux = ot;
            this.motionX = x;
            this.motionY = y;
            this.motionZ = z;
        }
    }
}


