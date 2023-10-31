package com.hybris.backoffice.widgets.title;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.core.util.impl.TypedSettingsMap;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.tree.node.TypeNode;


@DeclaredInput(value = "nodeSelected", socketType = NavigationNode.class)
@RunWith(MockitoJUnitRunner.class)
public class TitleControllerTest extends AbstractWidgetUnitTest<TitleController>
{
	private static final String SOCKET_IN_NODE_SELECTED = "nodeSelected";
	private static final String CURRENT_NAV_NODE = "currentNavNode";
	private static final String TITLE_LOCALE_KEY = "titleLocKey";

	@Mock
	private Label titleLabel;
	@Mock
	private LabelService labelService;
	@Mock
	private WidgetModel widgetModel;

	@Mock
	private TypedSettingsMap widgetSettings;

	@Spy
	@InjectMocks
	private TitleController controller;

	@Before
	public void setUp()
	{
		when(controller.getModel()).thenReturn(widgetModel);
		when(controller.getTitleLabel()).thenReturn(titleLabel);
		when(controller.getWidgetSettings()).thenReturn(widgetSettings);
	}

	@Test
	public void shouldSetTitleFromWidgetModelWhenInitialize()
	{
		// given
		final var title = "ProductTitle";
		final var locKey = "NameLocKey";
		final var node = mock(NavigationNode.class);
		when(node.getNameLocKey()).thenReturn(locKey);
		when(widgetModel.getValue(CURRENT_NAV_NODE, NavigationNode.class)).thenReturn(node);
		try (final MockedStatic<Labels> labelsMock = mockStatic(Labels.class))
		{
			labelsMock.when(() -> Labels.getLabel(locKey)).thenReturn(title);
			// when
			controller.initialize(new Div());
		}
		// then
		verify(titleLabel).setValue(title);
	}

	@Test
	public void shouldSetTitleFromWidgetSettingWhenInitialize()
	{
		// given
		final var titleLocaleKeyFromSetting = "titleLocaleKeyFromSetting";
		final var titleLocaleNameFromSetting = "titleLocaleNameFromSetting";
		when(widgetSettings.getString(TITLE_LOCALE_KEY)).thenReturn(titleLocaleKeyFromSetting);
		when(widgetModel.getValue(CURRENT_NAV_NODE, NavigationNode.class)).thenReturn(null);
		try (final MockedStatic<Labels> labelsMock = mockStatic(Labels.class))
		{
			labelsMock.when(() -> Labels.getLabel(titleLocaleKeyFromSetting)).thenReturn(titleLocaleNameFromSetting);
			// when
			controller.initialize(new Div());
		}
		// then
		verify(titleLabel).setValue(titleLocaleNameFromSetting);
	}

	@Test
	public void shouldUpdateTitleWithTranslatedTextFromNodeKeyWhenNodeSelected()
	{
		// given
		final var title = "ProductTitle";
		final var key = "NameLocKey";
		final var node = mock(NavigationNode.class);
		when(node.getNameLocKey()).thenReturn(key);
		try (final MockedStatic<Labels> labelsMock = mockStatic(Labels.class))
		{
			labelsMock.when(() -> Labels.getLabel(key)).thenReturn(title);
			// when
			executeInputSocketEvent(SOCKET_IN_NODE_SELECTED, node);
		}
		// then
		verify(titleLabel).setValue(title);
		verify(widgetModel).setValue(CURRENT_NAV_NODE, node);
	}

	@Test
	public void shouldUpdateTitleWithTranslatedTextFromNodeNameWhenNodeSelected()
	{
		// given
		final var name = "name";
		final var node = mock(NavigationNode.class);
		when(node.getNameLocKey()).thenReturn(StringUtils.EMPTY);
		when(node.getName()).thenReturn(name);

		// when
		executeInputSocketEvent(SOCKET_IN_NODE_SELECTED, node);

		// then
		verify(titleLabel).setValue(name);
		verify(widgetModel).setValue(CURRENT_NAV_NODE, node);
	}

	@Test
	public void shouldUpdateTitleWithTranslatedTextFromNodeCodeWhenNodeSelected()
	{
		// given
		final var translatedCode = "translatedCode";
		final var code = "code";
		final var node = mock(TypeNode.class);
		when(node.getNameLocKey()).thenReturn(StringUtils.EMPTY);
		when(node.getCode()).thenReturn(code);
		when(labelService.getObjectLabel(code)).thenReturn(translatedCode);

		// when
		executeInputSocketEvent(SOCKET_IN_NODE_SELECTED, node);

		// then
		verify(titleLabel).setValue(translatedCode);
		verify(widgetModel).setValue(CURRENT_NAV_NODE, node);
	}

	@Test
	public void shouldUpdateTitleWithNodeIdWhenNodeSelected()
	{
		// given
		final var nodeId = "nodeId";
		final var node = mock(NavigationNode.class);
		when(node.getNameLocKey()).thenReturn(StringUtils.EMPTY);
		when(node.getName()).thenReturn(StringUtils.EMPTY);
		when(node.getId()).thenReturn(nodeId);

		// when
		executeInputSocketEvent(SOCKET_IN_NODE_SELECTED, node);

		// then
		verify(titleLabel).setValue(nodeId);
		verify(widgetModel).setValue(CURRENT_NAV_NODE, node);
	}


	@Override
	protected TitleController getWidgetController()
	{
		return controller;
	}
}
