package de.randymc.hipan.async;

/**
 * @author Moritz Beck (http://mbuniverse.de)
 * @version 1.0
 */
public interface PromiseCallback<A, R> {

    Promise<R> invoke( A arg );
}
