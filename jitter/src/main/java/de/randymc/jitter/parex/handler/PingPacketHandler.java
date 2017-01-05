package de.randymc.jitter.parex.handler;

import de.randymc.hipan.async.Promise;
import de.randymc.hipan.async.Promises;
import de.randymc.jitter.parex.Node;
import de.randymc.jitter.parex.PacketHandler;
import de.randymc.jitter.parex.ParexComponent;
import de.randymc.jitter.parex.network.packet.PingPacket;

/**
 * @author Moritz Beck (http://mbuniverse.de)
 * @version 1.0
 */
public class PingPacketHandler extends PacketHandler<PingPacket> {
    private final ParexComponent parexComponent;

    public PingPacketHandler( ParexComponent parexComponent ) {
        super( PingPacket.class );
        this.parexComponent = parexComponent;
    }

    @Override
    public Promise<PingPacket> handleDelivery( Node node, PingPacket packet ) {
        PingPacket pingPacket = new PingPacket();
        pingPacket.setTimeSent( packet.getTimeSent() );
        pingPacket.setTimeResponded( System.nanoTime() );

        return Promises.resolve(pingPacket);
    }

}
