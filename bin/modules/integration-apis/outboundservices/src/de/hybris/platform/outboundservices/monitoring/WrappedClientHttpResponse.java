/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.monitoring;

import de.hybris.platform.integrationservices.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.naming.SizeLimitExceededException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;

public class WrappedClientHttpResponse implements ClientHttpResponse
{
	private static final Logger LOG = Log.getLogger(WrappedClientHttpResponse.class);
	private final ClientHttpResponse clientHttpResponse;
	private final int sizeLimit;
	private ByteArrayInputStream inputStream;

	public WrappedClientHttpResponse(final ClientHttpResponse clientHttpResponse, final int sizeLimit)
	{
		this.clientHttpResponse = clientHttpResponse;
		this.sizeLimit = sizeLimit;
	}

	@Override
	public org.springframework.http.HttpStatus getStatusCode() throws IOException
	{
		return clientHttpResponse.getStatusCode();
	}

	@Override
	public int getRawStatusCode() throws IOException
	{
		return clientHttpResponse.getRawStatusCode();
	}

	@Override
	public String getStatusText() throws IOException
	{
		return clientHttpResponse.getStatusText();
	}

	@Override
	public void close()
	{
		clientHttpResponse.close();
		try
		{
			if (inputStream != null)
			{
				inputStream.close();
			}
		}
		catch (final IOException e)
		{
			LOG.trace(e.getMessage(), e);
		}
	}


	@Override
	public InputStream getBody() throws IOException
	{
		if (inputStream == null)
		{
			// In order to determine if the response body exceeds the max size, we create a BoundedInputStream of size sizeLimit
			// +1. If when we create it it's full it means that the response body had a size larger than allowed
			final BoundedInputStream input = new BoundedInputStream(clientHttpResponse.getBody(), (long) sizeLimit + 1);
			final byte[] bytesRead = IOUtils.toByteArray(input);
			if (bytesRead.length > sizeLimit)
			{
				throw new IOException("Body size exceeds limit", new SizeLimitExceededException("bad size"));
			}

			inputStream = new ByteArrayInputStream(bytesRead);
		}
		return inputStream;
	}

	@Override
	public HttpHeaders getHeaders()
	{
		return clientHttpResponse.getHeaders();
	}

	boolean isResponseMaxSizeExceeded()
	{
		if (isContentLengthHeaderApplicable() && getHeaders().getContentLength() > sizeLimit)
		{
			close();
			return true;
		}
		return isBodyExceedsMaxSize();
	}

	private boolean isContentLengthHeaderApplicable()
	{
		// based on https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.4, when Transfer-Encoding header is present,
		// Content-Length should be ignored.
		return getHeaders().containsKey(HttpHeaders.CONTENT_LENGTH) &&
				!getHeaders().containsKey(HttpHeaders.TRANSFER_ENCODING);
	}

	private boolean isBodyExceedsMaxSize()
	{
		try
		{
			this.getBody();
		}
		catch (final IOException e)
		{
			if (e.getCause() instanceof SizeLimitExceededException)
			{
				this.close();
				return true;
			}
		}
		return false;
	}
}
