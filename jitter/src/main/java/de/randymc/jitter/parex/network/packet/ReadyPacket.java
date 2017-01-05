package de.randymc.jitter.parex.network.packet;

import de.randymc.jitter.parex.network.Packet;
import de.randymc.jitter.parex.network.Protocol;
import io.netty.buffer.ByteBuf;

/**
 * @author Digot
 * @version 1.0
 */
public class ReadyPacket extends Packet {

    public ReadyPacket () {
        super( Protocol.PACKET_READY );
    }

    @Override
    public void write ( ByteBuf buffer ) {

    }

    @Override
    public void read ( ByteBuf buffer ) {

    }
}
