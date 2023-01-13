package dev.tilera.auracore.api.research;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * DO NOT IMPLEMENT THIS!
 * Implemented by TileResearchTable. Can safely be casted to it.
 */
public interface IResearchTable {
    
    ResearchTableExtension getInternalExtension();

    World getWorld();

    int getXCoord();

    int getYCoord();

    int getZCoord();

    void openGUI(EntityPlayer player);

}
