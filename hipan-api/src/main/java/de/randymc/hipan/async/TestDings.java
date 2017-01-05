package de.randymc.hipan.async;

/**
 * @author Moritz Beck (http://mbuniverse.de)
 * @version 1.0
 */
public class TestDings {
    private static int zahl = 0;

    public static void main( String[] args ) {
        int count = 1000000;
        Thread increment = new Thread( new Runnable() {
            @Override
            public void run() {
                for ( int i = 0; i < count; i++ ) {
                    zahl++;
                }
            }
        } );

        Thread decrement = new Thread( new Runnable() {
            @Override
            public void run() {
                for ( int i = 0; i < count; i++ ) {
                    zahl--;
                }
            }
        } );

        increment.start();
        decrement.start();

        try {
            increment.join();
            decrement.join();
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }

        System.out.println(zahl);
    }

}
