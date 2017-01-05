package de.randymc.hipan.util;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * @author Digot
 * @version 1.0
 */
public abstract class ReadWriteable {

    public abstract void write ( ByteBuf buffer ) throws Exception;
    public abstract void read ( ByteBuf buffer ) throws Exception;

    protected void writeString ( ByteBuf buffer, String string ){
        byte[] bytes = string.getBytes( Charset.forName( "UTF-8" ) );
        int byteLength = bytes.length;
        buffer.writeInt( byteLength );
        buffer.writeBytes( bytes );
    }

    protected String readString ( ByteBuf buffer ) {
        int length = buffer.readInt();
        byte[] bytes = new byte[ length ];
        buffer.readBytes( bytes );
        return new String( bytes, Charset.forName( "UTF-8" ) );
    }

    protected void writeBytes( ByteBuf buffer, byte[] bytes ) {
        int length = bytes.length;

        buffer.writeInt( length );
        buffer.writeBytes( bytes );
    }

    protected byte[] readBytes( ByteBuf buffer ) {
        int length = buffer.readInt();
        byte[] bytes = new byte[ length ];

        buffer.readBytes( bytes );
        return bytes;
    }

    protected void writeUUID( UUID uuid, ByteBuf buffer ) {
        buffer.writeLong( uuid.getLeastSignificantBits() );
        buffer.writeLong( uuid.getMostSignificantBits() );
    }

    protected UUID readUUID( ByteBuf buffer ) {
        return new UUID( buffer.readLong(), buffer.readLong() );
    }

}
