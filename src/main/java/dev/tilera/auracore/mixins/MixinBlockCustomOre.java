package dev.tilera.auracore.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dev.tilera.auracore.api.AuraNode;
import dev.tilera.auracore.aura.AuraManager;
import dev.tilera.auracore.helper.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blocks.BlockCustomOre;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;
import thaumcraft.common.lib.world.biomes.BiomeHandler;

@Mixin(BlockCustomOre.class)
public abstract class MixinBlockCustomOre extends Block {

    MixinBlockCustomOre(Material p_i45394_1_) {
        super(p_i45394_1_);
    }

    /**
     * @author tilera
     * @reason Vis, Tainted and Dull ores
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Overwrite
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
        par3List.add(new ItemStack(par1, 1, 2));
        par3List.add(new ItemStack(par1, 1, 3));
        par3List.add(new ItemStack(par1, 1, 4));
        par3List.add(new ItemStack(par1, 1, 5));
        par3List.add(new ItemStack(par1, 1, 6));
        par3List.add(new ItemStack(par1, 1, 7));
        par3List.add(new ItemStack(par1, 1, 8));
        par3List.add(new ItemStack(par1, 1, 9));
        par3List.add(new ItemStack(par1, 1, 10));
    }

    @Override
    public void updateTick(final World world, final int x, final int y, final int z, final Random rand) {
        if (world.isRemote) {
            return;
        }
        try {
            final int md = world.getBlockMetadata(x, y, z);
            if (md != 0 && md != 7 && md != 10 && rand.nextInt(500) == 42) {
                if (world.getBiomeGenForCoords(x, z).biomeID == ThaumcraftWorldGenerator.biomeTaint.biomeID) {
                    world.setBlock(x, y, z, this, 10, 3);
                    return;
                }
                final ArrayList<Integer> nodes = AuraManager.getAurasWithin(world, x + 0.5f, y + 0.5f, z + 0.5f);
                if (nodes.size() == 0) {
                    return;
                }
                if (md == 9) {
                    for (final Integer key : nodes) {
                        final AuraNode nd = AuraManager.getNode(key);
                        if (nd == null) {
                            continue;
                        }
                        if (nd.level - 10 >= nd.baseLevel) {
                            AuraManager.queueNodeChanges(nd.key, -10, 0, false, null, 0.0f, 0.0f, 0.0f);
                            int tmd = 0;
                            if (Utils.isBlockTouching(world, x, y, z, this, 1)) {
                                tmd = 1;
                            } else if (Utils.isBlockTouching(world, x, y, z, this, 2)) {
                                tmd = 2;
                            } else if (Utils.isBlockTouching(world, x, y, z, this, 3)) {
                                tmd = 3;
                            } else if (Utils.isBlockTouching(world, x, y, z, this, 4)) {
                                tmd = 4;
                            } else if (Utils.isBlockTouching(world, x, y, z, this, 5)) {
                                tmd = 5;
                            } else if (Utils.isBlockTouching(world, x, y, z, this, 6)) {
                                tmd = 6;
                            } else if (Utils.isBlockTouching(world, x, y, z, this, 8)) {
                                tmd = 8;
                            } else {
                                Aspect aspect = BiomeHandler.getRandomBiomeTag(world.getBiomeGenForCoords(x, z).biomeID, rand);
                                if (aspect == Aspect.AIR) {
                                    tmd = 1;
                                } else if (aspect == Aspect.FIRE) {
                                    tmd = 2;
                                } else if (aspect == Aspect.WATER) {
                                    tmd = 3;
                                } else if (aspect == Aspect.EARTH) {
                                    tmd = 4;
                                } else if (aspect == Aspect.ORDER) {
                                    tmd = 5;
                                } else if (aspect == Aspect.ENTROPY) {
                                    tmd = 6;
                                } else if (aspect == Aspect.MAGIC) {
                                    tmd = 8;
                                } else if (aspect == Aspect.TAINT) {
                                    tmd = 10;
                                } else {
                                    tmd = rand.nextInt(6) + 1;
                                }
                            }
                            world.setBlock(x, y, z, this, tmd, 3);
                            break;
                        }
                    }
                } else {
                    for (final Integer key : nodes) {
                        final AuraNode nd = AuraManager.getNode(key);
                        if (nd == null) {
                            continue;
                        }
                        if (nd.level + 10 <= nd.baseLevel) {
                            AuraManager.queueNodeChanges(nd.key, 10, 0, false, null, 0.0f, 0.0f, 0.0f);
                            world.setBlock(x, y, z, this, 9, 3);
                            break;
                        }
                        if (rand.nextInt(50) != 42 || nd.level - 100 < nd.baseLevel) {
                            continue;
                        }
                        for (int a = 0; a < 6; ++a) {
                            if (world.getBlock(x + ForgeDirection.getOrientation(a).offsetX,
                                    y + ForgeDirection.getOrientation(a).offsetY,
                                    z + ForgeDirection.getOrientation(a).offsetZ) == Blocks.stone) {
                                AuraManager.queueNodeChanges(nd.key, -50, 0, false, null, 0.0f, 0.0f, 0.0f);
                                world.setBlock(x + ForgeDirection.getOrientation(a).offsetX,
                                        y + ForgeDirection.getOrientation(a).offsetY,
                                        z + ForgeDirection.getOrientation(a).offsetZ, ConfigBlocks.blockCustomOre, md,
                                        3);
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
