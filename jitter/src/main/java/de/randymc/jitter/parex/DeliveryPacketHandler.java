package de.randymc.jitter.parex;

import de.randymc.jitter.parex.network.packet.DeliveryPacket;

/**
 * @author Moritz Beck (http://mbuniverse.de)
 * @version 1.0
 */
public interface DeliveryPacketHandler {

    boolean handle ( Node node, DeliveryPacket packet );
}
