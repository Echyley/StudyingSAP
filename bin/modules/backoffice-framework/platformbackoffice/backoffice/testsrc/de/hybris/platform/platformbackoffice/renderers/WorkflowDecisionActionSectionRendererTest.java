/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.renderers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.hybris.backoffice.workflow.impl.DefaultWorkflowDecisionMaker;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;


@RunWith(MockitoJUnitRunner.class)
public class WorkflowDecisionActionSectionRendererTest extends AbstractCockpitngUnitTest<WorkflowDecisionActionSectionRenderer>
{

	@Mock
	private WidgetInstanceManager widgetInstanceManager;

	@Mock
	private WidgetModel widgetModel;

	@Mock
	private DefaultWorkflowDecisionMaker workflowDecisionMaker;

	@Mock
	private WorkflowActionModel workflowAction;

	@Mock
	private WorkflowDecisionModel workflowDecision;

	@Mock
	private PermissionFacade permissionFacade;

	@Spy
	@InjectMocks
	private WorkflowDecisionActionSectionRenderer workflowDecisionActionSectionRenderer;

	@Override
	protected Class<? extends WorkflowDecisionActionSectionRenderer> getWidgetType()
	{
		return WorkflowDecisionActionSectionRenderer.class;
	}

	@Before
	public void setUp()
	{
		when(widgetInstanceManager.getModel()).thenReturn(widgetModel);
	}

	@Test
	public void shouldPerformMakingDecisionWhenModelNotChanged()
	{
		//given
		when(widgetModel.getValue(WorkflowDecisionActionSectionRenderer.MODEL_VALUE_CHANGED, Boolean.class))
				.thenReturn(Boolean.FALSE);

		//when
		workflowDecisionActionSectionRenderer.performAction(workflowAction, workflowDecision, widgetInstanceManager);

		//then
		verify(workflowDecisionMaker).makeDecision(any(), any(), any());
	}

	@Test
	public void shouldNotPerformMakingDecisionWhenModelChanged()
	{
		//given
		when(widgetModel.getValue(WorkflowDecisionActionSectionRenderer.MODEL_VALUE_CHANGED, Boolean.class))
				.thenReturn(Boolean.TRUE);

		//when
		workflowDecisionActionSectionRenderer.performAction(workflowAction, workflowDecision, widgetInstanceManager);

		//then
		verifyZeroInteractions(workflowDecisionMaker);
	}

	@Test
	public void shouldHavePermissionToAction()
	{
		//given
		when(permissionFacade.canChangeInstance(any())).thenReturn(true);
		when(permissionFacade.canReadInstance(any())).thenReturn(true);

		//when
		final boolean hasPermission = workflowDecisionActionSectionRenderer.hasPermissions(workflowAction);

		//then
		assertThat(hasPermission).isEqualTo(true);
	}

	@Test
	public void shouldNotHavePermissionToActionWhenCannotReadInstance()
	{
		//given
		when(permissionFacade.canReadInstance(any())).thenReturn(false);

		//when
		final boolean hasPermission = workflowDecisionActionSectionRenderer.hasPermissions(workflowAction);

		//then
		assertThat(hasPermission).isEqualTo(false);
	}

	@Test
	public void shouldNotHavePermissionToActionWhenCannotChangeInstance()
	{
		//given
		when(permissionFacade.canChangeInstance(any())).thenReturn(false);
		when(permissionFacade.canReadInstance(any())).thenReturn(true);
		//when
		final boolean hasPermission = workflowDecisionActionSectionRenderer.hasPermissions(workflowAction);

		//then
		assertThat(hasPermission).isEqualTo(false);
	}

	@Test
	public void shouldHavePermissionToMakeDecision()
	{
		//given
		doReturn(true).when(workflowDecisionActionSectionRenderer).hasPermissions(workflowAction);
		doReturn(Lists.newArrayList(workflowAction)).when(workflowDecisionActionSectionRenderer)
				.getPermittedDecisions(workflowAction);

		//when
		final boolean hasPermission = workflowDecisionActionSectionRenderer.hasPermissionToMakeDecision(workflowAction);

		//then
		assertThat(hasPermission).isEqualTo(true);
	}

	@Test
	public void shouldGetEmptyListOfPermittedDecisionsWhenCanNotReadType()
	{
		//given
		final WorkflowDecisionModel permission = mock(WorkflowDecisionModel.class);
		when(workflowAction.getDecisions()).thenReturn(Lists.newArrayList(permission));

		//when
		final List<WorkflowDecisionModel> permittedDecisions = workflowDecisionActionSectionRenderer
				.getPermittedDecisions(workflowAction);

		//then
		assertThat(permittedDecisions).isEmpty();
	}
}
