package com.l2jserver.gameserver.model.actor.instance.events;

/**
 * Base Event. Interface for events stored in DB
 */
public interface L2PcInstanceEvent {
    public long getEventTime();
    public int getOwnerId();
    public int getPartnerId();
    public EventType getEventType();

}
