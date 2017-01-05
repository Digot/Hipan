package de.randymc.hydra.handler;

import de.randymc.jitter.util.HUID;
import de.randymc.hydra.bootstrap.Hydra;
import de.randymc.jitter.parex.Node;
import de.randymc.jitter.parex.PacketHandler;
import de.randymc.jitter.parex.network.packet.AssignmentPacket;
import de.randymc.jitter.parex.network.packet.HandshakePacket;

/**
 * @author Digot
 * @version 1.0
 */
public class HandshakeHandler extends PacketHandler<HandshakePacket> {

    public HandshakeHandler ( Hydra hydra ) {
        super( HandshakePacket.class );
    }

    @Override
    public void handle ( Node node, HandshakePacket packet ) {
        HUID huid = HUID.randomHUID();

        node.send( new AssignmentPacket( huid ) );
        node.setId( huid );
        node.setType( packet.getType() );
    }
}
