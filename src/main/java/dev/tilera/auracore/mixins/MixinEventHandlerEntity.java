package dev.tilera.auracore.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import dev.tilera.auracore.aura.AuraManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.IRepairableExtended;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.events.EventHandlerEntity;
import thaumcraft.common.lib.research.ResearchManager;

@Mixin(EventHandlerEntity.class)
public abstract class MixinEventHandlerEntity {
    
    /**
     * @author tilera
     * @reason Repair using Vis from the aura
     */
    @Overwrite(remap = false)
    public static void doRepair(ItemStack is, EntityPlayer player) {
        int level = EnchantmentHelper.getEnchantmentLevel(Config.enchRepair.effectId, is);
        if (level > 0) {
           if (level > 2) {
              level = 2;
           }
  
           AspectList cost = ThaumcraftCraftingManager.getObjectTags(is);
           if (cost != null && cost.size() != 0) {
              cost = ResearchManager.reduceToPrimals(cost);
              AspectList finalCost = new AspectList();
              Aspect[] aspects = cost.getAspects();
  
              for(Aspect a : aspects) {
                 if (a != null) {
                    finalCost.merge(a, (int)Math.sqrt((double)(cost.getAmount(a) * 2)) * level);
                 }
              }
  
              if (is.getItem() instanceof IRepairableExtended) {
                 if (((IRepairableExtended)is.getItem()).doRepair(is, player, level) && WandManager.consumeVisFromInventory(player, finalCost)) {
                    is.damageItem(-level, player);
                 }
              } else if (WandManager.consumeVisFromInventory(player, finalCost) || AuraManager.decreaseClosestAura(player.worldObj, player.posX, player.posY, player.posZ, 1)) {
                 is.damageItem(-level, player);
              }
  
           }
        }
     }

}
