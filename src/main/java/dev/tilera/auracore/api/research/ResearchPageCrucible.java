package dev.tilera.auracore.api.research;

import dev.tilera.auracore.api.crafting.CrucibleRecipe;
import thaumcraft.api.research.ResearchPage;

public class ResearchPageCrucible extends ResearchPage {

    public ResearchPageCrucible(CrucibleRecipe recipe) {
        super("auracore.research_title.crucible");
        this.recipe = recipe;
        this.recipeOutput = recipe.recipeOutput.copy();
    }

    public ResearchPageCrucible(CrucibleRecipe[] recipes) {
        super("auracore.research_title.crucible");
        this.recipe = recipes;
        if (recipes.length > 0) {
            this.recipeOutput = recipes[0].recipeOutput.copy();
        }
    }
    
}
