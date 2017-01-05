package de.randymc.jitter.parex.network;

import lombok.Getter;

/**
 * Created by Moritz Beck (http://mbuniverse.de) on 01.04.16.
 */
public enum NodeType {

    CORE,
    PROXY,
    GAMESERVER,
    DAEMON,
    HYDRA,
    OTHER;

    // Created this additional field because NodeType.values() is quiet performance intensive
    @Getter
    private static NodeType[] values = values();

    public static NodeType getByOrdinal( int ordinal ) {
        return values[ ordinal ];
    }
}
