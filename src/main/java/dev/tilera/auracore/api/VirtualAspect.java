package dev.tilera.auracore.api;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;

public class VirtualAspect extends Aspect {

    public VirtualAspect(String tag, int color, ResourceLocation image, int blend) {
        super(tag, color, null, image, blend);
        Aspect.aspects.remove(tag);
    }
    
}
