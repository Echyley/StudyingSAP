/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.b2b.dao.impl;

import de.hybris.bootstrap.util.LocaleHelper;
import de.hybris.platform.b2b.dao.B2BUnitOrderDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.search.dao.impl.DefaultPagedGenericDao;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Default implementation of the {@link B2BUnitOrderDao }
 *
 * @spring.bean b2BUnitOrderDao
 */

public class DefaultB2BUnitOrderDao extends DefaultPagedGenericDao<OrderModel> implements B2BUnitOrderDao
{
	private static final Logger LOG = Logger.getLogger(DefaultB2BUnitOrderDao.class);

	private List<OrderStatus> filterOrderStatusList;

	private static final String ORDER_ALIAS = "o";
	private static final String UNIT_ALIAS = "u";
	private static final String USER_ALIAS = "b";

	private static final String BRANCH_UNITS = "branchUnits";
	private static final String STATUS_LIST = "statusList";
	private static final String FILTER_STATUS_LIST = "filterStatusList";
	private static final String STORE = "store";
	private static final String CODE = "code";
	private static final String AND = " AND ";
	private static final String FROM = " FROM ";


	private static final String B2B_UNIT_JOIN_QUERY =
			" LEFT JOIN " + B2BUnitModel._TYPECODE + " AS " + UNIT_ALIAS + " ON {" + OrderModel.UNIT + "}={" + UNIT_ALIAS + ":"
					+ B2BUnitModel.PK + "}";
	private static final String BUYER_JOIN_QUERY =
			" LEFT JOIN " + B2BCustomerModel._TYPECODE + " AS " + USER_ALIAS + " ON {" + OrderModel.USER + "}={" + USER_ALIAS + ":"
					+ B2BCustomerModel.PK + "}";
	private static final String BRANCH_ORDERS_SEARCH_RESTRICTION = AND + "{" + OrderModel.UNIT + "} IN (?" + BRANCH_UNITS + ") ";

	private static final String FIND_BRANCH_ORDERS_TEMPLATE =
			" SELECT {" + OrderModel.PK + "}, {" + OrderModel.CREATIONTIME + "}, {" + OrderModel.CODE + "} %s FROM {"
					+ OrderModel._TYPECODE + " AS " + ORDER_ALIAS + " %s}";

	private static final String FIND_BRANCH_ORDERS = FIND_BRANCH_ORDERS_TEMPLATE.formatted("", "");

	private static final String FIND_BRANCH_ORDERS_JOIN_UNIT = FIND_BRANCH_ORDERS_TEMPLATE.formatted(
			", {" + UNIT_ALIAS + ":" + B2BUnitModel.NAME + "} AS unitName", B2B_UNIT_JOIN_QUERY);

	private static final String FIND_BRANCH_ORDERS_JOIN_BUYER = FIND_BRANCH_ORDERS_TEMPLATE.formatted(
			", {" + USER_ALIAS + ":" + B2BCustomerModel.NAME + "} AS userName", BUYER_JOIN_QUERY);


	private static final String FILTER_ORDER_BY_STORE =
			" WHERE  {" + OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.STORE + "} = ?" + STORE;

	private static final String FILTER_OUT_ORDER_STATUSES =
			AND + "{" + OrderModel.STATUS + "} NOT IN (?" + FILTER_STATUS_LIST + ")";
	private static final String FILTER_ORDER_BY_STATUS = AND + "{" + OrderModel.STATUS + "} IN (?" + STATUS_LIST + ")";

	private static final String FIND_BRANCH_ORDER_FOR_CODE =
			"SELECT {" + OrderModel.PK + "}, {" + OrderModel.CREATIONTIME + "}, {" + OrderModel.CODE + "}" + FROM + "{"
					+ OrderModel._TYPECODE + "}" + FILTER_ORDER_BY_STORE + AND + "{" + OrderModel.CODE + "} = ?" + CODE;

