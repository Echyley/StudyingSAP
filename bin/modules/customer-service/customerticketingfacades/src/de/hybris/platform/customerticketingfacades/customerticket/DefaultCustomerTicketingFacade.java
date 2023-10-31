/*
 * [y] hybris Platform
 *
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.customerticketingfacades.customerticket;

import de.hybris.platform.comments.model.CommentAttachmentModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.customerticketingfacades.TicketFacade;
import de.hybris.platform.customerticketingfacades.constants.CustomerticketingfacadesConstants;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.customerticketingfacades.data.TicketCategory;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.data.TicketEventAttachmentFileData;
import de.hybris.platform.customerticketingfacades.data.TicketEventData;
import de.hybris.platform.customerticketingfacades.strategies.TicketAssociationStrategies;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ClassMismatchException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.ticket.enums.CsEventReason;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsResolutionType;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsAgentGroupModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketAttachmentsService;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.ticket.service.TicketException;
import de.hybris.platform.ticket.service.TicketService;
import de.hybris.platform.ticketsystem.data.CsTicketParameter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.BadRequestException;

/**
 * CS Integration Ticket Facade Facade should provide access to a user's support ticket details and full details of
 * support ticket.
 *
 */
public class DefaultCustomerTicketingFacade implements TicketFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultCustomerTicketingFacade.class);

	private TicketService ticketService;
	private UserService userService;
	private BaseSiteService baseSiteService;

	private TicketBusinessService ticketBusinessService;
	private Converter<CsTicketModel, TicketData> ticketConverter;
	private Converter<CsTicketModel, TicketData> ticketListConverter;
	private Converter<CsCustomerEventModel, TicketEventData> ticketEventConverter;
	private Map<String, StatusData> statusMapping;
	private List<TicketAssociationStrategies> associationStrategies;
	private String ticketPriority;
	private String ticketReason;
	private EnumerationService enumerationService;
	private ConfigurationService configurationService;
	private TicketAttachmentsService ticketAttachmentsService;
	private MediaService mediaService;

	private static final String TICKET_ID_NOT_FOUND_ERROR_DESC = "Ticket not found for the given ID " ;
	private static final String EVENT_CODE_NOT_FOUND_ERROR_DESC = "Ticket event not found for the given event code " ;

	@Override
	public TicketData createTicket(final TicketData ticketData)
	{
		final CsTicketModel ticket;
		final CsTicketParameter ticketParameter = createCsTicketParameter(ticketData);
		ticket = getTicketBusinessService().createTicket(ticketParameter);
		ticketData.setId(ticket.getTicketID());
		return ticketData;
	}

	/**
	 * @return CsAgentGroupModel - Default Cs Agent Group Manager
	 */
	protected CsAgentGroupModel getDefaultCsAgentManagerGroup()
	{
		final String csManagerGroup = getConfigurationService().getConfiguration()
				.getString(CustomerticketingfacadesConstants.DEFAULT_CS_AGENT_MANAGER_GROUP_UID, "");
		if (StringUtils.isNotBlank(csManagerGroup))
		{
			try
			{
				return getUserService().getUserGroupForUID(csManagerGroup, CsAgentGroupModel.class);
			}
			catch (ClassMismatchException | UnknownIdentifierException exception)
			{
				LOG.error("Can't set AssignedGroup for the group " + csManagerGroup + ", cause: " + exception);
			}
		}
		return null;
	}

	@Override
	public TicketData updateTicket(final TicketData ticketData)
	{
		CsTicketModel ticket = getTicketService().getTicketForTicketId(ticketData.getId());
		if (ticket == null)
		{
			throw new RuntimeException(TICKET_ID_NOT_FOUND_ERROR_DESC + ticketData.getId());
		}

		// if with status change
		if (ticketData.getStatus() != null
				&& !getStatusMapping().get(ticket.getState().getCode()).getId().equalsIgnoreCase(ticketData.getStatus().getId()))
		{
			ticket = stateChanges.get(getCsStatus(ticketData)).apply(ticket, ticketData);
		}
		else
		// only a note
		{
			final List<MediaModel> attachments = getAttachments(ticketData);
			final CsCustomerEventModel customerEventModel = getTicketBusinessService().addNoteToTicket(ticket, CsInterventionType.IM,
					CsEventReason.UPDATE, ticketData.getMessage(), attachments);
			ticket = customerEventModel != null ? getTicketService().getTicketForTicketId(ticket.getTicketID()) : null;
		}

		if (ticket == null)
		{
			throw new RuntimeException("Something went wrong while updating the ticket for the given ID " + ticketData.getId());
		}

		return getTicketConverter().convert(ticket);
	}

	@Override
	public TicketEventData addAttachmentToEventByEventCode(final String ticketId, final String eventCode, final MultipartFile file)
	{
		final CsTicketModel ticket = getTicketService().getTicketForTicketId(ticketId);
		if (ticket == null)
		{
			throw new RuntimeException(TICKET_ID_NOT_FOUND_ERROR_DESC + ticketId);
		}

		final CsTicketEventModel event = getTicketService().getTicketEventsByTicketEventCode(ticket, eventCode);
		if (event == null)
		{
			throw new RuntimeException(EVENT_CODE_NOT_FOUND_ERROR_DESC + eventCode);
		}

		final CsCustomerEventModel storedEvent = (CsCustomerEventModel) event;
		final MediaModel attachment = createAttachmentModel(file);
		final CsCustomerEventModel updatedEvent = getTicketBusinessService().addAttachmentToNote(ticket, storedEvent, attachment);
		return getTicketEventConverter().convert(updatedEvent);
	}

	/**
	 * @param ticketData
	 * @return List<MediaModel>
	 */
	protected List<MediaModel> getAttachments(final TicketData ticketData)
	{
		List<MediaModel> attachments = null;
		if (CollectionUtils.isNotEmpty(ticketData.getAttachments()))
		{
			attachments = new ArrayList<>(ticketData.getAttachments().size());
			for (final MultipartFile file : ticketData.getAttachments())
			{
				try
				{
					attachments.add(getTicketAttachmentsService().createAttachment(file.getOriginalFilename(), file.getContentType(),
							file.getBytes(), getUserService().getCurrentUser()));
				}
				catch (final IOException e)
				{
					LOG.error(e.getMessage(), e);
					return Collections.emptyList();
				}
			}
		}
		return attachments;
	}

	/**
	 * @param file
	 * @return MediaModel
	 */
	protected MediaModel createAttachmentModel(final MultipartFile file)
	{
		try
		{
			return getTicketAttachmentsService().createAttachment(file.getOriginalFilename(), file.getContentType(),
					file.getBytes(), getUserService().getCurrentUser());
		}
		catch (final IOException e)
		{
			LOG.error(e.getMessage(), e);
			throw new RuntimeException("Failed to create attachment for the ticket.");
		}
	}

	/**
	 * Matches TicketData.Status with CsTicketStatus, using statusMapping map.
	 *
	 * @param data
	 * @return CsTicketState
	 */
	protected CsTicketState getCsStatus(final TicketData data)
	{
		for (final String key : getStatusMapping().keySet())
		{
			if (data.getStatus().getId().equalsIgnoreCase(getStatusMapping().get(key).getId()))
			{
				LOG.info("matching with: " + key);
				return CsTicketState.valueOf(key);
			}
		}
		LOG.warn("Status key not found");
		return null;
	}

	/**
	 * Special map, that know what to do in case of status changed.
	 */
	protected Map<CsTicketState, BiFunction<CsTicketModel, TicketData, CsTicketModel>> stateChanges = new HashMap<CsTicketState, BiFunction<CsTicketModel, TicketData, CsTicketModel>>()
	{
		{
			put(CsTicketState.CLOSED, (updatedTicket, ticketData) -> {
				try
				{
					final List<MediaModel> attachments = getAttachments(ticketData);
					return getTicketBusinessService().resolveTicket(updatedTicket, CsInterventionType.IM, CsResolutionType.CLOSED,
							ticketData.getMessage(), attachments) != null
									? getTicketService().getTicketForTicketId(updatedTicket.getTicketID()) : null;
				}
				catch (final TicketException ex)
				{
					LOG.error(ex.getMessage(), ex);
					return null;
				}
			});

			put(CsTicketState.OPEN, (updatedTicket, ticketData) -> {
				try
				{
					final List<MediaModel> attachments = getAttachments(ticketData);
					return getTicketBusinessService().unResolveTicket(updatedTicket, CsInterventionType.IM, CsEventReason.UPDATE,
							ticketData.getMessage(), attachments) != null
									? getTicketService().getTicketForTicketId(updatedTicket.getTicketID()) : null;
				}
				catch (final TicketException ex)
				{
					LOG.error(ex.getMessage(), ex);
					return null;
				}
			});
		}
	};


	public TicketEventAttachmentFileData getAttachmentFileByAttachmentId(final String ticketId, final String eventCode, final String attachmentId)
	{
		final Collection<CommentAttachmentModel> storedAttachments = getTicketEvent(ticketId, eventCode).getAttachments();

		if (storedAttachments == null)
		{
			throw new RuntimeException("No attachments found for the given event code " + eventCode);
		}
		final List attachmentList = new ArrayList(storedAttachments);
		if (Integer.parseInt(attachmentId) <= 0 || Integer.parseInt(attachmentId) > attachmentList.size())
		{
			throw new RuntimeException("No attachments found for the given attachment Id " + attachmentId);
		}
		final CommentAttachmentModel attachmentModel = (CommentAttachmentModel) attachmentList.get(Integer.parseInt(attachmentId) - 1);
		final MediaModel attachment = (MediaModel) attachmentModel.getItem();
		final TicketEventAttachmentFileData attachmentFileData = new TicketEventAttachmentFileData();
		attachmentFileData.setContent(getMediaService().getDataFromMedia(attachment));
		attachmentFileData.setFilename(attachment.getRealFileName());
		return attachmentFileData;
	}

	@Override
	public int getAttachmentCountByEventCode(final String ticketId, final String eventCode)
	{
		final Collection<CommentAttachmentModel> storedAttachments = getTicketEvent(ticketId, eventCode).getAttachments();
		return (storedAttachments == null) ? 0 : storedAttachments.size();
	}

	@Override
	public TicketData getTicket(final String ticketId)
	{
		final CsTicketModel ticketModel = getTicketService().getTicketForTicketId(ticketId);
		if (ticketModel == null)
		{
			throw new RuntimeException( TICKET_ID_NOT_FOUND_ERROR_DESC + ticketId);
		}

		return ticketModel.getCustomer().getUid().equals(getUserService().getCurrentUser().getUid())
				? getTicketConverter().convert(ticketModel, new TicketData()) : null;
	}

	protected CsTicketEventModel getTicketEvent(final String ticketId, final String eventCode)
	{
		final CsTicketModel ticketModel = getTicketService().getTicketForTicketId(ticketId);
		if (ticketModel == null)
		{
			throw new RuntimeException(TICKET_ID_NOT_FOUND_ERROR_DESC + ticketId);
		}

		if (!ticketModel.getCustomer().getUid().equals(getUserService().getCurrentUser().getUid()))
		{
			throw new RuntimeException("User is not authorized to access this ticket " + ticketId);
		}

		final CsTicketEventModel ticketEventModel = getTicketService().getTicketEventsByTicketEventCode(ticketModel, eventCode);

		if (ticketEventModel == null)
		{
			throw new RuntimeException(EVENT_CODE_NOT_FOUND_ERROR_DESC + eventCode);
		}

		return ticketEventModel;
	}

	@Override
	public SearchPageData<TicketData> getTickets(final PageableData pageableData)
	{
		final SearchPageData<CsTicketModel> searchPageData = getTicketService().getTicketsForCustomerOrderByModifiedTime(
				getUserService().getCurrentUser(), getBaseSiteService().getCurrentBaseSite(), pageableData);

		return convertPageData(searchPageData, getTicketListConverter());
	}

	protected <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final Converter<S, T> converter)
	{
		final SearchPageData<T> result = new SearchPageData<T>();
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());
		result.setResults(Converters.convertAll(source.getResults(), converter));
		return result;
	}

	@Override
	public Map<String, List<TicketAssociatedData>> getAssociatedToObjects()
	{
		final Map<String, List<TicketAssociatedData>> associatedObjects = new HashMap<String, List<TicketAssociatedData>>();
		for (final TicketAssociationStrategies ticketAssocitedStartegy : getAssociationStrategies())
		{
			associatedObjects.putAll(ticketAssocitedStartegy.getObjects(getUserService().getCurrentUser()));
		}
		filterBySiteId(associatedObjects, getBaseSiteService().getCurrentBaseSite().getUid());
		return associatedObjects;
	}

	protected CsTicketParameter createCsTicketParameter(final TicketData ticketData)
	{
		final CsTicketParameter ticketParameter = new CsTicketParameter();
		final AbstractOrderModel associatedObj = getTicketService().getAssociatedObject(ticketData.getAssociatedTo(), null, null);
		if (!StringUtils.isEmpty(ticketData.getAssociatedTo()) && associatedObj == null) {
			throw new BadRequestException("Associated-To Object not found.");
		} else {
			ticketParameter.setAssociatedTo(associatedObj);
		}
		ticketParameter.setPriority(getEnumerationService().getEnumerationValue(CsTicketPriority._TYPECODE, ticketPriority));
		ticketParameter.setReason(getEnumerationService().getEnumerationValue(CsEventReason._TYPECODE, ticketReason));
		ticketParameter.setAssociatedTo(getTicketService().getAssociatedObject(ticketData.getAssociatedTo(), null, null));
		ticketParameter.setAssignedGroup(getDefaultCsAgentManagerGroup());
		ticketParameter.setCategory(CsTicketCategory.valueOf(ticketData.getTicketCategory().name()));
		ticketParameter.setHeadline(ticketData.getSubject());
		ticketParameter.setInterventionType(CsInterventionType.TICKETMESSAGE);
		ticketParameter.setCreationNotes(ticketData.getMessage());
		ticketParameter.setCustomer(getUserService().getCurrentUser());
		ticketParameter.setAttachments(ticketData.getAttachments());
		return ticketParameter;
	}

	/**
	 * remove if associated obj is not related to this basesite, and remove the key who only has empty list。
	 */
	protected void filterBySiteId(Map<String, List<TicketAssociatedData>> associatedObjects, String baseSiteId)
	{
		associatedObjects.forEach((key, list) -> {
			if (!list.isEmpty()) {
				final ArrayList removeList = new ArrayList<TicketAssociatedData>();
				list.forEach(item -> {
					final String objSiteId = item.getSiteUid();
					// objSiteId is not null and different from currentId should be removed
					if (objSiteId != null && !objSiteId.equals(baseSiteId)) {
						removeList.add(item);
					}
				});
				list.removeAll(removeList);
			}
		});

		// ensure the empty list is deleted -- UI friendly
		removeEmptyList(associatedObjects);
	}

	/**
	 * remove key if list is empty
	 */
	protected void removeEmptyList(Map<String, List<TicketAssociatedData>> associatedObjects)
	{
		final ArrayList emptyKeys = new ArrayList<String>();
		associatedObjects.forEach((key, list) -> {
			if (list.isEmpty()) {
				emptyKeys.add(key);
			}
		});
		emptyKeys.forEach(key -> {
			associatedObjects.remove(key);
		});
	}

	/**
	 * @return the ticketService
	 */
	protected TicketService getTicketService()
	{
		return ticketService;
	}

	/**
	 * @return the userService
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * @return the ticketBusinessService
	 */
	protected TicketBusinessService getTicketBusinessService()
	{
		return ticketBusinessService;
	}

	/**
	 * @param ticketService
	 *           the ticketService to set
	 */
	@Required
	public void setTicketService(final TicketService ticketService)
	{
		this.ticketService = ticketService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @param ticketBusinessService
	 *           the ticketBusinessService to set
	 */
	@Required
	public void setTicketBusinessService(final TicketBusinessService ticketBusinessService)
	{
		this.ticketBusinessService = ticketBusinessService;
	}

	/**
	 * @return the ticketConverter
	 */
	protected Converter<CsTicketModel, TicketData> getTicketConverter()
	{
		return ticketConverter;
	}

	protected Converter<CsTicketModel, TicketData> getTicketListConverter()
	{
		return ticketListConverter;
	}

	/**
	 * @return the ticketEventConverter
	 */
	protected Converter<CsCustomerEventModel, TicketEventData> getTicketEventConverter()
	{
		return ticketEventConverter;
	}

	@Required
	public void setTicketListConverter(final Converter<CsTicketModel, TicketData> ticketListConverter)
	{
		this.ticketListConverter = ticketListConverter;
	}

	/**
	 * @return the statusMapping
	 */
	protected Map<String, StatusData> getStatusMapping()
	{
		return statusMapping;
	}

	/**
	 * @return the associationStrategies
	 */
	protected List<TicketAssociationStrategies> getAssociationStrategies()
	{
		return associationStrategies;
	}

	/**
	 * @param ticketConverter
	 *           the ticketConverter to set
	 */
	@Required
	public void setTicketConverter(final Converter<CsTicketModel, TicketData> ticketConverter)
	{
		this.ticketConverter = ticketConverter;
	}

	/**
	 * @param ticketEventConverter
	 *           the ticketEventConverter to set
	 */
	@Required
	public void setTicketEventConverter(final Converter<CsCustomerEventModel, TicketEventData> ticketEventConverter)
	{
		this.ticketEventConverter = ticketEventConverter;
	}

	/**
	 * @param statusMapping
	 *           the statusMapping to set
	 */
	@Required
	public void setStatusMapping(final Map<String, StatusData> statusMapping)
	{
		this.statusMapping = statusMapping;
	}

	/**
	 * @param associationStrategies
	 *           the associationStrategies to set
	 */
	@Required
	public void setAssociationStrategies(final List<TicketAssociationStrategies> associationStrategies)
	{
		this.associationStrategies = associationStrategies;
	}

	/**
	 * @return the ticketPriority
	 */
	protected String getTicketPriority()
	{
		return ticketPriority;
	}

	/**
	 * @param ticketPriority
	 *           the ticketPriority to set
	 */
	@Required
	public void setTicketPriority(final String ticketPriority)
	{
		this.ticketPriority = ticketPriority;
	}

	/**
	 * @return the ticketReason
	 */
	protected String getTicketReason()
	{
		return ticketReason;
	}

	/**
	 * @param ticketReason
	 *           the ticketReason to set
	 */
	@Required
	public void setTicketReason(final String ticketReason)
	{
		this.ticketReason = ticketReason;
	}

	/**
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * @param enumerationService
	 *           the enumerationService to set
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	@Override
	public List<TicketCategory> getTicketCategories()
	{
		return Arrays.asList(TicketCategory.values());
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the baseSiteService
	 */
	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * @param baseSiteService
	 *           the baseSiteService to set
	 */
	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * @return the ticketAttachmentsService
	 */
	protected TicketAttachmentsService getTicketAttachmentsService()
	{
		return ticketAttachmentsService;
	}

	/**
	 * @param ticketAttachmentsService
	 *           the ticketAttachmentsService to set
	 */
	@Required
	public void setTicketAttachmentsService(final TicketAttachmentsService ticketAttachmentsService)
	{
		this.ticketAttachmentsService = ticketAttachmentsService;
	}

	/**
	 * @return the mediaService
	 */
	protected MediaService getMediaService()
	{
		return mediaService;
	}

	/**
	 * @param mediaService
	 *           the mediaService to set
	 */
	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

}
