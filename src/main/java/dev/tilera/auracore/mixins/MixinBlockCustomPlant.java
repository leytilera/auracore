package dev.tilera.auracore.mixins;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import dev.tilera.auracore.api.EnumNodeType;
import dev.tilera.auracore.aura.AuraManager;
import dev.tilera.auracore.world.WorldGenSilverwoodTreesOld;
import net.minecraft.block.BlockBush;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockCustomPlant;

@Mixin(BlockCustomPlant.class)
public abstract class MixinBlockCustomPlant extends BlockBush {
    
    /**
     * @author tilera
     * @reason Old Silverwood tree
     */
    @Overwrite(remap = false)
    public void growSilverTree(World world, int i, int j, int k, Random random) {
        if (world == null || world.provider == null) {
            return;
        }
        world.setBlock(i, j, k, Blocks.air, 0, 3);
        WorldGenSilverwoodTreesOld obj = new WorldGenSilverwoodTreesOld(true);
        int value = random.nextInt(50) + 50;
        if (!AuraManager.decreaseClosestAura(world, i, j, k, (int)((float)value * 1.5f), false) || !obj.generate(world, random, i, j, k)) {
            world.setBlock(i, j, k, this, 1, 0);
        } else {
            try {
                if (AuraManager.decreaseClosestAura(world, i, j, k, (int)((float)value * 1.5f))) {
                    AuraManager.registerAuraNode(world, (short)value, EnumNodeType.PURE, world.provider.dimensionId, i, j + 1, k);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
     }

}
