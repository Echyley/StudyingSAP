/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.b2b.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.dao.B2BUnitOrderDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.store.BaseStoreModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultB2BUnitOrderServiceTest
{
    private static final String TEST_ORDER_CODE = "code";
    @InjectMocks
    private DefaultB2BUnitOrderService defaultB2BUnitOrderService;
    @Mock
    private B2BUnitOrderDao b2BUnitOrderDao;
    @Mock
    private BaseStoreModel baseStore;
    @Mock
    private PageableData pageableData;
    @Mock
    private B2BUnitModel b2BUnitModel;
    @Mock
    private Set<B2BUnitModel> branchUnits;
    @Mock
    private B2BCustomerModel b2BCustomerModel;
    @Mock
    private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;
    @Mock
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Before
    public void setUp()
    {
        given(b2bCustomerService.getCurrentB2BCustomer()).willReturn(b2BCustomerModel);
        given(b2bUnitService.getParent(b2BCustomerModel)).willReturn(b2BUnitModel);
        given(b2bUnitService.getBranch(b2BUnitModel)).willReturn(branchUnits);
    }

    @Test
    public void testGetBranchOrderForCode()
    {
        defaultB2BUnitOrderService.getBranchOrderForCode(TEST_ORDER_CODE, baseStore);
        verify(b2BUnitOrderDao).findBranchOrderByCode(TEST_ORDER_CODE, branchUnits, baseStore);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBranchOrderForCodeNull()
    {
        defaultB2BUnitOrderService.getBranchOrderForCode(null, baseStore);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBranchOrderForCodeNullStore()
    {
        defaultB2BUnitOrderService.getBranchOrderForCode(TEST_ORDER_CODE, null);
    }

    @Test
    public void testGetBranchOrderList()
    {
        defaultB2BUnitOrderService.getBranchOrderList(baseStore, null, null, pageableData);
        verify(b2BUnitOrderDao).findBranchOrdersByStore(branchUnits, baseStore, null, null, pageableData);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBranchOrderListNullStore()
    {
        defaultB2BUnitOrderService.getBranchOrderList(null, null, null, pageableData);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBranchOrderListNullPageable()
    {
        defaultB2BUnitOrderService.getBranchOrderList(baseStore, null, null, null);
    }
}
