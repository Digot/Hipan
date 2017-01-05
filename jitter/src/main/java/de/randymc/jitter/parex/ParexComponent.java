package de.randymc.jitter.parex;

import de.randymc.hipan.async.Callback;
import de.randymc.jitter.parex.handler.PingPacketHandler;
import de.randymc.jitter.parex.netty.ChannelHandler;
import de.randymc.jitter.parex.netty.Decoder;
import de.randymc.jitter.parex.netty.Encoder;
import de.randymc.jitter.parex.network.Packet;
import de.randymc.jitter.util.HUID;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * Defines the structure of a {@link ParexClient} and a {@link ParexServer}

 * @author Digot
 * @version 1.0
 */
public abstract class ParexComponent {

    public static final byte CLIENT = 0x00;
    public static final byte SERVER = 0x01;

    @Getter @Setter private Callback unhandledPacketCallback;
    @Getter private byte type;

    public ParexComponent() {
        if( Thread.currentThread().getStackTrace()[2].getClassName()
                .equals( ParexClient.class.getName() ) ) {
            //It's a parex client
            this.type = ParexComponent.CLIENT;
        }
        else if( Thread.currentThread().getStackTrace()[2].getClassName()
                .equals( ParexServer.class.getName() ) ) {
            //It's a parex server
            this.type = ParexComponent.SERVER;
        }
    }

    public abstract Logger getLogger ();

    public abstract void onClientConnected ( Channel channel );

    protected void preparePipeline( SocketChannel socketChannel ) {
        socketChannel.pipeline().addLast( new Encoder() );
        socketChannel.pipeline().addLast( new Decoder() );
        socketChannel.pipeline().addLast( this.getChannelHandler() );
    }

    public void addHandler( PacketHandler packetHandler ){
        this.getChannelHandler().registerHandler( packetHandler );
    }

    public void removeHandler( PacketHandler packetHandler ) {
        this.getChannelHandler().unregisterHandler( packetHandler );
    }

    public void shutdown() throws IOException {
        //Send shutdown packet or smth like that
        this.getChannelHandler().close();
    }

    public abstract HUID getNodeId();

    public abstract ChannelHandler getChannelHandler();

}
