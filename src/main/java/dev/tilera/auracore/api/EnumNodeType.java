package dev.tilera.auracore.api;

public enum EnumNodeType {
    NORMAL,
    PURE,
    DARK,
    UNSTABLE;

    public static final EnumNodeType[] VALID_TYPES;

    public static EnumNodeType getType(int id) {
        if (id >= 0 && id < VALID_TYPES.length) {
            return VALID_TYPES[id];
        }
        return NORMAL;
    }

    static {
        VALID_TYPES = new EnumNodeType[]{NORMAL, PURE, DARK, UNSTABLE};
    }
}
