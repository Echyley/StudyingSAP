/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.widgets.charts.facetchart.filter;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.core.Executable;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;


@RunWith(MockitoJUnitRunner.class)
public class CatalogVersionFacetChartFilterAdapterTest
{
	@Spy
	@InjectMocks
	private CatalogVersionFacetChartFilterAdapter adapter = new CatalogVersionFacetChartFilterAdapter();

	@Mock
	private LabelService labelService;

	@Mock
	private WidgetInstanceManager widgetInstanceManager;

	@Mock
	private Executable onDeletedCallback;

	@Test
	public void shouldDeleteFilter()
	{
		// given
		when(adapter.getNamesOfDeletedFilters(any(CockpitEvent.class))).thenReturn(Collections.singletonList("selectedFilter"));

		// when
		adapter.deleteFilter(mock(CockpitEvent.class), "selectedFilterValue", widgetInstanceManager, onDeletedCallback);

		// then
		verify(adapter).deleteFilterFromModel(anyString(), anyString(), any(WidgetInstanceManager.class), any(Executable.class));
	}

	@Test
	public void shouldDeleteMultipleFilters()
	{
		// given
		final List<String> filters = Arrays.asList("selectedFilterOne", "selectedFilterTwo", "selectedFilterThree");
		when(adapter.getNamesOfDeletedFilters(any(CockpitEvent.class))).thenReturn(filters);

		// when
		adapter.deleteFilter(mock(CockpitEvent.class), "selectedFilterValue", widgetInstanceManager, onDeletedCallback);

		// then
		verify(adapter, times(filters.size())).deleteFilterFromModel(anyString(), anyString(), any(WidgetInstanceManager.class),
				any(Executable.class));
	}

	@Test
	public void shouldNotDeleteFilter()
	{
		// given
		when(adapter.getNamesOfDeletedFilters(any(CockpitEvent.class))).thenReturn(Collections.emptyList());

		// when
		adapter.deleteFilter(mock(CockpitEvent.class), "selectedValue", widgetInstanceManager, onDeletedCallback);

		// then
		verify(adapter, times(0)).deleteFilterFromModel(anyString(), anyString(), any(WidgetInstanceManager.class),
				any(Executable.class));
	}

	@Test
	public void shouldGetNameOfDeletedFilter()
	{
		// given
		final String expectedVersionName = "testVersion";
		final CockpitEvent event = createEventStub(expectedVersionName);

		// when
		final List<String> namesOfDeletedFilters = adapter.getNamesOfDeletedFilters(event);

		// then
		assertThat(namesOfDeletedFilters.isEmpty()).isFalse();
		assertThat(namesOfDeletedFilters).contains(expectedVersionName);
	}

	@Test
	public void shouldNotGetNameOfDeletedFilterWhenVersionIsEmpty()
	{
		// given
		final CockpitEvent event = createEventStub(StringUtils.EMPTY);

		// when
		final List<String> namesOfDeletedFilters = adapter.getNamesOfDeletedFilters(event);

		// then
		assertThat(namesOfDeletedFilters.isEmpty()).isTrue();
	}

	@Test
	public void shouldNotGetNameOfDeletedFilterWhenVersionIsNull()
	{
		// given
		final CockpitEvent event = createEventStub(null);

		// when
		final List<String> namesOfDeletedFilters = adapter.getNamesOfDeletedFilters(event);

		// then
		assertThat(namesOfDeletedFilters.isEmpty()).isTrue();
	}

	@Test
	public void shouldGetCatalogVersion()
	{
		// given
		final String expectedVersionName = "testVersion";
		final ClassificationSystemVersionModel model = createClassificationSystemVersionModelStub(expectedVersionName);

		// when
		Optional<String> catalogVersion = adapter.getCatalogVersion(model);

		// then
		assertThat(catalogVersion.isPresent()).isTrue();
		catalogVersion.ifPresent(receivedVersionName -> assertThat(receivedVersionName).isEqualTo(expectedVersionName));
	}

	@Test
	public void shouldNotGetCatalogVersionWhenVersionIsEmpty()
	{
		// given
		final ClassificationSystemVersionModel model = createClassificationSystemVersionModelStub(StringUtils.EMPTY);

		// when
		final Optional<String> catalogVersion = adapter.getCatalogVersion(model);

		// then
		assertThat(catalogVersion.isPresent()).isFalse();
	}

	@Test
	public void shouldNotGetCatalogVersionWhenVersionIsNull()
	{
		// given
		ClassificationSystemVersionModel model = createClassificationSystemVersionModelStub(null);

		// when
		final Optional<String> catalogVersion = adapter.getCatalogVersion(model);

		// then
		assertThat(catalogVersion.isPresent()).isFalse();
	}

	@Test
	public void shouldBeAbleToDeleteFilter()
	{
		// given
		final String version = "testVersion";
		final ClassificationSystemVersionModel model = createClassificationSystemVersionModelStub(version);

		// when
		final boolean isAbleToDeleteFilter = adapter.canDelete(model, version);

		// then
		assertThat(isAbleToDeleteFilter).isTrue();
	}

	@Test
	public void shouldNotBeAbleToDeleteFilterWhenVersionIsDifferentThanNameOfDeletedItem()
	{
		// given
		final String version = "testVersion";
		final ClassificationSystemVersionModel model = createClassificationSystemVersionModelStub(version);

		// when
		final boolean isAbleToDeleteFilter = adapter.canDelete(model, "anotherVersion");

		// then
		assertThat(isAbleToDeleteFilter).isFalse();
	}

	@Test
	public void shouldNotBeAbleToDeleteFilterWhenTypeIsDifferentThanExpected()
	{
		// given
		final String version = "testVersion";
		final Object model = new Object();

		// when
		final boolean isAbleToDeleteFilter = adapter.canDelete(model, version);

		// then
		assertThat(isAbleToDeleteFilter).isFalse();
	}

	private CockpitEvent createEventStub(final String version)
	{
		final ClassificationSystemVersionModel model = createClassificationSystemVersionModelStub(version);
		final List<ClassificationSystemVersionModel> data = Collections.singletonList(model);
		return new DefaultCockpitEvent("testEventStub", data, new Object());
	}

	private ClassificationSystemVersionModel createClassificationSystemVersionModelStub(String version)
	{
		final ClassificationSystemModel classificationSystemModel = new ClassificationSystemModel();
		final ClassificationSystemVersionModel model = new ClassificationSystemVersionModel();
		model.setCatalog(classificationSystemModel);
		model.setVersion(version);
		return model;
	}
}
