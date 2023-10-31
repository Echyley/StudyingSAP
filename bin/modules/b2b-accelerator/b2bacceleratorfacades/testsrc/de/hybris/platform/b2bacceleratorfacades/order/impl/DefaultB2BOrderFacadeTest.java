/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.b2bacceleratorfacades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.services.B2BUnitOrderService;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;
import static org.assertj.core.api.Assertions.assertThat;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultB2BOrderFacadeTest
{
	@InjectMocks
	private DefaultB2BOrderFacade defaultB2BOrderFacade;
	@Mock
	private AbstractPopulatingConverter<OrderModel, OrderData> orderConverter;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private AbstractPopulatingConverter<OrderModel, OrderHistoryData> orderHistoryConverter;
	@Mock
	private PageableData pageableData;
	@Mock
	private SearchPageData<OrderModel> orderModelSearchPageData;
	@Mock
	private OrderHistoryData orderHistoryData;
	private OrderModel orderModel;
	private ProductData productData;
	@Mock(lenient = true)
	private B2BUnitOrderService b2BUnitOrderService;

	@Before
	public void setUp()
	{
		orderModel = new OrderModel();
		orderModel.setCode("order");
		final AbstractOrderEntryModel entryModel = new AbstractOrderEntryModel();
		final ProductModel productModel1 = new ProductModel();
		entryModel.setProduct(productModel1);
		final List<AbstractOrderEntryModel> entryModelList = new ArrayList<AbstractOrderEntryModel>();
		entryModelList.add(entryModel);
		orderModel.setEntries(entryModelList);

		final List<OrderModel> orderModelList = new ArrayList<>();
		orderModelList.add(orderModel);
		orderModelSearchPageData = new SearchPageData<>();
		orderModelSearchPageData.setResults(orderModelList);

		given(orderHistoryConverter.convert(orderModel)).willReturn(orderHistoryData);

		final OrderData orderData = Mockito.mock(OrderData.class, withSettings().lenient());
		final List<OrderEntryData> listData = new ArrayList<OrderEntryData>();
		final OrderEntryData entryData = new OrderEntryData();
		entryData.setProduct(productData);
		listData.add(entryData);
		given(orderData.getEntries()).willReturn(listData);
		given(orderConverter.convert(orderModel)).willReturn(orderData);

		productData = Mockito.mock(ProductData.class);
	}

    @Test
    public void testGetBranchOrderForCode()
    {
        given(
				b2BUnitOrderService.getBranchOrderForCode(Mockito.nullable(String.class), Mockito.nullable(BaseStoreModel.class))).willReturn(orderModel);
        defaultB2BOrderFacade.getBranchOrderForCode("1234");
        verify(orderConverter).convert(orderModel);
    }

    @Test(expected = UnknownIdentifierException.class)
    public void testGetBranchOrderForCodeIfOrderNotFound()
    {
        given(
				b2BUnitOrderService.getBranchOrderForCode(Mockito.nullable(String.class), Mockito.nullable(BaseStoreModel.class))).willThrow(ModelNotFoundException.class);
        defaultB2BOrderFacade.getBranchOrderForCode("1234");
    }

    @Test(expected = UnknownIdentifierException.class)
    public void testGetBranchOrderForCodeIfOrderNull()
    {
        given(
				b2BUnitOrderService.getBranchOrderForCode(Mockito.nullable(String.class), Mockito.nullable(BaseStoreModel.class))).willReturn(null);
        defaultB2BOrderFacade.getBranchOrderForCode("1234");
    }

	@Test
	public void testGetPagedBranchOrderHistoryForStatuses()
	{
		final BaseStoreModel baseStoreModel = new BaseStoreModel();
		baseStoreModel.setUid("baseStoreModel");
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(b2BUnitOrderService.getBranchOrderList(baseStoreModel, null, null, pageableData)).willReturn(orderModelSearchPageData);

		final SearchPageData<OrderHistoryData> branchOrderHistoryForStatuses = defaultB2BOrderFacade.getPagedBranchOrderHistoryForStatuses(
				pageableData, null, null);

		verify(b2BUnitOrderService).getBranchOrderList(baseStoreModel, null, null, pageableData);
		Assert.assertNotNull(branchOrderHistoryForStatuses);
		assertThat(branchOrderHistoryForStatuses.getResults()).hasSize(1).contains(orderHistoryData);

	}
}
