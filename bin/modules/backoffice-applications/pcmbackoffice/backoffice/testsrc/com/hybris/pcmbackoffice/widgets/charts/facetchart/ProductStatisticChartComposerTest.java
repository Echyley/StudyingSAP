/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.widgets.charts.facetchart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.chart.Charts;
import org.zkoss.chart.Point;
import org.zkoss.chart.Series;
import org.zkoss.chart.Tooltip;

import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import org.zkoss.chart.plotOptions.ColumnPlotOptions;
import org.zkoss.chart.plotOptions.PiePlotOptions;
import org.zkoss.chart.plotOptions.PlotOptions;


@RunWith(MockitoJUnitRunner.class)
public class ProductStatisticChartComposerTest
{
	@InjectMocks
	@Spy
	private ProductStatisticChartComposer productStatisticChartComposer;

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
	}

	@Test
	public void composeSeries()
	{
		//given
		final Point point1 = new Point("name", 1);
		point1.getDataLabels().setDistance(ProductStatisticChartComposer.CHART_DATA_LABEL_INSIDE_POSITION);
		final Point point2 = new Point("name2", 100);
		point2.getDataLabels().setDistance(ProductStatisticChartComposer.CHART_DATA_LABEL_INSIDE_POSITION);

		final List<Point> points = Arrays.asList(point1, point2);
		final Series series = mock(Series.class);

		when(series.getData()).thenReturn(points);
		when(series.getTooltip()).thenReturn(new Tooltip());

		//when
		productStatisticChartComposer.composeSeries(series);

		//then
		assertThat(point1.getDataLabels().getDistance()).isEqualTo(ProductStatisticChartComposer.CHART_DATA_LABEL_OUTSIDE_POSITION);
		assertThat(point1.getDataLabels().getColor().stringValue()).isEqualTo("black");

		assertThat(point2.getDataLabels().getDistance()).isEqualTo(ProductStatisticChartComposer.CHART_DATA_LABEL_INSIDE_POSITION);

	}

	@Test
	public void shouldComposeWithoutData()
	{
		//given
		final List<Point> points = null;
		final Series series = mock(Series.class);

		when(series.getData()).thenReturn(points);
		when(series.getTooltip()).thenReturn(mock(Tooltip.class));

		//when
		productStatisticChartComposer.composeSeries(series);

		//then
		verify(series.getTooltip()).setHeaderFormat(any());
		verify(series.getTooltip()).setPointFormat(any());
	}

	@Test
	public void shouldNotAllowSelectingPoints()
	{
		final Charts charts = spy(new Charts());
		final PlotOptions plotOptions = mock(PlotOptions.class);
		final PiePlotOptions piePlotOptions = spy(new PiePlotOptions());
		doReturn(plotOptions).when(charts).getPlotOptions();
		doReturn(piePlotOptions).when(plotOptions).getPie();

		productStatisticChartComposer.composeChart(charts);

		verify(piePlotOptions).setAllowPointSelect(false);
	}
}
