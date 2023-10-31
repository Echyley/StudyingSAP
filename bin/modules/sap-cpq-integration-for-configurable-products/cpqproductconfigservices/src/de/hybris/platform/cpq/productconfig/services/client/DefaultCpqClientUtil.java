/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.client;



import java.util.Optional;

import org.apache.log4j.Logger;

import com.hybris.charon.RawResponse;
import com.hybris.charon.exp.HttpException;

import rx.Observable;
import rx.schedulers.Schedulers;



/**
 * Default implementation of {@link CpqClientUtil}
 */
public class DefaultCpqClientUtil implements CpqClientUtil
{

	/**
	 * Exception message template in case CPQ returned unexpected HTTP status
	 */
	public static final String ERROR_MSG_WRONG_HTTP_STATUS_CODE = "HTTP interaction with CPQ client failed for '%s' operation, expected HTTP response code '%d', but CPQ retuned '%d'";

	private static final Logger LOG = Logger.getLogger(DefaultCpqClientUtil.class);
	private static final String ERROR_MSG_UNEXPECTED_CONTENT = "HTTP interaction with CPQ client failed for '%s' operation, expected NO_CONTENT, but CPQ returned content with length '%d'";
	private static final String ERROR_MSG_WRONG_HTTP_CONTENT_TYPE = "HTTP interaction with CPQ client failed for '%s' operation, expected HTTP content type '%s', but CPQ retuned '%s'";
	private static final String ERROR_MSG_SERVER_ERROR = "CPQ responded with HTTP ERROR code %d - %s, detailed message: %s";
	private static final String ERROR_PAGE_RECEIVED = "CPQ responded with an html error page / message: %s";

	@Override
	public void checkHTTPStatusCode(final String action, final int expectedStatusCode, final RawResponse<?> rawResponse)
	{
		final int actualStatusCode = rawResponse.getStatusCode();
		if (expectedStatusCode != actualStatusCode)
		{
			if (isErrorPageResponse(rawResponse))
			{
				LOG.error(String.format(ERROR_PAGE_RECEIVED, extractErrorPage(rawResponse)));
			}
			throw new IllegalStateException(
					String.format(ERROR_MSG_WRONG_HTTP_STATUS_CODE, action, expectedStatusCode, actualStatusCode));
		}
	}

	protected String extractErrorPage(final RawResponse<?> rawResponse)
	{
		try
		{
			return (String) toResponse(rawResponse.content());
		}
		catch (final IllegalArgumentException ex)
		{
			return "failed to extract error response, for further analysis, increase LogLevel of io.netty.handler.logging.LoggingHandler to DEBUG";
		}
	}

	protected boolean isErrorPageResponse(final RawResponse<?> rawResponse)
	{
		final Optional<Integer> contentLength = rawResponse.contentLength();
		final int statusCode = rawResponse.getStatusCode();
		return CpqClientConstants.HTTP_STATUS_OK == statusCode && contentLength.isPresent() && contentLength.get() > 0;
	}

	/**
	 * Resolves a response observable, assuming there is only exactly one element.
	 *
	 * @param response
	 *           observable to resolve
	 * @return actual response
	 */
	@Override
	public <T> T toResponse(final Observable<T> response)
	{
		try
		{
			return response.subscribeOn(Schedulers.io()).toBlocking().first();
		}
		catch (final HttpException ex)
		{
			LOG.error(extractLogMessage(ex));
			throw ex;
		}
	}

	protected String extractLogMessage(final HttpException ex)
	{
		final Observable<String> serverMessageObs = ex.getServerMessage();
		String message = null;
		if (null != serverMessageObs)
		{
			message = serverMessageObs.subscribeOn(Schedulers.io()).toBlocking().firstOrDefault(null);
		}
		return String.format(ERROR_MSG_SERVER_ERROR, ex.getCode(), ex.getMessage(), message);
	}

	@Override
	public void checkContentType(final String action, final String expectedContentType, final RawResponse<?> rawResponse)
	{
		final String actualContentType = rawResponse.contentType().orElse(CpqClientConstants.NO_CONTENT_TYPE_PROVIDED);
		final int contentLengh = rawResponse.contentLength().orElse(0);
		final boolean hasUnexpectedContent = CpqClientConstants.NO_CONTENT_TYPE_PROVIDED.equals(expectedContentType)
				&& contentLengh > 0;
		if (!actualContentType.contains(expectedContentType) || hasUnexpectedContent)
		{
			if (isErrorPageResponse(rawResponse))
			{
				LOG.error(String.format(ERROR_PAGE_RECEIVED, extractErrorPage(rawResponse)));
			}
			String msg;
			if (hasUnexpectedContent)
			{
				msg = String.format(ERROR_MSG_UNEXPECTED_CONTENT, action, contentLengh);
			}
			else
			{
				msg = String.format(ERROR_MSG_WRONG_HTTP_CONTENT_TYPE, action, expectedContentType, actualContentType);
			}
			throw new IllegalStateException(msg);
		}
	}

	protected boolean hasUnexpectedContent(final String expectedContentType, final int contentLengh)
	{
		return CpqClientConstants.NO_CONTENT_TYPE_PROVIDED.equals(expectedContentType) && contentLengh > 0;
	}

}
