/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.provider;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.customer360.GeneralActivityData;
import de.hybris.platform.assistedservicefacades.customer360.providers.TicketsModelProvider;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class TicketsModelProviderTest
{
	@Mock
	private TicketService ticketService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private UserService userService;
	@Mock
	private Converter<CsTicketModel, GeneralActivityData> ticketConverter;

	@InjectMocks
	private TicketsModelProvider provider = new TicketsModelProvider();

	private Map<String, String> parameters = new HashMap<>();

	@Test
	public void testDataProvidingWhenParametersAreEmpty()
	{
		final CustomerModel currentCustomer = Mockito.mock(CustomerModel.class);
		final CsTicketModel ticketModel = Mockito.mock(CsTicketModel.class);
		final GeneralActivityData generalActivityData = new GeneralActivityData();
		generalActivityData.setType("text.customer360.activity.general.ticket");
		generalActivityData.setId("ticketId");
		generalActivityData.setStatus("status");
		generalActivityData.setDescription("description");

		when(userService.getCurrentUser()).thenReturn(currentCustomer);
		when(ticketService.getTicketsForCustomer(currentCustomer)).thenReturn(Arrays.asList(ticketModel));
		when(ticketConverter.convert(ticketModel)).thenReturn(generalActivityData);

		final GeneralActivityData resultTicket = provider.getModel(parameters).getGeneralActivities().get(0);

		assertEquals("text.customer360.activity.general.ticket", resultTicket.getType());
		assertEquals("ticketId", resultTicket.getId());
		assertEquals("description", resultTicket.getDescription());
		assertEquals("status", resultTicket.getStatus());
	}

	@Test
	public void testDataProvidingWhenListSizeIsInvalid()
	{
		parameters.put("listSize", "invalid");

		assertThatThrownBy(() -> {
			provider.getModel(parameters);
		}).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testDataProvidingWhenSiteRelated()
	{
		parameters.put("listSize", "10");
		parameters.put("isSiteRelated", "true");
		final CustomerModel currentCustomer = Mockito.mock(CustomerModel.class);
		final CsTicketModel ticketModel = Mockito.mock(CsTicketModel.class);
		final BaseSiteModel baseSiteModel = Mockito.mock(BaseSiteModel.class);
		final GeneralActivityData generalActivityData = new GeneralActivityData();
		generalActivityData.setType("text.customer360.activity.general.ticket");
		generalActivityData.setId("ticketId");
		generalActivityData.setStatus("status");
		generalActivityData.setDescription("description");

		when(userService.getCurrentUser()).thenReturn(currentCustomer);
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
		when(ticketConverter.convert(ticketModel)).thenReturn(generalActivityData);

		when(ticketService.getTicketsForSiteAndCustomer(baseSiteModel, currentCustomer)).thenReturn(Arrays.asList(ticketModel));

		final GeneralActivityData resultTicket = provider.getModel(parameters).getGeneralActivities().get(0);

		assertEquals("text.customer360.activity.general.ticket", resultTicket.getType());
		assertEquals("ticketId", resultTicket.getId());
		assertEquals("description", resultTicket.getDescription());
		assertEquals("status", resultTicket.getStatus());
	}

}
