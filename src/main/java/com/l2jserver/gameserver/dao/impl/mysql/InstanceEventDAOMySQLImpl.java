package com.l2jserver.gameserver.dao.impl.mysql;

import com.l2jserver.gameserver.dao.InstanceEventDAO;
import com.l2jserver.gameserver.model.actor.instance.events.L2PcInstanceEvent;
import com.l2jserver.gameserver.model.actor.instance.events.L2PcInstanceItem;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * InstanceEvent DAO MySQL implementation.
 */

public class InstanceEventDAOMySQLImpl implements InstanceEventDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceEventDAOMySQLImpl.class);
    private static final String INSERT_EVENT = "INSERT INTO instance_trade_events (event_time, owner_id, partner_id, trade_type)" +
            " VALUES (?, ?, ?, ?)";
    private static final String INSERT_ITEMS = "INSERT INTO instance_trade_items (event_id, item_id) VALUES (?, ?)";

    /**
     * Saves trade event and trade items list in one transaction.
     *
     * @param event event object.
     * @param itemList list of items save.
     */
    public void insert(L2PcInstanceEvent event, List<L2PcInstanceItem> itemList) {
        try {
            var connection = ConnectionFactory.getInstance().getConnection();
            connection.setAutoCommit(false);
            Long eventId = insertAndReturnEventId(event, connection);

            for (L2PcInstanceItem item : itemList) {
                item.setEventId(eventId);
            }

            saveAllItems(itemList, connection);

            connection.commit();
            connection.setAutoCommit(true);
        } catch (Exception e) {
            LOGGER.log(Level.ALL, "Could not insert trade event data! " + e.getMessage(), e);
        }
    }

    /**
     * Saves trade items list in one batch.
     *
     * @param itemList list of items to save.
     * @param con connection object.
     */
    public void saveAllItems(List<L2PcInstanceItem> itemList, Connection con)
    {
        if (itemList == null || itemList.isEmpty()) {
            return;
        }
        try {
            PreparedStatement preparedStatement = con.prepareStatement(INSERT_ITEMS);
            for (L2PcInstanceItem item : itemList) {
                preparedStatement.setLong(1, item.getEventId());
                preparedStatement.setInt(2, item.getItemId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();

        } catch (Exception ex) {
            LOGGER.log(Level.ALL, "Could not insert trade item data! " + ex.getMessage(), ex);
        }
    }

    /**
     * Saves trade event.
     *
     * @param event trade event object to save.
     * @param con connection object.
     */
    public Long insertAndReturnEventId(L2PcInstanceEvent event, Connection con) {
        Long eventId = null;
        try {
            PreparedStatement preparedStatement = con.prepareStatement(INSERT_EVENT, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setTimestamp(1, new Timestamp(event.getEventTime()));
            preparedStatement.setInt(2, event.getOwnerId());
            preparedStatement.setInt(3, event.getPartnerId());
            preparedStatement.setString(4, event.getEventType().toString());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                eventId = resultSet.getLong(1);
            }

        } catch (Exception ex) {
            LOGGER.log(Level.ALL, "Could not insert trade event data! " + ex.getMessage(), ex);
            return null;
        }
        return eventId;
    }
}
