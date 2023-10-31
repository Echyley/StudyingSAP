/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.branding.customthemes.preview;

import static com.hybris.backoffice.widgets.branding.customthemes.preview.PreviewController.JS_FUNCTION_PATTERN;
import static com.hybris.backoffice.widgets.branding.customthemes.preview.PreviewController.SOCKET_IN_THEME_VARIABLES_CHANGED;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.zkoss.zk.ui.util.Clients;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;


@DeclaredInput(value = SOCKET_IN_THEME_VARIABLES_CHANGED, socketType = Map.class)
public class PreviewControllerTest extends AbstractWidgetUnitTest<PreviewController>
{

	@Spy
	@InjectMocks
	private PreviewController controller;

	@Test
	public void shouldUpdateColorsWhenThemeChanged()
	{
		try (final MockedStatic<Clients> clientsMock = mockStatic(Clients.class))
		{
			final Map<String, String> data = new HashMap();
			data.put("--bo-text-color", "#ffffff");
			executeInputSocketEvent(SOCKET_IN_THEME_VARIABLES_CHANGED, data);
			final String vars = data.entrySet().stream().map(e -> e.getKey() + ":" + e.getValue()).collect(Collectors.joining(";"));
			clientsMock.verify(() -> Clients.evalJavaScript(String.format(JS_FUNCTION_PATTERN, vars)), times(1));
		}
	}

	@Test
	public void shouldIgnoreWhenThemeChangedWithInvalidData()
	{
		try (final MockedStatic<Clients> clientsMock = mockStatic(Clients.class))
		{
			final Map<String, String> data = null;
			executeInputSocketEvent(SOCKET_IN_THEME_VARIABLES_CHANGED, data);
			clientsMock.verify(() -> Clients.evalJavaScript(anyString()), never());
		}
	}

	@Override
	protected PreviewController getWidgetController()
	{
		return controller;
	}
}
