package de.randymc.jitter.parex.network.packet;

import de.randymc.jitter.util.HUID;
import de.randymc.jitter.parex.network.Packet;
import de.randymc.jitter.parex.network.Protocol;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * @author Digot
 * @version 1.0
 */
public class AssignmentPacket extends Packet {

    @Getter private HUID nodeId;

    public AssignmentPacket () {
        super( Protocol.PACKET_ASSIGNMENT );
    }

    public AssignmentPacket ( HUID nodeId ) {
        this();
        this.nodeId = nodeId;
    }

    @Override
    public void write ( ByteBuf buffer ) {
        super.writeBytes( buffer, this.nodeId.getBytes() );
    }

    @Override
    public void read ( ByteBuf buffer ) {
        this.nodeId = new HUID( super.readBytes( buffer ) );
    }
}
