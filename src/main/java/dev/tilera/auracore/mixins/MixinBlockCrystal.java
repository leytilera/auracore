package dev.tilera.auracore.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import dev.tilera.auracore.api.AuraNode;
import dev.tilera.auracore.api.ICrystal;
import dev.tilera.auracore.aura.AuraManager;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockCrystal;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileCrystal;
import thaumcraft.common.tiles.TileEldritchCrystal;

@Mixin(BlockCrystal.class)
public abstract class MixinBlockCrystal extends BlockContainer {

    protected MixinBlockCrystal(Material p_i45386_1_) {
        super(p_i45386_1_);
    }

    /**
     * @author tilera
     * @reason Old crystals
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Overwrite
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for(int var4 = 0; var4 <= 6; ++var4) {
            par3List.add(new ItemStack(par1, 1, var4));
        }
        par3List.add(new ItemStack(par1, 1, 8));
        par3List.add(new ItemStack(par1, 1, 9));
        par3List.add(new ItemStack(par1, 1, 10));
    }
    
    /**
     * @author tilera
     * @reason Old crystals
     */
    @Overwrite(remap = false)
    public TileEntity createTileEntity(World world, int metadata) {
        if (metadata == 7) {
            return new TileEldritchCrystal();
        } else {
            return new TileCrystal();
        }
    }

    /**
     * @author tilera
     * @reason Drop old crystals
     */
    @Overwrite(remap = false)
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int md, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<>();
        Integer count = ICrystal.crystalCounts.get(new ChunkCoordinates(x, y, z));
        if (md < 6 && count != null) {
            for(int t = 0; t < count.intValue(); ++t) {
                ret.add(new ItemStack(ConfigItems.itemShard, 1, md));
            }
        } else if ((md == 8 || md == 10) && count != null) {
            for(int t = 0; t < count.intValue(); ++t) {
                ret.add(new ItemStack(ConfigItems.itemShard, 1, md - 1));
            }
        } else if (md == 6) {
            for(int t = 0; t < 6; ++t) {
                ret.add(new ItemStack(ConfigItems.itemShard, 1, t));
            }
        } else if (md == 7) {
            ret.add(new ItemStack(ConfigItems.itemShard, 1, 6));
        } else if (md == 9) {
            for(int t = 0; t < 4; ++t) {
                ret.add(new ItemStack(ConfigItems.itemShard, 1, t));
            }
            ret.add(new ItemStack(ConfigItems.itemShard, 1, 7));
        } else {
            return super.getDrops(world, x, y, z, md, fortune);
        }
        return ret;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        TileEntity tile = world.getTileEntity(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (tile instanceof ICrystal) {
            ICrystal crystal = (ICrystal) tile;
            if (rand.nextInt(crystal.getCrystalCount(meta) * 75) == 0) {
                int nodeKey = AuraManager.getClosestAuraWithinRange(world, x, y, z, 64);
                if (nodeKey < 0) return;
                AuraNode node = AuraManager.getNode(nodeKey);
                if (node.level > node.baseLevel - 10) {
                    int crystals = crystal.getCrystalCount(meta);
                    crystal.setCrystalCount(crystals + 1);
                }
            }
        }
    }

}
