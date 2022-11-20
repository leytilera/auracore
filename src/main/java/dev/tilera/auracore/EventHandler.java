package dev.tilera.auracore;

import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dev.tilera.auracore.api.AuraNode;
import dev.tilera.auracore.api.EnumNodeType;
import dev.tilera.auracore.aura.AuraManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.world.ChunkDataEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class EventHandler {

    @SubscribeEvent
    public void chunkSave(ChunkDataEvent.Save event) {
        NBTTagList nodelist = new NBTTagList();
        Object object = AuraManager.saveLock;
        synchronized (object) {
            List<Integer> nds = AuraManager.nodeChunks.get(Arrays.asList(event.world.provider.dimensionId, event.getChunk().xPosition, event.getChunk().zPosition));
            if (nds != null && nds.size() > 0) {
                for (Integer key : nds) {
                    AuraNode node = AuraManager.getNode(key);
                    if (node == null) continue;
                    NBTTagCompound nodeNBT = new NBTTagCompound();
                    nodeNBT.setInteger("key", node.key);
                    nodeNBT.setShort("level", node.level);
                    nodeNBT.setShort("baseLevel", node.baseLevel);
                    nodeNBT.setShort("taint", node.taint);
                    nodeNBT.setByte("type", (byte)node.type.ordinal());
                    nodeNBT.setDouble("xPos", node.xPos);
                    nodeNBT.setDouble("yPos", node.yPos);
                    nodeNBT.setDouble("zPos", node.zPos);
                    nodeNBT.setBoolean("locked", node.locked);
                    nodeNBT.setBoolean("isVirtual", node.isVirtual);
                    NBTTagList flux = new NBTTagList();
                    if (node.flux.size() > 0) {
                        for (Aspect tag : node.flux.getAspects()) {
                            if (tag == null) continue;
                            NBTTagCompound f = new NBTTagCompound();
                            f.setString("id", tag.getTag());
                            f.setInteger("amount", node.flux.getAmount(tag));
                            flux.appendTag(f);
                        }
                    }
                    nodeNBT.setTag("flux", flux);
                    nodelist.appendTag(nodeNBT);
                }
            }
            event.getData().setTag("TCNODES", nodelist);
        }
    }

    @SubscribeEvent
    public void chunkLoad(ChunkDataEvent.Load event) {
        if (event.getData().hasKey("TCNODES")) {
            Object object = AuraManager.saveLock;
            synchronized (object) {
                NBTTagList nodeList = event.getData().getTagList("TCNODES", 10);
                for (int i = 0; i < nodeList.tagCount(); ++i) {
                    NBTTagCompound nodeData = nodeList.getCompoundTagAt(i);
                    AuraNode node = new AuraNode();
                    node.key = nodeData.getInteger("key");
                    node.dimension = event.world.provider.dimensionId;
                    node.level = nodeData.getShort("level");
                    node.baseLevel = nodeData.getShort("baseLevel");
                    node.locked = nodeData.getBoolean("locked");
                    if(nodeData.hasKey("taint"))
                        node.taint = nodeData.getShort("taint");
                    if(nodeData.hasKey("isVirtual"))
                        node.isVirtual = nodeData.getBoolean("isVirtual");
                    node.type = EnumNodeType.getType(nodeData.getByte("type"));
                    node.xPos = nodeData.getDouble("xPos");
                    node.yPos = nodeData.getDouble("yPos");
                    node.zPos = nodeData.getDouble("zPos");
                    node.flux = new AspectList();
                    NBTTagList fluxTags = nodeData.getTagList("flux", 10);
                    for (int j = 0; j < fluxTags.tagCount(); ++j) {
                        NBTTagCompound flux = fluxTags.getCompoundTagAt(j);
                        if (!flux.hasKey("id") || !flux.hasKey("amount")) continue;
                        node.flux.add(Aspect.getAspect(flux.getString("id")), flux.getInteger("amount"));
                    }
                    AuraManager.auraNodes.put(node.key, node);
                    AuraManager.addToAuraUpdateList(node);
                    AuraManager.generateNodeNeighbours(node);
                }
            }
        }
    }

}
