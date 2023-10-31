/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices;

import de.hybris.platform.apiregistryservices.model.EndpointModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.rules.ExternalResource;

/**
 * Endpoint builder to build an {@link EndpointModel}.
 */
public class EndpointBuilder extends ExternalResource
{

	private static final String DEFAULT_ID = "local-hybris";
	private static final String DEFAULT_VERSION = "unknown";
	private static final String DEFAULT_NAME = "local-hybris";
	private static final String DEFAULT_URL = "https://metadataurlthatdoesnotmatterhere";
	private final Set<Key> createdEndpointKeys = new HashSet<>();
	private String id;
	private String version;
	private String name;
	private String specUrl;

	private EndpointBuilder()
	{
		//Empty private constructor that cannot be called externally.
	}

	public static EndpointBuilder endpointBuilder()
	{
		return new EndpointBuilder();
	}

	public EndpointBuilder withId(final String id)
	{
		this.id = id;
		return this;
	}

	public EndpointBuilder withVersion(final String version)
	{
		this.version = version;
		return this;
	}

	public EndpointBuilder withName(final String name)
	{
		this.name = name;
		return this;
	}

	public EndpointBuilder withSpecUrl(final String specUrl)
	{
		this.specUrl = specUrl;
		return this;
	}

	public EndpointModel build()
	{
		return endpoint();
	}

	private EndpointModel endpoint()
	{
		final String idVal = deriveId();
		final String versionVal = deriveVersion();
		createdEndpointKeys.add(new Key(idVal, versionVal));

		try
		{
			IntegrationTestUtil.importImpEx(
					"INSERT_UPDATE Endpoint; id[unique = true]; version[unique = true]; name           ; specUrl",
					"                      ; " + idVal + "           ; " + versionVal + "           ; " + deriveName() + "; " + deriveUrl());
		}
		catch (final ImpExException ex)
		{
			throw new RuntimeException(ex);
		}
		return getEndpointByIdAndVersion(idVal, versionVal);
	}

	private String deriveId()
	{
		return StringUtils.isNotBlank(id) ? id : DEFAULT_ID;
	}

	private String deriveVersion()
	{
		return StringUtils.isNotBlank(version) ? version : DEFAULT_VERSION;
	}

	private String deriveName()
	{
		return StringUtils.isNotBlank(name) ? name : DEFAULT_NAME;
	}

	private String deriveUrl()
	{
		return StringUtils.isNotBlank(specUrl) ? specUrl : DEFAULT_URL;
	}

	private static EndpointModel getEndpointByIdAndVersion(final String id, final String version)
	{
		return IntegrationTestUtil.findAny(EndpointModel.class, endpoint ->
				endpoint.getId().equals(id) && endpoint.getVersion().equals(version)).orElse(null);
	}

	@Override
	protected void after()
	{
		cleanup();
	}

	public void cleanup()
	{
		createdEndpointKeys.forEach(key -> IntegrationTestUtil.remove(EndpointModel.class,
				endpoint -> endpoint.getId().equals(id) && endpoint.getVersion().equals(version)));
		createdEndpointKeys.clear();
	}

	private static class Key
	{
		private String id;
		private String version;

		public Key(final String i, final String v)
		{
			id = i;
			version = v;
		}

		public String getId()
		{
			return id;
		}

		public void setId(final String id)
		{
			this.id = id;
		}

		public String getVersion()
		{
			return version;
		}

		public void setVersion(final String version)
		{
			this.version = version;
		}
	}

}
