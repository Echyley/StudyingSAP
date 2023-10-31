/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.pricing.bol.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.productconfig.pricing.bol.constants.SapproductconfigpricingbolConstants;





/**
 * Jalo Manager class for <code>Sapproductconfigpricingbol</code> extension.
 */
public class SapproductconfigpricingbolManager extends GeneratedSapproductconfigpricingbolManager
{

	/**
	 * factory-method for this class
	 *
	 * @return manager instance
	 */
	public static final SapproductconfigpricingbolManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapproductconfigpricingbolManager) em.getExtension(SapproductconfigpricingbolConstants.EXTENSIONNAME);
	}

}
