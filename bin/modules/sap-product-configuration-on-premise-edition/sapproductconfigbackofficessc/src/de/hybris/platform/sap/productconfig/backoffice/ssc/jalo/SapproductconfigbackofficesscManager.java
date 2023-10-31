/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.backoffice.ssc.jalo;

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.productconfig.backoffice.ssc.constants.SapproductconfigbackofficesscConstants;


/**
 * Jalo Manager class for <code>sapcoreconfigurationbackofficessc</code> extension.
 */
public class SapproductconfigbackofficesscManager extends GeneratedSapproductconfigbackofficesscManager
{

	/**
	 * Get the valid instance of this manager.
	 *
	 * @return the current instance of this manager
	 */
	public static SapproductconfigbackofficesscManager getInstance()
	{
		return (SapproductconfigbackofficesscManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(SapproductconfigbackofficesscConstants.EXTENSIONNAME);
	}

}
