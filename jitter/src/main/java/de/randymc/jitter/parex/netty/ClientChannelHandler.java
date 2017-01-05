package de.randymc.jitter.parex.netty;

import de.randymc.jitter.parex.Node;
import de.randymc.jitter.parex.ParexClient;
import de.randymc.jitter.parex.network.packet.DeliveryPacket;
import de.randymc.jitter.parex.network.packet.HandshakePacket;
import de.randymc.jitter.util.HUID;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

/**
 * @author Moritz Beck (http://mbuniverse.de)
 * @version 1.0
 */
public class ClientChannelHandler extends ChannelHandler {
    private final ParexClient parexClient;
    @Getter private Node hydraNode;

    public ClientChannelHandler( ParexClient parexClient ) {
        super( parexClient );
        this.parexClient = parexClient;
    }

    @Override
    public void channelActive( ChannelHandlerContext ctx ) throws Exception {
        super.channelActive( ctx );
        //TODO sicherstellen dass es vorher noch kein Hydra node gab

        this.hydraNode = new Node( ctx.channel(), this );
        this.hydraNode.setId( HUID.forHydra() );

        this.hydraNode.send( new HandshakePacket( this.parexClient.getNodeType(), this.parexClient.getHash() ) );

    }

    @Override
    protected boolean handleDelivery( Node node, DeliveryPacket deliveryPacket ) {
        return super.getDeliveryResponseHandler().handle( node, deliveryPacket );
    }

    protected Node getNodeByChannel( Channel channel ) {
        return this.hydraNode;
    }
}
