package de.randymc.hipan.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify the version of a {@link Plugin}.
 *
 * @author Digot
 * @version 1.0
 */
@Target ( ElementType.TYPE )
@Retention ( RetentionPolicy.RUNTIME )
public @interface Version {
    int major ();
    int minor ();
}
