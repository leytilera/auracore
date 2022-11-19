package dev.tilera.auracore.aura;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.tilera.auracore.api.AuraNode;
import net.minecraft.util.MathHelper;
import thaumcraft.api.aspects.Aspect;

public class AuraUpdateThread
        implements Runnable {

    @Override
    public void run() {
        try {
            while (true) {
                boolean done = false;
                int count = 0;
                AuraManager.NodeChanges nc = (AuraManager.NodeChanges) AuraManager.auraUpdateQueue.take();
                Object object = AuraManager.saveLock;
                synchronized (object) {
                    while (AuraManager.getNode(nc.key) != null && !done && count < 10) {
                        ++count;
                        try {
                            int cz;
                            AuraNode node = AuraManager.getNode(nc.key);
                            if (node == null)
                                continue;
                            node.level = (short) (node.level + nc.levelMod);
                            node.baseLevel = (short) (node.baseLevel + nc.baseMod);
                            if (nc.lock) {
                                node.locked = !node.locked;
                            }
                            if (node.level < 0) {
                                node.level = 0;
                            }
                            if (node.baseLevel < 0) {
                                node.baseLevel = 0;
                            }
                            if (nc.flux != null) {
                                for (Aspect tag : nc.flux.getAspects()) {
                                    if (nc.flux.getAmount(tag) > 0) {
                                        node.flux.add(tag, nc.flux.getAmount(tag));
                                        continue;
                                    }
                                    node.flux.reduce(tag, -nc.flux.getAmount(tag)); // TODO:WTF
                                }
                            }
                            if (node.flux.size() > 0) {
                                ArrayList<Aspect> dt = new ArrayList<>();
                                ArrayList<Aspect> red = new ArrayList<>();
                                for (Aspect tag : node.flux.getAspects()) {
                                    if (node.flux.getAmount(tag) <= 0) {
                                        dt.add(tag);
                                        continue;
                                    }
                                    if (node.flux.getAmount(tag) <= 100)
                                        continue;
                                    red.add(tag);
                                }
                                if (red.size() > 0) {
                                    for (Aspect tag : red) {
                                        node.flux.reduce(tag, node.flux.getAmount(tag) - 100);
                                    }
                                }
                                if (dt.size() > 0) {
                                    for (Aspect tag : dt) {
                                        node.flux.remove(tag);
                                    }
                                }
                            }
                            if (nc.motionX != 0.0f || nc.motionY != 0.0f || nc.motionZ != 0.0f) {
                                int cx = MathHelper.floor_double((double) node.xPos) / 16;
                                cz = MathHelper.floor_double((double) node.zPos) / 16;
                                if (AuraManager.nodeChunks.get(Arrays.asList(node.dimension, cx, cz)) != null) {
                                    try {
                                        List<Integer> nds = AuraManager.nodeChunks
                                                .get(Arrays.asList(node.dimension, cx, cz));
                                        nds.remove(nds.indexOf(node.key));
                                        AuraManager.nodeChunks.put(Arrays.asList(node.dimension, cx, cz), nds);
                                    } catch (Exception e) {

                                    }
                                }
                            }
                            node.xPos += (double) nc.motionX;
                            node.yPos += (double) nc.motionY;
                            node.zPos += (double) nc.motionZ;
                            AuraManager.auraNodes.put(node.key, node);
                            done = true;
                            if (nc.motionX != 0.0f || nc.motionY != 0.0f || nc.motionZ != 0.0f) {
                                AuraManager.updateNodeNeighbours(node);
                                int cx = MathHelper.floor_double((double) node.xPos) / 16;
                                cz = MathHelper.floor_double((double) node.zPos) / 16;
                                if (AuraManager.nodeChunks.get(Arrays.asList(node.dimension, cx, cz)) != null) {
                                    List<Integer> nds = AuraManager.nodeChunks
                                            .get(Arrays.asList(node.dimension, cx, cz));
                                    if (!nds.contains(node.key)) {
                                        nds.add(node.key);
                                        AuraManager.nodeChunks.put(Arrays.asList(node.dimension, cx, cz), nds);
                                    }
                                } else {
                                    ArrayList<Integer> temp = new ArrayList<Integer>();
                                    temp.add(node.key);
                                    AuraManager.nodeChunks.put(Arrays.asList(node.dimension, cx, cz), temp);
                                }
                            }
                            AuraManager.markedForTransmission.put(node.key, 0L);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
