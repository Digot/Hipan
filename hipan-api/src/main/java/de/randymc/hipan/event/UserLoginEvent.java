package de.randymc.hipan.event;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Called whenever a player wants to login. Called before the PostLoginEvent and after the PreLoginEvent
 *
 * @author Digot
 * @version 1.0
 */
@NoArgsConstructor
public class UserLoginEvent extends Event implements Cancelable {

    @Getter private UUID playerUUID;
    @Getter @Setter private boolean cancelled;
    @Getter @Setter private String cancelReason;

    public UserLoginEvent( UUID uuid ) {
        this.playerUUID = uuid;
    }

    @Override
    public void write( ByteBuf buffer ) throws Exception {
        super.writeUUID( this.playerUUID, buffer );
    }

    @Override
    public void read( ByteBuf buffer ) throws Exception {
        this.playerUUID = super.readUUID( buffer );
    }
}
