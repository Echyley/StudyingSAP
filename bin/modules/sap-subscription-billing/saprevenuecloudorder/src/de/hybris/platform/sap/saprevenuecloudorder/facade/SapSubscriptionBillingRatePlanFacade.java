/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.facade;


import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;

/**
 * Facade which provides functionality to manage Subscription RatePlans.
 */
public interface SapSubscriptionBillingRatePlanFacade {

    /**
     * Returns rate plan associated  with the subscription of {@link SubscriptionData}.
     *
     * @param subscriptionData current page
     * @return SubscriptionData
     * @throws SubscriptionServiceException if customer enters wrong inputs
     *
     */
    SubscriptionData getRatePlanForSubscription(final SubscriptionData subscriptionData) throws SubscriptionServiceException;

}
