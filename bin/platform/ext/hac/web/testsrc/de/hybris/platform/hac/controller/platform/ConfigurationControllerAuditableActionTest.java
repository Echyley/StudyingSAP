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
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

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
public class ConfigurationControllerAuditableActionTest extends ServicelayerBaseTest
{
	final PropertyConfigSwitcher auditPropertyValues = new PropertyConfigSwitcher(AuditConfigurationControllerHelper.HAC_CONFIGURATION_VIEW_AUDIT_PROPERTY_VALUES);
	private ConfigurationController configurationController;

	@Resource
	private ConfigurationViewService configurationViewService;

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
		configurationController.configUpdateCreate("propertyToAdd.param", "paramValue");

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction("property added");
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry("propertyName", "propertyToAdd.param"),
				entry("propertyValue", getAuditValue("paramValue")));
	}

	@Test
	public void shouldAuditIfPropertyIsEdited()
	{
		//given
		configurationController.configUpdateCreate("propertyToEdit.param", "paramValue");

		//when
		configurationController.configUpdateCreate("propertyToEdit.param", "paramValueUpdated");

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction("property edited");
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry("propertyName", "propertyToEdit.param"),
				entry("newPropertyValue", getAuditValue("paramValueUpdated")),
				entry("prevPropertyValue", getAuditValue("paramValue")));
	}

	@Test
	public void shouldAuditRemoveOfProperty() throws IOException
	{
		//given
		configurationController.configUpdateCreate("propertyToRemove.param", "paramValue");
		final HttpServletResponse mockResponse = new MockHttpServletResponse();

		//when
		configurationController.configDelete(null, "propertyToRemove.param", mockResponse);

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction("property removed");
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry("propertyName", "propertyToRemove.param"),
				entry("propertyValue", getAuditValue("paramValue")));
	}

	@Test
	public void shouldAuditMultipleAddedProperties()
	{
		//given

		configurationController.valuechanged("param1", "value1");
		configurationController.valuechanged("param2", "value2");

		//when
		configurationController.applyEdited();

		//then
		final List<AuditableActions.Action> optionalAction = getAllAuditableAction("property added");
		assertThat(optionalAction).hasSize(2);
		assertThat(optionalAction.get(0).getActionAttributes()).containsOnly(entry("propertyName", "param1"),
				entry("propertyValue", getAuditValue("value1")));
		assertThat(optionalAction.get(1).getActionAttributes()).containsOnly(entry("propertyName", "param2"),
				entry("propertyValue", getAuditValue("value2")));
	}

	@Test
	public void shouldAuditMultipleEditedProperties()
	{
		//given
		configurationController.valuechanged("param1", "value1");
		configurationController.valuechanged("param2", "value2");
		configurationController.applyEdited();
		configurationController.valuechanged("param1", "value11");
		configurationController.valuechanged("param2", "value22");

		//when
		configurationController.applyEdited();

		//then
		final List<AuditableActions.Action> actionList = getAllAuditableAction("property edited");
		assertThat(actionList).hasSize(2);

		final Optional<AuditableActions.Action> optional1 = actionList.stream()
		                                                              .filter(action -> action.getActionAttributes()
		                                                                                      .get("propertyName")
		                                                                                      .equals("param1"))
		                                                              .findFirst();
		assertThat(optional1).isNotEmpty();
		assertThat(optional1.get().getActionAttributes()).containsOnly(
				entry("propertyName", "param1"),
				entry("newPropertyValue", getAuditValue("value11")), entry("prevPropertyValue", getAuditValue("value1")));

		final Optional<AuditableActions.Action> optional2 = actionList.stream()
		                                                              .filter(action -> action.getActionAttributes()
		                                                                                      .get("propertyName")
		                                                                                      .equals("param2"))
		                                                              .findFirst();
		assertThat(optional2).isNotEmpty();
		assertThat(optional2.get().getActionAttributes()).containsOnly(
				entry("propertyName", "param2"),
				entry("newPropertyValue", getAuditValue("value22")), entry("prevPropertyValue", getAuditValue("value2")));
	}


	@Test
	public void shouldAuditIfSecurePropertyIsAdded()
	{

		//given, when
		configurationController.configUpdateCreate("thekeydontexist", "keyvaluedontexist");

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction("property added");
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry("propertyName", "thekeydontexist"),
				entry("propertyValue", getAuditValue("keyvaluedontexist")));
	}

	protected String getAuditValue(final String auditValue)
	{
		return auditValue;
	}

}
