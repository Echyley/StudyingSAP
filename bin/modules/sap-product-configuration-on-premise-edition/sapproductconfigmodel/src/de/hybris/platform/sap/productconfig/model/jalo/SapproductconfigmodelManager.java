/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.model.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.productconfig.model.constants.SapproductconfigmodelConstants;


/**
 * Jalo Manager class for <code>sapproductconfigmodel</code> extension.
 */
public class SapproductconfigmodelManager extends GeneratedSapproductconfigmodelManager
{

	/**
	 * factory-method for this class
	 *
	 * @return manager instance
	 */
	public static final SapproductconfigmodelManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapproductconfigmodelManager) em.getExtension(SapproductconfigmodelConstants.EXTENSIONNAME);
	}

}
