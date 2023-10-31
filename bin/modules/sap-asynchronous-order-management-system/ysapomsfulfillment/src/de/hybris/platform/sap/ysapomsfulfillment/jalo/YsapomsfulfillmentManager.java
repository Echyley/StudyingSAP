/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapomsfulfillment.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.ysapomsfulfillment.constants.YsapomsfulfillmentConstants;
import org.apache.log4j.Logger;

public class YsapomsfulfillmentManager extends GeneratedYsapomsfulfillmentManager
{
	private static final Logger log = Logger.getLogger( YsapomsfulfillmentManager.class.getName() );
	
	public static final YsapomsfulfillmentManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (YsapomsfulfillmentManager) em.getExtension(YsapomsfulfillmentConstants.EXTENSIONNAME);
	}
	
}
