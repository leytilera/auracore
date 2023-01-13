package dev.tilera.auracore.api.research;

import java.lang.ref.WeakReference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class ResearchTableExtension {

    public WeakReference<IResearchTable>  researchTable;

    public ResearchTableExtension(IResearchTable researchTable) {
        this.researchTable = new WeakReference<>(researchTable);
    }

    public World getWorld() {
        return researchTable.get().getWorld();
    }

    public int getXCoord() {
        return researchTable.get().getXCoord();
    }

    public int getYCoord() {
        return researchTable.get().getYCoord();
    }

    public int getZCoord() {
        return researchTable.get().getZCoord();
    }

    public IResearchTable getResearchTable() {
        return researchTable.get();
    }

    public abstract void writeToNBT(NBTTagCompound nbt);

    public abstract void readFromNBT(NBTTagCompound nbt);

    public abstract void writeToPacket(NBTTagCompound nbt);

    public abstract void readFromPacket(NBTTagCompound nbt);

    public abstract void onTick();

    public abstract void markDirty();

    public abstract boolean openGUI(EntityPlayer player);

    public abstract String getNBTKey();

}
