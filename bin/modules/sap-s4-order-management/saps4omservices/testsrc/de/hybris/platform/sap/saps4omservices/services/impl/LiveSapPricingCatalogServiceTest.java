/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

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
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.sapmodel.enums.SAPProductType;
import de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMPricingService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class LiveSapPricingCatalogServiceTest {

	@Spy
	@InjectMocks
	LiveSapPricingCatalogService liveSapPricingCatalogService;
	
	@Mock
	SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	
	@Mock
	SapS4OMPricingService sapS4OMPricingService;
	
	@Mock
	ProductModel productModel;
	
	@Before
	public void init() {
		liveSapPricingCatalogService.setSapS4OrderManagementConfigService(sapS4OrderManagementConfigService);
		liveSapPricingCatalogService.setSapS4OMPricingService(sapS4OMPricingService);
		Set<SAPProductType> setProductType = new HashSet<>();
		setProductType.add(SAPProductType.PHYSICAL);
		when(productModel.getSapProductTypes()).thenReturn(setProductType);
	}
	
	@Test
	public void testGetPriceInformationsForProduct() { 		
		when(liveSapPricingCatalogService.getSapS4OrderManagementConfigService().isCatalogPricingEnabled()).thenReturn(true);
		List<PriceInformation> result = liveSapPricingCatalogService.getPriceInformationsForProduct(productModel);
		assertNotNull(result);
	}
	
	@Test
	public void testGetPriceInformationsForProductThrowsException() { 
		when(liveSapPricingCatalogService.getSapS4OrderManagementConfigService().isCatalogPricingEnabled()).thenReturn(true);
		when(liveSapPricingCatalogService.getPriceInformationsForProduct(productModel)).thenThrow(new OutboundServiceException("error"));
		try {
			liveSapPricingCatalogService.getPriceInformationsForProduct(productModel);
		} catch (UnknownIdentifierException e) {
			assertEquals("error", e.getMessage());
		}
		
	}

}
