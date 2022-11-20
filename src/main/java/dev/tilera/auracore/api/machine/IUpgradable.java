package dev.tilera.auracore.api.machine;

public interface IUpgradable
{
    boolean canAcceptUpgrade(byte p0);
    
    boolean hasUpgrade(byte p0);
    
    int getUpgradeLimit();
    
    byte[] getUpgrades();
    
    boolean setUpgrade(byte p0);
    
    boolean clearUpgrade(int p0);
}
