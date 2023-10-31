/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bfacades.webhook;

import de.hybris.platform.gigya.gigyab2bfacades.data.GigyaWebHookRequest;

/**
 * Facade to carry out gigya login functionality
 */
public interface GigyaB2BWebhookFacade {

	/**
	 * Receives the CDC Webhook events and schedules a task to create / update B2B users
	 * @param webhookRequest 
	 * 				The incoming CDC Webhook Request
	 */
	void receiveGigyaWebHookEvents(final GigyaWebHookRequest webhookRequest);
	
	
	/**
	 * Validate the JWT Token for CDC WebHook
	 * @param jwtToken
	 * 			JWT Token
	 * @param baseSiteId
	 * 			Base site identifier
	 * @param payloadHash
	 * 			Payload Hash String
	 * @return boolean, true if the JWT is valid
	 */
	boolean validateWebHookJWTToken(final String jwtToken, final String baseSiteId, final String payloadHash);
}
