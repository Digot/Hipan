package de.randymc.hipan.core.plugin;

import com.google.common.base.Preconditions;
import de.randymc.hipan.command.Command;
import de.randymc.hipan.command.CommandSender;
import de.randymc.hipan.core.bootstrap.HipanCore;
import de.randymc.hipan.event.Event;
import de.randymc.hipan.event.Listener;
import de.randymc.hipan.plugin.Plugin;
import de.randymc.hipan.plugin.PluginManager;
import de.randymc.hipan.plugin.PluginMeta;
import de.randymc.hipan.plugin.PluginVersion;
import de.randymc.hipan.scheduling.Scheduler;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Digot
 * @version 1.0
 */
public class HipanPluginManager implements PluginManager {

    private final Logger logger = LoggerFactory.getLogger( PluginManager.class );
    private final Scheduler scheduler;
    private final File pluginsFolder;

    //private final EventManager eventManager;
   // private final CommandManager commandManager;

    private final Set<Plugin > installedPlugins;
    private final Set<PluginMeta> detectedPlugins;

    public HipanPluginManager( HipanCore core ) {
        this.scheduler = core.getScheduler();
        this.installedPlugins = new HashSet<>();
        this.detectedPlugins = new LinkedHashSet<>();
        this.pluginsFolder = new File( "plugins/" ); //Can be customizable in the future using command line args or config

       // this.eventManager = new EventManager();
        //this.commandManager = new CommandManager();
    }


    public void loadPlugins(){
        // STEP 1: Scan the plugins folder for plugnins
        if( !this.scanForPlugins() ) return;
        // STEP 2: Load the classes of the plugins and construct them
        this.installPlugins();
        // STEP 3: Invoke the enable hook on all plugins
        this.enablePlugins();
    }

    private void enablePlugins ( ) {
        for ( Plugin installedPlugin : this.installedPlugins ) {
            try{
                installedPlugin.onEnable(); //Not sure if onInstall would be called here
                this.logger.info( "Enabled " + installedPlugin.toString() );
            }
            catch ( Exception e ) {
                this.logger.error( "Failed to enable " + installedPlugin.getName() + "!", e);
            }
        }
    }

    private void installPlugins () {
        for ( PluginMeta detectedPlugin : this.detectedPlugins ) {
            try {
                //Load the main class
                ClassLoader classLoader = new PluginClassLoader(new URL[] { detectedPlugin.getPluginFile().toURI().toURL() } );
                Class<?> pluginClass = classLoader.loadClass( detectedPlugin.getMainClass() );

                //Retrieve the constructor
                Constructor<?> constructor = pluginClass.getConstructor();
                constructor.setAccessible( true );

                //Initialize the instance
                Plugin plugin = ( Plugin ) constructor.newInstance();
                plugin.setMetaData( detectedPlugin );
                plugin.setPluginManager( this );

                //Add to installed plugins
                this.installedPlugins.add( plugin );
            } catch ( Exception e ) {
                this.logger.error( "Failed to install plugin " + detectedPlugin.getName() + ": " + e.getMessage() );
            }
        }

        //We don't need the detected plugin collection anymore. Clear it.
        this.detectedPlugins.clear();
    }

    /**
     * Scans the plugin folder (and creates it if it doesn't exist) for plugins
     * @return if any plugins were found
     */
    private boolean scanForPlugins(){
        //Check if the plugin directory exists
        if( !this.pluginsFolder.exists() ) {
            //It doesn't exist - create it
            if( !this.pluginsFolder.mkdir() ){
                this.logger.error( "Couldn't create plugins folder. Check your file permissions!" );
                return false;
            }
        }

        //Retrieve a list of all files inside the plugins folder
        File[] files = this.pluginsFolder.listFiles();

        if( files == null ||  files.length == 0 ) {
            //Do nothing
            return false;
        }

        //Start scanning
        for ( File file : files ) {
            if( file.getName().toLowerCase().endsWith( ".jar" )) {
                try( JarFile jarFile = new JarFile( file )) {
                    //Lets find the main class of the plugin
                    ClassFile mainClassFile = null;
                    File mainFile = null;

                    //Retrieve all elements in the plugin jar file
                    Enumeration<JarEntry > entryEnumeration = jarFile.entries();
                    while( entryEnumeration.hasMoreElements() ) {
                        JarEntry jarEntry = entryEnumeration.nextElement();

                        //Check if its a class file
                        if( jarEntry.getName().toLowerCase().endsWith( ".class" )) {
                            ClassFile classFile = new ClassFile( new DataInputStream( jarFile.getInputStream( jarEntry ) ) );

                            if( classFile.getSuperclass().equals( Plugin.class.getName() ) ) {
                                if( mainClassFile == null ) {
                                    //Set it as the main class for now
                                    mainClassFile = classFile;
                                    mainFile = file;
                                }
                                else {
                                    //There are more than one main class
                                    throw new IllegalStateException( "Multiple plugin main classes found in " + file.getName() );
                                }
                            }
                        }
                    }

                    //Check if the main class was found
                    if( mainClassFile == null ) {
                        throw new IllegalStateException( "No class that extends Plugin found in " + file.getName() );
                    }

                    //We have the main class now - let's try to load metadata
                    AnnotationsAttribute annotations = (AnnotationsAttribute) mainClassFile.getAttribute( AnnotationsAttribute.visibleTag );

                    String pluginName = null;
                    PluginVersion pluginVersion = null;

                    for ( Annotation annotation : annotations.getAnnotations() ) {
                        switch ( annotation.getTypeName() ) {
                            case "de.randymc.hipan.plugin.Name":
                                pluginName = ( (StringMemberValue ) annotation.getMemberValue( "value" ) ).getValue();
                                break;
                            case "de.randymc.hipan.plugin.Version":
                                int major = ( (IntegerMemberValue ) annotation.getMemberValue( "major" ) ).getValue();
                                int minor = ( (IntegerMemberValue) annotation.getMemberValue( "minor" ) ).getValue();
                                pluginVersion = new PluginVersion( major, minor );
                                break;
                        }
                    }

                    //Validate
                    if( pluginName == null || pluginVersion == null ) {
                        this.logger.error( "Couldn't find the name or the version of " + file.getName() + ". Are the annotations missing?" );
                        return false;
                    }

                    //Fill plugin meta
                    PluginMeta pluginMeta = new PluginMeta(
                            pluginName, pluginVersion, mainClassFile.getName(), mainFile
                    );

                    //Done! Add to detected plugins
                    this.detectedPlugins.add( pluginMeta );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    @Override
    public void registerListener ( Listener eventListener ) {
        Preconditions.checkArgument( eventListener != null, "Given EventListener is null" );
       // this.eventManager.registerListener( eventListener );
    }

    @Override
    public void registerCommand ( Command command ) {
        Preconditions.checkArgument( command != null, "Command is null" );
        //this.commandManager.registerCommand( command );
    }

    @Override
    public void callEvent ( Event event ) {
        Preconditions.checkArgument( event != null, "Given event is null" );
        //this.eventManager.triggerEvent( event );
    }

    @Override
    public void executeCommand ( CommandSender sender, String name, String... args ) {
        //this.commandManager.executeCommand( sender, name, args );
    }

    @Override
    public void executeCommand ( CommandSender sender, String name ) {
       // this.commandManager.executeCommand( sender, name, null );
    }

}
