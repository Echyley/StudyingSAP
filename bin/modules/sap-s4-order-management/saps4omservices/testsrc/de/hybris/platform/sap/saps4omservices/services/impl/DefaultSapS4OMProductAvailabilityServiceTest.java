/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.stock.strategies.CommerceAvailabilityCalculationStrategy;
import de.hybris.platform.commerceservices.stock.strategies.WarehouseSelectionStrategy;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.sapmodel.enums.SAPProductType;
import de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException;
import de.hybris.platform.sap.saps4omservices.services.SAPS4OMAvailabilityService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMProductAvailability;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4OMProductAvailabilityServiceTest {
	
	@InjectMocks
	DefaultSapS4OMProductAvailabilityService defaultSapS4OMProductAvailabilityService;
	
	@Mock
	SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	
	@Mock
	SAPS4OMAvailabilityService sapS4OMAvailabilityService;
	
	@Mock
	StockService stockService;
	
	@Spy
	WarehouseSelectionStrategy warehouseSelectionStrategy;
	
	@Spy
	CommerceAvailabilityCalculationStrategy commerceAvailabilityCalculationStrategy;
	
	@Before
	public void init() {
		defaultSapS4OMProductAvailabilityService.setSapS4OrderManagementConfigService(sapS4OrderManagementConfigService);
		defaultSapS4OMProductAvailabilityService.setSapS4OMAvailabilityService(sapS4OMAvailabilityService);
	}
	
	@Test
	public void testGetStockLevelForProductAndBaseStore() { 
		
		ProductModel productModel = mock(ProductModel.class);
		BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
		Set<SAPProductType> set = new HashSet<>();
		set.add(SAPProductType.PHYSICAL);		
		when(defaultSapS4OMProductAvailabilityService.getSapS4OrderManagementConfigService().isATPCheckActive()).thenReturn(true);
		when(productModel.getSapProductTypes()).thenReturn(set);
		Long result = defaultSapS4OMProductAvailabilityService.getStockLevelForProductAndBaseStore(productModel, baseStoreModel);
		
		assertNotNull(result);
	}
	
	@Test
	public void testGetStockLevelStatusForProductAndBaseStoreOutOfStock() { 
		ProductModel productModel = mock(ProductModel.class);
		BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
		Set<SAPProductType> set = new HashSet<>();
		set.add(SAPProductType.PHYSICAL);		
		when(defaultSapS4OMProductAvailabilityService.getSapS4OrderManagementConfigService().isATPCheckActive()).thenReturn(true);
		when(productModel.getSapProductTypes()).thenReturn(set);
		
		StockLevelStatus result = defaultSapS4OMProductAvailabilityService.getStockLevelStatusForProductAndBaseStore(productModel, baseStoreModel);
		
		assertNotNull(result);
	}
	
	@Test
	public void testGetStockLevelStatusForProductAndBaseStoreInStock() { 
		ProductModel productModel = mock(ProductModel.class);
		BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
		Set<SAPProductType> set = new HashSet<>();
		set.add(SAPProductType.PHYSICAL);		
		SapS4OMProductAvailability sapS4OMProductAvailability = mock(SapS4OMProductAvailability.class);
		
		when(defaultSapS4OMProductAvailabilityService.getSapS4OrderManagementConfigService().isATPCheckActive()).thenReturn(true);
		when(productModel.getSapProductTypes()).thenReturn(set);
		when(sapS4OMProductAvailability.getCurrentStockLevel()).thenReturn(Long.valueOf(1));
		when(defaultSapS4OMProductAvailabilityService.getSapS4OMAvailabilityService().getProductAvailability(productModel, baseStoreModel)).thenReturn(sapS4OMProductAvailability);
				
		StockLevelStatus result = defaultSapS4OMProductAvailabilityService.getStockLevelStatusForProductAndBaseStore(productModel, baseStoreModel);
		assertNotNull(result);
	}
	
	@Test
	public void testGetStockLevelForProductAndBaseStoreWithException() { 
		ProductModel productModel = mock(ProductModel.class);
		BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
		Set<SAPProductType> set = new HashSet<>();
		set.add(SAPProductType.PHYSICAL);		
		
		when(defaultSapS4OMProductAvailabilityService.getSapS4OrderManagementConfigService().isATPCheckActive()).thenReturn(true);
		when(productModel.getSapProductTypes()).thenReturn(set);
		when(defaultSapS4OMProductAvailabilityService.getSapS4OMAvailabilityService().getProductAvailability(productModel, baseStoreModel)).thenThrow(new OutboundServiceException("error"));

		try {
			defaultSapS4OMProductAvailabilityService.getStockLevelForProductAndBaseStore(productModel, baseStoreModel);
		} catch (UnknownIdentifierException e) {
			assertNotNull(e.getMessage());
		}
	}
	
	@Test
	public void testGetStockLevelStatusForProductAndBaseStoreInStockSuperCall() { 
		ProductModel productModel = mock(ProductModel.class);
		BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);				
		when(defaultSapS4OMProductAvailabilityService.getSapS4OrderManagementConfigService().isATPCheckActive()).thenReturn(false);
		List<WarehouseModel> list = new ArrayList<>();
		when(stockService.getProductStatus(productModel, list )).thenReturn(null);
		StockLevelStatus result = defaultSapS4OMProductAvailabilityService.getStockLevelStatusForProductAndBaseStore(productModel, baseStoreModel);
		assertNull(result);
	}
	
	@Test
	public void testGetStockLevelForProductAndBaseStoreInStockSuperCall() { 
		ProductModel productModel = mock(ProductModel.class);
		BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);				
		when(defaultSapS4OMProductAvailabilityService.getSapS4OrderManagementConfigService().isATPCheckActive()).thenReturn(false);
		Long result = defaultSapS4OMProductAvailabilityService.getStockLevelForProductAndBaseStore(productModel, baseStoreModel);
		assertEquals(0, result.longValue());
	}
	
}
