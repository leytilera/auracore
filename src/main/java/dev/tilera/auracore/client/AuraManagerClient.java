package dev.tilera.auracore.client;

import java.util.HashMap;

import dev.tilera.auracore.network.AuraPacket;

public class AuraManagerClient {
    public static HashMap<Integer, NodeStats> auraClientList = new HashMap<>();
    public static HashMap<Integer, NodeHistoryStats> auraClientHistory = new HashMap<>();
    public static HashMap<Integer, NodeRenderInfo> auraClientMovementList = new HashMap<>();

    public static class NodeRenderInfo {
        public float x = 0.0f;
        public float y = 0.0f;
        public float z = 0.0f;

        public NodeRenderInfo(float xx, float yy, float zz) {
            this.x = xx;
            this.y = yy;
            this.z = zz;
        }
    }

    public static class NodeStats {
        public int key;
        public double x;
        public double y;
        public double z;
        public short level;
        public short base;
        public short taint;
        public int flux;
        public boolean lock;
        public byte type;
        public int dimension;

        public NodeStats(AuraPacket packet, int dimension) {
            key = packet.key;
            x = packet.x;
            y = packet.y;
            z = packet.z;
            level = packet.level;
            base = packet.base;
            taint = packet.taint;
            flux = packet.flux;
            lock = packet.lock;
            type = packet.type;
            this.dimension = dimension;
        }

    }

    public static class NodeHistoryStats {

        public short level;
        public short taint;
        public int flux;

        public NodeHistoryStats(short level, int flux, short taint) {
            this.level = level;
            this.flux = flux;
            this.taint = taint;
        }

        public NodeHistoryStats(NodeStats stats) {
            if (stats != null) {
                this.level = stats.level;
                this.flux = stats.flux;
                this.taint = stats.taint;
            }
        }

    }

}
