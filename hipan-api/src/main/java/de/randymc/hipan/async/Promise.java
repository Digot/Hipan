package de.randymc.hipan.async;

/**
 *
 * The standard HIPAN way to handle async operations
 *
 * @author Moritz Beck (http://mbuniverse.de)
 * @version 1.0
 * @param <T> Type of the result
 */
public interface Promise<T> {

    /**
     * Registers a listener, will be invoked when the promises is resolved
     * @param onDone Callback to register
     * @param <Q> Type of the result the Callback returns
     * @return A new future for the further operations
     */
    <Q> Promise<Q> done( PromiseCallback<T, Q> onDone );

    /**
     * Registers a listener, will be invoked when the promises is resolved
     * @param onDone Delegate to register
     * @return A new future for the further operations
     */
    Promise<Void> done( Delegate<T> onDone );

    /**
     * Registers a listener, will be invoked when the promises is rejected
     * @param onFail Callback to register
     * @param <Q> Type of the result the Callback returns
     * @return A new future for the further operations
     */
    <Q> Promise<Q> fail( PromiseCallback<Throwable,Q> onFail );

    /**
     * Registers a listener, will be invoked when the promises is rejected
     * @param onFail Delegate to register
     * @return A new future for the further operations
     */
    Promise<Void> fail( Delegate<Throwable> onFail );

    /**
     * Registers a listener, will be invoked when the promises is resolved or rejected
     * @param onDone Callback to register, called when resolved
     * @param onFail Callback to register, called when rejected
     * @param <Q> Type of the result the Callback returns
     * @return A new future for the further operations
     */
    <Q> Promise<Q> then( PromiseCallback<T, Q> onDone, PromiseCallback<Throwable, Q> onFail );

    /**
     * Registers a listener, will be invoked when the promises is resolved or rejected
     * @param onDone Delegate to register, called when resolved
     * @param onFail Callback to register, called when rejected
     * @param <Q> Type of the result the Callback returns
     * @return A new future for the further operations
     */
    <Q> Promise<Q> then( Delegate<T> onDone, PromiseCallback<Throwable, Q> onFail );

    /**
     * Registers a listener, will be invoked when the promises is resolved or rejected
     * @param onDone Callback to register, called when resolved
     * @param onFail Delegate to register, called when rejected
     * @param <Q> Type of the result the Callback returns
     * @return A new future for the further operations
     */
    <Q> Promise<Q> then( PromiseCallback<T, Q> onDone, Delegate<Throwable> onFail );

    /**
     * Registers a listener, will be invoked when the promises is resolved or rejected
     * @param onDone Delegate to register, called when resolved
     * @param onFail Delegate to register, called when rejected
     * @return A new future for the further operations
     */
    Promise<Void> then( Delegate<T> onDone, Delegate<Throwable> onFail );

    /**
     * Resolves the promise
     * @param arg Result of the promise
     */
    void resolve( T arg );

    /**
     * Rejects the promise
     * @param arg Reject reason
     */
    void reject( Throwable arg );

    /**
     * @return if the promise is still pending
     */
    boolean isPending();

    /**
     * @return if the promise is resolved
     */
    boolean isResolved();

    /**
     * @return if the promise is rejected
     */
    boolean isRejected();

    /**
     * Only for internal promise usage
     * Do not use
     * Sets the promise that is invoked after the current promise
     * @param nextPromise Next Promise
     */
    void setNextPromise( Promise nextPromise );

    /**
     * Defines the different states that a promise can have
     */
    enum PromiseState {
        /**
         * When the promise has not yet resolved or rejected
         */
        PENDING,

        /**
         * When the promise was successful
         */
        RESOLVED,

        /**
         * When the promise was not successful or an exception was caught
         */
        REJECTED
    }
}
