/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.surveylauncher;

import com.hybris.backoffice.model.ThemeModel;
import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.theme.BackofficeThemeService;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.JsonUtilities;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.util.Clients;

import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@DeclaredInput(value = "perspectiveSelected", socketType = NavigationNode.class)
public class SurveyLauncherControllerTest extends AbstractWidgetUnitTest<SurveyLauncherController>
{
	private static final String QTX_INTERCEPT_URL = "intercept_url";
	private static final String QTX_CONTEXT_PARAMS = "context_params";
	private static final String JS_FUNCTION_PATTERN = "updateQtxContextParams('%s')";
	private static final String PERSPECTIVE_CHOOSER_WIDGET_CODE = "com.hybris.backoffice.perspectiveChooser";

	@Mock
	private ConfigurationService configurationService;
	@Mock
	private CockpitLocaleService cockpitLocaleService;
	@Mock
	private BackofficeThemeService themeService;
	@Mock
	private Config config;

	@Spy
	@InjectMocks
	private SurveyLauncherController controller;

	private Tenant currentTenant;

	@After
	public void tearDown()
	{
		if (currentTenant != null)
		{
			Registry.setCurrentTenant(currentTenant);
		}
	}

	@Test
	public void shouldSetCorrectModelWhenEnvironmentIsCould()
	{
		final var surveyUrl = "surveyUrl";
		final var params = "Params";
		final Component component = mock(Component.class);
		final var configuration = mock(Configuration.class);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString("backoffice.survey.qualtricsURL", "https://zn8wg0tz71we2lgrq-sapinsights.siteintercept.qualtrics.com/SIE/?Q_ZID=ZN_8wG0Tz71wE2LgrQ")).thenReturn(surveyUrl);

		doReturn(params).when(controller).buildContextParams();
		doReturn(mock(Component.class)).when(component).getParent();

		activateMasterTenant();
		Config.setParameter("modelt.environment.type", "testType");
		controller.preInitialize(component);

