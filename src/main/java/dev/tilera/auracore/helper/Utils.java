package dev.tilera.auracore.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;

import dev.tilera.auracore.api.IWand;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStem;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import thaumcraft.api.IGoggles;
import thaumcraft.api.IVisDiscountGear;

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
        if (bi instanceof IGrowable)
        {
            IGrowable igrowable = (IGrowable)bi;

            if (igrowable.func_149851_a(world, x, y, z, world.isRemote))
            {
                if (!world.isRemote)
                {
                    if (igrowable.func_149852_a(world, world.rand, x, y, z))
                    {
                        igrowable.func_149853_b(world, world.rand, x, y, z);
                    }
                }

                return true;
            }
        }
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

    public static int getTotalVisDiscount(final EntityPlayer player) {
        int total = 0;
        for (int a = 0; a < 4; ++a) {
            if (player.inventory.armorItemInSlot(a) != null
                && player.inventory.armorItemInSlot(a).getItem()
                        instanceof IVisDiscountGear) {
                total
                    += ((IVisDiscountGear) player.inventory.armorItemInSlot(a).getItem())
                           .getVisDiscount(
                               player.inventory.armorItemInSlot(a), player, null
                           );
            }
        }
        return total;
    }

    public static boolean hasCharge(ItemStack is, EntityPlayer pl, int c) {
        final int discount = 100 - Math.min(50, getTotalVisDiscount(pl));
        c = Math.round(c * (discount / 100.0f));
        return ((IWand) is.getItem()).getVis(is) >= c;
    }

    public static boolean spendCharge(final ItemStack itemstack, final EntityPlayer player, int amount) {
        final int discount = 100 - Math.min(50, getTotalVisDiscount(player));
        amount = Math.round(amount * (discount / 100.0f));
        return ((IWand) itemstack.getItem()).consumeVis(itemstack, amount);
    }

}
