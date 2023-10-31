/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtcfgfacades.jalo;

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.sapordermgmtcfgfacades.constants.SapproductconfigsomfacadesConstants;



/**
 * This is the extension manager of the Sapproductconfigsomfacades extension.
 */
public class SapproductconfigsomfacadesManager extends GeneratedSapproductconfigsomfacadesManager
{

	/**
	 * Get the valid instance of this manager.
	 *
	 * @return the current instance of this manager
	 */
	public static SapproductconfigsomfacadesManager getInstance()
	{
		return (SapproductconfigsomfacadesManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(SapproductconfigsomfacadesConstants.EXTENSIONNAME);
	}

}
