package dev.tilera.auracore.api.machine;

import net.minecraftforge.common.util.ForgeDirection;

public interface IConnection
{
    boolean getConnectable(ForgeDirection side);

    boolean isVisSource();

    boolean isVisConduit();

    float[] subtractVis(float amount);

    float getPureVis();

    void setPureVis(float vis);

    float getTaintedVis();

    void setTaintedVis(float taint);                                                                                                                                                       
                                                                                                                                                                                              
    float getMaxVis();                                                                                                                                                                        
                                                                                                                                                                                              
    int getVisSuction(int x, int y, int z);                                                                                                                                               
                                                                                                                                                                                              
    void setVisSuction(int suction);                                                                                                                                                         
                                                                                                                                                                                              
    int getTaintSuction(int x, int y, int z);                                                                                                                                             
                                                                                                                                                                                              
    void setTaintSuction(int suction);                                                                                                                                                       
                                                                                                                                                                                              
    void setSuction(int suction);                                                                                                                                                            
                                                                                                                                                                                              
    int getSuction(int x, int y, int z);                                                                                                                                                  
}