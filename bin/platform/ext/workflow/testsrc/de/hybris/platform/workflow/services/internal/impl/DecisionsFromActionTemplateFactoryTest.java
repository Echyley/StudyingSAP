/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.workflow.services.internal.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.link.LinkModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.jalo.link.Link;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.workflow.daos.WorkflowLinksDao;
import de.hybris.platform.workflow.daos.WorkflowLinksTemplateDao;
import de.hybris.platform.workflow.model.AbstractWorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DecisionsFromActionTemplateFactoryTest
{
	private final DecisionsFromActionTemplateFactory factory = new DecisionsFromActionTemplateFactory();

	@Mock
	private ModelService modelService;

	@Mock
	private WorkflowLinksDao workflowLinksDao;


	@Mock
	private WorkflowLinksTemplateDao workflowLinksTemplateDao;

	@Mock
	private DecisionFromDecisionTemplateFactory decisionFactory;

	@Mock
	private TypeService typeService;

	@Mock
	private I18NService i18nService;

	@Before
	public void prepare()
	{
		factory.setModelService(modelService);
		factory.setWorkflowLinksDao(workflowLinksDao);
		factory.setWorkflowLinksTemplateDao(workflowLinksTemplateDao);
		factory.setDecisionFactory(decisionFactory);
		factory.setTypeService(typeService);
		factory.setI18nService(i18nService);
	}

	@Test
	public void testEmptyDecisionsListNoMatchingAction()
	{
		final WorkflowModel workFlow = mock(WorkflowModel.class);
		final WorkflowActionTemplateModel templateAction = mock(WorkflowActionTemplateModel.class);

		try
		{
			factory.create(workFlow, templateAction);
			fail("Should have a NPE when no action found, by matching WorkflowActionTemplateModel??");
		}
		catch (final NullPointerException npe)
		{
			//ok here
		}
	}

	@Test
	public void testEmptyDecisionsListMatchingActionTemplate()
	{
		final WorkflowActionTemplateModel matchingTemplateAction = mock(WorkflowActionTemplateModel.class);

		final WorkflowActionModel workFlowActionOne = mock(WorkflowActionModel.class);
		when(workFlowActionOne.getTemplate()).thenReturn(new WorkflowActionTemplateModel());
		final WorkflowActionModel workFlowActionTwo = mock(WorkflowActionModel.class);
		when(workFlowActionTwo.getTemplate()).thenReturn(matchingTemplateAction);

		final WorkflowModel workFlow = mock(WorkflowModel.class);
		when(workFlow.getActions()).thenReturn(Arrays.asList(workFlowActionOne, workFlowActionTwo));

		assertEquals(Collections.EMPTY_LIST, factory.create(workFlow, matchingTemplateAction));

	}

	@Test
	public void testDecisionsListWithNoMatchingAction()
	{
		final WorkflowModel workFlow = mock(WorkflowModel.class);

		final WorkflowActionTemplateModel templateAction = mock(WorkflowActionTemplateModel.class);

		try
		{
			factory.create(workFlow, templateAction);
			fail("Should have a NPE when no action found, by matching WorkflowActionTemplateModel??");
		}
		catch (final NullPointerException npe)
		{
			//ok here
		}
	}


	@Test
	public void testDecisionsListWithMatchingActionTemplate()
	{

		final WorkflowActionModel workFlowActionOne = mock(WorkflowActionModel.class);
		when(workFlowActionOne.getTemplate()).thenReturn(new WorkflowActionTemplateModel());
		final WorkflowActionModel workFlowActionTwo = mock(WorkflowActionModel.class);

		final WorkflowModel workFlow = mock(WorkflowModel.class);
		when(workFlow.getActions()).thenReturn(Arrays.asList(workFlowActionOne, workFlowActionTwo));


		final WorkflowDecisionModel decisionOne = mock(WorkflowDecisionModel.class);
		final WorkflowDecisionModel decisionTwo = mock(WorkflowDecisionModel.class);

		final WorkflowDecisionTemplateModel decisionTemplateOne = mock(WorkflowDecisionTemplateModel.class);
		when(decisionTemplateOne.getActionTemplate()).thenReturn(new WorkflowActionTemplateModel());

		final WorkflowDecisionTemplateModel decisionTemplateTwo = mock(WorkflowDecisionTemplateModel.class);
		when(decisionTemplateTwo.getActionTemplate()).thenReturn(null);

		final WorkflowActionTemplateModel matchingTemplateAction = mock(WorkflowActionTemplateModel.class);
		when(matchingTemplateAction.getDecisionTemplates()).thenReturn(
				Arrays.asList(decisionTemplateOne, decisionTemplateTwo));
		when(workFlowActionTwo.getTemplate()).thenReturn(matchingTemplateAction);

		when(decisionFactory.create(workFlow, decisionTemplateOne)).thenReturn(decisionOne);
		when(decisionFactory.create(workFlow, decisionTemplateTwo)).thenReturn(decisionTwo);

		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		when(typeService.getComposedTypeForCode(AbstractWorkflowDecisionModel._TYPECODE)).thenReturn(composedTypeModel);
		when(composedTypeModel.getDeclaredattributedescriptors()).thenReturn(new ArrayList<>());

		assertEquals(Arrays.asList(decisionOne, decisionTwo), factory.create(workFlow, matchingTemplateAction));

	}

	@Test
	public void testDecisionsListWithMatchingActionTemplateAndDecisionAction()
	{
		final WorkflowActionTemplateModel decisionMatchingActionTemplate = new WorkflowActionTemplateModel();

		final WorkflowActionModel workFlowActionOne = mock(WorkflowActionModel.class);
		when(workFlowActionOne.getTemplate()).thenReturn(decisionMatchingActionTemplate);
		final WorkflowActionModel workFlowActionTwo = mock(WorkflowActionModel.class);

		final WorkflowModel workFlow = mock(WorkflowModel.class);
		when(workFlow.getActions()).thenReturn(Arrays.asList(workFlowActionOne, workFlowActionTwo));


		final WorkflowDecisionModel decisionOne = mock(WorkflowDecisionModel.class);
		final WorkflowDecisionModel decisionTwo = mock(WorkflowDecisionModel.class);

		final WorkflowDecisionTemplateModel decisionTemplateOne = mock(WorkflowDecisionTemplateModel.class);
		when(decisionTemplateOne.getActionTemplate()).thenReturn(new WorkflowActionTemplateModel());

		final WorkflowDecisionTemplateModel decisionTemplateTwo = mock(WorkflowDecisionTemplateModel.class);
		when(decisionTemplateTwo.getActionTemplate()).thenReturn(decisionMatchingActionTemplate);

		final WorkflowActionTemplateModel matchingTemplateAction = mock(WorkflowActionTemplateModel.class);
		when(matchingTemplateAction.getDecisionTemplates()).thenReturn(
				Arrays.asList(decisionTemplateOne, decisionTemplateTwo));
		when(workFlowActionTwo.getTemplate()).thenReturn(matchingTemplateAction);

		when(decisionFactory.create(workFlow, decisionTemplateOne)).thenReturn(decisionOne);
		when(decisionFactory.create(workFlow, decisionTemplateTwo)).thenReturn(decisionTwo);

		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		when(typeService.getComposedTypeForCode(AbstractWorkflowDecisionModel._TYPECODE)).thenReturn(composedTypeModel);
		when(composedTypeModel.getDeclaredattributedescriptors()).thenReturn(new ArrayList<>());

		assertEquals(Arrays.asList(decisionOne, decisionTwo), factory.create(workFlow, matchingTemplateAction));

	}


	@Test
	public void testDecisionsListWithMatchingActionTemplateCopyLinks() throws Exception
	{


		final WorkflowActionModel workFlowAction = mock(WorkflowActionModel.class);

		final WorkflowModel workFlow = mock(WorkflowModel.class);
		when(workFlow.getActions()).thenReturn(List.of(workFlowAction));

		final WorkflowDecisionModel decisionOne = mock(WorkflowDecisionModel.class);

		final WorkflowDecisionTemplateModel decisionTemplateOne = mock(WorkflowDecisionTemplateModel.class);
		when(decisionTemplateOne.getActionTemplate()).thenReturn(new WorkflowActionTemplateModel());

		final WorkflowActionTemplateModel matchingTemplateAction = mock(WorkflowActionTemplateModel.class);
		when(matchingTemplateAction.getDecisionTemplates()).thenReturn(List.of(decisionTemplateOne));
		when(workFlowAction.getTemplate()).thenReturn(matchingTemplateAction);

		when(decisionFactory.create(workFlow, decisionTemplateOne)).thenReturn(decisionOne);


		final LinkModel linkTemplateOne = mock(LinkModel.class);
		when(linkTemplateOne.getTarget()).thenReturn(new WorkflowActionTemplateModel());
		final LinkModel linkTemplateTwo = mock(LinkModel.class);
		when(linkTemplateTwo.getTarget()).thenReturn(matchingTemplateAction);//here it is a link matching action template

		when(workflowLinksTemplateDao.findLinksByDecisionAndAction(decisionTemplateOne, null)).thenReturn(
				Arrays.asList(linkTemplateOne, linkTemplateTwo));

		final LinkModel matchingTemplateLink = mock(LinkModel.class);

		when(workflowLinksDao.findLinksByDecisionAndAction(decisionOne, workFlowAction)).thenReturn(
				Collections.singleton(matchingTemplateLink));

		//record link setAttributeForLink 
		final Link targetLink = mock(Link.class);//
		final Link sourceLink = mock(Link.class);//

		when(modelService.getSource(linkTemplateTwo)).thenReturn(sourceLink);//source
		when(modelService.getSource(matchingTemplateLink)).thenReturn(targetLink);
		when(sourceLink.getAttribute("andConnectionTemplate")).thenReturn("fancyTemplateFlag");

		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		when(typeService.getComposedTypeForCode(AbstractWorkflowDecisionModel._TYPECODE)).thenReturn(composedTypeModel);
		when(composedTypeModel.getDeclaredattributedescriptors()).thenReturn(new ArrayList<>());

		//method
		assertEquals(List.of(decisionOne), factory.create(workFlow, matchingTemplateAction));

		//verify link setAttributeForLink

		Mockito.verify(sourceLink).getAttribute("andConnectionTemplate");
		Mockito.verify(targetLink).setAttribute("andconnection", "fancyTemplateFlag");
	}
}
