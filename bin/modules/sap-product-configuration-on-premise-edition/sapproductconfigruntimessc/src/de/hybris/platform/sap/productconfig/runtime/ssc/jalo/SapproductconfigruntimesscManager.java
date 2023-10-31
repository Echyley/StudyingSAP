/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.productconfig.runtime.ssc.constants.SapproductconfigruntimesscConstants;


/**
 * Jalo Manager class for <code>sapproductconfigruntimessc</code> extension.
 */
public class SapproductconfigruntimesscManager extends GeneratedSapproductconfigruntimesscManager
{

	/**
	 * factory-method for this class
	 *
	 * @return manager instance
	 */
	public static final SapproductconfigruntimesscManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapproductconfigruntimesscManager) em.getExtension(SapproductconfigruntimesscConstants.EXTENSIONNAME);
	}

}
