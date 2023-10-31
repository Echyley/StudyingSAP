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

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class ConfigurationControllerSecureParamsRemoveRuleAuditableActionTest extends ServicelayerBaseTest
{

	final PropertyConfigSwitcher auditPropertyValues = new PropertyConfigSwitcher(AuditConfigurationControllerHelper.HAC_CONFIGURATION_VIEW_AUDIT_PROPERTY_VALUES);
	final PropertyConfigSwitcher configurationViewStrategy = new PropertyConfigSwitcher("configuration.view.strategy");

	private ConfigurationController configurationController;

	@Resource
	private SessionService sessionService;

	private AuditableActionHandler testAuditableActionHandler;
	private Supplier<AuditableActionHandler> originalActionHandler;

	@Before
	public void setUp()
	{
		configureAuditFlag();
		configurationViewStrategy.switchToValue("remove");
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
		configurationViewStrategy.switchBackToDefault();
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

	protected String getAuditValue()
	{
		return "notAuditableValue";
	}

}
