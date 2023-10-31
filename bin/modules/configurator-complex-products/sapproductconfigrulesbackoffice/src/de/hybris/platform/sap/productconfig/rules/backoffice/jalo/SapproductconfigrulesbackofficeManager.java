/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.backoffice.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;


/**
 * Jalo Manager class for <code>sapproductconfigrulesbackoffice</code> extension.
 */
public class SapproductconfigrulesbackofficeManager extends GeneratedSapproductconfigrulesbackofficeManager
{

	/**
	 * factory-method for this class
	 *
	 * @return manager instance
	 */
	public static final SapproductconfigrulesbackofficeManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapproductconfigrulesbackofficeManager) em.getExtension(SapproductconfigrulesbackofficeConstants.EXTENSIONNAME);
	}

}
