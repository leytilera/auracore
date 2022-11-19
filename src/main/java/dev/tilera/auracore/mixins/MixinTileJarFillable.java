package dev.tilera.auracore.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.tilera.auracore.api.IEssenceContainer;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.TileJar;
import thaumcraft.common.tiles.TileJarFillable;

@Mixin(TileJarFillable.class)
public abstract class MixinTileJarFillable extends TileJar implements IEssenceContainer {

    @Shadow(remap = false)
    public Aspect aspect;
    @Shadow(remap = false)
    public int amount;

    @Override
    public Aspect getAspect() {
        return aspect;
    }

    @Override
    public int getAmount() {
        return amount;
    }

}

