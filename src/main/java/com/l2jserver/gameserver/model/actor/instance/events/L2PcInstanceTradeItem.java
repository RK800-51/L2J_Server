package com.l2jserver.gameserver.model.actor.instance.events;

/**
 * Represents item info after successful trade between players.
 */
public class L2PcInstanceTradeItem implements L2PcInstanceItem{

    private Long eventId;
    private final int itemId;
    private final boolean ownerFlag;
    private final long amount;

    public L2PcInstanceTradeItem(int itemId, boolean ownerFlag, long amount) {
        this.itemId = itemId;
        this.ownerFlag = ownerFlag;
        this.amount = amount;
    }

    @Override
    public long getAmount() {
        return this.amount;
    }

    @Override
    public boolean getOwnerFlag() {
        return ownerFlag;
    }

    @Override
    public long getEventId() {
        return eventId;
    }

    @Override
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public int getItemId() {
        return itemId;
    }
}
