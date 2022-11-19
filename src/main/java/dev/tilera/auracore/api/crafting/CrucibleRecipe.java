package dev.tilera.auracore.api.crafting;

import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class CrucibleRecipe {
    
    public ItemStack recipeOutput;
    public AspectList aspects;
    public String key;
    public String researchKey;
    public int cost;

    public CrucibleRecipe(String researchKey, String key, ItemStack result, AspectList tags, int cost) {
        this.recipeOutput = result;
        this.aspects = tags;
        this.key = key;
        this.researchKey = researchKey;
        this.cost = cost;
    }

    public CrucibleRecipe(String key, ItemStack result, AspectList tags, int cost) {
        this.recipeOutput = result;
        this.aspects = tags;
        this.key = key;
        this.researchKey = key;
        this.cost = cost;
    }

    public boolean matches(AspectList itags) {
        if (itags == null) {
            return false;                                                                                                                                                                     
        }                                                                                                                                                                                     
        for (Aspect tag : this.aspects.getAspects()) {                                                                                                                                          
            if (itags.getAmount(tag) >= this.aspects.getAmount(tag)) continue;                                                                                                                   
            return false;                                                                                                                                                                     
        }                                                                                                                                                                                     
        return true;                                                                                                                                                                          
    }                                                                                                                                                                                         
                                                                                                                                                                                              
    public AspectList removeMatching(AspectList itags) {
        AspectList temptags = new AspectList();
        temptags.aspects.putAll(itags.aspects);
        for (Aspect tag : this.aspects.getAspects()) {
            if (temptags.reduce(tag, this.aspects.getAmount(tag))) continue;
            return null;
        }
        itags = temptags;
        return itags;
    }

}
