/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.mapping.mappers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicewebservices.dto.C360CartEntryWsDTO;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import ma.glasnost.orika.MappingContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * Test suite for {@link OrderEntryDataMapperTest}
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class OrderEntryDataMapperTest {
    private static final String FORMATTED_BASE_PRICE = "$20.99";
    private static final String FORMATTED_TOTAL_PRICE = "$18.99";
    private static final String PRODUCT_CODE = "testCode";
    @Mock
    private MappingContext context;
    @InjectMocks
    OrderEntryDataMapper classUnderTest;
    private AutoCloseable closeable;
    private final OrderEntryData orderEntryData = new OrderEntryData();
    private final C360CartEntryWsDTO c360CartEntryWsDTO = new C360CartEntryWsDTO();
    private final ProductData productData = new ProductData();
    private final PriceData totalPrice = new PriceData();
    private final PriceData basePrice = new PriceData();

    @Before
    public void setUp()
    {
        closeable = MockitoAnnotations.openMocks(this);
        orderEntryData.setProduct(productData);
        orderEntryData.setBasePrice(basePrice);
        orderEntryData.setTotalPrice(totalPrice);
        productData.setCode(PRODUCT_CODE);
        totalPrice.setFormattedValue(FORMATTED_TOTAL_PRICE);
        basePrice.setFormattedValue(FORMATTED_BASE_PRICE);
    }

    @Test
    public void testMapAtoB()
    {
        classUnderTest.mapAtoB(orderEntryData, c360CartEntryWsDTO, context);
        assertEquals(PRODUCT_CODE, c360CartEntryWsDTO.getProductCode());
        assertEquals(FORMATTED_TOTAL_PRICE, c360CartEntryWsDTO.getTotalPrice());
        assertEquals(FORMATTED_BASE_PRICE, c360CartEntryWsDTO.getBasePrice());
    }

    @After
    public void tearDown() throws Exception
    {
        closeable.close();
    }
}
