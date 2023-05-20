package dev.tilera.auracore.api;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;

public interface ICrystal {

    public static Map<ChunkCoordinates, Integer> crystalCounts = new HashMap<>();
    
    /**
     * @return The amount of crystals in this cluster
     */
    int getCrystalCount(int meta);

    /**
     * Set the amount of crystals in this cluster.
     * @param count The new amount of crystals
     * @return true, if the crystal count was changed
     */
    boolean setCrystalCount(int count);

    /**
     * Harvest a single shard with a specific tool
     */
    void harvestShard(EntityPlayer player);

    boolean canHarvest(EntityPlayer player);

}
