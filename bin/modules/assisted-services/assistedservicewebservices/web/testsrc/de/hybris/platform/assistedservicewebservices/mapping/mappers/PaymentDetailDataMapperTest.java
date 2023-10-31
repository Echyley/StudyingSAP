/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.mapping.mappers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicewebservices.dto.C360PaymentDetailWsDTO;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
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
import static org.junit.Assert.assertTrue;

/**
 * Test suite for {@link PaymentDetailDataMapperTest}
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class PaymentDetailDataMapperTest {
    @Mock
    private MappingContext context;
    @InjectMocks
    PaymentDetailDataMapper paymentDetailDataMapper;

    private final CCPaymentInfoData paymentDetailData = new CCPaymentInfoData();
    private final C360PaymentDetailWsDTO c360PaymentDetailWsDTO = new C360PaymentDetailWsDTO();

    private final CardTypeData cardTypeData = new CardTypeData();

    private AutoCloseable closeable;

    @Before
    public void setUp()
    {
        closeable = MockitoAnnotations.openMocks(this);
        cardTypeData.setCode("visa");
        cardTypeData.setName("Visa");
        paymentDetailData.setCardTypeData(cardTypeData);
        paymentDetailData.setDefaultPaymentInfo(true);
    }

    @Test
    public void testMapAtoB()
    {
        paymentDetailDataMapper.mapAtoB(paymentDetailData, c360PaymentDetailWsDTO, context);
        assertEquals("visa", c360PaymentDetailWsDTO.getCardType().getCode());
        assertEquals("Visa",  c360PaymentDetailWsDTO.getCardType().getName());
        assertTrue( c360PaymentDetailWsDTO.isDefaultPayment());
    }

    @After
    public void tearDown() throws Exception
    {
        closeable.close();
    }
}
