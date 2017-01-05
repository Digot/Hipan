package de.randymc.jitter.parex.netty;

import de.randymc.jitter.parex.network.Packet;
import de.randymc.jitter.parex.network.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Digot
 * @version 1.0
 */
@Slf4j
public class Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode( ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list ) throws Exception {
        if(byteBuf instanceof EmptyByteBuf) {
            return;
        }

        //Retrieve the id
        byte id = byteBuf.readByte();
        Packet packet = Protocol.newPacket( id );


        if(packet != null) {
            packet.read( byteBuf );
            list.add( packet );
        }
        else {
            Decoder.log.warn( "Received unknown packet id " + id );
        }
    }

}
