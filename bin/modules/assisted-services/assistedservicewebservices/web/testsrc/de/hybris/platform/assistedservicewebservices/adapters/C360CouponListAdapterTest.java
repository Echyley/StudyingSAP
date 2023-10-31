/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicepromotionfacades.customer360.CSACouponData;
import de.hybris.platform.assistedservicewebservices.dto.C360CouponList;
import de.hybris.platform.assistedservicewebservices.dto.C360CouponWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class C360CouponListAdapterTest {
    @Mock
    private DataMapper dataMapper;

    private C360CouponListAdapter c360CouponListAdapter;

    @Before
    public void setUp()
    {
        c360CouponListAdapter = new C360CouponListAdapter();
        c360CouponListAdapter.setDataMapper(dataMapper);
    }

    @Test
    public void testC360CouponListAdapter()
    {
        final CSACouponData csaCouponData = new CSACouponData();
        csaCouponData.setCode("test");

        final C360CouponWsDTO c360CouponWsDTO = new C360CouponWsDTO();
        c360CouponWsDTO.setCode("csa_coupon_test");

        when(dataMapper.map(csaCouponData, C360CouponWsDTO.class))
                .thenReturn(c360CouponWsDTO);

        final C360CouponList c360CouponList = (C360CouponList) c360CouponListAdapter.adapt(Arrays.asList(csaCouponData));
        verify(dataMapper).map(csaCouponData, C360CouponWsDTO.class);
        assertThat(c360CouponList).isNotNull();
        assertThat(c360CouponList.getCoupons()
                .get(0)).isSameAs(c360CouponWsDTO);
    }

    @Test
    public void testC360CouponListAdapterWithNull()
    {
        final C360CouponList c360CouponList = (C360CouponList) c360CouponListAdapter.adapt(null);
        assertThat(c360CouponList).isNotNull();
        assertThat(c360CouponList.getCoupons()).isEmpty();
    }
}
