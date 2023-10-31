/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services;

import de.hybris.platform.cpq.productconfig.services.client.CpqClient;
import de.hybris.platform.cpq.productconfig.services.data.AuthorizationData;


/**
 * Accessing oauth2 tokens and other destination attributes
 */
public interface AuthorizationService
{
	/**
	 * Get data used for initiating a CPQ session with admin scope. This is derived from the destination maintained for
	 * {@link CpqClient} attached to the current base site
	 *
	 * @return Attributes for initiating a CPQ session, including an oAuth2 token
	 */
	AuthorizationData getAuthorizationDataForAdmin();


	/**
	 * Get data used for initiating a CPQ session with a limited scope for a given owner id. This is derived from the
	 * destination maintained for {@link CpqClient} attached to the current base site
	 * 
	 * @param ownerId
	 *           owner id of the client
	 *
	 * @return Attributes for initiating a CPQ session, including an oAuth2 token
	 */
	AuthorizationData getAuthorizationDataForClient(String ownerId);

}
