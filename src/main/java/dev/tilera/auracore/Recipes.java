package dev.tilera.auracore;

import java.util.HashSet;
import java.util.Set;

import dev.tilera.auracore.api.Aspects;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;

public class Recipes {

    static Set<String> oldCrucibleRecipes = new HashSet<>();
    
    public static void initRecipes() {
        ConfigResearch.recipes.put("Clusters8", shapelessOreDictRecipe(new ItemStack(ConfigBlocks.blockCrystal, 1, 8), new Object[] { new ItemStack(ConfigItems.itemShard, 1, 7), new ItemStack(ConfigItems.itemShard, 1, 7), new ItemStack(ConfigItems.itemShard, 1, 7), new ItemStack(ConfigItems.itemShard, 1, 7), new ItemStack(ConfigItems.itemShard, 1, 7), new ItemStack(ConfigItems.itemShard, 1, 7) }));
        ConfigResearch.recipes.put("Clusters10", shapelessOreDictRecipe(new ItemStack(ConfigBlocks.blockCrystal, 1, 10), new Object[] { new ItemStack(ConfigItems.itemShard, 1, 9), new ItemStack(ConfigItems.itemShard, 1, 9), new ItemStack(ConfigItems.itemShard, 1, 9), new ItemStack(ConfigItems.itemShard, 1, 9), new ItemStack(ConfigItems.itemShard, 1, 9), new ItemStack(ConfigItems.itemShard, 1, 9) }));
        ConfigResearch.recipes.put("Clusters9", shapelessOreDictRecipe(new ItemStack(ConfigBlocks.blockCrystal, 1, 9), new Object[] { new ItemStack(ConfigItems.itemShard, 1, 0), new ItemStack(ConfigItems.itemShard, 1, 1), new ItemStack(ConfigItems.itemShard, 1, 2), new ItemStack(ConfigItems.itemShard, 1, 3), new ItemStack(ConfigItems.itemShard, 1, 7) }));
        if (false) {
            ThaumcraftApi.addCrucibleRecipe("ALUMENTUM", new ItemStack(ConfigItems.itemResource, 1, 0), 5, new AspectList().merge(Aspect.ENERGY, 6).merge(Aspect.FIRE, 6).merge(Aspects.DESTRUCTION, 3));
            oldCrucibleRecipes.add("ALUMENTUM");
            //ThaumcraftApi.addCrucibleRecipe("GUNPOWDER", new ItemStack(Items.gunpowder), 5, new AspectList().merge(Aspect.FIRE, 6).merge(Aspects.DESTRUCTION, 6));
            ThaumcraftApi.addCrucibleRecipe("NITOR", new ItemStack(ConfigItems.itemResource, 1, 1), 5, new AspectList().merge(Aspect.ENERGY, 4).merge(Aspect.FIRE, 4).merge(Aspect.LIGHT, 6));
            oldCrucibleRecipes.add("NITOR");
            ThaumcraftApi.addCrucibleRecipe("THAUMIUM", new ItemStack(ConfigItems.itemResource, 1, 2), 5, new AspectList().merge(Aspect.METAL, 8).merge(Aspect.MAGIC, 4));
            oldCrucibleRecipes.add("THAUMIUM");
            ThaumcraftApi.addCrucibleRecipe("TALLOW", new ItemStack(ConfigItems.itemResource, 1, 4), 5, new AspectList().merge(Aspect.FLESH, 4));
            oldCrucibleRecipes.add("TALLOW");
            ThaumcraftApi.addCrucibleRecipe("TRANSGOLD", new ItemStack(Items.gold_nugget, 2, 0), 5, new AspectList().merge(Aspect.METAL, 2).merge(Aspects.VALUABLE, 1));
            oldCrucibleRecipes.add("TRANSGOLD");
            if (thaumcraft.common.config.Config.foundCopperIngot) {
                ThaumcraftApi.addCrucibleRecipe("TRANSCOPPER", new ItemStack(ConfigItems.itemNugget, 3, 1), 5, new AspectList().merge(Aspect.METAL, 3).merge(Aspect.LIFE, 1));
                oldCrucibleRecipes.add("TRANSCOPPER");
            }
            if (thaumcraft.common.config.Config.foundTinIngot) {
                ThaumcraftApi.addCrucibleRecipe("TRANSTIN", new ItemStack(ConfigItems.itemNugget, 3, 2), 5, new AspectList().merge(Aspect.METAL, 3).merge(Aspect.CRYSTAL, 1));
                oldCrucibleRecipes.add("TRANSTIN");
            }
            if (thaumcraft.common.config.Config.foundSilverIngot) {
                ThaumcraftApi.addCrucibleRecipe("TRANSSILVER", new ItemStack(ConfigItems.itemNugget, 3, 3), 5, new AspectList().merge(Aspect.METAL, 3).merge(Aspect.EXCHANGE, 1));
                oldCrucibleRecipes.add("TRANSSILVER");
            }
            if (thaumcraft.common.config.Config.foundLeadIngot) {
                ThaumcraftApi.addCrucibleRecipe("TRANSLEAD", new ItemStack(ConfigItems.itemNugget, 3, 4), 5, new AspectList().merge(Aspect.METAL, 3).merge(Aspect.VOID, 1));
                oldCrucibleRecipes.add("TRANSLEAD");
            }
            ThaumcraftApi.addCrucibleRecipe("TRANSIRON", new ItemStack(ConfigItems.itemNugget, 2, 0), 5, new AspectList().merge(Aspect.METAL, 2));
            oldCrucibleRecipes.add("TRANSIRON");
            ThaumcraftApi.getCraftingRecipes().removeIf((Object o) -> o instanceof thaumcraft.api.crafting.CrucibleRecipe && oldCrucibleRecipes.contains(((thaumcraft.api.crafting.CrucibleRecipe)o).key));
        }
    }

    @SuppressWarnings({"unchecked"})
    static IRecipe shapelessOreDictRecipe(final ItemStack res, final Object[] params) {
        final IRecipe rec = (IRecipe)new ShapelessOreRecipe(res, params);
        CraftingManager.getInstance().getRecipeList().add(rec);
        return rec;
    }

}
