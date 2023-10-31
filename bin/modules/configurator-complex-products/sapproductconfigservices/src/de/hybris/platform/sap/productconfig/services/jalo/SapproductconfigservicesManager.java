/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.productconfig.services.constants.SapproductconfigservicesConstants;


/**
 * Jalo Manager class for <code>sapproductconfigservices</code> extension.
 */
public class SapproductconfigservicesManager extends GeneratedSapproductconfigservicesManager
{

	/**
	 * factory-method for this class
	 *
	 * @return manager instance
	 */
	public static final SapproductconfigservicesManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapproductconfigservicesManager) em.getExtension(SapproductconfigservicesConstants.EXTENSIONNAME);
	}

}
