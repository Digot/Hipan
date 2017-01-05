package de.randymc.jitter.parex.async;

import com.google.common.base.Preconditions;
import de.randymc.hipan.async.Delegate;
import de.randymc.hipan.async.Promise;
import de.randymc.hipan.async.PromiseCallback;
import io.netty.util.concurrent.Future;
import lombok.Synchronized;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Moritz Beck (http://mbuniverse.de)
 * @version 1.0
 */
public class HipanPromise<T> implements Promise<T> {
    private PromiseState state;
    private PromiseCallback<T, ?> doneCallback;
    private Delegate<T> doneDelegate;
    private PromiseCallback<Throwable, ?> failCallback;
    private Delegate<Throwable> failDelegate;
    private T result;
    private Throwable failResult;
    private Promise nextPromise;

    public HipanPromise() {
        this.state = PromiseState.PENDING;
    }

    public HipanPromise( Promise promise ) {
        this();
        promise.setNextPromise( this );
    }

    /**
     * Creates a Promise that gets fulfilled when the Netty-Future is completed
     * @param nettyFuture
     */
    public HipanPromise( Future<T> nettyFuture ) {
        this();
        nettyFuture.addListener( future  -> {
            if ( future.isSuccess() ) {
                this.resolve( ( T ) future.get() );
            } else {
                this.reject( future.cause() );
            }
        } );
    }

    @Synchronized
    public <Q> Promise<Q> done( PromiseCallback<T, Q> onDone ) {
        this.checkAddListener();
        this.doneCallback = onDone;
        this.handleListenerAdd();
        return ( Promise<Q> ) this.nextPromise;
    }

    @Synchronized
    public Promise<Void> done( Delegate<T> onDone ) {
        this.checkAddListener();
        this.doneDelegate = onDone;
        this.handleListenerAdd();
        return ( Promise<Void> ) this.nextPromise;
    }

    @Synchronized
    public <Q> Promise<Q> fail( PromiseCallback<Throwable,Q> onFail ) {
        this.checkAddListener();
        this.failCallback = onFail;
        this.handleListenerAdd();
        return ( Promise<Q> ) this.nextPromise;
    }

    @Synchronized
    public Promise<Void> fail( Delegate<Throwable> onFail ) {
        this.checkAddListener();
        this.failDelegate = onFail;
        this.handleListenerAdd();
        return ( Promise<Void> ) this.nextPromise;
    }

    @Synchronized
    public <Q> Promise<Q> then( PromiseCallback<T, Q> onDone, PromiseCallback<Throwable, Q> onFail ) {
        this.checkAddListener();
        this.doneCallback = onDone;
        this.failCallback = onFail;
        this.handleListenerAdd();
        return ( Promise<Q> ) this.nextPromise;
    }

    @Synchronized
    public <Q> Promise<Q> then( Delegate<T> onDone, PromiseCallback<Throwable, Q> onFail ) {
        this.checkAddListener();
        this.doneDelegate = onDone;
        this.failCallback = onFail;
        this.handleListenerAdd();
        return ( Promise<Q> ) this.nextPromise;
    }

    @Synchronized
    public <Q> Promise<Q> then( PromiseCallback<T, Q> onDone, Delegate<Throwable> onFail ) {
        this.checkAddListener();
        this.doneCallback = onDone;
        this.failDelegate = onFail;
        this.handleListenerAdd();
        return ( Promise<Q> ) this.nextPromise;
    }

    @Synchronized
    public Promise<Void> then( Delegate<T> onDone, Delegate<Throwable> onFail ) {
        this.checkAddListener();
        this.doneDelegate = onDone;
        this.failDelegate = onFail;
        this.handleListenerAdd();
        return ( Promise<Void> ) this.nextPromise;
    }

    @Synchronized
    public void resolve( T arg ) {
        this.checkResolveReject();
        this.state = PromiseState.RESOLVED;
        this.result = arg;
        this.invokeDone();
    }

    @Synchronized
    public void reject( Throwable arg ) {
        this.checkResolveReject();
        this.state = PromiseState.REJECTED;
        this.failResult = arg;
        this.invokeFail();
    }

    public boolean isPending() {
        return this.state.equals( PromiseState.PENDING );
    }

    public boolean isResolved() {
        return this.state.equals( PromiseState.RESOLVED );
    }

    public boolean isRejected() {
        return this.state.equals( PromiseState.REJECTED );
    }

    @Synchronized
    public void setNextPromise( Promise nextPromise ) {
        this.checkAddListener();
        this.nextPromise = nextPromise;
       //log( "setNextPromise" );

        if ( this.isResolved() )
            this.resolveNext( result );
        else if ( this.isRejected() )
            this.rejectNext( failResult );
    }

    private void checkResolveReject() {
        //log("checkResolveReject");
        Preconditions.checkState( this.isPending(), "Already resolved or rejected promise" );
    }

    private void checkAddListener() {
        Preconditions.checkArgument( canAddListener(), "Already registered a callback" );
    }

    private boolean canAddListener() {
        return this.doneCallback == null && this.doneDelegate == null && this.failCallback == null && this.failDelegate == null && this.nextPromise == null;
    }

    private void handleListenerAdd() {
        this.nextPromise = new HipanPromise();
        //log("handleListenerAdd");

        if ( this.isResolved() )
            this.invokeDone();
        else if ( this.isRejected() )
            this.invokeFail();
    }

    private void invokeDone() {
        //log("invokeDone");
        //TODO test schreiben
        if ( this.doneCallback != null ) {
            Promise<?> promise;
            try {
                promise = this.doneCallback.invoke( result );
            } catch ( Exception e ) {
                rejectNext( e );
                return;
            }
            if ( promise == null ) {
                this.resolveNext( null );
                return;
            }
            //log("invokedone2");
            promise.setNextPromise( this.nextPromise );

        } else if ( this.doneDelegate != null ) {
            try {
                this.doneDelegate.invoke( result );
            } catch ( Exception e ) {
                this.rejectNext( e );
                return;
            }
            this.resolveNext( null );

        } else if ( !canAddListener() ) {
            this.resolveNext( this.result );
        }
    }

    private void invokeFail() {
        //log("invokeFail");
        if ( this.failCallback != null ) {
            Promise<?> promise;
            try {
                promise = failCallback.invoke( failResult );
            } catch ( Exception e ) {
                this.rejectNext( e );
                return;
            }
            if ( promise == null ) {
                this.resolveNext( null );
                return;
            }
            //log("invokeFail2");
            promise.setNextPromise( this.nextPromise );
        } else if ( this.failDelegate != null ) {
            try {
                failDelegate.invoke( failResult );
            } catch ( Exception e ) {
                this.rejectNext( e );
            }
            this.resolveNext( null );
        } else if ( !canAddListener() ) {
            this.rejectNext( this.failResult );
        }
    }

    private void next( Object arg ) {
        if ( arg instanceof Throwable ) {
            rejectNext( ( ( Throwable ) arg ) );
        } else {
            resolveNext( arg );
        }
    }

    private void resolveNext( Object arg ) {
        if ( this.nextPromise == null) return;

        //log("resNext");
        this.nextPromise.resolve( arg );
    }

    private void log( String method ) {
        System.out.println(method + ": This: " + this + "; next: " + this.nextPromise + "; Thread: " + Thread.currentThread().getName());
    }

    private void rejectNext( Throwable arg ) {
        if ( this.nextPromise == null) {
            throw new RuntimeException( "Uncaught exception in Promise", arg );
        }
        this.nextPromise.reject( arg );
    }
}
