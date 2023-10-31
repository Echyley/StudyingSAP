/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerticketingfacades.TicketFacade;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.site.BaseSiteService;

import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@IntegrationTest
public class ClientTest extends ServicelayerTest
{
	private final static Logger LOGGER = Logger.getLogger(ClientTest.class);

	@Resource(name = "c4cTicketFacadeMock")
	private TicketFacade ticketFacade;

	@Resource
	private BaseSiteService baseSiteService;

	@Mock
	private CustomerService customerService;


	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/customerticketingfacades/test/testCustomerTicketing.impex", "UTF-8");

		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("testSite");
		LOGGER.info("BS:" + baseSite);
		baseSiteService.setCurrentBaseSite(baseSite, true);

		if(ticketFacade instanceof C4CTicketFacadeImpl) {
			MockitoAnnotations.initMocks(this);
			CustomerModel customer = new CustomerModel();
			customer.setC4cBuyerId("testC4CId");
			Mockito.when(customerService.getCustomerByCustomerId(null)).thenReturn(customer);
			C4CTicketFacadeImpl c4CTicketFacade = (C4CTicketFacadeImpl) ticketFacade;
			c4CTicketFacade.setCustomerService(customerService);
		}
	}

	@Test
	public void testForGetTicket()
	{
		TicketData data = new TicketData();
		data.setCustomerId("RKB2C1");
		data.setSubject("Ticket subject");
		data.setMessage("meeessage");
		data = ticketFacade.createTicket(data);

		LOGGER.info(data.getSubject());
		LOGGER.info(data.getCustomerId());

		LOGGER.info("Trying get ticket with id: " + data.getId());
		final TicketData ticket = ticketFacade.getTicket(data.getId());

		LOGGER.info("ticket is " + ticket);
		LOGGER.info("ticket subj " + ticket.getSubject());
		LOGGER.info("ticket id " + ticket.getId());
		LOGGER.info("ticket messageHistory " + ticket.getMessageHistory());

		assertNotNull(ticket);
		assertEquals("Ticket subject", ticket.getSubject());
		assertTrue(ticket.getLastModificationDate() != null);
		assertTrue(ticket.getCreationDate() != null);
	}

	@Test
	public void testForGetTickets1()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(3);
		pageableData.setCurrentPage(2);
		pageableData.setSort("LastChangeDateTime");

		final SearchPageData<TicketData> tickets = ticketFacade.getTickets(pageableData);
		for (final TicketData t : tickets.getResults())
		{
			LOGGER.info("ticket is " + t.getId());
			LOGGER.info("ticket is " + t.getSubject());
		}
		assertFalse(tickets.getResults().isEmpty());
	}

	@Test
	public void testForGetTickets2()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(10);
		pageableData.setCurrentPage(0);
		pageableData.setSort("LastChangeDateTime");

		final SearchPageData<TicketData> tickets = ticketFacade.getTickets(pageableData);
		for (final TicketData t : tickets.getResults())
		{
			LOGGER.info("ticket is " + t.getId());
			LOGGER.info("ticket is " + t.getSubject());
		}
		assertFalse(tickets.getResults().isEmpty());
	}

	@Test
	public void testForGetTickets3()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(14);
		pageableData.setCurrentPage(26);
		pageableData.setSort("LastChangeDateTime");

		final SearchPageData<TicketData> tickets = ticketFacade.getTickets(pageableData);
		for (final TicketData t : tickets.getResults())
		{
			LOGGER.info("ticket is " + t.getId());
			LOGGER.info("ticket is " + t.getSubject());
		}
		assertFalse(tickets.getResults().isEmpty());
	}

	@Test
	public void testForGetTickets4()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(14);
		pageableData.setCurrentPage(27);
		pageableData.setSort("LastChangeDateTime");

		final SearchPageData<TicketData> tickets = ticketFacade.getTickets(pageableData);
		for (final TicketData t : tickets.getResults())
		{
			LOGGER.info("ticket is " + t.getId());
			LOGGER.info("ticket is " + t.getSubject());
		}
		assertFalse(tickets.getResults().isEmpty());
	}

	@Test
	public void testForCreateTicket()
	{
		TicketData data = new TicketData();
		data.setCustomerId("RKB2C1");
		data.setSubject("Ticket subject");
		data.setMessage("meeessage");
		data = ticketFacade.createTicket(data);

		LOGGER.info(data.getSubject());
		LOGGER.info(data.getCustomerId());
		LOGGER.info(data.getId());

		assertTrue(data.getSubject().equals("Ticket subject"));
	}

	@Test
	public void testForUpdateTicket()
	{
		TicketData data = new TicketData();
		data.setCustomerId("RKB2C1");
		data.setSubject("Ticket subject");
		data.setMessage("EASYSHARE-Z730-Zoom-Digital-Camera");
		data = ticketFacade.createTicket(data);

		LOGGER.info(data.getSubject());
		LOGGER.info(data.getCustomerId());
		LOGGER.info(data.getId());

		assertTrue(data.getSubject().equals("Ticket subject"));

		final StatusData statusData = new StatusData();
		statusData.setId("COMPLETED");
		data.setStatus(statusData);
		data.setMessage("Let me take a look at this product");
		ticketFacade.updateTicket(data);

		data = ticketFacade.getTicket(data.getId());

		LOGGER.info(data.getStatus().getId());
		assertTrue(data.getTicketEvents().stream().map(event -> event.getText()).collect(Collectors.joining(""))
				.contains("EASYSHARE-Z730-Zoom-Digital-Camera"));
		assertTrue(data.getTicketEvents().stream().map(event -> event.getText()).collect(Collectors.joining(""))
				.contains("Let me take a look at this product"));
		assertTrue(data.getStatus().getId().equalsIgnoreCase("COMPLETED"));
	}

	@Test
	public void testForUpdateTicketStatus()
	{
		TicketData data = new TicketData();
		data.setCustomerId("RKB2C1");
		data.setSubject("Ticket subject");
		data.setMessage("Create message");
		data = ticketFacade.createTicket(data);

		LOGGER.info("After creating ticket");
		LOGGER.info(data.getAvailableStatusTransitions().get(0).getId());
		assertTrue(data.getStatus().getId().equals("OPEN"));
		assertTrue(data.getAvailableStatusTransitions().get(0).getId().equals("COMPLETED"));

		final StatusData statusData = new StatusData();
		statusData.setId("COMPLETED");
		data.setStatus(statusData);
		data.setMessage("New message!");
		ticketFacade.updateTicket(data);
		data = ticketFacade.getTicket(data.getId());

		LOGGER.info("After updating ticket with status 5");
		LOGGER.info(data.getStatus().getId());
		assertFalse(data.getAvailableStatusTransitions().isEmpty());
		assertTrue(data.getStatus().getId().equals("COMPLETED"));
	}

}
