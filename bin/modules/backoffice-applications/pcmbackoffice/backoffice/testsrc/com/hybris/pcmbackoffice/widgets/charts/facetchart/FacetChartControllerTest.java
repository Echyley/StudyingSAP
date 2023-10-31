/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.widgets.charts.facetchart;

import static com.hybris.pcmbackoffice.widgets.charts.facetchart.FacetChartController.SELECTED_FACETS;
import static com.hybris.pcmbackoffice.widgets.charts.facetchart.FacetChartController.SOCKET_IN_INIT_SEARCH;
import static com.hybris.pcmbackoffice.widgets.charts.facetchart.FacetChartController.SOCKET_IN_PAGEABLE;
import static com.hybris.pcmbackoffice.widgets.charts.facetchart.FacetChartController.SOCKET_OUT_FACETS;
import static com.hybris.pcmbackoffice.widgets.charts.facetchart.FacetChartController.SOCKET_OUT_INITIAL_SEARCH_CONTEXT;
import static com.hybris.pcmbackoffice.widgets.charts.facetchart.FacetChartController.SOCKET_OUT_INITIAL_SEARCH_DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.zkoss.chart.Charts;
import org.zkoss.chart.ChartsEvent;
import org.zkoss.chart.Color;
import org.zkoss.chart.Exporting;
import org.zkoss.chart.Point;
import org.zkoss.chart.Series;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Popup;

import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.dataaccess.context.Context;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.search.data.FullTextSearchData;
import com.hybris.cockpitng.search.data.facet.FacetData;
import com.hybris.cockpitng.search.data.pageable.FullTextSearchPageable;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredGlobalCockpitEvent;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;


