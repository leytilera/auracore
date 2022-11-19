package dev.tilera.auracore.mixins;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;

import dev.tilera.auracore.aura.AuraManager;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.tiles.TileCrystal;

@Mixin(TileCrystal.class)
public abstract class MixinTileCrystal extends TileThaumcraft {
    
    private long count = System.currentTimeMillis() + (long)new Random().nextInt(300000);

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
        int md = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if (md == 7) {
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

}
