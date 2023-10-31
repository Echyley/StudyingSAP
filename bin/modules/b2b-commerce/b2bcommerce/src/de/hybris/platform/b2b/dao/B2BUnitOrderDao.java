/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.b2b.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Set;

/**
 * A dao for b2b unit-level orders.
 *
 * @spring.bean B2BUnitOrderDao
 */
public interface B2BUnitOrderDao
{
	/**
	 * Find order for a specific code and a list of unit+subunits in the current session's active catalog versions.
	 * For B2B users, information about the unit-subunit hierarchy is stored in a user session.
	 *
	 * @param code
	 * 			the code of the order
	 * @param branchUnits
	 * 			list of units+subunits in the current session's active catalog versions
	 * @param store
	 * 			the current store
	 * @return order for code in the units associated with the store
	 */
	OrderModel findBranchOrderByCode(final String code, Set<B2BUnitModel> branchUnits, final BaseStoreModel store);
	
	/**
	 * Finds orders for a list of units+subunits in the current session's active catalog versions
	 * For B2B users, information about the unit-subunit hierarchy is stored in a user session.
	 *
	 * @param branchUnits
	 * 				list of units+subunits in the current session's active catalog versions
	 * @param store
	 * 				the current store
	 * @param filters
	 *              a filter terms limiting the result according to the given values
	 * @param status
	 * 				a list of order statuses to include in the result, if null or empty then all statuses are included
	 * @param pageableData
	 * 				the pagination data
	 * @return The list of orders in the units associated with the store
	 */
	SearchPageData<OrderModel> findBranchOrdersByStore(Set<B2BUnitModel> branchUnits, final BaseStoreModel store,
			final SolrSearchQueryData filters, final OrderStatus[] status, final PageableData pageableData);
}
