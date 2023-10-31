/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence;

import de.hybris.platform.inboundservices.persistence.impl.DefaultPersistenceContext;
import de.hybris.platform.inboundservices.persistence.impl.DefaultPersistenceContextBuilder;
import de.hybris.platform.integrationservices.item.IntegrationItem;

import java.net.URI;
import java.util.Locale;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;

public class StorageRequestBuilder
{
	private EdmEntitySet entitySet;
	private URI serviceRoot;
	private URI requestUri;
	private String contentType;
	private String integrationObjectCode;
	private String integrationKey;
	private final DefaultPersistenceContextBuilder contextBuilder;

	StorageRequestBuilder()
	{
		this(DefaultPersistenceContext.persistenceContextBuilder());
	}

	private StorageRequestBuilder(final DefaultPersistenceContextBuilder builder)
	{
		contextBuilder = builder;
	}

	public StorageRequestBuilder withEntitySet(final EdmEntitySet set)
	{
		entitySet = set;
		return this;
	}

	public StorageRequestBuilder withServiceRoot(final URI uri)
	{
		serviceRoot = uri;
		return this;
	}

	public StorageRequestBuilder withContentType(final String mimeType)
	{
		contentType = mimeType;
		return this;
	}

	public StorageRequestBuilder withRequestUri(final URI uri)
	{
		requestUri = uri;
		return this;
	}

	public StorageRequestBuilder withIntegrationObject(final String code)
	{
		integrationObjectCode = code;
		return this;
	}

	public StorageRequestBuilder withPrePersistHook(final String hook)
	{
		contextBuilder.withPrePersistHook(hook);
		return this;
	}

	public StorageRequestBuilder withPostPersistHook(final String hook)
	{
		contextBuilder.withPostPersistHook(hook);
		return this;
	}

	public StorageRequestBuilder withIntegrationItem(final IntegrationItem item)
	{
		contextBuilder.withIntegrationItem(item);
		return this;
	}

	public StorageRequestBuilder withAcceptLocale(final Locale locale)
	{
		contextBuilder.withAcceptLocale(locale);
		return this;
	}

	public StorageRequestBuilder withContentLocale(final Locale locale)
	{
		contextBuilder.withContentLocale(locale);
		return this;
	}

	public StorageRequestBuilder withReplaceAttributes(final boolean flag)
	{
		contextBuilder.withReplaceAttributes(flag);
		return this;
	}

	public StorageRequestBuilder withItemCanBeCreated(final boolean value)
	{
		contextBuilder.withItemCanBeCreated(value);
		return this;
	}

	public StorageRequestBuilder from(final StorageRequest request)
	{
		final var ctxBuilder = contextBuilder.from(request.getPersistenceContext());
		return new StorageRequestBuilder(ctxBuilder)
				.withEntitySet(request.getEntitySet())
				.withIntegrationObject(request.getIntegrationObjectCode())
				.withPrePersistHook(request.getPrePersistHook())
				.withPostPersistHook(request.getPostPersistHook());
	}

	public final StorageRequest build() throws EdmException
	{
		final StorageRequest request = new StorageRequest(contextBuilder.build(), entitySet);
		request.setIntegrationObjectCode(integrationObjectCode);
		request.setIntegrationKey(integrationKey);
		request.setServiceRoot(serviceRoot);
		request.setRequestUri(requestUri);
		request.setContentType(contentType);
		return request;
	}
}
