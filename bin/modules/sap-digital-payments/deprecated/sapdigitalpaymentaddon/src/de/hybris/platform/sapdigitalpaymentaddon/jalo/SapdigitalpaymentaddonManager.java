/*
 *  
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapdigitalpaymentaddon.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sapdigitalpaymentaddon.constants.SapdigitalpaymentaddonConstants;
import org.apache.log4j.Logger;

public class SapdigitalpaymentaddonManager extends GeneratedSapdigitalpaymentaddonManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( SapdigitalpaymentaddonManager.class.getName() );
	
	public static final SapdigitalpaymentaddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapdigitalpaymentaddonManager) em.getExtension(SapdigitalpaymentaddonConstants.EXTENSIONNAME);
	}
	
}
