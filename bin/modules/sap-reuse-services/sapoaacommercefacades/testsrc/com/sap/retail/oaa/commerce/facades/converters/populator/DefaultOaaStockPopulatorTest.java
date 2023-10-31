/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.facades.converters.populator;

import static de.hybris.platform.basecommerce.enums.StockLevelStatus.INSTOCK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;
import com.sap.retail.oaa.commerce.services.stock.SapOaaCommerceStockService;
import com.sap.retail.oaa.commerce.services.stock.impl.DefaultSapOaaCommerceStockService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService; 


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOaaStockPopulatorTest {
	
	@InjectMocks
	private DefaultOaaStockPopulator<ProductModel, StockData> oaaStockPopulator = new DefaultOaaStockPopulator<ProductModel, StockData>();
	
	@Mock
	private SapOaaCommerceStockService oaaStockService;

	@Mock
	private BaseStoreService baseStoreService;
	
	@Mock
	private ProductModel product;
	
	@Mock
	private CommonUtils commonUtils;
	
	@Mock
	private DefaultSapOaaCommerceStockService defaultSapOaaCommerceStockService;
	
	@Mock
	private CommerceStockService commerceStockService;	
	
	private BaseStoreModel baseStore = new BaseStoreModel();


	@Before
	public void setUp() throws Exception {
		initMocks(this);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStore);
		oaaStockPopulator.setCommonUtils(commonUtils);	
		oaaStockPopulator.setOaaStockService(commerceStockService);
		defaultSapOaaCommerceStockService.setCommonUtils(commonUtils);
	}
	
	@Test
	public void testPopulate_NotEnabled() {
		given(oaaStockPopulator.isStockSystemEnabled(baseStore)).willReturn(false);
		given(commonUtils.isCOSEnabled()).willReturn(true);
		StockData stockData = new StockData();
		oaaStockPopulator.populate(product, stockData);
		assertSame(stockData.getStockLevelStatus().toString(), String.valueOf(INSTOCK));
		assertEquals(stockData.getStockLevel(), Long.valueOf(0));
	}

}
