package de.randymc.hipan.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Digot
 * @version 1.0
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Logger logger;
    private final StringWriter stringWriter;
    private final PrintWriter printWriter;

    public ExceptionHandler() {
        this.logger = LoggerFactory.getLogger( ExceptionHandler.class );
        this.stringWriter = new StringWriter();
        this.printWriter = new PrintWriter( this.stringWriter );
    }

    public void uncaughtException( Thread t, Throwable e ) {
        //Write the stacktrace into a string using writers
        e.printStackTrace( printWriter );
        String printedStackTrace = stringWriter.toString();

        //Clean up
        this.stringWriter.getBuffer().setLength( 0 );

        //Write to log
        this.logger.error( printedStackTrace );
    }

}
