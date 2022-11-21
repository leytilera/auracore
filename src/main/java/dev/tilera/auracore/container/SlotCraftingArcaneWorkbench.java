package dev.tilera.auracore.container;

import cpw.mods.fml.common.FMLCommonHandler;
import dev.tilera.auracore.crafting.AuracoreCraftingManager;
import dev.tilera.auracore.helper.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class SlotCraftingArcaneWorkbench extends SlotCrafting {
    private final TileArcaneWorkbench craftMatrix;
    private EntityPlayer thePlayer;

    public SlotCraftingArcaneWorkbench(EntityPlayer par1EntityPlayer, TileArcaneWorkbench par2IInventory, IInventory par3IInventory, int par4, int par5, int par6) {
        super(par1EntityPlayer, par2IInventory, par3IInventory, par4, par5, par6);
        this.thePlayer = par1EntityPlayer;
        this.craftMatrix = par2IInventory;
    }

    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par1ItemStack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(this.thePlayer, par1ItemStack, this.craftMatrix);
        this.onCrafting(par1ItemStack);
        IArcaneRecipe recipe = AuracoreCraftingManager.findMatchingArcaneRecipe(this.craftMatrix, this.thePlayer);
        int cost = recipe == null ? 0 : AuracoreCraftingManager.getArcaneRecipeVisCost(recipe, this.craftMatrix);
        if (cost > 0) {
            Utils.spendCharge(this.craftMatrix.getStackInSlot(10), par1EntityPlayer, cost);
        }
        for (int var2 = 0; var2 < 9; ++var2) {
            ItemStack var3 = this.craftMatrix.getStackInSlot(var2);
            if (var3 == null) continue;
            this.craftMatrix.decrStackSize(var2, 1);
            if (!var3.getItem().hasContainerItem()) continue;
            ItemStack var4 = var3.getItem().getContainerItem(var3);
            if (var4.isItemStackDamageable() && var4.getItemDamage() > var4.getMaxDamage()) {
                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(this.thePlayer, var4));
                var4 = null;
            }
            if (var4 == null || var3.getItem().doesContainerItemLeaveCraftingGrid(var3) && this.thePlayer.inventory.addItemStackToInventory(var4)) continue;
            if (this.craftMatrix.getStackInSlot(var2) == null) {
                this.craftMatrix.setInventorySlotContents(var2, var4);
                continue;
            }
            this.thePlayer.dropPlayerItemWithRandomChoice(var4, true);
        }
    }
}
