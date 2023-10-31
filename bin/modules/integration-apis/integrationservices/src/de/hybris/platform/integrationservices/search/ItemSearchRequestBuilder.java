/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.search;

import de.hybris.platform.integrationservices.item.IntegrationItem;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.List;
import java.util.Locale;

/**
 * A builder for {@link ImmutableItemSearchRequest}
 */
public class ItemSearchRequestBuilder
{
	private TypeDescriptor typeDescriptor;
	private IntegrationItem integrationItem;
	private PaginationParameters pageParameters;
	private WhereClauseConditions whereClause;
	private List<OrderExpression> orderDiscipline;
	private Locale locale;
	private boolean totalCount;
	private boolean countOnly;

	public ItemSearchRequestBuilder()
	{
	}

	ItemSearchRequestBuilder(final ItemSearchRequest request)
	{
		request.getRequestedItem().ifPresent(this::setIntegrationItem);
		itemType(request.getTypeDescriptor());
		filter(request.getFilter());
		orderBy(request.getOrderBy());
		setLocale(request.getAcceptLocale());
		countOnly(request.isCountOnly());
		totalCount(request.includeTotalCount());
		request.getPaginationParameters().ifPresent(this::setPageParameters);
	}

	private ItemSearchRequestBuilder setIntegrationItem(final IntegrationItem item)
	{
		final var type = item != null ? item.getItemType() : null;
		integrationItem = item;
		return itemType(type);
	}

	public ItemSearchRequestBuilder withIntegrationItem(final IntegrationItem item)
	{
		return setIntegrationItem(item);
	}

	private ItemSearchRequestBuilder itemType(final TypeDescriptor type)
	{
		typeDescriptor = type;
		return this;
	}

	public ItemSearchRequestBuilder withItemType(final TypeDescriptor type)
	{
		return itemType(type);
	}

	private ItemSearchRequestBuilder filter(final WhereClauseConditions filter)
	{
		whereClause = filter;
		return this;
	}

	public ItemSearchRequestBuilder withFilter(final WhereClauseConditions filter)
	{
		return filter(filter);
	}

	private ItemSearchRequestBuilder orderBy(final List<OrderExpression> orderByExpressions)
	{
		orderDiscipline = orderByExpressions;
		return this;
	}

	public ItemSearchRequestBuilder withOrderBy(final List<OrderExpression> orderByExpressions)
	{
		return orderBy(orderByExpressions);
	}

	private ItemSearchRequestBuilder setLocale(final Locale locale)
	{
		this.locale = locale;
		return this;
	}

	public ItemSearchRequestBuilder withLocale(final Locale locale)
	{
		return setLocale(locale);
	}

	private ItemSearchRequestBuilder countOnly(final boolean value)
	{
		countOnly = value;
		return totalCount(value);
	}

	public ItemSearchRequestBuilder withCountOnly()
	{
		return countOnly(true);
	}

	public ItemSearchRequestBuilder withNoCountOnly()
	{
		return countOnly(false);
	}

	private ItemSearchRequestBuilder totalCount(final boolean value)
	{
		totalCount = value;
		return this;
	}

	public ItemSearchRequestBuilder withTotalCount()
	{
		return totalCount(true);
	}

	public ItemSearchRequestBuilder withNoTotalCount() {
		return totalCount(false);
	}

	private ItemSearchRequestBuilder setPageParameters(final PaginationParameters params)
	{
		pageParameters = params;
		return this;
	}

	public ItemSearchRequestBuilder withPageParameters(final PaginationParameters params)
	{
		return setPageParameters(params);
	}

	public ImmutableItemSearchRequest build()
	{
		final var request = new ImmutableItemSearchRequest(typeDescriptor);
		request.integrationItem = integrationItem;
		request.paginationParameters = pageParameters;
		request.filter = whereClause;
		request.orderBy = orderDiscipline;
		request.totalCount = totalCount;
		request.countOnly = countOnly;
		request.locale = locale;
		return request;
	}
}
