/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.platform.assistedservicefacades.customer360.GeneralActivityDataList;
import de.hybris.platform.assistedservicewebservices.dto.C360TicketList;
import de.hybris.platform.assistedservicewebservices.dto.C360TicketWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.Customer360DataWsDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class C360TicketListAdapter extends C360FragmentDataAdapter<GeneralActivityDataList>
{
	@Override
	public Customer360DataWsDTO adapt(GeneralActivityDataList data)
	{
		final C360TicketList c360TicketList = new C360TicketList();
		List<C360TicketWsDTO> c360TicketWsDTOList = Collections.emptyList();
		if (data != null)
		{
			c360TicketWsDTOList = data.getGeneralActivities().stream().map(activity -> getDataMapper().map(activity, C360TicketWsDTO.class)).collect(Collectors.toList());
		}
		c360TicketList.setTickets(c360TicketWsDTOList);
		return c360TicketList;
	}
}
