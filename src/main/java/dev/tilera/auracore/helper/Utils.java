package dev.tilera.auracore.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import thaumcraft.api.IGoggles;

public class Utils {
    
    public static boolean hasGoggles(EntityPlayer player) {
        ItemStack goggles = player.inventory.armorItemInSlot(3);
        return goggles != null && goggles.getItem() instanceof IGoggles && ((IGoggles)goggles.getItem()).showIngamePopups(goggles, player);
    }

    public static void sendChatNearby(World world, double x, double y, double z, double radius, String text) {
        //MinecraftServer.getServer().getConfigurationManager().sendChatNearby(x, y, z, radius, world.provider.dimensionId, (Packet)new Packet3Chat(text));
    }

    public static boolean isChunkLoaded(World world, int x, int z) {
        int xx = x / 16;
        int zz = z / 16;
        return world.getChunkProvider().chunkExists(xx, zz);
    }

    public static boolean useBonemealAtLoc(World world, int x, int y, int z) {
        Block bi = world.getBlock(x, y, z);
        if (bi == Blocks.sapling) {
            if (!world.isRemote) {
                ((BlockSapling)bi).func_149878_d(world, x, y, z, world.rand); //TODO func
            }
            return true;
        }
        if (bi == Blocks.brown_mushroom || bi == Blocks.red_mushroom_block) {
            if (!world.isRemote) {
                ((BlockMushroom)bi).func_149884_c(world, x, y, z, world.rand); //TODO func
            }
            return true;
        }
        if (bi == Blocks.melon_stem || bi == Blocks.pumpkin_stem) {
            if (world.getBlockMetadata(x, y, z) == 7) {
                return false;
            }
            if (!world.isRemote) {
                ((BlockStem)bi).func_149874_m(world, x, y, z); //TODO func
            }
            return true;
        }
        if (bi instanceof BlockCrops) {
            if (world.getBlockMetadata(x, y, z) == 7) {
                return false;
            }
            if (!world.isRemote) {
                ((BlockCrops)bi).func_149863_m(world, x, y, z); //TODO func
            }
            return true;
        }
        if (bi == Blocks.cocoa) {
            if (!world.isRemote) {
                world.setBlockMetadataWithNotify(x, y, z, 8 | BlockDirectional.getDirection((int)world.getBlockMetadata(x, y, z)), 3);
            }
            return true;
        }
        /*if (bi == Blocks.grass) {
            if (!world.isRemote) {
                block0: for (int var12 = 0; var12 < 128; ++var12) {
                    int var13 = x;
                    int var14 = y + 1;
                    int var15 = z;
                    for (int var16 = 0; var16 < var12 / 16; ++var16) {
                        if (world.getBlock(var13 += world.rand.nextInt(3) - 1, (var14 += (world.rand.nextInt(3) - 1) * world.rand.nextInt(3) / 2) - 1, var15 += world.rand.nextInt(3) - 1) != Blocks.grass || world.(var13, var14, var15)) continue block0;
                    }
                    if (world.getBlock(var13, var14, var15) != 0) continue;
                    if (world.rand.nextInt(10) != 0) {
                        if (!Block.field_71962_X.func_71854_d(world, var13, var14, var15)) continue;
                        world.func_72832_d(var13, var14, var15, Block.field_71962_X.field_71990_ca, 1, 3);
                        continue;
                        
                    }
                    ForgeHooks.plantGrass((World)world, (int)var13, (int)var14, (int)var15);
                }
            }
            return true;
        }*/ //TODO: WTF
        return false;
    }

    public static int getFirstUncoveredBlockHeight(World world, int par1, int par2) {
        int var3;
        for (var3 = 10; !world.isAirBlock(par1, var3 + 1, par2) || var3 > 250; ++var3) {
        }
        return var3;
    }

    public static boolean isBlockTouching(IBlockAccess world, int x, int y, int z, Block id, int md) {
        return world.getBlock(x, y, z + 1) == id && world.getBlockMetadata(x, y, z + 1) == md || world.getBlock(x, y, z - 1) == id && world.getBlockMetadata(x, y, z - 1) == md || world.getBlock(x + 1, y, z) == id && world.getBlockMetadata(x + 1, y, z) == md || world.getBlock(x - 1, y, z) == id && world.getBlockMetadata(x - 1, y, z) == md || world.getBlock(x, y + 1, z) == id && world.getBlockMetadata(x, y + 1, z) == md || world.getBlock(x, y - 1, z) == id && world.getBlockMetadata(x, y - 1, z) == md;
    }

    public static boolean isBlockTouching(IBlockAccess world, int x, int y, int z, Block id) {
        return world.getBlock(x, y, z + 1) == id || world.getBlock(x, y, z - 1) == id || world.getBlock(x + 1, y, z) == id || world.getBlock(x - 1, y, z) == id || world.getBlock(x, y + 1, z) == id || world.getBlock(x, y - 1, z) == id;
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        AbstractInterruptibleChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            ((FileChannel)destination).transferFrom(source, 0L, source.size());
        }
        finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static int getFirstUncoveredY(World world, int par1, int par2) {
        int var3 = 5;
        while (!world.isAirBlock(par1, var3 + 1, par2)) {
            ++var3;
        }
        return var3;
    }

}
