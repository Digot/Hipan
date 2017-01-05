package de.randymc.hipan.core.bootstrap;

import de.randymc.hipan.scheduling.Future;
import de.randymc.hipan.scheduling.Scheduler;
import de.randymc.hipan.scheduling.Task;
import de.randymc.jitter.scheduling.CoreScheduler;
import de.randymc.jitter.util.Timer;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author Digot
 * @version 1.0
 */
public class Test {

    public static void main ( String[] args ) {
        Scheduler scheduler = new CoreScheduler();
        Task task = scheduler.callTaskAsync( new Callable< String >() {
            @Override
            public String call ( ) throws Exception {
                Thread.sleep( 5000 );
                return "hi";
            }
        } );
        try( Timer timer = new Timer( LoggerFactory.getLogger( Test.class ), "FutureTest1" ) ) {
            task.cancel();
            Future<String> stringFuture = task.getFuture();
            try {
                System.out.println( stringFuture.get() );
            } catch ( InterruptedException | ExecutionException | TimeoutException e ) {
                e.printStackTrace();
            }
        }


    }

}
