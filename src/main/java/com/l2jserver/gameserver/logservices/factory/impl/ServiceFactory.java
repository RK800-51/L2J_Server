package com.l2jserver.gameserver.logservices.factory.impl;

import com.l2jserver.gameserver.logservices.EventLogService;
import com.l2jserver.gameserver.logservices.EventLogServiceImpl;
import com.l2jserver.gameserver.logservices.factory.IServiceFactory;

/**
 * ServiceFactory implementation.
 */
public enum ServiceFactory implements IServiceFactory {
    INSTANCE;

    private final EventLogService tradeEventLogService = new EventLogServiceImpl();

    public static ServiceFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public EventLogService getEventLogService() {
        return tradeEventLogService;
    }
}
