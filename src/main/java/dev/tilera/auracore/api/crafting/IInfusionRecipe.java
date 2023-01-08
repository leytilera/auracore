package dev.tilera.auracore.api.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.aspects.AspectList;

public interface IInfusionRecipe {
    boolean matches(IInventory var1, World var2, EntityPlayer var3);

    ItemStack getCraftingResult(IInventory var1);

    int getRecipeSize();

    ItemStack getRecipeOutput();

    int getCost();

    AspectList getAspects();

    String getKey();

    String getResearch();
}
