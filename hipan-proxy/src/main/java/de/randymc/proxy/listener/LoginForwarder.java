package de.randymc.proxy.listener;

import de.randymc.hipan.event.UserLoginEvent;
import de.randymc.jitter.util.Timer;
import de.randymc.proxy.main.HipanProxy;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * @author Digot
 * @version 1.0
 */
public class LoginForwarder implements Listener {

    private final HipanProxy hipanProxy;
    private final Logger logger;

    public LoginForwarder ( HipanProxy hipanProxy ) {
        this.hipanProxy = hipanProxy;
        this.logger = LoggerFactory.getLogger( LoginForwarder.class );
    }

    @EventHandler
    public void onLogin ( LoginEvent event ) {
        try ( Timer timer = new Timer( this.logger, "UserLoginEvent" ) ) {
            //Construct the event
            UserLoginEvent userLoginEvent = new UserLoginEvent( event.getConnection().getUniqueId() );

            //Process it
            Future< UserLoginEvent > future = this.hipanProxy.getDeliveryProcessor().processEvent( userLoginEvent, true );

            try {
                userLoginEvent = future.get(
                        this.hipanProxy.getEventTimeout(),
                        this.hipanProxy.getEventTimeoutUnit() );
            } catch ( TimeoutException | InterruptedException | ExecutionException e ) {
                this.logger.warn( "Failed to process LoginEvent: Timeout!", e.getClass().getSimpleName() );
                return;
            }

            //No errors
            event.setCancelled( userLoginEvent.isCancelled() );
            event.setCancelReason( userLoginEvent.getCancelReason() );
        }
    }
}
