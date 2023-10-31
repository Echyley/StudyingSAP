/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.service;

import de.hybris.platform.customerticketingc4cintegration.data.Contact;
import de.hybris.platform.customerticketingc4cintegration.data.IndividualCustomer;
import de.hybris.platform.customerticketingc4cintegration.exception.C4CServiceException;

/**
 * Ticket service to interact with C4C
 */
public interface C4CCustomerService {

    /**
     * Get Individual Customer by external ID
     * @param customerId commerce customer id
     * @return Individual Customer by External ID
     * @throws C4CServiceException when error occurs due to invalid data
     */
    IndividualCustomer getIndividualCustomerByExternalId(String customerId) throws C4CServiceException;

    /**
     * Get Contact by external ID
     * @param customerId commerce c4c customerId
     * @return Contact by External ID
     * @throws C4CServiceException when error occurs due to invalid data
     */
    Contact getContactByExternalId(String customerId) throws C4CServiceException;

}
