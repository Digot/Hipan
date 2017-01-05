package de.randymc.jitter.parex.handler;

import de.randymc.hipan.async.Promise;
import de.randymc.jitter.util.HUID;
import de.randymc.jitter.parex.Node;
import de.randymc.jitter.parex.async.HipanPromise;
import de.randymc.jitter.parex.network.Packet;
import de.randymc.jitter.parex.network.packet.DeliveryPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Moritz Beck (http://mbuniverse.de)
 * @version 1.0
 */
public class DeliveryResponseHandler {
    private Map<HUID, Promise<Packet>> promiseMap;

    public DeliveryResponseHandler() {
        this.promiseMap = new HashMap<>();
    }

    /**
     * Decides if the Delivery is a response to a Delivery or if it should be passed to the PacketHandlers
     * @param node the Delivery came from
     * @param packet that needs be handled
     * @return true if already handled, false if it should be passed to the PacketHandlers
     */
    public boolean handle( Node node, DeliveryPacket packet ) {
        Promise<Packet> promise = promiseMap.get( packet.getDeliveryId() );

        if ( promise == null ) { // There is no promise in the Map so its not a response
            return false;
        }

        promise.resolve( packet.getDelivery() );
        return true;
    }

    /**
     * Add a DeliveryPacket to handle response when it Parex receives one
     * @param packet that shoud be added
     * @param <T> type of the packet
     * @return Promise that gets fulfilled when the Delivery gets a response
     */
    @SuppressWarnings( "unchecked" )
    public <T extends Packet> Promise<T> addRequest( DeliveryPacket<T> packet ) {
        Promise<T> promise = new HipanPromise<>();
        //TODO Timeout?

        promiseMap.put( packet.getDeliveryId(), (Promise) promise );

        return promise;
    }
}
