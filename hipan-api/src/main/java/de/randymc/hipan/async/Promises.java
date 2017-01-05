package de.randymc.hipan.async;

/**
 * @author Moritz Beck (http://mbuniverse.de)
 * @version 1.0
 */
public class Promises {

    public static <T> Promise<T> resolve( T arg ) {
        return new ResultPromise<T>() {
            public boolean isResolved() {
                return true;
            }

            public void setNextPromise( Promise nextPromise ) {
                nextPromise.resolve( arg );
            }
        };
    }

    public static <T> Promise<T> reject( Throwable arg ) {
        return new ResultPromise<T>() {
            public boolean isRejected() {
                return true;
            }

            public void setNextPromise( Promise nextPromise ) {
                nextPromise.reject( arg );
            }
        };
    }

    public static <T> Promise<T> pending() {
        return new ResultPromise<T>() {
            public void setNextPromise( Promise nextPromise ) {

            }

            public boolean isPending() {
                return true;
            }
        };
    }

}
