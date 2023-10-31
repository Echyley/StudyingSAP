/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx;

import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import java.util.HashSet;
import java.util.Set;

public class ExposedOAuthCredentialBuilder
		extends AbstractCredentialBuilder<ExposedOAuthCredentialBuilder, ExposedOAuthCredentialModel>
{
	private static final OAuthClientDetailsModel DEFAULT_CLIENT_DETAILS = OAuthClientDetailsBuilder.oAuthClientDetailsBuilder()
	                                                                                               .build();
	private final Set<OAuthClientDetailsBuilder> createdClientDetails = new HashSet<>();
	private OAuthClientDetailsModel clientDetails;

	public static ExposedOAuthCredentialBuilder exposedOAuthCredentialBuilder()
	{
		return new ExposedOAuthCredentialBuilder();
	}

	public ExposedOAuthCredentialBuilder withClientDetails(final OAuthClientDetailsBuilder builder)
	{
		createdClientDetails.add(builder);
		return withClientDetails(builder.build());
	}

	public ExposedOAuthCredentialBuilder withClientDetails(final OAuthClientDetailsModel details)
	{
		this.clientDetails = details;
		return this;
	}

	@Override
	protected void persist(final String id, final String password)
	{
		try
		{
			importImpEx(
					"INSERT_UPDATE ExposedOAuthCredential; id[unique = true]; oAuthClientDetails         ; password",
					"                                    ; " + id + "              ; " + deriveClientDetails().getPk() + "; " + password);
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
	protected Class<ExposedOAuthCredentialModel> credentialClass()
	{
		return ExposedOAuthCredentialModel.class;
	}

	@Override
	public void cleanup()
	{
		createdClientDetails.forEach(OAuthClientDetailsBuilder::cleanup);
		super.cleanup();
	}
}
