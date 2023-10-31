/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.service;

import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customerticketingc4cintegration.data.MemoActivity;
import de.hybris.platform.customerticketingc4cintegration.data.Note;
import de.hybris.platform.customerticketingc4cintegration.data.RelatedTransaction;
import de.hybris.platform.customerticketingc4cintegration.data.ServiceRequestData;
import de.hybris.platform.customerticketingc4cintegration.exception.C4CServiceException;

import java.util.List;

/**
 * C4C Ticket Service
 */
public interface C4CServiceRequestService {

    /**
     * Update Ticket
     * @param serviceRequest service request to update
     * @throws C4CServiceException if data is functionally incorrect
     */
    void updateTicket(ServiceRequestData serviceRequest) throws C4CServiceException;

    /**
     * Create a new note
     * @param note note to be added
     * @throws C4CServiceException if data is functionally incorrect
     */
    void createNote(Note note) throws C4CServiceException;

    /**
     * Create a new memo activity
     *
     * @param memoActivityRequest memo to be created
     * @return created memo
     * @throws C4CServiceException if data is functionally incorrect
     */
    MemoActivity createMemoActivity(MemoActivity memoActivityRequest) throws C4CServiceException;

    /**
     * Update ticket with memo activity
     * 
     * @param relatedTransactionRequest request
     * @throws C4CServiceException if data is functionally incorrect
     */
    void updateTicketWithMemoActivity(RelatedTransaction relatedTransactionRequest) throws C4CServiceException;

    /**
     * Get memo activities by ticket ID
     * 
     * @param ticketId ticket id
     * @return list of memo activities
     * @throws C4CServiceException if data is functionally incorrect
     */
    List<MemoActivity> getMemoActivities(String ticketId) throws C4CServiceException;

    /**
     * Returns a Service Request by ID
     * @param id service request id
     * @return service request
     * @throws C4CServiceException if data is functionally incorrect
     */
    ServiceRequestData getServiceRequest(String id) throws C4CServiceException;

    /**
     * Creates new service request
     * @param serviceRequest service request to be created
     * @return created service request
     * @throws C4CServiceException when error occurs due to invalid data
     */
    ServiceRequestData createServiceRequest(ServiceRequestData serviceRequest) throws C4CServiceException;

    /**
     * Gets service requests by buyer party id
     * @param buyerPartyID buyer party id
     * @param pageSize page size
     * @param currentPage current page
     * @param sorting sorting
     * @return Search Page of service request
     * @throws C4CServiceException when error occurs due to invalid data
     */
    SearchPageData<ServiceRequestData> getServiceRequestsByBuyerPartyID(String buyerPartyID, int pageSize, int currentPage, String sorting) throws C4CServiceException;

    /**
     * Gets service requests by buyer main contact party id
     * @param buyerMainContactPartyID buyer main contact party id
     * @param pageSize page size
     * @param currentPage current page
     * @param sorting sorting
     * @return Search Page of service request
     * @throws C4CServiceException when error occurs due to invalid data
     */
    SearchPageData<ServiceRequestData> getServiceRequestsByBuyerMainContactPartyID(String buyerMainContactPartyID, int pageSize, int currentPage, String sorting) throws C4CServiceException;

    /**
     * Gets ticket ObjectID for a ticketId
     * 
     * @param ticketId ticket Id
     * @return Object ID
     */
    String getTicketObjectId(String ticketId) throws C4CServiceException;
}
