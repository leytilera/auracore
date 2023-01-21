package dev.tilera.auracore.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import dev.tilera.auracore.Config;
import dev.tilera.auracore.api.IAlembic;
import dev.tilera.auracore.aura.AuraManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTank;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileCrucible;

@Mixin(TileCrucible.class)
public abstract class MixinTileCrucible extends TileThaumcraft implements IAspectSource {

    @Shadow(remap = false)
    public short heat;
    @Shadow(remap = false)
    public AspectList aspects;
    @Shadow(remap = false)
    public FluidTank tank;
    @Shadow(remap = false)
    int bellows;
    @Shadow(remap = false)
    private long counter;

    @Shadow(remap = false)
    public abstract AspectList takeRandomFromSource();
    @Shadow(remap = false)
    public abstract void getBellows();
    @Shadow(remap = false)
    public abstract int tagAmount();
    @Shadow(remap = false)
    public abstract void spill();
    @Shadow(remap = false)
    abstract void drawEffects();

    boolean spillNextTick = false;

    /**
     * @author tilera
     * @reason Spill remnants to alembics and aura
     */
    @Overwrite(remap = false)
    public void spillRemnants() {
        if (this.tank.getFluidAmount() > 0 || this.aspects.visSize() > 0) {
            this.tank.setFluid(null);
            this.heat = 0;

            for (int a = 2; a < 6; ++a) {
                TileEntity tile = this.worldObj.getTileEntity(
                        this.xCoord + ForgeDirection.getOrientation((int) a).offsetX,
                        this.yCoord + ForgeDirection.getOrientation((int) a).offsetY,
                        this.zCoord + ForgeDirection.getOrientation((int) a).offsetZ);
                if (tile == null || !(tile instanceof IAlembic))
                    continue;
                for (Aspect tag : this.aspects.getAspectsSortedAmount()) {
                    try {
                        int result;
                        if (tag == null || ((IAlembic) tile).containerContains(tag) <= 0 || (result = ((IAlembic) tile)
                                .addToContainer(tag, this.aspects.getAmount(tag))) == this.aspects.getAmount(tag))
                            continue;
                        this.aspects.reduce(tag, this.aspects.getAmount(tag) - result);
                    } catch (Exception e) {

                    }
                }
            }
            for (int a = 2; a < 6; ++a) {
                TileEntity tile = this.worldObj.getTileEntity(
                        this.xCoord + ForgeDirection.getOrientation((int) a).offsetX,
                        this.yCoord + ForgeDirection.getOrientation((int) a).offsetY,
                        this.zCoord + ForgeDirection.getOrientation((int) a).offsetZ);
                if (tile == null || !(tile instanceof IAlembic))
                    continue;
                for (Aspect tag : this.aspects.getAspectsSortedAmount()) {
                    try {
                        int result;
                        if (tag == null || (result = ((IAlembic) tile).addToContainer(tag,
                                this.aspects.getAmount(tag))) == this.aspects.getAmount(tag))
                            continue;
                        this.aspects.reduce(tag, this.aspects.getAmount(tag) - result);
                    } catch (Exception e) {

                    }
                }
            }
            if (Config.legacyCrucibleMechanics) {
                AuraManager.addFluxToClosest(this.worldObj, (float) this.xCoord + 0.5f, (float) this.yCoord + 0.5f, (float) this.zCoord + 0.5f, this.aspects);
            } else {
                for(int a = 0; a < this.aspects.visSize() / 2; ++a) {
                    this.spill();
                }
            }

            this.aspects = new AspectList();
            this.markDirty();
            super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
            super.worldObj.addBlockEvent(super.xCoord, super.yCoord, super.zCoord, ConfigBlocks.blockMetalDevice, 2, 5);
        }

    }

