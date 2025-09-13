package com.l2jserver.gameserver.model.actor.instance.events;

/**
 * Class-container for different types of player events.
 */
public enum EventType {

    TRADE("trade"),
    PRIVATE_SHOP("private_shop"),
    MAIL("mail");

    private final String eventType;

    EventType(String eventType) {
        this.eventType = eventType;
    }
    public String getEventType() {
        return eventType;
    }

}
