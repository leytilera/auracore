package dev.tilera.auracore.api.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;

public class ShapedInfusionCraftingRecipe implements IInfusionRecipe {
    public int recipeWidth;
    public int recipeHeight;
    public String key;
    public String research;
    public int cost;
    public AspectList tags;
    public ItemStack[] recipeItems;
    private ItemStack recipeOutput;
    public final Item recipeOutputItem;

    @Override
    public String getKey() {
        return this.key;
    }

    public ShapedInfusionCraftingRecipe(String key, String research, int par1, int par2, ItemStack[] par3ArrayOfItemStack, ItemStack par4ItemStack, int cost, AspectList tags) {
        this.recipeOutputItem = par4ItemStack.getItem();
        this.recipeWidth = par1;
        this.recipeHeight = par2;
        this.recipeItems = par3ArrayOfItemStack;
        this.recipeOutput = par4ItemStack;
        this.key = key;
        this.research = research;
        this.cost = cost;
        this.tags = tags;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    @Override
    public boolean matches(IInventory par1InventoryCrafting, World world, EntityPlayer player) {
        if (this.research.length() > 0 && !ThaumcraftApiHelper.isResearchComplete(player.getDisplayName(), this.research)) {
            return false;
        }
        for (int var2 = 0; var2 <= 3 - this.recipeWidth; ++var2) {
            for (int var3 = 0; var3 <= 3 - this.recipeHeight; ++var3) {
                if (this.checkMatch(par1InventoryCrafting, var2, var3, true)) {
                    return true;
                }
                if (!this.checkMatch(par1InventoryCrafting, var2, var3, false)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean checkMatch(IInventory par1InventoryCrafting, int par2, int par3, boolean par4) {
        for (int var5 = 0; var5 < 3; ++var5) {
            for (int var6 = 0; var6 < 3; ++var6) {
                ItemStack var10;
                int var7 = var5 - par2;
                int var8 = var6 - par3;
                ItemStack var9 = null;
                if (var7 >= 0 && var8 >= 0 && var7 < this.recipeWidth && var8 < this.recipeHeight) {
                    var9 = par4 ? this.recipeItems[this.recipeWidth - var7 - 1 + var8 * this.recipeWidth] : this.recipeItems[var7 + var8 * this.recipeWidth];
                }
                if ((var10 = ThaumcraftApiHelper.getStackInRowAndColumn((Object)par1InventoryCrafting, var5, var6)) == null && var9 == null) continue;
                if (var10 == null && var9 != null || var10 != null && var9 == null) {
                    return false;
                }
                if (var9.getItem() != var10.getItem()) {
                    return false;
                }
                if (var9.getItemDamage() != 32767 && var9.getItemDamage() != var10.getItemDamage()) {
                    return false;
                }
                if (!var9.hasTagCompound()) continue;
                return ThaumcraftApiHelper.areItemStackTagsEqualForCrafting(var10, var9);
            }
        }
        return true;
    }

    @Override
    public ItemStack getCraftingResult(IInventory par1InventoryCrafting) {
        return new ItemStack(this.recipeOutput.getItem(), this.recipeOutput.stackSize, this.recipeOutput.getItemDamage());
    }

    @Override
    public int getRecipeSize() {
        return this.recipeWidth * this.recipeHeight;
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
        return this.research;
    }
}
