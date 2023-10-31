/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.actions.workflow;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.workflow.impl.DefaultWorkflowDecisionMaker;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.engine.WidgetInstanceManager;


@RunWith(MockitoJUnitRunner.class)
public class WorkflowActionDecisionActionTest
{

	@Spy
	@InjectMocks
	private WorkflowActionDecisionAction workflowActionDecisionAction;

	@Mock
	private DefaultWorkflowDecisionMaker decisionMaker;

	@Mock
	private ActionContext actionContext;

	@Mock
	private WidgetInstanceManager widgetInstanceManager;

	@Mock
	private WorkflowDecisionModel workflowDecision;

	@Mock
	private WorkflowActionModel workflowAction;

	@Mock
	private WidgetModel widgetModel;

	private static final String SELECTED_DECISION = "selectedDecision";
	private static final String ACTION_UID = "uid";

	@Before
	public void setUp()
	{
		when(actionContext.getParameter("wim")).thenReturn(widgetInstanceManager);

		when(workflowDecision.getAction()).thenReturn(workflowAction);
		when(workflowAction.getStatus()).thenReturn(WorkflowActionStatus.IN_PROGRESS);

		when(actionContext.getParameter(ActionContext.ACTION_UID)).thenReturn(ACTION_UID);
		when(actionContext.getParameter(ActionContext.PARENT_WIDGET_MODEL)).thenReturn(widgetModel);

		final Map<String, Object> widgetModelValuesMap = new HashMap<>();
		widgetModelValuesMap.put(ACTION_UID, workflowDecision);
		when(widgetModel.getValue(SELECTED_DECISION, Map.class)).thenReturn(widgetModelValuesMap);
	}

	@Test
	public void shouldReturnActionSuccessWhenMakeDecisionIsCalled()
	{
		//when
		final ActionResult actionResult = workflowActionDecisionAction.perform(actionContext);

		//then
		assertThat(actionResult.getResultCode()).isEqualTo(ActionResult.SUCCESS);
	}

	@Test
	public void shouldReturnActionErrorWhenWidgetInstanceManagerIsNull()
	{
		//given
		when(actionContext.getParameter("wim")).thenReturn(null);

		//when
		final ActionResult actionResult = workflowActionDecisionAction.perform(actionContext);

		//then
		assertThat(actionResult.getResultCode()).isEqualTo(ActionResult.ERROR);
	}

	@Test
	public void shouldReturnActionErrorWhenSelectedDecisionIsNull()
	{
		//given
		when(widgetModel.getValue(SELECTED_DECISION, Map.class)).thenReturn(Collections.emptyMap());

		//when
		final ActionResult actionResult = workflowActionDecisionAction.perform(actionContext);

		//then
		assertThat(actionResult.getResultCode()).isEqualTo(ActionResult.ERROR);
	}

	@Test
	public void shouldReturnActionErrorWhenSelectedTaskIsNotInProgress()
	{
		//given
		when(workflowAction.getStatus()).thenReturn(WorkflowActionStatus.COMPLETED);

		//when
		final ActionResult actionResult = workflowActionDecisionAction.perform(actionContext);

		//then
		assertThat(actionResult.getResultCode()).isEqualTo(ActionResult.ERROR);
	}

}
