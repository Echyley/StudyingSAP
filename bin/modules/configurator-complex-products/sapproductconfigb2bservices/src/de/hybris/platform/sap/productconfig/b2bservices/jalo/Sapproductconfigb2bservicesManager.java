/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.b2bservices.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.productconfig.b2bservices.constants.Sapproductconfigb2bservicesConstants;


/**
 * Jalo Manager class for <code>sapproductconfigb2bservices</code> extension.
 */
public class Sapproductconfigb2bservicesManager extends GeneratedSapproductconfigb2bservicesManager
{

	/**
	 * factory-method for this class
	 *
	 * @return manager instance
	 */
	public static final Sapproductconfigb2bservicesManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (Sapproductconfigb2bservicesManager) em.getExtension(Sapproductconfigb2bservicesConstants.EXTENSIONNAME);
	}

}
