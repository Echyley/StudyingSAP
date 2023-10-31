/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx;

import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import java.util.HashSet;
import java.util.Set;

public class ConsumedOAuthCredentialBuilder
		extends AbstractCredentialBuilder<ConsumedOAuthCredentialBuilder, ConsumedOAuthCredentialModel>
{
	private static final OAuthClientDetailsModel DEFAULT_CLIENT_DETAILS = OAuthClientDetailsBuilder.oAuthClientDetailsBuilder()
	                                                                                               .build();
	private final Set<OAuthClientDetailsBuilder> createdClientDetails = new HashSet<>();
	private OAuthClientDetailsModel clientDetails;

	public static ConsumedOAuthCredentialBuilder consumedOAuthCredentialBuilder()
	{
		return new ConsumedOAuthCredentialBuilder();
	}

	public ConsumedOAuthCredentialBuilder withClientDetails(final OAuthClientDetailsBuilder builder)
	{
		createdClientDetails.add(builder);
		return withClientDetails(builder.build());
	}

	public ConsumedOAuthCredentialBuilder withClientDetails(final OAuthClientDetailsModel details)
	{
		this.clientDetails = details;
		return this;
	}

	@Override
	protected void persist(final String id, final String password)
	{
		final OAuthClientDetailsModel details = deriveClientDetails();
		try
		{
			importImpEx(
					"INSERT_UPDATE ConsumedOAuthCredential; id[unique = true]; clientId     ; oAuthUrl    ; clientSecret",
					"                  ; " + id + "; " + details.getClientId() + "; " + details.getOAuthUrl() + "; " + password);
		}
		catch (final ImpExException e)
		{
			throw new RuntimeException(e);
		}
	}

	private OAuthClientDetailsModel deriveClientDetails()
	{
		return clientDetails != null ? clientDetails : DEFAULT_CLIENT_DETAILS;
	}

	@Override
	protected Class<ConsumedOAuthCredentialModel> credentialClass()
	{
		return ConsumedOAuthCredentialModel.class;
	}

	@Override
	public void cleanup()
	{
		createdClientDetails.forEach(OAuthClientDetailsBuilder::cleanup);
		super.cleanup();
	}
}
