package de.randymc.jitter.parex.network.packet;

import de.randymc.hipan.event.Event;
import de.randymc.jitter.parex.network.Packet;
import de.randymc.jitter.parex.network.Protocol;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * @author Digot
 * @version 1.0
 */
public class EventPacket extends Packet {

    @Getter private Event event;

    public EventPacket () {
        super( Protocol.PACKET_EVENT );
    }

    public EventPacket ( Event event ) {
        this();
        this.event = event;
    }

    @Override
    public void write ( ByteBuf buffer ) throws Exception {
        //When working with Events we use the class names to load the classes and not ids
        super.writeString( buffer, this.event.getClass().getName() );
        this.event.write( buffer );
    }

    @Override
    public void read ( ByteBuf buffer ) throws Exception {
        String eventClassName = super.readString( buffer );

        if( eventClassName == null ) {
            //Something went wrong
            throw new NullPointerException( "Event class name is null" );
        }

        Class<?> clazz = Class.forName( eventClassName );

        try {
            this.event = ( Event ) clazz.newInstance();
            this.event.read( buffer );
        } catch ( InstantiationException | IllegalAccessException e ) {
            e.printStackTrace();
        }
    }
}
