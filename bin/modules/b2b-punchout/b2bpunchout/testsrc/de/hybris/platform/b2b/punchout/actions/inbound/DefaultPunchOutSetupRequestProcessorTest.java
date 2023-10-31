/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;

import java.io.FileNotFoundException;

import org.cxml.CXML;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutSetupRequestProcessorTest
{
	@InjectMocks
	private DefaultPunchOutSetupRequestProcessor punchOutSetupRequestProcessor;

	private CXML requestXML;

	@Mock
	private PunchOutSetupRequestCartProcessing punchOutSetupRequestCartProcessing;
	@Mock
	private PopulateSetupResponsePunchOutProcessing populateSetupResponsePunchOutProcessing;
	@Mock
	private PunchOutSessionService punchoutSessionService;

	@Before
	public void setUp() throws FileNotFoundException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequest.xml");
	}

	@Test
	public void testGenerateCxmlResponse()
	{
		final CXML responseXML = punchOutSetupRequestProcessor.generatecXML(requestXML);

		assertThat(responseXML).isNotNull();

		verify(punchoutSessionService).initAndActivatePunchOutSession(requestXML);
		verify(punchOutSetupRequestCartProcessing).processCartData(requestXML);
		verify(punchoutSessionService).saveCurrentPunchoutSession();
		verify(populateSetupResponsePunchOutProcessing).populateResponse(responseXML);
	}
}
