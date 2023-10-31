package com.hybris.ybackoffice.jalo;

import com.hybris.ybackoffice.constants.YBackofficeConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class YBackofficeManager extends GeneratedYBackofficeManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( YBackofficeManager.class.getName() );
	
	public static final YBackofficeManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (YBackofficeManager) em.getExtension(YBackofficeConstants.EXTENSIONNAME);
	}
	
}
