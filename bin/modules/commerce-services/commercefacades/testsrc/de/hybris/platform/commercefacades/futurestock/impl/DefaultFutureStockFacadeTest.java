/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.futurestock.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.FutureStockData;
import de.hybris.platform.commerceservices.futurestock.FutureStockService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.variants.model.VariantProductModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit tests for {@link DefaultFutureStockFacade}
 */
@UnitTest
@RunWith(value = MockitoJUnitRunner.class)
public class DefaultFutureStockFacadeTest
{
	private static final Logger LOG = Logger.getLogger(DefaultFutureStockFacadeTest.class);

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	private DefaultFutureStockFacade futureStockFacade;

	@Mock
	private FutureStockService futureStockService;
	@Mock
	private ProductService productService;
	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	@Mock
	private LanguageModel languageModel;


	@Before
	public void setUp()
	{
		futureStockFacade = new DefaultFutureStockFacade();
		futureStockFacade.setFutureStockService(futureStockService);
		futureStockFacade.setProductService(productService);
		futureStockFacade.setCommerceCommonI18NService(commerceCommonI18NService);
		Mockito.when(commerceCommonI18NService.getDefaultLanguage()).thenReturn(languageModel);
		Mockito.when(commerceCommonI18NService.getLocaleForLanguage(languageModel)).thenReturn(Locale.ENGLISH);
	}

	private Map<String, Map<Date, Integer>> getFutureMap(final String productCode)
	{
		final Map<String, Map<Date, Integer>> productsMap = new HashMap<>();
		final Map<Date, Integer> futureMap = new HashMap<>();
		try
		{
			futureMap.put(dateFormat.parse("20130505"), 1);
			futureMap.put(dateFormat.parse("20130303"), 2);
			futureMap.put(dateFormat.parse("20130304"), 3);
			futureMap.put(dateFormat.parse("20130101"), 4);
			futureMap.put(dateFormat.parse("20130102"), 5);
		}
		catch (final ParseException e)
		{
			LOG.error(e.getMessage(), e);
		}
		productsMap.put(productCode, futureMap);
		return productsMap;
	}


	@Test
	public void testGetFutureAvailabilityWhenProductNotExist()
	{
		final String productCode = "001";
		final String errorMessage = "product not exist";
		final UnknownIdentifierException error = new UnknownIdentifierException(errorMessage);
		Mockito.when(productService.getProductForCode(productCode)).thenThrow(error);
		try
		{
			futureStockFacade.getFutureAvailability(productCode);
		}
		catch (final UnknownIdentifierException e)
		{
			Assert.assertEquals(errorMessage, e.getMessage());
		}
	}

	@Test
	public void testGetFutureAvailabilityWhenNoFuture()
	{
		final String productCode = "007";
		final ProductModel product = new ProductModel();
		product.setCode(productCode);

		Mockito.when(futureStockService.getFutureAvailability(Mockito.anyList())).thenReturn(new HashMap<>());
		Mockito.when(productService.getProductForCode(productCode)).thenReturn(product);

		final List<FutureStockData> emptyFutureStock = futureStockFacade.getFutureAvailability(productCode);
		Assert.assertNotNull(emptyFutureStock);
		Assert.assertEquals(0, emptyFutureStock.size());
	}

	@Test
	public void testGetFutureAvailability()
	{
		final String productCode = "001_01";
		final ProductModel product = new ProductModel();
		product.setCode(productCode);

		Mockito.when(futureStockService.getFutureAvailability(Mockito.anyList())).thenReturn(getFutureMap(productCode));
		Mockito.when(productService.getProductForCode(productCode)).thenReturn(product);

		final List<FutureStockData> orderedFutureStock = futureStockFacade.getFutureAvailability(productCode);
		Assert.assertNotNull(orderedFutureStock);
		Assert.assertEquals(5, orderedFutureStock.size());
		FutureStockData fsdOld = orderedFutureStock.get(0);
		Assert.assertNotNull(fsdOld);
		// check if returned list is ordered
		for (int i = 1; i < orderedFutureStock.size(); i++)
		{
			final FutureStockData fsd = orderedFutureStock.get(i);
			Assert.assertNotNull(fsd);
			// current element should have a date that is newer or equal to last element

			final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");

			try
			{
				final Date newDate = dateFormat.parse(fsd.getFormattedDate());
				final Date oldDate = dateFormat.parse(fsdOld.getFormattedDate());

				Assert.assertTrue(newDate.compareTo(oldDate) > 0);
				fsdOld = fsd;
			}
			catch (final ParseException pe)
			{
				Assert.fail(pe.getMessage());
			}

		}
	}

	@Test
	public void testGetFutureAvailabilityForSelectedVariantsWhenNotVar()
	{
		final String productCode = "002";
		final List<String> skus = new ArrayList<>();
		final ProductModel product = new ProductModel();
		Mockito.when(productService.getProductForCode(productCode)).thenReturn(product);
		final Map<String, List<FutureStockData>> emptyFutureStock = futureStockFacade.getFutureAvailabilityForSelectedVariants(productCode, skus);
		Assert.assertNull(emptyFutureStock);
	}

	@Test
	public void testGetFutureAvailabilityForSelectedVariantsWhenSelectedEmpty()
	{
		final String productCode = "003";
		final String varProductCode = "003_01";
		final List<String> skus = new ArrayList<>();
		skus.add("003_02");
		final ProductModel product = new ProductModel();
		final VariantProductModel variantProduct = new VariantProductModel();
		variantProduct.setCode(varProductCode);
		final Collection<VariantProductModel> variantProductModels = new ArrayList<>();
		variantProductModels.add(variantProduct);
		product.setVariants(variantProductModels);

		Mockito.when(productService.getProductForCode(productCode)).thenReturn(product);
		final Map<String, List<FutureStockData>> emptyFutureStock = futureStockFacade.getFutureAvailabilityForSelectedVariants(productCode, skus);
		Assert.assertNull(emptyFutureStock);
	}

	@Test
	public void testGetFutureAvailabilityForSelectedVariants()
	{
		final String productCode = "004";
		final String varProductCode = "004_01";
		final List<String> skus = new ArrayList<>();
		skus.add(varProductCode);
		final ProductModel product = new ProductModel();
		final VariantProductModel variantProduct = new VariantProductModel();
		variantProduct.setCode(varProductCode);
		final Collection<VariantProductModel> variantProductModels = new ArrayList<>();
		variantProductModels.add(variantProduct);
		product.setVariants(variantProductModels);

		Mockito.when(productService.getProductForCode(productCode)).thenReturn(product);
		Mockito.when(futureStockService.getFutureAvailability(Mockito.anyList())).thenReturn(getFutureMap(varProductCode));
		Mockito.when(productService.getProductForCode(varProductCode)).thenReturn(variantProduct);

		final Map<String, List<FutureStockData>> futureStock = futureStockFacade.getFutureAvailabilityForSelectedVariants(productCode, skus);
		Assert.assertNotNull(futureStock);
		Assert.assertEquals(1, futureStock.size());
		Assert.assertEquals(5, futureStock.get(varProductCode).size());
	}
}
