/*
 *  
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customercouponbackoffice.jalo;

import de.hybris.platform.customercouponbackoffice.constants.CustomercouponbackofficeConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class CustomercouponbackofficeManager extends GeneratedCustomercouponbackofficeManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( CustomercouponbackofficeManager.class.getName() );
	
	public static final CustomercouponbackofficeManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (CustomercouponbackofficeManager) em.getExtension(CustomercouponbackofficeConstants.EXTENSIONNAME);
	}
	
}
