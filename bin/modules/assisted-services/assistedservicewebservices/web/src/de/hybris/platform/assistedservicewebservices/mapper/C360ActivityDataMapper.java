/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.mapper;

import de.hybris.platform.assistedservicefacades.customer360.GeneralActivityData;
import de.hybris.platform.assistedservicewebservices.dto.C360ActivityStatusWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360ActivityTypeWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360ActivityWsDTO;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;
import ma.glasnost.orika.MappingContext;

import javax.annotation.Resource;
import java.util.Locale;

public class C360ActivityDataMapper extends AbstractCustomMapper<GeneralActivityData, C360ActivityWsDTO> {
    @Resource
    private OrderFacade orderFacade;
    @Resource
    private L10NService l10NService;
    private static final String CREATED = "created";
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED = "updated";
    private static final String UPDATED_AT = "updatedAt";
    private static final String DESCRIPTION = "description";
    private static final String ID = "id";
    private static final String ASSOCIATED_TYPE_ID = "associatedTypeId";
    private static final String ORDER = "text.customer360.activity.general.order";
    private static final String CART = "text.customer360.activity.general.cart";
    private static final String SAVED_CART = "text.customer360.activity.general.savedcart";
    private static final String TICKET = "text.customer360.activity.general.ticket";
    private static final String ORDER_TYPE_CODE = "ORDER";
    private static final String CART_TYPE_CODE = "CART";
    private static final String SAVED_CART_TYPE_CODE = "SAVED CART";
    private static final String TICKET_TYPE_CODE = "TICKET";
    private static final String CART_DESCRIPTION = "text.customer360.activity.general.cart.description";
    private static final String ORDER_DESCRIPTION = "text.customer360.activity.general.order.description";
    private static final String ACTIVITY_STATUS_KEY_PREFIX = "text.customer360.activity.general.status.";

    @Override
    public void mapAtoB(final GeneralActivityData generalActivityData, final C360ActivityWsDTO c360ActivityWsDTO, final MappingContext context) {
        mapAToBAssociatedTypeId(generalActivityData, c360ActivityWsDTO, context);
        mapAToBCreatedAt(generalActivityData, c360ActivityWsDTO, context);
        mapAToBUpdatedAt(generalActivityData, c360ActivityWsDTO, context);
        mapAToBDescriptionStatusType(generalActivityData, c360ActivityWsDTO, context);
    }

    private void mapAToBAssociatedTypeId(final GeneralActivityData generalActivityData, final C360ActivityWsDTO c360ActivityWsDTO, final MappingContext context) {
        context.beginMappingField(ID, getAType(), generalActivityData, ASSOCIATED_TYPE_ID, getBType(), c360ActivityWsDTO);
        try {
            if (shouldMap(generalActivityData, c360ActivityWsDTO, context)) {
                c360ActivityWsDTO.setAssociatedTypeId(generalActivityData.getId());
            }
        } finally {
            context.endMappingField();
        }
    }

    private void mapAToBCreatedAt(final GeneralActivityData generalActivityData, final C360ActivityWsDTO c360ActivityWsDTO, final MappingContext context) {
        context.beginMappingField(CREATED, getAType(), generalActivityData, CREATED_AT, getBType(), c360ActivityWsDTO);
        try {
            if (shouldMap(generalActivityData, c360ActivityWsDTO, context)) {
                c360ActivityWsDTO.setCreatedAt(generalActivityData.getCreated());
            }
        } finally {
            context.endMappingField();
        }
    }

    private void mapAToBUpdatedAt(final GeneralActivityData generalActivityData, final C360ActivityWsDTO c360ActivityWsDTO, final MappingContext context) {
        context.beginMappingField(UPDATED, getAType(), generalActivityData, UPDATED_AT, getBType(), c360ActivityWsDTO);
        try {
            if (shouldMap(generalActivityData, c360ActivityWsDTO, context)) {
                c360ActivityWsDTO.setUpdatedAt(generalActivityData.getUpdated());
            }
        } finally {
            context.endMappingField();
        }
    }

    private void mapAToBDescriptionStatusType(final GeneralActivityData generalActivityData, final C360ActivityWsDTO c360ActivityWsDTO, final MappingContext context) {
        context.beginMappingField(DESCRIPTION, getAType(), generalActivityData, DESCRIPTION, getBType(), c360ActivityWsDTO);
        try {
            if (shouldMap(generalActivityData, c360ActivityWsDTO, context)) {
                final String activityType = generalActivityData.getType();
                switch (activityType) {
                    case ORDER:
                        composeDescription(ORDER_DESCRIPTION, generalActivityData, c360ActivityWsDTO);
                        mapType(c360ActivityWsDTO, ORDER_TYPE_CODE, ORDER);
                        mapStatus(generalActivityData, c360ActivityWsDTO);
                        break;
                    case CART:
                        composeDescription(CART_DESCRIPTION, generalActivityData, c360ActivityWsDTO);
                        mapType(c360ActivityWsDTO, CART_TYPE_CODE, CART);
                        break;
                    case SAVED_CART:
                        c360ActivityWsDTO.setDescription(generalActivityData.getDescription());
                        mapType(c360ActivityWsDTO, SAVED_CART_TYPE_CODE, SAVED_CART);
                        break;
                    case TICKET:
                        c360ActivityWsDTO.setDescription(generalActivityData.getDescription());
                        mapType(c360ActivityWsDTO, TICKET_TYPE_CODE, TICKET);
                        mapStatus(generalActivityData, c360ActivityWsDTO);
                        break;
                    default:
                        break;
                }
            }
        } finally {
            context.endMappingField();
        }
    }

    private void composeDescription(final String description, final GeneralActivityData generalActivityData, final C360ActivityWsDTO c360ActivityWsDTO) {
        final Object[] descriptionArgs = generalActivityData.getDescriptionArgs();
        final String activityDescription = getL10NService().getLocalizedString(description, descriptionArgs);
        c360ActivityWsDTO.setDescription(activityDescription);
    }

    private void mapType(final C360ActivityWsDTO c360ActivityWsDTO, final String activityTypeCode, final String typeKey){
        final C360ActivityTypeWsDTO type = new C360ActivityTypeWsDTO();
        type.setCode(activityTypeCode);
        type.setName(getL10NService().getLocalizedString(typeKey));
        c360ActivityWsDTO.setType(type);
    }

    private void mapStatus(final GeneralActivityData generalActivityData, final C360ActivityWsDTO c360ActivityWsDTO) {
        final C360ActivityStatusWsDTO status = new C360ActivityStatusWsDTO();
        String activityStatus = generalActivityData.getStatus();
        if (generalActivityData.getType().equals(ORDER)) {
            final String orderId = generalActivityData.getId();
            final OrderData order = getOrderFacade().getOrderDetailsForCode(orderId);
            status.setCode(order.getStatus().getCode());
            status.setName(activityStatus);
        }
        else {
            final String statusKey = ACTIVITY_STATUS_KEY_PREFIX + activityStatus;
            final String activityStatusName = getL10NService().getLocalizedString(statusKey.toLowerCase(Locale.ENGLISH));
            status.setCode(activityStatus);
            status.setName(activityStatusName);
        }
        c360ActivityWsDTO.setStatus(status);
    }

    public OrderFacade getOrderFacade() {
        return orderFacade;
    }

    public void setOrderFacade(OrderFacade orderFacade) {
        this.orderFacade = orderFacade;
    }


    public L10NService getL10NService() {
        return l10NService;
    }

    public void setL10NService(L10NService l10NService) {
        this.l10NService = l10NService;
    }

}
