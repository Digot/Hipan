package de.randymc.jitter.parex;

import de.randymc.hipan.async.Promise;
import de.randymc.hipan.scheduling.Scheduler;
import de.randymc.jitter.parex.async.HipanPromise;
import de.randymc.jitter.parex.config.ParexServerConfig;
import de.randymc.jitter.parex.handler.PingPacketHandler;
import de.randymc.jitter.parex.netty.ServerChannelHandler;
import de.randymc.jitter.util.ConfigHelper;
import de.randymc.jitter.util.HUID;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides the central server which all clients connect to.
 *
 * @author Digot
 * @version 1.0
 */
public class ParexServer extends ParexComponent {

    @Getter private final ServerChannelHandler channelHandler;
    @Getter @Setter private DeliveryPacketHandler deliveryHandler;
    @Getter private Logger logger;
    private Scheduler scheduler;
    private ParexServerConfig config;
    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    /**
     * Initializes a Parex Server but doesn't start it. Call {@link #bootstrap()} to start the server
     * @param scheduler used to run the networking threads
     */
    public ParexServer ( Scheduler scheduler ) {
        this.channelHandler = new ServerChannelHandler( this );
        this.scheduler = scheduler;
        this.logger = LoggerFactory.getLogger( ParexServer.class );
        this.loadConfig();

        this.addHandler( new PingPacketHandler( this ) );
    }

    /**
     * Initializes the server
     * @return Promise that gets fulfilled when the start was successful
     */
    public Promise<Void> bootstrap() {
        //TODO Find out value
        bossGroup = new NioEventLoopGroup( 8 ,  this.scheduler.getThreadFactory() ); // (1)
        workerGroup = new NioEventLoopGroup( 8, this.scheduler.getThreadFactory() );

        ServerBootstrap b = new ServerBootstrap(); //(2)
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class) // (3)
                .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        //Define the pipeline for the packets
                        ParexServer.super.preparePipeline( ch );
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)// (5)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
                //.option( ChannelOption.TCP_NODELAY, true );

        this.logger.info( "Binding to port " + this.config.getPort() );

        ChannelFuture channelFuture = b.bind( this.config.getPort() ); // (7)
        channel = channelFuture.channel();

        return new HipanPromise<>( channelFuture );
    }

    public void close() {
        this.logger.info( "Shutting down ParexServer" );
        bossGroup.shutdownGracefully().awaitUninterruptibly();
        workerGroup.shutdownGracefully().awaitUninterruptibly();
    }

    private void loadConfig() {
        try {
            this.config = ConfigHelper.loadConfig( ParexServerConfig.class, "server.cfg" );
        } catch ( Exception e ) {
            this.logger.error( "Error while loading config! " , e );
        }
    }

    public void onClientConnected( Channel channel ) {
        this.logger.info( "New incoming connection " + channel.remoteAddress().toString() );
    }

    @Override
    public HUID getNodeId() {
        return HUID.forHydra();
    }

}
