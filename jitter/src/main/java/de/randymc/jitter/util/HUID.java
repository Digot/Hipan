package de.randymc.jitter.util;

import de.randymc.jitter.parex.network.NodeType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a unique identifier.
 *
 * @author Digot
 * @version 1.0
 */
@AllArgsConstructor
public class HUID {

    private static final SecureRandom RANDOM  = new SecureRandom();
    private static final ThreadLocal<StringBuilder> LOCAL_STRING_BUILDER = new ThreadLocal<>();

    private static final int          DEFAULT_LENGTH  = 7;
    private static final Charset      DEFAULT_CHARSET = Charset.forName( "UTF-8" );
    private static final char[]       CHARS           = "abcdefghijklmnopqrstuvwxyz123456789123456789".toCharArray();
    private static final Set<Integer> USED_HUIDS      = new HashSet<>();

    @Getter private final byte[] bytes;

    /**
     * Generates a new random HUID
     * @return the generated HUID
     */
    public static HUID randomHUID() {
        //Since multiple threads may try to receive a random HUID, it can happen, that the StringBuilder
        // gets used by multiple threads which will break everything. So give each thread it's own StringBuilder
        if( HUID.LOCAL_STRING_BUILDER.get() == null ) {
            HUID.LOCAL_STRING_BUILDER.set( new StringBuilder() );
        }

        //Generate a random byte sequence
        for ( int i = 0; i < HUID.DEFAULT_LENGTH; i++ ) {
            char c = HUID.CHARS[ HUID.RANDOM.nextInt( HUID.CHARS.length ) ];
            HUID.LOCAL_STRING_BUILDER.get().append(c);
        }

        //Retrieve the byte array
        String output = HUID.LOCAL_STRING_BUILDER.get().toString();
        byte[] bytes = output.getBytes( HUID.DEFAULT_CHARSET );

        //Check for duplicate
        if( HUID.USED_HUIDS.contains( Arrays.hashCode( bytes ) )) {
            //Very unlikely but this HUID is already in use
            return HUID.randomHUID();
        }

        //Add to used HUIDs
        HUID.USED_HUIDS.add( Arrays.hashCode( bytes ) );

        //Reset the string builder for multiple use
        HUID.LOCAL_STRING_BUILDER.get().setLength( 0 );
        return new HUID( bytes );
    }

    //TODO Nicht jedes mal eine neue Instance erzeugen, sondern cachen
    public static HUID forCores() {
        return HUID.fromString( "CORE-BROADCAST" );
    }

    public static HUID forProxies() {
        return HUID.fromString( "PROXY-BROADCAST" );
    }

    public static HUID forGameservers() {
        return HUID.fromString( "GAMESERVER-BROADCAST" );
    }

    public static HUID forDaemons() {
        return HUID.fromString( "DAEMON-BROADCAST" );
    }

    public static HUID forHydra() {
        return HUID.fromString( "HYDRA" );
    }

    public static HUID fromString( String value ) {
        return new HUID( value.getBytes( HUID.DEFAULT_CHARSET ) );
    }

    /**
     * Gets the NodeType the broadcast HUID should be to
     * @return NodeType
     */
    public NodeType getBroadcastType() {
        if ( bytes.length == 7 ) //Only random HUIDs are 7 bytes long so no broadcast
            return null;

        if ( this.equals( HUID.forCores() ) ) {
            return NodeType.CORE;
        } else if ( this.equals( HUID.forProxies() ) ) {
            return NodeType.PROXY;
        } else if ( this.equals( HUID.forGameservers() ) ) {
            return NodeType.GAMESERVER;
        } else if ( this.equals( HUID.forDaemons() ) ) {
            return NodeType.DAEMON;
        } else if ( this.equals( HUID.forHydra() ) ) {
            return NodeType.HYDRA;
        } else {
            throw new IllegalArgumentException( "Unknown HUID-Type" );
        }
    }

    @Override
    public String toString() {
        return new String ( this.bytes, HUID.DEFAULT_CHARSET );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        HUID huid = ( HUID ) o;

        return Arrays.equals( this.bytes, huid.bytes );

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode( bytes );
    }
}
