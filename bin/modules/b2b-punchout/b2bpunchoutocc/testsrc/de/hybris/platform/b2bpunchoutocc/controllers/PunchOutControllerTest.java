/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocc.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.security.PunchOutUserAuthenticationStrategy;
import de.hybris.platform.b2b.punchout.services.PunchOutService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.cxml.CXML;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutControllerTest
{
	private static final String IDENTITY = "abc_test";

	@InjectMocks
	private PunchOutController punchOutController;

	@Mock
	private PunchOutService punchOutService;
	@Mock
	private PunchOutUserAuthenticationStrategy punchOutUserAuthenticationStrategy;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private SessionService sessionService;
	@Mock
	private Map<String, String> occSupportedTransactionURLPaths;
	@Spy
	private CXML requestXML;

	@Test
	public void testCreatePunchOutProfileRequest()
	{
		Configuration config = mock(Configuration.class);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getString("ccv2.services.api.url.0")).thenReturn("");
		when(config.getString(eq("webservicescommons.required.channel"), anyString())).thenReturn("https");
		when(config.getString(eq("ext.b2bpunchoutocc.extension.webmodule.webroot"), anyString())).thenReturn("/occ/v2");

		when(occSupportedTransactionURLPaths.entrySet()).thenReturn(new HashSet<>());

		punchOutController.createPunchOutProfileRequest(requestXML, "host", "baseSiteId");

		verify(occSupportedTransactionURLPaths).entrySet();
		verify(configurationService, times(3)).getConfiguration();
		verify(punchOutService).processProfileRequest(requestXML);
		verify(config).getString("ccv2.services.api.url.0");
		verify(config).getString("webservicescommons.required.channel", "https");
		verify(sessionService).setAttribute(eq(PunchOutUtils.SUPPORTED_TRANSACTION_URL_PATHS), any());
	}

	@Test
	public void testCreatePunchOutPurchaseOrderRequest() throws IOException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/sampleOrderRequest.xml");

		when(punchOutService.retrieveIdentity(any())).thenReturn(IDENTITY);
		doNothing().when(punchOutUserAuthenticationStrategy).authenticate(any(), any(), any());
		when(punchOutService.processPurchaseOrderRequest(any())).thenReturn(requestXML);

		final CXML responseXML = punchOutController.createPunchOutPurchaseOrderRequest(requestXML, null, null);

		assertThat(responseXML).isNotNull();
		verify(punchOutService).retrieveIdentity(requestXML);
		verify(punchOutUserAuthenticationStrategy).authenticate(IDENTITY, null, null);
		verify(punchOutService).processPurchaseOrderRequest(requestXML);
	}

	@Test
	public void testCreatePunchOutSetUpRequest()
	{
		doReturn(new CXML()).when(punchOutService).processPunchOutSetUpRequest(requestXML);

		final CXML response = punchOutController.createPunchOutSetUpRequest(requestXML);

		assertThat(response).isNotNull();
		verify(punchOutService).processPunchOutSetUpRequest(requestXML);
	}
}
