/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.widgets.charts.facetchart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.chart.Charts;
import org.zkoss.chart.plotOptions.ColumnPlotOptions;
import org.zkoss.chart.plotOptions.PlotOptions;

import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class DataQualityFacetChartComposerTest
{
	@InjectMocks
	@Spy
	private DataQualityFacetChartComposer dataQualityFacetChartComposer;

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		when(dataQualityFacetChartComposer.getXAxisLabel()).thenReturn("xLabel");
	}

	@Test
	public void composeChart()
	{
		final Charts charts = new Charts();
		dataQualityFacetChartComposer.composeChart(charts);

		assertThat(charts.getXAxis().getTitle().getText()).isEqualTo("xLabel");
	}

	@Test
	public void shouldNotAllowSelectingPoints()
	{
		final Charts charts = spy(new Charts());
		final PlotOptions plotOptions = mock(PlotOptions.class);
		final ColumnPlotOptions columnPlotOptions = spy(new ColumnPlotOptions());
		doReturn(plotOptions).when(charts).getPlotOptions();
		doReturn(columnPlotOptions).when(plotOptions).getColumn();

		dataQualityFacetChartComposer.composeChart(charts);

		verify(columnPlotOptions).setAllowPointSelect(false);
	}
}
