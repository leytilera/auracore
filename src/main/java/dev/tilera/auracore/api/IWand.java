package dev.tilera.auracore.api;

import net.minecraft.item.ItemStack;

public interface IWand {
    
    int getVis(ItemStack stack);

    int getMaxVis(ItemStack stack);

    boolean consumeVis(ItemStack stack, int amount);

}
