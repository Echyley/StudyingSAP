/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service;

import de.hybris.platform.sap.saprevenuecloudorder.pojo.rateplan.v2.RatePlanViewBatchRequest;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.rateplanResponse.v2.RatePlanViewBatchResponse;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;

/**
 * This service connects to Sap Subscription Billing System and performs Subscription rateplan related actions
 */
public interface SapSubscriptionBillingRatePlanService {

    /**
     * Fetch rate plans based on pricing parameters
     *
     * @param ratePlanViewBatchRequest pricing parameters to fetch the corresponding rate plans
     *
     * @return Response of batch process {@link RatePlanViewBatchResponse}
     *
     * @throws SubscriptionServiceException if error occurs due to input data
     */
    RatePlanViewBatchResponse getRatePlanViewBatchRequest( RatePlanViewBatchRequest ratePlanViewBatchRequest) throws SubscriptionServiceException;
}
