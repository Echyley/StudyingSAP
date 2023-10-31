/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.populators;

import com.hybris.yprofile.common.Utils;
import com.hybris.yprofile.dto.Address;
import com.hybris.yprofile.dto.OrderLineItem;
import com.hybris.yprofile.dto.Order;
import com.hybris.yprofile.dto.OrderBody;
import com.hybris.yprofile.dto.ShipmentInfo;
import com.hybris.yprofile.dto.Consumer;
import com.hybris.yprofile.dto.Promotion;
import com.hybris.yprofile.dto.PaymentInfo;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.impl.DefaultSessionTokenService;

import java.util.ArrayList;
import java.util.List;

public class OrderEventPopulator implements Populator<OrderModel, Order> {

    private static final String NEW_ORDER_EVENT_TYPE = "order";
    private static final String NEW_ORDER_STATUS = "new";
    private static final String NOT_DELIVERED_STATUS = "not delivered";

    private Converter<AddressModel, Address> profileAddressConverter;
    private Converter<UserModel, Consumer> profileConsumerConverter;
    private Converter<AbstractOrderEntryModel, OrderLineItem> profileOrderLineItemConverter;
    private DefaultSessionTokenService defaultSessionTokenService;

    @Override
    public void populate(OrderModel orderModel, Order order) {

        order.setChannelRef(orderModel.getSite().getUid());
        order.setType(NEW_ORDER_EVENT_TYPE);
        order.setConsumer(getProfileConsumerConverter().convert(orderModel.getUser()));
        order.setBody(getOrderBody(orderModel));
        order.setSessionId(getDefaultSessionTokenService().getOrCreateSessionToken());
        order.setDate(Utils.formatDate(orderModel.getCreationtime()));
    }

    protected OrderBody getOrderBody(OrderModel orderModel){
        final OrderBody orderBody = new OrderBody();
        orderBody.setOrderId(orderModel.getCode());
        orderBody.setCartId(orderModel.getCartIdReference());
        orderBody.setDate(Utils.formatDate(orderModel.getCreationtime()));
        orderBody.setOrderValue(orderModel.getTotalPrice());
        orderBody.setCurrency(orderModel.getCurrency().getIsocode());

        orderBody.setDeliveryCost(orderModel.getDeliveryCost());
        orderBody.setTotalDiscounts(orderModel.getTotalDiscounts());

        orderBody.setStatus(orderModel.getStatus()!= null ? orderModel.getStatus().getCode(): NEW_ORDER_STATUS);

        final List<Promotion> promotions = new ArrayList<>();
        orderModel.getAllPromotionResults().forEach(
                promotionResultModel ->
                        promotions.add(getPromotion(promotionResultModel.getPromotion()))
        );
        orderBody.setPromotionInfo(promotions);

        orderBody.setPaymentInfo(getPaymentInfo(orderModel));

        orderBody.setShipmentInfo(getShipmentInfo(orderModel));

        final List<OrderLineItem> lineItems = new ArrayList<>();
        orderModel.getEntries().stream().forEach(
                (AbstractOrderEntryModel abstractOrderEntryModel)
                        -> lineItems.add(getProfileOrderLineItemConverter().convert(abstractOrderEntryModel))
        );

        orderBody.setLineItems(lineItems);

        return orderBody;
    }

    protected Promotion getPromotion(AbstractPromotionModel promotionResultModel){
        final Promotion promotion = new Promotion();
        promotion.setRef(promotionResultModel.getCode());
        promotion.setType(promotionResultModel.getPromotionType());

        return promotion;
    }

    protected PaymentInfo getPaymentInfo(OrderModel orderModel){
        final PaymentInfo paymentInfo = new PaymentInfo();

        final PaymentInfoModel paymentInfoModel = orderModel.getPaymentInfo();

        paymentInfo.setPaymentType(paymentInfoModel != null ? paymentInfoModel.getItemtype() : "");

        if (paymentInfoModel instanceof CreditCardPaymentInfoModel){
            paymentInfo.setPaymentType(((CreditCardPaymentInfoModel) paymentInfoModel).getType().toString());
        }

        paymentInfo.setStatus(orderModel.getPaymentStatus() != null ? orderModel.getPaymentStatus().toString() : "");
        paymentInfo.setAddress(getProfileAddressConverter().convert(orderModel.getPaymentAddress()));

        return paymentInfo;
    }

    protected ShipmentInfo getShipmentInfo(OrderModel orderModel){

        final ShipmentInfo shipmentInfo = new ShipmentInfo();
        shipmentInfo.setAddress(getProfileAddressConverter().convert(orderModel.getDeliveryAddress()));
        shipmentInfo.setStatus(orderModel.getDeliveryStatus() != null ? orderModel.getDeliveryStatus().getCode() : NOT_DELIVERED_STATUS);

        return shipmentInfo;
    }

    public Converter<UserModel, Consumer> getProfileConsumerConverter() {
        return profileConsumerConverter;
    }

    public void setProfileConsumerConverter(Converter<UserModel, Consumer> profileConsumerConverter) {
        this.profileConsumerConverter = profileConsumerConverter;
    }

    public Converter<AbstractOrderEntryModel, OrderLineItem> getProfileOrderLineItemConverter() {
        return profileOrderLineItemConverter;
    }

    public Converter<AddressModel, Address> getProfileAddressConverter() {
        return profileAddressConverter;
    }

    public void setProfileAddressConverter(Converter<AddressModel, Address> profileAddressConverter) {
        this.profileAddressConverter = profileAddressConverter;
    }

    public void setProfileOrderLineItemConverter(Converter<AbstractOrderEntryModel, OrderLineItem> profileOrderLineItemConverter) {
        this.profileOrderLineItemConverter = profileOrderLineItemConverter;
    }

    public DefaultSessionTokenService getDefaultSessionTokenService() {
        return defaultSessionTokenService;
    }

    public void setDefaultSessionTokenService(DefaultSessionTokenService defaultSessionTokenService) {
        this.defaultSessionTokenService = defaultSessionTokenService;
    }
}
