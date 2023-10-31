/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.adaptivesearch.context.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.AsException;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProvider;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProviderFactory;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAsSearchProfileContextFactoryTest
{
	private static final String INDEX_CONFIGURATION = "indexConfiguration";
	private static final String INDEX_TYPE = "indexType";

	private AutoCloseable mocks;

	@Mock
	private CatalogVersionModel catalogVersion1;

	@Mock
	private CatalogVersionModel catalogVersion2;

	@Mock
	private CategoryModel category1;

	@Mock
	private CategoryModel category2;

	@Mock
	private LanguageModel language;

	@Mock
	private CurrencyModel currency;

	@Mock
	private AsSearchProviderFactory asSearchProviderFactory;

	@Mock
	private AsSearchProvider asSearchProvider;

	private DefaultAsSearchProfileContextFactory asSearchProfileContextFactory;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		asSearchProfileContextFactory = new DefaultAsSearchProfileContextFactory();
		asSearchProfileContextFactory.setAsSearchProviderFactory(asSearchProviderFactory);

	}

	@Test
	public void create1() throws AsException
	{
		// given
		final List<CatalogVersionModel> catalogVersions = List.of(catalogVersion1, catalogVersion2);
		final List<CategoryModel> categoryPath = List.of(category1, category2);
		when(asSearchProviderFactory.getSearchProvider()).thenReturn(asSearchProvider);

		// when
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				catalogVersions, categoryPath, language, currency);

		// then
		assertTrue(context instanceof DefaultAsSearchProfileContext);
		assertSame(INDEX_CONFIGURATION, context.getIndexConfiguration());
		assertSame(INDEX_TYPE, context.getIndexType());
		assertSame(catalogVersions, context.getCatalogVersions());
		assertSame(categoryPath, context.getCategoryPath());
		assertSame(context.getLanguage(), language);
		assertSame(context.getCurrency(), currency);
		assertNotNull(context.getAttributes());
	}

	@Test
	public void create2() throws AsException
	{
		// given
		final List<CatalogVersionModel> catalogVersions = List.of(catalogVersion1, catalogVersion2);
		final List<CategoryModel> categoryPath = List.of(category1, category2);
		when(asSearchProviderFactory.getSearchProvider()).thenReturn(asSearchProvider);

		// when
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				catalogVersions, categoryPath);

		// then
		assertTrue(context instanceof DefaultAsSearchProfileContext);
		assertSame(INDEX_CONFIGURATION, context.getIndexConfiguration());
		assertSame(INDEX_TYPE, context.getIndexType());
		assertSame(catalogVersions, context.getCatalogVersions());
		assertSame(categoryPath, context.getCategoryPath());
		assertNotNull(context.getAttributes());
	}
}
