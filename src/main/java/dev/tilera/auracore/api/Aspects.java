package dev.tilera.auracore.api;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;

public class Aspects {

    public static final Aspect INSECT = new Aspect("bestiola", 0x808880, new Aspect[] {Aspect.POISON, Aspect.BEAST}, new ResourceLocation("auracore", "textures/aspects/bestiola.png"), 1);
    public static final Aspect EVIL = new Aspect("malum", 0x700000, new Aspect[] {Aspect.TAINT, Aspect.FIRE}, new ResourceLocation("auracore", "textures/aspects/malum.png"), 1);
    public static final Aspect FLUX = new Aspect("mutatio", 12061625, new Aspect[] {Aspect.MAGIC, Aspects.EVIL}, new ResourceLocation("auracore", "textures/aspects/mutatio.png"), 1);
    public static final Aspect SOUND = new Aspect("sonus", 1100224, new Aspect[] {Aspect.SENSES, Aspect.AIR}, new ResourceLocation("auracore", "textures/aspects/sonus.png"), 1);
    public static final Aspect VISION = new Aspect("visum", 14013676, new Aspect[] {Aspect.SENSES, Aspect.LIGHT}, new ResourceLocation("auracore", "textures/aspects/visum.png"), 1);
    public static final Aspect TIME = new Aspect("tempus", 9466080, new Aspect[] {Aspect.VOID, Aspect.ORDER}, new ResourceLocation("auracore", "textures/aspects/tempus.png"), 1);
    public static final Aspect ROCK = new Aspect("saxum", 6047810, new Aspect[] {Aspect.EARTH, Aspect.EARTH}, new ResourceLocation("auracore", "textures/aspects/saxum.png"), 1);
    public static final Aspect DESTRUCTION = new Aspect("fractus", 0x506050, new Aspect[] {Aspect.ENTROPY, Aspect.ENTROPY}, new ResourceLocation("auracore", "textures/aspects/fractus.png"), 1);
    public static final Aspect PURE = new Aspect("purus", 10878973, new Aspect[] {Aspect.CRYSTAL, Aspect.ORDER}, new ResourceLocation("auracore", "textures/aspects/purus.png"), 1);
    public static final Aspect VALUABLE = new Aspect("carus", 15121988, new Aspect[] {Aspect.GREED, Aspects.PURE}, new ResourceLocation("auracore", "textures/aspects/carus.png"), 1);
    public static final Aspect CONTROL = new Aspect("imperito", 10000715, new Aspect[] {Aspect.MIND, Aspect.ORDER}, new ResourceLocation("auracore", "textures/aspects/imperito.png"), 1);
    public static final Aspect SOIL = new Aspect("solum", 7421741, new Aspect[] {Aspect.EARTH, Aspects.ROCK}, new ResourceLocation("auracore", "textures/aspects/solum.png"), 1);
    public static final Aspect FLOWER = new Aspect("flos", 0xFFFF40, new Aspect[] {Aspect.PLANT, Aspects.SOIL}, new ResourceLocation("auracore", "textures/aspects/flos.png"), 1);
    public static final Aspect FUNGUS = new Aspect("fungus", 16246215, new Aspect[] {Aspect.PLANT, Aspect.TREE}, new ResourceLocation("auracore", "textures/aspects/fungus.png"), 1);

    public static void load() {}

}