    /**
     * @author tilera
     * @reason Flux to aura & no aspect boiling down
     */
    @Overwrite
    public void updateEntity() {
        ++this.counter;
        int prevheat = this.heat;
        if (!super.worldObj.isRemote) {
           if (this.bellows < 0) {
              this.getBellows();
           }
           if (this.spillNextTick) {
              this.spillRemnants();
              this.spillNextTick = false;
              this.getBellows();
           }
           if (this.tank.getFluidAmount() <= 0) {
              if (this.heat > 0) {
                 --this.heat;
              }
           } else {
              Material mat = super.worldObj.getBlock(super.xCoord, super.yCoord - 1, super.zCoord).getMaterial();
              Block bi = super.worldObj.getBlock(super.xCoord, super.yCoord - 1, super.zCoord);
              int md = super.worldObj.getBlockMetadata(super.xCoord, super.yCoord - 1, super.zCoord);
              if (mat != Material.lava && mat != Material.fire && (bi != ConfigBlocks.blockAiry || md != 1)) {
                 if (this.heat > 0) {
                    --this.heat;
                    if (this.heat == 149) {
                       this.markDirty();
                       super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
                    }
                 }
              } else if (this.heat < 200) {
                 this.heat = (short)(this.heat + 1 + this.bellows * 2);
                 if (prevheat < 151 && this.heat >= 151) {
                    this.markDirty();
                    super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
                 }
              }
           }
           if (Config.legacyCrucibleMechanics) {
                if (this.tagAmount() > 500 && this.counter % 5L == 0L) {
                    AspectList tt = this.takeRandomFromSource();
                    AuraManager.addFluxToClosest(this.worldObj, this.xCoord, this.yCoord, this.zCoord, tt);
                    if (this.tagAmount() <= 500) {
                        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                    }
                }
           } else {
                if (this.tagAmount() > 100 && this.counter % 5L == 0L) {
                    this.takeRandomFromSource();
                    this.spill();
                }
    
                if (this.counter > 100L && this.heat > 150) {
                    this.counter = 0L;
                    if (this.tagAmount() > 0) {
                        int s = this.aspects.getAspects().length;
                        Aspect a = this.aspects.getAspects()[super.worldObj.rand.nextInt(s)];
                        if (a.isPrimal()) {
                            a = this.aspects.getAspects()[super.worldObj.rand.nextInt(s)];
                        }
    
                    this.tank.drain(2, true);
                    this.aspects.remove(a, 1);
                    if (!a.isPrimal()) {
                        if (super.worldObj.rand.nextBoolean()) {
                            this.aspects.add(a.getComponents()[0], 1);
                        } else {
                            this.aspects.add(a.getComponents()[1], 1);
                        }
                    } else {
                        this.spill();
                    }
                }
    
                this.markDirty();
                super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
             }
           }
        } else if (this.tank.getFluidAmount() > 0) {
           this.drawEffects();
        }
  
        if (super.worldObj.isRemote && prevheat < 151 && this.heat >= 151) {
           ++this.heat;
        }
  
     }

    /**
     * @author tilera
     * @reason Implement aspect container properly 
     */
    @Overwrite(remap = false)
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (this.aspects.getAmount(tag) >= amount) {
            this.aspects.reduce(tag, amount);
            this.spillNextTick = true;
            this.markDirty();
            super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
            return true;
        }
        return false;
    }

    /**
     * @author tilera
     * @reason Implement aspect container properly 
     */
    @Overwrite(remap = false)
    public boolean takeFromContainer(AspectList ot) {
        if (this.doesContainerContain(ot)) {
            for (Aspect tag : ot.getAspects()) {
                this.aspects.reduce(tag, ot.getAmount(tag));
                this.spillNextTick = true;
                this.markDirty();
                super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
            }
            return true;
        }
        return false;
    }

    /**
     * @author tilera
     * @reason Implement aspect container properly 
     */
    @Overwrite(remap = false)
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return this.aspects.getAmount(tag) >= amount;
    }

    /**
     * @author tilera
     * @reason Implement aspect container properly 
     */
    @Overwrite(remap = false)
    public boolean doesContainerContain(AspectList ot) {
        for (Aspect tag : this.aspects.getAspects()) {
            if (this.aspects.getAmount(tag) > ot.getAmount(tag)) continue;
            return false;
        }
        return true;
    }

    /**
     * @author tilera
     * @reason Implement aspect container properly 
     */
    @Overwrite(remap = false)
    public int containerContains(Aspect tag) {
        return this.aspects.getAmount(tag);
    }

}
