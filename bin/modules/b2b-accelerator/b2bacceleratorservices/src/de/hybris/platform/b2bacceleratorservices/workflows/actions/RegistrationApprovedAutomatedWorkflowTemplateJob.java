/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.workflows.actions;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BRegistrationModel;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import org.springframework.beans.factory.annotation.Required;



/**
 * Action called when a registration request has been approved
 */
public class RegistrationApprovedAutomatedWorkflowTemplateJob extends AbstractAutomatedWorkflowTemplateJob
{
	private CustomerEmailResolutionService defaultCustomerEmailResolutionService;
	protected CustomerEmailResolutionService getDefaultCustomerEmailResolutionService()
	{
		return defaultCustomerEmailResolutionService;
	}

	@Required
	public void setDefaultCustomerEmailResolutionService(final CustomerEmailResolutionService defaultCustomerEmailResolutionService)
	{
		this.defaultCustomerEmailResolutionService = defaultCustomerEmailResolutionService;
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.workflow.jobs.AutomatedWorkflowTemplateJob#perform(de.hybris.platform.workflow.model.
	 * WorkflowActionModel)
	 */
	@Override
	public WorkflowDecisionModel perform(final WorkflowActionModel workflowAction)
	{

		final B2BRegistrationModel registration = getRegistrationAttachment(workflowAction);
		final CustomerModel customer = getCustomer(registration);

		final B2BCustomerModel b2BCustomer = createB2BCustomerModel(customer, registration);

		//Delete temporary customer attached to workflow
		getModelService().remove(customer);
		registration.setCustomer(b2BCustomer);

		//persist the newly created b2bCustomer
		getModelService().saveAll(b2BCustomer, registration);

		return defaultDecision(workflowAction);

	}

	/**
	 * Creates an instance of {@link B2BCustomerModel} out of {@link CustomerModel}.
	 *
	 * @param customer CustomerModel data
	 * @return An instance of {@link B2BCustomerModel}
	 */

	protected B2BCustomerModel createB2BCustomerModel(final CustomerModel customer, final B2BRegistrationModel registration)
	{

		final B2BCustomerModel b2bCustomer = getModelService().create(B2BCustomerModel.class);

		b2bCustomer.setEmail(getDefaultCustomerEmailResolutionService().getEmailForCustomer(customer));
		b2bCustomer.setName(customer.getName());
		b2bCustomer.setTitle(customer.getTitle());
		b2bCustomer.setUid(customer.getUid());
		b2bCustomer.setCustomerID(customer.getCustomerID());
		b2bCustomer.setDefaultB2BUnit(registration.getDefaultB2BUnit());
		b2bCustomer.setSite(customer.getSite());

		return b2bCustomer;
	}
}
