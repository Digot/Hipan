package de.randymc.proxy;

import de.randymc.hipan.event.Event;
import de.randymc.jitter.util.HUID;
import de.randymc.jitter.parex.Node;
import de.randymc.jitter.parex.PacketHandler;
import de.randymc.jitter.parex.network.Packet;
import de.randymc.jitter.parex.network.packet.DeliveryPacket;
import de.randymc.jitter.parex.network.packet.EventPacket;
import de.randymc.proxy.main.HipanProxy;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author Digot
 * @version 1.0
 */
public class DeliveryProcessor {

    private final HipanProxy hipanProxy;
    private final Set<DeliveryPacket> receivedPackets;


    public DeliveryProcessor( HipanProxy hipanProxy ) {
        this.hipanProxy = hipanProxy;
        this.receivedPackets = new HashSet<>();

        this.hipanProxy.getParexClient().addHandler( new PacketHandler<DeliveryPacket>( DeliveryPacket.class ) {
            @Override
            public void handle( Node node, DeliveryPacket packet ) {
                synchronized ( DeliveryProcessor.this.receivedPackets ) {
                    DeliveryProcessor.this.receivedPackets.add( packet );
                }

            }
        } );
    }

    public <T extends Event> FutureTask<T> processEvent( Event event, boolean waitForResponse ) {
        DeliveryPacket deliveryPacket = new DeliveryPacket( this.hipanProxy.getParexClient().getNodeId(), HUID.forCores(), new EventPacket( event ) );
        //this.hipanProxy.getParexClient().broadcast( deliveryPacket );

        if( !waitForResponse ) {
            return null;
        }

        FutureTask<T> task = new FutureTask<>( new Callable<T>() {
            @Override
            public T call() throws Exception {
                while( true ) {
                    synchronized ( DeliveryProcessor.this.receivedPackets ) {
                        for ( DeliveryPacket receivedPacket : DeliveryProcessor.this.receivedPackets ) {
                            if( receivedPacket.getDeliveryId().equals( deliveryPacket.getDeliveryId() ) ) {
                                EventPacket eventPacket = ( EventPacket ) receivedPacket.getDelivery();
                                return ( T ) eventPacket.getEvent();
                            }
                        }
                    }
                }
            }


        });

        this.hipanProxy.getProxy().getScheduler().runAsync( this.hipanProxy, task );
        return task;
    }


    public <T extends Packet> FutureTask<T> process( Packet packet, boolean waitForResponse ) {
        DeliveryPacket deliveryPacket = new DeliveryPacket( this.hipanProxy.getParexClient().getNodeId(), HUID.forCores(), packet );

        FutureTask<T> task = new FutureTask<>( new Callable<T>() {
            @Override
            public T call() throws Exception {
                while( true ) {
                    synchronized ( DeliveryProcessor.this.receivedPackets ) {
                        for ( DeliveryPacket receivedPacket : DeliveryProcessor.this.receivedPackets ) {
                            if( receivedPacket.getDeliveryId().equals( deliveryPacket.getDeliveryId() ) ) {
                                return ( T ) receivedPacket.getDelivery();
                            }
                        }
                    }
                }
            }


        });

        this.hipanProxy.getProxy().getScheduler().runAsync( this.hipanProxy, task );
        return task;
    }
}
