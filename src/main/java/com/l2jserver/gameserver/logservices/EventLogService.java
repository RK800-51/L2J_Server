package com.l2jserver.gameserver.logservices;


import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.events.EventType;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;

/**
 * EventLogService interface.
 */

public interface EventLogService {
    boolean logL2PcInstanceTradeEvent(L2PcInstance owner, L2PcInstance partner, InventoryUpdate ownerIU,
                                             InventoryUpdate partnerIU, EventType eventType);

    boolean logL2PcInstanceTradeEvent(int ownerId, int partnerId, InventoryUpdate ownerIU, long adenaAmount,
             EventType eventType);
}

