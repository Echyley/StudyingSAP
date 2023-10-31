/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence;

import de.hybris.platform.integrationservices.item.IntegrationItem;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.search.ImmutableItemSearchRequest;
import de.hybris.platform.integrationservices.search.ItemSearchRequestBuilder;
import de.hybris.platform.integrationservices.search.OrderExpression;
import de.hybris.platform.integrationservices.search.PaginationParameters;
import de.hybris.platform.integrationservices.search.WhereClauseConditions;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment;
import org.apache.olingo.odata2.api.uri.NavigationSegment;

class ItemLookupRequestBuilder
{
	private List<NavigationSegment> navigationSegments;
	private List<ArrayList<NavigationPropertySegment>> expand;
	private boolean noFilterResult;
	private PaginationParameters pageParameters;
	private String integrationKey;
	private ODataEntry oDataEntry;
	private URI serviceRoot;
	private String contentType;
	private URI requestUri;
	private EdmEntitySet entitySet;
	private String integrationObjectCode;
	private final ItemSearchRequestBuilder itemSearchRequestBuilder;

	ItemLookupRequestBuilder()
	{
		this(ImmutableItemSearchRequest.itemSearchRequestBuilder());
	}

	private ItemLookupRequestBuilder(final ItemSearchRequestBuilder itemSearchRequestBuilder)
	{
		this.itemSearchRequestBuilder = itemSearchRequestBuilder;
	}

	ItemLookupRequestBuilder withIntegrationItem(final IntegrationItem item)
	{
		itemSearchRequestBuilder.withIntegrationItem(item);
		return this;
	}

	ItemLookupRequestBuilder withTypeDescriptor(final TypeDescriptor typeDescriptor)
	{
		itemSearchRequestBuilder.withItemType(typeDescriptor);
		return this;
	}

	ItemLookupRequestBuilder withFilter(final WhereClauseConditions filter)
	{
		itemSearchRequestBuilder.withFilter(filter);
		return this;
	}

	ItemLookupRequestBuilder withOrderBy(final List<OrderExpression> orderBy)
	{
		itemSearchRequestBuilder.withOrderBy(orderBy);
		return this;
	}

	ItemLookupRequestBuilder withLocale(final Locale locale)
	{
		itemSearchRequestBuilder.withLocale(locale);
		return this;
	}

	ItemLookupRequestBuilder withCountOnly(final boolean countOnly)
	{
		if (countOnly)
		{
			itemSearchRequestBuilder.withCountOnly();
		}
		return this;
	}

	ItemLookupRequestBuilder withCount(final boolean count)
	{
		if (count)
		{
			itemSearchRequestBuilder.withTotalCount();
		}
		return this;
	}

	ItemLookupRequestBuilder withAcceptLocale(final Locale locale)
	{
		withLocale(locale);
		return this;
	}

	/**
	 * @param skip specifies position of the first item to be returned in the search result within the search result set.
	 *             For example, if there are 200 items in the persistent storage and this parameter is equal to 100, then first 100
	 *             items will be skipped and the result will contain items beginning from the 100th item.
	 *             <p>Any negative or zero value means the result should not skip items and that it will contain items beginning
	 *             from the very first item in the result set.</p>
	 * @return an {@link ItemLookupRequestBuilder} with the skip value set
	 * @deprecated replaced with {@link #withPageParameters(PaginationParameters)} where the
	 * {@link PaginationParameters} are composed of the {@code skip} and {@code top} of the
	 * {@link ItemLookupRequest} that is being built
	 */
	@Deprecated(since = "2105", forRemoval = true)
	ItemLookupRequestBuilder withSkip(final Integer skip)
	{
		if(skip != null) {
			pageParameters = PaginationParameters.create(skip, safePageParams().getPageSize());
		}
		return this;
	}

	/**
	 * @param top number of items to include in the result set. Any negative number is treated as {@code 0}.
	 *            <p>This parameter allows to limit number of items returned, so that the application would not attempt to load
	 *            too many items matching the search criteria into the memory and result in the {@link OutOfMemoryError}.</p>
	 * @return an {@link ItemLookupRequestBuilder} with the top value set
	 * @deprecated replaced with {@link #withPageParameters(PaginationParameters)} where the
	 * {@link PaginationParameters} are composed of the {@code skip} and {@code top} of the
	 * {@link ItemLookupRequest} that is being built
	 */
	@Deprecated(since = "2105", forRemoval = true)
	ItemLookupRequestBuilder withTop(final Integer top)
	{
		if(top != null) {
			pageParameters = PaginationParameters.create(safePageParams().getPageStart(), top);
		}
		return this;
	}

