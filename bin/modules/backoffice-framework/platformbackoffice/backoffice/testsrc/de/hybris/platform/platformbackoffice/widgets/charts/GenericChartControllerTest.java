/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.charts;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredInputs;


@DeclaredInputs(
{ @DeclaredInput(GenericChartController.REFRESH_INPUT_SOCKET) })
public class GenericChartControllerTest extends AbstractWidgetUnitTest<GenericChartController>
{

	private final GenericChartController abstractChartController = new GenericChartController();

	@Override
	protected GenericChartController getWidgetController()
	{
		return abstractChartController;
	}
}
