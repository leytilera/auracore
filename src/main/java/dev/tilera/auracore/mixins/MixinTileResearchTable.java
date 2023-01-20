package dev.tilera.auracore.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.tilera.auracore.api.research.IResearchTable;
import dev.tilera.auracore.api.research.ResearchTableExtension;
import dev.tilera.auracore.api.research.ResearchTableExtensionRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.TileResearchTable;

@Mixin(TileResearchTable.class)
public abstract class MixinTileResearchTable extends TileThaumcraft implements IResearchTable {

    private ResearchTableExtension extension = null;

    @Inject(method = "<init>()V", at = @At("TAIL"), remap = false)
    public void constructorHead(CallbackInfo ci) {
        if (ResearchTableExtensionRegistry.hasActiveExtension()) {
            extension = ResearchTableExtensionRegistry.createInstance(this);
        }
    }

    @Override
    public ResearchTableExtension getInternalExtension() {
        return extension;
    }

    @Override
    public World getWorld() {
        return this.worldObj;
    }

    @Override
    public int getXCoord() {
        return this.xCoord;
    }

    @Override
    public int getYCoord() {
        return this.yCoord;
    }

    @Override
    public int getZCoord() {
        return this.zCoord;
    }

    @Override
    public void openGUI(EntityPlayer player) {
        if (extension != null) {
            if (extension.openGUI(player)) return;
        }
        player.openGui(Thaumcraft.instance, 10, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
    }

    @Inject(method = "markDirty", at = @At("TAIL"))
    public void onMarkDirty(CallbackInfo ci) {
        if (extension != null) {
            extension.markDirty();
        }
    }

    @Inject(method = "updateEntity", at = @At("TAIL"))
    public void onUpdateEntity(CallbackInfo ci) {
        if (extension != null) {
            extension.onTick();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        if (extension != null) {
            String key = extension.getNBTKey();
            if (!nbttagcompound.hasKey(key)) return;
            NBTTagCompound nbt = nbttagcompound.getCompoundTag(key);
            extension.readFromNBT(nbt);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        if (extension != null) {
            String key = extension.getNBTKey();
            NBTTagCompound nbt = new NBTTagCompound();
            extension.writeToNBT(nbt);
            nbttagcompound.setTag(key, nbt);
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeCustomNBT(nbttagcompound);
        if (extension != null) {
            String key = extension.getNBTKey();
            NBTTagCompound nbt = new NBTTagCompound();
            extension.writeToPacket(nbt);
            nbttagcompound.setTag(key, nbt);
        }
        return new S35PacketUpdateTileEntity(super.xCoord, super.yCoord, super.zCoord, -999, nbttagcompound);
     }

    /**
     * @author tilera
     * @reason ResearchTable extensions
     */
    @Overwrite(remap = false)
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        if (super.worldObj != null && super.worldObj.isRemote) {
            super.worldObj.markBlockForUpdate(super.xCoord, super.yCoord, super.zCoord);
         }
        if (extension != null) {
            NBTTagCompound nbttagcompound = pkt.func_148857_g();
            String key = extension.getNBTKey();
            if (!nbttagcompound.hasKey(key)) return;
            NBTTagCompound nbt = nbttagcompound.getCompoundTag(key);
            extension.readFromNBT(nbt);
        }
    }
    
    @Inject(method = "getSizeInventory", at = @At("HEAD"), remap = false, cancellable = true)
    public void onGetInventorySize(CallbackInfoReturnable<Integer> ci) {
        if (extension instanceof IInventory) {
            IInventory ext = (IInventory) extension;
            ci.setReturnValue(2 + ext.getSizeInventory());
        }
    }

    @Inject(method = "getStackInSlot", at = @At("HEAD"), remap = false, cancellable = true)
    public void onGetStackInSlot(int slot, CallbackInfoReturnable<ItemStack> ci) {
        if (extension instanceof IInventory && slot >= 2) {
            IInventory ext = (IInventory) extension;
            ci.setReturnValue(ext.getStackInSlot(slot - 2));
        }
    }

    @Inject(method = "getStackInSlotOnClosing", at = @At("HEAD"), remap = false, cancellable = true)
    public void onGetStackInSlotOnClosing(int slot, CallbackInfoReturnable<ItemStack> ci) {
        if (extension instanceof IInventory && slot >= 2) {
            IInventory ext = (IInventory) extension;
            ci.setReturnValue(ext.getStackInSlotOnClosing(slot - 2));
        }
    }

    @Inject(method = "decrStackSize", at = @At("HEAD"), remap = false, cancellable = true)
    public void onDecrStackSize(int slot, int amt, CallbackInfoReturnable<ItemStack> ci) {
        if (extension instanceof IInventory && slot >= 2) {
            IInventory ext = (IInventory) extension;
            ci.setReturnValue(ext.decrStackSize(slot - 2, amt));
        }
    }

    @Inject(method = "setInventorySlotContents", at = @At("HEAD"), remap = false, cancellable = true)
    public void onSetSlotContents(int slot, ItemStack stack, CallbackInfo ci) {
        if (extension instanceof IInventory && slot >= 2) {
            IInventory ext = (IInventory) extension;
            ext.setInventorySlotContents(slot - 2, stack);
            ci.cancel();
        }
    }

    @Inject(method = "isItemValidForSlot", at = @At("HEAD"), remap = false, cancellable = true)
    public void onIsItemValid(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
        if (extension instanceof IInventory) {
            IInventory ext = (IInventory) extension;
            ci.setReturnValue(ext.isItemValidForSlot(slot - 2, stack));
        }
    }

}
