package de.randymc.jitter.parex.netty;

import de.randymc.jitter.parex.Node;
import de.randymc.jitter.parex.ParexServer;
import de.randymc.jitter.parex.network.packet.DeliveryPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Moritz Beck (http://mbuniverse.de)
 * @version 1.0
 */
public class ServerChannelHandler extends ChannelHandler {
    private ParexServer parexServer;
    private final Map<Channel, Node> connectedClientsByChannel;

    public ServerChannelHandler( ParexServer parexServer ) {
        super( parexServer );
        this.parexServer = parexServer;
        this.connectedClientsByChannel = new HashMap<>();
            //HashObjObjMaps.newMutableMap();
    }

    @Override
    public void channelActive( ChannelHandlerContext ctx ) throws Exception {
        super.channelActive( ctx );
        this.connectedClientsByChannel.put( ctx.channel(), new Node( ctx.channel(), this ) );
    }

    @Override
    protected boolean handleDelivery( Node node, DeliveryPacket deliveryPacket ) {
        if ( this.parexServer.getDeliveryHandler().handle( node, deliveryPacket ) )
            return true;

        if ( super.getDeliveryResponseHandler().handle( node, deliveryPacket ) )
            return true;

        return false;
    }

    protected Node getNodeByChannel( Channel channel ) {
        synchronized ( this.connectedClientsByChannel ) {
            return connectedClientsByChannel.get( channel );
        }
    }
}
