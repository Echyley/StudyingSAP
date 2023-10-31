/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductconfigsombol.jalo;

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.sapproductconfigsombol.constants.SapproductconfigsombolConstants;



/**
 * This is the extension manager of the Sapproductconfigsombol extension.
 */
public class SapproductconfigsombolManager extends GeneratedSapproductconfigsombolManager
{
	/**
	 * Get the valid instance of this manager.
	 *
	 * @return the current instance of this manager
	 */
	public static SapproductconfigsombolManager getInstance()
	{
		return (SapproductconfigsombolManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(SapproductconfigsombolConstants.EXTENSIONNAME);
	}

}
