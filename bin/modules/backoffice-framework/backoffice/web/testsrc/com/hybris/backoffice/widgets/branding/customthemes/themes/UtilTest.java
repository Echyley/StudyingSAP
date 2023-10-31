/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.branding.customthemes.themes;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.theme.impl.ThemeStyleUtil;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UtilTest
{
	@Spy
	@InjectMocks
	private ThemeStyleUtil util = new ThemeStyleUtil();
	@Mock
	private MediaService mediaService;

	@Test
	public void shouldReturnCorrectStyleInputStream() throws IOException
	{
		final Map<String, String> map = new LinkedHashMap<>();
		map.put("--bo-shell-background", "#ffc847");
		map.put("--bo-shell-interactive-color", "#e7a1a1");
		map.put("--bo-shell-text-color", "#f37aa2");
		map.put("--bo-brand-color", "#e269c9");

		final var styleInputStream = util.mapToStyleInputStream(map);
		final String styleString = IOUtils.toString(styleInputStream, StandardCharsets.UTF_8);
		final var expected = ":root {\n" + "--bo-shell-background: #ffc847;\n" + "--bo-shell-interactive-color: #e7a1a1;\n"
				+ "--bo-shell-text-color: #f37aa2;\n" + "--bo-brand-color: #e269c9;\n" + "}\n";
		assertThat(styleString).isEqualTo(expected);
	}

	@Test
	public void shouldReturnFalseIfColorIsNotValidHexColor()
	{
		assertThat(util.isValidHexColor("#12345")).isFalse();
		assertThat(util.isValidHexColor("#1")).isFalse();
		assertThat(util.isValidHexColor("#12")).isFalse();
		assertThat(util.isValidHexColor("#12G")).isFalse();
		assertThat(util.isValidHexColor("#12345H")).isFalse();
		assertThat(util.isValidHexColor("#HJK")).isFalse();
		assertThat(util.isValidHexColor("#lmnopq")).isFalse();
		assertThat(util.isValidHexColor("abc")).isFalse();
		assertThat(util.isValidHexColor("12345")).isFalse();
	}

	@Test
	public void shouldReturnTrueIfColorIsValidHexColor()
	{
		assertThat(util.isValidHexColor("#123")).isTrue();
		assertThat(util.isValidHexColor("#123456")).isTrue();
		assertThat(util.isValidHexColor("#abcdef")).isTrue();
		assertThat(util.isValidHexColor("#abc")).isTrue();
		assertThat(util.isValidHexColor("#6789ab")).isTrue();
		assertThat(util.isValidHexColor("#679")).isTrue();
		assertThat(util.isValidHexColor("#def")).isTrue();
	}

	@Test
	public void shouldReturnCorrectLongHexColor()
	{
		assertThat(util.toLongHexColor("#123")).isEqualTo("#112233");
		assertThat(util.toLongHexColor("#abc")).isEqualTo("#aabbcc");
		assertThat(util.toLongHexColor(null)).isEqualTo(null);
		assertThat(util.toLongHexColor("#1234")).isEqualTo("#1234");
	}

	@Test
	public void shouldReturnCorrectBOVariablesMap()
	{
		final var inputStr = " :root {\n" + "  --sapTile_TextColor: #556b82;\n" + "  --sapHighlightColor: #0070f2;\n"
				+ "  --sapBaseColor: #fff;\n" + "--bo-tile-text-color: var(--sapTile_TextColor);\n"
				+ "/*** Highlights - Interaction States ***/\n" + "  --bo-highlight-color: var(--sapHighlightColor);\n"
				+ "/*** Main UI ***/\n" + "  --bo-base-color: var(--sapBaseColor);\n" + "--primary2: #0a6ed1;\n" + "}\n";
		final MediaModel style = mock(MediaModel.class);
		when(mediaService.hasData(style)).thenReturn(true);
		when(mediaService.getStreamFromMedia(style)).thenReturn(IOUtils.toInputStream(inputStr, StandardCharsets.UTF_8));
		final var map = util.convertStyleMediaToVariableMap(style);
		assertThat(map.size()).isEqualTo(4);
		assertThat(map.get("--primary2")).isEqualTo("#0a6ed1");
		assertThat(map.get("--bo-tile-text-color")).isEqualTo("#556b82");
		assertThat(map.get("--bo-highlight-color")).isEqualTo("#0070f2");
		assertThat(map.get("--bo-base-color")).isEqualTo("#ffffff");
	}

	@Test
	public void shouldReturnCorrectThemeVariablesMapping()
	{
		final var mappings = util.getThemeVariablesMapping();
		assertThat(mappings.size()).isEqualTo(8);
		assertThat(mappings.get(0).getVariables()).contains("--bo-shell-background");
		assertThat(mappings.get(1).getVariables()).contains("--bo-shell-interactive-color");
		assertThat(mappings.get(2).getVariables()).contains("--bo-shell-text-color");
		assertThat(mappings.get(3).getVariables()).contains("--bo-brand-color");
	}

}
