package dev.tilera.auracore.api;

import dev.tilera.auracore.api.machine.IConnection;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class HelperLocation {
   public double x;
   public double y;
   public double z;
   public ForgeDirection facing;

   public HelperLocation(TileEntity tile) {
      this.x = (double)tile.xCoord;
      this.y = (double)tile.yCoord;
      this.z = (double)tile.zCoord;
      this.facing = ForgeDirection.UNKNOWN;
   }

   public HelperLocation(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.facing = ForgeDirection.UNKNOWN;
   }

   public HelperLocation(double x, double y, double z, ForgeDirection facing) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.facing = facing;
   }

   public HelperLocation(TileEntity te, ForgeDirection facing) {
      this.x = (double)te.xCoord;
      this.y = (double)te.yCoord;
      this.z = (double)te.zCoord;
      this.facing = facing;
   }

   public HelperLocation(TileEntity te, int facing) {
      this.x = (double)te.xCoord;
      this.y = (double)te.yCoord;
      this.z = (double)te.zCoord;
      switch (facing) {
         case 0:
            this.facing = ForgeDirection.DOWN;
            break;
         case 1:
            this.facing = ForgeDirection.UP;
            break;
         case 2:
            this.facing = ForgeDirection.NORTH;
            break;
         case 3:
            this.facing = ForgeDirection.SOUTH;
            break;
         case 4:
            this.facing = ForgeDirection.WEST;
            break;
         case 5:
            this.facing = ForgeDirection.EAST;
      }

   }

   public HelperLocation(HelperLocation l) {
      this.x = l.x;
      this.y = l.y;
      this.z = l.z;
      this.facing = l.facing;
   }

   public void moveUp(double amount) {
      switch (this.facing) {
         case SOUTH:
         case NORTH:
         case EAST:
         case WEST:
            this.y += amount;
         default:
      }
   }

   public void moveDown(double amount) {
      switch (this.facing) {
         case SOUTH:
         case NORTH:
         case EAST:
         case WEST:
            this.y -= amount;
         default:
      }
   }

   public void moveRight(double amount) {
      switch (this.facing) {
         case SOUTH:
            this.x -= amount;
            break;
         case NORTH:
            this.x += amount;
            break;
         case EAST:
            this.z += amount;
            break;
         case WEST:
            this.z -= amount;
      }

   }

   public void moveLeft(double amount) {
      switch (this.facing) {
         case SOUTH:
            this.x += amount;
            break;
         case NORTH:
            this.x -= amount;
            break;
         case EAST:
            this.z -= amount;
            break;
         case WEST:
            this.z += amount;
      }

   }

   public void moveForwards(double amount) {
      switch (this.facing) {
         case SOUTH:
            this.z += amount;
            break;
         case NORTH:
            this.z -= amount;
            break;
         case EAST:
            this.x += amount;
            break;
         case WEST:
            this.x -= amount;
            break;
         case UP:
            this.y += amount;
            break;
         case DOWN:
            this.y -= amount;
      }

   }

   public void moveBackwards(double amount) {
      switch (this.facing) {
         case SOUTH:
            this.z -= amount;
            break;
         case NORTH:
            this.z += amount;
            break;
         case EAST:
            this.x -= amount;
            break;
         case WEST:
            this.x += amount;
            break;
         case UP:
            this.y -= amount;
            break;
         case DOWN:
            this.y += amount;
      }

   }

   public TileEntity getConnectableTile(World w) {
      this.moveForwards(1.0);
      TileEntity te = w.getTileEntity((int)this.x, (int)this.y, (int)this.z);
      return te instanceof IConnection && ((IConnection)te).getConnectable(this.facing.getOpposite()) ? te : null;
   }

   public TileEntity getFacingTile(World w) {
      this.moveForwards(1.0);
      TileEntity te = w.getTileEntity((int)this.x, (int)this.y, (int)this.z);
      return te != null ? te : null;
   }

   public TileEntity getConnectableTile(IBlockAccess ibc) {
      this.moveForwards(1.0);
      TileEntity te = ibc.getTileEntity((int)this.x, (int)this.y, (int)this.z);
      return te instanceof IConnection && ((IConnection)te).getConnectable(this.facing.getOpposite()) ? te : null;
   }

   public boolean equals(HelperLocation loc) {
      return this.x == loc.x && this.y == loc.y && this.z == loc.z;
   }
}
