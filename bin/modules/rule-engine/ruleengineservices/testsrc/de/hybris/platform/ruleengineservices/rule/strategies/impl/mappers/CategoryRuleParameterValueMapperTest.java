/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ruleengineservices.rule.strategies.impl.mappers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.daos.CategoryDao;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapper;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapperException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CategoryRuleParameterValueMapperTest
{
	private static final String ANY_STRING = "anyString";

	private static final String CATALOG_AWARE_VALUE = "code::catalog";

	private static final String CATALOG = "catalog";

	private static final String CATALOG_VERSION = "catalogVersion";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Mock
	private CategoryDao categoryDao;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private CategoryModel category;

	@Mock
	private CatalogModel catalog;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private RuleParameterValueMapper<CatalogModel> catalogRuleParameterValueMapper;

	@InjectMocks
	private CategoryRuleParameterValueMapper mapper;

	@Before
	public void setUp()
	{
		Mockito.lenient().when(catalog.getId()).thenReturn(CATALOG);
		Mockito.lenient().when(catalogVersion.getVersion()).thenReturn(CATALOG_VERSION);
		Mockito.lenient().when(catalogRuleParameterValueMapper.fromString(CATALOG)).thenReturn(catalog);
		Mockito.lenient().when(catalogVersionService.getCatalogVersion(CATALOG, CATALOG_VERSION)).thenReturn(catalogVersion);
		mapper.setDelimiter("::");
		mapper.setCatalogVersionName(CATALOG_VERSION);
	}

	@Test
	public void nullTestFromString()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		mapper.fromString(null);
	}

	@Test
	public void nullTestToString()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		mapper.toString(null);
	}

	@Test
	public void noCategoryFoundTest()
	{
		//Mockito.lenient().when
		Mockito.lenient().when(categoryDao.findCategoriesByCode(Mockito.anyString())).thenReturn(null);

		//expect
		expectedException.expect(RuleParameterValueMapperException.class);

		//when
		mapper.fromString(ANY_STRING);
	}

	@Test
	public void mappedCategoryTest()
	{
		//Mockito.lenient().when
		final List<CategoryModel> categories = new ArrayList<CategoryModel>();
		categories.add(Mockito.mock(CategoryModel.class));
		categories.add(Mockito.mock(CategoryModel.class));

		Mockito.lenient().when(categoryDao.findCategoriesByCode(Mockito.anyString())).thenReturn(categories);

		//when
		final CategoryModel mappedCategory = mapper.fromString(ANY_STRING);

		//then
		assertTrue(categories.contains(mappedCategory));
	}

	@Test
	public void mappedCategoryIsFirstFoundTest()
	{
		//Mockito.lenient().when
		final List<CategoryModel> categories = new ArrayList<CategoryModel>();
		categories.add(Mockito.mock(CategoryModel.class));
		categories.add(Mockito.mock(CategoryModel.class));

		Mockito.lenient().when(categoryDao.findCategoriesByCode(Mockito.anyString())).thenReturn(categories);

		//when
		final CategoryModel mappedCategory = mapper.fromString(ANY_STRING);

		//then
		Assert.assertEquals(categories.get(0), mappedCategory);
	}

	@Test
	public void objectToStringTest()
	{
		//Mockito.lenient().when
		whenStringRepresentationAttribute();
		Mockito.lenient().when(category.getCatalogVersion()).thenReturn(catalogVersion);
		Mockito.lenient().when(catalogVersion.getCatalog()).thenReturn(catalog);
		Mockito.lenient().when(catalogRuleParameterValueMapper.toString(category.getCatalogVersion().getCatalog())).thenReturn(CATALOG);

		//when
		final String stringRepresentation = mapper.toString(category);

		//then
		Assert.assertEquals(ANY_STRING + "::" + CATALOG, stringRepresentation);
	}

	@Test
	public void mappedCatalogAwareCategoryTest()
	{
		//Mockito.lenient().when
		final List<CategoryModel> categories = new ArrayList<>();
		categories.add(Mockito.mock(CategoryModel.class));
		categories.add(Mockito.mock(CategoryModel.class));

		Mockito.lenient().when(categoryDao.findCategoriesByCode(catalogVersion, "code")).thenReturn(categories);

		//when
		final CategoryModel mappedCategory = mapper.fromString(CATALOG_AWARE_VALUE);

		//then
		assertTrue(categories.contains(mappedCategory));

		verify(catalogRuleParameterValueMapper).fromString(CATALOG);
	}

	private void whenStringRepresentationAttribute()
	{
		Mockito.lenient().when(category.getCode()).thenReturn(ANY_STRING);
	}
}
