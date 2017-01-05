package de.randymc.jitter.parex.network.packet;

import de.randymc.jitter.util.HUID;
import de.randymc.jitter.parex.network.Packet;
import de.randymc.jitter.parex.network.Protocol;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

/**
 * A packet that contains a packet that is sent over Hydra
 *
 * @author Digot
 * @version 1.0
 */
public class DeliveryPacket<T extends Packet> extends Packet {

    @Getter @Setter private HUID from;
    @Getter @Setter private HUID to;
    @Getter @Setter private HUID deliveryId;
    @Getter @Setter private boolean requiresResponse;

    @Getter private T delivery;

    public DeliveryPacket() {
        super( Protocol.PACKET_DELIVERY );
    }

    public DeliveryPacket( HUID from, HUID to, T delivery ) {
        this();
        this.from = from;
        this.to = to;
        this.delivery = delivery;

        this.deliveryId = HUID.randomHUID();
    }

    @Override
    public void write( ByteBuf buffer ) throws Exception {
        super.writeBytes( buffer, this.from.getBytes() );
        super.writeBytes( buffer, this.to.getBytes() );
        super.writeBytes( buffer, this.deliveryId.getBytes() );

        buffer.writeBoolean( this.requiresResponse );
        buffer.writeByte( this.delivery.getId() );
        this.delivery.write( buffer );
    }

    @Override
    public void read( ByteBuf buffer ) throws Exception {
        this.from = new HUID( super.readBytes( buffer ) );
        this.to = new HUID( super.readBytes( buffer ) );
        this.deliveryId = new HUID( super.readBytes( buffer ) );
        this.requiresResponse = buffer.readBoolean();

        byte deliveryPacketId = buffer.readByte();
        this.delivery = ( T ) Protocol.newPacket( deliveryPacketId );

        if( this.delivery == null ) {
            throw new IllegalStateException( "The delivery id is not valid! (" + deliveryPacketId + ")" );
        }
        else {
            this.delivery.read( buffer );
        }
    }

    public void swap() {
        HUID tmp = this.from;
        this.from = this.to;
        this.to = tmp;
    }

    @Override
    public String toString ( ) {
        return "DeliveryPacket{" +
                "from=" + from +
                ", to=" + to +
                ", deliveryId=" + deliveryId +
                '}';
    }
}
