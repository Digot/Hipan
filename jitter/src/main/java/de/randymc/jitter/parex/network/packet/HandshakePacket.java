package de.randymc.jitter.parex.network.packet;

import de.randymc.jitter.parex.network.NodeType;
import de.randymc.jitter.parex.network.Packet;
import de.randymc.jitter.parex.network.Protocol;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * @author Digot
 * @version 1.0
 */
@Getter
public class HandshakePacket extends Packet {

    private NodeType type;
    private byte[] hash;

    public HandshakePacket() {
        super( Protocol.PACKET_HANDSHAKE );
    }

    public HandshakePacket( NodeType type, byte[] hash) {
        this();
        this.type = type;
        this.hash = hash;
    }

    public void write( ByteBuf buffer ) {
        buffer.writeByte( type.ordinal() );
        super.writeBytes( buffer, hash );
    }

    public void read( ByteBuf buffer ) {
        this.type = NodeType.getByOrdinal(buffer.readByte());
        this.hash = super.readBytes( buffer );
    }
}
