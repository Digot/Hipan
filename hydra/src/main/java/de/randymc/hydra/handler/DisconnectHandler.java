package de.randymc.hydra.handler;

import de.randymc.hydra.bootstrap.Hydra;
import de.randymc.jitter.parex.Node;
import de.randymc.jitter.parex.PacketHandler;
import de.randymc.jitter.parex.network.packet.DisconnectPacket;

/**
 * Created by chnkf on 12.05.2016.
 */
public class DisconnectHandler extends PacketHandler<DisconnectPacket> {

    private final Hydra hydra;

    public DisconnectHandler(Hydra hydra) {
        super( DisconnectPacket.class );
        this.hydra = hydra;
    }

    @Override
    public void handle( Node node, DisconnectPacket packet ) {
        this.hydra.getNodeManager().removeNode( node );
    }
}
