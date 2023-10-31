/*
 *  
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.addons.xyformssamples.jalo;

import de.hybris.platform.addons.xyformssamples.constants.XyformssamplesConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class XyformssamplesManager extends GeneratedXyformssamplesManager
{
	private static final Logger LOG = Logger.getLogger( XyformssamplesManager.class.getName() );
	
	public static final XyformssamplesManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (XyformssamplesManager) em.getExtension(XyformssamplesConstants.EXTENSIONNAME);
	}
	
}
