/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.testdata.occ.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.productconfig.testdata.occ.constants.SapproductconfigtestdataoccConstants;




/**
 * This is the extension manager of the Sapproductconfigtestdataocc extension.
 */
public class SapproductconfigtestdataoccManager extends GeneratedSapproductconfigtestdataoccManager
{
	/**
	 * factory-method for this class
	 *
	 * @return manager instance
	 */
	public static final SapproductconfigtestdataoccManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapproductconfigtestdataoccManager) em.getExtension(SapproductconfigtestdataoccConstants.EXTENSIONNAME);
	}

}
