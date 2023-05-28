package dev.tilera.auracore.api;

public class CrystalColors {
    
    public static final int[] colors = new int[]{16777215, 16777086, 16727041, 37119, 40960, 15650047, 5592439, 11154172, 0xB0B0BC, 0x800080};

    public static int getColorForShard(int meta) {
        if (meta > 9 || meta < 0 || meta == 6) {
            return 0xFFFFFF;
        } else if (meta > 6) {
            return colors[meta];
        } else {
            return colors[meta + 1];
        }
    }

    public static int getColorForOre(int meta) {
        if (meta > 10 || meta < 0) {
            return 0xFFFFFF;
        } else if (meta > 7) {
            return colors[meta - 1];
        } else {
            return colors[meta];
        }
    }

    public static int getColorForCrystal(int meta) {
        if (meta > 10 || meta < 0) {
            return 0xFFFFFF;
        } else if (meta > 7) {
            return colors[meta - 1];
        } else {
            return colors[meta + 1];
        }
    }

}
