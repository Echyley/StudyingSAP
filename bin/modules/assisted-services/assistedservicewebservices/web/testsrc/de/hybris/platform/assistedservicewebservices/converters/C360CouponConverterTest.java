/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.converters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicepromotionfacades.constants.AssistedservicepromotionfacadesConstants;
import de.hybris.platform.assistedservicepromotionfacades.customer360.CSACouponData;
import de.hybris.platform.assistedservicewebservices.dto.C360CouponWsDTO;
import de.hybris.platform.util.Config;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class C360CouponConverterTest {
    private C360CouponConverter c360CouponConverter;

    @Before
    public void setUp()
    {
        try (MockedStatic<Config> config = Mockito.mockStatic(Config.class)) {
            config.when(() -> Config.getString(AssistedservicepromotionfacadesConstants.COUPON_SEARCH_PREFIX_KEY,
                    AssistedservicepromotionfacadesConstants.COUPON_SEARCH_PREFIX)).thenReturn(AssistedservicepromotionfacadesConstants.COUPON_SEARCH_PREFIX);
            assertThat(Config.getString(AssistedservicepromotionfacadesConstants.COUPON_SEARCH_PREFIX_KEY,
                    AssistedservicepromotionfacadesConstants.COUPON_SEARCH_PREFIX)).isEqualTo(AssistedservicepromotionfacadesConstants.COUPON_SEARCH_PREFIX);
            c360CouponConverter = new C360CouponConverter();
        }

    }

    @Test
    public void testC360CouponConverterTo()
    {
        final CSACouponData csaCouponData = new CSACouponData();
        csaCouponData.setCode("test");

        C360CouponWsDTO c360CouponWsDTO = c360CouponConverter.convertTo(csaCouponData, null, null);

        Assert.assertEquals("csa_coupon_test", c360CouponWsDTO.getCode());
        Assert.assertFalse(c360CouponWsDTO.isApplied());

    }

    @Test
    public void testC360CouponConverterFrom()
    {
        final C360CouponWsDTO c360CouponWsDTO = new C360CouponWsDTO();
        c360CouponWsDTO.setCode("csa_coupon_test");

        CSACouponData csaCouponData = c360CouponConverter.convertFrom(c360CouponWsDTO, null, null);

        Assert.assertEquals("test", csaCouponData.getCode());
        Assert.assertEquals(Boolean.FALSE, csaCouponData.getCouponApplied());
    }
}
