/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.config.IntegrationServicesConfiguration;
import de.hybris.platform.servicelayer.config.ConfigurationService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4OMPassportServiceTest {
	
	@InjectMocks
	private DefaultSapS4OMPassportService service = new DefaultSapS4OMPassportService();
	
	@Mock
	private IntegrationServicesConfiguration configuration;
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ConfigurationService configurationService;
	
	
	

	@Test
	public void testOnGenerate() {
		 String action = "send";
		 String property ="passport version";
	     when(configuration.getSapPassportSystemId()).thenReturn("hybris");
	     when(configuration.getSapPassportServiceValue()).thenReturn(11);
	     when(configuration.getSapPassportUser()).thenReturn("39");
	     when(configurationService.getConfiguration().getInt(property)).thenReturn(3);
	     String result=service.generate(action);
	     assertNotNull(result);
		
	}

}
