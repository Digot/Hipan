package de.randymc.jitter.util;

import com.blackypaw.simpleconfig.SimpleConfig;

import java.io.File;
import java.io.FileWriter;

/**
 * @author Digot
 * @version 1.0
 */
public class ConfigHelper {

    private static File CONFIG_DIRECTORY = new File( "config" );

    public static <T extends SimpleConfig> T loadConfig ( Class<T> clazz, String fileName ) throws Exception {
        if( !ConfigHelper.CONFIG_DIRECTORY.exists() ) {
            ConfigHelper.CONFIG_DIRECTORY.mkdir();
        }

        File filePath =  new File( ConfigHelper.CONFIG_DIRECTORY, fileName );
        T config = clazz.newInstance();

        if( config == null ) return null;

        if( !filePath.exists() ) {
            try ( FileWriter fileWriter = new FileWriter( filePath ) ) {
                config.write( fileWriter );
            }
        }

        config.initialize( filePath );
        return config;
    }

}
