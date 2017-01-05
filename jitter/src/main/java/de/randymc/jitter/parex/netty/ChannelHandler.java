package de.randymc.jitter.parex.netty;

import de.randymc.hipan.async.Promise;
import de.randymc.jitter.parex.Node;
import de.randymc.jitter.parex.PacketHandler;
import de.randymc.jitter.parex.ParexComponent;
import de.randymc.jitter.parex.async.HipanPromise;
import de.randymc.jitter.parex.handler.DeliveryResponseHandler;
import de.randymc.jitter.parex.network.Packet;
import de.randymc.jitter.parex.network.packet.DeliveryPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Digot
 * @version 1.0
 */
@io.netty.channel.ChannelHandler.Sharable
public abstract class ChannelHandler extends SimpleChannelInboundHandler<Packet> implements Closeable {

    private final Logger logger;
    @Getter private final ParexComponent parexComponent;
    @Getter private final DeliveryResponseHandler deliveryResponseHandler;
    private final Map<Class<? extends Packet>, PacketHandler> handlers;
    private final AtomicBoolean closed;

    public ChannelHandler ( ParexComponent parexComponent ) {
        this.parexComponent = parexComponent;
        this.handlers = new HashMap<>();
                //HashObjObjMaps.newMutableMap();
        this.logger = LoggerFactory.getLogger( ChannelHandler.class );
        this.closed = new AtomicBoolean( false );
        this.deliveryResponseHandler = new DeliveryResponseHandler();
    }

    @SuppressWarnings( "unchecked" )
    public void registerHandler( PacketHandler packetHandler ) {
        this.checkClosed();

        this.handlers.put( packetHandler.getClazz(), packetHandler );
    }

    public void unregisterHandler( PacketHandler packetHandler ) {
        this.checkClosed();

        this.handlers.remove( packetHandler.getClazz() );
    }

    @Override
    public void channelActive( ChannelHandlerContext ctx ) throws Exception {
        this.checkClosed();

        this.parexComponent.onClientConnected( ctx.channel() );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected void channelRead0( ChannelHandlerContext channelHandlerContext, Packet packet ) throws Exception {
        this.checkClosed();

        // Get Node the message is from
        Node node = getNodeByChannel( channelHandlerContext.channel() );

        DeliveryPacket deliveryPacket = null;
        if ( packet instanceof DeliveryPacket ) {
            deliveryPacket = ( ( DeliveryPacket ) packet );

            if ( this.handleDelivery( node, deliveryPacket ) ) // Handle Delivery ( see ServerChannelHandler and ClientChannelHandler )
                return; // Already handled so return

            packet = deliveryPacket.getDelivery(); // Handle the Packet contained in the DeliveryPacket
        }

        synchronized ( this.handlers ) {
            if ( this.handlers.containsKey( packet.getClass() ) ) {
                if ( deliveryPacket != null && deliveryPacket.isRequiresResponse() ) {

                    //TODO try catch
                    DeliveryPacket finalDeliveryPacket = deliveryPacket;
                    Promise<? extends Packet> promise = new HipanPromise<>( this.handlers.get( packet.getClass() ).handleDelivery( node, packet ) ); // Create new HipanPromise from the received promise
                            promise.then( arg -> {
                                DeliveryPacket delivery = new DeliveryPacket( this.getParexComponent().getNodeId(), finalDeliveryPacket.getFrom(), arg ); // Create DeliveryPacket for the response
                                delivery.setDeliveryId( finalDeliveryPacket.getDeliveryId() );
                                node.sendRaw( delivery );
                            }, arg -> {
                                //TODO: Error-Packet zurückschicken
                                this.logger.error( "Fehler beim Handlen" );
                            } );

                } else {
                    //TODO try catch
                    this.handlers.get( packet.getClass() ).handle( node, packet );
                }
            }
            else {
                this.parexComponent.getUnhandledPacketCallback().invoke( packet );
                this.logger.warn( "No packet handler for " + packet.getClass().getSimpleName() );
            }
        }
    }

    /**
     * Should return if the Delivery has already been handled, otherwise it will be passed on to the PacketHandlers
     * @param node that the packet came from
     * @param deliveryPacket that was received
     * @return true if the Delivery has already been handled, false if it should be passed on to the PacketHandlers
     */
    protected abstract boolean handleDelivery( Node node, DeliveryPacket deliveryPacket );

    protected abstract Node getNodeByChannel( Channel channel );

    @Override
    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
        this.logger.error( ctx.channel().remoteAddress().toString() + " caused exception! Disconnecting!" );
        cause.printStackTrace();

        //Disconnect the client
        ctx.close();
    }

    @Override
    public void close ( ) throws IOException {
        this.closed.set( true );
        //TODO
        //Disconnect all connections
        //Clear all collections
        this.handlers.clear();
    }

    private void checkClosed () {
        if( this.closed.get() ) throw new IllegalStateException( "ChannelHandler has already been closed!" );
    }
}