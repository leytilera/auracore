package dev.tilera.auracore.container;

import dev.tilera.auracore.api.IWand;
import dev.tilera.auracore.crafting.AuracoreCraftingManager;
import dev.tilera.auracore.helper.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.container.ContainerDummy;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class ContainerWorkbench extends Container {
    private TileArcaneWorkbench tileEntity;
    private InventoryPlayer ip;

    public ContainerWorkbench(InventoryPlayer par1InventoryPlayer, TileArcaneWorkbench e) {
        int var7;
        int var6;
        this.tileEntity = e;
        this.tileEntity.eventHandler = this;
        this.ip = par1InventoryPlayer;
        this.addSlotToContainer((Slot)new SlotCraftingArcaneWorkbench(par1InventoryPlayer.player, this.tileEntity, this.tileEntity, 9, 124, 29));
        this.addSlotToContainer(new SlotWorkbenchWand(this.tileEntity, 10, 124, 61, par1InventoryPlayer.player));
        for (var6 = 0; var6 < 3; ++var6) {
            for (var7 = 0; var7 < 3; ++var7) {
                this.addSlotToContainer(new Slot(this.tileEntity, var7 + var6 * 3, 30 + var7 * 18, 17 + var6 * 18));
            }
        }
        for (var6 = 0; var6 < 3; ++var6) {
            for (var7 = 0; var7 < 9; ++var7) {
                this.addSlotToContainer(new Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 94 + var6 * 18));
            }
        }
        for (var6 = 0; var6 < 9; ++var6) {
            this.addSlotToContainer(new Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 152));
        }
        this.onCraftMatrixChanged(this.tileEntity);
    }

    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory) {
        InventoryCrafting ic = new InventoryCrafting(new ContainerDummy(), 3, 3);
        for (int a = 0; a < 9; ++a) {
            ic.setInventorySlotContents(a, this.tileEntity.getStackInSlot(a));
        }
        this.tileEntity.setInventorySlotContentsSoftly(9, CraftingManager.getInstance().findMatchingRecipe(ic, this.tileEntity.getWorldObj()));
        if (this.tileEntity.getStackInSlot(9) == null && this.tileEntity.getStackInSlot(10) != null && this.tileEntity.getStackInSlot(10).getItem() instanceof IWand) {
            IArcaneRecipe recipe = AuracoreCraftingManager.findMatchingArcaneRecipe(this.tileEntity, this.ip.player);
            if (recipe != null && Utils.hasCharge(this.tileEntity.getStackInSlot(10), this.ip.player, AuracoreCraftingManager.getArcaneRecipeVisCost(recipe, this.tileEntity))) {
                this.tileEntity.setInventorySlotContentsSoftly(9, recipe.getCraftingResult(this.tileEntity));
            }
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (!this.tileEntity.getWorldObj().isRemote) {
            this.tileEntity.eventHandler = null;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
        return this.tileEntity.getWorldObj().getTileEntity(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord) != this.tileEntity ? false : par1EntityPlayer.getDistanceSq((double)this.tileEntity.xCoord + 0.5, (double)this.tileEntity.yCoord + 0.5, (double)this.tileEntity.zCoord + 0.5) <= 64.0;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par1) {
        ItemStack var2 = null;
        Slot var3 = (Slot)this.inventorySlots.get(par1);
        if (var3 != null && var3.getHasStack()) {
            ItemStack var4 = var3.getStack();
            var2 = var4.copy();
            if (par1 == 0) {
                if (!this.mergeItemStack(var4, 11, 47, true)) {
                    return null;
                }
                var3.onSlotChange(var4, var2);
            } else if (par1 >= 11 && par1 < 38) {
                if (var4.getItem() instanceof ItemWandCasting || var4.getItem() instanceof IWand) {
                    if (!this.mergeItemStack(var4, 1, 2, false)) {
                        return null;
                    }
                    var3.onSlotChange(var4, var2);
                } else if (!this.mergeItemStack(var4, 38, 47, false)) {
                    return null;
                }
            } else if (par1 >= 38 && par1 < 47 ? !this.mergeItemStack(var4, 11, 38, false) : !this.mergeItemStack(var4, 11, 47, false)) {
                return null;
            }
            if (var4.stackSize == 0) {
                var3.putStack((ItemStack)null);
            } else {
                var3.onSlotChanged();
            }
            if (var4.stackSize == var2.stackSize) {
                return null;
            }
            var3.onPickupFromSlot(this.ip.player, var4);
        }
        return var2;
    }

    @Override
    public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer) {
        return super.slotClick(par1, par2, par3, par4EntityPlayer);
    }
}
