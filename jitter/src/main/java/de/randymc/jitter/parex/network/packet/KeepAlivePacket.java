package de.randymc.jitter.parex.network.packet;

import de.randymc.jitter.parex.network.Packet;
import de.randymc.jitter.parex.network.Protocol;
import io.netty.buffer.ByteBuf;

/**
 * Created by chnkf on 12.05.2016.
 */
public class KeepAlivePacket extends Packet {

    public KeepAlivePacket () {
        super( Protocol.PACKET_KEEPALIVE );
    }

    @Override
    public void write ( ByteBuf buffer ) {

    }

    @Override
    public void read ( ByteBuf buffer ) {

    }

}
