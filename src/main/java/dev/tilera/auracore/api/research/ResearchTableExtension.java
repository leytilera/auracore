package dev.tilera.auracore.api.research;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class ResearchTableExtension {

    public World world;
    public int xCoord;
    public int yCoord;
    public int zCoord;

    public ResearchTableExtension(IResearchTable researchTable) {
        this.world = researchTable.getWorld();
        this.xCoord = researchTable.getXCoord();
        this.yCoord = researchTable.getYCoord();
        this.zCoord = researchTable.getZCoord();
    }

    public IResearchTable getResearchTable() {
        TileEntity te = this.world.getTileEntity(this.xCoord, this.yCoord, this.zCoord);
        if (te instanceof IResearchTable) {
            return (IResearchTable) te;
        }
        return null;
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
