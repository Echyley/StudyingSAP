/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedserviceservices.dao.impl;

import de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants;
import de.hybris.platform.assistedserviceservices.dao.CustomerGroupDao;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.search.dao.impl.DefaultPagedGenericDao;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.util.Config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Concrete implementation for the customer group Dao
 */
public class DefaultCustomerGroupDao extends DefaultPagedGenericDao<CustomerModel> implements CustomerGroupDao
{
	protected static final String SORT_BY_NAME_ASC = "byNameAsc";
	protected static final String SORT_BY_NAME_DESC = "byNameDesc";

	protected static final String SORT_BY_ODER_DATE_ASC = "byOrderDateAsc";
	protected static final String SORT_BY_ORDER_DATE_DESC = "byOrderDateDesc";

	protected static final String SORT_BY_CREATIONTIME_DESC = "byCreationTimeDesc";

	protected static final String GROUPS_UID = "groupsUid";
	protected static final String CURRENTDATE = "currentDate";
	protected static final String DELIVERY_STATUS = "READY_FOR_PICKUP";
	protected static final String LOGINDISABLED_PARAMETER = "loginDisabled";
	protected static final String SITE_PARAMETER = "site";

	protected static final String QUERY_PARAMETER = "query";
	protected static final String WHERE_KEYWORD = "WHERE";
	protected static final String DELIVERY_STATE = "deliveryState";

	private static final String CUSTOMERS_PER_STORE_PREFIX = "SELECT {cu." + CustomerModel.PK + "}, {cu." + CustomerModel.NAME + "}, {cu." + CustomerModel.UID + "}"
		+ " FROM {PrincipalGroupRelation as pg JOIN PrincipalGroup as p ON {pg.target} = {p.pk}"
		+ " JOIN " + CustomerModel._TYPECODE + " as cu  ON {pg.source} = {cu." + CustomerModel.PK + "}}"
		+ " WHERE  {p.pk} in ( ?" + GROUPS_UID + " ) "
		+ " AND {cu:" + CustomerModel.LOGINDISABLED + "} = ?" + LOGINDISABLED_PARAMETER
		+ " AND ({cu:" + CustomerModel.DEACTIVATIONDATE + "} IS NULL"
		+ " OR {cu:" + CustomerModel.DEACTIVATIONDATE + "} > ?" + CURRENTDATE + ") ";
	private static final String CUSTOMERS_PER_STORE_ISOLATED = CUSTOMERS_PER_STORE_PREFIX
		+ " AND {cu:" + CustomerModel.SITE  + "} = ?" + SITE_PARAMETER;
	private static final String CUSTOMERS_PER_STORE_COMMON = CUSTOMERS_PER_STORE_PREFIX
		+ " AND {cu:" + CustomerModel.SITE + "} IS NULL";

