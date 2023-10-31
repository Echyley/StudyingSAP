/*
 *  
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.subscriptionservices.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.subscriptionservices.constants.SubscriptionservicesConstants;
import org.apache.log4j.Logger;

public class SubscriptionservicesManager extends GeneratedSubscriptionservicesManager
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( SubscriptionservicesManager.class.getName() );
	
	public static final SubscriptionservicesManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SubscriptionservicesManager) em.getExtension(SubscriptionservicesConstants.EXTENSIONNAME);
	}
	
}
