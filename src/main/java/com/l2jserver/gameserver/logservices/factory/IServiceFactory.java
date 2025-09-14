package com.l2jserver.gameserver.logservices.factory;

import com.l2jserver.gameserver.logservices.EventLogService;

/**
 * Service Factory interface.
 */
public interface IServiceFactory {
    EventLogService getEventLogService();
}
