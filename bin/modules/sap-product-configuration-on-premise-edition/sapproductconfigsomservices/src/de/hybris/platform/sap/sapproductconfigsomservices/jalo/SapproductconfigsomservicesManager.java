/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductconfigsomservices.jalo;

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.sapproductconfigsomservices.constants.SapproductconfigsomservicesConstants;



/**
 * This is the extension manager of the Sapproductconfigsomservices extension.
 */
public class SapproductconfigsomservicesManager extends GeneratedSapproductconfigsomservicesManager
{
	/**
	 * Get the valid instance of this manager.
	 *
	 * @return the current instance of this manager
	 */
	public static SapproductconfigsomservicesManager getInstance()
	{
		return (SapproductconfigsomservicesManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(SapproductconfigsomservicesConstants.EXTENSIONNAME);
	}


}
