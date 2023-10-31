/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.b2bfrontend.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.productconfig.b2bfrontend.constants.Sapproductconfigb2baddonConstants;


/**
 * Jalo Manager class for <code>sapproductconfigb2baddon</code> extension.
 */
public class Sapproductconfigb2baddonManager extends GeneratedSapproductconfigb2baddonManager
{

	/**
	 * factory-method for this class
	 *
	 * @return manager instance
	 */
	public static final Sapproductconfigb2baddonManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (Sapproductconfigb2baddonManager) em.getExtension(Sapproductconfigb2baddonConstants.EXTENSIONNAME);
	}

}
