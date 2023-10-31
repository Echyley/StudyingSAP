/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.workflows.actions;

import de.hybris.platform.b2b.model.B2BRegistrationModel;
import de.hybris.platform.b2b.model.B2BRegistrationRejectedProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;



/**
 * Workflow action responsible for sending the "rejected" email
 */
public class SendRejectedEmailAutomatedWorkflowTemplateJob extends
		SendEmailAutomatedWorkflowTemplateJob<B2BRegistrationRejectedProcessModel>
{

	@Override
	protected B2BRegistrationRejectedProcessModel createProcessModel(final CustomerModel customerModel,
			final B2BRegistrationModel registrationModel)
	{
		final B2BRegistrationRejectedProcessModel process = super.createProcessModel(customerModel, registrationModel);
		process.setRejectReason(registrationModel.getRejectReason());
		return process;
	}

}
