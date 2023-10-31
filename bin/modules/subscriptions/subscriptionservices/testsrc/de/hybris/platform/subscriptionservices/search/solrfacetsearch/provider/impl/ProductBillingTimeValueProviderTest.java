/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.subscriptionservices.search.solrfacetsearch.provider.impl;

import static java.util.Collections.singletonList;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.PropertyFieldValueProviderTestBase;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test to check that billing frequency attribute is returned by ProudctBillingTimeValueProvider
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class ProductBillingTimeValueProviderTest extends PropertyFieldValueProviderTestBase
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static final String TEST_PRODUCT_KEYWORD_PROP_VAL = "propVal";
	private static final String TEST_PRODUCT_KEYWORD_PROP_EN_FIELD_NAME = "prop_en_string";
	@Mock
	private SessionService sessionService;
	@Mock
	private VariantProductModel model;
	@Mock
	private ProductModel baseProduct;

	@Mock
	private IndexedProperty indexedProperty;

	private final BillingTimeModel value = new BillingTimeModel();

	@Before
	public void setUp() throws Exception
	{
		configure();
	}

	@Override
	protected String getPropertyName()
	{
		return TEST_PRODUCT_KEYWORD_PROP_VAL;
	}

	@Override
	protected void configure()
	{
		setPropertyFieldValueProvider(new ProductBillingTimeValueProvider());
		configureBase();
		((ProductBillingTimeValueProvider) getPropertyFieldValueProvider()).setCommonI18NService(commonI18NService);
		((ProductBillingTimeValueProvider) getPropertyFieldValueProvider()).setSessionService(sessionService);
		((ProductBillingTimeValueProvider) getPropertyFieldValueProvider()).setFieldNameProvider(fieldNameProvider);

		Assert.assertTrue(getPropertyFieldValueProvider() instanceof FieldValueProvider);

		Mockito.lenient().when(indexedProperty.isLocalized()).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(model.getBaseProduct()).thenReturn(baseProduct);



		Mockito.lenient().when(sessionService.executeInLocalView((SessionExecutionBody) Matchers.any())).thenReturn(value);
	}

	@Test
	public void testInvalidArgs() throws FieldValueProviderException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("model can not be null");

		((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, null);
	}

	@Test
	public void testWhenIndexPropertyIsLocalized() throws FieldValueProviderException
	{
		Collection<FieldValue> result;
		Mockito.lenient().when(indexedProperty.isLocalized()).thenReturn(Boolean.TRUE);

		Mockito.lenient().when(fieldNameProvider.getFieldNames(Matchers.<IndexedProperty> any(), Matchers.<String> any())).thenReturn(
				singletonList(TEST_PRODUCT_KEYWORD_PROP_EN_FIELD_NAME));

		result = ((FieldValueProvider) getPropertyFieldValueProvider()).getFieldValues(indexConfig, indexedProperty, model);
		Assert.assertNotNull("Results CANNOT be null.", result);
		Assert.assertEquals("Did not receive expected result size.", 2, result.size());
		for (final FieldValue val : result)
		{
			Assert.assertEquals(val.getValue(), value);
		}
	}
}
