/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.sappassport;

import org.apache.log4j.Logger;

import com.sap.conn.jco.ext.Environment;

import de.hybris.platform.sap.core.configuration.rfc.dao.SAPRFCDestinationDao;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.tenant.TenantService;


/**
 * Default implementation for the {@link DefaultRegisterClientPassportManager}.
 */
public class DefaultRegisterClientPassportManager
{
	private static final Logger LOG = Logger.getLogger(DefaultRegisterClientPassportManager.class.getName());

	private DefaultClientPassportManager clientPassportManager = null; //NOPMD

	private TenantService tenantService;

	private ConfigurationService configurationService;

	private SAPRFCDestinationDao rfcDestinationDao;

	public SAPRFCDestinationDao getRfcDestinationDao()
	{
		return rfcDestinationDao;
	}

	/**
	 * Injection setter for {@link SAPRFCDestinationDao}.
	 * 
	 * @param rfcDestinationDao
	 *                             {@link SAPRFCDestinationDao} to set
	 */
	public void setRfcDestinationDao(SAPRFCDestinationDao rfcDestinationDao)
	{
		this.rfcDestinationDao = rfcDestinationDao;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Injection setter for {@link ConfigurationService}.
	 * 
	 * @param configurationService
	 *                                {@link ConfigurationService} to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	private DefaultClientPassportManager getClientPassportManager()
	{
		return clientPassportManager;
	}

	/**
	 * Injection setter for {@link TenantService}.
	 * 
	 * @param tenantService
	 *                         {@link TenantService} to set
	 */
	public void setTenantService(final TenantService tenantService)
	{
		this.tenantService = tenantService;
	}

	/**
	 * Initialization method called by the Spring framework.
	 */
	public void init()
	{
		LOG.info("registering the ClientPassportManager in tenant = " + tenantService.getCurrentTenantId());
		if (!"junit".equals(tenantService.getCurrentTenantId()))
		{
			registerClientPassportManager();
		}
	}

	/**
	 * Destroy method called by the Spring framework.
	 */
	public void destroy()
	{
		LOG.info("unregistering the ClientPassportManager in tenant = " + tenantService.getCurrentTenantId());

		if (!"junit".equals(tenantService.getCurrentTenantId()))
		{
			unregisterClientPassportManager();
		}
	}

	/**
	 * Registers the Client Passport Manager.
	 * 
	 */
	private synchronized void registerClientPassportManager()
	{
		if (Environment.isClientPassportManagerRegistered())
		{
			LOG.warn("SAP ClientPassportManager already registered");
		}
		else
		{
			clientPassportManager = DefaultClientPassportManager.newClientPassportManager(getConfigurationService(),
					getRfcDestinationDao());
			com.sap.conn.jco.ext.Environment.registerClientPassportManager(getClientPassportManager());
		}
	}

	/**
	 * Unregisters the Client Passport Manager.
	 */
	private synchronized void unregisterClientPassportManager()
	{
		try
		{
			com.sap.conn.jco.ext.Environment.unregisterClientPassportManager(getClientPassportManager());
		}
		catch (final IllegalStateException e)
		{
			LOG.error("SAP ClientPassportManager exists in service, but is not registered", e);
		}
	}

}
