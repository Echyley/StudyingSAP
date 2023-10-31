/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.widgets.charts.facetchart.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Div;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.core.Executable;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReferenceEditorFacetChartFilterRendererTest
{
	private static final String FACET_NAME = "myFacet";

	@InjectMocks
	@Spy
	private ReferenceEditorFacetChartFilterRenderer renderer;
	private WidgetInstanceManager widgetInstanceManager;
	@Spy
	private Div filterContainer = new Div();
	@Mock
	private BiConsumer<String, Set<String>> facetSelectionListener;
	@Mock
	private ReferenceEditorFacetChartFilterAdapter adapter;

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		widgetInstanceManager = CockpitTestUtil.mockWidgetInstanceManager();
	}

	@Test
	public void shouldRender3Elements()
	{
		doReturn(new Editor()).when(renderer).createEditor();

		renderer.renderFilters(widgetInstanceManager, filterContainer, facetSelectionListener);

		verify(filterContainer).appendChild(any());
		assertThat(filterContainer.getChildren().get(0).getChildren()).hasSize(3);//we add one div with tree elements (label, clear all button, editor)
	}

	@Test
	public void shouldStoreSelectedValueAndFireEventWhenNewFilterWasSet()
	{
		//given
		renderer.setFacetName(FACET_NAME);

		doReturn(new Editor()).when(renderer).createEditor();
		renderer.renderFilters(widgetInstanceManager, filterContainer, facetSelectionListener);

		//when
		renderer.filterChanged(mock(Event.class));

		//then
		final WidgetModel model = widgetInstanceManager.getModel();
		verify(model).setValue(eq("selectedFilter_" + FACET_NAME), any());
		verify(facetSelectionListener).accept(eq(FACET_NAME), any());
	}

	@Test
	public void shouldSetEmptyValueEndTriggerEventWhenClearAllFiltersWasClicked()
	{
		//given
		renderer.setFacetName(FACET_NAME);

		final Editor mockedEditor = mock(Editor.class);
		doReturn(mockedEditor).when(renderer).createEditor();
		doReturn(Optional.of(mockedEditor)).when(renderer).findEditor(any());

		renderer.renderFilters(widgetInstanceManager, filterContainer, facetSelectionListener);

		//when
		renderer.removeAllFilters(mock(Event.class));

		//then
		final WidgetModel model = widgetInstanceManager.getModel();
		verify(model).setValue("selectedFilter_" + FACET_NAME, Collections.emptySet());
		verify(facetSelectionListener).accept(FACET_NAME, Collections.emptySet());
		verify(mockedEditor).setValue(null);
	}

	@Test
	public void shouldDeleteFilterOnFilterDeletedEvent()
	{
		// given
		final CockpitEvent event = mock(CockpitEvent.class);
		final Executable onDeletedCallback = mock(Executable.class);
		renderer.setFacetName(FACET_NAME);
		doReturn(mock(Editor.class)).when(renderer).createEditor();
		renderer.renderFilters(widgetInstanceManager, filterContainer, facetSelectionListener);

		// when
		renderer.onFilterDeleted(event, onDeletedCallback);

		// then
		verify(adapter).deleteFilter(any(CockpitEvent.class), anyString(), any(WidgetInstanceManager.class), any(Executable.class));
	}
}
