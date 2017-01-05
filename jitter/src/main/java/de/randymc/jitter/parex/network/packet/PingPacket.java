package de.randymc.jitter.parex.network.packet;

import de.randymc.jitter.parex.network.Packet;
import de.randymc.jitter.parex.network.Protocol;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Moritz Beck (http://mbuniverse.de)
 * @version 1.0
 */
public class PingPacket extends Packet {
    @Getter @Setter private long timeSent;
    @Getter @Setter private long timeResponded;

    public PingPacket() {
        super( Protocol.PACKET_PING );
    }

    @Override
    public void write( ByteBuf buffer ) {
        buffer.writeLong( this.timeSent );
        buffer.writeLong( this.timeResponded );
    }

    @Override
    public void read( ByteBuf buffer ) {
        this.timeSent = buffer.readLong();
        this.timeResponded = buffer.readLong();
    }
}
