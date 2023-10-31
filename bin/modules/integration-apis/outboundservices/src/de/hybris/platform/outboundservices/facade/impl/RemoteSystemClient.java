/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.facade.impl;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

/**
 * REST interface to a remote system.
 */
public interface RemoteSystemClient
{
	/**
	 * Performs POST request to the remote system.
	 *
	 * @param destination a remote system info, e.g. URL, to which the request has to be sent.
	 * @param entity      an entity to use for the POST request body.
	 * @return result of the POST request.
	 * @deprecated Use {@link #post(ConsumedDestinationModel, HttpEntity, Class)} instead.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	ResponseEntity<Map> post(@NotNull ConsumedDestinationModel destination, HttpEntity<Map<String, Object>> entity);

	/**
	 * Performs POST request to the remote system with a parametrized response type.
	 *
	 * @param destination  a remote system info, e.g. URL, to which the request has to be sent.
	 * @param entity       an entity to use for the POST request body.
	 * @param responseType the class type that the HTTP response will contain
	 * @param <T>          Type of response entity
	 * @param <U>          Type of request entity
	 * @return A ResponseEntity with the contents of the REST call response
	 */
	<T, U> ResponseEntity<T> post(@NotNull ConsumedDestinationModel destination, HttpEntity<U> entity, Class<T> responseType);

	/**
	 * Performs POST request to the remote system with a parametrized response type.
	 *
	 * @param destination    a remote system info, e.g. URL, to which the request has to be sent.
	 * @param entity         an entity to use for the POST request body.
	 * @param responseType   the class type that the HTTP response will contain
	 * @param additionalPath additional request path added to the {@code destination} URL
	 * @param <T>            Type of response entity
	 * @param <U>            Type of request entity
	 * @return A ResponseEntity with the contents of the REST call response
	 */
	<T, U> ResponseEntity<T> post(@NotNull ConsumedDestinationModel destination,
	                              HttpEntity<U> entity,
	                              Class<T> responseType,
	                              String additionalPath);
}
