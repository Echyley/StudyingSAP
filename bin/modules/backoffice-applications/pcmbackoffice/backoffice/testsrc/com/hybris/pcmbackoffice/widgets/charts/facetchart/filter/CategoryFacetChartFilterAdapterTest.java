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

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;

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


@RunWith(MockitoJUnitRunner.class)
public class CategoryFacetChartFilterAdapterTest
{
	@Spy
	@InjectMocks
	private CategoryFacetChartFilterAdapter adapter = new CategoryFacetChartFilterAdapter();

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
		final String expectedCode = "testCode";
		final CockpitEvent event = createEventStub(expectedCode);

		// when
		final List<String> namesOfDeletedFilters = adapter.getNamesOfDeletedFilters(event);

		// then
		assertThat(namesOfDeletedFilters.isEmpty()).isFalse();
		assertThat(namesOfDeletedFilters).contains(expectedCode);
	}

	@Test
	public void shouldNotGetNameOfDeletedFilterWhenCodeIsNull()
	{
		// given
		final CockpitEvent event = createEventStub(null);

		// when
		final List<String> namesOfDeletedFilter = adapter.getNamesOfDeletedFilters(event);

		// then
		assertThat(namesOfDeletedFilter.isEmpty()).isTrue();
	}

	@Test
	public void shouldGetCategoryCode()
	{
		// given
		final String code = "testCode";
		final ClassificationClassModel model = createClassificationClassModelStub(code);

		// when
		final Optional<String> categoryCode = adapter.getCategoryCode(model);

		// then
		assertThat(categoryCode.isPresent()).isTrue();
		categoryCode.ifPresent(receivedCode -> assertThat(receivedCode).isEqualTo(code));
	}

	@Test
	public void shouldNotGetCategoryCodeWhenCodeIsEmpty()
	{
		// given
		final ClassificationClassModel model = createClassificationClassModelStub(StringUtils.EMPTY);

		// when
		final Optional<String> categoryCode = adapter.getCategoryCode(model);

		// then
		assertThat(categoryCode.isPresent()).isFalse();
	}

	@Test
	public void shouldNotGetCategoryCodeWhenCodeIsNull()
	{
		// given
		final ClassificationClassModel model = createClassificationClassModelStub(null);

		// when
		final Optional<String> categoryCode = adapter.getCategoryCode(model);

		// then
		assertThat(categoryCode.isPresent()).isFalse();
	}

	@Test
	public void shouldBeAbleToDeleteFilter()
	{
		// given
		final String code = "testCode";
		final ClassificationClassModel model = createClassificationClassModelStub(code);

		// when
		final boolean isAbleToDeleteFilter = adapter.canDelete(model, code);

		// then
		assertThat(isAbleToDeleteFilter).isTrue();
	}

	@Test
	public void shouldNotBeAbleToDeleteFilterWheCodeIsDifferentThanNameOfDeletedItem()
	{
		// given
		final String code = "testCode";
		final ClassificationClassModel model = createClassificationClassModelStub(code);

		// when
		final boolean isAbleToDeleteFilter = adapter.canDelete(model, "anotherCode");

		// then
		assertThat(isAbleToDeleteFilter).isFalse();
	}

	@Test
	public void shouldNotBeAbleToDeleteFilterWhenTypeIsDifferentThanExpected()
	{
		// given
		final String code = "testCode";
		final Object model = new Object();

		// when
		final boolean isAbleToDeleteFilter = adapter.canDelete(model, code);

		// then
		assertThat(isAbleToDeleteFilter).isFalse();
	}

	private CockpitEvent createEventStub(final String code)
	{
		final ClassificationClassModel model = createClassificationClassModelStub(code);
		final List<ClassificationClassModel> data = Collections.singletonList(model);
		return new DefaultCockpitEvent("testEventStub", data, new Object());
	}

	private ClassificationClassModel createClassificationClassModelStub(final String code)
	{
		final ClassificationClassModel model = new ClassificationClassModel();
		model.setCode(code);
		return model;
	}
}
