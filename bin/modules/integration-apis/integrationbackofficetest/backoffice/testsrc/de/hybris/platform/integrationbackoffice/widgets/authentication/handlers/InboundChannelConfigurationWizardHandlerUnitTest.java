/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.authentication.handlers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.verifyZeroInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.inboundservices.model.IntegrationClientCredentialsDetailsModel;
import de.hybris.platform.integrationbackoffice.widgets.authentication.InboundChannelConfigNotificationService;
import de.hybris.platform.integrationbackoffice.widgets.authentication.registration.RegisterIntegrationObjectDefaultService;
import de.hybris.platform.integrationservices.enums.AuthenticationType;
import de.hybris.platform.integrationservices.model.InboundChannelConfigurationModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.components.Widgetslot;
import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.config.jaxb.wizard.StepType;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.configurableflow.ConfigurableFlowController;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;

import static de.hybris.platform.integrationbackoffice.widgets.authentication.handlers.InboundChannelConfigurationWizardHandler.WIDGET_CONTROLLER;
import static de.hybris.platform.integrationbackoffice.widgets.authentication.handlers.InboundChannelConfigurationWizardHandler.INBOUND_CHANNEL_CONFIG;
import static de.hybris.platform.integrationbackoffice.widgets.authentication.handlers.InboundChannelConfigurationWizardHandler.NEW_INTEGRATION_SERVICE_CLIENT_DETAIL;
import static de.hybris.platform.integrationbackoffice.widgets.authentication.handlers.InboundChannelConfigurationWizardHandler.INTEGRATION_ERROR_TITLE;
import static de.hybris.platform.integrationbackoffice.widgets.authentication.handlers.InboundChannelConfigurationWizardHandler.INTEGRATION_CLIENT_CRED_ERROR_MESSAGE;
import static de.hybris.platform.integrationbackoffice.widgets.authentication.handlers.InboundChannelConfigurationWizardHandler.INTEGRATION_USER_ERROR_MESSAGE;
import static de.hybris.platform.integrationbackoffice.widgets.authentication.handlers.InboundChannelConfigurationWizardHandler.STEP_ONE;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class InboundChannelConfigurationWizardHandlerUnitTest
{
	private static final String CREATED_CLIENT_ID = "clientId";
	private static final String CREATED_CLIENT_SECRET = "clientSecret";
	private static final String STEP_TWO = "step2";

	@Mock(lenient = true)
	private EmployeeModel selectedUser;
	@Mock(lenient = true)
	private ModelService modelService;
	@Mock(lenient = true)
	private CockpitEventQueue cockpitEventQueue;
	@Mock(lenient = true)
	private InboundChannelConfigNotificationService iccNotificationService;
	@Mock(lenient = true)
	private RegisterIntegrationObjectDefaultService registerIntegrationObjectService;
	@Mock(lenient = true)
	private CustomType customType;

	@Spy
	@InjectMocks
	private InboundChannelConfigurationWizardHandler handler;

	final private InboundChannelConfigurationModel inboundChannelConfiguration = new InboundChannelConfigurationModel();
	final private IntegrationClientCredentialsDetailsModel integrationCCD = new IntegrationClientCredentialsDetailsModel();

	@Test
	public void testSuccessfulCreationOfICCAtStepOne()
	{
		final FlowActionHandlerAdapter adapter = flowActionHandlerAdapter(STEP_ONE);

		handler.perform(customType, adapter, Collections.emptyMap());

		verify(modelService).save(inboundChannelConfiguration);
		verify(cockpitEventQueue).publishEvent(any());
		verify(iccNotificationService).createICCToExposedDestinationSuccessNotification(adapter.getWidgetInstanceManager(), inboundChannelConfiguration);
		verifyZeroInteractions(registerIntegrationObjectService);
	}

	@Test
	public void testSuccessfulCreateICCAndClientCredentialDetailWithCorrectInfoAtStepTwo()
	{
		final FlowActionHandlerAdapter adapter = flowActionHandlerAdapter(STEP_TWO);
		inboundChannelConfiguration.setAuthenticationType(AuthenticationType.OAUTH);
		integrationCCD.setClientId(CREATED_CLIENT_ID);
		integrationCCD.setClientSecret(CREATED_CLIENT_SECRET);
		integrationCCD.setUser(selectedUser);

		handler.perform(customType, adapter, Collections.emptyMap());

		verify(modelService).save(inboundChannelConfiguration);
		verify(cockpitEventQueue).publishEvent(any());
		verify(iccNotificationService).createICCToExposedDestinationSuccessNotification(adapter.getWidgetInstanceManager(), inboundChannelConfiguration);
		verify(registerIntegrationObjectService).createExposedOAuthCredential(integrationCCD);
	}

	@Test
	public void testNoICCOrClientCredentialDetailCreatedIfAuthenticationTypeIsBasicAtStepTwo()
	{
		final FlowActionHandlerAdapter adapter = flowActionHandlerAdapter(STEP_TWO);
		inboundChannelConfiguration.setAuthenticationType(AuthenticationType.BASIC);

		handler.perform(customType, adapter, Collections.emptyMap());

		verify(handler, never()).showErrorMessageBox(anyString(), anyString());
		verifyCreatingActionNeverExecuted();
	}

	@Test
	public void testPopupErrorMessageWhenCreateICCAndClientDetailWhenMissClientIdOrSecretAtStepTwo()
	{
		final FlowActionHandlerAdapter adapter = flowActionHandlerAdapter(STEP_TWO);
		inboundChannelConfiguration.setAuthenticationType(AuthenticationType.OAUTH);
		integrationCCD.setUser(selectedUser);

		doNothing().when(handler).showErrorMessageBox(anyString(), anyString());

		integrationCCD.setClientId(CREATED_CLIENT_ID);
		integrationCCD.setClientSecret(null);

		handler.perform(customType, adapter, Collections.emptyMap());
		verify(handler).showErrorMessageBox(INTEGRATION_CLIENT_CRED_ERROR_MESSAGE, INTEGRATION_ERROR_TITLE);
		verifyCreatingActionNeverExecuted();

		reset(handler);
		doNothing().when(handler).showErrorMessageBox(anyString(), anyString());
		integrationCCD.setClientId(null);
		integrationCCD.setClientSecret(CREATED_CLIENT_SECRET);

		handler.perform(customType, adapter, Collections.emptyMap());
		verify(handler).showErrorMessageBox(INTEGRATION_CLIENT_CRED_ERROR_MESSAGE, INTEGRATION_ERROR_TITLE);
		verifyCreatingActionNeverExecuted();
	}

	@Test
	public void testPopupErrorMessageWhenCreateICCAndClientDetailWhenNoUserSelectedAtStepTwo()
	{
		final FlowActionHandlerAdapter adapter = flowActionHandlerAdapter(STEP_TWO);
		inboundChannelConfiguration.setAuthenticationType(AuthenticationType.OAUTH);

		doNothing().when(handler).showErrorMessageBox(anyString(), anyString());
		integrationCCD.setClientId(CREATED_CLIENT_ID);
		integrationCCD.setClientSecret(CREATED_CLIENT_SECRET);
		integrationCCD.setUser(null);

		handler.perform(customType, adapter, Collections.emptyMap());
		verify(handler).showErrorMessageBox(INTEGRATION_USER_ERROR_MESSAGE, INTEGRATION_ERROR_TITLE);
		verifyCreatingActionNeverExecuted();
	}

	private FlowActionHandlerAdapter flowActionHandlerAdapter(final String step)
	{
		final FlowActionHandlerAdapter adapter = mock(FlowActionHandlerAdapter.class);
		final WidgetInstanceManager manager = mock(WidgetInstanceManager.class);
		lenient().when(adapter.getWidgetInstanceManager()).thenReturn(manager);

		final Widgetslot slot = mock(Widgetslot.class);
		lenient().when(manager.getWidgetslot()).thenReturn(slot);

		final WidgetModel model = widgetModel();
		lenient().when(manager.getModel()).thenReturn(model);

		final ConfigurableFlowController controller = configurableFlowController(step);
		lenient().when(slot.getAttribute(WIDGET_CONTROLLER)).thenReturn(controller);

		return adapter;
	}

	private ConfigurableFlowController configurableFlowController(final String step)
	{
		final ConfigurableFlowController controller = mock(ConfigurableFlowController.class);
		final StepType stepType = mock(StepType.class);
		lenient().when(controller.getCurrentStep()).thenReturn(stepType);
		lenient().when(stepType.getId()).thenReturn(step);

		return controller;
	}

	private WidgetModel widgetModel()
	{
		final WidgetModel model = mock(WidgetModel.class);
		lenient().when(model.getValue(INBOUND_CHANNEL_CONFIG, InboundChannelConfigurationModel.class)).thenReturn(
				inboundChannelConfiguration);
		lenient().when(model.getValue(NEW_INTEGRATION_SERVICE_CLIENT_DETAIL, IntegrationClientCredentialsDetailsModel.class)).thenReturn(integrationCCD);

		return model;
	}

	private void verifyCreatingActionNeverExecuted()
	{
		verifyZeroInteractions(modelService);
		verifyZeroInteractions(cockpitEventQueue);
		verifyZeroInteractions(iccNotificationService);
		verifyZeroInteractions(registerIntegrationObjectService);
	}
}
