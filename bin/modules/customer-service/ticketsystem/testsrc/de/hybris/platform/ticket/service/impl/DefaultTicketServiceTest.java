/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.ticket.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.ticket.resolver.TicketAssociatedObjectResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultTicketServiceTest {
    @Mock
    Map<String, TicketAssociatedObjectResolver> map;
    @Mock
    TicketAssociatedObjectResolver ticketAssociatedObjectResolver;
    @Spy
    CartModel savedCart;
    @Spy
    CartModel cart;
    @Mock
    Date date;
    @InjectMocks
    DefaultTicketService ticketService;

    private final String SAVED_CART_ASSOCIATED_CODE = "Saved Cart=00000000";
    private final String SAVED_CART_ASSOCIATED_CODE_WITHOUT_BLANK = "SavedCart=00000000";
    private final String CART_ASSOCIATED_CODE = "Cart=00000000";

    @Before
    public void setUp() {
        savedCart.setSaveTime(date);
        when(map.containsKey(anyString())).thenReturn(true);
        when(map.get(anyString())).thenReturn(ticketAssociatedObjectResolver);
    }

    @Test
    public void testGetAssociatedObjectWhenTypeIsSavedCartAndCodeIsCart() {
        when(ticketAssociatedObjectResolver.getObject(any(), any(), any())).thenReturn(cart);
        assertThatThrownBy(() -> {
            ticketService.getAssociatedObject(SAVED_CART_ASSOCIATED_CODE, null, null);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testGetAssociatedObjectWhenTypeIsSavedCartAndCodeIsSavedCart() {
        when(ticketAssociatedObjectResolver.getObject(any(), any(), any())).thenReturn(savedCart);
        final AbstractOrderModel associatedObject = ticketService.getAssociatedObject(SAVED_CART_ASSOCIATED_CODE, null, null);
        assertThat(associatedObject).isEqualTo(savedCart);
    }

    @Test
    public void testGetAssociatedObjectWhenTypeIsSavedCartWithoutBlankAndCodeIsSavedCart() {
        when(ticketAssociatedObjectResolver.getObject(any(), any(), any())).thenReturn(savedCart);
        final AbstractOrderModel associatedObject = ticketService.getAssociatedObject(SAVED_CART_ASSOCIATED_CODE_WITHOUT_BLANK, null, null);
        assertThat(associatedObject).isEqualTo(savedCart);
    }
    @Test
    public void testGetAssociatedObjectWhenTypeIsCartAndCodeIsSavedCart() {
        when(ticketAssociatedObjectResolver.getObject(any(), any(), any())).thenReturn(savedCart);
        final AbstractOrderModel associatedObject = ticketService.getAssociatedObject(CART_ASSOCIATED_CODE, null, null);
        assertThat(associatedObject).isEqualTo(savedCart);
    }

    @Test
    public void testGetAssociatedObjectWhenTypeIsCartAndCodeIsCart() {
        when(ticketAssociatedObjectResolver.getObject(any(), any(), any())).thenReturn(cart);
        final AbstractOrderModel associatedObject = ticketService.getAssociatedObject(CART_ASSOCIATED_CODE, null, null);
        assertThat(associatedObject).isEqualTo(cart);
    }
}
