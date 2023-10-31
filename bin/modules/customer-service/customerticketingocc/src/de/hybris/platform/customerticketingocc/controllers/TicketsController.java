/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.customerticketingocc.controllers;

import static de.hybris.platform.customerticketingocc.constants.SecuredAccessConstants.ROLE_CUSTOMERGROUP;
import static de.hybris.platform.customerticketingocc.constants.SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.customerticketingfacades.TicketFacade;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.customerticketingfacades.data.TicketCategory;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.data.TicketEventData;
import de.hybris.platform.customerticketingfacades.data.TicketEventAttachmentData;
import de.hybris.platform.customerticketingfacades.data.TicketEventAttachmentFileData;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketAssociatedObjectListWsDTO;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketAssociatedObjectWsDTO;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketCategoryListWsDTO;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketCategoryWsDTO;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketEventAttachmentWsDTO;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketEventWsDTO;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketListWsDTO;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketStarterWsDTO;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketWsDTO;
import de.hybris.platform.customerticketingocc.errors.exceptions.TicketCreateException;
import de.hybris.platform.customerticketingocc.errors.exceptions.TicketEventAttachmentCreateException;
import de.hybris.platform.customerticketingocc.errors.exceptions.TicketEventCreateException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/{baseSiteId}")
@ApiVersion("v2")
@Tag(name = "Tickets")
public class TicketsController extends TicketBaseController
{
	private static final Logger LOG = LoggerFactory.getLogger(TicketsController.class);

	@Resource
	private TicketFacade ticketFacade;

	@Resource
	private Validator ticketStarterValidator;

	@Resource
	private Validator ticketEventValidator;

	@Value("${customerticketingocc.tickets.events.attachments.maximumNumber}")
	private int maximumNumberOfAttachmentsPerTicketEvent;

	@GetMapping(value="/users/{userId}/tickets", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(OK)
	@Secured({ ROLE_CUSTOMERGROUP, ROLE_CUSTOMERMANAGERGROUP })
	@Operation(summary = "Get all tickets for user.", description = "Returns history data for all tickets requested by a specified user for a specified base store. The response can display the results across multiple pages, if required.", operationId = "getTickets")
	@ApiBaseSiteIdAndUserIdParam
	public TicketListWsDTO getTickets(
			@Parameter(description = "The current result page requested.", required = false) @RequestParam(value = "currentPage", defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "The number of results returned per page.", required = false) @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Sorting method applied to the returned results. Currently, byDate and byTicketId are supported.", required = false) @RequestParam(value = "sort", defaultValue = "byDate") final String sort,
			@ApiFieldsParam(defaultValue = "BASIC") @RequestParam(required = false, defaultValue = "BASIC") final String fields)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(pageSize);
		pageableData.setCurrentPage(currentPage);
		pageableData.setSort(sort);
		final SearchPageData<TicketData> searchPageData = getTicketFacade().getTickets(pageableData);
		return getDataMapper().map(searchPageData, TicketListWsDTO.class, fields);
	}

	@PostMapping(value="/users/{userId}/tickets", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(CREATED)
	@Secured({ ROLE_CUSTOMERGROUP })
	@Operation(summary = "Create a ticket.", operationId = "createTicket")
	@ApiBaseSiteIdAndUserIdParam
	public TicketWsDTO createTicket(
			@Parameter(description = "Basic information of the ticket.", required = true) @RequestBody final TicketStarterWsDTO ticketStarter,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = "DEFAULT") final String fields)
	{
		validate(ticketStarter, "ticketStarter", getTicketStarterValidator());
		final TicketData ticketData = getDataMapper().map(ticketStarter, TicketData.class);
		final TicketData createdTicketData;
		try
		{
			createdTicketData = getTicketFacade().createTicket(ticketData);
		}
		catch (final RuntimeException re)
		{
			throw new TicketCreateException(getErrorMessage(re).orElse("Encountered an error when creating a new ticket"), null, re);
		}
		final TicketData returnedTicketData = getTicketFacade().getTicket(createdTicketData.getId());
		return getDataMapper().map(returnedTicketData, TicketWsDTO.class, fields);
	}

