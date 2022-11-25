package dev.tilera.auracore;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;

public class Recipes {
    
    public static void initRecipes() {
        ConfigResearch.recipes.put("Clusters8", shapelessOreDictRecipe(new ItemStack(ConfigBlocks.blockCrystal, 1, 8), new Object[] { new ItemStack(ConfigItems.itemShard, 1, 7), new ItemStack(ConfigItems.itemShard, 1, 7), new ItemStack(ConfigItems.itemShard, 1, 7), new ItemStack(ConfigItems.itemShard, 1, 7), new ItemStack(ConfigItems.itemShard, 1, 7), new ItemStack(ConfigItems.itemShard, 1, 7) }));
        ConfigResearch.recipes.put("Clusters10", shapelessOreDictRecipe(new ItemStack(ConfigBlocks.blockCrystal, 1, 10), new Object[] { new ItemStack(ConfigItems.itemShard, 1, 9), new ItemStack(ConfigItems.itemShard, 1, 9), new ItemStack(ConfigItems.itemShard, 1, 9), new ItemStack(ConfigItems.itemShard, 1, 9), new ItemStack(ConfigItems.itemShard, 1, 9), new ItemStack(ConfigItems.itemShard, 1, 9) }));
        ConfigResearch.recipes.put("Clusters9", shapelessOreDictRecipe(new ItemStack(ConfigBlocks.blockCrystal, 1, 9), new Object[] { new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(ConfigItems.itemShard, 1, 1), new ItemStack(ConfigItems.itemShard, 1, 2), new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(ConfigItems.itemShard, 1, 7) }));
    }

    static IRecipe shapelessOreDictRecipe(final ItemStack res, final Object[] params) {
        final IRecipe rec = (IRecipe)new ShapelessOreRecipe(res, params);
        CraftingManager.getInstance().getRecipeList().add(rec);
        return rec;
    }

}
