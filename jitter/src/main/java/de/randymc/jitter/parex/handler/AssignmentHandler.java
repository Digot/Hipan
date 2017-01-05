package de.randymc.jitter.parex.handler;

import de.randymc.hipan.async.Promise;
import de.randymc.jitter.parex.Node;
import de.randymc.jitter.parex.PacketHandler;
import de.randymc.jitter.parex.ParexClient;
import de.randymc.jitter.parex.network.packet.AssignmentPacket;
import de.randymc.jitter.parex.network.packet.ReadyPacket;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.SucceededFuture;

/**
 * @author Digot
 * @version 1.0
 */
public class AssignmentHandler extends PacketHandler<AssignmentPacket> {

    private final ParexClient parexClient;
    private final Promise<Void> promise;

    public AssignmentHandler ( ParexClient parexClient, Promise<Void> promise ) {
        super( AssignmentPacket.class );
        this.parexClient = parexClient;
        this.promise = promise;
    }

    @Override
    public void handle ( Node node, AssignmentPacket packet ) {
        parexClient.removeHandler( this );

        this.parexClient.setNodeId( packet.getNodeId() );
        this.parexClient.getLogger().info( "Received node id " + packet.getNodeId() );

        node.send( new ReadyPacket() );

        promise.resolve( null );
    }
}
