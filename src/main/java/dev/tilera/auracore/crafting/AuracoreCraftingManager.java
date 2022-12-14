package dev.tilera.auracore.crafting;

import dev.tilera.auracore.api.AuracoreRecipes;
import dev.tilera.auracore.api.crafting.CrucibleRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.TileCrucible;
import thaumcraft.common.tiles.TileMagicWorkbench;

public class AuracoreCraftingManager {
    
    public static IArcaneRecipe findMatchingArcaneRecipe(TileMagicWorkbench awb, EntityPlayer player) {
        for (Object recipe : ThaumcraftApi.getCraftingRecipes()) {
            if (recipe instanceof IArcaneRecipe && ((IArcaneRecipe)recipe).matches(awb, player.worldObj, player)) {
                return (IArcaneRecipe) recipe;
            }
        }

        return null;
    }

    public static int getArcaneRecipeVisCost(IArcaneRecipe recipe, TileMagicWorkbench awb) {
        if (recipe == null) return 0;
        int sum = 0;
        AspectList aspects = recipe.getAspects(awb);
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect != null) sum += aspects.getAmount(aspect);
        }
        return sum;
    }

    public static TileMagicWorkbench createBridgeInventory(IInventory sourceInventory, int firstSlotIndex, int gridSize )
	{
		TileMagicWorkbench workbenchTile = new TileMagicWorkbench();
		for( int i = 0; i < gridSize; i++ )
		{
			workbenchTile.setInventorySlotContentsSoftly( i, sourceInventory.getStackInSlot( i + firstSlotIndex ) );
		}
		return workbenchTile;
	}

    public static boolean performCrucibleCrafting(EntityPlayer player, TileCrucible tile) {
        AspectList tags = new AspectList();
        for (Aspect tag : tile.aspects.getAspects()) {
            tags.add(tag, tile.aspects.getAmount(tag));
        }
        World world = tile.getWorldObj();
        CrucibleRecipe recipe = AuracoreRecipes.getCrucibleRecipe(tags, tile);
        ItemStack output = AuracoreRecipes.getCrucibleOutput(tags, tile, recipe);
        if (output != null && isCrucibleCreationSuccessful(world, output, player) && !world.isRemote) {
            tile.ejectItem(output);
            world.addBlockEvent(tile.xCoord, tile.yCoord, tile.zCoord, ConfigBlocks.blockMetalDevice, 1, -1);
        }
        if (!world.isRemote) {
            tile.spillRemnants();
        }
        return true;
    }

    public static boolean isCrucibleCreationSuccessful(World world, ItemStack item, EntityPlayer player) {
        String key = AuracoreRecipes.getCrucibleRecipe((ItemStack)item).key;
        boolean completed = ResearchManager.isResearchComplete(player.getDisplayName(), key);
        float chance = 0.0f;
        if (completed) {
            return true;
        }
        return false;
        //TODO: research
        /*if (!ResearchManager.doesPlayerHaveRequisites(player.getDisplayName(), key)) {
            return false;
        }
        ItemStack note = ResearchManager.createResearchNoteForPlayer(world, player, key);
        if (note == null) {
            if (!world.isRemote) {
                player.func_70006_a("Your discover something, but you can't record your findings!");
            }
        } else if (ResearchManager.progressExperimentalResearch(world, key, note, Config.resExpChance + (int)Math.sqrt(item.stackSize * 8))) {
            chance = ResearchManager.getData(note).getTotalProgress();
            if (chance == 1.0f) {
                note.setItemDamage(note.getItemDamage() + 64);
            }
            player.inventoryContainer.detectAndSendChanges();
            if (!world.isRemote) {
                player.func_70006_a("You've learned something new!");
            }
        }
        return world.rand.nextFloat() < chance;*/
    }

}
