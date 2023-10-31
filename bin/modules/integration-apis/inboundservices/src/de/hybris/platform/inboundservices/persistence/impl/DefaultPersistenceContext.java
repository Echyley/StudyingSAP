/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.inboundservices.persistence.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.integrationservices.item.IntegrationItem;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.search.ItemSearchRequest;
import de.hybris.platform.integrationservices.search.ItemSearchRequestBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Context which contains details about the item(s) for persistence.
 */
public class DefaultPersistenceContext implements PersistenceContext
{
	private final Locale acceptLocale;
	private final Locale contentLocale;
	private final IntegrationItem integrationItem;
	Map<String, TypeItems> items = new HashMap<>();
	PersistenceContext sourceContext;
	boolean itemCanBeCreated;
	boolean replaceAttributes;
	String postPersistHook;
	String prePersistHook;

	DefaultPersistenceContext(final IntegrationItem item, final Locale content, final Locale accept)
	{
		Preconditions.checkArgument(accept != null, "Accept Locale cannot be null");
		Preconditions.checkArgument(content != null, "Content Locale cannot be null");
		Preconditions.checkArgument(item != null, "Integration Item cannot be null");

		integrationItem = item;
		contentLocale = content;
		acceptLocale = accept;
	}

	/**
	 * Instantiates a new builder for creating instances of a {@link PersistenceContext}
	 *
	 * @return the {@link DefaultPersistenceContextBuilder}
	 */
	public static DefaultPersistenceContextBuilder persistenceContextBuilder()
	{
		return new DefaultPersistenceContextBuilder();
	}

	@Nonnull
	@Override
	public IntegrationItem getIntegrationItem()
	{
		return integrationItem;
	}

	@Override
	public Optional<ItemModel> getContextItem()
	{
		final String type = getItemCode();
		return Optional.ofNullable(items.get(type))
		               .flatMap(typeItems -> typeItems.getItemByKeyString(getIntegrationKey()));
	}

	@Override
	public void putItem(final ItemModel item)
	{
		items.computeIfAbsent(getItemCode(), k -> new TypeItems())
		     .addItem(getIntegrationKey(), item);
	}

	private String getIntegrationKey()
	{
		return getIntegrationItem().getIntegrationKey();
	}

	@NotNull
	private String getItemCode()
	{
		return getIntegrationItem().getItemType().getItemCode();
	}

	@Override
	public PersistenceContext getReferencedContext(final TypeAttributeDescriptor attribute)
	{
		final IntegrationItem nestedItem = getIntegrationItem().getReferencedItem(attribute);
		return createItemPersistenceContext(attribute, nestedItem);
	}

	@Override
	public Collection<PersistenceContext> getReferencedContexts(final TypeAttributeDescriptor attribute)
	{
		return this.getIntegrationItem()
		           .getReferencedItems(attribute)
		           .stream()
		           .map(currentIntegrationItem -> createItemPersistenceContext(attribute, currentIntegrationItem))
		           .toList();
	}

	private PersistenceContext createItemPersistenceContext(final TypeAttributeDescriptor attribute, final IntegrationItem item)
	{
		return persistenceContextBuilder().from(this)
		                                  .withIntegrationItem(item)
		                                  .withItemCanBeCreated(attribute.isAutoCreate() || attribute.isPartOf())
		                                  .build();
	}

	@Override
	public Optional<PersistenceContext> getSourceContext()
	{
		return Optional.ofNullable(sourceContext);
	}

	@Nonnull
	@Override
	public PersistenceContext getRootContext()
	{
		return findRootContext(this);
	}

	private PersistenceContext findRootContext(final PersistenceContext context)
	{
		final Optional<PersistenceContext> parent = context.getSourceContext();
		return parent.isEmpty()
				? context
				: findRootContext(parent.get());
	}

	@Nonnull
	@Override
	public ItemSearchRequest toItemSearchRequest()
	{
		final ItemSearchRequestBuilder searchRequestBuilder = new ItemSearchRequestBuilder();
		return searchRequestBuilder
				.withIntegrationItem(getIntegrationItem())
				.withLocale(getAcceptLocale())
				.build();
	}

	@Override
	public boolean isItemCanBeCreated()
	{
		return itemCanBeCreated;
	}

	@Nonnull
	@Override
	public Locale getAcceptLocale()
	{
		return acceptLocale;
	}

	@Nonnull
	@Override
	public Locale getContentLocale()
	{
		return contentLocale;
	}

	@Override
	public boolean isReplaceAttributes()
	{
		return replaceAttributes;
	}

	@Override
	public String getPrePersistHook()
	{
		return prePersistHook;
	}

	@Override
	public String getPostPersistHook()
	{
		return postPersistHook;
	}

	static class TypeItems
	{
		private final Map<String, ItemModel> itemsByKeys;

		public TypeItems()
		{
			this(new HashMap<>());
		}

		public TypeItems(final Map<String, ItemModel> items)
		{
			itemsByKeys = items;
		}

		public Optional<ItemModel> getItemByKeyString(final String key)
		{
			return Optional.ofNullable(itemsByKeys.get(key));
		}

		public void addItem(final String key, final ItemModel item)
		{
			itemsByKeys.put(key, item);
		}

		public boolean contains(final String key, final ItemModel item)
		{
			return getItemByKeyString(key)
					.filter(i -> Objects.equals(i, item))
					.isPresent();
		}
	}
}
