package dev.tilera.auracore.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import dev.tilera.auracore.api.IWand;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.common.container.SlotLimitedByWand;
import thaumcraft.common.items.wands.ItemWandCasting;

@Mixin(SlotLimitedByWand.class)
public abstract class MixinSlotLimitedByWand extends Slot {

    public MixinSlotLimitedByWand(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
        super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
    }
    
    /**
     * @author tilera
     * @reason Allow classic wands in Arcane Worktable
     */
    @Overwrite
    public boolean isItemValid(final ItemStack stack) {
        return stack != null && stack.getItem() != null && ((stack.getItem() instanceof ItemWandCasting && !((ItemWandCasting)stack.getItem()).isStaff(stack)) || stack.getItem() instanceof IWand);
    }

}
