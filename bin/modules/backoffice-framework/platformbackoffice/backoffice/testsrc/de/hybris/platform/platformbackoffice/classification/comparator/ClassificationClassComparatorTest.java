/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.classification.comparator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.core.util.CockpitProperties;


@RunWith(MockitoJUnitRunner.class)
public class ClassificationClassComparatorTest
{

	private static final String CONFIG_KEY_DEEP_SORT = "classification-tab-renderer.deep-sorting.enabled";

	@InjectMocks
	private ClassificationClassComparator comparator;

	@Mock
	private CockpitProperties cockpitProperties;

	@Mock
	private ClassificationClassModel leftObject;

	@Mock
	private ClassificationClassModel rightObject;

	@Mock
	private ClassificationSystemVersionModel leftCatalogVersion;

	@Mock
	private ClassificationSystemModel leftCatalog;

	@Mock
	private ClassificationSystemVersionModel rightCatalogVersion;

	@Mock
	private ClassificationSystemModel rightCatalog;

	@Before
	public void setUp()
	{
		when(leftObject.getCatalogVersion()).thenReturn(leftCatalogVersion);
		when(leftCatalogVersion.getCatalog()).thenReturn(leftCatalog);
		when(rightObject.getCatalogVersion()).thenReturn(rightCatalogVersion);
		when(rightCatalogVersion.getCatalog()).thenReturn(rightCatalog);
	}

	@Test
	public void namesShouldBeEqual()
	{
		doReturn("n1").when(leftObject).getName();
		doReturn("n1").when(rightObject).getName();

		assertThat(getComparator().compare(leftObject, rightObject)).isEqualTo(0);
	}

	@Test
	public void namesShouldNotBeEqual()
	{
		doReturn("n1").when(leftObject).getName();
		doReturn("n2").when(rightObject).getName();

		assertThat(getComparator().compare(leftObject, rightObject)).isLessThan(0);
	}

	@Test
	public void leftObjectShouldContainRightObject()
	{
		final Collection<?> models = Arrays.asList(rightObject);
		doReturn(models).when(leftObject).getAllSubcategories();
		doReturn(true).when(cockpitProperties).getBoolean(eq(CONFIG_KEY_DEEP_SORT), anyBoolean());

		assertThat(getComparator().compare(leftObject, rightObject)).isLessThan(0);
	}

	@Test
	public void rightObjectShouldContainLeftObject()
	{
		final Collection<?> models = Arrays.asList(leftObject);
		doReturn(models).when(rightObject).getAllSubcategories();
		doReturn(true).when(cockpitProperties).getBoolean(eq(CONFIG_KEY_DEEP_SORT), anyBoolean());

		assertThat(getComparator().compare(leftObject, rightObject)).isGreaterThan(0);
	}

	@Test
	public void codesShouldBeEqual()
	{
		doReturn("n").when(leftObject).getName();
		doReturn("n").when(rightObject).getName();

		doReturn("n1").when(leftObject).getCode();
		doReturn("n2").when(rightObject).getCode();

		assertThat(getComparator().compare(leftObject, rightObject)).isLessThan(0);
	}

	@Test
	public void orderShouldBeEqual()
	{
		doReturn(0).when(leftObject).getOrder();
		doReturn(0).when(rightObject).getOrder();

		doReturn("a").when(leftObject).getName();
		doReturn("b").when(rightObject).getName();

		assertThat(getComparator().compare(leftObject, rightObject)).isLessThan(0);
	}

	@Test
	public void orderShouldNotBeEqual()
	{
		doReturn(1).when(leftObject).getOrder();
		doReturn(0).when(rightObject).getOrder();

		assertThat(getComparator().compare(leftObject, rightObject)).isGreaterThan(0);
	}

	@Test
	public void orderAndNameShouldBeEqual()
	{
		doReturn(0).when(leftObject).getOrder();
		doReturn(0).when(rightObject).getOrder();

		doReturn("a").when(leftObject).getName();
		doReturn("a").when(rightObject).getName();

		assertThat(getComparator().compare(leftObject, rightObject)).isEqualTo(0);
	}

	@Test
	public void rightObjectShouldBeNull()
	{
		doReturn(1).when(leftObject).getOrder();
		doReturn(null).when(rightObject).getOrder();

		assertThat(getComparator().compare(leftObject, rightObject)).isLessThan(0);
	}

	@Test
	public void leftObjectShouldBeNull()
	{
		doReturn(null).when(leftObject).getOrder();
		doReturn(1).when(rightObject).getOrder();



		assertThat(getComparator().compare(leftObject, rightObject)).isGreaterThan(0);
	}

	@Test
	public void bothShouldBeNull()
	{
		doReturn(null).when(leftObject).getOrder();
		doReturn(null).when(rightObject).getOrder();

		doReturn("b").when(leftObject).getName();
		doReturn("a").when(rightObject).getName();

		assertThat(getComparator().compare(leftObject, rightObject)).isGreaterThan(0);
	}

	public ClassificationClassComparator getComparator()
	{
		return comparator;
	}
}
