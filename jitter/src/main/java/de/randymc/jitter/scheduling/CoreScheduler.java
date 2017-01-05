package de.randymc.jitter.scheduling;

import de.randymc.hipan.scheduling.Callback;
import de.randymc.hipan.scheduling.Future;
import de.randymc.hipan.scheduling.Scheduler;
import de.randymc.hipan.scheduling.Task;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Digot
 * @version 1.0
 */
public class CoreScheduler implements Scheduler {

    private Set<Task> runningTasks;
    private ExecutorService executorService;
    private AtomicBoolean wasShutdown;

    /**
     * Initializes a core scheduler which uses Java Executors for task scheduling
     */
    public CoreScheduler() {
        //TODO Use a FixedThreadPool and set a nThreads value
        this.executorService = Executors.newCachedThreadPool( getThreadFactory() );
        this.runningTasks = new HashSet<>();
        this.wasShutdown = new AtomicBoolean( false );
    }

    /**
     * Initializes a core scheduler which uses a custom executors for task scheduling
     *
     * @param executorService that should be used for scheduling
     */
    public CoreScheduler( ExecutorService executorService ) {
        this.executorService = executorService;
        this.runningTasks = new HashSet<>();
    }

    /**
     * Starts a new asynchronous task and executes a sequence of code.
     *
     * @param runnable the runnable to execute
     * @return the created Task
     */
    public Task runTaskAsync( Runnable runnable ) {
        this.checkAlreadyShutdown();

        Task task = new Task( this,
                this.executorService.submit( runnable ), null, null );
        this.runningTasks.add( task );
        return task;
    }

    @Override
    public Task runTaskAsync ( Runnable runnable, String name ) {
        this.checkAlreadyShutdown();

        Thread thread = new Thread( runnable, name );
        thread.setDaemon( true );
        thread.setName( name );
        thread.start();

        Task task = new Task( this, null, null, thread );
        this.runningTasks.add( task );
        return task;
    }

    @Override
    public <T> Task callTaskAsync ( Callable<T> callback ) {
        this.checkAlreadyShutdown();

        Future<T> future = new Future<>();

        return new Task( this,
                this.executorService.submit( new Runnable() {
                    @Override
                    public void run ( ) {
                        try{
                            T result = callback.call();
                            future.resolve( result );
                        }
                        catch ( Exception e ) {
                            future.fail( e );
                        }
                    }
                } ), future, null );
    }

    /**
     * Shuts down all tasks and closes the executors
     */
    public void shutdown() {
        this.wasShutdown.set( true );

        for ( Task runningTask : this.runningTasks ) {
            runningTask.cancel();
        }

        this.executorService.shutdown();
    }

    @Override
    public ThreadFactory getThreadFactory() {
        return new ThreadFactory() {
            public Thread newThread( Runnable r ) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName( "Thread-" + CoreScheduler.this.runningTasks.size() );
                return thread;
            }
        };
    }

    private void checkAlreadyShutdown() {
        if( this.wasShutdown.get() ) throw new IllegalStateException( "CoreScheduler has already been shutdown" );
    }
}
