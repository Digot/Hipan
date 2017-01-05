package de.randymc.jitter.parex.network;

import de.randymc.hipan.util.ReadWriteable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Defines the structure of a packet
 *
 * @author Digot
 * @version 1.0
 */
@AllArgsConstructor
public abstract class Packet extends ReadWriteable {

    @Getter private byte id;

}
