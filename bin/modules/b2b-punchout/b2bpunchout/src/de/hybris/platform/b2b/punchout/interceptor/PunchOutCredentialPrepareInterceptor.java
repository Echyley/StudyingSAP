/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.interceptor;

import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.PersistenceOperation;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;


import org.joda.time.LocalDateTime;


public class PunchOutCredentialPrepareInterceptor implements PrepareInterceptor
{

	private ModelService modelService;

	public void onPrepare(final Object model, InterceptorContext ctx)
	{
		if (model instanceof PunchOutCredentialModel)
		{
			final PunchOutCredentialModel credentialModel = (PunchOutCredentialModel) model;

			if (isSharedSecretModified(credentialModel))
			{
				credentialModel.setSharedSecretModifiedTime(LocalDateTime.now().toDate());
				ctx.registerElementFor(credentialModel, PersistenceOperation.SAVE);
			}
		}

	}

	private boolean isSharedSecretModified(final PunchOutCredentialModel newModel)
	{
		if (newModel.getPk() == null)
		{
			return true;
		}
		else
		{
			final PunchOutCredentialModel lastModel = getModelService().get(newModel.getPk());
			if (newModel.getSharedsecret() == null)
			{
				return false;
			}
			return !newModel.getSharedsecret().equals(lastModel.getSharedsecret());
		}
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
