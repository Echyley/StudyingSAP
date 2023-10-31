/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.workflow;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.core.impl.NotificationStack;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


@RunWith(MockitoJUnitRunner.class)
public class CreateWorkflowTemplateWizardHandlerTest
{

	@Spy
	@InjectMocks
	private CreateWorkflowTemplateWizardHandler createWorkflowTemplateWizardHandler;

	@Mock
	private ModelService modelService;
	@Mock
	private NotificationStack notificationStack;

	private static final CustomType ANY_TYPE = null;
	private static final Map<String, String> ANY_PARAMETERS = null;

	@Test
	public void shouldSendWorkflowTemplateToOutput()
	{
		//given
		final WorkflowTemplateModel workflowTemplate = mock(WorkflowTemplateModel.class);
		final WidgetInstanceManager wim = CockpitTestUtil.mockWidgetInstanceManager();
		final FlowActionHandlerAdapter adapter = mock(FlowActionHandlerAdapter.class);
		wim.getModel().setValue(CreateWorkflowTemplateWizardHandler.WORKFLOW_KEY, workflowTemplate);
		when(adapter.getWidgetInstanceManager()).thenReturn(wim);
		doNothing().when(createWorkflowTemplateWizardHandler).removeConfigurableFlowIdFromNotificationStack(adapter);

		//when
		createWorkflowTemplateWizardHandler.perform(ANY_TYPE, adapter, ANY_PARAMETERS);

		//then
		verify(modelService).save(workflowTemplate);
		verify(wim).sendOutput(CreateWorkflowTemplateWizardHandler.SOCKET_OUT_TEMPLATE, workflowTemplate);
	}

	@Test
	public void shouldNotSaveWorkflowTemplateIfItIsNull()
	{
		//given
		final WorkflowTemplateModel workflowTemplate = mock(WorkflowTemplateModel.class);
		final WidgetInstanceManager wim = CockpitTestUtil.mockWidgetInstanceManager();
		final FlowActionHandlerAdapter adapter = mock(FlowActionHandlerAdapter.class);
		wim.getModel().setValue(CreateWorkflowTemplateWizardHandler.WORKFLOW_KEY, null);
		when(adapter.getWidgetInstanceManager()).thenReturn(wim);

		//when
		createWorkflowTemplateWizardHandler.perform(ANY_TYPE, adapter, ANY_PARAMETERS);

		//then
		verify(modelService, never()).save(workflowTemplate);
		verify(wim, never()).sendOutput(CreateWorkflowTemplateWizardHandler.SOCKET_OUT_TEMPLATE, workflowTemplate);
	}

	@Test
	public void shouldConfigurableFlowIdBeRemovedFromNotificationStackBeforeOpeningWorkflowDesigner()
	{
		// given
		final WorkflowTemplateModel workflowTemplate = mock(WorkflowTemplateModel.class);
		final WidgetInstanceManager wim = CockpitTestUtil.mockWidgetInstanceManager();
		final FlowActionHandlerAdapter adapter = mock(FlowActionHandlerAdapter.class);
		wim.getModel().setValue(CreateWorkflowTemplateWizardHandler.WORKFLOW_KEY, workflowTemplate);
		when(adapter.getWidgetInstanceManager()).thenReturn(wim);

		// when
		createWorkflowTemplateWizardHandler.perform(ANY_TYPE, adapter, ANY_PARAMETERS);

		// then
		final InOrder inOrder = inOrder(notificationStack, wim, adapter);
		inOrder.verify(notificationStack).onTemplateClosed(eq(wim.getWidgetslot().getWidgetInstance()));
		inOrder.verify(wim).sendOutput(eq(CreateWorkflowTemplateWizardHandler.SOCKET_OUT_TEMPLATE), eq(workflowTemplate));
		inOrder.verify(adapter).done();
	}
}
