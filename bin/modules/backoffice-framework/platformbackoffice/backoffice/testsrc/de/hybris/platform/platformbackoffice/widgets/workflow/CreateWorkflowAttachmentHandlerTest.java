/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.workflow;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowItemAttachmentModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.widgets.notificationarea.DefaultNotificationService;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


@RunWith(MockitoJUnitRunner.class)
public class CreateWorkflowAttachmentHandlerTest
{

	@Spy
	@InjectMocks
	private CreateWorkflowAttachmentHandler handler;
	@Spy
	private DefaultNotificationService defaultNotificationService;

	@Test
	public void shouldWarningAppearWhenAddedItemAlreadyExists()
	{
		// given
		final WorkflowItemAttachmentModel attachment = mock(WorkflowItemAttachmentModel.class);
		final WorkflowItemAttachmentModel workflowAttachment = mock(WorkflowItemAttachmentModel.class);
		final WorkflowModel workflow = mock(WorkflowModel.class);
		final ItemModel item = mock(ItemModel.class);
		given(workflowAttachment.getItem()).willReturn(item);
		given(attachment.getItem()).willReturn(item);
		given(attachment.getWorkflow()).willReturn(workflow);
		given(workflow.getAttachments()).willReturn(Lists.newArrayList(workflowAttachment));
		doReturn(attachment).when(handler).getWorkflowAttachment(null);

		// when
		handler.perform(null, null, null);

		// then
		verify(defaultNotificationService).notifyUser(anyString(), any(), any(), any());
	}

	@Test
	public void shouldWorkflowActionBeIncreasedByNewAttachment()
	{
		// given
		final List<WorkflowItemAttachmentModel> currentAttachments = Lists.newArrayList(mock(WorkflowItemAttachmentModel.class),
				mock(WorkflowItemAttachmentModel.class));
		final WorkflowActionModel workflowAction = mock(WorkflowActionModel.class);
		given(workflowAction.getAttachments()).willReturn(currentAttachments);
		final WorkflowModel workflow = mock(WorkflowModel.class);
		given(workflow.getActions()).willReturn(Lists.newArrayList(workflowAction));
		final WorkflowItemAttachmentModel workflowItemAttachment = mock(WorkflowItemAttachmentModel.class);
		given(workflowItemAttachment.getWorkflow()).willReturn(workflow);

		doReturn(workflowItemAttachment).when(handler).getWorkflowAttachment(any());
		doReturn(false).when(handler).hasAttachment(any(), any());
		doNothing().when(handler).setWorkflowAttachment(any(), any());


		// when
		handler.perform(null, mock(FlowActionHandlerAdapter.class), null);

		// then
		then(workflowAction).should().setAttachments(argThat(new ArgumentMatcher<List<WorkflowItemAttachmentModel>>()
		{
			@Override
			public boolean matches(List<WorkflowItemAttachmentModel> workflowItemAttachmentModels) {
				return workflowItemAttachmentModels.size() == currentAttachments.size() + 1;
			}
		}));
	}

}
