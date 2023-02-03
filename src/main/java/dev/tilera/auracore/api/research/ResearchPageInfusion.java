package dev.tilera.auracore.api.research;

import dev.tilera.auracore.api.crafting.IInfusionRecipe;
import thaumcraft.api.research.ResearchPage;

public class ResearchPageInfusion extends ResearchPage {

    public ResearchPageInfusion(IInfusionRecipe recipe) {
        super("auracore.research_title.infusion");
        this.recipe = recipe;
        this.recipeOutput = recipe.getRecipeOutput().copy();
    }

    public ResearchPageInfusion(IInfusionRecipe[] recipes) {
        super("auracore.research_title.infusion");
        this.recipe = recipes;
        if (recipes.length > 0) {
            this.recipeOutput = recipes[0].getRecipeOutput().copy();
        }
    }
    
}
