package de.randymc.hydra.manager;

import de.randymc.jitter.parex.network.packet.DeliveryPacket;
import de.randymc.jitter.util.HUID;
import de.randymc.hydra.bootstrap.Hydra;
import de.randymc.jitter.parex.Node;
import de.randymc.jitter.parex.network.NodeType;
import lombok.Getter;
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Digot
 * @version 1.0
 */
public class NodeManager {

    private final Hydra hydra;
    private final Logger logger;
    @Getter private final Map<HUID, Node> connectedNodes;


    public NodeManager ( Hydra hydra ) {
        this.hydra = hydra;
        this.connectedNodes = HashObjObjMaps.newMutableMap();

        this.logger = LoggerFactory.getLogger( NodeManager.class );
    }

    public void addNode( Node node ) {
        synchronized ( this.connectedNodes ) {
            this.connectedNodes.put( node.getId(), node );
        }

        this.logger.info( "Added node: " + node.toString() );
    }

    public void removeNode ( Node node ) {
        synchronized ( this.connectedNodes ) {
            this.connectedNodes.remove( node.getId(), node );
        }

        this.logger.info( "Removed node: " + node.toString() );
    }

    public Node getNodeById( HUID huid ) {
        synchronized ( this.connectedNodes ) {
            return this.connectedNodes.get( huid );
        }
    }

    /**
     * Broadcast a DeliveryPacket to all Nodes with the specified type
     * @param type that the broadcast should be sent to
     * @param deliveryPacket that should be sent
     * @return the count of nodes the packet was sent to
     */
    public int broadcastByType( NodeType type, DeliveryPacket deliveryPacket ) {
        int count = 0;
        synchronized ( this.connectedNodes ) {
            for ( Node node : connectedNodes.values() ) {
                if ( node.getType() == type ) {
                    node.send( deliveryPacket );
                    count++;
                }
            }
        }
        return count;
    }

}
