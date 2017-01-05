package de.randymc.hydra.handler;

import de.randymc.hydra.bootstrap.Hydra;
import de.randymc.jitter.parex.Node;
import de.randymc.jitter.parex.PacketHandler;
import de.randymc.jitter.parex.network.packet.KeepAlivePacket;

import org.slf4j.Logger;

/**
 * Created by chnkf on 12.05.2016.
 */
public class KeepAliveHandler extends PacketHandler<KeepAlivePacket> {

    private final Hydra hydra;
    private final Logger logger;

    public KeepAliveHandler(Hydra hydra, Logger logger) {
        super( KeepAlivePacket.class );
        this.hydra = hydra;
        this.logger = logger;
    }

    @Override
    public void handle( Node node, KeepAlivePacket packet ) {
        node.setLastkeepalive( System.currentTimeMillis() );
        this.logger.info( "Get KeepAlivePacket from: " + node.getId().toString() );
    }
}
