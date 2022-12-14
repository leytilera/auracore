package dev.tilera.auracore.api;

import java.util.ArrayList;
import java.util.List;

import dev.tilera.auracore.api.crafting.CrucibleRecipe;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.tiles.TileCrucible;

public class AuracoreRecipes {
    
    private static List<CrucibleRecipe> crucibleRecipes = new ArrayList<CrucibleRecipe>();

    public static void addCrucibleRecipe(String key, ItemStack result, int cost, AspectList tags) {
        crucibleRecipes.add(new CrucibleRecipe(key, result, tags, cost));
    }

    public static void addCrucibleRecipe(String key, String recipeKey, ItemStack result, int cost, AspectList tags) {
        crucibleRecipes.add(new CrucibleRecipe(key, recipeKey, result, tags, cost));
    }

    public static List<CrucibleRecipe> getCrucibleRecipes() {
        return crucibleRecipes;
    }

    public static CrucibleRecipe getCrucibleRecipe(String key) {
        for (Object r : crucibleRecipes) {
            if (!(r instanceof CrucibleRecipe) || !((CrucibleRecipe)r).key.equals(key)) continue;
            return (CrucibleRecipe)r;
        }
        return null;
    }

    public static CrucibleRecipe getCrucibleRecipe(ItemStack stack) {
        for (Object r : crucibleRecipes) {
            if (!(r instanceof CrucibleRecipe) || !((CrucibleRecipe)r).recipeOutput.isItemEqual(stack)) continue;
            return (CrucibleRecipe)r;
        }
        return null;
    }

    public static CrucibleRecipe getCrucibleRecipe(AspectList tags, TileCrucible tile) {
        int highest = 0;
        int index = -1;
        for (int a = 0; a < AuracoreRecipes.getCrucibleRecipes().size(); ++a) {
            int result;
            CrucibleRecipe recipe;
            if (!(AuracoreRecipes.getCrucibleRecipes().get(a) instanceof CrucibleRecipe) || !(recipe = (CrucibleRecipe)AuracoreRecipes.getCrucibleRecipes().get(a)).matches(tags) || (result = recipe.aspects.size()) <= highest) continue;
            highest = result;
            index = a;
        }
        if (index < 0) {
            return null;
        } else {
            return crucibleRecipes.get(index);
        }
    }

    public static int getCrucibleOutputCost(TileCrucible tile, CrucibleRecipe recipe) {
        int output = 0;
        AspectList tt = new AspectList();
        tt.aspects.putAll(tile.aspects.aspects);
        while (recipe.matches(tt)) {
            tt = recipe.removeMatching(tt);
            output += recipe.cost;
        }
        return output;
    }

    public static ItemStack getCrucibleOutput(AspectList tags, TileCrucible tile, CrucibleRecipe recipe) {
        ItemStack output = recipe.recipeOutput.copy();
        int stackInc = output.stackSize;
        output.stackSize = 0;
        while (recipe.matches(tags)) {
            tags = recipe.removeMatching(tags);
            output.stackSize += stackInc;
        }
        if (!tile.getWorldObj().isRemote) {
            tile.aspects = tags;
        }
        return output;
    }

}