	ItemLookupRequestBuilder withPageParameters(final PaginationParameters params)
	{
		pageParameters = params;
		return this;
	}

	ItemLookupRequestBuilder withNavigationSegments(final List<NavigationSegment> navigationSegments)
	{
		this.navigationSegments = navigationSegments;
		return this;
	}

	ItemLookupRequestBuilder withExpand(final List<ArrayList<NavigationPropertySegment>> expand)
	{
		this.expand = expand;
		return this;
	}

	ItemLookupRequestBuilder withHasNoFilterResult(final boolean noFilterResult)
	{
		this.noFilterResult = noFilterResult;
		return this;
	}

	/**
	 * Specifies integration key for the request to build
	 * @param integrationKey integration key value
	 * @return a builder with the integration key specified
	 * @deprecated the method has no effect because the only way to specify an integration key is to specify
	 * the integration item, i.e. {@link #withIntegrationItem(IntegrationItem)}
	 */
	@Deprecated(since = "2105", forRemoval = true)
	public ItemLookupRequestBuilder withIntegrationKey(final String integrationKey)
	{
		this.integrationKey = integrationKey;
		return this;
	}

	/**
	 * Specifies OData entry associated with the request
	 *
	 * @param oDataEntry an OData entry to be handled by the request
	 * @return a builder with the OData entry specified
	 * @deprecated since 1905. Use {@link #withIntegrationItem(IntegrationItem)} instead
	 */
	@Deprecated(since = "1905", forRemoval = true)
	public ItemLookupRequestBuilder withODataEntry(final ODataEntry oDataEntry)
	{
		this.oDataEntry = oDataEntry;
		return this;
	}

	public ItemLookupRequestBuilder withServiceRoot(final URI serviceRoot)
	{
		this.serviceRoot = serviceRoot;
		return this;
	}

	public ItemLookupRequestBuilder withContentType(final String contentType)
	{
		this.contentType = contentType;
		return this;
	}

	public ItemLookupRequestBuilder withRequestUri(final URI currentRequestUri)
	{
		this.requestUri = currentRequestUri;
		return this;
	}

	public ItemLookupRequestBuilder withEntitySet(final EdmEntitySet entitySet)
	{
		this.entitySet = entitySet;
		return this;
	}

	public ItemLookupRequestBuilder withIntegrationObject(final String code)
	{
		this.integrationObjectCode = code;
		return this;
	}

	public ItemLookupRequestBuilder from(final ItemLookupRequest request)
	{
		return new ItemLookupRequestBuilder()
				.withODataEntry(request.getODataEntry())
				.withIntegrationItem(request.getIntegrationItem())
				.withEntitySet(request.getEntitySet())
				.withIntegrationObject(request.getIntegrationObjectCode())
				.withTypeDescriptor(request.getTypeDescriptor())
				.withNavigationSegments(request.getNavigationSegments())
				.withPageParameters(request.getPaginationParameters().orElse(null))
				.withCount(request.includeTotalCount())
				.withCountOnly(request.isCountOnly())
				.withExpand(request.getExpand())
				.withServiceRoot(request.getServiceRoot())
				.withContentType(request.getContentType())
				.withRequestUri(request.getRequestUri())
				.withFilter(request.getFilter())
				.withOrderBy(request.getOrderBy())
				.withLocale(request.getAcceptLocale())
				.withHasNoFilterResult(request.isNoFilterResult());
	}

	public final ItemLookupRequest build() throws EdmException
	{
		itemSearchRequestBuilder.withPageParameters(pageParameters);
		final ItemLookupRequest request = new ItemLookupRequest(itemSearchRequestBuilder.build(), entitySet);
		request.setIntegrationKey(integrationKey);
		request.setODataEntry(oDataEntry);
		request.setContentType(contentType);
		request.setServiceRoot(serviceRoot);
		request.setRequestUri(requestUri);
		request.setIntegrationObjectCode(integrationObjectCode);
		request.setNavigationSegments(navigationSegments);
		request.setExpand(expand);
		request.setNoFilterResult(noFilterResult);
		return request;
	}

	private PaginationParameters safePageParams()
	{
		return pageParameters != null ? pageParameters : PaginationParameters.create(0, 0);
	}
}
