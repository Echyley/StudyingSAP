/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.core.saps4omservices.cache.service.CacheAccess;
import de.hybris.platform.sap.saps4omservices.services.SapS4SalesOrderSimulationService;
import de.hybris.platform.sap.saps4omservices.utils.SapS4OrderUtil;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPricingServiceTest {

	@InjectMocks
	DefaultPricingService defaultPricingService;
	
	@Mock
	ModuleConfigurationAccess moduleConfigurationAccess;
	
	@Mock
	SapS4SalesOrderSimulationService sapS4SalesOrderSimulationService;
	
	@Mock
	CacheAccess cacheAccess;
	
	@Mock
	SapS4OrderUtil sapS4OrderUtil;
	
	@Mock
	CommonI18NService commonI18NService;
	
	@Before
	public void init() {
		defaultPricingService.setModuleConfigurationAccess(moduleConfigurationAccess);
		defaultPricingService.setSapS4SalesOrderSimulationService(sapS4SalesOrderSimulationService);
		defaultPricingService.setSapS4OMSalesPricingCacheRegion(cacheAccess);
		defaultPricingService.setSapS4OrderUtil(sapS4OrderUtil);
		defaultPricingService.setCommonI18NService(commonI18NService);
	}
	
	@Test
	public void testGetPriceForProduct() { 
		ProductModel productModel = mock(ProductModel.class);
		when(defaultPricingService.getModuleConfigurationAccess().getProperty("saps4omcacheprice")).thenReturn(false);
		List<PriceInformation> result = defaultPricingService.getPriceForProduct(productModel);
		assertNotNull(result);
	}
	
	@Test
	public void testGetPriceForProductFromCache() { 
		ProductModel productModel = mock(ProductModel.class);
		CurrencyModel currencyModel = mock(CurrencyModel.class);
		LanguageModel languageModel = mock(LanguageModel.class);
		when(currencyModel.getIsocode()).thenReturn("USD");
		when(languageModel.getIsocode()).thenReturn("en");
		when(defaultPricingService.getModuleConfigurationAccess().getProperty("saps4omcacheprice")).thenReturn(true);
		
		when(defaultPricingService.getCommonI18NService().getCurrentCurrency()).thenReturn(currencyModel);
		when(defaultPricingService.getCommonI18NService().getCurrentLanguage()).thenReturn(languageModel);
		List<PriceInformation> result = defaultPricingService.getPriceForProduct(productModel);
		assertNotNull(result);
	}
	
	
}
