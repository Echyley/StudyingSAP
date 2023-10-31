/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.actions.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zul.Combobox;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionListener;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.components.Action;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.util.WidgetUtils;


@RunWith(MockitoJUnitRunner.class)
public class WorkflowActionDecisionActionRendererTest
{

	@Spy
	@InjectMocks
	private WorkflowActionDecisionActionRenderer workflowActionDecisionActionRenderer;

	@Mock
	private LabelService labelService;

	@Mock
	private ObjectFacade objectFacade;

	@Mock
	private WidgetUtils widgetUtils;

	@Spy
	private Action parent = new Action();

	@Mock
	private CockpitAction cockpitAction;

	@Mock
	private ActionContext actionContext;

	@Mock
	private ActionListener actionListener;

	@Mock
	private WidgetInstanceManager widgetInstanceManager;

	@Mock
	private WorkflowActionModel workflowAction;

	@Mock
	private PermissionFacade permissionFacade;

	@Mock
	private WorkflowDecisionModel decision;

	@Before
	public void setUp()
	{
		when(parent.getWidgetInstanceManager()).thenReturn(widgetInstanceManager);

		when(actionContext.getData()).thenReturn(workflowAction);
		when(workflowAction.getStatus()).thenReturn(WorkflowActionStatus.IN_PROGRESS);
		when(workflowAction.getDecisions()).thenReturn(createDecisions());

		when(permissionFacade.canReadType(any())).thenReturn(Boolean.TRUE);
		when(permissionFacade.canReadInstance(any())).thenReturn(Boolean.TRUE);
		when(permissionFacade.canChangeType(any())).thenReturn(Boolean.TRUE);
		when(permissionFacade.canChangeInstance(any())).thenReturn(Boolean.TRUE);


		doReturn(new Combobox()).when(workflowActionDecisionActionRenderer).createDecisionCombobox(any(), any());
	}

	@Test
	public void shouldRenderWorkflowActionDecisionAction()
	{
		//when
		workflowActionDecisionActionRenderer.render(parent, cockpitAction, actionContext, true, actionListener);

		//then
		verify(parent).appendChild(any());
		assertThat(parent.getChildren().get(0).getChildren().size()).isEqualTo(2);

		verify(workflowActionDecisionActionRenderer).registerWorkflowActionUpdateListener(any(), any(), any());
	}

	@Test
	public void shouldHavePermissionToAction()
	{
		//when
		final boolean hasPermission = workflowActionDecisionActionRenderer.hasPermissions(workflowAction);

		//then
		assertThat(hasPermission).isEqualTo(true);
	}

	@Test
	public void shouldNotHavePermissionToActionWhenCannotReadType()
	{
		//given
		when(Boolean.valueOf(permissionFacade.canReadType(any()))).thenReturn(Boolean.FALSE);

		//when
		final boolean hasPermission = workflowActionDecisionActionRenderer.hasPermissions(workflowAction);

		//then
		assertThat(hasPermission).isEqualTo(false);
	}

	@Test
	public void shouldNotHavePermissionToActionWhenCannotReadInstance()
	{
		//given
		when(permissionFacade.canReadInstance(any())).thenReturn(Boolean.FALSE);

		//when
		final boolean hasPermission = workflowActionDecisionActionRenderer.hasPermissions(workflowAction);

		//then
		assertThat(hasPermission).isEqualTo(false);
	}

	@Test
	public void shouldNotHavePermissionToActionWhenCannotChangeType()
	{
		//given
		when(permissionFacade.canChangeType(any())).thenReturn(Boolean.FALSE);

		//when
		final boolean hasPermission = workflowActionDecisionActionRenderer.hasPermissions(workflowAction);

		//then
		assertThat(hasPermission).isEqualTo(false);
	}

	@Test
	public void shouldNotHavePermissionToActionWhenCannotChangeInstance()
	{
		//given
		when(permissionFacade.canChangeInstance(any())).thenReturn(Boolean.FALSE);

		//when
		final boolean hasPermission = workflowActionDecisionActionRenderer.hasPermissions(workflowAction);

		//then
		assertThat(hasPermission).isEqualTo(false);
	}

	@Test
	public void shouldHavePermissionToDecision()
	{
		//when
		final boolean hasPermission = workflowActionDecisionActionRenderer.hasPermissions(decision);

		//then
		assertThat(hasPermission).isEqualTo(true);
	}

	@Test
	public void shouldNotHavePermissionToDecisionWhenCannotReadType()
	{
		//given
		when(permissionFacade.canReadType(any())).thenReturn(Boolean.FALSE);

		//when
		final boolean hasPermission = workflowActionDecisionActionRenderer.hasPermissions(decision);

		//then
		assertThat(hasPermission).isEqualTo(false);
	}

	@Test
	public void shouldNotHavePermissionToDecisionWhenCannotReadInstance()
	{
		//given
		when(permissionFacade.canReadInstance(any())).thenReturn(Boolean.FALSE);

		//when
		final boolean hasPermission = workflowActionDecisionActionRenderer.hasPermissions(decision);

		//then
		assertThat(hasPermission).isEqualTo(false);
	}

	@Test
	public void shouldNotHavePermissionWhenDecisionListIsEmpty()
	{
		//given
		final List<WorkflowDecisionModel> decisions = new ArrayList<>();
		when(workflowAction.getDecisions()).thenReturn(decisions);

		//when
		final boolean hasPermission = workflowActionDecisionActionRenderer.hasPermissionToMakeDecision(workflowAction);

		//then
		assertThat(hasPermission).isEqualTo(false);
	}


	protected List<WorkflowDecisionModel> createDecisions()
	{
		final List<WorkflowDecisionModel> decisions = new ArrayList<>();
		decisions.add(decision);
		return decisions;
	}

}
