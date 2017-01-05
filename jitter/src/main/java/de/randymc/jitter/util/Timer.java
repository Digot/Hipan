package de.randymc.jitter.util;

import org.slf4j.Logger;

/**
 * Used to measure how long the execution of a code block takes
 *
 * Example use:
 * <code>
 *     try( Timer timer = new Timer( "NetworkTest" ) ) {
 *         this.executeSomething();
 *     }
 * </code>
 *
 * @author Digot
 * @version 1.0
 */
public class Timer implements AutoCloseable {

    private final long startMs;
    private final Logger logger;
    private final String topic;

    public Timer ( Logger logger, String topic ) {
        this.logger = logger;
        this.topic = topic;
        this.startMs = System.currentTimeMillis();
    }

    @Override
    public void close ( ) {
        this.logger.info( this.topic + " took " + ( System.currentTimeMillis() - this.startMs ) + "ms" );
    }
}
