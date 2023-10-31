/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingfacades.customerticket;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.customerticketingfacades.TicketFacade;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketEventAttachmentData;
import de.hybris.platform.customerticketingfacades.data.TicketCategory;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.data.TicketEventData;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.ticket.jalo.AbstractTicketsystemTest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.ws.rs.BadRequestException;

import de.hybris.platform.ticket.service.TicketAttachmentsService;
import de.hybris.platform.ticket.service.impl.DefaultTicketAttachmentsService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


/**
 * Test cases for the Customer Ticket Facade
 *
 */
public class CustomerTicketingFacadeIntegrationTest extends AbstractTicketsystemTest
{
	private static final String SUBJECT = "Ticket subject";
	private static final String NOTE = "Hello";
	private static final int DEFAULT_NUMBER_OF_TICKETS = 2;

	private final String TEST_CATALOG_ID = "testCatalog";
	private final String TEST_CS_AGENT_USER_GROUP_ID = "testTicketGroup1";
	private final String TEST_MEDIAFOLDER_NAME = "root";

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "statusMapping")
	private Map<String, StatusData> statusMapping;

	@Resource(name = "validTransitions")
	private Map<StatusData, List<StatusData>> validTransitions;

	@Resource(name = "ticket_open")
	private StatusData open;

	@Resource(name = "defaultTicketFacade")
	private TicketFacade ticketFacade;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource(name = "defaultTicketAttachmentsService")
	private TicketAttachmentsService ticketAttachmentsService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		importCsv("/customerticketingfacades/test/testCustomerTicketing.impex", "UTF-8");

		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("testSite");
		baseSiteService.setCurrentBaseSite(baseSite, true);

		userService.setCurrentUser(testUser);

		createTickets(DEFAULT_NUMBER_OF_TICKETS);
	}

	@Test
	public void testCreateTicket()
	{
		final TicketData ticketData = new TicketData();
		ticketData.setSubject(SUBJECT);
		ticketData.setMessage(NOTE);
		ticketData.setTicketCategory(TicketCategory.ENQUIRY);
		ticketData.setCustomerId(testUser.getUid());
		ticketData.setStatus(open);

		final TicketData ticketData1 = ticketFacade.createTicket(ticketData);
		assertNotNull(ticketData1.getId());

		assertEquals(ticketData1.getStatus().getId(), open.getId());
		assertEquals(ticketData1.getSubject(), SUBJECT);

		final TicketData ticket = ticketFacade.getTicket(ticketData1.getId());

		assertNotNull(ticket);
		assertEquals(ticket.getSubject(), SUBJECT);
		if (ticket.getTicketEvents() == null || ticket.getTicketEvents().isEmpty())
		{
			assertTrue(ticket.getMessageHistory().contains(NOTE));
		}
		else
		{
			assertTrue(ticket.getTicketEvents().get(0).getText().contains(NOTE));
		}
	}

	@Test(expected = BadRequestException.class)
	public void testCreateTicketWithInvalidAssociatedObj()
	{
		final TicketData ticketData = new TicketData();
		ticketData.setAssociatedTo("obj=1234");
		ticketFacade.createTicket(ticketData);
	}

	@Test
	public void testGetTicketsForCustomerOrderByModifiedTime()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(DEFAULT_NUMBER_OF_TICKETS);
		pageableData.setCurrentPage(0);
		pageableData.setSort("byDate");

		final SearchPageData<TicketData> tickets = ticketFacade.getTickets(pageableData);

		// first one must be after second one and so on. So latest on bottom, newest on top
		assertTrue(
				tickets.getResults().get(0).getLastModificationDate().after(tickets.getResults().get(1).getLastModificationDate()));
	}

	@Test
	public void testGetTicketsIncludesTicketCategory()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(DEFAULT_NUMBER_OF_TICKETS);
		pageableData.setCurrentPage(0);

		final SearchPageData<TicketData> ticketsSearchPageData = ticketFacade.getTickets(pageableData);
		final List<TicketData> tickets = ticketsSearchPageData.getResults();

		assertEquals(DEFAULT_NUMBER_OF_TICKETS, tickets.size());

		for (final TicketData ticket: tickets)
		{
			assertNotNull(ticket.getTicketCategory());
		}

	}

	@Test
	public void testUpdateTicketShouldKeepOldStatusIfNewStatusIsNull()
	{
		final String TICKET_STATUS_OPEN = "OPEN";

		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(DEFAULT_NUMBER_OF_TICKETS);
		pageableData.setCurrentPage(0);
		final SearchPageData<TicketData> tickets = ticketFacade.getTickets(pageableData);

		final TicketData ticket = tickets.getResults().get(0);
		assertEquals(TICKET_STATUS_OPEN, ticket.getStatus().getId());
		final String ticketId = ticket.getId();

		final TicketData ticketData = new TicketData();
		ticketData.setId(ticketId);
		ticketData.setStatus(null);
		ticketData.setMessage("Sorry, forgot the status. But should be ok.");

		final TicketData updatedTicket = ticketFacade.updateTicket(ticketData);

		assertEquals(TICKET_STATUS_OPEN, updatedTicket.getStatus().getId());
	}

	@Test
	public void testAddImageAttachmentToEventByEventCodeShouldAddAttachmentToEvent()
	{
		final String TEST_EVENT_MESSAGE = "Test message.";
		final int INITIAL_TOTAL_NUMBER_OF_ATTACHMENTS = 0;
		final int FINAL_TOTAL_NUMBER_OF_ATTACHMENTS = 1;

		final int TEST_ATTACHMENT_IMAGE_WIDTH = 2;
		final int TEST_ATTACHMENT_IMAGE_HEIGHT = 2;
		final BufferedImage TEST_ATTACHMENT_IMAGE = new BufferedImage(TEST_ATTACHMENT_IMAGE_WIDTH, TEST_ATTACHMENT_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

		final int MAX_COLORS = 256;
		final int MOVE_24_BITS = 24;
		final int MOVE_16_BITS = 16;
		final int MOVE_8_BITS = 8;

		for(int y = 0; y < TEST_ATTACHMENT_IMAGE_HEIGHT; y++){
			for(int x = 0; x < TEST_ATTACHMENT_IMAGE_WIDTH; x++){
				final int alpha = (int)(Math.random()*MAX_COLORS);
				final int red = (int)(Math.random()*MAX_COLORS);
				final int green = (int)(Math.random()*MAX_COLORS);
				final int blue = (int)(Math.random()*MAX_COLORS);
				final int pixel = (alpha<<MOVE_24_BITS) | (red<<MOVE_16_BITS) | (green<<MOVE_8_BITS) | blue;
				TEST_ATTACHMENT_IMAGE.setRGB(x, y, pixel);
			}
		}

		final String TEST_ATTACHMENT_FILE_NAME = "test_file.jpg";
		byte[] TEST_ATTACHMENT_FILE_CONTENT = null;

		try
		{
			final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageIO.write(TEST_ATTACHMENT_IMAGE, "jpg", outputStream);
			TEST_ATTACHMENT_FILE_CONTENT = outputStream.toByteArray();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail("Failed to create the attachment image for testing.");
		}

		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(DEFAULT_NUMBER_OF_TICKETS);
		pageableData.setCurrentPage(0);
		final SearchPageData<TicketData> tickets = ticketFacade.getTickets(pageableData);

		final TicketData ticket = tickets.getResults().get(0);
		final String ticketId = ticket.getId();

		TicketEventData ticketEvent = null;
		if ( ticket.getTicketEvents() != null && ticket.getTicketEvents().size() > 0 )
		{
			ticketEvent = ticket.getTicketEvents().get(0);
		}
		else
		{
			final TicketData ticketData = new TicketData();
			ticketData.setId(ticketId);
			ticketData.setStatus(null);
			ticketData.setMessage(TEST_EVENT_MESSAGE);

			final TicketData updatedTicket = ticketFacade.updateTicket(ticketData);
			if ( updatedTicket.getTicketEvents().size() > 0 )
			{
				ticketEvent = updatedTicket.getTicketEvents().get(0);
			}
			else
			{
				fail("Failed to create a new event for the given test ticket.");
			}
		}

		if (ticketEvent.getAttachments() != null)
		{
			assertEquals(INITIAL_TOTAL_NUMBER_OF_ATTACHMENTS, ticketEvent.getAttachments().size());
		}

		final String eventCode = ticketEvent.getCode();
		final MultipartFile file = new MockMultipartFile(TEST_ATTACHMENT_FILE_NAME, TEST_ATTACHMENT_FILE_NAME, APPLICATION_OCTET_STREAM_VALUE, TEST_ATTACHMENT_FILE_CONTENT);

		((DefaultTicketAttachmentsService) ticketAttachmentsService).setCatalogId(TEST_CATALOG_ID);
		((DefaultTicketAttachmentsService) ticketAttachmentsService).setFolderName(TEST_MEDIAFOLDER_NAME);
		((DefaultTicketAttachmentsService) ticketAttachmentsService).setCommonCsAgentUserGroup(TEST_CS_AGENT_USER_GROUP_ID);
		final TicketEventData updatedTicketEvent = ticketFacade.addAttachmentToEventByEventCode(ticketId, eventCode, file);

		if (updatedTicketEvent.getAttachments() != null)
		{
			final List<TicketEventAttachmentData> ticketEventAttachments = updatedTicketEvent.getAttachments();
			assertEquals(FINAL_TOTAL_NUMBER_OF_ATTACHMENTS, ticketEventAttachments.size());

			final TicketEventAttachmentData lastTicketEventAttachment = ticketEventAttachments.get(ticketEventAttachments.size() - 1);
			assertEquals(TEST_ATTACHMENT_FILE_NAME, lastTicketEventAttachment.getFilename());
		}
		else
		{
			fail("Failed to add attachment to the test event.");
		}
	}

	@Test
	public void testAddTextAttachmentToEventByEventCodeShouldAddAttachmentToEvent()
	{
		final String TEST_EVENT_MESSAGE = "Test message.";
		final int INITIAL_TOTAL_NUMBER_OF_ATTACHMENTS = 0;
		final int FINAL_TOTAL_NUMBER_OF_ATTACHMENTS = 1;

		final String TEST_ATTACHMENT_FILE_NAME = "test_file.txt";
		byte[] TEST_ATTACHMENT_FILE_CONTENT = "Test file content".getBytes();

		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(DEFAULT_NUMBER_OF_TICKETS);
		pageableData.setCurrentPage(0);
		final SearchPageData<TicketData> tickets = ticketFacade.getTickets(pageableData);

		final TicketData ticket = tickets.getResults().get(0);
		final String ticketId = ticket.getId();

		TicketEventData ticketEvent = null;
		if ( ticket.getTicketEvents() != null && ticket.getTicketEvents().size() > 0 )
		{
			ticketEvent = ticket.getTicketEvents().get(0);
		}
		else
		{
			final TicketData ticketData = new TicketData();
			ticketData.setId(ticketId);
			ticketData.setStatus(null);
			ticketData.setMessage(TEST_EVENT_MESSAGE);

			final TicketData updatedTicket = ticketFacade.updateTicket(ticketData);
			if ( updatedTicket.getTicketEvents().size() > 0 )
			{
				ticketEvent = updatedTicket.getTicketEvents().get(0);
			}
			else
			{
				fail("Failed to create a new event for the given test ticket.");
			}
		}

		if (ticketEvent.getAttachments() != null)
		{
			assertEquals(INITIAL_TOTAL_NUMBER_OF_ATTACHMENTS, ticketEvent.getAttachments().size());
		}

		final String eventCode = ticketEvent.getCode();
		final MultipartFile file = new MockMultipartFile(TEST_ATTACHMENT_FILE_NAME, TEST_ATTACHMENT_FILE_NAME, APPLICATION_OCTET_STREAM_VALUE, TEST_ATTACHMENT_FILE_CONTENT);

		((DefaultTicketAttachmentsService) ticketAttachmentsService).setCatalogId(TEST_CATALOG_ID);
		((DefaultTicketAttachmentsService) ticketAttachmentsService).setFolderName(TEST_MEDIAFOLDER_NAME);
		((DefaultTicketAttachmentsService) ticketAttachmentsService).setCommonCsAgentUserGroup(TEST_CS_AGENT_USER_GROUP_ID);
		final TicketEventData updatedTicketEvent = ticketFacade.addAttachmentToEventByEventCode(ticketId, eventCode, file);

		if (updatedTicketEvent.getAttachments() != null)
		{
			assertEquals(FINAL_TOTAL_NUMBER_OF_ATTACHMENTS, updatedTicketEvent.getAttachments().size());
		}
		else
		{
			fail("Failed to add attachment to the test event.");
		}
	}

	private void createTickets(final Integer number)
	{
		for (int i = 0; i < number; i++)
		{
			final TicketData ticketData = new TicketData();
			ticketData.setSubject(SUBJECT);
			ticketData.setMessage(NOTE);
			ticketData.setTicketCategory(TicketCategory.COMPLAINT);
			ticketData.setCustomerId(testUser.getUid());
			final TicketData createdTicketData = ticketFacade.createTicket(ticketData);

			assertNotNull(createdTicketData.getId());
		}
	}
}
