package com.sap.hybris.ysaprcomsfulfillment.jalo;

import com.sap.hybris.ysaprcomsfulfillment.constants.YsaprcomsfulfillmentConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class YsaprcomsfulfillmentManager extends GeneratedYsaprcomsfulfillmentManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( YsaprcomsfulfillmentManager.class.getName() );
	
	public static final YsaprcomsfulfillmentManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (YsaprcomsfulfillmentManager) em.getExtension(YsaprcomsfulfillmentConstants.EXTENSIONNAME);
	}
	
}
