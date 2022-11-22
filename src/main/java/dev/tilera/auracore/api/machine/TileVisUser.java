package dev.tilera.auracore.api.machine;

import dev.tilera.auracore.api.HelperLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileVisUser extends TileEntity implements IConnection
{
    public int visSuction;
    public int taintSuction;
    
    public TileVisUser() {
        this.visSuction = 0;
        this.taintSuction = 0;
    }
    
    @Override
    public void updateEntity() {
        super.updateEntity();
        this.setSuction(0);
    }
    
    public boolean getExactPureVis(float amount) {
        this.setVisSuction(50);
        int x = super.xCoord;
        int y = super.yCoord;
        int z = super.zCoord;
        for (ForgeDirection side : ForgeDirection.values()) {
            int offX = x + side.offsetX;
            int offY = y + side.offsetY;
            int offZ = z + side.offsetZ;
            if (this.getConnectable(side)) {
                TileEntity te = worldObj.getTileEntity(offX, offY, offZ);
                IConnection ic = te instanceof IConnection ? (IConnection) te : null;
                if (ic != null && (ic.isVisConduit() || ic.isVisSource()) && ic.getPureVis() >= amount) {
                    ic.setPureVis(ic.getPureVis() - amount);
                    return true;
                }
            }
        }
        return false;
    }
    
    public float getAvailablePureVis(float amount) {
        this.setVisSuction(50);
        int x = super.xCoord;
        int y = super.yCoord;
        int z = super.zCoord;
        float gatheredVis = 0.0f;
        for (ForgeDirection side : ForgeDirection.values()) {
            int offX = x + side.offsetX;
            int offY = y + side.offsetY;
            int offZ = z + side.offsetZ;
            if (this.getConnectable(side)) {
                TileEntity te = worldObj.getTileEntity(offX, offY, offZ);
                IConnection ic = te instanceof IConnection ? (IConnection) te : null;
                if (ic != null && (ic.isVisConduit() || ic.isVisSource())) {
                    float sucked = Math.min(amount - gatheredVis, ic.getPureVis());
                    if (sucked < 0.001f) {
                        sucked = 0.0f;
                    }
                    gatheredVis += sucked;
                    ic.setPureVis(ic.getPureVis() - sucked);
                }
                if (gatheredVis >= amount) {
                    break;
                }
            }
        }
        return Math.min(gatheredVis, amount);
    }
    
    public float getAvailableTaintedVis(float amount) {
        this.setTaintSuction(50);
        int x = super.xCoord;
        int y = super.yCoord;
        int z = super.zCoord;
        float gatheredVis = 0.0f;
        for (ForgeDirection side : ForgeDirection.values()) {
            int offX = x + side.offsetX;
            int offY = y + side.offsetY;
            int offZ = z + side.offsetZ;
            if (this.getConnectable(side)) {
                TileEntity te = worldObj.getTileEntity(offX, offY, offZ);
                IConnection ic = te instanceof IConnection ? (IConnection) te : null;
                if (ic != null && (ic.isVisConduit() || ic.isVisSource())) {
                    float sucked = Math.min(amount - gatheredVis, ic.getTaintedVis());
                    if (sucked < 0.001f) {
                        sucked = 0.0f;
                    }
                    gatheredVis += sucked;
                    ic.setTaintedVis(ic.getTaintedVis() - sucked);
                }
                if (gatheredVis >= amount) {
                    break;
                }
            }
        }
        return Math.min(gatheredVis, amount);
    }
    
    protected boolean gettingPower() {
        return super.worldObj.isBlockIndirectlyGettingPowered(super.xCoord, super.yCoord, super.zCoord) || super.worldObj.isBlockIndirectlyGettingPowered(super.xCoord, super.yCoord + 1, super.zCoord);
    }
    
    @Override
    public boolean getConnectable(ForgeDirection face) {
        return false;
    }
    
    @Override
    public boolean isVisSource() {
        return false;
    }
    
    @Override
    public boolean isVisConduit() {
        return false;
    }
    
    @Override
    public float[] subtractVis(float amount) {
        return new float[] { 0.0f, 0.0f };
    }
    
    @Override
    public float getPureVis() {
        return 0.0f;
    }
    
    @Override
    public void setPureVis(float amount) {
    }
    
    @Override
    public float getTaintedVis() {
        return 0.0f;
    }
    
    @Override
    public float getMaxVis() {
        return 0.0f;
    }
    
    @Override
    public void setTaintedVis(float amount) {
    }
    
    @Override
    public int getVisSuction(HelperLocation loc) {
        return this.visSuction;
    }
    
    @Override
    public void setVisSuction(int suction) {
        this.visSuction = suction;
    }
    
    @Override
    public int getTaintSuction(HelperLocation loc) {
        return this.taintSuction;
    }
    
    @Override
    public void setTaintSuction(int suction) {
        this.taintSuction = suction;
    }
    
    @Override
    public void setSuction(int suction) {
        this.visSuction = suction;
        this.taintSuction = suction;
    }
    
    @Override
    public int getSuction(HelperLocation loc) {
        return Math.max(this.visSuction, this.taintSuction);
    }
}
