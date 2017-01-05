package de.randymc.jitter.parex.config;

import com.blackypaw.simpleconfig.SimpleConfig;
import com.blackypaw.simpleconfig.annotation.Comment;
import lombok.Getter;

/**
 * Provides a file configuration for a Parex Server
 *
 * @author Digot
 * @version 1.0
 */
public class ParexServerConfig extends SimpleConfig {

    @Comment ( "The port the parex server should listen on to receive data")
    @Getter private int port = 7960;

}
