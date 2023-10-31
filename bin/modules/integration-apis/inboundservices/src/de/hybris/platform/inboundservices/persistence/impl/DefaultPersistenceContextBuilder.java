/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.impl;

import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.impl.DefaultPersistenceContext.TypeItems;
import de.hybris.platform.integrationservices.item.IntegrationItem;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DefaultPersistenceContextBuilder
{
	private Locale acceptLocale;
	private Locale contentLocale;
	private IntegrationItem integrationItem;
	private Map<String, TypeItems> items = new HashMap<>();
	private PersistenceContext sourceContext;
	private boolean itemCanBeCreated;
	private boolean replaceAttributes;
	private String prePersistHook;
	private String postPersistHook;

	DefaultPersistenceContextBuilder()
	{
	}

	public DefaultPersistenceContextBuilder withContentLocale(final Locale locale)
	{
		contentLocale = locale;
		return this;
	}

	public DefaultPersistenceContextBuilder withAcceptLocale(final Locale locale)
	{
		acceptLocale = locale;
		return this;
	}

	public DefaultPersistenceContextBuilder withSourceContext(final PersistenceContext sourceContext)
	{
		this.sourceContext = sourceContext;
		return this;
	}

	public DefaultPersistenceContextBuilder withReplaceAttributes(final boolean replaceAttributes)
	{
		this.replaceAttributes = replaceAttributes;
		return this;
	}

	public DefaultPersistenceContextBuilder withItemCanBeCreated(final boolean value)
	{
		itemCanBeCreated = value;
		return this;
	}

	public DefaultPersistenceContextBuilder withIntegrationItem(final IntegrationItem item)
	{
		integrationItem = item;
		return this;
	}

	public DefaultPersistenceContextBuilder withPrePersistHook(final String prePersistHook)
	{
		this.prePersistHook = prePersistHook;
		return this;
	}

	public DefaultPersistenceContextBuilder withPostPersistHook(final String postPersistHook)
	{
		this.postPersistHook = postPersistHook;
		return this;
	}

	private DefaultPersistenceContextBuilder withTypeItems(final Map<String, TypeItems> items)
	{
		this.items = items != null ? items : new HashMap<>();
		return this;
	}

	public final DefaultPersistenceContext build()
	{
		final DefaultPersistenceContext request = new DefaultPersistenceContext(integrationItem, contentLocale, acceptLocale);
		request.items = items;
		request.sourceContext = sourceContext;
		request.itemCanBeCreated = itemCanBeCreated;
		request.replaceAttributes = replaceAttributes;
		request.postPersistHook = postPersistHook;
		request.prePersistHook = prePersistHook;
		return request;
	}

	public DefaultPersistenceContextBuilder from(final DefaultPersistenceContext request)
	{
		return withIntegrationItem(request.getIntegrationItem())
				.withTypeItems(request.items)
				.withSourceContext(request)
				.withItemCanBeCreated(request.isItemCanBeCreated())
				.withAcceptLocale(request.getAcceptLocale())
				.withContentLocale(request.getContentLocale())
				.withReplaceAttributes(request.isReplaceAttributes())
				.withPrePersistHook(request.getPrePersistHook())
				.withPostPersistHook(request.getPostPersistHook());
	}
}
