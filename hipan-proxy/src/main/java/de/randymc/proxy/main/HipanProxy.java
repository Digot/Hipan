package de.randymc.proxy.main;

import de.randymc.hipan.scheduling.Scheduler;
import de.randymc.jitter.parex.ParexClient;
import de.randymc.jitter.parex.network.NodeType;
import de.randymc.jitter.scheduling.CoreScheduler;
import de.randymc.proxy.DeliveryProcessor;
import de.randymc.proxy.listener.LoginForwarder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Digot
 * @version 1.0
 */
public class HipanProxy extends Plugin {

    @Getter private final long eventTimeout = 500;
    @Getter private TimeUnit eventTimeoutUnit = TimeUnit.MILLISECONDS;

    @Getter private ParexClient parexClient;
    @Getter private DeliveryProcessor deliveryProcessor;
    private Logger logger;
    private Scheduler scheduler;

    @Override
    public void onEnable() {
        try {
            //Logger
            this.logger = LoggerFactory.getLogger( HipanProxy.class );
            this.logger.info( "Initializing HipanProxy..." );

            //Scheduler
            this.logger.info( "Starting scheduler..." );
            this.scheduler = new CoreScheduler( super.getExecutorService() );

            //Start the parex client
            this.logger.info( "Connecting to Hydra..." );
            this.parexClient = new ParexClient( this.scheduler );
            this.parexClient.setNodeType( NodeType.PROXY );
            this.parexClient.setHash( new byte[] { 0x00 } ); //TODO Replace

            /*this.parexClient.connect( new GenericFutureListener() {
                @Override
                public void operationComplete( Future future ) throws Exception {
                    future.get();
                }
            } );*/

            //DeliveryProcessor
            this.logger.info( "Starting DeliveryProcessor..." );
            this.deliveryProcessor = new DeliveryProcessor( this );

            //Register listeners
            this.logger.info( "Registering listeners..." );
            this.registerListeners();

            //Request commands
            this.logger.info( "Sending command installation request" );
        }
        catch ( Exception e ) {
            this.logger.error( "Failed to start the proxy! Stopping in 5 seconds", e );

            try {
                Thread.sleep( 5000 );
            } catch ( InterruptedException e1 ) {
                e1.printStackTrace();
            }
            finally {
                this.getProxy().stop( "Failed to connect to Hydra" );
            }
        }
    }

    @Override
    public void onDisable() {
        try {
            this.parexClient.shutdown();
            this.scheduler.shutdown();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    private void registerListeners() {
        this.getProxy().getPluginManager().registerListener( this, new LoginForwarder( this ) );
    }
}
