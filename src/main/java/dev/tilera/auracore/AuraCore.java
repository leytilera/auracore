package dev.tilera.auracore;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import dev.tilera.auracore.api.Aspects;
import dev.tilera.auracore.aura.AuraCalculationThread;
import dev.tilera.auracore.aura.AuraDeleteThread;
import dev.tilera.auracore.aura.AuraManager;
import dev.tilera.auracore.aura.AuraUpdateThread;
import dev.tilera.auracore.aura.AuraWorldTicker;
import dev.tilera.auracore.client.GUITicker;
import dev.tilera.auracore.client.RenderEventHandler;
import dev.tilera.auracore.proxy.CommonProxy;
import dev.tilera.auracore.world.WorldGenerator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

@Mod(modid = "auracore", name = "AuraCore", version = "{VERSION}", dependencies = "required-after:Thaumcraft")
public class AuraCore {

    public static SimpleNetworkWrapper CHANNEL;
    @Mod.Instance("auracore")
    public static AuraCore INSTANCE;
    @SidedProxy(modId = "auracore", clientSide = "dev.tilera.auracore.proxy.ClientProxy", serverSide = "dev.tilera.auracore.proxy.CommonProxy")
    public static CommonProxy proxy;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        Config.load();
        Aspects.load();
        CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("auracore");
        proxy.preInit();
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
        FMLCommonHandler.instance().bus().register(new AuraWorldTicker());
        FMLCommonHandler.instance().bus().register(new GUITicker());
        Thread auraCalcThread = new Thread(new AuraCalculationThread());
        auraCalcThread.setName("TC Aura Calculation Thread");
        auraCalcThread.start();
        Thread auraDelThread = new Thread(new AuraDeleteThread());
        auraDelThread.setName("TC Aura Deletion Thread");
        auraDelThread.start();
        Thread auraUpdateThread = new Thread(new AuraUpdateThread());
        auraUpdateThread.setName("TC Aura Update Thread");
        auraUpdateThread.start();
        GameRegistry.registerWorldGenerator(new WorldGenerator(), 100);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent ev) {
        ConfigBlocks.blockCrystal.setTickRandomly(true);
        Recipes.initRecipes();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 0), new AspectList().add(Aspect.MAGIC, 2).add(Aspect.AIR, 2).add(Aspect.CRYSTAL, 2).add(Aspect.MOTION, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 1), new AspectList().add(Aspect.MAGIC, 2).add(Aspect.FIRE, 2).add(Aspect.CRYSTAL, 2).add(Aspect.ENERGY, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 2), new AspectList().add(Aspect.MAGIC, 2).add(Aspect.WATER, 2).add(Aspect.CRYSTAL, 2).add(Aspect.COLD, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 3), new AspectList().add(Aspect.MAGIC, 2).add(Aspect.EARTH, 2).add(Aspect.CRYSTAL, 2).add(Aspects.ROCK, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 4), new AspectList().add(Aspect.MAGIC, 2).add(Aspect.ORDER, 2).add(Aspect.CRYSTAL, 2).add(Aspects.CONTROL, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 5), new AspectList().add(Aspect.MAGIC, 2).add(Aspect.ENTROPY, 2).add(Aspect.CRYSTAL, 2).add(Aspects.DESTRUCTION, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 7), new AspectList().add(Aspect.MAGIC, 6).add(Aspect.CRYSTAL, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 8), new AspectList().add(Aspect.MAGIC, 2).add(Aspect.CRYSTAL, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigItems.itemShard, 1, 9), new AspectList().add(Aspect.MAGIC, 2).add(Aspect.TAINT, 2).add(Aspect.CRYSTAL, 2).add(Aspects.FLUX, 2));

        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomOre, 1, 8), new AspectList().add(Aspect.EARTH, 1).add(Aspect.MAGIC, 3).add(Aspect.CRYSTAL, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomOre, 1, 9), new AspectList().add(Aspect.EARTH, 1).add(Aspect.CRYSTAL, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(ConfigBlocks.blockCustomOre, 1, 10), new AspectList().add(Aspect.EARTH, 1).add(Aspect.TAINT, 3).add(Aspect.CRYSTAL, 2));
    }

    @Mod.EventHandler
    public void onServerLoad(FMLServerAboutToStartEvent event) {
        AuraManager.invalidate();
    }

    @Mod.EventHandler
    public void onServerStop(FMLServerStoppedEvent event) {
        AuraManager.invalidate();
    }

}
