package de.randymc.hipan.scheduling;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;

/**
 * Provides synchronous and asynchronous task scheduling
 *
 * @author Digot
 * @version 1.0
 */
public interface Scheduler {

    /**
     * Starts a new asynchronous task and executes a sequence of code.
     *
     * @param runnable the runnable to execute
     * @return the created Task
     */
    Task runTaskAsync( Runnable runnable );

    /**
     * Starts a new asynchronous task with the given name and executes a sequence of code.
     *
     * @param runnable to execute
     * @param name to use
     * @return the created Task
     */
    Task runTaskAsync( Runnable runnable, String name );

    /**
     * Starts a new asynchronous task that is used to retrieve a value
     *
     * @param callable to execute
     * @param <T> to use
     * @return the created Task
     */
    <T> Task callTaskAsync( Callable<T> callable );

    /**
     * Shuts down all tasks and closes the executors
     */
    void shutdown();

    /**
     * Used to retrieve the ThreadFactory of the scheduler
     * @return the thread factory
     */
    ThreadFactory getThreadFactory();

}
