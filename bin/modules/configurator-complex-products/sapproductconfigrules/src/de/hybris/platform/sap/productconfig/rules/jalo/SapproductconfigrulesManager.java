/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.productconfig.rules.constants.SapproductconfigrulesConstants;


/**
 * Jalo Manager class for <code>sapproductconfigrules</code> extension.
 */
public class SapproductconfigrulesManager extends GeneratedSapproductconfigrulesManager
{

	/**
	 * factory-method for this class
	 *
	 * @return manager instance
	 */
	public static final SapproductconfigrulesManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapproductconfigrulesManager) em.getExtension(SapproductconfigrulesConstants.EXTENSIONNAME);
	}

}
