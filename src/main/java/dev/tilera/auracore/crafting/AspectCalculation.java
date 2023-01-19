package dev.tilera.auracore.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import dev.tilera.auracore.api.AuracoreRecipes;
import dev.tilera.auracore.api.crafting.CrucibleRecipe;
import dev.tilera.auracore.api.crafting.IInfusionRecipe;
import dev.tilera.auracore.api.crafting.ShapedInfusionCraftingRecipe;
import dev.tilera.auracore.api.crafting.ShapelessInfusionCraftingRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class AspectCalculation {

    public static AspectList generateTagsFromInfusionRecipes(Item item, int meta, ArrayList<List> history) {
        AspectList ret = null;
        int value = 0;
        List<IInfusionRecipe> recipeList = AuracoreRecipes.getInfusionRecipes();
        for (int q = 0; q < recipeList.size(); ++q) {
            int idS;
            IInfusionRecipe recipe = recipeList.get(q);
            int idR = recipe.getRecipeOutput().getItemDamage() < 0 ? 0 : recipe.getRecipeOutput().getItemDamage();
            int n = idS = meta < 0 ? 0 : meta;
            if (recipe.getRecipeOutput().getItem() != item || idR != idS) continue;
            HashMap<ItemSignature, ItemStack> ingredients = new HashMap<>();
            AspectList ph = new AspectList();
            int cval = 0;
            try {
                if (recipe instanceof ShapedInfusionCraftingRecipe) {
                    int width = ((ShapedInfusionCraftingRecipe)recipe).recipeWidth;
                    int height = ((ShapedInfusionCraftingRecipe)recipe).recipeHeight;
                    ItemStack[] items = ((ShapedInfusionCraftingRecipe)recipe).recipeItems;
                    for (int i = 0; i < width && i < 3; ++i) {
                        for (int j = 0; j < height && j < 3; ++j) {
                            if (items[i + j * width] == null) continue;
                            items[i + j * width].stackSize = 1;
                            if (ingredients.containsKey(new ItemSignature(items[i + j * width]))) {
                                ItemStack is = (ItemStack)ingredients.get(new ItemSignature(items[i + j * width]));
                                ++is.stackSize;
                                ingredients.put(new ItemSignature(items[i + j * width]), is);
                                continue;
                            }
                            ingredients.put(new ItemSignature(items[i + j * width]), items[i + j * width]);
                        }
                    }
                } else {
                    List<ItemStack> items = ((ShapelessInfusionCraftingRecipe)recipe).recipeItems;
                    for (int i = 0; i < items.size() && i < 9; ++i) {
                        if (items.get(i) == null) continue;
                        ((ItemStack)items.get((int)i)).stackSize = 1;
                        if (ingredients.containsKey(new ItemSignature(items.get((int)i)))) {
                            ItemStack is = (ItemStack)ingredients.get(new ItemSignature(items.get((int)i)));
                            ++is.stackSize;
                            ingredients.put(new ItemSignature(items.get((int)i)), is);
                            continue;
                        }
                        ingredients.put(new ItemSignature(items.get((int)i)), items.get(i));
                    }
                }
                Collection<ItemStack> ings = ingredients.values();
                for (ItemStack is : ings) {
                    AspectList obj = ThaumcraftCraftingManager.generateTags(is.getItem(), is.getItemDamage(), history);
                    AspectList objC = null;
                    if (is.getItem().getContainerItem() != null) {
                        objC = ThaumcraftCraftingManager.generateTags(is.getItem().getContainerItem(), -1, history);
                    }
                    if (obj == null) continue;
                    for (Aspect as : obj.getAspects()) {
                        float amnt;
                        if (objC != null && objC.getAmount(as) > 0 || !((amnt = (float)(obj.getAmount(as) * is.stackSize) / (float)recipe.getRecipeOutput().stackSize) > 0.5f)) continue;
                        float tmod = 0.8f;
                        ph.add(as, Math.max(Math.round(amnt * tmod), 1));
                        cval += Math.max(Math.round(amnt * tmod), 1);
                    }
                }
                ph.add(Aspect.MAGIC, Math.round((float)recipe.getCost() / 10.0f / (float)recipe.getRecipeOutput().stackSize));
                for (Aspect tag : recipe.getAspects().getAspects()) {
                    ph.add(tag, Math.round((float)recipe.getAspects().getAmount(tag) / (float)recipe.getRecipeOutput().stackSize));
                }
                if (cval < value) continue;
                ret = ph;
                value = cval;
                continue;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static AspectList generateTagsFromCrucibleRecipes(Item item, int meta, ArrayList<List> history) {
        CrucibleRecipe cr = AuracoreRecipes.getCrucibleRecipe(new ItemStack(item, 1, meta));
        if (cr != null) {
            AspectList ot = new AspectList();
            int ss = cr.recipeOutput.stackSize;
            ot.add(Aspect.MAGIC, Math.round((float)cr.cost / 10.0f));
            for (Aspect tt : cr.aspects.getAspects()) {
                int amt = cr.aspects.getAmount(tt) / ss;
                ot.add(tt, amt);
            }
            return ot;
        }
        return null;
    }

    public static AspectList generateTagsFromArcaneRecipes(Item item, int meta, ArrayList<List> history) {
        AspectList ret = null;
        int value = 0;
        List recipeList = ThaumcraftApi.getCraftingRecipes();
        for (int q = 0; q < recipeList.size(); ++q) {
            int idS;
            if (!(recipeList.get(q) instanceof IArcaneRecipe)) continue;
            IArcaneRecipe recipe = (IArcaneRecipe)recipeList.get(q);
            if (!(recipe instanceof ShapedArcaneRecipe || recipe instanceof ShapelessArcaneRecipe)) continue;
            int idR = recipe.getRecipeOutput().getItemDamage() < 0 ? 0 : recipe.getRecipeOutput().getItemDamage();
            int n = idS = meta < 0 ? 0 : meta;
            if (recipe.getRecipeOutput().getItem() != item || idR != idS) continue;
            HashMap<ItemSignature, ItemStack> ingredients = new HashMap<>();
            AspectList ph = new AspectList();
            int cval = 0;
            try {
                if (recipe instanceof ShapedArcaneRecipe) {
                    int width = ((ShapedArcaneRecipe)recipe).width;
                    int height = ((ShapedArcaneRecipe)recipe).width;
                    Object[] items = ((ShapedArcaneRecipe)recipe).input;
                    for (int i = 0; i < width && i < 3; ++i) {
                        for (int j = 0; j < height && j < 3; ++j) {
                            if (items[i + j * width] == null) continue;
                            ItemStack stack = null;
                            if (items[i + j * width] instanceof ItemStack) {
                                stack = (ItemStack) items[i + j * width];
                                
                            } else if (items[i + j * width] instanceof ArrayList) {
                                ArrayList<ItemStack> l = (ArrayList<ItemStack>) items[i + j * width];
                                if (l.size() > 0) {
                                    stack = l.get(0);
                                }
                            }
                            if (stack != null) {
                                stack.stackSize = 1;
                                if (ingredients.containsKey(new ItemSignature(stack))) {
                                    ItemStack is = (ItemStack)ingredients.get(new ItemSignature(stack));
                                    ++is.stackSize;
                                    ingredients.put(new ItemSignature(stack), is);
                                    continue;
                                }
                                ingredients.put(new ItemSignature(stack), stack);
                            }
                        }
                    }
                } else {
                    List<Object> items = ((ShapelessArcaneRecipe)recipe).getInput();
                    for (int i = 0; i < items.size() && i < 9; ++i) {
                        if (items.get(i) == null) continue;
                        ItemStack stack = null;
                        if (items.get(i) instanceof ItemStack) {
                            stack = (ItemStack) items.get(i);
                        } else if (items.get(i) instanceof ArrayList) {
                            ArrayList<ItemStack> l = (ArrayList<ItemStack>) items.get(i);
                            if (l.size() > 0){
                                stack = l.get(0);
                            }
                        }
                        if (stack != null) {
                            stack.stackSize = 1;
                            if (ingredients.containsKey(new ItemSignature(stack))) {
                                ItemStack is = (ItemStack)ingredients.get(new ItemSignature(stack));
                                ++is.stackSize;
                                ingredients.put(new ItemSignature(stack), is);
                                continue;
                            }
                            ingredients.put(new ItemSignature(stack), stack);
                        }
                    }
                }
                Collection<ItemStack> ings = ingredients.values();
                for (ItemStack is : ings) {
                    AspectList obj = ThaumcraftCraftingManager.generateTags(is.getItem(), is.getItemDamage(), history);
                    AspectList objC = null;
                    if (is.getItem().getContainerItem() != null) {
                        objC = ThaumcraftCraftingManager.generateTags(is.getItem().getContainerItem(), -1, history);
                    }
                    if (obj == null) continue;
                    for (Aspect as : obj.getAspects()) {
                        float amnt;
                        if (objC != null && objC.getAmount(as) > 0 || !((amnt = (float)(obj.getAmount(as) * is.stackSize) / (float)recipe.getRecipeOutput().stackSize) > 0.5f)) continue;
                        float tmod = 0.8f;
                        ph.add(as, Math.max(Math.round(amnt * tmod), 1));
                        cval += Math.max(Math.round(amnt * tmod), 1);
                    }
                }
                ph.add(Aspect.MAGIC, Math.round((float)AuracoreCraftingManager.getArcaneRecipeVisCost(recipe, null) / 10.0f / (float)recipe.getRecipeOutput().stackSize));
                if (cval < value) continue;
                ret = ph;
                value = cval;
                continue;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static class ItemSignature {

        Item item;
        int metadata;

        public ItemSignature(ItemStack stack) {
            this.item = stack.getItem();
            this.metadata = stack.getItemDamage();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((item == null) ? 0 : item.hashCode());
            result = prime * result + metadata;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ItemSignature other = (ItemSignature) obj;
            if (item == null) {
                if (other.item != null)
                    return false;
            } else if (!item.equals(other.item))
                return false;
            if (metadata != other.metadata)
                return false;
            return true;
        }

    }

}
