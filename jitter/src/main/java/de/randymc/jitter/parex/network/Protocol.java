package de.randymc.jitter.parex.network;

import de.randymc.jitter.parex.network.packet.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * @author Digot
 * @version 1.0
 */
@AllArgsConstructor (access = AccessLevel.PRIVATE)
public class Protocol {

    public static final byte PACKET_HANDSHAKE = 0x00;
    public static final byte PACKET_ASSIGNMENT = 0x01;
    public static final byte PACKET_READY = 0x02;
    public static final byte PACKET_PING = 0x03;
    public static final byte PACKET_DELIVERY = 0x04;
    public static final byte PACKET_EVENT = 0x05;
    public static final byte PACKET_DISCONNECT = 0x06;
    public static final byte PACKET_KEEPALIVE = 0x07;

    public static Packet newPacket( byte packetId ) {
        switch ( packetId ) {
            case PACKET_HANDSHAKE:
                return new HandshakePacket();
            case PACKET_ASSIGNMENT:
                return new AssignmentPacket();
            case PACKET_READY:
                return new ReadyPacket();
            case PACKET_PING:
                return new PingPacket();
            case PACKET_DELIVERY:
                return new DeliveryPacket();
            case PACKET_EVENT:
                return new EventPacket();
            case PACKET_DISCONNECT:
                return new DisconnectPacket();
            case PACKET_KEEPALIVE:
                return new KeepAlivePacket();
            default: break;
        }

        return null;
    }

}
