package dev.tilera.auracore.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;

public class AuracoreCraftingManager {
    
    public static IArcaneRecipe findMatchingArcaneRecipe(IInventory awb, EntityPlayer player) {
        for (Object recipe : ThaumcraftApi.getCraftingRecipes()) {
            if (recipe instanceof IArcaneRecipe && ((IArcaneRecipe)recipe).matches(awb, player.worldObj, player)) {
                return (IArcaneRecipe) recipe;
            }
        }

        return null;
    }

    public static int getArcaneRecipeVisCost(IArcaneRecipe recipe, IInventory awb) {
        if (recipe == null) return 0;
        int sum = 0;
        AspectList aspects = recipe.getAspects(awb);
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect != null) sum += aspects.getAmount(aspect);
        }
        return sum;
    }

}
