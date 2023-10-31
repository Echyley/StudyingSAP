/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.workflows.actions;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BRegistrationModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.registration.B2BRegistrationService;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.workflow.WorkflowActionService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import javax.annotation.Resource;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class B2BRegistrationWorkflowIntegrationTest extends ServicelayerTest
{
	@Resource
	private BaseStoreService baseStoreService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private WorkflowActionService workflowActionService;

	@Resource
	private B2BRegistrationService b2bRegistrationService;

	@Resource
	private BusinessProcessService businessProcessService;

	@Resource
	protected WorkflowProcessingService workflowProcessingService;

	@Resource
	private WorkflowTemplateService workflowTemplateService;

	@Resource
	protected WorkflowService newestWorkflowService;

	@Resource
	protected ModelService modelService;

	@Resource
	protected SessionService sessionService;

	@Resource
	protected I18NService i18nService;

	@Resource
	protected CommonI18NService commonI18NService;

	@Resource
	private UserService userService;




	private final static String REGISTRATION_APPROVED_DECISION_CODE = "B2BRRegistrationApproved";
	private final static String REGISTRATION_REJECTED_DECISION_CODE = "B2BRRegistrationRejected";
	private final static String EMAIL = "peter.parker@dailybugle.com";
	private final static String NAME = "Peter Parker";
	private final static String CUSTOMER_ID = "bffa13d2-80e1-4696-bce1-dfd88d564576";

	private Transaction tx;

	//Process Codes
	private final static String REGISTRATION_PENDING_APPROVAL_EMAIL_PROCESS_CODE =
			"b2bRegistrationPendingApprovalEmailProcess" + "-" + EMAIL;
	private final static String REGISTRATION_RECEIVED_EMAIL_PROCESS_CODE = "b2bRegistrationReceivedEmailProcess" + "-" + EMAIL;
	private final static String REGISTRATION_APPROVED_EMAIL_PROCESS_CODE = "b2bRegistrationApprovedEmailProcess" + "-" + EMAIL;
	private final static String REGISTRATION_REJECTED_EMAIL_PROCESS_CODE = "b2bRegistrationRejectedEmailProcess" + "-" + EMAIL;

	//Add all process Codes
	private final String[] processes = { REGISTRATION_PENDING_APPROVAL_EMAIL_PROCESS_CODE,
			REGISTRATION_RECEIVED_EMAIL_PROCESS_CODE, REGISTRATION_APPROVED_EMAIL_PROCESS_CODE,
			REGISTRATION_REJECTED_EMAIL_PROCESS_CODE };


	@Before
	public void beforeTest() throws Exception
	{
		//Setup Test Infrastructure
		Registry.setCurrentTenantByID("junit");
		ServicelayerTest.createCoreData();
		ServicelayerTest.createDefaultUsers();

		CatalogManager.getInstance().createEssentialData(java.util.Collections.EMPTY_MAP, null);

		tx = Transaction.current();
		tx.begin();

		//import Necessary Data
		loadB2bRegistrationTestData();
		simulateRegistrationFacade();

		sessionService.getCurrentSession()
				.setAttribute("user", modelService.<Object>toPersistenceLayer(userService.getAdminUser()));

		i18nService.setCurrentLocale(Locale.ENGLISH);

		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("EUR"));

	}


	/**
	 *
	 */
	private void simulateRegistrationFacade()
	{
		final CustomerModel bogusCustomer = modelService.create(CustomerModel.class);
		bogusCustomer.setUid(EMAIL);
		bogusCustomer.setName(NAME);
		bogusCustomer.setCustomerID(CUSTOMER_ID);
		modelService.save(bogusCustomer);

	}


	@After
	public void cleanUp()
	{
		System.out.println("Rollback");
		tx.rollback();
	}

	/**
	 * @throws ImpExException
	 */
	private static void loadB2bRegistrationTestData() throws ImpExException
	{
		importCsv("/b2bacceleratorservices/test/testCatalog.csv", "UTF-8");
		importCsv("/b2bacceleratorservices/test/testdata.csv", "UTF-8");
		importCsv("/impex/essentialdata-b2bacceleratorservices-usergroups.impex", "UTF-8");
		importCsv("/impex/essentialdata-b2bacceleratorservices-workflow.impex", "UTF-8");

		importCsv("/impex/projectdata-b2bacceleratorservices-registrationapprovedemailprocess.impex", "UTF-8");
		importCsv("/impex/projectdata-b2bacceleratorservices-registrationpendingapprovalemailprocess.impex", "UTF-8");
		importCsv("/impex/projectdata-b2bacceleratorservices-registrationreceivedemailprocess.impex", "UTF-8");
		importCsv("/impex/projectdata-b2bacceleratorservices-registrationrejectedemailprocess.impex", "UTF-8");
	}

	@Test
	public void shouldStartApprovalProcessAndAssertApprovalFromMerchant() throws Exception
	{

		final WorkflowModel workflow = startAprovalProcess();

		final Collection<WorkflowActionModel> workflowActions = getStartWorkflowActions(workflow);
		WorkflowActionModel workflowAction = workflowActions.iterator().next();

		// Send Registration Pending Approval Action

		workflowAction = executeAction(workflowAction);


		workflowAction = executeAction(workflowAction);

		final WorkflowDecisionModel decisionToTest = getDecisionToTest(workflowAction, REGISTRATION_APPROVED_DECISION_CODE);
		Assert.assertNotNull(decisionToTest);


		// Take a decision that we 'APPROVE' the user
		workflowAction.setSelectedDecision(decisionToTest);


		//Add test b2b unit to the attachment
		final B2BRegistrationModel b2bRegistrationAttachment = (B2BRegistrationModel) workflowAction.getAttachmentItems().get(0);

		final B2BUnitModel testUnit = modelService.create(B2BUnitModel.class);
		testUnit.setUid(b2bRegistrationAttachment.getCompanyName());
		testUnit.setName(b2bRegistrationAttachment.getCompanyName());
		modelService.save(testUnit);
		b2bRegistrationAttachment.setDefaultB2BUnit(testUnit);

		//End current action and execute the next one
		workflowActionService.complete(workflowAction);
		workflowAction = decisionToTest.getToActions().iterator().next();

		// Approval action to create the user
		workflowAction = executeAction(workflowAction);

		// Send Request Approved Email 
		workflowAction = executeAction(workflowAction);
		//waitForProcess(REGISTRATION_APPROVED_EMAIL_PROCESS_CODE, 20000);

		// Ending Workflow
		workflowProcessingService.endWorkflow(workflow);

		Assert.assertTrue(userService.getUserForUID(EMAIL) instanceof B2BCustomerModel);
		Assert.assertTrue(userService.getUserForUID(EMAIL).getGroups().contains(b2bRegistrationAttachment.getDefaultB2BUnit()));
		Assert.assertEquals(CUSTOMER_ID, ((B2BCustomerModel) userService.getUserForUID(EMAIL)).getCustomerID());
	}



	@Test
	public void shouldStartApprovalProcessAndAssertRejectionFromMerchant() throws Exception
	{

		final WorkflowModel workflow = startAprovalProcess();
		final Collection<WorkflowActionModel> workflowActions = getStartWorkflowActions(workflow);

		// Send Registration Pending Approval Action
		WorkflowActionModel workflowAction = workflowActions.iterator().next();

		//		modelService.remove(businessProcessService.getProcess(REGISTRATION_PENDING_APPROVAL_EMAIL_PROCESS_CODE));
		workflowAction = executeAction(workflowAction);


		//		modelService.remove(businessProcessService.getProcess(REGISTRATION_RECEIVED_EMAIL_PROCESS_CODE));
		workflowAction = executeAction(workflowAction);

		final WorkflowDecisionModel decisionToTest = getDecisionToTest(workflowAction, REGISTRATION_REJECTED_DECISION_CODE);
		Assert.assertNotNull(decisionToTest);


		// Take a decision that we 'APPROVE' the user
		workflowAction.setSelectedDecision(decisionToTest);

		//Add test b2b unit to the attachment
		final B2BRegistrationModel b2bRegistrationAttachment = (B2BRegistrationModel) workflowAction.getAttachmentItems().get(0);


		b2bRegistrationAttachment.setRejectReason("You are not wanted");

		//End current action and execute the next one
		workflowActionService.complete(workflowAction);
		workflowAction = decisionToTest.getToActions().iterator().next();


		// Approval action to reject the user
		workflowAction = executeAction(workflowAction);


		// Send Request Rejected Email 
		workflowAction = executeAction(workflowAction);
		//waitForProcess(REGISTRATION_APPROVED_EMAIL_PROCESS_CODE, 20000);

		// Ending Workflow
		workflowProcessingService.endWorkflow(workflow);
		//Make sure a B2BCustomer was not created for the test UID
		Assert.assertFalse(userService.getUserForUID(EMAIL) instanceof B2BCustomerModel);
	}

	public Collection<WorkflowActionModel> getStartWorkflowActions(final WorkflowModel workflow)
	{
		return getWorkflowActionService().getStartWorkflowActions(workflow);
	}

	/**
	 * @param workflowAction
	 * @return
	 */
	private WorkflowDecisionModel getDecisionToTest(final WorkflowActionModel workflowAction, final String decisionCode)
	{
		WorkflowDecisionModel decisionToTest;
		final Iterator<WorkflowDecisionModel> decisions = workflowAction.getDecisions().iterator();
		do
		{
			decisionToTest = decisions.next();
		}
		while (!decisionToTest.getCode().equals(decisionCode));

		return decisionToTest;
	}

	protected WorkflowActionService getWorkflowActionService()
	{
		return workflowActionService;
	}

	protected B2BRegistrationModel getB2BRegistrationModel()
	{

		final B2BRegistrationModel registrationModel = modelService.create(B2BRegistrationModel.class);

		registrationModel.setBaseStore(baseStoreService.getAllBaseStores().iterator().next());
		registrationModel.setBaseSite(baseSiteService.getAllBaseSites().iterator().next());
		registrationModel.setCompanyAddressCity("Some City");
		registrationModel.setCompanyAddressCountry(commonI18NService.getCountry("US"));
		registrationModel.setCompanyAddressPostalCode("90210");
		registrationModel.setCompanyAddressStreet("123 Some Street");
		registrationModel.setCompanyName("My Company Inc.");
		registrationModel.setCurrency(commonI18NService.getCurrentCurrency());
		registrationModel.setEmail(EMAIL);
		registrationModel.setLanguage(commonI18NService.getCurrentLanguage());
		registrationModel.setMessage("I have nothing to say");
		registrationModel.setName(NAME);
		registrationModel.setPosition("Ruler of the world");
		registrationModel.setTelephone("123-123-1234");
		registrationModel.setTitle(userService.getAllTitles().iterator().next());

		modelService.save(registrationModel);

		return registrationModel;

	}

	private WorkflowModel startAprovalProcess()
	{
		final WorkflowTemplateModel workflowTemplate = workflowTemplateService
				.getWorkflowTemplateForCode(B2BConstants.Workflows.REGISTRATION_WORKFLOW);

		final WorkflowModel workflow = newestWorkflowService
				.createWorkflow(workflowTemplate, getB2BRegistrationModel(), userService.getAdminUser());
		modelService.save(workflow);

		return workflow;

	}

	private WorkflowActionModel executeAction(final WorkflowActionModel workflowActionModel)
	{

		workflowProcessingService.activate(workflowActionModel);

		final WorkflowDecisionModel workflowDecisionModel = workflowActionModel.getSelectedDecision();

		for (final WorkflowActionModel action : workflowDecisionModel.getToActions())
		{
			return action;
		}

		return null;

	}
}
