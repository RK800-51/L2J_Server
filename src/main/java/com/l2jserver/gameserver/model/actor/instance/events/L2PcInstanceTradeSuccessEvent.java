package com.l2jserver.gameserver.model.actor.instance.events;

/**
 * Represents trade info after successful trade between players.
 */

public class L2PcInstanceTradeSuccessEvent implements L2PcInstanceEvent {

    private final long eventTime;
    private final int ownerId;
    private final int partnerId;
    private final EventType _tradeType;

    public L2PcInstanceTradeSuccessEvent(long eventTime, int ownerId,
                                        int partnerId, EventType _tradeType) {
        this.eventTime = eventTime;
        this.ownerId = ownerId;
        this.partnerId = partnerId;
        this._tradeType = _tradeType;
    }

    @Override
    public long getEventTime() {
        return eventTime;
    }
    @Override
    public int getOwnerId() {
        return ownerId;
    }
    @Override
    public int getPartnerId() {
        return partnerId;
    }
    public EventType getEventType() {
        return _tradeType;
    }
}