	@GetMapping(value = "/users/{userId}/tickets/{ticketId}", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(OK)
	@Secured({ ROLE_CUSTOMERGROUP, ROLE_CUSTOMERMANAGERGROUP })
	@Operation(summary = "Get a ticket by ticket id.", description = "", operationId = "getTicket")
	@ApiBaseSiteIdAndUserIdParam
	public TicketWsDTO getTicket(
			@Parameter(description = "Ticket identifier.<br> Example: ```00000001```.", required = true) @PathVariable final String ticketId,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = "DEFAULT") final String fields)
	{
		final TicketData ticketData = getTicketById(ticketId).orElseThrow(() -> new NotFoundException("Ticket not found for the given ID " + ticketId, "notFound"));
		return getDataMapper().map(ticketData, TicketWsDTO.class, fields);
	}

	@PostMapping(value = "/users/{userId}/tickets/{ticketId}/events", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(CREATED)
	@Secured({ ROLE_CUSTOMERGROUP })
	@Operation(summary = "Create a new ticket event.", description = "Create new ticket event with property message(required) and toStatus(optional).", operationId = "createTicketEvent")
	@ApiBaseSiteIdAndUserIdParam
	public TicketEventWsDTO createTicketEvent(
			@Parameter(description = "Ticket identifier.<br> Example: ```00000001```.", required = true) @PathVariable final String ticketId,
			@Parameter(description = "Basic information about the ticket event.", required = true) @RequestBody final TicketEventWsDTO ticketEvent,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = "DEFAULT") final String fields)
	{
		validate(ticketEvent, "ticketEvent", getTicketEventValidator());
		final TicketData ticketData = getDataMapper().map(ticketEvent, TicketData.class);
		ticketData.setId(ticketId);
		final TicketData storedTicketData = getTicketById(ticketId).orElseThrow(() -> new TicketEventCreateException("Ticket not found for the given ID " + ticketId, "notFound"));

		if (ticketData.getStatus() == null)
		{
			ticketData.setStatus(storedTicketData.getStatus());
		}
		else if (storedTicketData.getStatus() != null && !ticketData.getStatus().getId().equals(storedTicketData.getStatus().getId()))
		{
			// ensure the new status is allowed
			final List<StatusData> availableStatuses = storedTicketData.getAvailableStatusTransitions();
			if (availableStatuses != null)
			{
				final String expectedStatusId = ticketData.getStatus().getId();
				final Optional<StatusData> allowedState = availableStatuses.stream().filter(data -> data.getId().equals(expectedStatusId)).findAny();
				if (allowedState.isEmpty())
				{
					throw new TicketEventCreateException("Unable to change ticket status to " + ticketData.getStatus().getId() + " for the ticket " + ticketId, null);
				}
			}
		}
		try
		{
			final TicketData updatedTicketData = getTicketFacade().updateTicket(ticketData);
			final List<TicketEventData> updatedTicketEventDataList = updatedTicketData.getTicketEvents();
			if ( updatedTicketEventDataList != null && !updatedTicketEventDataList.isEmpty())
			{
				return getDataMapper().map(updatedTicketEventDataList.get(0), TicketEventWsDTO.class, fields);
			}
			else
			{
				throw new TicketEventCreateException("Unable to add ticket event to the ticket with given ticket id " + ticketId, null);
			}
		}
		catch (final RuntimeException re)
		{
			throw new TicketEventCreateException(getErrorMessage(re).orElse("Unable to add ticket event to the ticket with given ticket id " + ticketId), null ,re);
		}
	}

