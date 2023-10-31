/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.jalo;

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;



/**
 * Jalo Manager class for <code>sapcoreconfigruntimecps</code> extension.
 */
public class SapproductconfigruntimecpsManager extends GeneratedSapproductconfigruntimecpsManager
{

	/**
	 * Get the valid instance of this manager.
	 *
	 * @return the current instance of this manager
	 */
	public static SapproductconfigruntimecpsManager getInstance()
	{
		return (SapproductconfigruntimecpsManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(SapproductconfigruntimecpsConstants.EXTENSIONNAME);
	}

}
