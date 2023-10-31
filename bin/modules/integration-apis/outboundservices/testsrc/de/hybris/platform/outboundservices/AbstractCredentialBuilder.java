/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices;

import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.rules.ExternalResource;

/**
 * Abstract credential builder.
 *
 * @param <BUILDER>    a builder that extends the {@link AbstractCredentialBuilder}
 * @param <CREDENTIAL> a model that extends {@link AbstractCredentialModel }
 */
public abstract class AbstractCredentialBuilder<BUILDER, CREDENTIAL extends AbstractCredentialModel> extends ExternalResource
{
	private static final String DEFAULT_ID = "testOauthCredential";
	private static final String DEFAULT_PASSWORD = "testSecret";

	private final Set<String> createdCredentialIDs = new HashSet<>();
	private String id;
	private String password;

	public AbstractCredentialBuilder<BUILDER, CREDENTIAL> withId(final String id)
	{
		this.id = id;
		return this;
	}

	public AbstractCredentialBuilder<BUILDER, CREDENTIAL> withPassword(final String password)
	{
		this.password = password;
		return this;
	}

	public CREDENTIAL build()
	{
		final String credentialId = deriveId();
		persist(credentialId, derivePassword());
		createdCredentialIDs.add(credentialId);
		return getCredentialById(credentialId);
	}

	private String deriveId()
	{
		return StringUtils.isNotBlank(id) ? id : DEFAULT_ID;
	}

	private String derivePassword()
	{
		return StringUtils.isNotBlank(password) ? password : DEFAULT_PASSWORD;
	}

	private CREDENTIAL getCredentialById(final String id)
	{
		return IntegrationTestUtil.findAny(credentialClass(), credential -> credential.getId().equals(id))
		                          .orElse(null);
	}

	@Override
	protected void after()
	{
		cleanup();
	}

	public void cleanup()
	{
		createdCredentialIDs.forEach(
				id -> IntegrationTestUtil.remove(credentialClass(), credential -> credential.getId().equals(id)));
		createdCredentialIDs.clear();
	}

	protected abstract void persist(String id, String pwd);

	protected abstract Class<CREDENTIAL> credentialClass();
}
