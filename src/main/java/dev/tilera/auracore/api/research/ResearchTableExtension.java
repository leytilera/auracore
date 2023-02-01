package dev.tilera.auracore.api.research;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class ResearchTableExtension {

    public WeakReference<IResearchTable>  researchTable;
    private Method markDirty;

    public ResearchTableExtension(IResearchTable researchTable) {
        this.researchTable = new WeakReference<>(researchTable);
        Class<? extends ResearchTableExtension> impl = this.getClass();
        try {
            markDirty = impl.getDeclaredMethod("markDirty", new Class[0]);
        } catch (Exception e) {
            markDirty = null;
        }
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

    public void extMarkDirty() {
        if (this instanceof IInventory) {
            ((IInventory)this).markDirty();
        } else if (markDirty != null) {
            try {
                markDirty.invoke(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public abstract boolean openGUI(EntityPlayer player);

    public abstract String getNBTKey();

}
