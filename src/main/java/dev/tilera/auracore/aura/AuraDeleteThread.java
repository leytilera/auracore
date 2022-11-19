package dev.tilera.auracore.aura;

import java.util.Arrays;
import java.util.List;

import dev.tilera.auracore.api.AuraNode;
import net.minecraft.util.MathHelper;

public class AuraDeleteThread
        implements Runnable {

    @Override
    public void run() {
        try {
            while (true) {
                boolean done = false;
                int count = 0;
                int dl = (Integer) AuraManager.auraDeleteQueue.take();
                Object object = AuraManager.saveLock;
                synchronized (object) {
                    while (AuraManager.auraNodes.get(dl) != null && !done && count < 10) {
                        ++count;
                        try {
                            AuraNode t = AuraManager.getNode(dl);
                            AuraManager.auraNodes.remove(dl);
                            AuraManager.nodeNeighbours.remove(dl);
                            int cx = MathHelper.floor_double((double) t.xPos) / 16;
                            int cz = MathHelper.floor_double((double) t.zPos) / 16;
                            if (AuraManager.nodeChunks.get(Arrays.asList(t.dimension, cx, cz)) != null) {
                                List<Integer> nds = AuraManager.nodeChunks.get(Arrays.asList(t.dimension, cx, cz));
                                if (nds.remove((Object) dl)) {
                                    AuraManager.nodeChunks.put(Arrays.asList(t.dimension, cx, cz), nds);
                                }
                            }
                            done = true;
                        } catch (Exception e1) {
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
