/*
 *  
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutaddon.jalo;

import de.hybris.platform.b2bpunchoutaddon.constants.B2bpunchoutaddonConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class B2bpunchoutaddonManager extends GeneratedB2bpunchoutaddonManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( B2bpunchoutaddonManager.class.getName() );
	
	public static final B2bpunchoutaddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (B2bpunchoutaddonManager) em.getExtension(B2bpunchoutaddonConstants.EXTENSIONNAME);
	}
	
}
