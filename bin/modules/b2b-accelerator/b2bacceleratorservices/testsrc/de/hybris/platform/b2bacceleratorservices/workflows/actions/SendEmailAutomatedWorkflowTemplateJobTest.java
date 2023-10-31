/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.workflows.actions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.b2b.model.B2BRegistrationModel;
import de.hybris.platform.b2b.model.B2BRegistrationProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowAttachmentService;
import de.hybris.platform.workflow.model.WorkflowActionModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SendEmailAutomatedWorkflowTemplateJobTest
{

	private SendEmailAutomatedWorkflowTemplateJob sendEmailWorkflowJob;

	private WorkflowAttachmentService workflowAttachmentService;
	private WorkflowActionModel workflowActionModel;

	private CustomerModel customerModel;
	private B2BRegistrationModel b2bRegistrationModel;

	private BusinessProcessService businessProcessService;

	private B2BRegistrationProcessModel b2bRegistrationProcessModel;

	private ModelService modelService;

	private UserService userService;


	@Before
	public void setUp()
	{
		sendEmailWorkflowJob = new SendEmailAutomatedWorkflowTemplateJob();
		workflowActionModel = Mockito.mock(WorkflowActionModel.class);

		workflowAttachmentService = Mockito.mock(WorkflowAttachmentService.class);
		sendEmailWorkflowJob.setWorkflowAttachmentService(workflowAttachmentService);

		customerModel = Mockito.mock(CustomerModel.class);
		b2bRegistrationModel = Mockito.mock(B2BRegistrationModel.class);

		businessProcessService = Mockito.mock(BusinessProcessService.class);
		sendEmailWorkflowJob.setBusinessProcessService(businessProcessService);

		b2bRegistrationProcessModel = Mockito.mock(B2BRegistrationProcessModel.class);


		modelService = Mockito.mock(ModelService.class);
		sendEmailWorkflowJob.setModelService(modelService);

		userService = Mockito.mock(UserService.class);
		sendEmailWorkflowJob.setUserService(userService);


	}


	@Test(expected = IllegalArgumentException.class)
	public void callPerformWithNoB2BRegistrationModel()
	{
		sendEmailWorkflowJob.perform(workflowActionModel);
	}


	@Test(expected = IllegalArgumentException.class)
	public void callPerformWithNoCustomerModel()
	{

		final List<ItemModel> b2bRegistrationModelList = new ArrayList<>();
		b2bRegistrationModelList.add(b2bRegistrationModel);

		Mockito.when(
				workflowAttachmentService.getAttachmentsForAction(workflowActionModel,
						"de.hybris.platform.b2b.model.B2BRegistrationModel")).thenReturn(b2bRegistrationModelList);

		Mockito.when(b2bRegistrationModel.getEmail()).thenReturn("test.hybris@sap.com");

		sendEmailWorkflowJob.perform(workflowActionModel);

	}

	@Test
	public void callPerformWithValidB2BRegistrationModel()
	{
		final String TEST_EMAIL = "test@SAP.com";
		final String TEST_EMAIL_LOWER = "test@sap.com";
		final List<ItemModel> b2bRegistrationModelList = new ArrayList<>();
		b2bRegistrationModelList.add(b2bRegistrationModel);

		Mockito.when(
				workflowAttachmentService.getAttachmentsForAction(workflowActionModel,
						"de.hybris.platform.b2b.model.B2BRegistrationModel")).thenReturn(b2bRegistrationModelList);

		Mockito.when(b2bRegistrationModel.getEmail()).thenReturn(TEST_EMAIL);

		Mockito.when(userService.getUserForUID(TEST_EMAIL_LOWER, CustomerModel.class)).thenReturn(customerModel);

		Mockito.when(businessProcessService.createProcess(nullable(String.class), nullable(String.class))).thenReturn(
				b2bRegistrationProcessModel);

		sendEmailWorkflowJob.perform(workflowActionModel);

		Mockito.verify(businessProcessService).startProcess(b2bRegistrationProcessModel);
	}

}
