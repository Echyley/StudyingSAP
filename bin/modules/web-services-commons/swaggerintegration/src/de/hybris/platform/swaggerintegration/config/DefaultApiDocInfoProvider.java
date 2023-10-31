/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.swaggerintegration.config;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.swagger.ApiDocInfo;

/**
 * Default implementation of {@link ApiDocInfoProvider}. API document properties are fetched by {@link ConfigurationService#getConfiguration()}
 *
 * @since 2211
 */
public class DefaultApiDocInfoProvider implements ApiDocInfoProvider
{
	private static final String PASSWORD_AUTHORIZATION_SCOPE_PROPERTY = "api.oauth.password.scope";
	private static final String CLIENT_CREDENTIAL_AUTHORIZATION_SCOPE_PROPERTY = "api.oauth.clientCredentials.scope";
	private static final String OAUTH_TOKEN_URL_PROPERTY = "api.oauth.tokenUrl";
	private static final String DESC_PROPERTY = "api.description";
	private static final String TITLE_PROPERTY = "api.title";
	private static final String VERSION_PROPERTY = "api.version";
	private static final String LICENSE_PROPERTY = "api.license";
	private static final String LICENSE_URL_PROPERTY = "api.license.url";

	private static final String PASSWORD_AUTHORIZATION_NAME = "oauth2_Password";
	private static final String CLIENT_CREDENTIAL_AUTHORIZATION_NAME = "oauth2_client_credentials";
	private static final String DEFAULT_OAUTH_TOKEN_URL = "/authorizationserver/oauth/token";

	private final ConfigurationService configurationService;

	private final ApiDocInfo apiDocInfo;

	public DefaultApiDocInfoProvider(final ApiDocInfo apiDocInfo, final ConfigurationService configurationService)
	{
		this.apiDocInfo = apiDocInfo;
		this.configurationService = configurationService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Override
	public String getConfigPrefix()
	{
		return apiDocInfo.getConfigPrefix();
	}

	@Override
	public String getTitle()
	{
		return getPropertyValue(TITLE_PROPERTY);
	}

	@Override
	public String getLicense()
	{
		return getPropertyValue(LICENSE_PROPERTY);
	}

	@Override
	public String getDescription()
	{
		return getPropertyValue(DESC_PROPERTY);
	}

	@Override
	public String getLicenseUrl()
	{
		return getPropertyValue(LICENSE_URL_PROPERTY);
	}

	@Override
	public String getOAuthPasswordName()
	{
		return PASSWORD_AUTHORIZATION_NAME;
	}

	@Override
	public String getOAuthPasswordScope()
	{
		return getPropertyValue(PASSWORD_AUTHORIZATION_SCOPE_PROPERTY);
	}

	@Override
	public String getOAuthClientCredentialName()
	{
		return CLIENT_CREDENTIAL_AUTHORIZATION_NAME;
	}

	@Override
	public String getOAuthClientCredentialScope()
	{
		return getPropertyValue(CLIENT_CREDENTIAL_AUTHORIZATION_SCOPE_PROPERTY);
	}

	@Override
	public String getOAuthTokenUrl()
	{
		return getPropertyValue(OAUTH_TOKEN_URL_PROPERTY, DEFAULT_OAUTH_TOKEN_URL);
	}

	@Override
	public String getVersion()
	{
		return getPropertyValue(VERSION_PROPERTY);
	}

	protected String getPropertyValue(final String key, final String defaultValue)
	{
		String propKey = getConfigPrefix();
		if (!propKey.endsWith("."))
		{
			propKey = propKey + ".";
		}
		propKey = propKey + key;
		return getConfigurationService().getConfiguration().getString(propKey, defaultValue);
	}

	protected String getPropertyValue(final String key)
	{
		return getPropertyValue(key, "");
	}
}
