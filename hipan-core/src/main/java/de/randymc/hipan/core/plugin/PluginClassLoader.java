package de.randymc.hipan.core.plugin;

import de.randymc.hipan.core.bootstrap.HipanCore;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * A custom class loader that loads the appropriate classes for plugins
 *
 * @author Digot
 * @version 1.0
 */
public class PluginClassLoader extends URLClassLoader {

    private static ClassLoader serverClassLoader;

    static {
        ClassLoader.registerAsParallelCapable();
        PluginClassLoader.serverClassLoader = HipanCore.class.getClassLoader();
    }

    public PluginClassLoader ( URL[] urls ) {
        super( urls );
    }

    @Override
    protected Class<?> loadClass( String name, boolean resolve ) throws ClassNotFoundException {
        try {
            //Try to find the class in the classpath of the plugin
            return super.loadClass( name, resolve );
        } catch ( ClassNotFoundException ignored ) {
            //Try to find it in the classpath of the server
            return PluginClassLoader.serverClassLoader.loadClass( name );
        }
    }

}
