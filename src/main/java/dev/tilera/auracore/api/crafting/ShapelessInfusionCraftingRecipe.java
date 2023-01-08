package dev.tilera.auracore.api.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;

public class ShapelessInfusionCraftingRecipe implements IInfusionRecipe {
    private final ItemStack recipeOutput;
    public final List<ItemStack> recipeItems;
    public String key;
    public int cost;
    public AspectList tags;

    @Override
    public String getKey() {
        return this.key;
    }

    public ShapelessInfusionCraftingRecipe(String key, ItemStack par1ItemStack, List<ItemStack> par2List, int cost, AspectList tags) {
        this.recipeOutput = par1ItemStack;
        this.recipeItems = par2List;
        this.key = key;
        this.cost = cost;
        this.tags = tags;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    @Override
    public boolean matches(IInventory par1InventoryCrafting, World world, EntityPlayer player) {
        if (this.key.length() > 0 && !ThaumcraftApiHelper.isResearchComplete(player.getDisplayName(), this.key)) {
            return false;
        }
        ArrayList<ItemStack> var2 = new ArrayList<>(this.recipeItems);
        for (int var3 = 0; var3 < 3; ++var3) {
            for (int var4 = 0; var4 < 3; ++var4) {
                ItemStack var5 = ThaumcraftApiHelper.getStackInRowAndColumn((Object)par1InventoryCrafting, var4, var3);
                if (var5 == null) continue;
                boolean var6 = false;
                for (ItemStack var8 : var2) {
                    if (var5.getItem() != var8.getItem() || var8.getItemDamage() != 32767 && var5.getItemDamage() != var8.getItemDamage()) continue;
                    boolean matches = true;
                    if (var8.hasTagCompound()) {
                        matches = ThaumcraftApiHelper.areItemStackTagsEqualForCrafting(var5, var8);
                    }
                    if (!matches) continue;
                    var6 = true;
                    var2.remove((Object)var8);
                    break;
                }
                if (var6) continue;
                return false;
            }
        }
        return var2.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(IInventory par1InventoryCrafting) {
        return this.recipeOutput.copy();
    }

    @Override
    public int getRecipeSize() {
        return this.recipeItems.size();
    }

    @Override
    public int getCost() {
        return this.cost;
    }

    @Override
    public AspectList getAspects() {
        return this.tags;
    }

    @Override
    public String getResearch() {
        return this.key;
    }
}