	protected static final String FILTER_NAME_USER = "user";
	protected static final String FILTER_NAME_UNIT = "unit";
	protected static final String FILTER_ORDER_BY_USER =
			AND + "{" + OrderModel.USER + "} IN ({{SELECT {e:" + B2BCustomerModel.PK + "}" + FROM + "{" + B2BCustomerModel._TYPECODE
					+ " AS e} WHERE lower({e:" + B2BCustomerModel.NAME + "}) LIKE ?" + FILTER_NAME_USER + "}})";
	protected static final String FILTER_ORDER_BY_UNIT =
			AND + "{" + OrderModel.UNIT + "} IN ({{SELECT {e:" + B2BUnitModel.PK + "}" + FROM + "{" + B2BUnitModel._TYPECODE
					+ " AS e} WHERE lower({e:" + B2BUnitModel.NAME + "}) LIKE ?" + FILTER_NAME_UNIT + "}})";

	private static final String BY_DATE = "byDate";
	private static final String BY_ORDER_NUMBER = "byOrderNumber";
	private static final String BY_ORG_UNIT = "byOrgUnit";
	private static final String BY_BUYER = "byBuyer";
	private static final String BY_ORG_UNIT_DESC = "byOrgUnitDesc";
	private static final String BY_BUYER_DESC = "byBuyerDesc";

	private static final String SORT_ORDERS_BY_DATE =
			" ORDER BY {" + ORDER_ALIAS + ":" + OrderModel.CREATIONTIME + "} DESC, {" + ORDER_ALIAS + ":" + OrderModel.PK + "}";

	private static final String STORE_MUST_NOT_BE_NULL = "Store must not be null";
	private static final String PAGEABLE_MUST_NOT_BE_NULL = "PageableData must not be null";
	private static final String BRANCH_MUST_NOT_BE_NULL = "Branch units cannot be null";
	private static final String FILTERS_MUST_NOT_BE_NULL = "Filters object cannot be null";

	private ConfigurationService configurationService;
	private FlexibleSearchService flexibleSearchService;

	public DefaultB2BUnitOrderDao(final String typeCode)
	{
		super(typeCode);
	}

	protected List<OrderStatus> getFilterOrderStatusList()
	{
		return filterOrderStatusList;
	}

	/**
	 * Optional list of {@link OrderStatus} values to be filtered out.
	 */
	public void setFilterOrderStatusList(final List<OrderStatus> filterOrderStatusList)
	{
		this.filterOrderStatusList = filterOrderStatusList;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return this.flexibleSearchService;
	}

