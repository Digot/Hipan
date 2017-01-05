package de.randymc.hydra.bootstrap;

import de.randymc.hipan.scheduling.Scheduler;
import de.randymc.jitter.util.HUID;
import de.randymc.hydra.handler.*;
import de.randymc.hydra.manager.NodeManager;
import de.randymc.jitter.parex.Node;
import de.randymc.jitter.parex.ParexServer;
import de.randymc.jitter.scheduling.CoreScheduler;
import jline.console.ConsoleReader;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Digot
 * @version 1.0
 */
public class Hydra {

    private final Logger logger;
    private final AtomicBoolean running;
    private final Scheduler scheduler;
    private final Lock startUpLock;

    private final ParexServer parexServer;
    @Getter private final NodeManager nodeManager;

    public Hydra() {
        this.running = new AtomicBoolean( false );
        this.logger = LoggerFactory.getLogger( Hydra.class );
        this.startUpLock = new ReentrantLock();
        this.scheduler = new CoreScheduler();
        this.nodeManager = new NodeManager( this );
        this.parexServer = new ParexServer( this.scheduler );
        this.parexServer.setDeliveryHandler( new DeliveryForwardHandler( this ) );
    }

    public void start() {
        this.logger.info( "Starting Hydra Central Server" );
        long start = System.currentTimeMillis();
        this.running.set( true );

        this.startUpLock.lock();

        this.parexServer.bootstrap().then( arg -> {
            this.logger.info( "Successfully started ParexServer" );
        }, arg -> {
            this.logger.error( "Failed to start ParexServer! Port already in use?", arg );
            this.initShutdown();
        } );

        //Packet handlers
        this.registerHandlers();

        this.logger.info( "Hydra is ready! Start up took " + ( System.currentTimeMillis() - start ) + "ms" );
        this.startUpLock.unlock();

        //Load Console
        try {
            ConsoleReader consoleReader = new ConsoleReader();
            consoleReader.setExpandEvents( false );

            String line;

            while ( this.running.get() && ( line = consoleReader.readLine( "Hydra> " ) ) != null ) {
                if ( line.equalsIgnoreCase( "stop" ) ) {
                    this.initShutdown();
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        this.shutdown();
    }

    private void registerHandlers ( ) {
        this.parexServer.addHandler( new HandshakeHandler( this ) );
        this.parexServer.addHandler( new ReadyHandler( this ) );
        this.parexServer.addHandler( new DisconnectHandler( this ) );
        this.parexServer.addHandler( new KeepAliveHandler( this, logger ) );
    }

    public void initShutdown() {
        this.logger.info( "Init shutdown.." );
        this.running.set( false );
    }

    private void shutdown() {
        this.logger.info( "Shutting down Hydra..." );

        //TODO Disconnect all clients

        //Send disconnect packet


        //Shutdown everything
        this.parexServer.close();

        this.logger.info( "Shutting down Scheduler" );
        this.scheduler.shutdown();
        //Stop accepting packets
        //Disable plugins
        this.logger.info( "Shutdown finished" );
    }

}
