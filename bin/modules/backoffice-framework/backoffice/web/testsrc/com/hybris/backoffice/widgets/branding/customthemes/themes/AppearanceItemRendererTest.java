/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.branding.customthemes.themes;

import de.hybris.platform.core.model.media.MediaModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.zkoss.zhtml.Div;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;

import com.hybris.backoffice.theme.impl.ThemeStyleUtil;
import com.hybris.backoffice.theme.impl.ThemeVariablesMapping;
import com.hybris.cockpitng.engine.WidgetInstanceManager;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppearanceItemRendererTest
{
	private ThemeVariablesMapping themeVariablesMapping;
	private ThemeStyleUtil mockUtil = mock(ThemeStyleUtil.class);
	private AppearanceItemRenderer.AppearanceItem item;
	private AppearanceItemRenderer renderer;
	private List<String> variables;
	private Map<String, String> styleVariableMap;
	private WidgetInstanceManager wim = Mockito.mock(WidgetInstanceManager.class);
	private Button saveButton = new Button();
	private Button cancelButton = new Button();

	@Before
	public void before()
	{
		variables = Arrays.asList("--bo-brand-color", "--bo-highlight-color", "--bo-selected-color");
		themeVariablesMapping = new ThemeVariablesMapping();
		themeVariablesMapping.setVariables(variables);
		when(mockUtil.getThemeVariablesMapping()).thenReturn(Arrays.asList(themeVariablesMapping));

		final var style = mock(MediaModel.class);
		styleVariableMap = new HashMap<>();
		styleVariableMap.put("--bo-brand-color", "#123456");
		styleVariableMap.put("--bo-highlight-color", "#edeff0");
		styleVariableMap.put("--bo-selected-color", "#abcdef");
		styleVariableMap.put("---primary1", "#354a5f");
		when(mockUtil.convertStyleMediaToVariableMap(style)).thenReturn(styleVariableMap);
		renderer = new AppearanceItemRenderer(new Div(), saveButton, cancelButton, mockUtil , wim);
		renderer.resetAppearanceItemColor(style);
		item = renderer.new AppearanceItem(themeVariablesMapping, new Div());

		saveButton.setDisabled(true);
		cancelButton.setDisabled(true);
	}

	@Test
	public void shouldRenderCorrectAppearanceItem()
	{
		assertThat(renderer.getAppearanceItems().size()).isEqualTo(1);
		assertThat(renderer.isColorChanged()).isFalse();
		assertThat(renderer.getStyleVariableMap().size()).isEqualTo(4);
		assertThat(renderer.getStyleVariableMap().get("--bo-brand-color")).isEqualTo("#123456");
		assertThat(renderer.getStyleVariableMap().get("--bo-selected-color")).isEqualTo("#abcdef");
	}

	@Test
	public void shouldRenderColorBoxAndColorInput()
	{
		assertThat(item.colorBox).isNotNull();
		assertThat(item.textBox).isNotNull();
	}

	@Test
	public void shouldSetSameColorToInputIfColorBoxChange()
	{
		final var color = "#123456";
		item.colorBox.setColor(color);
		item.textBox.setValue("#abcdef");

		item.onColorboxChange(mock(Event.class));

		assertThat(item.textBox.getValue()).isEqualTo(color);
		verify(wim).sendOutput(eq("themeVariablesChanged"), argThat(new ArgumentMatcher<Map<String, String>>()
		{
			@Override
			public boolean matches(final Map<String, String> map)
			{
				return map.size() == variables.size() && variables.stream().allMatch(var -> map.get(var).equals(color));
			}
		}));
		assertThat(saveButton.isDisabled()).isFalse();
		assertThat(cancelButton.isDisabled()).isFalse();
	}

	@Test
	public void shouldSetSameValueToColorBoxIfInputChange()
	{
		final var color = "#123456";
		item.colorBox.setColor("#abcdef");
		item.textBox.setValue(color);

		when(mockUtil.isValidHexColor(color)).thenReturn(true);
		when(mockUtil.toLongHexColor(color)).thenReturn(color);

		item.onTextboxChange(mock(Event.class));

		assertThat(item.colorBox.getColor()).isEqualTo(color);
		assertThat(item.textBox.getValue()).isEqualTo(color);
		verify(wim).sendOutput(eq("themeVariablesChanged"), argThat(new ArgumentMatcher<Map<String, String>>()
		{
			@Override
			public boolean matches(final Map<String, String> map)
			{
				return map.size() == variables.size() && variables.stream().allMatch(var -> map.get(var).equals(color));
			}
		}));
		assertThat(saveButton.isDisabled()).isFalse();
		assertThat(cancelButton.isDisabled()).isFalse();
	}

	@Test
	public void shouldResetInputIfValueIsNotValidColor()
	{
		final var correctColor = "#abcdef";
		final var color = "#1234";
		item.colorBox.setColor(correctColor);
		item.textBox.setValue(color);

		when(mockUtil.isValidHexColor(color)).thenReturn(false);

		item.onTextboxChange(mock(Event.class));

		assertThat(item.colorBox.getColor()).isEqualTo(correctColor);
		assertThat(item.textBox.getValue()).isEqualTo(correctColor);
		assertThat(saveButton.isDisabled()).isTrue();
		assertThat(cancelButton.isDisabled()).isTrue();
	}

	@Test
	public void shouldSetBothColorBoxAndInputColor()
	{
		final var oldColor = "#123456";
		final var newColor = "#abcdef";
		item.colorBox.setColor(oldColor);
		item.textBox.setValue(oldColor);

		item.setColor(newColor);

		assertThat(item.colorBox.getColor()).isEqualTo(newColor);
		assertThat(item.textBox.getValue()).isEqualTo(newColor);
	}

}
