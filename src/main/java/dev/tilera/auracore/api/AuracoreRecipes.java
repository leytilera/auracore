package dev.tilera.auracore.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.tilera.auracore.api.crafting.CrucibleRecipe;
import dev.tilera.auracore.api.crafting.IInfusionRecipe;
import dev.tilera.auracore.api.crafting.ShapedInfusionCraftingRecipe;
import dev.tilera.auracore.api.crafting.ShapelessInfusionCraftingRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.tiles.TileCrucible;

public class AuracoreRecipes {
    
    private static List<CrucibleRecipe> crucibleRecipes = new ArrayList<CrucibleRecipe>();
    private static List<IInfusionRecipe> infusionRecipes = new ArrayList<IInfusionRecipe>();

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

    public static List<IInfusionRecipe> getInfusionRecipes() {
        return infusionRecipes;
    }

    public static IInfusionRecipe addInfusionCraftingRecipe(String key, String research, int cost, AspectList tags, ItemStack result, Object ... ingredients) {
        int var9;
        String var3 = "";
        int var4 = 0;
        int var5 = 0;
        int var6 = 0;
        if (ingredients[var4] instanceof String[]) {
            String[] var7;
            String[] var8 = var7 = (String[])ingredients[var4++];
            var9 = var7.length;
            for (int var10 = 0; var10 < var9; ++var10) {
                String var11 = var8[var10];
                ++var6;
                var5 = var11.length();
                var3 = var3 + var11;
            }
        } else {
            while (ingredients[var4] instanceof String) {
                String var13 = (String)ingredients[var4++];
                ++var6;
                var5 = var13.length();
                var3 = var3 + var13;
            }
        }
        HashMap<Character, ItemStack> var14 = new HashMap<Character, ItemStack>();
        while (var4 < ingredients.length) {
            Character var16 = (Character)ingredients[var4];
            ItemStack var17 = null;
            if (ingredients[var4 + 1] instanceof Item) {
                var17 = new ItemStack((Item)ingredients[var4 + 1]);
            } else if (ingredients[var4 + 1] instanceof Block) {
                var17 = new ItemStack((Block)ingredients[var4 + 1], 1, -1);
            } else if (ingredients[var4 + 1] instanceof ItemStack) {
                var17 = (ItemStack)ingredients[var4 + 1];
            }
            var14.put(var16, var17);
            var4 += 2;
        }
        ItemStack[] var15 = new ItemStack[var5 * var6];
        for (var9 = 0; var9 < var5 * var6; ++var9) {
            char var18 = var3.charAt(var9);
            var15[var9] = var14.containsKey(Character.valueOf(var18)) ? ((ItemStack)var14.get(Character.valueOf(var18))).copy() : null;
        }
        IInfusionRecipe rec = new ShapedInfusionCraftingRecipe(key, research, var5, var6, var15, result, cost, tags);
        infusionRecipes.add(rec);
        return rec;
    }

    public static IInfusionRecipe addShapelessInfusionCraftingRecipe(String key, String research, int cost, AspectList tags, ItemStack result, Object ... ingredients) {
        ArrayList<ItemStack> var3 = new ArrayList<ItemStack>();
        Object[] var4 = ingredients;
        int var5 = ingredients.length;
        for (int var6 = 0; var6 < var5; ++var6) {
            Object var7 = var4[var6];
            if (var7 instanceof ItemStack) {
                var3.add(((ItemStack)var7).copy());
                continue;
            }
            if (var7 instanceof Item) {
                var3.add(new ItemStack((Item)var7));
                continue;
            }
            if (!(var7 instanceof Block)) {
                throw new RuntimeException("Invalid shapeless recipe!");
            }
            var3.add(new ItemStack((Block)var7));
        }
        IInfusionRecipe rec = new ShapelessInfusionCraftingRecipe(key, research, result, var3, cost, tags);
        infusionRecipes.add(rec);
        return rec;
    }

}