@DeclaredInput(value = SOCKET_IN_PAGEABLE, socketType = FullTextSearchPageable.class)
@DeclaredInput(value = SOCKET_IN_INIT_SEARCH, socketType = Object.class)
@DeclaredViewEvent(componentID = FacetChartController.COMPONENT_SHOW_FILTERS_BUTTON_ID, eventName = Events.ON_CLICK)
@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, scope = CockpitEvent.SESSION)
@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)
@DeclaredGlobalCockpitEvent(eventName = Events.ON_CLIENT_INFO, scope = CockpitEvent.SESSION)
@NullSafeWidget
@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class FacetChartControllerTest extends AbstractWidgetUnitTest<FacetChartController>
{
	public static final String FACET = "facet";

	@Spy
	@InjectMocks
	private FacetChartController facetChartController;
	@Mock
	private Charts charts;
	@Mock
	private FacetChartDataExtractor facetChartDataExtractor;
	@Mock
	private FacetChartBottomPanelRenderer facetChartBottomPanelRenderer;
	@Mock
	private DefaultFacetChartFiltersRenderer solrChartFiltersRenderer;
	@Mock
	private FacetChartFacetChooserRenderer facetChartFacetChooserRenderer;
	@Mock
	private FullTextSearchPageable fullTextSearchPageableMock;
	@Mock
	private FullTextSearchData fullTextSearchDataMock;
	@Mock
	private Context contextMock;
	@Mock
	private FacetChartRightPanelRenderer facetChartRightPanelRenderer;
	@Mock
	private FacetChartComposer facetChartComposer;
	@Mock
	private Label filtersCounterLabel;
	@Mock
	private Popup filtersPopup;
	@Mock
	private Div filtersContainer;
	@Mock
	private Button showFilters;
	@Mock
	private AdvancedSearchData originalQuery;

	@Override
	protected FacetChartController getWidgetController()
	{
		return facetChartController;
	}

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		when(charts.getSeries()).thenReturn(mock(Series.class));
		when(charts.getExporting()).thenReturn(mock(Exporting.class));
		when(charts.getColors()).thenReturn(Collections.singletonList(new Color("red")));
		when(facetChartDataExtractor.getPoints(any(), any())).thenReturn(Collections.singletonList(new Point("name", 5)));

		when(fullTextSearchPageableMock.getFullTextSearchData()).thenReturn(fullTextSearchDataMock);
		when(fullTextSearchDataMock.getContext()).thenReturn(contextMock);
		when(contextMock.getAttribute(any())).thenReturn(mock(AdvancedSearchData.class));

		doNothing().when(facetChartController).initBeans();
	}

	@Test
	public void shouldInitializeAndRenderFilters()
	{
		//given
		doReturn(Collections.singletonList("facetName")).when(facetChartController).getFacetNames();
		when(solrChartFiltersRenderer.getRenderers()).thenReturn(Collections.singletonList(mock(FacetChartFiltersRenderer.class)));

		//when
		facetChartController.initialize(mock(Component.class));

		//then
		verify(solrChartFiltersRenderer).renderFilters(any(), any(), any());
	}

	@Test
	public void shouldRenderFacetChooser()
	{
		//given
		doReturn(fullTextSearchDataMock).when(facetChartController).getFullTextSearchData();

		//when
		facetChartController.processSearchResultAfterSearchExecution();

		//when
		verify(facetChartFacetChooserRenderer).render(any(), any(), any(), any());
	}

	@Test
	public void shouldRenderFilters()
	{
		//given
		widgetModel.setValue("initSearchPerformed", true);
		when(solrChartFiltersRenderer.getRenderers()).thenReturn(Collections.singletonList(mock(FacetChartFiltersRenderer.class)));
		doReturn(fullTextSearchDataMock).when(facetChartController).getFullTextSearchData();

		//when
		facetChartController.processSearchResultAfterSearchExecution();

		//when
		verify(solrChartFiltersRenderer).renderFilters(any(), any(), any());
	}

	@Test
	public void shouldShowFilterComponents()
	{
		//given
		widgetModel.setValue("initSearchPerformed", true);
		when(solrChartFiltersRenderer.getRenderers()).thenReturn(Collections.singletonList(mock(FacetChartFiltersRenderer.class)));
		doReturn(fullTextSearchDataMock).when(facetChartController).getFullTextSearchData();

		//when
		facetChartController.processSearchResultAfterSearchExecution();

		//when
		verify(facetChartController).showFilterComponents(true);
	}

	@Test
	public void shouldHideFilterComponents()
	{
		//given
		widgetModel.setValue("initSearchPerformed", true);
		when(solrChartFiltersRenderer.getRenderers()).thenReturn(Collections.emptyList());
		doReturn(fullTextSearchDataMock).when(facetChartController).getFullTextSearchData();

		//when
		facetChartController.processSearchResultAfterSearchExecution();

		//when
		verify(facetChartController).showFilterComponents(false);
	}

	@Test
	public void shouldInitSearch()
	{
		//given
		//when
		executeInputSocketEvent(SOCKET_IN_INIT_SEARCH);

		//then
		assertSocketOutput(SOCKET_OUT_INITIAL_SEARCH_CONTEXT, 1, (Predicate<Object>) data -> data instanceof Context);
		assertSocketOutput(SOCKET_OUT_INITIAL_SEARCH_DATA, 1, (Predicate<Object>) data -> data instanceof AdvancedSearchData);
	}

	@Test
	public void shouldInitSearchWithSelectedFacetsAndFilters()
	{
		//given
		final Map<String, Set<String>> selectedFacets = new HashMap<>();
		selectedFacets.put(FACET, Collections.singleton("facetValue"));
		when(widgetInstanceManager.getModel().getValue(SELECTED_FACETS, Map.class)).thenReturn(selectedFacets);

		//when
		executeInputSocketEvent(SOCKET_IN_INIT_SEARCH);

		//then
		final ArgumentCaptor<AdvancedSearchData> argumentCaptor = ArgumentCaptor.forClass(AdvancedSearchData.class);
		verify(widgetInstanceManager).sendOutput(eq(SOCKET_OUT_INITIAL_SEARCH_DATA), argumentCaptor.capture());

		final AdvancedSearchData sendQuery = argumentCaptor.getValue();
		assertThat(sendQuery.getSelectedFacets()).containsKeys(FACET);
		assertThat(sendQuery.getSelectedFacets().get(FACET)).hasSize(1);
		assertThat(sendQuery.getSelectedFacets().get(FACET)).contains("facetValue");
	}

	@Test
	public void shouldExecuteSearchOperationOnInputEvent()
	{
		//given
		//when
		executeInputSocketEvent(SOCKET_IN_PAGEABLE, fullTextSearchPageableMock);

		//then
		verify(facetChartController).executeSearchOperation(fullTextSearchPageableMock);
	}

	@Test
	public void shouldRenderChart()
	{
		//given
		doReturn(fullTextSearchDataMock).when(facetChartController).getFullTextSearchData();
		widgetModel.setValue("initSearchPerformed", true);

		//when
		facetChartController.processSearchResultAfterSearchExecution();

		//then
		verify(facetChartController).render();
		verify(facetChartDataExtractor).getPoints(any(), any());
		verify(facetChartBottomPanelRenderer).render(any(), any(), any());
		verify(facetChartController).renderFilters();
		verify(facetChartController).renderBottomPanel(any());
		verify(facetChartController).renderRightPanel(any());
	}

	@Test
	public void shouldMoveToCollectionBrowserAfterChartClick()
	{
		//given
		final AdvancedSearchData advancedSearchDataMock = mock(AdvancedSearchData.class);
		doReturn(advancedSearchDataMock).when(facetChartController).createCopyAdvancedSearchData(any());
		doReturn(originalQuery).when(facetChartController).getOriginalQuery();

		executeInputSocketEvent(SOCKET_IN_PAGEABLE, fullTextSearchPageableMock);

		final ChartsEvent event = mock(ChartsEvent.class);
		when(event.getPoint()).thenReturn(mock(Point.class));

		//when
		facetChartController.handleClickOnPoint(event);

		//then
		assertSocketOutput(SOCKET_OUT_FACETS, advancedSearchDataMock);
	}

	@Test
	public void shouldRunSearchAfterNewFacetSelection()
	{
		//given
		final AdvancedSearchData advancedSearchDataMock = mock(AdvancedSearchData.class);
		doReturn(advancedSearchDataMock).when(facetChartController).createCopyAdvancedSearchData(any());
		doReturn(originalQuery).when(facetChartController).getOriginalQuery();

		//when
		final Set<String> facetSelected = new HashSet<>();
		facetChartController.applyFacetSelection("facetName", facetSelected);

		//then
		assertSocketOutput(SOCKET_OUT_INITIAL_SEARCH_DATA, 1, advancedSearchDataMock);
		verify(widgetModel).setValue(eq(SELECTED_FACETS), any());
	}

	@Test
	public void testConvertFacetNameToFacetData()
	{
		// given
		doReturn(fullTextSearchDataMock).when(facetChartController).getFullTextSearchData();

		final List<String> facetNames = new LinkedList<>();
		facetNames.add("A");
		facetNames.add("C");

		final List<FacetData> facetData = new LinkedList<>();
		facetData.add(facetData("A", "dA"));
		facetData.add(facetData("B", "dB"));
		facetData.add(facetData("C", "dC"));
		facetData.add(facetData("D", "dD"));

		doReturn(facetData).when(fullTextSearchDataMock).getFacets();

		// when
		final List<FacetData> result = facetChartController.convertToFacets(facetNames);

		// then
		assertThat(result.size()).isEqualTo(2);
		assertThat(result).contains(facetData.get(0), facetData.get(2));
		verify(fullTextSearchDataMock).getFacets();
	}

	@Test
	public void shouldUpdateFiltersCounterLabel()
	{
		// given
		final Map<String, Set<String>> selectedFacets = new HashMap<>();
		selectedFacets.put(FACET, Collections.singleton("facetValue"));
		final int filtersNumber = selectedFacets.size();

		// when
		facetChartController.updateFiltersCounter(selectedFacets);

		// then
		verify(facetChartController).setFiltersCounterLabelValue(filtersNumber);
	}

	@Test
	public void shouldApplyFacetSelectionEvenIfSearchQueryNotExists()
	{
		//given
		final Object originalQuery = null;
		doReturn(originalQuery).when(facetChartController).getOriginalQuery();

		//when
		final Set<String> facetSelected = new HashSet<>();
		facetChartController.applyFacetSelection("facetName", facetSelected);

		//then
		verify(widgetModel).setValue(eq(SELECTED_FACETS), any());
		verify(facetChartController).updateFiltersCounter(any());
		verify(facetChartController).setValue(any(), any());
		verify(facetChartController, never()).createCopyAdvancedSearchData(any());
	}

	@Test
	public void shouldTriggerRefreshDataWhenNewProductModelSaved()
	{
		//given
		final ProductModel product = mock(ProductModel.class);
		doReturn(true).when(facetChartController).isNewTheUpdatedObject(any());
		final AdvancedSearchData advancedSearchDataMock = mock(AdvancedSearchData.class);
		doReturn(advancedSearchDataMock).when(facetChartController).createAdvancedSearchData();

		//when
		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, CockpitEvent.SESSION,
				new DefaultCockpitEvent(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, product, null));

		//then
		assertSocketOutput(SOCKET_OUT_INITIAL_SEARCH_DATA, 1, advancedSearchDataMock);
	}

	@Test
	public void shouldNotTriggerRefreshDataWhenProductModelIsNotNew()
	{
		//given
		final ProductModel product = mock(ProductModel.class);
		doReturn(false).when(facetChartController).isNewTheUpdatedObject(any());
		final AdvancedSearchData advancedSearchDataMock = mock(AdvancedSearchData.class);
		doReturn(advancedSearchDataMock).when(facetChartController).createAdvancedSearchData();

		//when
		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, CockpitEvent.SESSION,
				new DefaultCockpitEvent(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, product, null));

		//then
		assertSocketOutput(SOCKET_OUT_INITIAL_SEARCH_DATA, 0, advancedSearchDataMock);
	}

	@Test
	public void shouldNotRefreshDataOnWrongModel()
	{
		//given
		final ItemModel model = mock(ItemModel.class);
		doReturn(true).when(facetChartController).isNewTheUpdatedObject(any());
		final AdvancedSearchData advancedSearchDataMock = mock(AdvancedSearchData.class);
		doReturn(advancedSearchDataMock).when(facetChartController).createAdvancedSearchData();

		//when
		executeGlobalEvent(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, CockpitEvent.SESSION,
				new DefaultCockpitEvent(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, model, null));

		//then
		assertSocketOutput(SOCKET_OUT_INITIAL_SEARCH_DATA, 0, advancedSearchDataMock);
	}

	@Test
	public void shouldDecrementFiltersCounterLabel()
	{
		// given
		when(facetChartController.getFiltersCounterLabel()).thenReturn(mock(Label.class));
		when(facetChartController.getFiltersCounterLabel().getValue()).thenReturn("3");

		// when
		facetChartController.decrementFiltersCounterLabel();

		// then
		verify(facetChartController).setFiltersCounterLabelValue(2);
	}

	@Test
	public void shouldNotDecrementFiltersCounterLabelWhenItsZero()
	{
		// given
		when(facetChartController.getFiltersCounterLabel()).thenReturn(mock(Label.class));
		when(facetChartController.getFiltersCounterLabel().getValue()).thenReturn("0");

		// when
		facetChartController.decrementFiltersCounterLabel();

		// then
		verify(facetChartController).setFiltersCounterLabelValue(0);
	}

	@Test
	public void shouldStoreValueInModelWhenSearchInitHasBeenPerformed()
	{
		// given

		// when
		facetChartController.initSearch();

		// then
		assertThat(widgetModel.getValue("initSearchPerformed", Boolean.class)).isEqualTo(true);
	}

	@Test
	public void shouldNotRenderWhenInitSearchWasNotInvoked()
	{
		// given
		widgetModel.setValue("pageable", fullTextSearchPageableMock);
		widgetModel.setValue("initSearchPerformed", null);

		// when
		facetChartController.render();

		// then
		verify(facetChartController, never()).composeSeries(any());
	}

	@Test
	public void shouldRenderWhenInitSearchWasInvoked()
	{
		// given
		widgetModel.setValue("pageable", fullTextSearchPageableMock);
		widgetModel.setValue("initSearchPerformed", true);

		// when
		facetChartController.render();

		// then
		verify(facetChartController).composeSeries(any());
	}

	private FacetData facetData(final String name, final String displayName)
	{
		return new FacetData(name, displayName, null, Collections.emptyList());
	}

}
