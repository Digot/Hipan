package de.randymc.hipan.scheduling;

import lombok.AllArgsConstructor;

import java.util.concurrent.Future;

/**
 * Represents a running task started from a {@link Scheduler}
 *
 * @author Digot
 * @version 1.0
 */
@AllArgsConstructor
public class Task {

    private Scheduler scheduler;

    private Future<?> attachedFuture;
    private de.randymc.hipan.scheduling.Future<?> attachedHipanFuture;
    private Thread attachedThread;

    /**
     * Stops the execution of the task immediately
     */
    public void cancel() {
        if( this.attachedThread != null ) {
            this.attachedThread.interrupt();
        }
        else {
            this.attachedFuture.cancel( true );

            if( this.attachedHipanFuture != null ) {
                this.attachedHipanFuture.fail( new InterruptedException( "Task was canceled" ) );
            }
        }
    }

    public <T> de.randymc.hipan.scheduling.Future<T> getFuture() {
        return ( de.randymc.hipan.scheduling.Future< T > ) this.attachedHipanFuture;
    }

}
