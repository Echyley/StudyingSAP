/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.client;

import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static rx.Observable.empty;
import static rx.Observable.error;
import static rx.Observable.just;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.charon.RawResponse;
import com.hybris.charon.exp.HttpException;


/**
 * Unit test for {@link DefaultCpqClientUtil}
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultCpqClientUtilTest
{

	private final DefaultCpqClientUtil classUnderTest = new DefaultCpqClientUtil();

	@Mock
	private RawResponse<Object> rawResponse;

	@Test
	public void testToReponse()
	{
		final Object content = new Object();
		assertSame(content, classUnderTest.toResponse(just(content)));
	}

	@Test(expected = HttpException.class)
	public void testToReponseHttpEx()
	{
		classUnderTest.toResponse(error(new HttpException(400, "error", just("detailed message"))));
	}

	@Test
	public void testExtractLogMessageNoServerMsg()
	{
		final String logMessage = classUnderTest.extractLogMessage(new HttpException(400, "error", null));
		assertEquals("CPQ responded with HTTP ERROR code 400 - error, detailed message: null", logMessage);
	}

	@Test
	public void testExtractLogMessageEmptyServerMsg()
	{
		final String logMessage = classUnderTest.extractLogMessage(new HttpException(400, "error", empty()));
		assertEquals("CPQ responded with HTTP ERROR code 400 - error, detailed message: null", logMessage);
	}

	@Test
	public void testExtractLogMessageWithServerMsg()
	{
		final String logMessage = classUnderTest.extractLogMessage(new HttpException(500, "error", just("detailed message")));
		assertEquals("CPQ responded with HTTP ERROR code 500 - error, detailed message: detailed message", logMessage);
	}

	@Test
	public void testCheckHTTPStatusCodeMatches()
	{
		given(rawResponse.getStatusCode()).willReturn(CpqClientConstants.HTTP_STATUS_OK);
		classUnderTest.checkHTTPStatusCode("TEST", CpqClientConstants.HTTP_STATUS_OK, rawResponse);
	}

	@Test(expected = IllegalStateException.class)
	public void testCheckHTTPStatusCodeFails()
	{
		given(rawResponse.getStatusCode()).willReturn(CpqClientConstants.HTTP_STATUS_NO_CONTENT);
		classUnderTest.checkHTTPStatusCode("TEST", CpqClientConstants.HTTP_STATUS_OK, rawResponse);
	}

	@Test
	public void testExtractErrorPage()
	{
		final String erroPage = "<html><head><body>error</body></head></html>";
		given(rawResponse.content()).willReturn(just(erroPage));
		assertEquals(erroPage, classUnderTest.extractErrorPage(rawResponse));
	}

	@Test
	public void testExtractErrorPageFails()
	{
		given(rawResponse.content()).willReturn(error(new IllegalArgumentException()));
		assertTrue(classUnderTest.extractErrorPage(rawResponse).contains("io.netty.handler.logging.LoggingHandler"));
	}


	@Test
	public void testIsErrorPageResponseTrue()
	{
		given(rawResponse.contentLength()).willReturn(of(250));
		given(rawResponse.getStatusCode()).willReturn(CpqClientConstants.HTTP_STATUS_OK);

		final boolean isErrorPage = classUnderTest.isErrorPageResponse(rawResponse);
		assertTrue("response should be considered as error page", isErrorPage);
	}

	@Test
	public void testIsErrorPageResponseWrongResponseCode()
	{
		given(rawResponse.getStatusCode()).willReturn(CpqClientConstants.HTTP_STATUS_NO_CONTENT);
		final boolean isErrorPage = classUnderTest.isErrorPageResponse(rawResponse);
		assertFalse("wrong response code, should not be considered as error page", isErrorPage);
	}

	@Test
	public void testIsErrorPageResponseNoContent()
	{
		given(rawResponse.contentLength()).willReturn(of(0));
		given(rawResponse.getStatusCode()).willReturn(CpqClientConstants.HTTP_STATUS_OK);
		final boolean isErrorPage = classUnderTest.isErrorPageResponse(rawResponse);
		assertFalse("no content, should not be considered as error page", isErrorPage);
	}

	@Test
	public void testIsErrorPageResponseNoContentLengthHeader()
	{
		given(rawResponse.contentLength()).willReturn(Optional.empty());
		given(rawResponse.getStatusCode()).willReturn(CpqClientConstants.HTTP_STATUS_OK);
		final boolean isErrorPage = classUnderTest.isErrorPageResponse(rawResponse);
		assertFalse("no content, should not be considered as error page", isErrorPage);
	}

	@Test
	public void testCheckContentTypeMatches()
	{
		given(rawResponse.contentLength()).willReturn(of(100));
		given(rawResponse.contentType()).willReturn(of(CpqClientConstants.HTTP_PRODUCE_APPL_JSON));
		classUnderTest.checkContentType("TEST", CpqClientConstants.HTTP_PRODUCE_APPL_JSON, rawResponse);
	}

	@Test
	public void testCheckContentTypeMatchesNotExactly()
	{
		given(rawResponse.contentLength()).willReturn(of(100));
		given(rawResponse.contentType()).willReturn(of(CpqClientConstants.HTTP_PRODUCE_APPL_JSON + "; charset=UTF-8"));
		classUnderTest.checkContentType("TEST", CpqClientConstants.HTTP_PRODUCE_APPL_JSON, rawResponse);
	}


	@Test
	public void testCheckContentTypeMatchesForNoContent()
	{
		given(rawResponse.contentType()).willReturn(Optional.empty());
		given(rawResponse.contentLength()).willReturn(Optional.empty());
		given(rawResponse.contentType()).willReturn(of(CpqClientConstants.NO_CONTENT_TYPE_PROVIDED));
		classUnderTest.checkContentType("TEST", CpqClientConstants.NO_CONTENT_TYPE_PROVIDED, rawResponse);
	}


	@Test(expected = IllegalStateException.class)
	public void testCheckContentTypeFails()
	{
		given(rawResponse.contentType()).willReturn(Optional.empty());
		given(rawResponse.contentLength()).willReturn(of(100));
		classUnderTest.checkContentType("TEST", CpqClientConstants.HTTP_PRODUCE_APPL_JSON, rawResponse);
	}

	@Test(expected = IllegalStateException.class)
	public void testCheckContentTypeFailsNoContentExpectedButThereIs()
	{
		given(rawResponse.contentType()).willReturn(Optional.empty());
		given(rawResponse.contentLength()).willReturn(of(100));
		classUnderTest.checkContentType("TEST", CpqClientConstants.NO_CONTENT_TYPE_PROVIDED, rawResponse);
	}


}
