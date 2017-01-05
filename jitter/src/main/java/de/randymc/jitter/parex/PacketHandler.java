package de.randymc.jitter.parex;

import de.randymc.hipan.async.Promise;
import de.randymc.jitter.parex.network.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Handles a specific type of a {@link Packet}.
 *
 * @author Digot
 * @version 1.0
 */
@AllArgsConstructor
public abstract class PacketHandler<T extends Packet>  {

    @Getter private Class<T> clazz;

    /**
     * Handles a packet.
     * @param node who sent the packet
     * @param packet to handle
     */
    public void handle( Node node, T packet ) {
        throw new Error( "PacketHandler not defined (" + this.clazz.getSimpleName() + ")" );
    }

    /**
     * Handles a packet that requires a response
     * @param node who sent the packet
     * @param packet to handle
     * @return
     */
    public Promise<? extends Packet> handleDelivery( Node node, T packet ) {
        throw new Error( "PacketHandler with response not defined (" + this.clazz.getSimpleName() + ")" );
    }

}
