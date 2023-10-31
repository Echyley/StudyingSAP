/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutaddon.converter;

import de.hybris.bootstrap.annotations.UnitTest;

import org.cxml.CXML;
import org.cxml.CXMLAttachment;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@UnitTest
public class CXMLJaxb2MessageConverterTest
{
	private final CXMLJaxb2MessageConverter messageConverter = new CXMLJaxb2MessageConverter();
	private MockHttpOutputMessage outputMessage;

	@Before
	public void setUp()
	{
		outputMessage = new MockHttpOutputMessage();
	}

	@Test
	public void testWriteToResultForCXML() throws IOException
	{
		messageConverter.write(new CXML(), null, outputMessage);

		assertThat(outputMessage.getBodyAsString()).contains("<cXML/>");
	}

	@Test
	public void testWriteToResultForOtherObject() throws IOException
	{
		messageConverter.write(new CXMLAttachment(), null, outputMessage);

		assertThat(outputMessage.getBodyAsString()).contains("<cXMLAttachment/>");
	}

	@Test
	public void testWriteToResultThrowsHttpMessageNotWritableException()
	{
		assertThatThrownBy(() -> messageConverter.write("Provoke a MarshalException", null, outputMessage)).isExactlyInstanceOf(
				HttpMessageNotWritableException.class).hasMessageStartingWith("Could not marshal [");
	}
}
