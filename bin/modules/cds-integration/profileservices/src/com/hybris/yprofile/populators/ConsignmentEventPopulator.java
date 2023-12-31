/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.populators;

import com.hybris.yprofile.common.Utils;
import com.hybris.yprofile.dto.Order;
import com.hybris.yprofile.dto.OrderBody;
import com.hybris.yprofile.dto.Address;
import com.hybris.yprofile.dto.OrderLineItem;
import com.hybris.yprofile.dto.ShipmentInfo;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

public class ConsignmentEventPopulator implements Populator<ConsignmentModel, Order> {

    private static final String ORDER_SHIPPED_EVENT_TYPE ="order shipment";
    private static final String ORDER_STATUS_COMPLETED = "completed";
    private static final String ORDER_STATUS_PARTIAL_DELIVERED = "partial delivered";

    private Converter<AddressModel, Address> profileAddressConverter;
    private Converter<AbstractOrderEntryModel, OrderLineItem> profileOrderLineItemConverter;

    @Override
    public void populate(ConsignmentModel consignmentModel, Order order) {

        final OrderModel orderModel = (OrderModel) consignmentModel.getOrder();

        order.setChannelRef(orderModel.getSite().getUid());
        order.setType(ORDER_SHIPPED_EVENT_TYPE);
        order.setDate(Utils.formatDate(consignmentModel.getCreationtime()));

        final OrderBody orderBody = new OrderBody();

        final List<OrderLineItem> lineItems = new ArrayList<>();
        consignmentModel.getConsignmentEntries().stream().forEach(consignmentEntry -> {
                    final OrderLineItem lineItem = getProfileOrderLineItemConverter().convert(consignmentEntry.getOrderEntry());
                    lineItem.setStatus(consignmentModel.getStatus().getCode());
                    lineItem.setQuantity(consignmentEntry.getQuantity());
                    lineItems.add(lineItem);
                }
        );

        orderBody.setLineItems(lineItems);
        orderBody.setStatus(getOrderStatus(orderModel));
        orderBody.setDate(Utils.formatDate(consignmentModel.getCreationtime()));
        orderBody.setOrderId(orderModel.getCode());
        orderBody.setShipmentInfo(getShipmentInfo(consignmentModel));

        order.setBody(orderBody);
    }

    protected ShipmentInfo getShipmentInfo(ConsignmentModel consignmentModel){

        final ShipmentInfo shipmentInfo = new ShipmentInfo();
        shipmentInfo.setAddress(getProfileAddressConverter().convert(consignmentModel.getShippingAddress()));
        shipmentInfo.setStatus(consignmentModel.getStatus().getCode());
        shipmentInfo.setCarrier(consignmentModel.getCarrier());
        shipmentInfo.setTrackingRef(consignmentModel.getTrackingID());

        return shipmentInfo;
    }

    protected String getOrderStatus(OrderModel orderModel) {
        for (final ConsignmentModel consignment : orderModel.getConsignments()) {
            if (!consignment.getStatus().equals(ConsignmentStatus.SHIPPED) && !consignment.getStatus().equals(ConsignmentStatus.PICKUP_COMPLETE)) {
                return ORDER_STATUS_PARTIAL_DELIVERED;
            }

        }
        return ORDER_STATUS_COMPLETED;
    }

    public Converter<AddressModel, Address> getProfileAddressConverter() {
        return profileAddressConverter;
    }

    public void setProfileAddressConverter(Converter<AddressModel, Address> profileAddressConverter) {
        this.profileAddressConverter = profileAddressConverter;
    }

    public Converter<AbstractOrderEntryModel, OrderLineItem> getProfileOrderLineItemConverter() {
        return profileOrderLineItemConverter;
    }

    public void setProfileOrderLineItemConverter(Converter<AbstractOrderEntryModel, OrderLineItem> profileOrderLineItemConverter) {
        this.profileOrderLineItemConverter = profileOrderLineItemConverter;
    }
}
