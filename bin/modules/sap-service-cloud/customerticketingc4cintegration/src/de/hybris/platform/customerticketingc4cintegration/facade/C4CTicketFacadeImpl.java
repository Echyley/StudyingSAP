/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.facade;

import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.ORDER_DEFAULT_VALUE;
import static de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants.TICKETID_OBJECTID_MAP;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commerceservices.customer.CustomerService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerticketingc4cintegration.SitePropsHolder;
import de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants;
import de.hybris.platform.customerticketingc4cintegration.data.Contact;
import de.hybris.platform.customerticketingc4cintegration.data.IndividualCustomer;
import de.hybris.platform.customerticketingc4cintegration.data.MemoActivity;
import de.hybris.platform.customerticketingc4cintegration.data.ServiceRequestData;
import de.hybris.platform.customerticketingc4cintegration.exception.C4CServiceException;
import de.hybris.platform.customerticketingc4cintegration.service.C4CCustomerService;
import de.hybris.platform.customerticketingc4cintegration.service.C4CServiceRequestService;
import de.hybris.platform.customerticketingfacades.TicketFacade;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.customerticketingfacades.data.TicketCategory;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;


/**
 * TicketFacade for c4c integration
 */
public class C4CTicketFacadeImpl implements TicketFacade {
    private static final Logger LOGGER = Logger.getLogger(C4CTicketFacadeImpl.class);

    private Converter<ServiceRequestData, TicketData> ticketConverter;
    private Converter<TicketData, ServiceRequestData> defaultC4CTicketConverter;
    private Converter<TicketData, MemoActivity> memoActivityConverter;
    private Converter<MemoActivity, ServiceRequestData> relatedTransactionConverter;
    private SitePropsHolder sitePropsHolder;
    private CustomerFacade customerFacade;
    private StatusData completedStatus;
    private C4CBaseFacade c4cBaseFacade;
    private CustomerService customerService;
    private ModelService modelService;
    private C4CCustomerService c4CCustomerService;
    private C4CServiceRequestService c4CServiceRequestService;
    private SessionService sessionService;

    private static final String COMPLETED = "COMPLETED";
    private static final String CLOSED = "CLOSED";
    private static final int MAX_LIMIT = 255;

    @Nonnull
    @Override
    public TicketData createTicket(final TicketData ticket) {
        Assert.isTrue(StringUtils.isNotBlank(ticket.getSubject()), Customerticketingc4cintegrationConstants.EMPTY_SUBJECT);
        Assert.isTrue(ticket.getSubject().length() <= MAX_LIMIT, Customerticketingc4cintegrationConstants.SUBJECT_EXCEEDS_255_CHARS);
        Assert.isTrue(StringUtils.isNotBlank(ticket.getMessage()), Customerticketingc4cintegrationConstants.EMPTY_MESSAGE);

        try {
            //setting customerId explicitly and override the customerUid set from the addon
				final String customerId = getCustomerFacade().getCurrentCustomer().getCustomerId();
				final String c4cCustomerId = getC4cPartyId(customerId);
            ticket.setCustomerId(c4cCustomerId);

				final MemoActivity memoRequest = memoActivityConverter.convert(ticket);

				final MemoActivity memoResponse = c4CServiceRequestService.createMemoActivity(memoRequest);

            ServiceRequestData serviceRequest = defaultC4CTicketConverter.convert(ticket);

	    serviceRequest = relatedTransactionConverter.convert(memoResponse, serviceRequest);

            // Create Ticket
				final ServiceRequestData responseServiceRequest = c4CServiceRequestService.createServiceRequest(serviceRequest);

	    responseServiceRequest.setMemoActivities(Collections.singletonList(memoResponse));

            return ticketConverter.convert(responseServiceRequest);
			}
			catch (final C4CServiceException e)
			{
            LOGGER.error("Unable to create new ticket", e);
            throw new SystemException("Can't send request", e);
        }

    }

