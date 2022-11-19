package dev.tilera.auracore.aura;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.tilera.auracore.api.Aspects;
import dev.tilera.auracore.api.AuraNode;
import dev.tilera.auracore.helper.Utils;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class AuraCalculationThread
        implements Runnable {

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    while (true) {
                        List<Object> up = AuraManager.auraCalcQueue.take();
                        Object object = AuraManager.saveLock;
                        synchronized (object) {
                            AuraNode node = AuraManager.copyNode(AuraManager.getNode((Integer) up.get(0)));
                            this.updateNode((World) up.get(1), node);
                            this.checkFlux((World) up.get(1), node);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    public int getAverageFlux(AspectList ot) {
        int ret = 0;
        if (ot.size() > 0) {
            ret = ot.visSize();
            ret /= ot.size();
        }
        return ret;
    }

    private synchronized void updateNode(World world, AuraNode node) {
        int dim = world.provider.dimensionId;
        List<Integer> updateList = AuraManager.getNodeNeighbours(node.key);
        if (updateList.size() == 0) {
            AuraManager.generateNodeNeighbours(node);
        }
        int fluxTotal = this.getAverageFlux(node.flux);
        switch (node.type) {
            case PURE: {
                if (world.rand.nextInt(20) != 7)
                    break;
                this.removeRandomFlux(world, node, 1);
                break;
            }
            case DARK: {
                if (world.rand.nextInt(5 + fluxTotal) != 0)
                    break;
                if (world.rand.nextBoolean()) {
                    AuraManager.queueNodeChanges(node.key, 0, 0, false, new AspectList().add(Aspects.EVIL, 1), 0.0f,
                            0.0f, 0.0f);
                    break;
                }
                AuraManager.queueNodeChanges(node.key, 0, 0, false, new AspectList().add(Aspect.DEATH, 1), 0.0f, 0.0f,
                        0.0f);
                break;
            }
            case UNSTABLE: {
                if (world.rand.nextInt(1 + fluxTotal) != 0)
                    break;
                this.addRandomFlux(world, node, 1);
            }
        }
        /*
         * if (Config.foundIMP && node.level >= 5) {
         * AuraManager.queueNodeChanges(node.key, -5, 0, false, null, 0.0f, 0.0f, 0.0f);
         * }
         */ // TODO: WTF
        if (node.level > node.baseLevel && world.rand.nextFloat() > (float) node.baseLevel / (float) node.level) {
            this.addRandomFlux(world, node, 1);
        }
        if (updateList != null && updateList.size() > 0) {
            for (Integer nk : updateList) {
                double zd;
                double yd;
                double xd;
                double distSq;
                boolean sendTransferFX = false;
                boolean toT = false;
                AuraNode targetNode = AuraManager.copyNode(AuraManager.getNode(nk));
                if (targetNode == null || !Utils.isChunkLoaded(world, MathHelper.floor_double((double) targetNode.xPos),
                        MathHelper.floor_double((double) targetNode.zPos)))
                    continue;
                if (node.level < node.baseLevel && node.level < targetNode.level
                        && world.rand.nextFloat() > (float) node.level / (float) targetNode.level) {
                    node.level = (short) (node.level + 1);
                    targetNode.level = (short) (targetNode.level - 1);
                    AuraManager.queueNodeChanges(node.key, 1, 0, false, null, 0.0f, 0.0f, 0.0f);
                    AuraManager.queueNodeChanges(targetNode.key, -1, 0, false, null, 0.0f, 0.0f, 0.0f);
                    this.addRandomFlux(world, node, 1);
                    sendTransferFX = true;
                }
                if ((distSq = (xd = node.xPos - targetNode.xPos) * xd + (yd = node.yPos - targetNode.yPos) * yd
                        + (zd = node.zPos - targetNode.zPos) * zd) < (double) ((node.level + targetNode.level) / 2)
                        && distSq > 0.25) {
                    AspectList flx;
                    float zm;
                    float ym;
                    float xm;
                    float tq = node.level + targetNode.level;
                    if (!node.locked && !node.isVirtual && !targetNode.isVirtual) {
                        xm = (float) (-xd / distSq / (double) tq * (double) targetNode.level);
                        ym = (float) (-yd / distSq / (double) tq * (double) targetNode.level);
                        zm = (float) (-zd / distSq / (double) tq * (double) targetNode.level);
                        node.xPos += (double) xm;
                        node.yPos += (double) ym;
                        node.zPos += (double) zm;
                        flx = null;
                        if (world.rand.nextInt(25) == 0) {
                            flx = new AspectList();
                            flx.add(Aspect.MOTION, 1);
                        }
                        AuraManager.queueNodeChanges(node.key, 0, 0, false, flx, xm, ym, zm);
                    }
                    if (!targetNode.locked && !node.isVirtual && !targetNode.isVirtual) {
                        xm = (float) (xd / distSq / (double) tq * (double) node.level);
                        ym = (float) (yd / distSq / (double) tq * (double) node.level);
                        zm = (float) (zd / distSq / (double) tq * (double) node.level);
                        targetNode.xPos += (double) xm;
                        targetNode.yPos += (double) ym;
                        targetNode.zPos += (double) zm;
                        flx = null;
                        if (world.rand.nextInt(25) == 0) {
                            flx = new AspectList();
                            flx.add(Aspect.MOTION, 1);
                        }
                        AuraManager.queueNodeChanges(targetNode.key, 0, 0, false, flx, xm, ym, zm);
                    }
                } else if (distSq <= (double) 0.3f && !node.isVirtual && !targetNode.isVirtual) {
                    AspectList flx;
                    if (node.baseLevel > targetNode.baseLevel) {
                        node.level = (short) ((float) node.level + (float) targetNode.level * 0.75f);
                        node.baseLevel = (short) ((float) node.baseLevel + (float) targetNode.baseLevel * 0.33f);
                        double ox = node.xPos;
                        double oy = node.yPos;
                        double oz = node.zPos;
                        node.xPos = (node.xPos + targetNode.xPos) / 2.0;
                        node.yPos = (node.yPos + targetNode.yPos) / 2.0;
                        node.zPos = (node.zPos + targetNode.zPos) / 2.0;
                        flx = new AspectList();
                        if (targetNode.flux.size() > 0) {
                            for (Aspect tt : targetNode.flux.getAspects()) {
                                flx.add(tt, targetNode.flux.getAmount(tt));
                            }
                        }
                        flx.add(Aspect.EXCHANGE, (int) ((float) targetNode.baseLevel * 0.1f));
                        this.addRandomFlux(world, node, (int) ((float) targetNode.baseLevel * 0.3f));
                        AuraManager.queueNodeChanges(node.key, (int) ((float) targetNode.level * 0.75f),
                                (int) ((float) targetNode.baseLevel * 0.33f), false, flx, (float) (node.xPos - ox),
                                (float) (node.yPos - oy), (float) (node.zPos - oz));
                        AuraManager.auraDeleteQueue.add(targetNode.key);
                        AuraManager.sendNodeDeletionPacket(targetNode);
                    } else {
                        targetNode.level = (short) ((float) targetNode.level + (float) node.level * 0.75f);
                        targetNode.baseLevel = (short) ((float) targetNode.baseLevel + (float) node.baseLevel * 0.33f);
                        double ox = targetNode.xPos;
                        double oy = targetNode.yPos;
                        double oz = targetNode.zPos;
                        targetNode.xPos = (node.xPos + targetNode.xPos) / 2.0;
                        targetNode.yPos = (node.yPos + targetNode.yPos) / 2.0;
                        targetNode.zPos = (node.zPos + targetNode.zPos) / 2.0;
                        flx = new AspectList();
                        if (node.flux.size() > 0) {
                            for (Aspect tt : node.flux.getAspects()) {
                                flx.add(tt, node.flux.getAmount(tt));
                            }
                        }
                        flx.add(Aspect.EXCHANGE, (int) ((float) targetNode.baseLevel * 0.1f));
                        this.addRandomFlux(world, targetNode, (int) ((float) targetNode.baseLevel * 0.3f));
                        AuraManager.queueNodeChanges(targetNode.key, (int) ((float) node.level * 0.75f),
                                (int) ((float) node.baseLevel * 0.33f), false, flx, (float) (targetNode.xPos - ox),
                                (float) (targetNode.yPos - oy), (float) (targetNode.zPos - oz));
                        AuraManager.auraDeleteQueue.add(node.key);
                        AuraManager.sendNodeDeletionPacket(node);
                    }
                }
                if (!sendTransferFX)
                    continue;
                AuraManager.sendNodeTransferFXPacket(targetNode, node, distSq);
            }
        }
    }

    private void checkFlux(World world, AuraNode node) {
        if (node != null && node.flux != null) {
            for (Aspect fluxTag : node.flux.getAspects()) {
                int fluxAmt = node.flux.getAmount(fluxTag);
                int q = world.rand.nextInt(2500);
                if (AuraManager.fluxEventList == null) {
                    AuraManager.fluxEventList = new ArrayList<>();
                }
                if (q < 10 && fluxAmt >= 10) {
                    if (AuraManager.fluxEventList == null)
                        break;
                    AuraManager.fluxEventList.add(Arrays.asList(new Object[] { world, node, fluxTag, 0 }));
                    break;
                }
                if (q < 15 && fluxAmt >= 25) {
                    if (AuraManager.fluxEventList == null)
                        break;
                    AuraManager.fluxEventList.add(Arrays.asList(new Object[] { world, node, fluxTag, 1 }));
                    break;
                }
                if (q >= 20 || fluxAmt < 50)
                    continue;
                if (AuraManager.fluxEventList == null)
                    break;
                AuraManager.fluxEventList.add(Arrays.asList(new Object[] { world, node, fluxTag, 2 }));
                break;
            }
        }
    }

    private void addRandomFlux(World world, AuraNode node, int i) {
        AuraManager.addRandomFlux(world, node, i);
    }

    private void removeRandomFlux(World world, AuraNode node, int i) {
        AuraManager.removeRandomFlux(world, node, i);
    }
}
