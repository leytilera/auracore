package dev.tilera.auracore.mixins;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.tilera.auracore.Config;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.research.PlayerKnowledge;

@Mixin(PlayerKnowledge.class)
public abstract class MixinPlayerKnowledge {

    @Shadow(remap = false)
    public Map<String, AspectList> aspectsDiscovered;

    @Inject(method = "getAspectsDiscovered", at = @At("HEAD"), remap = false)
    public void onGetPlayerKnowledge(String player, CallbackInfoReturnable<AspectList> ci) {
        AspectList known = (AspectList)this.aspectsDiscovered.get(player);
        if (Config.knowAllAspects && (known == null || known.size() < Aspect.aspects.size())) {
            addAllAspects(player);
        }
    }

    public void addAllAspects(String player) {
        AspectList known = (AspectList)this.aspectsDiscovered.get(player);
        if (known == null) {
            known = new AspectList();
        }
        for (Aspect a : Aspect.aspects.values()) {
            if (!known.aspects.containsKey(a)) {
                known.add(a, 0);
            }
        }
        aspectsDiscovered.put(player, known);
    }
    
}
