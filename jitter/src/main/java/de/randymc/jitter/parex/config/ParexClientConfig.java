package de.randymc.jitter.parex.config;

import com.blackypaw.simpleconfig.SimpleConfig;
import com.blackypaw.simpleconfig.annotation.Comment;
import lombok.Getter;

/**
 * Provides a file configuration for a Parex Client
 *
 * @author Digot
 * @version 1.0
 */
public class ParexClientConfig extends SimpleConfig {

    @Comment ( "The host the parex client should connect to")
    @Getter private String host = "localhost";

    @Comment ( "The port the parex client should connect to")
    @Getter private int port = 7960;

}
