package dev.tilera.auracore.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tilera.auracore.api.EnumNodeType;
import dev.tilera.auracore.aura.AuraManager;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.tiles.TileNode;

@Mixin(TileNode.class)
public abstract class MixinTileNode extends TileThaumcraft {
 
    @Shadow(remap = false)
    private NodeType nodeType;
    @Shadow(remap = false)
    AspectList aspectsBase;
    int virtualNodeID = -1;

    @Inject(method = "writeCustomNBT", at = @At("HEAD"), remap = false)
    public void onNBTWrite(NBTTagCompound nbt, CallbackInfo ci) {
        nbt.setInteger("virtualNodeID", this.virtualNodeID);
    }

    @Inject(method = "readCustomNBT", at = @At("HEAD"), remap = false)
    public void onNBTRead(NBTTagCompound nbt, CallbackInfo ci) {
        if (nbt.hasKey("virtualNodeID")) {
            this.virtualNodeID = nbt.getInteger("virtualNodeID");
        }
    }

    @Inject(method = "updateEntity", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        if (virtualNodeID == -1) {
            this.createVirtualNode();
        }
    }

    private void createVirtualNode() {
        EnumNodeType type = convertNodeType(nodeType);
        virtualNodeID = AuraManager.registerAuraNode(this.worldObj, (short)aspectsBase.visSize(), type, this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, true);
    }

    private static EnumNodeType convertNodeType(NodeType type) {
        switch (type) {
            case TAINTED:
            case DARK:
                return EnumNodeType.DARK;
            case PURE:
                return EnumNodeType.PURE;
            case HUNGRY:
            case UNSTABLE:
                return EnumNodeType.UNSTABLE;
            default:
                return EnumNodeType.NORMAL;       
        }
    }

}