		assertThat(controller.getModel().getValue(QTX_INTERCEPT_URL, String.class)).isEqualTo(surveyUrl);
		assertThat(controller.getModel().getValue(QTX_CONTEXT_PARAMS, String.class)).isEqualTo(params);
		Config.setParameter("modelt.environment.type", "");
	}

	@Test
	public void shouldSetCorrectModelWhenEnvironmentIsNotCould()
	{
		final var params = "Params";
		final Component component = mock(Component.class);
		final Component parent = mock(Component.class);

		final var configuration = mock(Configuration.class);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString("backoffice.survey.qualtricsURL", "https://zn8wg0tz71we2lgrq-sapinsights.siteintercept.qualtrics.com/SIE/?Q_ZID=ZN_8wG0Tz71wE2LgrQ")).thenReturn("surveyUrl");

		doReturn(params).when(controller).buildContextParams();
		doReturn(parent).when(component).getParent();

		activateMasterTenant();
		controller.preInitialize(component);

		verify(parent, times(1)).removeChild(component);
	}

	@Test
	public void shouldBuildCorrectParams()
	{
		final var productName = "productName";
		final var configuration = mock(Configuration.class);
		final var themeCode = "sap dark";
		final var appVersion = "appVersion";
		final var tenantRole = "tenantRole";
		final var customerCode = "customerCode";
		final var projectCode = "projectCode";
		final var environmentCode = "environmentCode";
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString("backoffice.cockpitng.appTitle")).thenReturn(productName);
		when(configuration.getString("build.version.api")).thenReturn(appVersion);
		when(configuration.getString("modelt.environment.type")).thenReturn(tenantRole);
		when(configuration.getString("modelt.customer.code")).thenReturn(customerCode);
		when(configuration.getString("modelt.project.code")).thenReturn(projectCode);
		when(configuration.getString("modelt.environment.code")).thenReturn(environmentCode);
		when(cockpitLocaleService.getCurrentLocale()).thenReturn(new Locale("en"));
		doReturn(themeCode).when(controller).getCurrentThemeCode();


		final var paramMap = JsonUtilities.fromJson(controller.buildContextParams(), Map.class);

		assertThat(paramMap.get("productName")).isEqualTo(productName);
		assertThat(paramMap.get("appFrameworkId")).isEqualTo("1");
		assertThat(paramMap.get("appFrameworkVersion")).isEqualTo(String.format("%s", WebApps.getCurrent().getVersion()));
		assertThat(paramMap.get("language")).isEqualTo("en");
		assertThat(paramMap.get("theme")).isEqualTo(themeCode);
		assertThat(paramMap.get("appId")).isEqualTo("-");
		assertThat(paramMap.get("appTitle")).isEqualTo("-");
		assertThat(paramMap.get("appVersion")).isEqualTo(appVersion);
		assertThat(paramMap.get("tenantId")).isEqualTo(String.format("%s-%s-%s", customerCode, projectCode, environmentCode));
		assertThat(paramMap.get("tenantRole")).isEqualTo(tenantRole);
	}

	@Test
	public void shouldReturnCurrentThemeCode()
	{
		final var themeCode = "sap dark";
		final var theme = mock(ThemeModel.class);
		when(themeService.getCurrentTheme()).thenReturn(theme);
		when(theme.getCode()).thenReturn(themeCode);

		assertThat(controller.getCurrentThemeCode()).isEqualTo(themeCode);
	}

	@Test
	public void shouldDoNothingIfNullPerspectiveSelected()
	{
		try (final MockedStatic<Clients> clientsMock = mockStatic(Clients.class))
		{
			executeInputSocketEvent("perspectiveSelected", (NavigationNode) null);
			clientsMock.verify(() -> Clients.evalJavaScript(any()), never());
		}
	}

	@Test
	public void shouldUpdateTitleWithLocalNameWhenPerspectiveSelected()
	{
		final var node = mock(NavigationNode.class);
		final var key = "NameLocKey";
		final var name = "default name";
		final var title = "Backoffice Perspective";
		when(node.getNameLocKey()).thenReturn(key);
		when(node.getName()).thenReturn(name);
		try (final MockedStatic<Clients> clientsMock = mockStatic(Clients.class);
				final MockedStatic<Labels> labelsMock = mockStatic(Labels.class))
		{
			labelsMock.when(() -> Labels.getLabel(String.format("%s.%s", PERSPECTIVE_CHOOSER_WIDGET_CODE, key))).thenReturn(title);

			executeInputSocketEvent("perspectiveSelected", node);
			clientsMock.verify(() -> Clients.evalJavaScript(String.format(JS_FUNCTION_PATTERN, String.format("[[\"%s\",\"%s\"],[\"%s\",\"%s\"]]", "appTitle", title, "appId", String.format("%s.%s", PERSPECTIVE_CHOOSER_WIDGET_CODE, key)))), times(1));
		}
	}

	@Test
	public void shouldUpdateTitleWithSpecialLocalNameWhenPerspectiveSelected()
	{
		final var node = mock(NavigationNode.class);
		final var key = "NameLocKey";
		final var name = "default name";
		final var title = "Backoffice 'Perspective";
		final var escapeTitle = "Backoffice \\'Perspective";
		when(node.getNameLocKey()).thenReturn(key);
		when(node.getName()).thenReturn(name);
		try (final MockedStatic<Clients> clientsMock = mockStatic(Clients.class);
				final MockedStatic<Labels> labelsMock = mockStatic(Labels.class))
		{
			labelsMock.when(() -> Labels.getLabel(String.format("%s.%s", PERSPECTIVE_CHOOSER_WIDGET_CODE, key))).thenReturn(title);

			executeInputSocketEvent("perspectiveSelected", node);

			clientsMock.verify(() -> Clients.evalJavaScript(String.format(JS_FUNCTION_PATTERN, String.format("[[\"%s\",\"%s\"],[\"%s\",\"%s\"]]", "appTitle", escapeTitle, "appId", String.format("%s.%s", PERSPECTIVE_CHOOSER_WIDGET_CODE, key)))), times(1));
		}
	}

	@Test
	public void shouldUpdateTitleWithNameWhenPerspectiveSelected()
	{
		final var node = mock(NavigationNode.class);
		final var key = "NameLocKey";
		final var name = "default name";
		when(node.getNameLocKey()).thenReturn(key);
		when(node.getName()).thenReturn(name);
		try (final MockedStatic<Clients> clientsMock = mockStatic(Clients.class);
				final MockedStatic<Labels> labelsMock = mockStatic(Labels.class))
		{
			labelsMock.when(() -> Labels.getLabel(String.format("%s.%s", PERSPECTIVE_CHOOSER_WIDGET_CODE, key))).thenReturn(null);

			executeInputSocketEvent("perspectiveSelected", node);

			clientsMock.verify(() -> Clients.evalJavaScript(String.format(JS_FUNCTION_PATTERN, String.format("[[\"%s\",\"%s\"],[\"%s\",\"%s\"]]", "appTitle", name, "appId", String.format("%s.%s", PERSPECTIVE_CHOOSER_WIDGET_CODE, key)))), times(1));
		}
	}

	@Override
	protected SurveyLauncherController getWidgetController()
	{
		return controller;
	}

	private void activateMasterTenant()
	{
		currentTenant = Registry.getCurrentTenantNoFallback();
		Registry.activateMasterTenant();
	}
}
