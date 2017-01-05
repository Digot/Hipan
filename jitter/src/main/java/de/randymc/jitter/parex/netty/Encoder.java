package de.randymc.jitter.parex.netty;

import de.randymc.jitter.parex.network.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Converts a packets into bytes.
 *
 * @author Digot
 * @version 1.0
 */
@ChannelHandler.Sharable
public class Encoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode( ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf ) throws Exception {
        byte packetId = packet.getId();

        byteBuf.writeByte( packetId );
        packet.write( byteBuf );
    }

}
