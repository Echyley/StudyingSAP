/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4c.customer.service.impl;


import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.c4c.customer.dto.C4CCustomerData;
import com.sap.hybris.scpiconnector.data.ResponseData;
import com.sap.hybris.c4c.customer.constants.Sapc4ccustomerb2cConstants;
import com.sap.hybris.scpiconnector.httpconnection.CloudPlatformIntegrationConnection;

/**
 *
 */
@UnitTest
public class DefaultSapC4cCustomerPublicationServiceTest
{

	@InjectMocks
	private final DefaultSapC4cCustomerPublicationService sapC4cCustomerPublicationService = new DefaultSapC4cCustomerPublicationService();

	@Mock
	private CloudPlatformIntegrationConnection cloudPlatformIntegrationConnection;

	@Mock
	private ConfigurationService configurationService;

	private static final String DUMMY_TEXT = "dummy";

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPublishCustomerToCloudPlatformIntegration() throws IOException
	{
		final Configuration config = Mockito.mock(Configuration.class);
		final C4CCustomerData customerJson = Mockito.mock(C4CCustomerData.class);
		final ResponseData response = Mockito.mock(ResponseData.class);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getString(Mockito.eq(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_CPI_REPLICATE))).thenReturn("true");
		when(config.getString(Mockito.eq(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_SCPI_IFLOW_KEY))).thenReturn(DUMMY_TEXT);
		when(customerJson.toString()).thenReturn(DUMMY_TEXT);
		when(cloudPlatformIntegrationConnection.sendPost(Mockito.anyString(), Mockito.anyString())).thenReturn(response);

		sapC4cCustomerPublicationService.publishCustomerToCloudPlatformIntegration(customerJson);

		verify(cloudPlatformIntegrationConnection, times(1)).sendPost(Mockito.anyString(), Mockito.anyString());

	}
}
