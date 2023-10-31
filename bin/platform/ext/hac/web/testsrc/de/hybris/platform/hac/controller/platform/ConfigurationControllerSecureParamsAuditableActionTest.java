/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.hac.controller.platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.audit.AuditableActions;
import de.hybris.platform.audit.AuditableActionsUtil;
import de.hybris.platform.audit.actions.AuditableActionHandler;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.config.ConfigurationViewService;
import de.hybris.platform.servicelayer.config.impl.SecureConfigurationViewService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

@IntegrationTest
public class ConfigurationControllerSecureParamsAuditableActionTest extends ServicelayerBaseTest
{
	final PropertyConfigSwitcher auditPropertyValues = new PropertyConfigSwitcher(AuditConfigurationControllerHelper.HAC_CONFIGURATION_VIEW_AUDIT_PROPERTY_VALUES);
	private ConfigurationController configurationController;

	@Resource
	private SessionService sessionService;

	private AuditableActionHandler testAuditableActionHandler;
	private Supplier<AuditableActionHandler> originalActionHandler;

	@Before
	public void setUp()
	{
		configureAuditFlag();
		testAuditableActionHandler = mock(AuditableActionHandler.class);
		originalActionHandler = AuditableActionsUtil.getAuditableActionHandlerFactory();
		AuditableActionsUtil.setAuditableActionHandlerFactory(() -> testAuditableActionHandler);
		configurationController = new ConfigurationController();
		Config.setParameter("configuration.view.blacklist.onlyforaudittests", "param.for.test.secure.values.in.audit");
		Config.setParameter("configuration.view.regex.rule.regexRuleOnlyForAuditTests", "regexParamForTestSecureValuesInAudit");
		final ConfigurationViewService configurationViewService = new SecureConfigurationViewService();
		configurationController.setConfigurationViewService(configurationViewService);
		configurationController.setSessionService(sessionService);
	}


	@After
	public void tearDown()
	{
		AuditableActionsUtil.setAuditableActionHandlerFactory(originalActionHandler);
		auditPropertyValues.switchBackToDefault();
	}

	protected void configureAuditFlag()
	{
		auditPropertyValues.switchToValue("true");
	}

	private Optional<AuditableActions.Action> getAuditableAction(final String actionName)
	{
		return ConfigurationControllerTestAuditableHelper.getAuditableAction(testAuditableActionHandler, actionName);
	}

	private List<AuditableActions.Action> getAllAuditableAction(final String actionName)
	{
		return ConfigurationControllerTestAuditableHelper.getAllAuditableAction(testAuditableActionHandler, actionName);
	}

	@Test
	public void shouldAuditIfPropertyIsAdded()
	{
		//given, when
		configurationController.configUpdateCreate("param.for.test.secure.values.in.audit", "passwordThatShouldBeProtected");

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction("property added");
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(
				entry("propertyName", "param.for.test.secure.values.in.audit"),
				entry("propertyValue", getAuditValue()));
	}

	@Test
	public void shouldAuditIfPropertyIsEdited()
	{
		//given
		configurationController.configUpdateCreate("param.for.test.secure.values.in.audit", "passwordThatShouldBeProtected");

		//when
		configurationController.configUpdateCreate("param.for.test.secure.values.in.audit",
				"passwordThatShouldBeProtectedChanged");

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction("property edited");
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(
				entry("propertyName", "param.for.test.secure.values.in.audit"),
				entry("newPropertyValue", getAuditValue()), entry("prevPropertyValue", getAuditValue()));
	}

	@Test
	public void shouldAuditRemoveOfProperty() throws IOException
	{
		//given
		configurationController.configUpdateCreate("param.for.test.secure.values.in.audit", "passwordThatShouldBeProtected");
		final HttpServletResponse mockResponse = new MockHttpServletResponse();

		//when
		configurationController.configDelete(null, "param.for.test.secure.values.in.audit", mockResponse);

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction("property removed");
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(
				entry("propertyName", "param.for.test.secure.values.in.audit"),
				entry("propertyValue", getAuditValue()));
	}

	@Test
	public void shouldAuditMultipleAddedProperties()
	{
		//given

		configurationController.valuechanged("regexParamForTestSecureValuesInAudit.param", "passwordThatShouldBeProtected");
		configurationController.valuechanged("param.regexParamForTestSecureValuesInAudit.param", "passwordThatShouldBeProtected");

		//when
		configurationController.applyEdited();

		//then
		final List<AuditableActions.Action> actionList = getAllAuditableAction("property added");
		assertThat(actionList).hasSize(2);
		final Optional<AuditableActions.Action> optional1 = actionList.stream()
		                                                              .filter(action -> action.getActionAttributes()
		                                                                                      .get("propertyName")
		                                                                                      .equals("regexParamForTestSecureValuesInAudit.param"))
		                                                              .findFirst();
		assertThat(optional1).isNotEmpty();
		assertThat(optional1.get().getActionAttributes()).containsOnly(
				entry("propertyName", "regexParamForTestSecureValuesInAudit.param"),
				entry("propertyValue", getAuditValue()));
		final Optional<AuditableActions.Action> optional2 = actionList.stream()
		                                                              .filter(action -> action.getActionAttributes()
		                                                                                      .get("propertyName")
		                                                                                      .equals("param.regexParamForTestSecureValuesInAudit.param"))
		                                                              .findFirst();
		assertThat(optional2).isNotEmpty();
		assertThat(optional2.get().getActionAttributes()).containsOnly(
				entry("propertyName", "param.regexParamForTestSecureValuesInAudit.param"),
				entry("propertyValue", getAuditValue()));
	}

	@Test
	public void shouldAuditMultipleEditedProperties()
	{
		//given
		configurationController.valuechanged("regexParamForTestSecureValuesInAudit.param", "passwordThatShouldBeProtected");
		configurationController.valuechanged("param.for.test.secure.values.in.audit", "passwordThatShouldBeProtected");
		configurationController.applyEdited();
		configurationController.valuechanged("regexParamForTestSecureValuesInAudit.param", "passwordThatShouldBeProtectedChange");
		configurationController.valuechanged("param.for.test.secure.values.in.audit", "passwordThatShouldBeProtectedChange");

		//when
		configurationController.applyEdited();

		//then
		final List<AuditableActions.Action> actionList = getAllAuditableAction("property edited");
		assertThat(actionList).hasSize(2);
		final Optional<AuditableActions.Action> optional1 = actionList.stream()
		                                                              .filter(action -> action.getActionAttributes()
		                                                                                      .get("propertyName")
		                                                                                      .equals("regexParamForTestSecureValuesInAudit.param"))
		                                                              .findFirst();
		assertThat(optional1).isNotEmpty();
		assertThat(optional1.get().getActionAttributes()).containsOnly(
				entry("propertyName", "regexParamForTestSecureValuesInAudit.param"),
				entry("newPropertyValue", getAuditValue()), entry("prevPropertyValue", getAuditValue()));

		final Optional<AuditableActions.Action> optional2 = actionList.stream()
		                                                              .filter(action -> action.getActionAttributes()
		                                                                                      .get("propertyName")
		                                                                                      .equals("param.for.test.secure.values.in.audit"))
		                                                              .findFirst();
		assertThat(optional2).isNotEmpty();
		assertThat(optional2.get().getActionAttributes()).containsOnly(
				entry("propertyName", "param.for.test.secure.values.in.audit"),
				entry("newPropertyValue", getAuditValue()), entry("prevPropertyValue", getAuditValue()));

	}

	protected String getAuditValue()
	{
		return "notAuditableValue";
	}

}
