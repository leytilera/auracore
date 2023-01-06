package dev.tilera.auracore.world;

import java.util.HashMap;
import java.util.Random;

import cpw.mods.fml.common.IWorldGenerator;
import dev.tilera.auracore.Config;
import dev.tilera.auracore.api.EnumNodeType;
import dev.tilera.auracore.aura.AuraManager;
import dev.tilera.auracore.aura.NodeIdStorage;
import dev.tilera.auracore.helper.Utils;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.WorldGenHilltopStones;
import thaumcraft.common.lib.world.WorldGenMound;

public class WorldGenerator implements IWorldGenerator {

    HashMap<Integer, Boolean> structureNode = new HashMap<>();

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        this.worldGeneration(random, chunkX, chunkZ, world, true);
    }

    public void worldGeneration(Random random, int chunkX, int chunkZ, World world, boolean newGen) {
        if (AuraManager.nodeIdStore == null) {
            AuraManager.nodeIdStore = new NodeIdStorage(world.getSaveHandler());
        }
        switch (world.provider.dimensionId) {
            case -1: {
                this.generateNether(world, random, chunkX, chunkZ, newGen);
                break;
            }
            case 1: {
                break;
            }
            default: {
                this.generateSurface(world, random, chunkX, chunkZ, newGen);
            }
        }
        if (!newGen) {
            world.getChunkFromChunkCoords(chunkX, chunkZ).setChunkModified();
        }
    }

    private void generateSurface(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
        boolean auraGen = false;
        if (!world.getWorldInfo().getTerrainType().getWorldTypeName().startsWith("flat") && newGen) {
            int randPosX2 = chunkX * 16 + random.nextInt(16);
            int randPosZ2 = chunkZ * 16 + random.nextInt(16);
            int randPosY = world.getHeightValue(randPosX2, randPosZ2) - 9;
            Chunk var1 = world.getChunkFromBlockCoords(MathHelper.floor_double((double)randPosX2), MathHelper.floor_double((double)randPosZ2));
            WorldGenMound mound = new WorldGenMound();
            if (var1.getRandomWithSeed(957234911L).nextInt(100) == 0 && !AuraManager.specificAuraTypeNearby(world.provider.dimensionId, randPosX2 + 9, randPosY + 8, randPosZ2 + 9, EnumNodeType.DARK, 250)) {
                if (mound.generate(world, random, randPosX2, randPosY, randPosZ2)) {
                    auraGen = true;
                    int value = random.nextInt(200) + 400;
                    AuraManager.registerAuraNode(world, (short)value, EnumNodeType.DARK, world.provider.dimensionId, randPosX2 + 9, randPosY + 8, randPosZ2 + 9);
                }
            } else {
                WorldGenHilltopStones hilltopStones = new WorldGenHilltopStones();
                if (random.nextInt(3) == 0 && !AuraManager.specificAuraTypeNearby(world.provider.dimensionId, randPosX2, randPosY += 9, randPosZ2, EnumNodeType.UNSTABLE, 250) && hilltopStones.generate(world, random, randPosX2, randPosY, randPosZ2)) {
                    auraGen = true;
                    int value = random.nextInt(200) + 400;
                    AuraManager.registerAuraNode(world, (short)value, EnumNodeType.UNSTABLE, world.provider.dimensionId, randPosX2, randPosY + 5, randPosZ2);
                }
            }
        }
        if (newGen) {
            ChunkPosition var7 = (new MapGenScatteredFeature()).func_151545_a(world, chunkX * 16 + 8, world.getHeightValue(chunkX * 16 + 8, chunkZ * 16 + 8), chunkZ * 16 + 8);
            if (var7 != null && !this.structureNode.containsKey(var7.hashCode())) {
                auraGen = true;
                this.structureNode.put(var7.hashCode(), true);
                int yPos = world.getHeightValue(var7.chunkPosX, var7.chunkPosZ) + 3;
                int nearKey = AuraManager.getClosestAuraWithinRange(world, var7.chunkPosX, yPos, var7.chunkPosZ, 10);
                if (nearKey < 0) {
                    int value = random.nextInt(200) + 800;
                    AuraManager.registerAuraNode(world, (short)value, EnumNodeType.NORMAL, world.provider.dimensionId, var7.chunkPosX, yPos, var7.chunkPosZ);
                }
            }
            auraGen = this.generateAura(world, random, chunkX, chunkZ, auraGen, newGen);
        }
    }

    private void generateNether(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
        if (newGen) {
            this.generateAura(world, random, chunkX, chunkZ, false, newGen);
        }
    }

    private boolean generateAura(World world, Random random, int chunkX, int chunkZ, boolean auraGen, boolean newGen) {
        if (random.nextInt(Config.nodeRarity) == 0 && !auraGen) {
            int y;
            int p;
            int z;
            int q;
            int x = chunkX * 16 + random.nextInt(16);
            if (world.isAirBlock(x, (q = Utils.getFirstUncoveredY(world, x, z = chunkZ * 16 + random.nextInt(16))) + 1, z)) {
                ++q;
            }
            if (world.isAirBlock(x, q + (p = random.nextInt(6)), z)) {
                q += p;
                if (p == 5) {
                    p = random.nextInt(5);
                }
                if (world.isAirBlock(x, q + p, z)) {
                    q += p;
                }
            }
            if (AuraManager.auraNearby(world.provider.dimensionId, x, y = q, z, 64)) {
                return false;
            }
            BiomeGenBase bg = world.getBiomeGenForCoords(x, z);
            boolean bbase = false;
            int value = random.nextInt(/*BiomeHandler.getBiomeAura(bg)*/400 / 2) + /*BiomeHandler.getBiomeAura(bg)*/400 / 2;
            EnumNodeType type = EnumNodeType.NORMAL;
            if (random.nextInt(Config.specialNodeRarity) == 0) {
                switch (random.nextInt(3)) {
                    case 0: {
                        type = EnumNodeType.PURE;
                        break;
                    }
                    case 1: {
                        type = EnumNodeType.DARK;
                        break;
                    }
                    case 2: {
                        type = EnumNodeType.UNSTABLE;
                    }
                }
            }
            if (newGen && random.nextInt(type != EnumNodeType.NORMAL ? 2 : (world.provider.dimensionId == -1 ? 2 : 6)) == 0) {
                int topy;
                int n = topy = world.provider.dimensionId == -1 ? Utils.getFirstUncoveredY(world, x, z) - 1 : world.getHeightValue(x, z) - 1;
                if (world.getBlock(x, topy, z) != null && world.getBlock(x, topy, z).isLeaves(world, x, topy, z)) {
                    while (world.getBlock(x, --topy, z) != Blocks.grass && topy > 40) {
                    }
                }
                if (world.getBlock(x, topy, z) == Blocks.snow_layer || world.getBlock(x, topy, z) == Blocks.tallgrass) {
                    --topy;
                }
                if (world.getBlock(x, topy, z) == Blocks.grass || world.getBlock(x, topy, z) == Blocks.sand || world.getBlock(x, topy, z) == Blocks.dirt || world.getBlock(x, topy, z) == Blocks.stone || world.getBlock(x, topy, z) == Blocks.netherrack) {
                    int count;
                    for (count = 1; (world.isAirBlock(x, topy + count, z) || world.getBlock(x, topy, z) == Blocks.snow_layer || world.getBlock(x, topy, z) == Blocks.tallgrass) && count < 3; ++count) {
                    }
                    if (count >= 2) {
                        world.setBlock(x, topy, z, ConfigBlocks.blockCosmeticSolid, 1, 3);
                        for (count = 1; (world.isAirBlock(x, topy + count, z) || world.getBlock(x, topy, z) == Blocks.snow_layer || world.getBlock(x, topy, z) == Blocks.tallgrass) && count < 5; ++count) {
                            world.setBlock(x, topy + count, z, ConfigBlocks.blockCosmeticSolid, 0, 3);
                            if (count <= 1 || random.nextInt(4) != 0) continue;
                            count = 5;
                        }
                    }
                }
            }
            AuraManager.registerAuraNode(world, (short)value, type, world.provider.dimensionId, x, y, z);
            return true;
        }
        return false;
    }

}
