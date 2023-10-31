/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4c.customer.service.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;

import com.sap.hybris.c4c.customer.constants.Sapc4ccustomerb2cConstants;
import com.sap.hybris.c4c.customer.dto.C4CCustomerData;
import com.sap.hybris.c4c.customer.service.SapC4cCustomerPublicationService;
import com.sap.hybris.scpiconnector.httpconnection.CloudPlatformIntegrationConnection;
import org.apache.commons.lang.StringUtils;


/**
 * Service for publishing Customer JSON to SCPI
 */
public class DefaultSapC4cCustomerPublicationService implements SapC4cCustomerPublicationService
{

	private CloudPlatformIntegrationConnection cloudPlatformIntegrationConnection;
	private ConfigurationService configurationService;

	/**
	 * Publishes Customer Data to SCPI
	 */
	@Override
	public void publishCustomerToCloudPlatformIntegration(final C4CCustomerData customerJson) throws IOException
	{
		String strReplicationEnabled = getConfigurationService().getConfiguration().getString(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_CPI_REPLICATE);
		boolean isReplicationEnabled = false;
		if(!StringUtils.isBlank(strReplicationEnabled)){
			isReplicationEnabled = Boolean.parseBoolean(strReplicationEnabled);
		}

		if(isReplicationEnabled) {
			getCloudPlatformIntegrationConnection().sendPost(
					getConfigurationService().getConfiguration().getString(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_SCPI_IFLOW_KEY),
					customerJson.toString());
		}
	}

	/**
	 * @return the cloudPlatformIntegrationConnection
	 */
	public CloudPlatformIntegrationConnection getCloudPlatformIntegrationConnection()
	{
		return cloudPlatformIntegrationConnection;
	}


	/**
	 * @param cloudPlatformIntegrationConnection
	 *           the cloudPlatformIntegrationConnection to set
	 */
	public void setCloudPlatformIntegrationConnection(final CloudPlatformIntegrationConnection cloudPlatformIntegrationConnection)
	{
		this.cloudPlatformIntegrationConnection = cloudPlatformIntegrationConnection;
	}


	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}


	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
