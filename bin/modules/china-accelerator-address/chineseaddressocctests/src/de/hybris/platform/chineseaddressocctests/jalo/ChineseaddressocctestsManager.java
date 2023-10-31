/*
 *  
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseaddressocctests.jalo;

import de.hybris.platform.chineseaddressocctests.constants.ChineseaddressocctestsConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class ChineseaddressocctestsManager extends GeneratedChineseaddressocctestsManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( ChineseaddressocctestsManager.class.getName() );
	
	public static final ChineseaddressocctestsManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (ChineseaddressocctestsManager) em.getExtension(ChineseaddressocctestsConstants.EXTENSIONNAME);
	}
	
}
