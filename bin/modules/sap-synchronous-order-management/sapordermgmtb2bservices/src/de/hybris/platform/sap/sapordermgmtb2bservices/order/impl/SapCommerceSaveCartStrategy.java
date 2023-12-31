/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtb2bservices.order.impl;

import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceSaveCartStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.store.services.BaseStoreService;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class SapCommerceSaveCartStrategy extends DefaultCommerceSaveCartStrategy
{

	private BaseStoreService baseStoreService;


	@Override
	public CommerceSaveCartResult saveCart(final CommerceSaveCartParameter parameters) throws CommerceSaveCartException
	{

		if (isSyncOrdermgmtEnabled())
		{
			return new CommerceSaveCartResult();
		}

		return super.saveCart(parameters);

	}

	protected boolean isSyncOrdermgmtEnabled()
	{
		return (getBaseStoreService().getCurrentBaseStore().getSAPConfiguration() != null)
				&& (getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().isSapordermgmt_enabled());


	}

	/**
	 * @return the baseStoreService
	 */
	@Override
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Override
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

}
