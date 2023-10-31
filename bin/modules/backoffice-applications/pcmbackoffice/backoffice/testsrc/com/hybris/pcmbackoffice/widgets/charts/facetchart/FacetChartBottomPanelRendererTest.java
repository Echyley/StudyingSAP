/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.widgets.charts.facetchart;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.chart.Point;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Div;

import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class FacetChartBottomPanelRendererTest
{
	@InjectMocks
	private FacetChartBottomPanelRenderer facetChartBottomPanelRenderer;
	@Spy
	private Div container = new Div();
	@Mock
	private EventListener<? super Event> onClickListener;

	@Test
	public void shouldRenderAllPointsData()
	{
		final List<Point> points = new ArrayList<>();
		points.add(new Point("p1", 4));
		points.add(new Point("p2", 7));
		facetChartBottomPanelRenderer.render(container, points, onClickListener);

		assertThat(container.getChildren()).hasSize(points.size());
	}

	@Test
	public void shouldRenderAllPointsDataWithColor()
	{
		final List<Point> points = new ArrayList<>();
		final Point newPoint = new Point("p1", 4);
		newPoint.setColor("red");
		points.add(newPoint);

		facetChartBottomPanelRenderer.render(container, points, onClickListener);

		assertThat(container.getChildren()).hasSize(points.size());
		assertThat(CockpitTestUtil.find(container, this::isRedDiv)).isPresent();
	}

	private boolean isRedDiv(final Component component)
	{
		return component instanceof Div && ((Div) component).getStyle() != null
				&& ((Div) component).getStyle().contains("red");
	}
}
