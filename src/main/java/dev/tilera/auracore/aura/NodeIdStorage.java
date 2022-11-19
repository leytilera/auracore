package dev.tilera.auracore.aura;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.world.storage.ISaveHandler;

public class NodeIdStorage {
    private ISaveHandler saveHandler;
    private Map<String, Integer> idCounts = new HashMap<>();

    public NodeIdStorage(ISaveHandler par1ISaveHandler) {
        this.saveHandler = par1ISaveHandler;
        this.loadIdCounts();
    }

    private void loadIdCounts() {
        try {
            this.idCounts.clear();
            if (this.saveHandler == null) {
                return;
            }
            File var1 = this.saveHandler.getMapFileFromName("idcounts");
            if (var1 != null && var1.exists()) {
                DataInputStream var2 = new DataInputStream(new FileInputStream(var1));
                NBTTagCompound var3 = CompressedStreamTools.read(var2);
                var2.close();
                for (String key : (Set<String>)var3.func_150296_c()) {
                    NBTBase var5 = var3.getTag(key);
                    if (!(var5 instanceof NBTTagInt)) continue;
                    NBTTagInt var6 = (NBTTagInt)var5;
                    int var8 = var6.func_150287_d();
                    this.idCounts.put(key, var8);
                }
            }
        }
        catch (Exception var9) {
            var9.printStackTrace();
        }
    }

    public int getUniqueDataId(String par1Str) {
        Integer var2 = (Integer)this.idCounts.get(par1Str);
        var2 = var2 == null ? Integer.valueOf(0) : Integer.valueOf(var2 + 1);
        this.idCounts.put(par1Str, var2);
        if (this.saveHandler == null) {
            return var2;
        }
        try {
            File var3 = this.saveHandler.getMapFileFromName("idcounts");
            if (var3 != null) {
                NBTTagCompound var4 = new NBTTagCompound();
                for (String var6 : this.idCounts.keySet()) {
                    int var7 = (Integer)this.idCounts.get(var6);
                    var4.setInteger(var6, var7);
                }
                DataOutputStream var9 = new DataOutputStream(new FileOutputStream(var3));
                CompressedStreamTools.write((NBTTagCompound)var4, (DataOutput)var9);
                var9.close();
            }
        }
        catch (Exception var8) {
            var8.printStackTrace();
        }
        return var2;
    }
}
