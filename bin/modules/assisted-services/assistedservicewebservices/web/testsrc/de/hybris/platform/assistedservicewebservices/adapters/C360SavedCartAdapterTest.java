/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicewebservices.dto.C360CartDataWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360SavedCart;
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
public class C360SavedCartAdapterTest {
    @Mock
    private DataMapper dataMapper;

    private C360SavedCartAdapter c360SavedCartAdapter;

    @Before
    public void setUp()
    {
        c360SavedCartAdapter = new C360SavedCartAdapter();
        c360SavedCartAdapter.setDataMapper(dataMapper);
    }

    @Test
    public void testC360SavedCartAdapter()
    {
        final CartData cartData = new CartData();
        cartData.setCode("test");


        final C360CartDataWsDTO c360CartDataWsDTO = new C360CartDataWsDTO();

        when(dataMapper.map(cartData, C360CartDataWsDTO.class))
                .thenReturn(c360CartDataWsDTO);

        final C360SavedCart c360SavedCart = (C360SavedCart) c360SavedCartAdapter.adapt(cartData);
        verify(dataMapper).map(cartData, C360CartDataWsDTO.class);
        assertThat(c360SavedCart).isNotNull();
        assertThat(c360SavedCart.getSavedCart()).isSameAs(c360CartDataWsDTO);
    }

    @Test
    public void testC360SavedCartAdapterWithNULL()
    {
        final C360SavedCart c360SavedCart = (C360SavedCart) c360SavedCartAdapter.adapt(null);
        verify(dataMapper).map(null, C360CartDataWsDTO.class);
        assertThat(c360SavedCart).isNotNull();
        assertThat(c360SavedCart.getSavedCart()).isNull();
    }
}
