/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutUtils;

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.cxml.CXML;
import org.cxml.Header;
import org.cxml.Request;
import org.cxml.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;


/**
 * Unit test for {@link CXMLBuilder}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CXMLBuilderTest
{
	private final CXMLBuilder builder = CXMLBuilder.newInstance();

	@Test
	public void shouldCreateCXMLBuilder()
	{
		final CXML cxml = builder.create();
		assertNotNull(cxml);
	}

	@Test
	public void shouldCreateValidCXML()
	{
		final CXML cxml = builder.create();
		assertNotNull(cxml);
	}

	@Test
	public void shouldCreateValidRootElemet()
	{
		final CXML root = builder.create();
		assertTrue(StringUtils.isNotBlank(root.getPayloadID()));
		assertTrue(StringUtils.isNotBlank(root.getTimestamp()));
	}

	@Test
	public void shouldUnmarshallFromStringXML() throws FileNotFoundException
	{
		final CXML cxml = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequest.xml");

		final List<Object> kids = cxml.getHeaderOrMessageOrRequestOrResponse();
		assertNotNull(kids.get(0));
		assertNotNull(kids.get(1));

		assertTrue((kids.get(0) instanceof Header));
		assertTrue((kids.get(1) instanceof Request));
	}

	/**
	 * Tests that supplying both response code and message are part of the {@link Response} part of the {@link CXML}
	 * instance.
	 */
	@Test
	public void testStatusCodeAndMessage()
	{
		final CXML cxml = builder.withResponseCode("100").withResponseMessage("hello").create();

		final Response response = (Response) cxml.getHeaderOrMessageOrRequestOrResponse().get(0);
		assertNotNull(response);
		assertEquals("100", response.getStatus().getCode());
		assertEquals("hello", response.getStatus().getText());
	}

	/**
	 * Tests that when a new builder is configured with response code only the result contains a {@link Response} object
	 * with the correct data.
	 */
	@Test
	public void testWhenOnlyStatusCode()
	{
		final CXML cxml = builder.withResponseCode(HttpStatus.OK).create();

		assertNotNull("Response CXML cannot be null", cxml);

		final List<Object> headerOrMessageOrRequestOrResponse = cxml.getHeaderOrMessageOrRequestOrResponse();
		assertEquals("Expecting the result CXML to only contain a response instance", 1, headerOrMessageOrRequestOrResponse.size());

		final Response responseData = (Response) headerOrMessageOrRequestOrResponse.get(0);
		assertEquals("200", responseData.getStatus().getCode());
	}
}
