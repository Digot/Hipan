package de.randymc.jitter.parex;

import de.randymc.hipan.async.Promise;
import de.randymc.hipan.async.Promises;
import de.randymc.hipan.scheduling.Scheduler;
import de.randymc.jitter.parex.handler.PingPacketHandler;
import de.randymc.jitter.parex.netty.ClientChannelHandler;
import de.randymc.jitter.util.HUID;
import de.randymc.jitter.parex.async.HipanPromise;
import de.randymc.jitter.parex.config.ParexClientConfig;
import de.randymc.jitter.parex.handler.AssignmentHandler;
import de.randymc.jitter.parex.network.NodeType;
import de.randymc.jitter.parex.network.Packet;
import de.randymc.jitter.parex.network.packet.DisconnectPacket;
import de.randymc.jitter.util.ConfigHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Digot
 * @version 1.0
 */
public class ParexClient extends ParexComponent {

    @Getter private final ClientChannelHandler channelHandler;
    @Getter @Setter private NodeType nodeType;
    @Getter @Setter private byte[] hash;
    @Getter @Setter private HUID nodeId;
    @Getter private final Logger logger;
    private Scheduler scheduler;
    private ParexClientConfig config;

    /**
     * Initializes a Parex Client but doesn't start it. Call {@link #connect()} to connect
     * @param scheduler used to run the networking threads
     */
    public ParexClient ( Scheduler scheduler ) {
        this.channelHandler = new ClientChannelHandler( this );
        this.scheduler = scheduler;
        this.logger = LoggerFactory.getLogger( ParexClient.class );
        this.loadConfig();

        this.addHandler( new PingPacketHandler( this ) );
    }


    /**
     * Starts the Parex Client and connects to Hydra
     * @return Promise that gets fulfilled when the start was successful
     */
    public Promise<Void> connect() {
        if ( this.nodeType == null )
            throw new NullPointerException( "NodeType is not set" );
        if ( this.hash == null )
            throw new NullPointerException( "Hash is not set" );

        // Configure the bootstrap.
        //TODO Find out value
        EventLoopGroup workerGroup = new NioEventLoopGroup( 8, this.scheduler.getThreadFactory() );

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group( workerGroup )
                .channel( NioSocketChannel.class )
                .option( ChannelOption.TCP_NODELAY, true )
                .handler( new ChannelInitializer<SocketChannel>() {
                    protected void initChannel( SocketChannel ch ) throws Exception {
                        //Define the pipeline for the packets
                        ParexClient.super.preparePipeline( ch );
                    }
                } );

        ChannelFuture channelFuture = bootstrap.connect( this.config.getHost(), this.config.getPort() );

        Promise<Void> promise = new HipanPromise<>( channelFuture ).done( arg -> {
            return Promises.pending();
        });

        super.addHandler( new AssignmentHandler( this, promise ) );

        return promise;
    }

    public void close() {
        this.send( new DisconnectPacket() );
    }

    public <T extends Packet> void send( T packet ) {
        this.channelHandler.getHydraNode().send( packet );
    }

    public <T extends Packet> void send( T packet, HUID destination ) {
        this.channelHandler.getHydraNode().send( packet, destination );
    }

    public <T extends Packet> Promise<T> request( T packet, HUID destination ) {
        return this.channelHandler.getHydraNode().request( packet, destination );
    }

    private void loadConfig() {
        try {
            this.config = ConfigHelper.loadConfig( ParexClientConfig.class, "client.cfg" );
        } catch ( Exception e ) {
            this.logger.error( "Error while loading config! " , e );
        }
    }

    @Override
    public void onClientConnected ( Channel channel ) {

    }

}
