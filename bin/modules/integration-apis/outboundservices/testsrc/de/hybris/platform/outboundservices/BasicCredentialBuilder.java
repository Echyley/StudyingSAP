/*
 *  Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx;

import de.hybris.platform.apiregistryservices.model.BasicCredentialModel;
import de.hybris.platform.impex.jalo.ImpExException;

import org.apache.commons.lang.StringUtils;

public class BasicCredentialBuilder extends AbstractCredentialBuilder<BasicCredentialBuilder, BasicCredentialModel>
{
	private static final String DEFAULT_USERNAME = "testUser";

	private String username;

	public static BasicCredentialBuilder basicCredentialBuilder()
	{
		return new BasicCredentialBuilder();
	}

	public BasicCredentialBuilder withUsername(final String username)
	{
		this.username = username;
		return this;
	}

	@Override
	protected void persist(final String id, final String password)
	{
		try
		{
			importImpEx(
					"INSERT_UPDATE BasicCredential; id[unique = true]; username           ; password",
					"                             ; " + id + "              ; " + deriveUsername() + "; " + password);
		}
		catch (final ImpExException e)
		{
			throw new RuntimeException(e);
		}
	}

	private String deriveUsername()
	{
		return StringUtils.isNotBlank(username) ? username : DEFAULT_USERNAME;
	}

	@Override
	protected Class<BasicCredentialModel> credentialClass()
	{
		return BasicCredentialModel.class;
	}
}
