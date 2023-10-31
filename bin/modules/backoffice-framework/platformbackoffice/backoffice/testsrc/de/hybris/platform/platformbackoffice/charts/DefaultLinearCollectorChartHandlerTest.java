/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.charts;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.chart.Charts;
import org.zkoss.chart.Exporting;
import org.zkoss.chart.Legend;
import org.zkoss.chart.Marker;
import org.zkoss.chart.State;
import org.zkoss.chart.States;
import org.zkoss.chart.Title;
import org.zkoss.chart.XAxis;
import org.zkoss.chart.YAxis;
import org.zkoss.chart.plotOptions.AreaPlotOptions;
import org.zkoss.chart.plotOptions.PlotOptions;

import com.hybris.cockpitng.core.util.impl.TypedSettingsMap;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;


@ExtensibleWidget(level = ExtensibleWidget.ALL)
@RunWith(MockitoJUnitRunner.class)
public class DefaultLinearCollectorChartHandlerTest extends AbstractCockpitngUnitTest<DefaultLinearCollectorChartHandler>
{
	@InjectMocks
	private DefaultLinearCollectorChartHandler defaultLinearCollectorChartHandler;

	@Mock
	private WidgetInstanceManager widgetInstanceManager;
	@Mock
	private TypedSettingsMap widgetSettings;
	@Mock
	private Charts charts;
	@Mock
	private Title title;
	@Mock
	private Exporting exporting;
	@Mock
	private Legend legend;
	@Mock
	private XAxis xAxis;
	@Mock
	private YAxis yAxis;
	@Mock
	private PlotOptions plotOptions;
	@Mock
	private AreaPlotOptions areaPlotOptions;
	@Mock
	private Marker marker;
	@Mock
	private States states;
	@Mock
	private State hover;

	@Before
	public void setUp()
	{
		when(widgetInstanceManager.getWidgetSettings()).thenReturn(widgetSettings);
		when(widgetSettings.getString(DefaultLinearCollectorChartHandler.ZOOM_TYPE)).thenReturn("type");
		when(charts.getTitle()).thenReturn(title);
		when(charts.getExporting()).thenReturn(exporting);
		when(charts.getLegend()).thenReturn(legend);
		when(charts.getXAxis()).thenReturn(xAxis);
		when(charts.getYAxis()).thenReturn(yAxis);
		when(charts.getPlotOptions()).thenReturn(plotOptions);
		when(plotOptions.getArea()).thenReturn(areaPlotOptions);
		when(areaPlotOptions.getMarker()).thenReturn(marker);
		when(areaPlotOptions.getStates()).thenReturn(states);
		when(states.getHover()).thenReturn(hover);
	}

	@Test
	public void initializeChart()
	{
		defaultLinearCollectorChartHandler.initializeChart(widgetInstanceManager, charts);

		verify(legend).setEnabled(false);
		verify(charts).getTitle();
		verify(charts).setSpacingRight(Matchers.any(Integer.class));
		verify(charts).setSpacingLeft(Matchers.any(Integer.class));
		verify(charts).setSpacingTop(Matchers.any(Integer.class));
		verify(charts).setSpacingRight(Matchers.any(Integer.class));
	}

	@Override
	protected Class<DefaultLinearCollectorChartHandler> getWidgetType()
	{
		return DefaultLinearCollectorChartHandler.class;
	}
}
