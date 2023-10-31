/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.workflows.actions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BRegistrationModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowAttachmentService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RegistrationApprovedAutomatedWorkflowTemplateJobTest
{

	private RegistrationApprovedAutomatedWorkflowTemplateJob registrationApprovedAutomatedWorkflowTemplateJob;

	private WorkflowActionModel workflowActionModel;
	private WorkflowDecisionModel workflowDecisionModel;
	private CustomerModel customer;
	private B2BCustomerModel b2BCustomer;
	private B2BRegistrationModel b2BRegistrationModel;
	private TitleModel titleModel;
	private B2BUnitModel b2bUnitModel;

	private ModelService modelService;
	private WorkflowAttachmentService workflowAttachmentService;
	private UserService userService;
	private CustomerEmailResolutionService defaultCustomerEmailResolutionService;

	private List<ItemModel> attachments;
	private List<CustomerModel> customers;

	private static final String NAME = "Richard Feynman";
	private static final String TITLE = "Mr";
	private static final String DEFAULT_EMAIL = "richard.feynman@Ttest.ca";
	private static final String DEFAULT_CUSTOMER_ID = "bffa13d2-80e1-4696-bce1-dfd88d564576";
	private static final String DEFAULT_EMAIL_LOWER = "richard.feynman@ttest.ca";
	private static final String DECISION_NAME = "Default decision name";
	private static final String DEFAULT_B2BUNIT_NAME = "Pronto";


	@Before
	public void setUp()
	{
		registrationApprovedAutomatedWorkflowTemplateJob = new RegistrationApprovedAutomatedWorkflowTemplateJob();

		registrationApprovedAutomatedWorkflowTemplateJob.setModelService(modelService = mock(ModelService.class));

		registrationApprovedAutomatedWorkflowTemplateJob
				.setWorkflowAttachmentService(workflowAttachmentService = mock(WorkflowAttachmentService.class));

		registrationApprovedAutomatedWorkflowTemplateJob.setUserService(userService = mock(UserService.class));

		registrationApprovedAutomatedWorkflowTemplateJob.setDefaultCustomerEmailResolutionService(defaultCustomerEmailResolutionService = mock(
				CustomerEmailResolutionService.class));

		customer = createCustomerModel();
		b2BCustomer = createB2BCustomerModel(customer);
		b2BRegistrationModel = createB2BRegistrationModel();
		b2BRegistrationModel.setCustomer(customer);
		b2bUnitModel = createB2BUnit();

		customers = Arrays.asList(customer);

		workflowActionModel = new WorkflowActionModel();

		workflowDecisionModel = new WorkflowDecisionModel();
		workflowDecisionModel.setName(DECISION_NAME, Locale.getDefault());

		workflowActionModel.setDecisions(Arrays.asList(workflowDecisionModel));
	}


	@Test
	public void testPerform()
	{
		attachments = Arrays.asList(customer, b2BRegistrationModel);

		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation)
			{
				customers = new LinkedList<>();

				return null;
			}
		}).when(modelService).remove(customer);


		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation)
			{
				customers = new ArrayList<CustomerModel>();
				customers.add(b2BCustomer);

				return null;
			}
		}).when(modelService).saveAll(b2BCustomer, b2BRegistrationModel);



		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation)
			{
				attachments = new LinkedList<ItemModel>();
				attachments = Arrays.asList(createB2BCustomerModel(customers.get(0)), b2BRegistrationModel);

				return null;
			}
		}).when(workflowAttachmentService).addItems(nullable(WorkflowModel.class), nullable(attachments.getClass()));


		workflowAttachmentService.addItems(workflowActionModel.getWorkflow(), attachments);
		//		when(userService.getUserForUID(DEFAULT_EMAIL_LOWER, CustomerModel.class)).thenReturn(customer);

		final List<ItemModel> customerAsList = new LinkedList<ItemModel>();
		customerAsList.add(customer);

		final List<ItemModel> b2BRegistrationModelAsList = new LinkedList<ItemModel>();
		b2BRegistrationModelAsList.add(b2BRegistrationModel);
		when(workflowAttachmentService.getAttachmentsForAction(workflowActionModel, B2BRegistrationModel.class.getName()))
				.thenReturn(b2BRegistrationModelAsList);

		when(modelService.create(B2BCustomerModel.class)).thenReturn(b2BCustomer);

		final WorkflowModel workflowModel = new WorkflowModel();
		workflowModel.setActions(Arrays.asList(workflowActionModel));

		workflowActionModel.setWorkflow(workflowModel);

		final WorkflowDecisionModel decision = registrationApprovedAutomatedWorkflowTemplateJob.perform(workflowActionModel);

		assertEquals("The right decision shoule be returned", decision, workflowDecisionModel);

		assertTrue("B2BCustomer should have been created", attachments.get(0) instanceof B2BCustomerModel);

		assertEquals("B2BCustomer should have been assigned a B2BUnit",
				((B2BCustomerModel) attachments.get(0)).getDefaultB2BUnit().getName(), b2bUnitModel.getName());

		assertTrue("b2BRegistrationModel should still be in workflow attachment",
				attachments.get(1).getClass().getName().endsWith("B2BRegistrationModel"));
		final ArgumentCaptor<B2BCustomerModel> b2bCustomerModelCaptor = ArgumentCaptor.forClass(B2BCustomerModel.class);
		final ArgumentCaptor<B2BRegistrationModel> b2BRegistrationModelCaptor = ArgumentCaptor.forClass(B2BRegistrationModel.class);
		verify(modelService).saveAll(b2bCustomerModelCaptor.capture(), b2BRegistrationModelCaptor.capture());
		assertEquals(DEFAULT_CUSTOMER_ID, b2bCustomerModelCaptor.getValue().getCustomerID());
	}

	@Test
	public void testPerformWhenB2BRegistrationWithoutCustomer()
	{
		b2BRegistrationModel.setCustomer(null);
		attachments = Arrays.asList(customer, b2BRegistrationModel);

		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation)
			{
				customers = new LinkedList<>();

				return null;
			}
		}).when(modelService).remove(customer);


		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation)
			{
				customers = new ArrayList<CustomerModel>();
				customers.add(b2BCustomer);

				return null;
			}
		}).when(modelService).saveAll(b2BCustomer, b2BRegistrationModel);



		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation)
			{
				attachments = new LinkedList<ItemModel>();
				attachments = Arrays.asList(createB2BCustomerModel(customers.get(0)), b2BRegistrationModel);

				return null;
			}
		}).when(workflowAttachmentService).addItems(nullable(WorkflowModel.class), nullable(attachments.getClass()));


		workflowAttachmentService.addItems(workflowActionModel.getWorkflow(), attachments);
		when(userService.getUserForUID(DEFAULT_EMAIL_LOWER, CustomerModel.class)).thenReturn(customer);

		final List<ItemModel> customerAsList = new LinkedList<ItemModel>();
		customerAsList.add(customer);

		final List<ItemModel> b2BRegistrationModelAsList = new LinkedList<ItemModel>();
		b2BRegistrationModelAsList.add(b2BRegistrationModel);
		when(workflowAttachmentService.getAttachmentsForAction(workflowActionModel,
				B2BRegistrationModel.class.getName())).thenReturn(b2BRegistrationModelAsList);

		when(modelService.create(B2BCustomerModel.class)).thenReturn(b2BCustomer);

		final WorkflowModel workflowModel = new WorkflowModel();
		workflowModel.setActions(Arrays.asList(workflowActionModel));

		workflowActionModel.setWorkflow(workflowModel);

		final WorkflowDecisionModel decision = registrationApprovedAutomatedWorkflowTemplateJob.perform(workflowActionModel);

		assertEquals("The right decision shoule be returned", decision, workflowDecisionModel);

		assertTrue("B2BCustomer should have been created", attachments.get(0) instanceof B2BCustomerModel);

		assertEquals("B2BCustomer should have been assigned a B2BUnit",
				((B2BCustomerModel) attachments.get(0)).getDefaultB2BUnit().getName(), b2bUnitModel.getName());

		assertTrue("b2BRegistrationModel should still be in workflow attachment",
				attachments.get(1).getClass().getName().endsWith("B2BRegistrationModel"));
		final ArgumentCaptor<B2BCustomerModel> b2bCustomerModelCaptor = ArgumentCaptor.forClass(B2BCustomerModel.class);
		final ArgumentCaptor<B2BRegistrationModel> b2BRegistrationModelCaptor = ArgumentCaptor.forClass(B2BRegistrationModel.class);
		verify(modelService).saveAll(b2bCustomerModelCaptor.capture(), b2BRegistrationModelCaptor.capture());
		assertEquals(DEFAULT_CUSTOMER_ID, b2bCustomerModelCaptor.getValue().getCustomerID());
	}

	/******
	 * */
	private CustomerModel createCustomerModel()
	{

		final CustomerModel customer = new CustomerModel();

		customer.setName(NAME);
		customer.setUid(DEFAULT_EMAIL);
		customer.setCustomerID(DEFAULT_CUSTOMER_ID);

		final TitleModel titleModel = new TitleModel();
		titleModel.setCode(TITLE);
		customer.setTitle(titleModel);

		return customer;

	}


	/**********
	 *
	 * */
	private B2BCustomerModel createB2BCustomerModel(final CustomerModel customer)
	{
		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();

		b2bCustomer.setEmail(customer.getUid());
		b2bCustomer.setName(customer.getName());
		b2bCustomer.setTitle(customer.getTitle());
		b2bCustomer.setUid(customer.getUid());
		b2bCustomer.setDefaultB2BUnit(createB2BUnit());

		return b2bCustomer;
	}


	/*****
	 *
	 * */
	private B2BRegistrationModel createB2BRegistrationModel()
	{

		final B2BRegistrationModel b2BRegistration = new B2BRegistrationModel();

		b2BRegistration.setTitle(titleModel);

		final CountryModel country = new CountryModel();
		country.setIsocode("US");
		country.setActive(Boolean.TRUE);
		b2BRegistration.setEmail(DEFAULT_EMAIL);
		b2BRegistration.setCompanyAddressCountry(country);

		return b2BRegistration;

	}


	/*****
	 *
	 *
	 */
	private B2BUnitModel createB2BUnit()
	{
		final B2BUnitModel unit = new B2BUnitModel();
		unit.setName(DEFAULT_B2BUNIT_NAME);

		return unit;
	}


}
