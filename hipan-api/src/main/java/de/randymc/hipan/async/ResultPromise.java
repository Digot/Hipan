package de.randymc.hipan.async;

/**
 * @author Moritz Beck (http://mbuniverse.de)
 * @version 1.0
 */
public abstract class ResultPromise<T> implements Promise<T> {
    public <Q> Promise<Q> done( PromiseCallback<T, Q> onDone ) {
        throw new IllegalAccessError( "Not supported in ResultPromise" );
    }

    public Promise<Void> done( Delegate<T> onDone ) {
        throw new IllegalAccessError( "Not supported in ResultPromise" );
    }

    public <Q> Promise<Q> fail( PromiseCallback<Throwable, Q> onFail ) {
        throw new IllegalAccessError( "Not supported in ResultPromise" );
    }

    public Promise<Void> fail( Delegate<Throwable> onFail ) {
        throw new IllegalAccessError( "Not supported in ResultPromise" );
    }

    public <Q> Promise<Q> then( PromiseCallback<T, Q> onDone, PromiseCallback<Throwable, Q> onFail ) {
        throw new IllegalAccessError( "Not supported in ResultPromise" );
    }

    public <Q> Promise<Q> then( Delegate<T> onDone, PromiseCallback<Throwable, Q> onFail ) {
        throw new IllegalAccessError( "Not supported in ResultPromise" );
    }

    public <Q> Promise<Q> then( PromiseCallback<T, Q> onDone, Delegate<Throwable> onFail ) {
        throw new IllegalAccessError( "Not supported in ResultPromise" );
    }

    public Promise<Void> then( Delegate<T> onDone, Delegate<Throwable> onFail ) {
        throw new IllegalAccessError( "Not supported in ResultPromise" );
    }

    public void resolve( T arg ) {
        throw new IllegalAccessError( "Not supported in ResultPromise" );
    }

    public void reject( Throwable arg ) {
        throw new IllegalAccessError( "Not supported in ResultPromise" );
    }

    public boolean isPending() {
        return false;
    }

    public boolean isResolved() {
        return false;
    }

    public boolean isRejected() {
        return false;
    }
}
