package dev.tilera.auracore.container;

import dev.tilera.auracore.api.IWand;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.wands.ItemWandCasting;

class SlotWorkbenchWand extends Slot {
    public SlotWorkbenchWand(IInventory par2IInventory, int par3, int par4, int par5) {
        super(par2IInventory, par3, par4, par5);
    }

    @Override
    public boolean isItemValid(ItemStack par1ItemStack) {
        return par1ItemStack.getItem() instanceof ItemWandCasting || par1ItemStack.getItem() instanceof IWand;
    }
}
