/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.common.consent;



/**
 * Consent service for yMKT integration.
 */
public interface YmktConsentService
{

	/**
	 * Consent verification for current session user.
	 *
	 * @param consentId
	 *           Property prefix of yMKT consent Id.
	 * @return <code>true</code> if the user has consent, <code>false</code> otherwise.
	 */
	boolean getUserConsent(String consentId);

	/**
	 * Consent verification for provided customer.
	 *
	 * @param customerId
	 *           Value of <code>Customer.customerID</code>.
	 * @param consentId
	 *           Value of yMKT <code>ConsentTemplate.id</code>.
	 * @return <code>true</code> if the customer has consent, <code>false</code> otherwise.
	 */
	boolean getUserConsent(String customerId, String consentId);

}
