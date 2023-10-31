/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigaddonConstants;

import org.apache.log4j.Logger;


/**
 * YsapproductconfigaddonManager
 */
public class YsapproductconfigaddonManager extends GeneratedYsapproductconfigaddonManager
{
	private static final Logger LOG = Logger.getLogger(YsapproductconfigaddonManager.class.getName());

	/**
	 * Never call the constructor of any manager directly, call getInstance() You can place your business logic here -
	 * like registering a jalo session listener. Each manager is created once for each tenant.
	 */
	public YsapproductconfigaddonManager()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("constructor of ysapproductconfigaddonManager called.");
		}
	}

	/**
	 * @return YsapproductconfigaddonManager
	 */
	public static final YsapproductconfigaddonManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (YsapproductconfigaddonManager) em.getExtension(SapproductconfigaddonConstants.EXTENSIONNAME);
	}
}
