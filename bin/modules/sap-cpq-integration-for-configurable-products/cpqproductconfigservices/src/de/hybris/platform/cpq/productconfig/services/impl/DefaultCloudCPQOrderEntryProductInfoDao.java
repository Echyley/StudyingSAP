/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.cpq.productconfig.services.CloudCPQOrderEntryProductInfoDao;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchParameter;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchService;
import de.hybris.platform.servicelayer.search.paginated.util.PaginatedSearchUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of {@link DefaultCloudCPQOrderEntryProductInfoDao}
 */
public class DefaultCloudCPQOrderEntryProductInfoDao implements CloudCPQOrderEntryProductInfoDao
{
	// polyglot queries
	protected static final String GET_ALL_PRODUCT_INFOS_BY_CONFIG_ID = "GET {CloudCPQOrderEntryProductInfo} WHERE {configurationId} = ?configurationId";
	protected static final String GET_ALL_PRODUCT_INFOS = "GET {CloudCPQOrderEntryProductInfo} WHERE {configurationId} is not null";

	protected static final String PARAM_NAME_CONFIG_ID = "configurationId";
	protected static final String ARGUMENT_CONFIG_ID_CANNOT_BE_NULL = "Argument configId cannot be null";
	protected static final String ARGUMENT_ENTRY_CANNOT_BE_NULL = "Argument entry cannot be null";

	private final FlexibleSearchService flexSearchService;
	private final PaginatedFlexibleSearchService paginatedFlexibleSearchService;



	/**
	 * @param flexSearchService
	 * @param paginatedFlexibleSearchService
	 */
	public DefaultCloudCPQOrderEntryProductInfoDao(final FlexibleSearchService flexSearchService,
			final PaginatedFlexibleSearchService paginatedFlexibleSearchService)
	{
		super();
		this.flexSearchService = flexSearchService;
		this.paginatedFlexibleSearchService = paginatedFlexibleSearchService;
	}

	@Override
	public boolean isOnlyRelatedToGivenEntry(final AbstractOrderEntryModel entry, final String configId)
	{
		validateParameterNotNull(entry, ARGUMENT_ENTRY_CANNOT_BE_NULL);
		final List<CloudCPQOrderEntryProductInfoModel> configurationProductInfos = getAllConfigurationProductInfosById(configId);
		return configurationProductInfos.size() == 1
				&& configurationProductInfos.get(0).getOrderEntry().getPk().equals(entry.getPk());
	}

	@Override
	public List<CloudCPQOrderEntryProductInfoModel> getAllConfigurationProductInfosById(final String configId)
	{
		validateParameterNotNull(configId, ARGUMENT_CONFIG_ID_CANNOT_BE_NULL);
		final Map<String, String> params = Collections.singletonMap(PARAM_NAME_CONFIG_ID, configId);
		final SearchResult<CloudCPQOrderEntryProductInfoModel> search = flexSearchService
				.search(new FlexibleSearchQuery(GET_ALL_PRODUCT_INFOS_BY_CONFIG_ID, params));
		return search.getResult();
	}

	@Override
	public SearchPageData<CloudCPQOrderEntryProductInfoModel> getAllConfigurationProductInfos(final int pageSize,
			final int currentPage)

	{
		final PaginatedFlexibleSearchParameter parameter = createPageableQuery(new FlexibleSearchQuery(GET_ALL_PRODUCT_INFOS),
				pageSize, currentPage);
		return paginatedFlexibleSearchService.search(parameter);
	}

	protected PaginatedFlexibleSearchParameter createPageableQuery(final FlexibleSearchQuery flexibleSearchQuery,
			final int pageSize, final int currentPage)
	{
		final PaginatedFlexibleSearchParameter parameter = new PaginatedFlexibleSearchParameter();
		parameter.setFlexibleSearchQuery(flexibleSearchQuery);
		final Map<String, String> sortMap = new LinkedHashMap<>();
		final SearchPageData<?> searchPageData = PaginatedSearchUtils.createSearchPageDataWithPaginationAndSorting(pageSize,
				currentPage, true, sortMap);
		parameter.setSearchPageData(searchPageData);
		return parameter;
	}

}
