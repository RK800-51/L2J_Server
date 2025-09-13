package com.l2jserver.gameserver.logservices;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.model.ItemInfo;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.events.EventType;
import com.l2jserver.gameserver.model.actor.instance.events.L2PcInstanceItem;
import com.l2jserver.gameserver.model.actor.instance.events.L2PcInstanceTradeItem;
import com.l2jserver.gameserver.model.actor.instance.events.L2PcInstanceTradeSuccessEvent;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;
import static com.l2jserver.gameserver.model.itemcontainer.Inventory.ADENA_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service layer for trade events logging.
 * One L2PcInstanceTradeSuccessEvent and 1+ L2PcInstanceTradeItem objects together is a full trade log for one side of trade.
 * Therefore, each trade in game has two trade logs - for each side. The ownership of objects is determined by boolean ownerFlag
 */
public class EventLogServiceImpl implements EventLogService {

    private static final Logger LOG = LoggerFactory.getLogger(EventLogServiceImpl.class);

    private void asyncLog(Runnable task) {
        ThreadPoolManager.getInstance().executeGeneral(task);
    }

    /**
     * Takes trade data and converts it to L2PcInstanceTradeSuccessEvent and L2PcInstanceTradeItem.
     * Used for TRADE and PRIVATE_SHOP types
     * @param owner - object of player, who owns items, exchanges them and receives items in return. Logging happens on his behalf.
     * @param partner - object of player, who receives items and gives items in return.
     * @param ownerIU - object of owner's inventory change.
     * @param partnerIU - object of partner's inventory change.
     * @param eventType - enum type of exchange event (trade or private shop).
     */
    public boolean logL2PcInstanceTradeEvent(L2PcInstance owner, L2PcInstance partner, InventoryUpdate ownerIU,
                                        InventoryUpdate partnerIU, EventType eventType) {
        final int ownerId = owner.getId();
        final int partnerId = partner.getId();
        try {
            // create event object
            L2PcInstanceTradeSuccessEvent tradeEvent = new L2PcInstanceTradeSuccessEvent(System.currentTimeMillis(),
                    owner.getId(), partner.getId(), eventType);

            // convert owner items to TradeItems with ownerFlag = true
            List<ItemInfo> ownerItemInfo = new ArrayList<>(ownerIU.getItems());
            List<L2PcInstanceTradeItem> ownerItemList = ownerItemInfo.stream()
                    .map(itemInfo -> new L2PcInstanceTradeItem(itemInfo.getItem().getId(), true, itemInfo.getCount()))
                    .toList();
            // convert partner items to TradeItems with ownerFlag = false - owner does not own these items
            List<ItemInfo> partnerItemInfo = new ArrayList<>(partnerIU.getItems());
            List<L2PcInstanceTradeItem> partnerItemList = partnerItemInfo.stream()
                    .map(itemInfo -> new L2PcInstanceTradeItem(itemInfo.getItem().getId(), false, itemInfo.getCount()))
                    .toList();

            List<L2PcInstanceItem> mergedItemList = new ArrayList<>(ownerItemList);
            mergedItemList.addAll(partnerItemList);

            // final items for asyncLog
            List<L2PcInstanceItem> snapshot = List.copyOf(mergedItemList);

            asyncLog(() -> {
                // creating event and items for owner asynchronously
                try {
                    DAOFactory.getInstance().getInstanceEventDAO().insert(tradeEvent, snapshot);
                } catch (Exception ex) {
                    LOG.error("Async log failed for trade event: owner = {}, partner = {}", ownerId, partnerId, ex);
                }
            });


        }  catch (Exception ex) {
            LOG.error("Error preparing mail trade log event", ex);
            return false;
        }

        return true;
    }

    /**
     * Takes trade data and converts it to L2PcInstanceTradeSuccessEvent and L2PcInstanceTradeItem.
     * Used for MAIL type
     * @param ownerId - id of player, who owns items, exchanges them and receives adena in return. Logging starts on his behalf.
     * @param partnerId - id of player, who receives items and gives adena in return.
     * @param ownerIU - object of owner's inventory change.
     * @param adenaAmount - how much adena receiver pays.
     * @param eventType - enum type of exchange event (mail).
     */

    public boolean logL2PcInstanceTradeEvent(int ownerId, int partnerId, InventoryUpdate ownerIU, long adenaAmount,
                                             EventType eventType) {

        try {
            List<ItemInfo> ownerItemInfo = new ArrayList<>(ownerIU.getItems());
            // sender owns items, so items get ownerFlag = true
            List<L2PcInstanceTradeItem> ownerItemList1 = ownerItemInfo.stream()
                    .map(itemInfo -> new L2PcInstanceTradeItem(itemInfo.getItem().getId(), true, itemInfo.getCount()))
                    .toList();

            L2PcInstanceTradeItem partnerAdena1 = new L2PcInstanceTradeItem(ADENA_ID, false, adenaAmount);
            List<L2PcInstanceItem> mergedItemList1 = new ArrayList<>(ownerItemList1);
            mergedItemList1.add(partnerAdena1);
            List<L2PcInstanceItem> snapshot1 = List.copyOf(mergedItemList1);

            // reuse ownerItemInfo list elements, create items for receiver with ownerFlag = false. Receiver owns only adena
            List<L2PcInstanceTradeItem> ownerItemList2 = ownerItemInfo.stream()
                    .map(itemInfo -> new L2PcInstanceTradeItem(itemInfo.getItem().getId(), false, itemInfo.getCount()))
                    .toList();
            L2PcInstanceTradeItem partnerAdena2 = new L2PcInstanceTradeItem(ADENA_ID, true, adenaAmount);
            List<L2PcInstanceItem> mergedItemList2 = new ArrayList<>(ownerItemList2);
            mergedItemList2.add(partnerAdena2);
            List<L2PcInstanceItem> snapshot2 = List.copyOf(mergedItemList2);

            asyncLog(() -> {
                try {
                    // creating event and items for owner side (sender) asynchronously
                    L2PcInstanceTradeSuccessEvent tradePostEvent1 = new L2PcInstanceTradeSuccessEvent(
                            System.currentTimeMillis(), ownerId, partnerId, eventType
                    );
                    DAOFactory.getInstance().getInstanceEventDAO().insert(tradePostEvent1, snapshot1);

                    // creating event and items for partner side (receiver) asynchronously
                    L2PcInstanceTradeSuccessEvent tradePostEvent2 = new L2PcInstanceTradeSuccessEvent(
                            System.currentTimeMillis(), ownerId, partnerId, eventType
                    );
                    DAOFactory.getInstance().getInstanceEventDAO().insert(tradePostEvent2, snapshot2);
                } catch (Exception ex) {
                    LOG.error("Async log failed for mail trade event: owner = {}, partner = {}", ownerId, partnerId, ex);
                }
            });

        } catch (Exception ex) {
            LOG.error("Error preparing mail trade log event", ex);
            return false;
        }

        return true;
    }

}
