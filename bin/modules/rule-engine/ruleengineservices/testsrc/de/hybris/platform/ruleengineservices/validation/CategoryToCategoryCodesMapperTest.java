/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.ruleengineservices.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.mockito.Mockito;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Set;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CategoryToCategoryCodesMapperTest
{
	@Mock
	private CategoryService categoryService;

	@Mock
	private CategoryModel category;

	@InjectMocks
	private CategoryToCategoryCodesMapper mapper;

	@Before
	public void setUp()
	{
		Mockito.lenient().when(category.getCode()).thenReturn("categoryCode");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIAEOnNullParameter()
	{
		mapper.apply(null);
	}

	@Test
	public void shouldResultInSingleValueSetOnEmptySupercategories()
	{
		Mockito.lenient().when(categoryService.getAllSubcategoriesForCategory(category)).thenReturn(Collections.emptyList());
		final Set<String> result = mapper.apply(category);
		assertThat(result).isNotEmpty().containsOnly("categoryCode");
	}

	@Test
	public void shouldMapCategoryCodesCorrectly()
	{
		final CategoryModel category1 = mock(CategoryModel.class);
		Mockito.lenient().when(category1.getCode()).thenReturn("category1");

		final CategoryModel category2 = mock(CategoryModel.class);
		Mockito.lenient().when(category2.getCode()).thenReturn("category2");

		final CategoryModel category3 = mock(CategoryModel.class);
		Mockito.lenient().when(category3.getCode()).thenReturn("category3");

		final CategoryModel category4 = mock(CategoryModel.class);
		Mockito.lenient().when(category4.getCode()).thenReturn("category1");

		Mockito.lenient().when(categoryService.getAllSubcategoriesForCategory(category)).thenReturn(List.of(category1, category2, category3,
				category4));

		final Set<String> result = mapper.apply(category);

		assertThat(result).isNotEmpty().containsExactlyInAnyOrder("categoryCode", "category1", "category2", "category3");

		verify(categoryService).getAllSubcategoriesForCategory(category);
	}
}
