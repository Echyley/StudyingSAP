/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.mapping.mappers;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicewebservices.dto.C360CartDataWsDTO;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import ma.glasnost.orika.MappingContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test suite for {@link CartDataMapperTest}
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class CartDataMapperTest {
    private static final Integer UNIT_COUNT = 10;
    private static final String FORMATTED_PRICE = "$20.99";
    @Mock
    private MappingContext context;
    @InjectMocks
    CartDataMapper classUnderTest;
    private AutoCloseable closeable;
    private final CartData cartData = new CartData();
    private final C360CartDataWsDTO c360CartData = new C360CartDataWsDTO();
    private final PriceData priceData = new PriceData();

    @Before
    public void setUp()
    {
        closeable = MockitoAnnotations.openMocks(this);
        cartData.setTotalPrice(priceData);
        priceData.setFormattedValue(FORMATTED_PRICE);
        cartData.setTotalUnitCount(UNIT_COUNT);
    }

    @Test
    public void testMapAtoB()
    {
        classUnderTest.mapAtoB(cartData, c360CartData, context);
        assertEquals(UNIT_COUNT, c360CartData.getTotalItemCount());
        assertEquals(FORMATTED_PRICE, c360CartData.getTotalPrice());
    }

    @After
    public void tearDown() throws Exception
    {
        closeable.close();
    }
}
