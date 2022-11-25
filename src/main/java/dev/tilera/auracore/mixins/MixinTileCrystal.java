package dev.tilera.auracore.mixins;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import dev.tilera.auracore.api.ICrystal;
import dev.tilera.auracore.aura.AuraManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileCrystal;

@Mixin(TileCrystal.class)
public abstract class MixinTileCrystal extends TileThaumcraft implements ICrystal {
    
    private long count = System.currentTimeMillis() + (long)new Random().nextInt(300000);
    @Shadow(remap = false)
    public short orientation;
    public int crystals = 6;
    public int ticks = 0;
    public boolean isLoaded = false;

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (this.worldObj.isRemote) {
            return;
        }
        if (!isLoaded) {
            isLoaded = true;
            ICrystal.crystalCounts.put(new ChunkCoordinates(xCoord, yCoord, zCoord), crystals);
        }
        ticks++;
        int md = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if (md == 6) {
            crystals = 6;
        } else if (md == 9) {
            crystals = 5;
        } 
        if (md == 7 || md == 10 || crystals < 6) {
            return;
        }
        if (this.count <= System.currentTimeMillis()) {
            try {
                this.count = System.currentTimeMillis() + 300000L;
                AuraManager.increaseLowestAuraWithLimit(this.worldObj, (float)this.xCoord + 0.5f, (float)this.yCoord + 0.5f, (float)this.zCoord + 0.5f, 1, 1.1f);
            }
            catch (Exception exception) {
                
            }
        }
    }

    /**
     * @author tilera
     * @reason Crystal count
     */
    @Overwrite(remap = false)
    public void readCustomNBT(NBTTagCompound nbttagcompound) {
        super.readCustomNBT(nbttagcompound);
        this.orientation = nbttagcompound.getShort("orientation");
        if (nbttagcompound.hasKey("crystals"));
            this.crystals = nbttagcompound.getInteger("crystals");
        ICrystal.crystalCounts.put(new ChunkCoordinates(xCoord, yCoord, zCoord), crystals);
    }
  
    /**
     * @author tilera
     * @reason Crystal count
     */
    @Overwrite(remap = false)
    public void writeCustomNBT(NBTTagCompound nbttagcompound) {
        super.writeCustomNBT(nbttagcompound);
        nbttagcompound.setShort("orientation", this.orientation);
        nbttagcompound.setInteger("crystals", this.crystals);
    }

    @Override
    public int getCrystalCount(int meta) {
        if (meta == 6) return 6;
        if (meta == 9) return 5;
        return crystals;
    }

    @Override
    public boolean setCrystalCount(int count) {
        int md = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if(md == 6 || md == 9 || count > 6 || count < 1) return false;
        crystals = count;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        ICrystal.crystalCounts.put(new ChunkCoordinates(xCoord, yCoord, zCoord), crystals);
        return true;
    }

    @Override
    public void harvestShard(EntityPlayer player) {
        if(worldObj.isRemote || !setCrystalCount(crystals - 1)) return;
        int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if (meta > 7) {
            meta--;
        }
        ItemStack stack = new ItemStack(ConfigItems.itemShard, 1, meta);
        EntityItem entity = new EntityItem(worldObj, xCoord, yCoord, zCoord, stack);
        entity.motionX = (player.posX - entity.posX) * 0.1;
        entity.motionY = (player.posY - entity.posY) * 0.1;
        entity.motionZ = (player.posZ - entity.posZ) * 0.1;
        worldObj.spawnEntityInWorld(entity);
    }

}
