package de.randymc.hipan.plugin;

import lombok.Data;

import java.io.File;

/**
 * Provides basic information about a plugin
 *
 * @author Digot
 * @version 1.0
 */
@Data
public class PluginMeta {

    private final String name;
    private final PluginVersion version;
    private final String mainClass;
    private final File pluginFile;

}
