/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.core.sapsalesordersimulation.cache.service.CacheAccess;
import de.hybris.platform.sap.sapsalesordersimulation.service.SalesOrderSimulationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.PriceValue;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPricingServiceTest {
	private static final double PRICE_2 = 18.5;
	private static final double PRICE_1 = 15.5;
	public static final String CONF_PROP_IS_CACHED_CATALOG_PRICE = "saplivecacheprice";
	@InjectMocks
	DefaultPricingService defaultPricingService;
	@Mock
	private SalesOrderSimulationService salesOrderSimulationService;
	
	@Mock
	private ModuleConfigurationAccess moduleConfigurationAccess;
	
	@Mock
	protected CacheAccess sapSalesPricingCacheRegion;
	
	@Mock
	private UserService userService;
	
	@Mock
	private BaseStoreService baseStoreService;
	
	@Mock
	private CommonI18NService commonI18NService;
	
	@Mock
	private SalesOrderSimulationUtil salesOrderUtils;
	
	@Test
	public void testGetPriceForProduct() {
		PriceValue price= new PriceValue("USD",PRICE_1,false);
		PriceInformation priceInfo = new PriceInformation(price);
		List<PriceInformation> priceInformation =  new ArrayList<>();
		priceInformation.add(priceInfo);
		
		PriceValue cachedPrice= new PriceValue("USD",PRICE_2,false);
		PriceInformation cachedPriceInfo = new PriceInformation(cachedPrice);
		List<PriceInformation> cachedPriceInformation =  new ArrayList<>();
		cachedPriceInformation.add(cachedPriceInfo);
		
		ProductModel product = new ProductModel();
		product.setCode("DG-D010");
		Mockito.lenient().when(moduleConfigurationAccess.getProperty(CONF_PROP_IS_CACHED_CATALOG_PRICE)).thenReturn(false);
		Mockito.lenient().when(salesOrderSimulationService.getPriceDetailsForProduct(product)).thenReturn(priceInformation);
		List<PriceInformation> priceInfoDetails = defaultPricingService.getPriceForProduct(product);
		Assert.assertThat(Double.valueOf(PRICE_1), is(priceInfoDetails.iterator().next().getPriceValue().getValue()));
		BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
		SAPConfigurationModel sapConfigurationModel = mock(SAPConfigurationModel.class);
		Mockito.lenient().when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		Mockito.lenient().when(baseStoreService.getCurrentBaseStore().getSAPConfiguration()).thenReturn(sapConfigurationModel);
		Mockito.lenient().when(baseStoreService.getCurrentBaseStore().getSAPConfiguration().getSapcommon_referenceCustomer()).thenReturn("10100001");
		CurrencyModel currency = mock(CurrencyModel.class);
		Mockito.lenient().when(commonI18NService.getCurrentCurrency()).thenReturn(currency);
		Mockito.lenient().when(commonI18NService.getCurrentCurrency().getSapCode()).thenReturn("USD");
		Mockito.lenient().when(commonI18NService.getCurrentCurrency().getIsocode()).thenReturn("USD");
		LanguageModel languageModel = mock(LanguageModel.class);
		Mockito.lenient().when(commonI18NService.getCurrentLanguage()).thenReturn(languageModel);
		Mockito.lenient().when(commonI18NService.getCurrentLanguage().getIsocode()).thenReturn("USD");
		
		UserModel user = mock(UserModel.class);
		Mockito.lenient().when(userService.getCurrentUser()).thenReturn(user);
		Mockito.lenient().when(moduleConfigurationAccess.getProperty(CONF_PROP_IS_CACHED_CATALOG_PRICE)).thenReturn(true);
		Mockito.lenient().when(salesOrderSimulationService.getPriceDetailsForProduct(product)).thenReturn(priceInformation);
		
		
		Mockito.lenient().when (salesOrderUtils.getSoldToParty(null)).thenReturn("0017100003");
		defaultPricingService.setSalesordersimulationUtil(salesOrderUtils);
		Mockito.lenient().when(sapSalesPricingCacheRegion.get(defaultPricingService.getPriceCacheKey(product))).thenReturn(cachedPriceInformation);
		List<PriceInformation> cachedPriceInfoDetails = defaultPricingService.getPriceForProduct(product);
		Assert.assertThat(Double.valueOf(PRICE_2), is(cachedPriceInfoDetails.iterator().next().getPriceValue().getValue()));
		
	}
	
}
