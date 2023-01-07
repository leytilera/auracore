package dev.tilera.auracore.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import dev.tilera.auracore.Config;
import dev.tilera.auracore.helper.LegacyAspects;
import thaumcraft.common.config.ConfigAspects;

@Mixin(ConfigAspects.class)
public class MixinConfigAspects {
    
    @Shadow(remap = false)
    private static void registerEntityAspects() {}
    @Shadow(remap = false)
    private static void registerItemAspects() {}

    /**
     * @author tilera
     * @reason Legacy aspects
     */
    @Overwrite(remap = false)
    public static void init() {
        if (Config.legacyAspects) {
            LegacyAspects.initAspects();
        } else {
            registerItemAspects();
        }
        registerEntityAspects();
     }

}
