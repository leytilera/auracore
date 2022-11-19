package dev.tilera.auracore.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import dev.tilera.auracore.api.Aspects;
import dev.tilera.auracore.aura.AuraManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.MathHelper;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileArcaneFurnace;

@Mixin(TileArcaneFurnace.class)
public abstract class MixinTileArcaneFurnace extends TileThaumcraft {

    @Shadow(remap = false)
    private ItemStack[] furnaceItemStacks;
    @Shadow(remap = false)
    public int furnaceCookTime;
    @Shadow(remap = false)
    public int furnaceMaxCookTime;
    @Shadow(remap = false)
    public int speedyTime;
    @Shadow(remap = false)
    public int facingX;
    @Shadow(remap = false)
    public int facingZ;

    @Shadow(remap = false)
    protected abstract int getBellows();
    @Shadow(remap = false)
    protected abstract void getFacing();
    @Shadow(remap = false)
    protected abstract int calcCookTime();
    @Shadow(remap = false)
    public abstract int getSizeInventory();
    @Shadow(remap = false)
    protected abstract boolean canSmelt(int slotIn);

    /**
     * @author tilera
     * @reason Use Vis from the aura
     */
    @Overwrite
    public void updateEntity() {
        super.updateEntity();
        if (this.facingX == -5) {
           this.getFacing();
        }
  
        if (!super.worldObj.isRemote) {
           boolean cookedflag = false;
           if (this.furnaceCookTime > 0) {
              --this.furnaceCookTime;
              cookedflag = true;
           }
  
           if (cookedflag && this.speedyTime > 0) {
              --this.speedyTime;
           }
  
           if (this.speedyTime <= 0) {
              this.speedyTime = VisNetHandler.drainVis(super.worldObj, super.xCoord, super.yCoord, super.zCoord, Aspect.FIRE, 5);
           }
  
           if (this.furnaceMaxCookTime == 0) {
              this.furnaceMaxCookTime = this.calcCookTime();
           }
  
           if (this.furnaceCookTime > this.furnaceMaxCookTime) {
              this.furnaceCookTime = this.furnaceMaxCookTime;
           }
  
           int a;
           if (this.furnaceCookTime == 0 && cookedflag) {
              for(a = 0; a < this.getSizeInventory(); ++a) {
                 if (this.furnaceItemStacks[a] != null) {
                    ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[a]);
                    if (itemstack != null && AuraManager.decreaseClosestAura(this.worldObj, this.xCoord, this.yCoord, this.zCoord, 1)) {
                       this.ejectItem(itemstack.copy(), this.furnaceItemStacks[a]);
                       super.worldObj.addBlockEvent(super.xCoord, super.yCoord, super.zCoord, ConfigBlocks.blockArcaneFurnace, 3, 0);
                       --this.furnaceItemStacks[a].stackSize;
                       if (this.furnaceItemStacks[a].stackSize <= 0) {
                          this.furnaceItemStacks[a] = null;
                       }
                       break;
                    }
                 }
              }
           }
  
           if (this.furnaceCookTime == 0 && !cookedflag) {
              for(a = 0; a < this.getSizeInventory(); ++a) {
                 if (this.furnaceItemStacks[a] != null && this.canSmelt(a)) {
                    this.furnaceMaxCookTime = this.calcCookTime();
                    this.furnaceCookTime = this.furnaceMaxCookTime;
                    break;
                 }
              }
           }
        }
  
     }
 
    /**
     * @author tilera
     * @reason Add flux to aura
     */
    @Overwrite(remap = false)
    public void ejectItem(ItemStack items, ItemStack furnaceItemStack) {
        if (items != null) {
           ItemStack bit = items.copy();
           int bellows = this.getBellows();
           float lx = 0.5F;
           lx += (float)this.facingX * 1.2F;
           float lz = 0.5F;
           lz += (float)this.facingZ * 1.2F;
           float mx = this.facingX == 0 ? (super.worldObj.rand.nextFloat() - super.worldObj.rand.nextFloat()) * 0.03F : (float)this.facingX * 0.13F;
           float mz = this.facingZ == 0 ? (super.worldObj.rand.nextFloat() - super.worldObj.rand.nextFloat()) * 0.03F : (float)this.facingZ * 0.13F;
           EntityItem entityitem = new EntityItem(super.worldObj, (double)((float)super.xCoord + lx), (double)((float)super.yCoord + 0.4F), (double)((float)super.zCoord + lz), items);
           entityitem.motionX = (double)mx;
           entityitem.motionZ = (double)mz;
           entityitem.motionY = 0.0D;
           super.worldObj.spawnEntityInWorld(entityitem);
           if (ThaumcraftApi.getSmeltingBonus(furnaceItemStack) != null) {
              ItemStack bonus = ThaumcraftApi.getSmeltingBonus(furnaceItemStack).copy();
              if (bonus != null) {
                 if (bellows == 0) {
                    if (super.worldObj.rand.nextInt(4) == 0) {
                       ++bonus.stackSize;
                    }
                 } else {
                    for(int a = 0; a < bellows; ++a) {
                       if (super.worldObj.rand.nextFloat() < 0.44F) {
                          ++bonus.stackSize;
                       }
                    }
                 }
              }
  
              if (bonus != null && bonus.stackSize > 0) {
                 mx = this.facingX == 0 ? (super.worldObj.rand.nextFloat() - super.worldObj.rand.nextFloat()) * 0.03F : (float)this.facingX * 0.13F;
                 mz = this.facingZ == 0 ? (super.worldObj.rand.nextFloat() - super.worldObj.rand.nextFloat()) * 0.03F : (float)this.facingZ * 0.13F;
                 EntityItem entityitem2 = new EntityItem(super.worldObj, (double)((float)super.xCoord + lx), (double)((float)super.yCoord + 0.4F), (double)((float)super.zCoord + lz), bonus);
                 entityitem2.motionX = (double)mx;
                 entityitem2.motionZ = (double)mz;
                 entityitem2.motionY = 0.0D;
                 super.worldObj.spawnEntityInWorld(entityitem2);
              }
           }
           if (this.worldObj.rand.nextInt(15 + bellows * 5) == 0) {
                AuraManager.addFluxToClosest(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.worldObj.rand.nextBoolean() ? new AspectList().add(Aspect.FIRE, 1) : new AspectList().add(Aspects.EVIL, 1));
           }
           int var2 = items.stackSize;
           float var3 = FurnaceRecipes.smelting().func_151398_b(bit);
           int var4;
           if (var3 == 0.0F) {
              var2 = 0;
           } else if (var3 < 1.0F) {
              var4 = MathHelper.floor_float((float)var2 * var3);
              if (var4 < MathHelper.ceiling_float_int((float)var2 * var3) && (float)Math.random() < (float)var2 * var3 - (float)var4) {
                 ++var4;
              }
  
              var2 = var4;
           }
  
           while(var2 > 0) {
              var4 = EntityXPOrb.getXPSplit(var2);
              var2 -= var4;
              EntityXPOrb xp = new EntityXPOrb(super.worldObj, (double)((float)super.xCoord + lx), (double)((float)super.yCoord + 0.4F), (double)((float)super.zCoord + lz), var4);
              mx = this.facingX == 0 ? (super.worldObj.rand.nextFloat() - super.worldObj.rand.nextFloat()) * 0.025F : (float)this.facingX * 0.13F;
              mz = this.facingZ == 0 ? (super.worldObj.rand.nextFloat() - super.worldObj.rand.nextFloat()) * 0.025F : (float)this.facingZ * 0.13F;
              xp.motionX = (double)mx;
              xp.motionZ = (double)mz;
              xp.motionY = 0.0D;
              super.worldObj.spawnEntityInWorld(xp);
           }
  
        }
     }

}
