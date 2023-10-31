/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.ticket.jalo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.ticket.enums.CsEventReason;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.impl.DefaultTicketBusinessService;
import de.hybris.platform.ticket.strategies.TicketEventEmailStrategy;

import de.hybris.platform.ticketsystem.data.CsTicketParameter;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class DefaultTicketRenderTest extends AbstractTicketsystemTest
{
	private MockTicketEventEmailStrategy emailService = null;
	private TicketEventEmailStrategy originalMailService = null;

	private MockTicketEventEmailStrategy getEmailService()
	{
		if (emailService == null)
		{
			emailService = new MockTicketEventEmailStrategy();
			emailService.setModelService(modelService);
		}
		return emailService;
	}

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		originalMailService = ((DefaultTicketBusinessService) ticketBusinessService).getTicketEventEmailStrategy();
		((DefaultTicketBusinessService) ticketBusinessService).setTicketEventEmailStrategy(getEmailService());
	}

	@After
	public void tearDown()
	{
		// implement here code executed after each test
		getEmailService().reset();
		((DefaultTicketBusinessService) ticketBusinessService).setTicketEventEmailStrategy(originalMailService);
	}

	/**
	 * Check the service is available
	 */
	@Test
	public void testTicketBusinessService()
	{
		assertNotNull("Can not find ticket business Service", ticketBusinessService);
	}

	@Test
	public void testCreateTicketNoteRender()
	{
		final String headline = "Ticket Headline";
		final String note = "Ticket Creation Notes";

		CsTicketParameter ticketParameter = new CsTicketParameter();
		ticketParameter.setPriority(CsTicketPriority.LOW);
		ticketParameter.setReason(CsEventReason.FIRSTCONTACT);
		ticketParameter.setAssignedAgent(adminUser);
		ticketParameter.setAssignedGroup(testGroup);
		ticketParameter.setCategory(CsTicketCategory.NOTE);
		ticketParameter.setHeadline(headline);
		ticketParameter.setInterventionType(CsInterventionType.CALL);
		ticketParameter.setCreationNotes(note);
		ticketParameter.setCustomer(testUser);

		final CsTicketModel ticket = ticketBusinessService.createTicket(ticketParameter);
		assertNotNull(ticket);

		assertEquals(CsTicketState.OPEN, ticket.getState());
		assertTrue(ticket.getCustomer().equals(testUser));
		assertNull(ticket.getOrder());
		assertEquals(headline, ticket.getHeadline());
		assertTrue(StringUtils.isNotEmpty(ticket.getTicketID()));
		assertTrue(ticket.getAssignedAgent().equals(adminUser));
		assertTrue(ticket.getAssignedGroup().equals(testGroup));
		assertEquals(CsTicketPriority.LOW, ticket.getPriority());
		assertEquals(CsTicketCategory.NOTE, ticket.getCategory());
		assertEquals(1, ticket.getEvents().size());
		assertTrue(ticket.getEvents().get(0) instanceof CsCustomerEventModel);
		final CsCustomerEventModel creationEvent = (CsCustomerEventModel) ticket.getEvents().get(0);
		assertEquals(note, creationEvent.getText());
		assertEquals(CsInterventionType.CALL, creationEvent.getInterventionType());
		assertEquals(CsEventReason.FIRSTCONTACT, creationEvent.getReason());

		assertEquals("Ticket Created: " + note,
				ticketBusinessService.renderTicketEventText(ticketBusinessService.getLastEvent(ticket)));
	}


}