/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.mapper;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.customer360.GeneralActivityData;
import de.hybris.platform.assistedservicewebservices.dto.C360ActivityWsDTO;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.webservicescommons.mapping.FieldSelectionStrategy;
import ma.glasnost.orika.MappingContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class C360ActivityDataMapperTest {

    @Spy
    private GeneralActivityData generalActivityData;
    @Spy
    private C360ActivityWsDTO c360ActivityWsDTO;
    @Mock
    private Date createdTime;
    @Mock
    private Date updatedTime;
    @Mock
    private MappingContext mappingContext;
    @Mock
    private FieldSelectionStrategy fieldSelectionStrategy;
    @Spy
    private OrderFacade orderFacade;
    @Spy
    private OrderData orderData;
    @Spy
    private L10NService l10NService;
    @InjectMocks
    private C360ActivityDataMapper c360ActivityDataMapper;

    private static final String ORDER = "text.customer360.activity.general.order";
    private static final String CART = "text.customer360.activity.general.cart";
    private static final String TICKET = "text.customer360.activity.general.ticket";
    private static final String SAVED_CART = "text.customer360.activity.general.savedcart";
    private static final String DESCRIPTION = "This is the description";
    private static final Object[] DESCRIPTION_ARGS = {"1", "2", "3"};

    @Before
    public void setUp()
    {
        when(fieldSelectionStrategy.shouldMap(generalActivityData, c360ActivityWsDTO, mappingContext)).thenReturn(true);
        generalActivityData.setCreated(createdTime);
        generalActivityData.setUpdated(updatedTime);
    }

    @Test
    public void testMapGeneralActivityDataWithOrder() {
        final String statusName = "Fraud Checked";
        final String statusCode = "FRAUD_CHECKED";
        final String typeName = "Order";
        final String typeCode = "ORDER";
        final String orderId = "00000001";
        generalActivityData.setType(ORDER);
        generalActivityData.setStatus(statusName);
        generalActivityData.setId(orderId);
        orderData.setStatus(OrderStatus.FRAUD_CHECKED);
        generalActivityData.setDescriptionArgs(DESCRIPTION_ARGS);
        when(l10NService.getLocalizedString(anyString(),eq(DESCRIPTION_ARGS))).thenReturn(DESCRIPTION);
        when(l10NService.getLocalizedString(ORDER)).thenReturn(typeName);
        when(orderFacade.getOrderDetailsForCode(orderId)).thenReturn(orderData);


        c360ActivityDataMapper.mapAtoB(generalActivityData, c360ActivityWsDTO, mappingContext);

        Assert.assertEquals(createdTime, c360ActivityWsDTO.getCreatedAt());
        Assert.assertEquals(updatedTime, c360ActivityWsDTO.getUpdatedAt());
        Assert.assertEquals(DESCRIPTION, c360ActivityWsDTO.getDescription());
        Assert.assertEquals(statusCode, c360ActivityWsDTO.getStatus().getCode());
        Assert.assertEquals(statusName, c360ActivityWsDTO.getStatus().getName());
        Assert.assertEquals(typeCode, c360ActivityWsDTO.getType().getCode());
        Assert.assertEquals(typeName, c360ActivityWsDTO.getType().getName());
        Assert.assertEquals(orderId, c360ActivityWsDTO.getAssociatedTypeId());
    }

    @Test
    public void testMapGeneralActivityDataWithCart() {
        final String typeName = "Cart";
        final String typeCode = "CART";
        final String cartId = "cartId";
        generalActivityData.setType(CART);
        generalActivityData.setId(cartId);
        when(generalActivityData.getDescriptionArgs()).thenReturn(DESCRIPTION_ARGS);
        when(fieldSelectionStrategy.shouldMap(generalActivityData, c360ActivityWsDTO, mappingContext)).thenReturn(true);
        when(l10NService.getLocalizedString(anyString(),eq(DESCRIPTION_ARGS))).thenReturn(DESCRIPTION);
        when(l10NService.getLocalizedString(CART)).thenReturn(typeName);


        c360ActivityDataMapper.mapAtoB(generalActivityData, c360ActivityWsDTO, mappingContext);

        Assert.assertEquals(createdTime, c360ActivityWsDTO.getCreatedAt());
        Assert.assertEquals(updatedTime, c360ActivityWsDTO.getUpdatedAt());
        Assert.assertEquals(DESCRIPTION, c360ActivityWsDTO.getDescription());
        Assert.assertNull(c360ActivityWsDTO.getStatus());
        Assert.assertEquals(typeCode, c360ActivityWsDTO.getType().getCode());
        Assert.assertEquals(typeName, c360ActivityWsDTO.getType().getName());
        Assert.assertEquals(cartId, c360ActivityWsDTO.getAssociatedTypeId());
    }

    @Test
    public void testMapGeneralActivityDataWithSavedCart() {
        final String typeName = "Saved Cart";
        final String typeCode = "SAVED CART";
        final String savedCartId = "savedCartId";
        generalActivityData.setType(SAVED_CART);
        generalActivityData.setDescription(DESCRIPTION);
        generalActivityData.setId(savedCartId);
        when(l10NService.getLocalizedString(SAVED_CART)).thenReturn(typeName);

        c360ActivityDataMapper.mapAtoB(generalActivityData, c360ActivityWsDTO, mappingContext);

        Assert.assertEquals(createdTime, c360ActivityWsDTO.getCreatedAt());
        Assert.assertEquals(updatedTime, c360ActivityWsDTO.getUpdatedAt());
        Assert.assertEquals(DESCRIPTION, c360ActivityWsDTO.getDescription());
        Assert.assertNull(c360ActivityWsDTO.getStatus());
        Assert.assertEquals(typeCode, c360ActivityWsDTO.getType().getCode());
        Assert.assertEquals(typeName, c360ActivityWsDTO.getType().getName());
        Assert.assertEquals(savedCartId, c360ActivityWsDTO.getAssociatedTypeId());
    }

    @Test
    public void testMapGeneralActivityDataWithTicket() {
        final String statusName = "New";
        final String statusCode = "New";
        final String typeName = "Ticket";
        final String typeCode = "TICKET";
        final String StatusI18nKey = "text.customer360.activity.general.status.new";
        final String ticketId = "ticketId";
        generalActivityData.setType(TICKET);
        generalActivityData.setStatus(statusName);
        generalActivityData.setDescription(DESCRIPTION);
        generalActivityData.setId(ticketId);
        when(l10NService.getLocalizedString(TICKET)).thenReturn(typeName);
        when(l10NService.getLocalizedString(StatusI18nKey)).thenReturn(statusName);

        c360ActivityDataMapper.mapAtoB(generalActivityData, c360ActivityWsDTO, mappingContext);

        Assert.assertEquals(createdTime, c360ActivityWsDTO.getCreatedAt());
        Assert.assertEquals(updatedTime, c360ActivityWsDTO.getUpdatedAt());
        Assert.assertEquals(DESCRIPTION, c360ActivityWsDTO.getDescription());
        Assert.assertEquals(statusCode, c360ActivityWsDTO.getStatus().getCode());
        Assert.assertEquals(statusName, c360ActivityWsDTO.getStatus().getName());
        Assert.assertEquals(typeCode, c360ActivityWsDTO.getType().getCode());
        Assert.assertEquals(typeName, c360ActivityWsDTO.getType().getName());
        Assert.assertEquals(ticketId, c360ActivityWsDTO.getAssociatedTypeId());
    }
}
