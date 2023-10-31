/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static rx.Observable.just;

import de.hybris.platform.cpq.productconfig.services.client.CpqClientUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.hybris.charon.RawResponse;

import rx.Observable;


/**
 * Helper class for unit tests
 */
public class ObservableTestHelperLifeCycle
{

	private final CpqClientUtil clientUtil;

	/**
	 * @param clientUtil
	 */
	public ObservableTestHelperLifeCycle(final CpqClientUtil clientUtil)
	{
		super();
		this.clientUtil = clientUtil;
	}

	/**
	 * mocks an empty response with status
	 *
	 */
	public <T> Observable<RawResponse<T>> mockEmptyResponse(final int httpStatusCode)
	{
		return mockResponse(null, httpStatusCode, Collections.emptyMap());
	}


	/**
	 * mocks response with content and status
	 *
	 */
	public <T> Observable<RawResponse<T>> mockResponse(final T content, final int httpStatusCode)
	{
		return mockResponse(content, httpStatusCode, Collections.emptyMap());
	}
	
	
	
	/**
	 * mocks response with content, status and header
	 *
	 */
	public <T> Observable<RawResponse<T>> mockResponse(final T content, final int httpStatusCode,
			final Map<String, String> headers)
	{
		final RawResponse<T> rawResponse = mock(RawResponse.class);
		final Observable<T> contentObs = Observable.just(content);
		when(rawResponse.content()).thenReturn(content == null ? Observable.empty() : contentObs);
		when(rawResponse.header(anyString())).thenReturn(Optional.empty());
		for (final Entry<String, String> entry : headers.entrySet())
		{
			final String value = entry.getValue();
			when(rawResponse.header(entry.getKey())).thenReturn(null == value ? Optional.empty() : Optional.of(value));
		}
		final Observable<RawResponse<T>> obs = just(rawResponse);
		when(clientUtil.toResponse(obs)).thenReturn(rawResponse);
		when(clientUtil.toResponse(contentObs)).thenReturn(content);
		return obs;
	}
	




}
