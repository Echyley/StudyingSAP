/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.services;

import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * Service layer interface for Credentials.
 */
public interface CredentialService
{
	/**
	 * Find the list of ExposedOAuthCredentials for specific clientId
	 *
	 * @param clientId
	 *           The clientId of OAuthClientDetails
	 * @return a List of ExposedOAuthCredentials by the clientId
	 */
	List<ExposedOAuthCredentialModel> getCredentialsByClientId(String clientId);

	/**
	 * Method that :
	 * <ul>
	 * <li><i>create OAuthClientDetails using provided parameters</i></li>
	 * <li><i>update ExposedOAuthCredentialModels with newly created OAuthClientDetails</i></li>
	 * <li><i>schedule deletion of old OAuthClientDetails after the gracePeriod</i></li>
	 * </ul>
	 *
	 * @param credentials
	 *           a Credentials to be updated
	 * @param clientId
	 *           an ID for new OAuthClientDetails
	 * @param clientSecret
	 *           a secret for new OAuthClientDetails
	 * @param gracePeriod
	 *           a number of milliseconds after which the old OAuthClientDetails disappear
	 */
	void resetCredentials(List<ExposedOAuthCredentialModel> credentials, String clientId, String clientSecret,
			Integer gracePeriod);

	/**
	 * Get all client details that can be deleted
	 *
	 * @param credentials set of {@link ExposedOAuthCredentialModel}
	 * @return set of {@link OAuthClientDetailsModel}
	 */
	default Set<OAuthClientDetailsModel> getDeletableClientDetailsByCredentials(final Set<ExposedOAuthCredentialModel> credentials)
	{
		return Collections.emptySet();
	}
}
