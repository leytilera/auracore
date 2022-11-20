package dev.tilera.auracore.api;

import java.io.Serializable;

import thaumcraft.api.aspects.AspectList;

public class AuraNode implements Serializable {
    public int key;
    public short level;
    public short baseLevel;
    public short taint;
    public AspectList flux = new AspectList();
    public EnumNodeType type;
    public int dimension;
    public double xPos;
    public double yPos;
    public double zPos;
    public boolean locked;
    public boolean isVirtual;

    public AuraNode(int key, short lvl, EnumNodeType type, int dim, int x, int y, int z) {
        this.key = key;
        this.level = lvl;
        this.baseLevel = lvl;
        this.type = type;
        this.dimension = dim;
        this.xPos = (double)x + 0.5;
        this.yPos = (double)y + 0.5;
        this.zPos = (double)z + 0.5;
        this.taint = 0;
        this.isVirtual = false;
    }

    public AuraNode() {
    }
}
