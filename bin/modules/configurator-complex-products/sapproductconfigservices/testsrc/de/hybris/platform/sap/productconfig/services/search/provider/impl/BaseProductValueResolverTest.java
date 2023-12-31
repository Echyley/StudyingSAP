/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.search.provider.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.QualifierProvider;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link BaseProductValueResolver}
 */
@UnitTest
public class BaseProductValueResolverTest
{
	private static final String BASE_PRODUCT_CODE = "DRAGON_CAR";

	@Mock
	private SessionService sessionService;
	@Mock
	private QualifierProvider qualifierProvider;

	@Mock
	private VariantProductModel variantProductModel;
	@Mock
	private ProductModel baseProductModel;
	@Mock
	private FieldNameProvider fieldNameProvider;
	@Mock
	private InputDocument inputDocument;
	@Mock
	private IndexerBatchContext batchContext;
	@Mock
	private IndexedProperty indexedProperty;
	@Mock
	private JaloSession jaloSession;

	@InjectMocks
	private final BaseProductValueResolver classUnderTest = new BaseProductValueResolver();
	private final Optional<String> optionalBaseProduct = Optional.of(BASE_PRODUCT_CODE);;
	private final Collection<IndexedProperty> indexedProperties = new ArrayList<>();

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		when(variantProductModel.getBaseProduct()).thenReturn(baseProductModel);
		when(baseProductModel.getCode()).thenReturn(BASE_PRODUCT_CODE);
		when(sessionService.getRawSession(any())).thenReturn(jaloSession);
	}

	@Test
	public void testLoadData() throws FieldValueProviderException
	{
		final Optional<String> baseProduct = classUnderTest.loadData(null, null, variantProductModel);
		assertTrue(baseProduct.isPresent());
		assertEquals(BASE_PRODUCT_CODE, baseProduct.get());
	}

	@Test
	public void testLoadDataNoVariantProduct() throws FieldValueProviderException
	{
		final Optional<String> baseProduct = classUnderTest.loadData(null, null, baseProductModel);
		assertFalse(baseProduct.isPresent());
	}

	@Test(expected = NullPointerException.class)
	public void testLoadDataVariantProductDoesNotCarrayBase() throws FieldValueProviderException
	{
		when(variantProductModel.getBaseProduct()).thenReturn(null);
		classUnderTest.loadData(null, null, variantProductModel);
	}


	@Test
	public void testResolveForVariantProduct() throws FieldValueProviderException
	{
		indexedProperties.add(indexedProperty);
		when(qualifierProvider.canApply(indexedProperty)).thenReturn(false);
		when(qualifierProvider.getAvailableQualifiers(any(), any())).thenReturn(new ArrayList<>());

		classUnderTest.resolve(inputDocument, batchContext, indexedProperties, variantProductModel);
		verify(inputDocument, times(1)).addField(any(), matches(BASE_PRODUCT_CODE), any());
	}

	@Test

	public void testResolveForNonVariantProduct() throws FieldValueProviderException
	{
		indexedProperties.add(indexedProperty);
		when(qualifierProvider.canApply(indexedProperty)).thenReturn(false);
		when(qualifierProvider.getAvailableQualifiers(any(), any())).thenReturn(new ArrayList<>());

		classUnderTest.resolve(inputDocument, batchContext, indexedProperties, baseProductModel);
		verify(inputDocument, times(0)).addField(any(), matches(BASE_PRODUCT_CODE), any());
	}
}
