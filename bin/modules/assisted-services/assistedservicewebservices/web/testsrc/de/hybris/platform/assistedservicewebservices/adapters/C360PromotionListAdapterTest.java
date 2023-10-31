/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicepromotionfacades.customer360.CSAPromoData;
import de.hybris.platform.assistedservicewebservices.dto.C360PromoWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360PromotionList;
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
public class C360PromotionListAdapterTest {
    @Mock
    private DataMapper dataMapper;

    private C360PromotionListAdapter c360PromotionListAdapter;

    @Before
    public void setUp()
    {
        c360PromotionListAdapter = new C360PromotionListAdapter();
        c360PromotionListAdapter.setDataMapper(dataMapper);
    }

    @Test
    public void testC360PromotionListAdapter()
    {
        final CSAPromoData csaPromoData = new CSAPromoData();
        csaPromoData.setCode("csa_discount_test");

        final C360PromoWsDTO c360PromotionWsDTO = new C360PromoWsDTO();
        c360PromotionWsDTO.setCode("csa_discount_test");

        when(dataMapper.map(csaPromoData, C360PromoWsDTO.class))
                .thenReturn(c360PromotionWsDTO);

        final C360PromotionList c360PromotionListList = (C360PromotionList) c360PromotionListAdapter.adapt(Arrays.asList(csaPromoData));
        verify(dataMapper).map(csaPromoData, C360PromoWsDTO.class);
        assertThat(c360PromotionListList).isNotNull();
        assertThat(c360PromotionListList.getPromotions()
                .get(0)).isSameAs(c360PromotionWsDTO);
    }

    @Test
    public void testC360PromotionListAdapterWithNull()
    {
        final C360PromotionList c360PromotionListList = (C360PromotionList) c360PromotionListAdapter.adapt(null);
        assertThat(c360PromotionListList).isNotNull();
        assertThat(c360PromotionListList.getPromotions()).isEmpty();
    }
}
