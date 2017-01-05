package de.randymc.hydra.handler;

import de.randymc.jitter.parex.DeliveryPacketHandler;
import de.randymc.jitter.parex.network.NodeType;
import de.randymc.jitter.util.HUID;
import de.randymc.hydra.bootstrap.Hydra;
import de.randymc.jitter.parex.Node;
import de.randymc.jitter.parex.network.packet.DeliveryPacket;

/**
 * @author Digot
 * @version 1.0
 */
public class DeliveryForwardHandler implements DeliveryPacketHandler {

    private final Hydra hydra;

    public DeliveryForwardHandler( Hydra hydra ) {
        this.hydra = hydra;
    }

    /**
     * Gets called from {@link de.randymc.jitter.parex.netty.ServerChannelHandler#handleDelivery(Node, DeliveryPacket)}
     * Decides whether the Delivery should be passed on to the {@link de.randymc.jitter.parex.handler.DeliveryResponseHandler} and the PacketHandlers or forwarded to a Node
     * @param node the Delivery came from
     * @param packet that needs to get handled
     * @return true if already handled, false if it should be passed to {@link de.randymc.jitter.parex.handler.DeliveryResponseHandler}
     */
    public boolean handle ( Node node, DeliveryPacket packet ) {
        HUID destination = packet.getTo();

        NodeType nodeType = destination.getBroadcastType();

        // Check if its directly for Hydra
        if ( nodeType == NodeType.HYDRA ) {
            return false; // Return false to pass it on to Hydra ( see ServerChannelHandler#handleDelivery() )

        } else if ( nodeType == null ) { // if its adressed directly to a Node
            //Send to the target
            Node toNode = this.hydra.getNodeManager().getNodeById( packet.getTo() );
            if ( toNode == null ) {
                //TODO packet zurückschicken?
                return true;
            }
            System.out.println("was senden");
            toNode.sendRaw( packet );

        } else { // If its a Broadcast Delivery
            this.hydra.getNodeManager().broadcastByType( nodeType, packet );
            //TODO zurückschicken wenn an nieman gebroadcastet?
        }

        return true;
    }
}
