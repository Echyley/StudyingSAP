/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundleservices.search.solrfacetsearch.provider.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.PropertyFieldValueProviderTestBase;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Lists;


/**
 * Test to check whether the lowest price value is returned in LowestBundlePriceValueProvider
 */

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class LowestBundlePriceValueProviderTest extends PropertyFieldValueProviderTestBase
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static final String PROPERTY_NAME = "lowestBundlePriceValue";
	@Mock
	private BundleRuleService bundleRuleService;

	@Mock
	private ProductModel product;

	@Mock
	private IndexedProperty indexedProperty;

	private final Collection<CurrencyModel> currencies = new ArrayList<CurrencyModel>();
	private CurrencyModel usd;

	@Before
	public void setUp() throws Exception
	{
		configure();
	}

	@Override
	protected String getPropertyName()
	{
		return PROPERTY_NAME;
	}

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(new LowestBundlePriceValueProvider());
		configureBase();
		((LowestBundlePriceValueProvider) getPropertyFieldValueProvider()).setBundleRuleService(bundleRuleService);

		((LowestBundlePriceValueProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);
		Assert.assertTrue(getPropertyFieldValueProvider() instanceof FieldValueProvider);

		Mockito.lenient().when(indexedProperty.isLocalized()).thenReturn(Boolean.TRUE);
		createCurrencies();
		Mockito.lenient().when(fieldNameProvider.getFieldNames(indexedProperty, usd.getIsocode().toLowerCase())).thenReturn(
				Lists.newArrayList(getPropertyName()));

		Mockito.lenient().when(indexConfig.getCurrencies()).thenReturn(currencies);
		final ChangeProductPriceBundleRuleModel priceRule = new ChangeProductPriceBundleRuleModel();
		priceRule.setPrice(BigDecimal.valueOf(10.00));
		priceRule.setCurrency(usd);

		Mockito.lenient().when(bundleRuleService.getChangePriceBundleRuleWithLowestPrice(product, usd)).thenReturn(priceRule);


	}

	private void createCurrencies()
	{
		usd = new CurrencyModel();
		usd.setName("USD", Locale.US);
		usd.setIsocode("USD");
		currencies.add(usd);

	}

	@Test
	public void testGetFiledValues() throws FieldValueProviderException
	{
		final Collection<FieldValue> result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig,
				indexedProperty, product);

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(1, result.size());
		final Double value = (Double) result.iterator().next().getValue();
		Assert.assertEquals(10.0, value, 0.1);
	}

	@Test
	public void testInvalidArgs() throws FieldValueProviderException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("model can not be null");

		((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, null);
	}

}
