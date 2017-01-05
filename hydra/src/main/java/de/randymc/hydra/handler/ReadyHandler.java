package de.randymc.hydra.handler;

import de.randymc.hydra.bootstrap.Hydra;
import de.randymc.jitter.parex.Node;
import de.randymc.jitter.parex.PacketHandler;
import de.randymc.jitter.parex.network.packet.ReadyPacket;

/**
 * @author Digot
 * @version 1.0
 */
public class ReadyHandler extends PacketHandler<ReadyPacket> {

    private final Hydra hydra;


    public ReadyHandler ( Hydra hydra ) {
        super( ReadyPacket.class );
        this.hydra = hydra;
    }

    @Override
    public void handle ( Node node, ReadyPacket packet ) {
        this.hydra.getNodeManager().addNode( node );
    }
}