	private static final String CUSTOMERS_PER_STORE_NO_SITE = CUSTOMERS_PER_STORE_PREFIX;
	private static final String CUSTOMERS_REP_CONSIGNMENT_PREFIX = "SELECT {cu." + CustomerModel.PK + "}, MAX({co." + ConsignmentModel.CREATIONTIME + "}) as maxCreationTime"
		+ " FROM {" + ConsignmentModel._TYPECODE + " as co "
		+ " JOIN " + OrderModel._TYPECODE + " as o ON {o:" + OrderModel.PK + "} = {co:" + ConsignmentModel.ORDER + "} "
		+ " JOIN " + CustomerModel._TYPECODE + " as cu ON {o:" + OrderModel.USER + "} = {cu:" + CustomerModel.PK + "} "
		+ " JOIN " + ConsignmentStatus._TYPECODE + " as cs ON {co:" + ConsignmentModel.STATUS + "} = {cs:pk}} "
		+ " WHERE {cs.code} = ?" + DELIVERY_STATE
		+ " AND { co." + ConsignmentModel.DELIVERYPOINTOFSERVICE + "} in ( ?" + GROUPS_UID + " ) "
		+ " AND {cu:" + CustomerModel.LOGINDISABLED + "} = ?" + LOGINDISABLED_PARAMETER;
	private static final String CUSTOMERS_REP_CONSIGNMENT_POSTFIX = " AND ({cu:" + CustomerModel.DEACTIVATIONDATE + "} IS NULL"
		+ " OR {cu:" + CustomerModel.DEACTIVATIONDATE + "} > ?" + CURRENTDATE + ") "
		+ " GROUP BY {cu.pk}";
	private static final String CUSTOMERS_REP_CONSIGNMENT_ISOLATED = CUSTOMERS_REP_CONSIGNMENT_PREFIX
		+ " AND {cu:" + CustomerModel.SITE + "} = ?" + SITE_PARAMETER
		+ CUSTOMERS_REP_CONSIGNMENT_POSTFIX;
	private static final String CUSTOMERS_REP_CONSIGNMENT_COMMON = CUSTOMERS_REP_CONSIGNMENT_PREFIX
		+ " AND {cu:" + CustomerModel.SITE + "} IS NULL"
		+ CUSTOMERS_REP_CONSIGNMENT_POSTFIX;

	private static final String CUSTOMERS_REP_CONSIGNMENT_NO_SITE = CUSTOMERS_REP_CONSIGNMENT_PREFIX
			+ CUSTOMERS_REP_CONSIGNMENT_POSTFIX;
	private static final String SORT_CUSTOMERS_BY_NAME_ASC = " ORDER BY {cu." + CustomerModel.NAME + "} ASC";

	private static final String SORT_CUSTOMERS_BY_NAME_DESC = " ORDER BY {cu." + CustomerModel.NAME + "} DESC";


	private static final String SORT_ORDERS_BY_ASC = " ORDER BY MAX({co." + ConsignmentModel.CREATIONTIME + "}) ASC";

	private static final String SORT_ORDERS_BY_DESC = " ORDER BY MAX({co." + ConsignmentModel.CREATIONTIME + "}) DESC";

	private static final String QUERY_BY_NAME_OR_UID = "(LOWER({cu:" + CustomerModel.UID + "}) LIKE CONCAT(?" + QUERY_PARAMETER + ", '%') "
																		+ "OR LOWER({cu:name}) LIKE CONCAT('%', CONCAT(?" + QUERY_PARAMETER + ", '%'))) ";
	private TimeService timeService;

	private BaseSiteService baseSiteService;

	public DefaultCustomerGroupDao(final String typeCode)
	{
		super(typeCode);
	}

	@Override
	public <T extends CustomerModel> SearchPageData<T> findAllCustomersByGroups(final List<UserGroupModel> groups,
			final PageableData pageableData)
	{
		return findAllCustomersByGroups(groups, pageableData, null);
	}

