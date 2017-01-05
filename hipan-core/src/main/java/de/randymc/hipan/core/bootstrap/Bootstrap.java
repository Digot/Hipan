package de.randymc.hipan.core.bootstrap;

import de.randymc.hipan.core.util.ExceptionHandler;
import de.randymc.hipan.core.util.LoggingStreamRedirection;

/**
 * The main-entry point of the core application of Hipan.
 *
 * @author Digot
 * @version 1.0
 */
public class Bootstrap {

    /**
     * The main method of the application
     * @param args The command-line arguments
     */
    public static void main( String[] args ) {
        //Set the default exception handler
        Thread.setDefaultUncaughtExceptionHandler( new ExceptionHandler() );

        //Redirect output streams for logging
        LoggingStreamRedirection.bindSystemStreams();

        //To be able to use jline through IntelliJ for testing
        System.setProperty( "jline.WindowsTerminal.directConsole", "false" );

        //Start the core
        HipanCore core = new HipanCore();
        core.start();
    }

}
