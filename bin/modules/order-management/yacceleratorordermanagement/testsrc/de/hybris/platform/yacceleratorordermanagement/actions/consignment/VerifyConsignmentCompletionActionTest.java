/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yacceleratorordermanagement.actions.consignment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.taskassignment.services.WarehousingConsignmentWorkflowService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.Collections;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VerifyConsignmentCompletionActionTest
{

	private static final String CONSIGNMENT_WORKFLOW_CODE = "Workflow_Code";

	@InjectMocks
	private final VerifyConsignmentCompletionAction action = new VerifyConsignmentCompletionAction();

	@Mock
	private ModelService modelService;
	@Mock
	private WorkflowService workflowService;
	@Mock
	private WorkflowProcessingService workflowProcessingService;
	@Mock
	private WarehousingConsignmentWorkflowService warehousingConsignmentWorkflowService;
	@Mock
	private WorkflowModel workflowModel;
	@Mock
	private ConsignmentEntryModel consignmentEntryModel;
	@Mock
	private ConsignmentProcessModel consignmentProcessModel;
	@Spy
	private ConsignmentModel consignmentModel;

	@Before
	public void setup()
	{
		consignmentModel.setConsignmentEntries(Sets.newHashSet(consignmentEntryModel));
		consignmentModel.setConsignmentProcesses(Collections.singleton(consignmentProcessModel));
		consignmentModel.setTaskAssignmentWorkflow(CONSIGNMENT_WORKFLOW_CODE);

		when(consignmentProcessModel.getConsignment()).thenReturn(consignmentModel);
		Mockito.lenient().when(workflowModel.getCode()).thenReturn(CONSIGNMENT_WORKFLOW_CODE);
		Mockito.lenient().when(workflowService.getWorkflowForCode(CONSIGNMENT_WORKFLOW_CODE)).thenReturn(workflowModel);
	}

	@Test
	public void shouldUpdateWorkFlowAndWaitWhenQuantityPendingIsMoreThanZero() throws Exception
	{
		//Given
		when(consignmentEntryModel.getQuantityPending()).thenReturn(1L);

		//When
		final String transition = action.execute(consignmentProcessModel);

		//Then
		verify(warehousingConsignmentWorkflowService).terminateConsignmentWorkflow(consignmentModel);
		verify(warehousingConsignmentWorkflowService).startConsignmentWorkflow(consignmentModel);
		assertEquals(ConsignmentStatus.READY, consignmentModel.getStatus());
		assertTrue(VerifyConsignmentCompletionAction.Transition.WAIT.toString().equals(transition));
	}

	@Test
	public void shouldUpdateWorkFlowAndNotWaitWhenQuantityPendingEqualsZero() throws Exception
	{
		//Given
		when(consignmentEntryModel.getQuantityPending()).thenReturn(0L);

		//When
		final String transition = action.execute(consignmentProcessModel);

		//Then
		verify(warehousingConsignmentWorkflowService).terminateConsignmentWorkflow(consignmentModel);
		verify(warehousingConsignmentWorkflowService, never()).startConsignmentWorkflow(consignmentModel);
		verify(modelService).save(consignmentModel);
		assertTrue(VerifyConsignmentCompletionAction.Transition.OK.toString().equals(transition));
		assertEquals(ConsignmentStatus.CANCELLED, consignmentModel.getStatus());
	}
}
