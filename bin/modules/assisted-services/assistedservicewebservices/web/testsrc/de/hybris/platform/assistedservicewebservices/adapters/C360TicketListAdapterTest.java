/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.customer360.GeneralActivityData;
import de.hybris.platform.assistedservicefacades.customer360.GeneralActivityDataList;
import de.hybris.platform.assistedservicewebservices.dto.C360TicketList;
import de.hybris.platform.assistedservicewebservices.dto.C360TicketWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class C360TicketListAdapterTest
{
    @Mock
    private DataMapper dataMapper;

    private C360TicketListAdapter c360TicketListAdapter;

    @Before
    public void setUp()
    {
        c360TicketListAdapter = new C360TicketListAdapter();
        c360TicketListAdapter.setDataMapper(dataMapper);
    }

    @Test
    public void testC360TicketListAdapterNotEmptyList()
    {
        final GeneralActivityData generalActivityData = new GeneralActivityData();

        final GeneralActivityDataList generalActivityDataList = new GeneralActivityDataList();
        generalActivityDataList.setGeneralActivities(Arrays.asList(generalActivityData));

        final C360TicketWsDTO c360TicketWsDTO = new C360TicketWsDTO();

        when(dataMapper.map(generalActivityData, C360TicketWsDTO.class))
                .thenReturn(c360TicketWsDTO);

        final C360TicketList c360TicketList = (C360TicketList) c360TicketListAdapter.adapt(generalActivityDataList);
        verify(dataMapper).map(generalActivityData, C360TicketWsDTO.class);
        assertThat(c360TicketList).isNotNull();
        assertThat(c360TicketList.getTickets().get(0)).isSameAs(c360TicketWsDTO);
    }

    @Test
    public void testC360TicketListAdapterEmptyList()
    {
        final GeneralActivityDataList generalActivityDataList = new GeneralActivityDataList();
        generalActivityDataList.setGeneralActivities(Arrays.asList());

        final C360TicketList c360TicketList = (C360TicketList) c360TicketListAdapter.adapt(generalActivityDataList);
        assertThat(c360TicketList).isNotNull();
        assertThat(c360TicketList.getTickets()).isEmpty();
    }

    @Test
    public void testC360CouponListAdapterForNull()
    {
        final C360TicketList c360TicketList = (C360TicketList) c360TicketListAdapter.adapt(null);
        assertThat(c360TicketList).isNotNull();
        assertThat(c360TicketList.getTickets()).isEmpty();
    }
}
