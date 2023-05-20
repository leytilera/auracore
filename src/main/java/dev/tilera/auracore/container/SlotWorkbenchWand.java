package dev.tilera.auracore.container;

import dev.tilera.auracore.api.IWand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileArcaneWorkbench;

class SlotWorkbenchWand extends Slot {
    TileArcaneWorkbench workbench;
    EntityPlayer player;

    public SlotWorkbenchWand(TileArcaneWorkbench par2IInventory, int par3, int par4, int par5, EntityPlayer player) {
        super(par2IInventory, par3, par4, par5);
        this.workbench = par2IInventory;
        this.player = player;
    }

    @Override
    public boolean isItemValid(ItemStack par1ItemStack) {
        return par1ItemStack.getItem() instanceof ItemWandCasting || par1ItemStack.getItem() instanceof IWand;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        if (
            !this.workbench.getWorldObj().isRemote && 
            this.inventory.getStackInSlot(this.getSlotIndex()) != null &&
            this.inventory.getStackInSlot(this.getSlotIndex()).getItem() instanceof ItemWandCasting) {
                player.inventory.setItemStack(null);
                player.openGui(Thaumcraft.instance, 13, this.workbench.getWorldObj(), this.workbench.xCoord, this.workbench.yCoord, this.workbench.zCoord);
            }
    }

}
