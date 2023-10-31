/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.client;

import com.hybris.charon.RawResponse;

import rx.Observable;


/**
 * Provides re-use utility methods for interacting with CPQClient.
 */
public interface CpqClientUtil
{

	/**
	 * CPQ may return in some error situations with HTTP 200/OK and issue and error page, instead of returning the
	 * expected HTTP 201/CREATED or 204/NO_CONTENT.<br>
	 * Use this method to detect such situations and terminate gracefully, instead of running in parse errors while
	 * trying to parse unexpected content.<br>
	 * In case you actually expect HTTP 200/OK, you should check the content type on top using
	 * {@link #checkContentType(String, String, RawResponse)}
	 *
	 * @param action
	 *           log friendly string, indicating the triggering action
	 * @param expectedStatusCode
	 *           expected status HTTP code
	 * @param rawResponse
	 *           response containing the actual HTTP status code
	 */
	void checkHTTPStatusCode(String action, int expectedStatusCode, RawResponse<?> rawResponse);

	/**
	 * Resolves a response observable, assuming there is only exactly one element.
	 *
	 * @param response
	 *           observable to resolve
	 * @return actual response
	 */
	<T> T toResponse(Observable<T> response);

	/**
	 * CPQ may return in some error situations with HTTP 200/OK and issue and error page, instead of returning a JSON
	 * response.
	 *
	 * @param action
	 *           log friendly string, indicating the triggering action
	 * @param expectedContentType
	 *           expected content type
	 * @param rawResponse
	 *           response containing the actual content type
	 */
	void checkContentType(String action, String expectedContentType, RawResponse<?> rawResponse);

}
