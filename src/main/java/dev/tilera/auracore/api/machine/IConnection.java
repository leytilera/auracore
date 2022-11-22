package dev.tilera.auracore.api.machine;

import dev.tilera.auracore.api.HelperLocation;
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
                                                                                                                                                                                              
    int getVisSuction(HelperLocation loc);                                                                                                                                               
                                                                                                                                                                                              
    void setVisSuction(int suction);                                                                                                                                                         
                                                                                                                                                                                              
    int getTaintSuction(HelperLocation loc);                                                                                                                                             
                                                                                                                                                                                              
    void setTaintSuction(int suction);                                                                                                                                                       
                                                                                                                                                                                              
    void setSuction(int suction);                                                                                                                                                            
                                                                                                                                                                                              
    int getSuction(HelperLocation loc);                                                                                                                                                  
}