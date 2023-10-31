/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundleservices.bundle.impl;

import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.hook.CommerceCloneSavedCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.servicelayer.model.ModelService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Bundle specific hook for cloning saved carts
 * @deprecated since 2105
 */
@Deprecated(since = "2105", forRemoval= true )
public class DefaultBundleCommerceCloneSavedCartMethodHook implements CommerceCloneSavedCartMethodHook
{

	private ModelService modelService;

	@Override
	public void beforeCloneSavedCart(final CommerceSaveCartParameter parameters) throws CommerceSaveCartException
	{
		return; //NOPMD
	}

	@Override
	public void afterCloneSavedCart(final CommerceSaveCartParameter parameters, final CommerceSaveCartResult cloneCartResult)
			throws CommerceSaveCartException
	{
		return; //NOPMD
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
