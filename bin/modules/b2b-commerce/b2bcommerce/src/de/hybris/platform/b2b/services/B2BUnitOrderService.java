/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.b2b.services;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.store.BaseStoreModel;

/**
 * A service for b2b unit-level orders.
 *
 * @spring.bean B2BUnitOrderService
 */
public interface B2BUnitOrderService
{
    /**
     * Gets the order based on it's {@link OrderModel#CODE}.
     *
     * @param code
     *          the code of the order
     * @param store
     * 			the current base store
     * @return the order
     */
    public OrderModel getBranchOrderForCode(final String code, final BaseStoreModel store);

    /**
     * Retrieves all orders for the current user's organization branch
     *
     * @param store
     * 	        the current store
     * @param filters
     *          a filter terms limiting the result according to the given values
     * @param status
     * 	        one or more OrderStatuses to include in the result
     * @param pageableData
     * 	        pagination information
     * @return the list of orders
     */
    SearchPageData<OrderModel> getBranchOrderList(final BaseStoreModel store, SolrSearchQueryData filters,
                                                  final OrderStatus[] status, final PageableData pageableData);
}
