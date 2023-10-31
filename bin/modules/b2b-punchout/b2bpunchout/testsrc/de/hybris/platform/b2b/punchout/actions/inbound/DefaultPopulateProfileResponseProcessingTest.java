/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.converters.Populator;

import java.io.FileNotFoundException;

import org.cxml.CXML;
import org.cxml.ProfileResponse;
import org.cxml.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPopulateProfileResponseProcessingTest
{
	@InjectMocks
	private DefaultPopulateProfileResponseProcessing populateProfileResponseProcessing;

	private CXML requestXML;
	private CXML responseXML;

	@Mock
	private Populator<CXML, ProfileResponse> profileResponsePopulator;

	@Before
	public void setUp() throws FileNotFoundException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/sampleProfileRequest.xml");
		responseXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/defaultSuccessResponse.xml");
	}

	@Test
	public void shouldFailBadRequest() throws FileNotFoundException, PunchOutException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/badProfileRequest.xml");

		assertThatThrownBy(() -> populateProfileResponseProcessing.process(requestXML, responseXML))
			.isInstanceOf(PunchOutException.class);
	}

	@Test
	public void shouldReturnResponseOK()
	{
		populateProfileResponseProcessing.process(requestXML, responseXML);

		assertThat(responseXML.getHeaderOrMessageOrRequestOrResponse()).isNotNull();

		final Response response = (Response) responseXML.getHeaderOrMessageOrRequestOrResponse().get(0);
		assertThat(response).isNotNull()
							.hasFieldOrPropertyWithValue("status.code", "200")
							.hasFieldOrPropertyWithValue("status.text", "OK");
	}
}
