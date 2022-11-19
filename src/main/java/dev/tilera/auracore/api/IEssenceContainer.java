package dev.tilera.auracore.api;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

public interface IEssenceContainer extends IAspectContainer {
    
    Aspect getAspect();

    int getAmount();

}