    @Override
    public TicketData updateTicket(final TicketData ticket) {
        Assert.isTrue(StringUtils.isNotBlank(ticket.getMessage()), "Message can't be empty");

        // Get C4C Customer Id
		  final String customerId = getCustomerFacade().getCurrentCustomer().getCustomerId();
        try {
			  final String c4cCustomerId = getC4cPartyId(customerId);
            ticket.setCustomerId(c4cCustomerId);
        }
		  catch (final C4CServiceException ex)
		  {
            LOGGER.error("unable to resolve c4c customer ID", ex);
            return null;
        }

	String ticketObjectId;

	try {
	    ticketObjectId = getTicketObjectId(ticket.getId());
	 }
	 catch (final C4CServiceException ex)
	 {
	    LOGGER.error("unable to resolve ticket objectID.", ex);
	    return null;
	}

        // Fetch remote ticket
		  final TicketData remoteTicket = getTicket(ticket.getId());
		  final String oldStatus = remoteTicket.getStatus().getId();
		  final String newStatus = ticket.getStatus().getId();

        //Throw exception when trying to add message to completed ticket
        if( equalsIgnoreCase(oldStatus, newStatus) && equalsAnyIgnoreCase(newStatus, COMPLETED, CLOSED)){
            throw new IllegalArgumentException("You can not add a message to a completed ticket. Please, reopen the ticket");
        }

        MemoActivity memoResponse = null;

        // Add message to the ticket
        // if ticket is open or
        // moving to completed or closed state
        try {
            if ((equalsIgnoreCase(oldStatus, newStatus) && !equalsAnyIgnoreCase(newStatus, COMPLETED, CLOSED)) ||
                    (!equalsIgnoreCase(oldStatus, newStatus) && equalsAnyIgnoreCase(newStatus, COMPLETED, CLOSED))
            ) {
					final MemoActivity memoRequest = memoActivityConverter.convert(ticket);
		memoResponse = c4CServiceRequestService.createMemoActivity(memoRequest);
		memoResponse.setTicketObjectID(ticketObjectId);

		final ServiceRequestData relatedTransaction = relatedTransactionConverter.convert(memoResponse);
		c4CServiceRequestService
			.updateTicketWithMemoActivity(relatedTransaction.getRelatedTransactions().get(0));

            }
        }
		  catch (final C4CServiceException e)
		  {
	    LOGGER.error("Unable to create memo activity", e);
            return null;
        }

        // Update the ticket status
        try {
			  final ServiceRequestData serviceRequest = getDefaultC4CTicketConverter().convert(ticket);
	    serviceRequest.setObjectID(ticketObjectId);
            c4CServiceRequestService.updateTicket(serviceRequest);
        }
		  catch (final C4CServiceException e)
		  {
            LOGGER.error("Unable to update ticket status", e);
            return null;
        }

	// Add message to the ticket
	// if ticket is reopening
	try {
	    if (!equalsIgnoreCase(oldStatus, newStatus) && equalsAnyIgnoreCase(oldStatus, COMPLETED, CLOSED)) {
			 final MemoActivity memoRequest = memoActivityConverter.convert(ticket);
		memoResponse = c4CServiceRequestService.createMemoActivity(memoRequest);
		memoResponse.setTicketObjectID(ticketObjectId);

		final ServiceRequestData relatedTransaction = relatedTransactionConverter.convert(memoResponse);
		c4CServiceRequestService
			.updateTicketWithMemoActivity(relatedTransaction.getRelatedTransactions().get(0));
	    }
	 }
	 catch (final C4CServiceException e)
	 {
	    LOGGER.error("Unable to create memo activity after ticket reopening", e);
	    return null;
	}

        return getTicket(ticket.getId());
    }

    @Override
    public TicketData getTicket(final String ticketId) {
        validateParameterNotNullStandardMessage("ticketId", ticketId);

        try {
            // Call API
				final String ticketObjectId = getTicketObjectId(ticketId);
				final ServiceRequestData requestData = c4CServiceRequestService.getServiceRequest(ticketObjectId);

            // Check Service Request authorization
				final String customerId = getCustomerFacade().getCurrentCustomer().getCustomerId();
				final String c4cPartyId = getC4cPartyId(customerId);

            if (getSitePropsHolder().isB2C()) {
                if( !equalsIgnoreCase(c4cPartyId, requestData.getBuyerPartyID()) ){
                    LOGGER.error("Service Request Doesn't belong to the user. Received buyer party ID is: "+c4cPartyId);
                    throw new SystemException("Unable Fetch ticket with party id: "+ticketId);
                }
            } else {
                if( !equalsIgnoreCase(c4cPartyId, requestData.getBuyerMainContactPartyID()) ){
                    LOGGER.error("Service Request Doesn't belong to the user. Received buyer main contact party ID is: "+c4cPartyId);
                    throw new SystemException("Unable Fetch ticket with main contact id: "+ticketId);
                }
            }

				final List<MemoActivity> memoActivities = c4CServiceRequestService.getMemoActivities(ticketObjectId);
            requestData.setMemoActivities(memoActivities);

            return ticketConverter.convert(requestData);
        }
		  catch (final C4CServiceException e)
		  {
            throw new SystemException("Unable Fetch ticket with id: "+ticketId);
        }
    }

