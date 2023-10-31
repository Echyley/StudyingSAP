/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicewebservices.dto.C360Cart;
import de.hybris.platform.assistedservicewebservices.dto.C360CartDataWsDTO;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class C360CartAdapterTest {
    @Mock
    private DataMapper dataMapper;

    private C360CartAdapter c360CartAdapter;

    @Before
    public void setUp()
    {
        c360CartAdapter = new C360CartAdapter();
        c360CartAdapter.setDataMapper(dataMapper);
    }

    @Test
    public void testC360CartAdapterNotEmptyCart()
    {
        final CartData cartData = new CartData();
        cartData.setCode("test");
        cartData.setGuid("test");


        final C360CartDataWsDTO c360CartDataWsDTO = new C360CartDataWsDTO();

        when(dataMapper.map(cartData, C360CartDataWsDTO.class))
                .thenReturn(c360CartDataWsDTO);

        final C360Cart c360cart = (C360Cart) c360CartAdapter.adapt(cartData);
        verify(dataMapper).map(cartData, C360CartDataWsDTO.class);
        assertThat(c360cart).isNotNull();
        assertThat(c360cart.getCart()).isSameAs(c360CartDataWsDTO);
    }

    @Test
    public void testC360CartAdapterEmptyCart()
    {
        final CartData cartData = new CartData();

        final C360Cart c360cart = (C360Cart) c360CartAdapter.adapt(cartData);
        assertThat(c360cart).isNotNull();
        assertThat(c360cart.getCart()).isNull();
    }

    @Test
    public void testC360CartAdapterWithNull()
    {
        final C360Cart c360cart = (C360Cart) c360CartAdapter.adapt(null);
        assertThat(c360cart).isNotNull();
        assertThat(c360cart.getCart()).isNull();
    }
}
