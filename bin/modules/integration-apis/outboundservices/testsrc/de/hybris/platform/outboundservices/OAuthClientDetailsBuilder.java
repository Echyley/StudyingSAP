/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices;


import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.rules.ExternalResource;

public class OAuthClientDetailsBuilder extends ExternalResource
{
	public static final String DEFAULT_CLIENT_ID = "testOauthClient";
	public static final String DEFAULT_OAUTH_URL = "https://oauth.url.for.test/oauth2/api/v1/token";

	private final Set<String> createdClientDetailIds = new HashSet<>();
	private String clientId;
	private String url;
	private Set<String> scopes = new HashSet<>();

	public static OAuthClientDetailsBuilder oAuthClientDetailsBuilder()
	{
		return new OAuthClientDetailsBuilder();
	}

	public OAuthClientDetailsBuilder withClientId(final String clientId)
	{
		this.clientId = clientId;
		return this;
	}

	public OAuthClientDetailsBuilder withOAuthUrl(final String url)
	{
		this.url = url;
		return this;
	}

	/**
	 * replaces existing scopes with the provided {@link Set} of scopes
	 *
	 * @param scopes Set of provided scopes.
	 * @return {@link OAuthClientDetailsBuilder} with a mutable copy of the provided {@code scopes},
	 * if {@code scopes} is non-null; otherwise an empty mutable set.
	 */
	public OAuthClientDetailsBuilder withScopes(final Set<String> scopes)
	{
		this.scopes = scopes != null ? new HashSet<>(scopes) : new HashSet<>();
		return this;
	}

	/**
	 * adds a scope to the existing scopes
	 *
	 * @param scope a scope to be added
	 * @return {@link OAuthClientDetailsBuilder} after adding the provided scope to the existing scopes, if scope is non-null.
	 */
	public OAuthClientDetailsBuilder withScope(final String scope)
	{
		if (scope != null)
		{
			scopes.add(scope);
		}
		return this;
	}

	public OAuthClientDetailsModel build()
	{
		return oAuthClientDetails(clientId, url, scopes);
	}

	private OAuthClientDetailsModel oAuthClientDetails(final String clientId, final String url, final Set<String> scopes)
	{
		final String clientIdVal = deriveClientId(clientId);
		try
		{
			importImpEx(
					"INSERT_UPDATE OAuthClientDetails; clientId[unique = true]; oAuthUrl         ; scope",
					"                ; " + clientIdVal + "           ; " + deriveUrl(url) + "; " + serializeScope(scopes) + ")");
		}
		catch (final ImpExException ex)
		{
			throw new RuntimeException(ex);
		}

		createdClientDetailIds.add(clientIdVal);
		return getOAuthClientDetailsById(clientIdVal);
	}

	private static String serializeScope(final Set<String> scopes)
	{
		return String.join(",", scopes);
	}

	private static String deriveClientId(final String clientId)
	{
		return StringUtils.isNotBlank(clientId) ? clientId : DEFAULT_CLIENT_ID;
	}

	private static String deriveUrl(final String url)
	{
		return StringUtils.isNotBlank(url) ? url : DEFAULT_OAUTH_URL;
	}

	private static OAuthClientDetailsModel getOAuthClientDetailsById(final String clientId)
	{
		return IntegrationTestUtil.findAny(OAuthClientDetailsModel.class, it -> it.getClientId().equals(clientId))
		                          .orElse(null);
	}

	@Override
	protected void after()
	{
		cleanup();
	}

	public void cleanup()
	{
		createdClientDetailIds.forEach(
				id -> IntegrationTestUtil.remove(OAuthClientDetailsModel.class, it -> it.getClientId().equals(id))
		);
		createdClientDetailIds.clear();
	}
}
