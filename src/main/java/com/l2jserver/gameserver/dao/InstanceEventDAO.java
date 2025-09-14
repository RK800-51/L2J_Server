package com.l2jserver.gameserver.dao;


import com.l2jserver.gameserver.model.actor.instance.events.L2PcInstanceEvent;
import com.l2jserver.gameserver.model.actor.instance.events.L2PcInstanceItem;

import java.util.List;
/**
 * Instance Event DAO interface.
 */
public interface InstanceEventDAO {
    void insert(L2PcInstanceEvent event, List<L2PcInstanceItem> itemList);
}
