 /*
  * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
  */
 package com.hybris.backoffice.theme;

 import de.hybris.platform.core.model.media.MediaModel;
 import de.hybris.platform.servicelayer.media.MediaService;
 import de.hybris.platform.servicelayer.model.ModelService;

 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.Mockito;
 import org.mockito.Spy;
 import org.mockito.junit.MockitoJUnitRunner;

 import com.hybris.backoffice.model.CustomThemeModel;
 import com.hybris.backoffice.model.ThemeModel;
 import com.hybris.backoffice.theme.impl.DefaultBackofficeCustomThemeService;
 import com.hybris.backoffice.theme.impl.ThemeStyleUtil;
 import com.hybris.backoffice.theme.impl.ThemeVariablesMapping;

 import static org.assertj.core.api.Assertions.assertThat;
 import static org.mockito.Mockito.doNothing;
 import static org.mockito.Mockito.verify;
 import static org.mockito.Mockito.when;

 @RunWith(MockitoJUnitRunner.class)
 public class DefaultBackofficeCustomThemeServiceTest
 {
 	@Mock
 	private ThemeStyleUtil themeStyleUtil;
 	@Mock
 	private MediaService mediaService;
 	@Mock
	private ModelService modelService;

 	@Spy
 	@InjectMocks
 	private DefaultBackofficeCustomThemeService backofficeCustomThemeService;

 	@Test
 	public void generateCustomThemeStyleSucc()
 	{
 		final CustomThemeModel theme = Mockito.mock(CustomThemeModel.class);
		final ThemeModel base = Mockito.mock(ThemeModel.class);
		final InputStream in = Mockito.mock(InputStream.class);
		final MediaModel style = Mockito.mock(MediaModel.class);
		final MediaModel baseStyle = Mockito.mock(MediaModel.class);
		final Map<String, String> baseStyleVariableMaps = new HashMap<String, String>(){{
			put("a","1");
			put("b","2");
		}};  ;
		final Map<String, String> styleVariableMaps = Map.of("b", "22", "c", "33","d", "44");
		final List<ThemeVariablesMapping> variablesMappings = new ArrayList<>();
		ThemeVariablesMapping themeVariablesMapping = new ThemeVariablesMapping();
		List<String> variables = new ArrayList<>();
		variables.add("b");
		variables.add("c");
		themeVariablesMapping.setVariables(variables);
		variablesMappings.add(themeVariablesMapping);

 		//given
		when(theme.getStyle()).thenReturn(style);
		when(theme.getBase()).thenReturn(base);
		when(base.getStyle()).thenReturn(baseStyle);
		when(themeStyleUtil.convertStyleMediaToVariableMap(theme.getBase().getStyle())).thenReturn(baseStyleVariableMaps);
		when(themeStyleUtil.convertStyleMediaToVariableMap(theme.getStyle())).thenReturn(styleVariableMaps);
		when(themeStyleUtil.getThemeVariablesMapping()).thenReturn(variablesMappings);

		when(themeStyleUtil.mapToStyleInputStream(baseStyleVariableMaps)).thenReturn(in);
		//doNothing().when(mediaService).setStreamForMedia(theme.getStyle(), in);
		doNothing().when(modelService).save(Mockito.any(ThemeModel.class));

		//when
		backofficeCustomThemeService.generateCustomThemeStyle(theme);

		//then
		assertThat(baseStyleVariableMaps).containsEntry("a", "1")
			.containsEntry("b", "22")
			.containsEntry("c", "22")
			.doesNotContainKey("d");
 		verify(modelService).save(theme);
 	}
 }

