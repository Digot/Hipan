package de.randymc.jitter.test;

import de.randymc.hipan.async.Delegate;
import de.randymc.hipan.async.Future;
import de.randymc.hipan.async.FutureListener;
import de.randymc.hipan.async.Promise;
import de.randymc.hipan.async.Promises;
import de.randymc.jitter.parex.async.HipanPromise;

import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Moritz Beck (http://mbuniverse.de)
 * @version 1.0
 */
public class TestBootstrap {
    private static ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor( 100000 );

    public static void main( String[] args ) throws InterruptedException {
        /*Set<LongWrapper> proLongs = new HashSet<>();
        Set<LongWrapper> fuLongs = new HashSet<>();

        Thread.sleep( 100 );

        for ( int i = 0; i < 1000; i++) {

            LongWrapper atomicLong = new LongWrapper();
            fuLongs.add( atomicLong );

            dbLoadUuidFuture( atomicLong ).addListener( new FutureListener<UUID>() {
                @Override
                public void onResolved( UUID arg ) {
                    long time = System.nanoTime();
                    atomicLong.setValue( System.nanoTime() - atomicLong.getValue() );
                }

                @Override
                public void onFailed( Throwable cause ) {

                }
            } );

            LongWrapper atomicLong2 = new LongWrapper();
            proLongs.add( atomicLong2 );
            dbLoadUuid( atomicLong2 ).done( new PromiseCallback<UUID, Object>() {
                @Override
                public Promise<Object> invoke( UUID arg ) {
                    long time = System.nanoTime();
                    atomicLong2.setValue( System.nanoTime() - atomicLong2.getValue() );
                    return null;
                }
            });
        }

        Thread.sleep( 2000 );

        long gesFu = 0;
        for ( LongWrapper l : fuLongs ) {
            gesFu += l.getValue();
        }
        gesFu = gesFu / 1000;

        long gesPro = 0;
        for ( LongWrapper l : proLongs ) {
            gesPro += l.getValue();
        }
        gesPro = gesPro / 1000;

        System.out.println("Promise dauert: " + ( gesPro/1000 ) + " µs");
        System.out.println("Future dauert: " + ( gesFu/1000 ) + " µs");*/


        //startTest();
        test2();
    }

    private static void test2() {
        Promise<String> dings = new HipanPromise<>();

        dings.done( ( Delegate<String> ) arg -> {
            //throw new IllegalAccessError( "test" );
            System.out.println( arg.toString() );
        } );

        dings.resolve( null );
    }

    private static void startTest() {
        new Thread( () -> {
            long time = System.nanoTime();
            for ( int i = 0; i < 1000000; i++ ) {
                Promise<String> dings = new HipanPromise<>();
                dings.done( arg -> {
                    String lol = arg;
                    return Promises.resolve( "hallo" );
                } ).done( arg -> {
                    return null;
                });
                dings.resolve( "test" );
            }

            System.out.println("Promise took: " + ( System.nanoTime() - time )/1000000 + " ns");
        }).start();

        new Thread( () -> {
            long time = System.nanoTime();
            for ( int i = 0; i < 1000000; i++ ) {
                Future<String> dings = new Future<>();
                dings.addListener( new FutureListener<String>() {
                    @Override
                    public void onResolved( String arg ) {
                        String lol = arg;
                    }

                    @Override
                    public void onFailed( Throwable cause ) {

                    }
                } );
                dings.resolve( "test" );
            }

            System.out.println("Future took: " + ( System.nanoTime() - time )/1000000 + " ns");
        }).start();
    }

    private static Promise<UUID> dbLoadUuid( String test ) {
        Promise<UUID> promise = new HipanPromise<>();

        pool.schedule( () -> {
            promise.resolve( UUID.randomUUID() );
        }, 1, TimeUnit.SECONDS );

        return promise;
    }

    private static Future<UUID> dbLoadUuidFuture( String test ) {
        Future<UUID> future = new Future<>();

        pool.schedule( () -> {
            future.resolve( UUID.randomUUID() );
        }, 1, TimeUnit.SECONDS );

        return future;
    }
}