	@GetMapping(value = "/ticketCategories", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(OK)
	@Operation(summary = "Get all ticket categories.", operationId = "getTicketCategories")
	@ApiBaseSiteIdParam
	public TicketCategoryListWsDTO getTicketCategories(
			@ApiFieldsParam @RequestParam(required = false, defaultValue = "DEFAULT") final String fields)
	{
		TicketCategoryListWsDTO ticketCategoryList = new TicketCategoryListWsDTO();
		final List<TicketCategory> ticketCategories = getTicketFacade().getTicketCategories();
		final List<TicketCategoryWsDTO> mappedTicketCategories = getDataMapper().mapAsList(ticketCategories, TicketCategoryWsDTO.class, fields);
		ticketCategoryList.setTicketCategories(mappedTicketCategories);
		return ticketCategoryList;
	}

	@GetMapping(value = "/users/{userId}/ticketAssociatedObjects", produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(OK)
	@Secured({ ROLE_CUSTOMERGROUP })
	@Operation(summary = "Get order and cart objects that can be associated with a ticket for the current user.", operationId = "getTicketAssociatedObjects")
	@ApiBaseSiteIdAndUserIdParam
	public TicketAssociatedObjectListWsDTO getTicketAssociatedObjects(
			@ApiFieldsParam @RequestParam(required = false, defaultValue = "DEFAULT") final String fields)
	{
		final Map<String, List<TicketAssociatedData>> associatedObjectDataMap = getTicketFacade().getAssociatedToObjects();
		final List<TicketAssociatedData> ticketAssociatedDataList = new ArrayList<>();
		for (List<TicketAssociatedData> value : associatedObjectDataMap.values())
		{
			ticketAssociatedDataList.addAll(value);
		}
		final List<TicketAssociatedObjectWsDTO> ticketAssociatedObjects = getDataMapper()
				.mapAsList(ticketAssociatedDataList, TicketAssociatedObjectWsDTO.class, fields);
		final TicketAssociatedObjectListWsDTO ticketAssociatedObjectListWsDTO = new TicketAssociatedObjectListWsDTO();
		ticketAssociatedObjectListWsDTO.setTicketAssociatedObjects(ticketAssociatedObjects);
		return ticketAssociatedObjectListWsDTO;
	}

	@GetMapping(value = "/users/{userId}/tickets/{ticketId}/events/{eventCode}/attachments/{attachmentId}", produces = APPLICATION_OCTET_STREAM_VALUE)
	@ResponseStatus(OK)
	@Secured({ ROLE_CUSTOMERGROUP })
	@Operation(summary = "Retrieves an attachment for an event in a ticket.", description = "Retrieves an attachment based on the attachment identifier for a specific ticket event.",
			   operationId = "getTicketEventAttachment")
	@ApiBaseSiteIdAndUserIdParam
	public ResponseEntity<byte[]> getTicketEventAttachment(
			@Parameter(description = "Ticket identifier.<br> Example: ```00000001```.", required = true) @PathVariable @Nonnull @Valid final String ticketId,
			@Parameter(description = "Ticket event code that is specific to each event. It is used to identify an event.<br>Example: ```00000A15```.", required = true) @PathVariable @Nonnull @Valid final String eventCode,
			@Parameter(description = "Attachment identifier.<br>Example: ```001```.", required = true) @PathVariable @Nonnull @Valid final String attachmentId)
	{
		final TicketEventAttachmentFileData attachmentFileData = getAttachmentFileByAttachmentId(ticketId, eventCode, attachmentId).orElseThrow(() -> new NotFoundException("Ticket Event attachment not found.", "notFound"));
		return ResponseEntity.ok().contentLength(attachmentFileData.getContent().length)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + attachmentFileData.getFilename())
				.body(attachmentFileData.getContent());
	}

	@PostMapping(value = "/users/{userId}/tickets/{ticketId}/events/{eventCode}/attachments", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(CREATED)
	@Secured({ ROLE_CUSTOMERGROUP })
	@Operation(summary = "Creates an attachment for an event in a ticket.", description = "Creates an attachment for a specific ticket event. "
			+ "The event code is used to identify the ticket event.", operationId = "createTicketEventAttachment")
	@ApiBaseSiteIdAndUserIdParam
	public TicketEventAttachmentWsDTO createTicketEventAttachment(
			@Parameter(description = "Ticket identifier.<br> Example: ```00000001```.", required = true) @PathVariable final String ticketId,
			@Parameter(description = "Ticket event code is specific to each event. It is used to identify an event.<br>Example: ```00000A15```.", required = true) @PathVariable final String eventCode,
			@Parameter(description = "File to be attached to a ticket event.", required = true) @RequestParam(value = "ticketEventAttachment") MultipartFile ticketEventAttachment,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		try
		{
			final int attachmentCount = getTicketFacade().getAttachmentCountByEventCode(ticketId, eventCode);
			if (attachmentCount >= maximumNumberOfAttachmentsPerTicketEvent)
			{
				throw new TicketEventAttachmentCreateException("You have exceeded the maximum number (" + maximumNumberOfAttachmentsPerTicketEvent + ") of attachments.", null);
			}
			final TicketEventData updatedTicketEventData = getTicketFacade().addAttachmentToEventByEventCode(ticketId, eventCode, ticketEventAttachment);
			final TicketEventAttachmentData ticketEventAttachmentData = getLastTicketEventAttachment(updatedTicketEventData).orElseThrow(() -> new TicketEventAttachmentCreateException("Failed to create attachment for the given ticket id / event code", null));
			final TicketEventAttachmentWsDTO ticketEventAttachmentWsDTO = getDataMapper().map(ticketEventAttachmentData, TicketEventAttachmentWsDTO.class, fields);
			ticketEventAttachmentWsDTO.setId(encodeIdFromArrayIndex(updatedTicketEventData.getAttachments().size() - 1));
			return ticketEventAttachmentWsDTO;
		}
		catch (final RuntimeException re)
		{
			LOG.error(re.getMessage(), re);
			throw new TicketEventAttachmentCreateException(getErrorMessage(re).orElse("Failed to create attachment for the given ticket id / event code"), null ,re);
		}
	}

	protected Optional<TicketData> getTicketById(final String ticketId)
	{
		try
		{
			return Optional.ofNullable(getTicketFacade().getTicket(ticketId));
		}
		catch (final RuntimeException re)
		{
			LOG.error(re.getMessage(), re);
		}
		return Optional.empty();
	}

	protected Optional<TicketEventAttachmentData> getLastTicketEventAttachment(final TicketEventData ticketEventData)
	{
		if (ticketEventData != null)
		{
			try
			{
				final List<TicketEventAttachmentData> ticketEventAttachments = ticketEventData.getAttachments();
				return Optional.ofNullable(ticketEventAttachments.get(ticketEventAttachments.size() - 1));
			}
			catch (final RuntimeException re)
			{
				LOG.error(re.getMessage(), re);
			}
		}
		return Optional.empty();
	}

	protected Optional<TicketEventAttachmentFileData> getAttachmentFileByAttachmentId(final String ticketId, final String eventCode, final String attachmentId)
	{
		try
		{
			return Optional.ofNullable(getTicketFacade().getAttachmentFileByAttachmentId(ticketId, eventCode, attachmentId));
		}
		catch (final RuntimeException re)
		{
			LOG.error(re.getMessage(), re);
		}
		return Optional.empty();
	}

	private String encodeIdFromArrayIndex(final int arrayIndex)
	{
		return String.format("%03d", arrayIndex + 1);
	}

	public TicketFacade getTicketFacade()
	{
		return ticketFacade;
	}

	public void setTicketFacade(final TicketFacade ticketFacade)
	{
		this.ticketFacade = ticketFacade;
	}

	public Validator getTicketStarterValidator()
	{
		return ticketStarterValidator;
	}

	public void setTicketStarterValidator(final Validator ticketStarterValidator)
	{
		this.ticketStarterValidator = ticketStarterValidator;
	}

	public Validator getTicketEventValidator()
	{
		return ticketEventValidator;
	}

	public void setTicketEventValidator(final Validator ticketEventValidator)
	{
		this.ticketEventValidator = ticketEventValidator;
	}

	private Optional<String> getErrorMessage(final RuntimeException exception)
	{
		if (exception instanceof ModelSavingException && exception.getCause() instanceof InterceptorException)
		{
			final String causeMessage = exception.getCause().getMessage();
			final String message = causeMessage.substring(causeMessage.indexOf(':') + 1)
					.replace("\"headline\"", "\"Subject\"")
					.replace("\"text\"", "\"Message\"")
					.trim();
			return Optional.of(message);
		}
		if (exception instanceof UnknownIdentifierException)
		{
			return Optional.of(exception.getMessage().trim());
		}
		if (exception.getMessage() != null && !(exception.getMessage().isBlank()))
		{
			return Optional.of(exception.getMessage().trim());
		}
		return Optional.empty();
	}
}
