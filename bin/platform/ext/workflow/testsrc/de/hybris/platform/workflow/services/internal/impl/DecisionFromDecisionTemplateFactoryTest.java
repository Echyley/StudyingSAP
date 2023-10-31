/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.workflow.services.internal.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DecisionFromDecisionTemplateFactoryTest
{
	private final DecisionFromDecisionTemplateFactory factory = new DecisionFromDecisionTemplateFactory();

	@Mock
	private ModelService modelService;

	@Spy
	private final WorkflowDecisionModel decision = new WorkflowDecisionModel();


	@Before
	public void prepare()
	{
		factory.setModelService(modelService);

		Mockito.when(modelService.create(WorkflowDecisionModel.class)).thenReturn(decision);
	}

	@Test
	public void testEmptyToTemplateActions()
	{

		final WorkflowModel workFlow = Mockito.mock(WorkflowModel.class);

		final WorkflowDecisionTemplateModel decisionTemplate = Mockito.mock(WorkflowDecisionTemplateModel.class);

		Assert.assertSame(decision, factory.create(workFlow, decisionTemplate));
		Mockito.verify(decision).setToActions(Collections.EMPTY_LIST);
	}

	@Test
	public void testNoMatchingActionTemplate()
	{
		final WorkflowActionModel actionOne = Mockito.mock(WorkflowActionModel.class);
		Mockito.lenient().when(actionOne.getTemplate()).thenReturn(new WorkflowActionTemplateModel());
		final WorkflowActionModel actionTwo = Mockito.mock(WorkflowActionModel.class);
		Mockito.lenient().when(actionTwo.getTemplate()).thenReturn(new WorkflowActionTemplateModel());

		final WorkflowModel workFlow = Mockito.mock(WorkflowModel.class);
		Mockito.lenient().when(workFlow.getActions()).thenReturn(Arrays.asList(actionOne, actionTwo));

		final WorkflowDecisionTemplateModel decisionTemplate = Mockito.mock(WorkflowDecisionTemplateModel.class);
		Mockito.lenient().when(decisionTemplate.getActionTemplate()).thenReturn(new WorkflowActionTemplateModel());

		Assert.assertSame(decision, factory.create(workFlow, decisionTemplate));
		Mockito.verify(decision).setToActions(Collections.EMPTY_LIST);
	}


	@Test
	public void testCommonMatchingActionTemplate()
	{
		final WorkflowActionTemplateModel commonActionTemplate = new WorkflowActionTemplateModel();

		final WorkflowActionModel actionOne = Mockito.mock(WorkflowActionModel.class);
		Mockito.when(actionOne.getTemplate()).thenReturn(new WorkflowActionTemplateModel());
		final WorkflowActionModel actionTwo = Mockito.mock(WorkflowActionModel.class);
		Mockito.when(actionTwo.getTemplate()).thenReturn(commonActionTemplate);

		final WorkflowModel workFlow = Mockito.mock(WorkflowModel.class);
		Mockito.when(workFlow.getActions()).thenReturn(Arrays.asList(actionOne, actionTwo));

		final WorkflowDecisionTemplateModel decisionTemplate = Mockito.mock(WorkflowDecisionTemplateModel.class);
		Mockito.when(decisionTemplate.getToTemplateActions()).thenReturn(Collections.singleton(commonActionTemplate));

		Assert.assertSame(decision, factory.create(workFlow, decisionTemplate));
		Mockito.verify(decision).setToActions(Arrays.asList(actionTwo));
	}
}
