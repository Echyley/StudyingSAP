/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.search;

import de.hybris.platform.integrationservices.item.IntegrationItem;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Immutable value object for representing a search request
 */
public class ImmutableItemSearchRequest implements ItemSearchRequest
{
	@NotNull
	private final TypeDescriptor typeDescriptor;

	IntegrationItem integrationItem;
	PaginationParameters paginationParameters;
	WhereClauseConditions filter;
	List<OrderExpression> orderBy;
	Locale locale;
	boolean totalCount;
	boolean countOnly;

	ImmutableItemSearchRequest(final TypeDescriptor type)
	{
		Preconditions.checkArgument(type != null, "TypeDescriptor must be specified for a ImmutableItemSearchRequest");
		typeDescriptor = type;
	}

	/**
	 * Instantiates a new builder for creating instances of an {@link ImmutableItemSearchRequest}
	 *
	 * @return the {@link ItemSearchRequestBuilder}
	 */
	public static ItemSearchRequestBuilder itemSearchRequestBuilder()
	{
		return new ItemSearchRequestBuilder();
	}

	/**
	 * Instantiates a new builder for creating instances of an {@link ImmutableItemSearchRequest} from
	 * an existing {@link ItemSearchRequest}
	 *
	 * @param request An existing request to build from
	 * @return the {@link ItemSearchRequestBuilder}
	 */
	public static ItemSearchRequestBuilder itemSearchRequestBuilder(final ItemSearchRequest request)
	{
		Preconditions.checkArgument(request != null, "ItemSearchRequest cannot be null");
		return new ItemSearchRequestBuilder(request);
	}

	@Override
	public Optional<PaginationParameters> getPaginationParameters()
	{
		return Optional.ofNullable(paginationParameters);
	}

	@Override
	public @NotNull TypeDescriptor getTypeDescriptor()
	{
		return typeDescriptor;
	}

	@Override
	public Optional<IntegrationItem> getRequestedItem()
	{
		return Optional.ofNullable(integrationItem);
	}

	@Override
	public WhereClauseConditions getFilter()
	{
		return filter;
	}

	@Override
	public List<OrderExpression> getOrderBy()
	{
		return orderBy != null ? Collections.unmodifiableList(orderBy) : Collections.emptyList();
	}

	@Override
	public Locale getAcceptLocale()
	{
		return locale;
	}

	@Override
	public boolean includeTotalCount()
	{
		return totalCount;
	}

	@Override
	public boolean isCountOnly()
	{
		return countOnly;
	}
}
