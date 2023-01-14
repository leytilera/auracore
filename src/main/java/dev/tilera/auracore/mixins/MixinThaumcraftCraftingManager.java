package dev.tilera.auracore.mixins;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Invoker;

import dev.tilera.auracore.api.Aspects;
import dev.tilera.auracore.crafting.AspectCalculation;
import net.minecraft.item.Item;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

@Mixin(ThaumcraftCraftingManager.class)
public abstract class MixinThaumcraftCraftingManager {

    @Invoker(value = "generateTagsFromCrucibleRecipes", remap = false)
    public static AspectList invokeGenerateTagsFromCrucibleRecipes(Item item, int meta, ArrayList<List> history) {
        throw new RuntimeException();
    }
    
    @Invoker(value = "generateTagsFromArcaneRecipes", remap = false)
    public static AspectList invokeGenerateTagsFromArcaneRecipes(Item item, int meta, ArrayList<List> history) {
        throw new RuntimeException();
    }

    @Invoker(value = "generateTagsFromInfusionRecipes", remap = false)
    public static AspectList invokeGenerateTagsFromInfusionRecipes(Item item, int meta, ArrayList<List> history) {
        throw new RuntimeException();
    }

    @Invoker(value = "generateTagsFromCraftingRecipes", remap = false)
    public static AspectList invokeGenerateTagsFromCraftingRecipes(Item item, int meta, ArrayList<List> history) {
        throw new RuntimeException();
    }

    /**
     * @author tilera
     * @reason recipe tag generation
     */
    @Overwrite(remap = false)
    private static AspectList generateTagsFromRecipes(Item item, int meta, ArrayList<List> history) {
        AspectList ret = null;
        ret = AspectCalculation.generateTagsFromCrucibleRecipes(item, meta, history);
        if (ret != null) {
           return ret;
        }
        ret = invokeGenerateTagsFromCrucibleRecipes(item, meta, history);
        if (ret != null) {
           return ret;
        }
        ret = AspectCalculation.generateTagsFromArcaneRecipes(item, meta, history);
        if (ret != null) {
              return ret;
        }
        ret = invokeGenerateTagsFromArcaneRecipes(item, meta, history);
        if (ret != null) {
              return ret;
        }
        ret = AspectCalculation.generateTagsFromInfusionRecipes(item, meta, history);
        if (ret != null) {
              return ret;
        }
        ret = invokeGenerateTagsFromInfusionRecipes(item, meta, history);
        if (ret != null) {
            return ret;
        } 
        ret = invokeGenerateTagsFromCraftingRecipes(item, meta, history);

        if (ret != null) {
            int vis = ret.getAmount(Aspects.VIS);
            if (vis > 0) {
                ret.remove(Aspects.VIS);
                int magic = Math.round((float)vis / 10.0f);
                ret.add(Aspect.MAGIC, magic);
            }
        }

        return ret;
     }

}
