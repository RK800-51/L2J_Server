package com.l2jserver.gameserver.model.actor.instance.events;

public interface L2PcInstanceItem {
    public long getEventId();
    public int getItemId();
    public boolean getOwnerFlag();
    public long getAmount();

    public void setEventId(Long eventId);
}
