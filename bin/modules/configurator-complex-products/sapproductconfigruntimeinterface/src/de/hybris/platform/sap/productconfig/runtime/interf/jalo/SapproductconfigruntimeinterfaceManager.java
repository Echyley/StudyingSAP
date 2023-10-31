/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.productconfig.runtime.interf.constants.SapproductconfigruntimeinterfaceConstants;


/**
 * Jalo Manager class for <code>sapproductconfigruntimeinterface</code> extension.
 */
public class SapproductconfigruntimeinterfaceManager extends GeneratedSapproductconfigruntimeinterfaceManager
{

	/**
	 * factory-method for this class
	 *
	 * @return manager instance
	 */
	public static final SapproductconfigruntimeinterfaceManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapproductconfigruntimeinterfaceManager) em.getExtension(SapproductconfigruntimeinterfaceConstants.EXTENSIONNAME);
	}

}
