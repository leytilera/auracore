package dev.tilera.auracore.api.research;

import java.lang.reflect.Constructor;

public class ResearchTableExtensionRegistry {
    
    private static Class<? extends ResearchTableExtension> extension = null;

    public static boolean registerResearchTableExtension(Class<? extends ResearchTableExtension> ext, boolean force) {
        if (extension == null || force) {
            extension = ext;
            return true;
        } 
        return false;
    }

    public static boolean hasActiveExtension() {
        return extension != null;
    }

    public static ResearchTableExtension createInstance(IResearchTable table) {
        try {
            Constructor<?> constr = extension.getConstructor(IResearchTable.class);
            return (ResearchTableExtension) constr.newInstance(table);
        } catch (NullPointerException | ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }   
    }

}