    @Nonnull
    @Override
    public SearchPageData<TicketData> getTickets(final PageableData pageableData) {
        // Get c4c customer id
		  final String customerId = getCustomerFacade().getCurrentCustomer().getCustomerId();
        String c4cPartyId;
        try {
            c4cPartyId = getC4cPartyId(customerId);
        }
		  catch (final C4CServiceException ex)
		  {
            LOGGER.error("unable to resolve c4c customer ID", ex);
            return getC4cBaseFacade().convertPageData(Collections.emptyList(), getTicketConverter(), pageableData, 0);
        }
        validateParameterNotNullStandardMessage("c4cPartyId", c4cPartyId);

        // Construct url
        final String sorting = StringUtils.defaultIfBlank(pageableData.getSort(), ORDER_DEFAULT_VALUE);
        LOGGER.debug("Sorting: " + sorting);

        // Call API
        try {
            final de.hybris.platform.core.servicelayer.data.SearchPageData<ServiceRequestData> srPageData;
            if (getSitePropsHolder().isB2C()) {
                srPageData = c4CServiceRequestService.getServiceRequestsByBuyerPartyID(c4cPartyId, pageableData.getPageSize(), pageableData.getCurrentPage(), sorting);
            } else {
                srPageData = c4CServiceRequestService.getServiceRequestsByBuyerMainContactPartyID(c4cPartyId, pageableData.getPageSize(), pageableData.getCurrentPage(), sorting);
            }

            final List<ServiceRequestData> results = srPageData.getResults();
	    saveObjectIdInSession(results);
            final int totalResults = (int) srPageData.getPagination().getTotalNumberOfResults();
            return getC4cBaseFacade().convertPageData(results, getTicketConverter(), pageableData, totalResults);
			}
			catch (final C4CServiceException e)
			{
            LOGGER.error("Unable to fetch tickets", e);
        }

        return getC4cBaseFacade().convertPageData(Collections.emptyList(), getTicketConverter(), pageableData, 0);
    }

    /**
     * Returns customer ID in case of B2C and contact ID in case of B2B using commerce customer ID
     * @param customerId commerce customer ID
     * @return customer ID / contact ID from c4c using commerce customer ID
     */
	 private String getC4cPartyId(final String customerId) throws C4CServiceException
	 {
        String c4cBuyerId;

        // Check if c4cBuyerId is available locally
		  final CustomerModel customerModel = customerService.getCustomerByCustomerId(customerId);
        c4cBuyerId = customerModel.getC4cBuyerId();
        if( StringUtils.isNotEmpty(c4cBuyerId) ){
            return c4cBuyerId;
        }

        // Get Buyer ID from API
        if (getSitePropsHolder().isB2C()) {
			  final IndividualCustomer individualCustomer = c4CCustomerService.getIndividualCustomerByExternalId(customerId);
            c4cBuyerId = individualCustomer.getCustomerID();
        } else {
			  final Contact contact = c4CCustomerService.getContactByExternalId(customerId);
            c4cBuyerId = contact.getContactID();
        }


        if( StringUtils.isEmpty(c4cBuyerId) ){
            throw new SystemException("Unable to fetch c4c ID: Empty API response" );
        }

        // Save Model
        customerModel.setC4cBuyerId(c4cBuyerId);
        modelService.save(customerModel);

        return c4cBuyerId;
    }

