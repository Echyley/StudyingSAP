/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.swaggerintegration.config;

/**
 * Interface for providing swagger API document properties.
 *
 * @since 2211
 */
public interface ApiDocInfoProvider
{
	String getConfigPrefix();

	String getTitle();

	String getLicense();

	String getDescription();

	String getLicenseUrl();

	String getOAuthPasswordName();

	String getOAuthPasswordScope();

	String getOAuthClientCredentialName();

	String getOAuthClientCredentialScope();

	String getOAuthTokenUrl();

	String getVersion();

}
