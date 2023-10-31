/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.jalo;

import com.sap.hybris.constants.SapreturnsbackofficeConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class SapreturnsbackofficeManager extends GeneratedSapreturnsbackofficeManager
{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger( SapreturnsbackofficeManager.class.getName() );
	
	public static final SapreturnsbackofficeManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapreturnsbackofficeManager) em.getExtension(SapreturnsbackofficeConstants.EXTENSIONNAME);
	}
	
}
