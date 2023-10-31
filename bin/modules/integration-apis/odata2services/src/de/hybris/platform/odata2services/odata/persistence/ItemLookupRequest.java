/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.persistence;

import static de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest.itemConversionRequestBuilder;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.item.IntegrationItem;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.search.ImmutableItemSearchRequest;
import de.hybris.platform.integrationservices.search.ItemSearchRequest;
import de.hybris.platform.integrationservices.search.OrderExpression;
import de.hybris.platform.integrationservices.search.PaginationParameters;
import de.hybris.platform.integrationservices.search.WhereClauseConditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.api.uri.NavigationSegment;

import com.google.common.base.Preconditions;

public class ItemLookupRequest extends CrudRequest implements ItemSearchRequest
{
	private final ImmutableItemSearchRequest itemSearchRequest;
	private List<ArrayList<NavigationPropertySegment>> expand;
	private List<NavigationSegment> navigationSegments;
	private boolean noFilterResult;

	ItemLookupRequest(final ImmutableItemSearchRequest itemSearchRequest, final EdmEntitySet entitySet)
			throws EdmException
	{
		super(entitySet);
		Preconditions.checkArgument(itemSearchRequest.getAcceptLocale() != null, "Accept Locale must be provided");
		this.itemSearchRequest = itemSearchRequest;
	}

	static ItemLookupRequestBuilder itemLookupRequestBuilder()
	{
		return new ItemLookupRequestBuilder();
	}

	@Override
	public IntegrationItem getIntegrationItem()
	{
		return itemSearchRequest.getRequestedItem().orElse(null);
	}

	/**
	 * Retrieves number of items to be skipped in the result set.
	 *
	 * @return requested items will be read from the persistent storage, then, if offset is greater than {@code 0}, the {@code offset} number
	 * of items at the beginning of the result set will be removed.
	 * @deprecated use {@link #getPaginationParameters().getPageSize()} method instead.
	 */
	@Deprecated(since = "2105", forRemoval = true)
	public Integer getSkip()
	{
		return itemSearchRequest.getPaginationParameters().map(PaginationParameters::getPageStart).orElse(0);
	}

	/**
	 * Retrieves number of items to return in the search result.
	 *
	 * @return number of items to be retrieved. The search result may contain fewer number of items or maybe even not items, but it
	 * should not contain more items then the page size specified.
	 * @deprecated use {@link #getPaginationParameters().getPageStart()} method instead.
	 */
	@Deprecated(since = "2105", forRemoval = true)
	public Integer getTop()
	{
		return itemSearchRequest.getPaginationParameters().map(PaginationParameters::getPageSize).orElse(0);
	}

	public List<NavigationSegment> getNavigationSegments()
	{
		return navigationSegments;
	}

	@Override
	public Optional<PaginationParameters> getPaginationParameters()
	{
		return itemSearchRequest.getPaginationParameters();
	}

	@Override
	public @NotNull TypeDescriptor getTypeDescriptor()
	{
		return itemSearchRequest.getTypeDescriptor();
	}

	@Override
	public Optional<IntegrationItem> getRequestedItem()
	{
		return itemSearchRequest.getRequestedItem();
	}

	@Override
	public boolean includeTotalCount()
	{
		return itemSearchRequest.includeTotalCount();
	}

	@Override
	public WhereClauseConditions getFilter()
	{
		return itemSearchRequest.getFilter();
	}

	@Override
	public List<OrderExpression> getOrderBy()
	{
		return itemSearchRequest.getOrderBy();
	}

	/**
	 * @deprecated since 1905.2002-CEP attribute conditions are converted to filter conditions and therefore
	 * can be retrieved by {@link #getFilter()} call.
	 */
	@Deprecated(since = "1905.2002-CEP", forRemoval = true)
	public Pair<String, String> getAttribute()
	{
		return null;
	}

	public boolean isNoFilterResult()
	{
		return noFilterResult;
	}

	@Override
	public boolean isCountOnly()
	{
		return itemSearchRequest.isCountOnly();
	}

	public List<ArrayList<NavigationPropertySegment>> getExpand()
	{
		return expand;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Locale getAcceptLocale()
	{
		return itemSearchRequest.getAcceptLocale();
	}

	void setNavigationSegments(final List<NavigationSegment> segments)
	{
		navigationSegments = segments != null
				? List.copyOf(segments)
				: Collections.emptyList();
	}

	void setExpand(final List<ArrayList<NavigationPropertySegment>> expand)
	{

		this.expand = expand != null
				? List.copyOf(expand.stream()
				                    .map(ArrayList::new)
				                    .collect(Collectors.toUnmodifiableList()))
				: Collections.emptyList();
	}

	void setNoFilterResult(final boolean noFilterResult)
	{
		this.noFilterResult = noFilterResult;
	}

	/**
	 * Builds a conversion request with default conversion options from this item lookup request
	 *
	 * @param item an item to be converted to an ODataEntry
	 * @return an item conversion request built from this item lookup request
	 * @throws EdmException if underlying EDM is invalid. This is not likely because in such case this request could not be built
	 *                      at the first place
	 */
	public ItemConversionRequest toConversionRequest(final ItemModel item) throws EdmException
	{
		return toConversionRequest(item, null);
	}

	/**
	 * Builds a conversion request from this item lookup request
	 *
	 * @param item    an item to be converted to an ODataEntry
	 * @param options conversion options to apply
	 * @return an item conversion request built from this item lookup request
	 * @throws EdmException if underlying EDM is invalid. This is not likely because in such case this request could not be built
	 *                      at the first place
	 */
	public ItemConversionRequest toConversionRequest(final ItemModel item, final ConversionOptions options) throws EdmException
	{
		return itemConversionRequestBuilder()
				.withEntitySet(getEntitySet())
				.withValue(item)
				.withAcceptLocale(getAcceptLocale())
				.withOptions(options)
				.withIntegrationObject(getIntegrationObjectCode())
				.build();
	}
}
