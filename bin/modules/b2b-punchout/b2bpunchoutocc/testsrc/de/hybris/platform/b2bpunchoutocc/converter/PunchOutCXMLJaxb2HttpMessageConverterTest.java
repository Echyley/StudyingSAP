/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocc.converter;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.IOException;

import org.cxml.CXML;
import org.cxml.CXMLAttachment;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.http.MockHttpOutputMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@UnitTest
public class PunchOutCXMLJaxb2HttpMessageConverterTest
{
	private final PunchOutCXMLJaxb2HttpMessageConverter messageConverter = new PunchOutCXMLJaxb2HttpMessageConverter();
	private MockHttpOutputMessage outputMessage;

	@Before
	public void setUp()
	{
		outputMessage = new MockHttpOutputMessage();
	}

	@Test
	public void testWriteToResultForCXML() throws IOException
	{
		messageConverter.write(new CXML(),null, outputMessage);

		assertThat(outputMessage.getBodyAsString())
				.contains("<cXML/>");
	}

	@Test
	public void testWriteToResultForOtherObject() throws IOException
	{
		messageConverter.write(new CXMLAttachment(),null, outputMessage);

		assertThat(outputMessage.getBodyAsString())
				.contains("<cXMLAttachment/>");
	}

	@Test
	public void testWriteToResultThrowsHttpMessageNotWritableException()
	{
		assertThatThrownBy( () -> messageConverter.write("Provoke a MarshalException", null, outputMessage))
				.isExactlyInstanceOf(HttpMessageNotWritableException.class)
				.hasMessageStartingWith("Could not marshal [");
	}

	@Test
	public void testCanReadCXML()
	{
		assertThat(messageConverter.canRead(CXML.class, null))
				.isTrue();
	}

	@Test
	public void testCanNotReadOtherClass()
	{
		assertThat(messageConverter.canRead(String.class, null))
				.isFalse();
	}

	@Test
	public void testCanWriteCXML()
	{
		assertThat(messageConverter.canWrite(CXML.class, null))
				.isTrue();
	}

	@Test
	public void testCanNotWriteOtherClass()
	{
		assertThat(messageConverter.canWrite(Integer.class, null))
				.isFalse();
	}
}
