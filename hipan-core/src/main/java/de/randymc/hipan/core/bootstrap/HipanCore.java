package de.randymc.hipan.core.bootstrap;

import de.randymc.hipan.core.plugin.HipanPluginManager;
import de.randymc.hipan.plugin.PluginManager;
import de.randymc.hipan.scheduling.Scheduler;
import de.randymc.jitter.util.HUID;
import de.randymc.jitter.parex.ParexClient;
import de.randymc.jitter.parex.network.NodeType;
import de.randymc.jitter.parex.network.packet.PingPacket;
import de.randymc.jitter.scheduling.CoreScheduler;
import jline.console.ConsoleReader;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The main class of the Hipan core which contains the plugin manager and more.
 *
 * @author Digot
 * @version 1.0
 */
public class HipanCore {

    @Getter private final Logger logger;
    @Getter private final PluginManager pluginManager;
    @Getter private final Scheduler scheduler;
    @Getter private ParexClient parexClient;

    private final long systemStart;
    private final AtomicBoolean running;

    /**
     * Constructor used to initialize internal members
     */
    public HipanCore() {
        this.systemStart = System.currentTimeMillis();
        this.logger = LoggerFactory.getLogger( HipanCore.class );
        this.scheduler = new CoreScheduler();
        this.pluginManager = new HipanPluginManager( this );
        this.parexClient = new ParexClient( this.scheduler );
        this.running = new AtomicBoolean( false );
    }

    /**
     * Starts all systems and makes the core instance ready
     */
    public void start() {
        this.logger.info( "Starting Hipan core..." );

        //Start the client
        this.parexClient.setNodeType( NodeType.CORE );
        this.parexClient.setHash( new byte[] { 0x23 } );

        //Start the network client
        this.logger.info( "Connecting to Hydra..." );
        this.parexClient.connect().then( arg -> {
            this.logger.info( "Successfully connected to Hydra" );
        }, error -> {
            this.logger.error( "Failed to connect to Hydra!", error );
            this.initShutdown();
        } );

        //Register handlers
        this.registerHandlers();

        //Load all plugins
        this.pluginManager.loadPlugins();

        //Done! Everything is ready!
        this.logger.info( "Done - Startup took " + (System.currentTimeMillis() - systemStart) + "ms");
        this.running.set( true );

        //Load Console
        try {
            ConsoleReader consoleReader = new ConsoleReader();
            consoleReader.setExpandEvents( false );

            String line;

            while ( this.running.get() && ( line = consoleReader.readLine( "HipanCore> " )) != null) {
                if( line.equals( "stop" ) ) {
                    this.running.set( false );
                }
                if ( line.equals( "ping" ) ) {
                    PingPacket pingPacket = new PingPacket();
                    pingPacket.setTimeSent( System.nanoTime() );
                    this.parexClient.request( pingPacket, HUID.forHydra() ).then( packet -> {
                        long time = System.nanoTime();
                        this.logger.info("Ping took: " + ( time - packet.getTimeSent() ) / 1000 + " µs" +
                                " ( " + ( packet.getTimeResponded() - packet.getTimeSent() ) / 1000 + " µs from Client to Hydra and "
                                + ( time - packet.getTimeResponded() ) / 1000 + " µs from Hydra to Client )" );
                    }, error -> {
                        this.logger.error( "Error making ping", error );
                    });
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        this.shutdown();
    }

    private void registerHandlers() {

    }

    public void initShutdown() {
        this.logger.info( "Init shutdown.." );
        this.running.set( false );
    }

    private void shutdown() {
        this.logger.info( "Shutting down.." );

        //send packet
        this.parexClient.close();

        //Shutdown everything
        this.scheduler.shutdown();
        //Stop accepting packets
        //Disable plugins
    }
}
