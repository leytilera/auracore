package dev.tilera.auracore.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import dev.tilera.auracore.api.IAlembic;
import dev.tilera.auracore.aura.AuraManager;
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
            AuraManager.addFluxToClosest(this.worldObj, (float) this.xCoord + 0.5f, (float) this.yCoord + 0.5f,
                    (float) this.zCoord + 0.5f, this.aspects);

            this.aspects = new AspectList();
            this.markDirty();
            super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
            super.worldObj.addBlockEvent(super.xCoord, super.yCoord, super.zCoord, ConfigBlocks.blockMetalDevice, 2, 5);
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
            //this.spillNextTick = true;
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
                //this.spillNextTick = true;
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