	@Override
	public OrderModel findBranchOrderByCode(final String code, Set<B2BUnitModel> branchUnits, final BaseStoreModel store)
	{
		validateParameterNotNull(branchUnits, BRANCH_MUST_NOT_BE_NULL);

		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put(CODE, code);
		queryParams.put(BRANCH_UNITS, branchUnits);
		queryParams.put(STORE, store);

		String sql = FIND_BRANCH_ORDER_FOR_CODE + BRANCH_ORDERS_SEARCH_RESTRICTION;
		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql);
		query.getQueryParameters().putAll(queryParams);
		return getFlexibleSearchService().searchUnique(query);
	}

	@Override
	public SearchPageData<OrderModel> findBranchOrdersByStore(Set<B2BUnitModel> branchUnits, final BaseStoreModel store,
			final SolrSearchQueryData filters, final OrderStatus[] status, final PageableData pageableData)
	{
		validateParameterNotNull(store, STORE_MUST_NOT_BE_NULL);
		validateParameterNotNull(pageableData, PAGEABLE_MUST_NOT_BE_NULL);
		validateParameterNotNull(filters, FILTERS_MUST_NOT_BE_NULL);

		final Map<String, Object> queryParams = new HashMap<>();
		List<String> filterClauses = new ArrayList<>();
		queryParams.put(STORE, store);
		queryParams.put(BRANCH_UNITS, branchUnits);
		filterClauses.add(FILTER_ORDER_BY_STORE);
		filterClauses.add(BRANCH_ORDERS_SEARCH_RESTRICTION);

		if (CollectionUtils.isNotEmpty(getFilterOrderStatusList()))
		{
			queryParams.put(FILTER_STATUS_LIST, getFilterOrderStatusList());
			filterClauses.add(FILTER_OUT_ORDER_STATUSES);
		}

		if (ArrayUtils.isNotEmpty(status))
		{
			queryParams.put(STATUS_LIST, Arrays.asList(status));
			filterClauses.add(FILTER_ORDER_BY_STATUS);
		}

		var additionalFilter = getFindOrdersAdditionalFilter();
		if (!additionalFilter.isEmpty())
		{
			filterClauses.add(additionalFilter);
		}

		for (SolrSearchQueryTermData filter : filters.getFilterTerms())
		{
			String filterName = filter.getKey();
			queryParams.put(filterName, "%" + filter.getValue().toLowerCase(LocaleHelper.getPersistenceLocale()) + "%");
			if (filterName.equals(FILTER_NAME_USER))
			{
				filterClauses.add(FILTER_ORDER_BY_USER);
			}
			else if (filterName.equals(FILTER_NAME_UNIT))
			{
				filterClauses.add(FILTER_ORDER_BY_UNIT);
			}
		}

		final String filterClause = filterClauses.stream().collect(Collectors.joining());
		final List<SortQueryData> sortQueries = getSortQueryDataForUnitLevelOrders(filterClause);
		return getPagedFlexibleSearchService().search(sortQueries, BY_DATE, queryParams, pageableData);
	}

	@Override
	protected SortQueryData createSortQueryData(final String sortCode, final String query)
	{
		final SortQueryData result = new SortQueryData();
		result.setSortCode(sortCode);
		result.setQuery(query);
		return result;
	}

	protected String createQuery(final String... queryClauses)
	{
		final StringBuilder queryBuilder = new StringBuilder();

		for (final String queryClause: queryClauses)
		{
			queryBuilder.append(queryClause);
		}

		return queryBuilder.toString();
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	protected String getFindOrdersAdditionalFilter()
	{
		try
		{
			if (getConfigurationService().getConfiguration()
					.getBoolean(CommerceServicesConstants.FIND_ORDERS_ADDITIONAL_FILTER_ENABLED, false))
			{
				return CommerceServicesConstants.FIND_ORDERS_ADDITIONAL_FILTER;
			}
		}
		catch (final ConversionException conversionException)
		{
			LOG.error("The parameter enabling additional filter for find orders query is misconfigured.", conversionException);
			throw conversionException;
		}
		return StringUtils.EMPTY;
	}

	private List<SortQueryData> getSortQueryDataForUnitLevelOrders(final String filterQuery)
	{
		ArrayList<SortQueryData> array = new ArrayList<>();
		array.add(createSortQueryData(BY_DATE, createQuery(FIND_BRANCH_ORDERS, filterQuery, SORT_ORDERS_BY_DATE)));
		array.add(createSortQueryData(BY_ORDER_NUMBER,
				createQuery(FIND_BRANCH_ORDERS, filterQuery, getSortByAliasWithDateAndPK(ORDER_ALIAS, OrderModel.CODE, true))));
		array.add(createSortQueryData(BY_BUYER, createQuery(FIND_BRANCH_ORDERS_JOIN_BUYER, filterQuery,
				getSortByAliasWithDateAndPK(USER_ALIAS, B2BCustomerModel.NAME, true))));
		array.add(createSortQueryData(BY_BUYER_DESC, createQuery(FIND_BRANCH_ORDERS_JOIN_BUYER, filterQuery,
				getSortByAliasWithDateAndPK(USER_ALIAS, B2BCustomerModel.NAME, false))));
		array.add(createSortQueryData(BY_ORG_UNIT, createQuery(FIND_BRANCH_ORDERS_JOIN_UNIT, filterQuery,
				getSortByAliasWithDateAndPK(UNIT_ALIAS, UnitModel.NAME, true))));
		array.add(createSortQueryData(BY_ORG_UNIT_DESC, createQuery(FIND_BRANCH_ORDERS_JOIN_UNIT, filterQuery,
				getSortByAliasWithDateAndPK(UNIT_ALIAS, UnitModel.NAME, false))));
		return array;
	}

	private String getSortByAliasWithDateAndPK(String alias, String attributeKey, boolean isAsc)
	{
		final String direction = isAsc ? "" : "DESC";
		return " ORDER BY {" + alias + ":" + attributeKey + "} " + direction + ",{ " + OrderModel.CREATIONTIME + "} DESC, {"
				+ OrderModel.PK + "}";
	}
}
