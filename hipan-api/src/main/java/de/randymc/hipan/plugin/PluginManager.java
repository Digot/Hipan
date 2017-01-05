package de.randymc.hipan.plugin;

import de.randymc.hipan.command.Command;
import de.randymc.hipan.command.CommandSender;
import de.randymc.hipan.event.Event;
import de.randymc.hipan.event.Listener;

/**
 * Manages all plugins of a core instance.
 *
 * @author Digot
 * @version 1.0
 */
public interface PluginManager {

     /**
     * Loads and initializes the plugins of the GoMint server
     */
    void loadPlugins();

    /**
     * Wrapper to register an {@link Listener}
     * @param listener to register
     */
    void registerListener( Listener listener );

    /**
     * Wrapper to register a {@link Command}
     * @param command to register
     */
    void registerCommand( Command command );

    /**
     * Wrapper to call an {@link Event}
     * @param event to call
     */
    void callEvent( Event event );

    /**
     * Wrapper to invoke a command with arguments
     */
    void executeCommand ( CommandSender sender, String name, String... args );

    /**
     * Wrapper to invoke a command without arguments
     * @param sender who invoked the command
     * @param name of the triggered command
     */
    void executeCommand ( CommandSender sender, String name);
}
