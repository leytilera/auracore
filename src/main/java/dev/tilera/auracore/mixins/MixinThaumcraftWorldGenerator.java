package dev.tilera.auracore.mixins;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import dev.tilera.auracore.api.EnumNodeType;
import dev.tilera.auracore.aura.AuraManager;
import dev.tilera.auracore.world.WorldGenSilverwoodTreesOld;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;
import thaumcraft.common.lib.world.biomes.BiomeHandler;

@Mixin(ThaumcraftWorldGenerator.class)
public abstract class MixinThaumcraftWorldGenerator {
    
    /**
     * @author tilera
     * @reason Old Silverwood trees
     */
    @Overwrite(remap = false)
    public static boolean generateSilverwood(World world, Random random, int chunkX, int chunkZ) {
        int x = chunkX * 16 + random.nextInt(16);
        int z = chunkZ * 16 + random.nextInt(16);
        int y = world.getHeightValue(x, z);
        BiomeGenBase bio = world.getBiomeGenForCoords(x, z);
        if (bio.equals(ThaumcraftWorldGenerator.biomeMagicalForest) || bio.equals(ThaumcraftWorldGenerator.biomeTaint) || !BiomeDictionary.isBiomeOfType(bio, Type.MAGICAL) && bio.biomeID != BiomeGenBase.forestHills.biomeID && bio.biomeID != BiomeGenBase.birchForestHills.biomeID) {
           return false;
        } else {
           boolean t = (new WorldGenSilverwoodTreesOld(false)).generate(world, random, x, y, z);
           if (t) {
            int value = random.nextInt(200) + 200;
            AuraManager.registerAuraNode(world, (short)value, EnumNodeType.PURE, world.provider.dimensionId, x, y + 1, z);
            ThaumcraftWorldGenerator.generateFlowers(world, random, x, y, z, 2);
           }
           return t;
        }
     }

     /**
     * @author tilera
     * @reason Generate infused ores
     */
     @Overwrite(remap = false)
     private void generateOres(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
        BiomeGenBase bgb = world.getBiomeGenForCoords(chunkX * 16 + 8, chunkZ * 16 + 8);
        if (ThaumcraftWorldGenerator.getBiomeBlacklist(bgb.biomeID) != 0 && ThaumcraftWorldGenerator.getBiomeBlacklist(bgb.biomeID) != 2) {
           int i;
           int randPosX;
           int randPosZ;
           int randPosY;
           Block block;
           if (Config.genCinnibar && (newGen || Config.regenCinnibar)) {
              for(i = 0; i < 18; ++i) {
                 randPosX = chunkX * 16 + random.nextInt(16);
                 randPosZ = random.nextInt(world.getHeight() / 5);
                 randPosY = chunkZ * 16 + random.nextInt(16);
                 block = world.getBlock(randPosX, randPosZ, randPosY);
                 if (block != null && block.isReplaceableOreGen(world, randPosX, randPosZ, randPosY, Blocks.stone)) {
                    world.setBlock(randPosX, randPosZ, randPosY, ConfigBlocks.blockCustomOre, 0, 0);
                 }
              }
           }
  
           if (Config.genAmber && (newGen || Config.regenAmber)) {
              for(i = 0; i < 20; ++i) {
                 randPosX = chunkX * 16 + random.nextInt(16);
                 randPosZ = chunkZ * 16 + random.nextInt(16);
                 randPosY = world.getHeightValue(randPosX, randPosZ) - random.nextInt(25);
                 block = world.getBlock(randPosX, randPosY, randPosZ);
                 if (block != null && block.isReplaceableOreGen(world, randPosX, randPosY, randPosZ, Blocks.stone)) {
                    world.setBlock(randPosX, randPosY, randPosZ, ConfigBlocks.blockCustomOre, 7, 2);
                 }
              }
           }
  
           if (Config.genInfusedStone && (newGen || Config.regenInfusedStone)) {
              for(i = 0; i < 8; ++i) {
                 randPosX = chunkX * 16 + random.nextInt(16);
                 randPosZ = chunkZ * 16 + random.nextInt(16);
                 randPosY = random.nextInt(Math.max(5, world.getHeightValue(randPosX, randPosZ) - 5));
                 int md = random.nextInt(7) + 1;
                 if (random.nextInt(3) == 0) {
                    Aspect tag = BiomeHandler.getRandomBiomeTag(world.getBiomeGenForCoords(randPosX, randPosZ).biomeID, random);
                    if (tag == null) {
                       md = 1 + random.nextInt(7);
                    } else if (tag == Aspect.AIR) {
                       md = 1;
                    } else if (tag == Aspect.FIRE) {
                       md = 2;
                    } else if (tag == Aspect.WATER) {
                       md = 3;
                    } else if (tag == Aspect.EARTH) {
                       md = 4;
                    } else if (tag == Aspect.ORDER) {
                       md = 5;
                    } else if (tag == Aspect.ENTROPY) {
                       md = 6;
                    }
                 }
                 if (md > 6) {
                     md = 8;
                 }
  
                 try {
                    (new WorldGenMinable(ConfigBlocks.blockCustomOre, md, 6, Blocks.stone)).generate(world, random, randPosX, randPosY, randPosZ);
                 } catch (Exception var13) {
                    var13.printStackTrace();
                 }
              }
           }
  
        }
     }

}
