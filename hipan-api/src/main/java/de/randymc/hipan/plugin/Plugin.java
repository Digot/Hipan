package de.randymc.hipan.plugin;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * The main class of every Hipan plugin. Provides access to the {@link PluginManager}.
 *
 * Every plugin needs a {@link Name} and a {@link Version} annotation
 * </p>
 *
 * @author Digot
 * @version 1.0
 */
public abstract class Plugin {

    @Getter @Setter private PluginManager pluginManager;
    @Getter private String name;
    @Getter private PluginVersion version;

    /**
     * Called when the plugin gets enabled
     */
    public abstract void onEnable();

    /**
     * Called when the plugin gets disabled
     */
    public abstract void onDisable();

    /**
     * Sets meta information about the plugin
     * @param meta The meta data set by the {@link PluginManager}
     */
    public void setMetaData( PluginMeta meta ) {
        this.name = meta.getName();
        this.version = meta.getVersion();
    }
}
