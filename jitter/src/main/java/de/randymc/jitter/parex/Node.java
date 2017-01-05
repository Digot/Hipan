package de.randymc.jitter.parex;

import com.google.common.base.Preconditions;
import de.randymc.hipan.async.Promise;
import de.randymc.jitter.parex.netty.ChannelHandler;
import de.randymc.jitter.parex.network.packet.DeliveryPacket;
import de.randymc.jitter.util.HUID;
import de.randymc.jitter.parex.network.NodeType;
import de.randymc.jitter.parex.network.Packet;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Digot
 * @version 1.0
 */
public class Node {

    private final ChannelHandler channelHandler;
    private final Channel connection;
    @Getter @Setter private HUID id;
    @Getter @Setter private NodeType type;
    @Getter @Setter private long lastkeepalive;

    public Node( Channel connection, ChannelHandler channelHandler ) {
        this.connection = connection;
        this.channelHandler = channelHandler;
    }

    private void sendPacket( Packet packet ) {
        this.connection.writeAndFlush( packet );
    }

    public String getHost() {
        return this.connection.remoteAddress().toString();
    }

    /**
     * Send a packet without any checks and deliveries
     * @param packet to send
     * @param <T> type of the packet
     */
    public <T extends Packet> void sendRaw( T packet ) {
        this.sendPacket( packet );
    }

    /**
     * Send a packet without destination
     * @param packet to send
     * @param <T> type of the packet
     */
    public <T extends Packet> void send( T packet ) {
        this.send( packet, null );
    }

    /**
     * Send a packet to the specified destination
     * @param packet to send
     * @param destination for the packet
     * @param <T> type of the packet
     */
    public <T extends Packet> void send( T packet, HUID destination ) {
        Preconditions.checkArgument( !( packet instanceof DeliveryPacket ) , "A DeliveryPacket is not allowed" );
        if ( destination == null ) {
            this.sendPacket( packet );
            return;
        }

        DeliveryPacket<T> deliveryPacket = new DeliveryPacket<>( this.channelHandler.getParexComponent().getNodeId(), destination, packet);
        this.sendPacket( deliveryPacket );
    }

    /**
     * Request a response from a Node
     * Sends a DeliveryPacket with required response
     * @param packet to send
     * @param destination for the packet
     * @param <T> type of the packet
     * @return Promise that gets fulfilled when a response is received
     */
    public <T extends Packet> Promise<T> request( T packet, HUID destination ) {
        System.out.println("Requeste…");
        Preconditions.checkArgument( !( packet instanceof DeliveryPacket ) , "Cannot create DeliveryPacket that contains a DeliveryPacket" );
        Preconditions.checkArgument( destination != null, "destination must not be null" );

        DeliveryPacket<T> deliveryPacket = new DeliveryPacket<>( this.channelHandler.getParexComponent().getNodeId(), destination, packet);
        deliveryPacket.setRequiresResponse( true );

        this.sendPacket( deliveryPacket );

        return this.channelHandler.getDeliveryResponseHandler().addRequest( deliveryPacket );
    }

    @Override
    public String toString ( ) {
        return this.type.name() + "#" + id.toString();
    }
}
