/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.b2b.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.productconfig.b2b.constants.Sapproductconfigb2bConstants;


/**
 * Jalo Manager class for <code>sapproductconfigb2b</code> extension.
 */
public class Sapproductconfigb2bManager extends GeneratedSapproductconfigb2bManager
{

	/**
	 * factory-method for this class
	 *
	 * @return manager instance
	 */
	public static final Sapproductconfigb2bManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (Sapproductconfigb2bManager) em.getExtension(Sapproductconfigb2bConstants.EXTENSIONNAME);
	}

}
