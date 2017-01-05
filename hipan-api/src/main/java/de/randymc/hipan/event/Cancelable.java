package de.randymc.hipan.event;

/**
 * @author Digot
 * @version 1.0
 */
public interface Cancelable {

    boolean isCancelled();
    void setCancelled( boolean value );

}
