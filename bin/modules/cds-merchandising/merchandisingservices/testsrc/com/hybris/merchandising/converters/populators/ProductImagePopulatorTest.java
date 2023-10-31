/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.converters.populators;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.merchandising.model.IndexedPropertyInfo;
import com.hybris.merchandising.model.MerchProductDirectoryConfigModel;
import com.hybris.merchandising.model.Product;
import com.hybris.merchandising.model.ProductIndexContainer;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductImagePopulatorTest
{

	@Mock
	private ProductIndexContainer source;
	@Mock
	private LanguageModel language;
	@Mock
	private IndexedPropertyInfo indexedPropertyInfo;
	@Mock
	private InputDocument inputDocument;
	@Mock
	private SearchQuery searchQuery;
	@Mock
	private FacetSearchConfig facetSearchConfig;
	@Mock
	private IndexConfig indexConfig;
	@Mock
	private MerchProductDirectoryConfigModel model;

	private static final String NAME_KEY = "name";
	private static final String VALUE = "TEST";
	private static final String ISOCODE = "NAME";
	private static final String KEY = "TEST_VALUE";
	private static final String THUMB_NAIL_IMAGE = "thumbnailImage";
	private static final String MAIN_IMAGE = "mainImage";

	private ProductImagePopulator productImagePopulator;

	@Before
	public void setUp()
	{
		productImagePopulator = new ProductImagePopulator();

		final HashMap<String, IndexedPropertyInfo> map = new HashMap<>();
		map.put(THUMB_NAIL_IMAGE, indexedPropertyInfo);
		map.put(MAIN_IMAGE, indexedPropertyInfo);
		map.put(KEY, indexedPropertyInfo);

		final HashMap<String, String> mappings = new HashMap<>();
		mappings.put(KEY, "TEST_VALUE");
		mappings.put(MAIN_IMAGE, MAIN_IMAGE);
		mappings.put(THUMB_NAIL_IMAGE, THUMB_NAIL_IMAGE);


		when(source.getIndexedPropertiesMapping()).thenReturn(map);
		when(source.getInputDocument()).thenReturn(inputDocument);
		when(inputDocument.getFieldValue(Mockito.any())).thenReturn(VALUE);
		when(source.getMerchPropertiesMapping()).thenReturn(mappings);
		when(source.getMerchProductDirectoryConfigModel()).thenReturn(model);
		when(model.getBaseImageUrl()).thenReturn("TEST.URL");
		when(indexedPropertyInfo.getTranslatedFieldName()).thenReturn(NAME_KEY);
	}

	@Test
	public void testPopulate()
	{
		final Product target = new Product();
		productImagePopulator.populate(source, target);
		assertEquals("Expected value is  TEST.URLTEST", String.format("%s%s", "TEST.URL", "TEST"),
				target.getImages().getMainImage());
		assertEquals("Expected value is  TEST.URLTEST", String.format("%s%s", "TEST.URL", "TEST"),
				target.getImages().getThumbnailImage());
	}

	@Test
	public void testPopulateProductImage()
	{
		assertEquals("Expected value should be TEST.URLTEST", String.format("%s%s", "TEST.URL", VALUE),
				productImagePopulator.populateProductImage(source, KEY).get());
	}
}
