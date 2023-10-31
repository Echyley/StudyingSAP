/*
 *  
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapdigitalpaymentb2baddon.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sapdigitalpaymentb2baddon.constants.Sapdigitalpaymentb2baddonConstants;
import org.apache.log4j.Logger;

public class Sapdigitalpaymentb2baddonManager extends GeneratedSapdigitalpaymentb2baddonManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( Sapdigitalpaymentb2baddonManager.class.getName() );
	
	public static final Sapdigitalpaymentb2baddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (Sapdigitalpaymentb2baddonManager) em.getExtension(Sapdigitalpaymentb2baddonConstants.EXTENSIONNAME);
	}
	
}
