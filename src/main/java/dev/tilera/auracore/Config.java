package dev.tilera.auracore;

import java.io.File;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.config.Configuration;

public class Config {
    
    private static Configuration config = new Configuration(new File(Loader.instance().getConfigDir(), "AuraCore.cfg"));
    public static int nodeRarity = 23;
    public static int specialNodeRarity = 75;
    public static boolean replaceSilverwood = true;
    public static boolean knowAllAspects = true;

    public static boolean noScanning() {
        return knowAllAspects;
    }

    public static void load() {
        config.load();
        nodeRarity = config.get("worldgen", "nodeRarity", nodeRarity).getInt(nodeRarity);
        specialNodeRarity = config.get("worldgen", "specialNodeRarity", specialNodeRarity).getInt(specialNodeRarity);
        replaceSilverwood = config.getBoolean("replaceSilverwood", "worldgen", replaceSilverwood, "Replace Silverwood trees with TC3 Silverwood");
        knowAllAspects = config.getBoolean("knowAllAspects", "research", knowAllAspects, "Know all Aspects from beginning");
        config.save();
    }

}