	 protected void saveObjectIdInSession(final List<ServiceRequestData> serviceRequests)
	 {

		 final Map<String, String> ticketIdObjectIdMap = serviceRequests.stream()
		.collect(Collectors.toMap(ServiceRequestData::getID, ServiceRequestData::getObjectID));
	sessionService.getCurrentSession().setAttribute(TICKETID_OBJECTID_MAP, ticketIdObjectIdMap);

    }

	 protected String getTicketObjectId(final String ticketId) throws C4CServiceException
	 {

	Map<String, String> ticketIdObjectIdMap = sessionService.getCurrentSession()
		.getAttribute(TICKETID_OBJECTID_MAP);

	ticketIdObjectIdMap = ticketIdObjectIdMap != null ? ticketIdObjectIdMap : new HashMap<>();
	String objectId = ticketIdObjectIdMap.get(ticketId);
	if(StringUtils.isBlank(objectId)) {
	    objectId = c4CServiceRequestService.getTicketObjectId(ticketId);
	    ticketIdObjectIdMap.put(ticketId, objectId);
	 }

	sessionService.getCurrentSession().setAttribute(TICKETID_OBJECTID_MAP, ticketIdObjectIdMap);

	return objectId;
    }

    public Converter<ServiceRequestData, TicketData> getTicketConverter() {
        return ticketConverter;
    }

    public void setTicketConverter(final Converter<ServiceRequestData, TicketData> ticketConverter) {
        this.ticketConverter = ticketConverter;
    }

    public Converter<TicketData, ServiceRequestData> getDefaultC4CTicketConverter() {
        return defaultC4CTicketConverter;
    }

    public void setDefaultC4CTicketConverter(final Converter<TicketData, ServiceRequestData> defaultC4CTicketConverter) {
        this.defaultC4CTicketConverter = defaultC4CTicketConverter;
    }

    @Override
    public Map<String, List<TicketAssociatedData>> getAssociatedToObjects() {
        throw new UnsupportedOperationException("It has not been implemeted for C4C yet.....");
    }

    @Override
    public List<TicketCategory> getTicketCategories() {
        throw new UnsupportedOperationException("It has not been implemeted for C4C yet.....");
    }

    protected SitePropsHolder getSitePropsHolder() {
        return sitePropsHolder;
    }

    public void setSitePropsHolder(final SitePropsHolder sitePropsHolder) {
        this.sitePropsHolder = sitePropsHolder;
    }

    protected CustomerFacade getCustomerFacade() {
        return customerFacade;
    }

    public void setCustomerFacade(final CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }

    protected StatusData getCompletedStatus() {
        return completedStatus;
    }

    public void setCompletedStatus(final StatusData completedStatus) {
        this.completedStatus = completedStatus;
    }

    protected C4CBaseFacade getC4cBaseFacade() {
        return c4cBaseFacade;
    }

    public void setC4cBaseFacade(final C4CBaseFacade c4cBaseFacade) {
        this.c4cBaseFacade = c4cBaseFacade;
    }

	 public void setCustomerService(final CustomerService customerService)
	 {
        this.customerService = customerService;
    }

	 public void setModelService(final ModelService modelService)
	 {
        this.modelService = modelService;
    }

	 public void setC4CCustomerService(final C4CCustomerService c4CCustomerService)
	 {
        this.c4CCustomerService = c4CCustomerService;
    }

	 public void setC4CServiceRequestService(final C4CServiceRequestService c4CServiceRequestService)
	 {
        this.c4CServiceRequestService = c4CServiceRequestService;
    }

    public Converter<TicketData, MemoActivity> getMemoActivityConverter() {
	return memoActivityConverter;
    }

	 public void setMemoActivityConverter(final Converter<TicketData, MemoActivity> memoActivityConverter)
	 {
	this.memoActivityConverter = memoActivityConverter;
    }

    public Converter<MemoActivity, ServiceRequestData> getRelatedTransactionConverter() {
	return relatedTransactionConverter;
    }

    public void setRelatedTransactionConverter(
			 final Converter<MemoActivity, ServiceRequestData> relatedTransactionConverter)
	 {
	this.relatedTransactionConverter = relatedTransactionConverter;
    }

    public SessionService getSessionService() {
	return sessionService;
    }

	 public void setSessionService(final SessionService sessionService)
	 {
	this.sessionService = sessionService;
    }
    //</editor-fold>
}
