package dev.tilera.auracore;

import java.io.File;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.config.Configuration;

public class Config {
    
    private static Configuration config = new Configuration(new File(Loader.instance().getConfigDir(), "AuraCore.cfg"));
    public static int nodeRarity = 23;
    public static int specialNodeRarity = 75;
    public static int newNodeRarity = 20;
    public static boolean replaceSilverwood = true;
    public static boolean knowAllAspects = true;
    public static boolean replaceAspects = true;
    public static boolean legacyAspects = false;
    public static boolean generateEldritchRing = true;
    public static boolean legacyCrucibleMechanics = true;

    public static boolean noScanning() {
        return knowAllAspects;
    }

    public static void load() {
        config.load();
        nodeRarity = config.get("worldgen", "nodeRarity", nodeRarity).getInt(nodeRarity);
        specialNodeRarity = config.get("worldgen", "specialNodeRarity", specialNodeRarity).getInt(specialNodeRarity);
        newNodeRarity = config.getInt("newNodeRarity", "worldgen", newNodeRarity, -1, Integer.MAX_VALUE, "Rarity of TC4 nodes generating instead of TC3 nodes (-1 to disable TC4 nodes)");
        replaceSilverwood = config.getBoolean("replaceSilverwood", "worldgen", replaceSilverwood, "Replace Silverwood trees with TC3 Silverwood");
        knowAllAspects = config.getBoolean("knowAllAspects", "research", knowAllAspects, "Know all Aspects from beginning");
        replaceAspects = config.getBoolean("replaceAspects", "client", replaceAspects, "Replace some aspect textures");
        legacyAspects = config.getBoolean("legacyAspects", "aspects", legacyAspects, "Use TC3 item aspects");
        generateEldritchRing = config.getBoolean("generateEldritchRing", "worldgen", generateEldritchRing, "Generate Eldritch Ring structures");
        legacyCrucibleMechanics = config.getBoolean("legacyCrucibleMechanics", "crucible", legacyCrucibleMechanics, "Use TC3 crucible mechanics");
        config.save();
    }

}
