/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.b2b.services.impl;

import de.hybris.platform.b2b.dao.B2BUnitOrderDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitOrderService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Set;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Default implementation of the {@link B2BUnitOrderService }
 *
 * @spring.bean b2BUnitOrderService
 */
public class DefaultB2BUnitOrderService implements B2BUnitOrderService {

    private static final String STORE_MUST_NOT_BE_NULL = "Store must not be null";
    private static final String PAGEABLE_MUST_NOT_BE_NULL = "PageableData must not be null";
    private static final String CODE_MUST_NOT_BE_NULL = "Order code cannot be null";
    private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
    private B2BUnitOrderDao b2BUnitOrderDao;

    public void setB2bCustomerService(final B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService)
    {
        this.b2bCustomerService = b2bCustomerService;
    }
    protected B2BCustomerService<B2BCustomerModel, B2BUnitModel> getB2bCustomerService()
    {
        return b2bCustomerService;
    }
    public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
    {
        this.b2bUnitService = b2bUnitService;
    }
    protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
    {
        return b2bUnitService;
    }
    public void setB2BUnitOrderDao(final B2BUnitOrderDao b2BUnitOrderDao)
    {
        this.b2BUnitOrderDao = b2BUnitOrderDao;
    }
    protected B2BUnitOrderDao getB2BUnitOrderDao()
    {
        return b2BUnitOrderDao;
    }

    @Override
    public OrderModel getBranchOrderForCode(final String code, final BaseStoreModel store)
    {
        validateParameterNotNull(code, CODE_MUST_NOT_BE_NULL);
        validateParameterNotNull(store, STORE_MUST_NOT_BE_NULL);

        return getB2BUnitOrderDao().findBranchOrderByCode(code, getBranchForCurrentCustomer(), store);
    }

    @Override
    public SearchPageData<OrderModel> getBranchOrderList(final BaseStoreModel store, SolrSearchQueryData filters,
                                                         final OrderStatus[] status, final PageableData pageableData)
    {
        validateParameterNotNull(store, STORE_MUST_NOT_BE_NULL);
        validateParameterNotNull(pageableData, PAGEABLE_MUST_NOT_BE_NULL);

        return getB2BUnitOrderDao().findBranchOrdersByStore(getBranchForCurrentCustomer(), store, filters, status, pageableData);
    }

    private Set<B2BUnitModel> getBranchForCurrentCustomer()
    {
        final B2BCustomerModel b2BCustomerModel = getB2bCustomerService().getCurrentB2BCustomer();
        return getB2bUnitService().getBranch(getB2bUnitService().getParent(b2BCustomerModel));
    }
}
