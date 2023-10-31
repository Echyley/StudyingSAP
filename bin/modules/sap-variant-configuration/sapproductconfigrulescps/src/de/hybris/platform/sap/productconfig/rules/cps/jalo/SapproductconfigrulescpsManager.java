/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.cps.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.productconfig.rules.cps.constants.SapproductconfigrulescpsConstants;



/**
 * Jalo Manager class for <code>SapproductconfigrulescpsManager</code> extension.
 */
public class SapproductconfigrulescpsManager extends GeneratedSapproductconfigrulescpsManager
{

	/**
	 * factory-method for this class
	 *
	 * @return manager instance
	 */
	public static final SapproductconfigrulescpsManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapproductconfigrulescpsManager) em.getExtension(SapproductconfigrulescpsConstants.EXTENSIONNAME);
	}

}
