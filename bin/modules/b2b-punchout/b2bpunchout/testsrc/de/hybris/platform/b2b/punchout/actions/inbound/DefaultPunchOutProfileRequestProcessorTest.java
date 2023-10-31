/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutUtils;

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
public class DefaultPunchOutProfileRequestProcessorTest
{
	@InjectMocks
	private DefaultPunchOutProfileRequestProcessor punchOutProfileRequestProcessor;

	private CXML requestXML;

	@Mock
	private DefaultPopulateProfileResponseProcessing populateProfileResponseProcessing;

	@Before
	public void setUp() throws FileNotFoundException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/sampleProfileRequest.xml");
	}

	@Test
	public void testGenerateCxmlResponse()
	{
		final CXML responseXML = punchOutProfileRequestProcessor.generatecXML(requestXML);

		assertThat(responseXML).isNotNull();

		verify(populateProfileResponseProcessing).process(requestXML, responseXML);
	}
}
