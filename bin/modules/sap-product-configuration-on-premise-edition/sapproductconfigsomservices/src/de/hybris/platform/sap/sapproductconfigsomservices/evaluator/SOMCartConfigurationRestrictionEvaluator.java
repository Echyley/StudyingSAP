/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductconfigsomservices.evaluator;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.sap.productconfig.services.evaluator.CMSCartConfigurationRestrictionEvaluator;
import de.hybris.platform.sap.productconfig.services.model.CMSCartConfigurationRestrictionModel;
import de.hybris.platform.store.services.BaseStoreService;


/**
 * Evaluator: Configuration specific UI active only if SOM is not active. (Because if SOM is active, it will provide a
 * cart UI)
 */
public class SOMCartConfigurationRestrictionEvaluator extends CMSCartConfigurationRestrictionEvaluator
{
	private BaseStoreService baseStoreService;

	@Override
	public boolean evaluate(final CMSCartConfigurationRestrictionModel arg0, final RestrictionData arg1)
	{

		return !isSapOrderMgmtEnabled();
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * Check if synchronous order management SOM is active
	 *
	 * @return true is SOM is active
	 */
	protected boolean isSapOrderMgmtEnabled()
	{
		return getBaseStoreService().getCurrentBaseStore().getSAPConfiguration() != null
				&& getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().isSapordermgmt_enabled();
	}
}
