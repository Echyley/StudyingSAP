/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.customer360.CustomerViewHeadingData;
import de.hybris.platform.assistedservicewebservices.dto.C360Overview;
import de.hybris.platform.assistedservicewebservices.dto.C360OverviewDataWsDTO;
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
public class C360OverviewAdapterTest {
    @Mock
    private DataMapper dataMapper;

    private C360OverviewAdapter c360OverviewAdapter;

    @Before
    public void setUp()
    {
        c360OverviewAdapter = new C360OverviewAdapter();
        c360OverviewAdapter.setDataMapper(dataMapper);
    }

    @Test
    public void testC360OverviewAdapter()
    {
        final CustomerViewHeadingData overviewData = new CustomerViewHeadingData();

        final C360OverviewDataWsDTO c360OverviewDataWsDTO = new C360OverviewDataWsDTO();

        when(dataMapper.map(overviewData, C360OverviewDataWsDTO.class))
                .thenReturn(c360OverviewDataWsDTO);

        final C360Overview c360Overview = (C360Overview) c360OverviewAdapter.adapt(overviewData);
        verify(dataMapper).map(overviewData, C360OverviewDataWsDTO.class);
        assertThat(c360Overview).isNotNull();
        assertThat(c360Overview.getOverview()).isSameAs(c360OverviewDataWsDTO);
    }

    @Test
    public void testC360OverviewAdapterWithNull()
    {
        final C360Overview c360Overview = (C360Overview) c360OverviewAdapter.adapt(null);
        assertThat(c360Overview).isNotNull();
        assertThat(c360Overview.getOverview()).isNull();
    }
}
