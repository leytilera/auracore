package dev.tilera.auracore.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dev.tilera.auracore.api.CrystalColors;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.ItemShard;

@Mixin(ItemShard.class)
public abstract class MixinItemShard extends Item {
    
    /**
     * @author tilera
     * @reason Vis, Tainted and Dull shards
     */
    @Overwrite
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int par2) {
       return stack.getItemDamage() == 6 ? super.getColorFromItemStack(stack, par2) : CrystalColors.getColorForShard(stack.getItemDamage());
    }
 
    /**
     * @author tilera
     * @reason Vis, Tainted and Dull shards
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Overwrite
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
       for(int a = 0; a <= 9; ++a) {
          par3List.add(new ItemStack(this, 1, a));
       }
    }

}