	@Override
	public <T extends CustomerModel> SearchPageData<T> findAllCustomersByGroups(final List<UserGroupModel> groups,
			final PageableData pageableData, final Map<String, Object> parameterMap)
	{
		final Map<String, Object> params = new HashMap<>();
		final BaseSiteModel site = getBaseSiteService().getCurrentBaseSite();
		String queryString = CUSTOMERS_PER_STORE_NO_SITE;
		if (site != null)
		{
			final boolean isSiteParameterSupported = isSiteIsolated(site);
			queryString = isSiteParameterSupported ? CUSTOMERS_PER_STORE_ISOLATED : CUSTOMERS_PER_STORE_COMMON;
			if (isSiteParameterSupported)
			{
				params.put(SITE_PARAMETER, site);
			}
		}

		params.put(CURRENTDATE, getTimeService().getCurrentTime());
		params.put(GROUPS_UID, groups);
		params.put(LOGINDISABLED_PARAMETER, Boolean.FALSE);
		if (parameterMap != null && parameterMap.containsKey(QUERY_PARAMETER))
		{
			queryString = addingQueryToString(queryString);
			params.put(QUERY_PARAMETER, ((String)parameterMap.get(QUERY_PARAMETER)).toLowerCase(Locale.getDefault()));
		}

		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData(SORT_BY_NAME_ASC, createQuery(queryString, SORT_CUSTOMERS_BY_NAME_ASC)),
				createSortQueryData(SORT_BY_NAME_DESC, createQuery(queryString, SORT_CUSTOMERS_BY_NAME_DESC)));
		return getPagedFlexibleSearchService().search(sortQueries, SORT_BY_NAME_ASC, params, pageableData);
	}

	@Override
	public <T extends CustomerModel> SearchPageData<T> findAllCustomersByConsignmentsInPointOfServices(
			final List<PointOfServiceModel> poses, final PageableData pageableData)
	{
		return findAllCustomersByConsignmentsInPointOfServices(poses, pageableData, null);
	}

	@Override
	public <T extends CustomerModel> SearchPageData<T> findAllCustomersByConsignmentsInPointOfServices(
			final List<PointOfServiceModel> poses, final PageableData pageableData, final Map<String, Object> parameterMap)
	{
		final Map<String, Object> params = new HashMap<>();
		final BaseSiteModel site = getBaseSiteService().getCurrentBaseSite();
		final boolean isSiteParameterSupported = isSiteIsolated(site) && site != null;
		String queryString = CUSTOMERS_REP_CONSIGNMENT_NO_SITE;
		if (site != null)
		{
			queryString = isSiteParameterSupported ? CUSTOMERS_REP_CONSIGNMENT_ISOLATED : CUSTOMERS_REP_CONSIGNMENT_COMMON;
			if (isSiteParameterSupported)
			{
				params.put(SITE_PARAMETER, site);
			}
		}

		params.put(CURRENTDATE, getTimeService().getCurrentTime());
		params.put(GROUPS_UID, poses);
		params.put(LOGINDISABLED_PARAMETER, Boolean.FALSE);
		params.put(DELIVERY_STATE, getDeliveryStatus());

		if (parameterMap != null && parameterMap.containsKey(QUERY_PARAMETER))
		{
			queryString = addingQueryToString(queryString);
			params.put(QUERY_PARAMETER, ((String)parameterMap.get(QUERY_PARAMETER)).toLowerCase(Locale.getDefault()));
		}

		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData(SORT_BY_ODER_DATE_ASC, createQuery(queryString, SORT_ORDERS_BY_ASC)),
				createSortQueryData(SORT_BY_ORDER_DATE_DESC, createQuery(queryString, SORT_ORDERS_BY_DESC)));

		return getPagedFlexibleSearchService().search(sortQueries, SORT_BY_ORDER_DATE_DESC, params, pageableData);
	}

	private String addingQueryToString(String queryStr)
	{
		if(StringUtils.isNotEmpty(queryStr))
		{
			final String[] splitStrings = queryStr.split(WHERE_KEYWORD);
			if (splitStrings.length > 1)
			{
				return splitStrings[0] + " WHERE " + QUERY_BY_NAME_OR_UID + " AND " + splitStrings[1];
			}
		}
		return queryStr;
	}

	protected String createQuery(final String... queryClauses)
	{
		final StringBuilder queryBuilder = new StringBuilder();

		for (final String queryClause : queryClauses)
		{
			queryBuilder.append(queryClause);
		}

		return queryBuilder.toString();
	}

	protected String getDeliveryStatus()
	{
		return Config.getString(AssistedserviceservicesConstants.DEFAULT_BOPIS_STATUS, DELIVERY_STATUS);
	}

	protected boolean isSiteIsolated(BaseSiteModel site)
	{
		return site != null && site.getDataIsolationEnabled().booleanValue();
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(TimeService timeService)
	{
		this.timeService = timeService;
	}

	public void setBaseSiteService(BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}
}
