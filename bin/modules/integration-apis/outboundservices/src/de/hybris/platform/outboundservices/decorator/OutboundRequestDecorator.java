/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.decorator;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

/**
 * Decorates the outbound client request. By modifying headers and payload along the decorator chain.
 */
public interface OutboundRequestDecorator
{
	/**
	 * Determines if the decorator is enabled.
	 *
	 * @return {@code true} if the decorator is enabled; {@code false} if not
	 */
	default boolean isEnabled() {
		return true;
	}

	/**
	 * Determines if the decorator is applicable.
	 *
	 * @param decoratorContext with the information about the request being decorated.
	 * @return {@code true} if the decorator is applicable; {@code false} if not
	 */
	default boolean isApplicable(final DecoratorContext decoratorContext) {
		return true;
	}


	/**
	 * Decorates an Outbound request
	 *
	 * @param httpHeaders      The headers to be used for the outgoing request.
	 * @param payload          The payload used for the request
	 * @param decoratorContext Some extra information that can be used by decorators.
	 * @param execution        The execution chain.
	 * @return An {@link HttpEntity} containing the result of the decoration. Normally by calling {@code execution.createHttpEntity()}
	 */
	HttpEntity<Map<String, Object>> decorate(HttpHeaders httpHeaders, Map<String, Object> payload,
	                                         DecoratorContext decoratorContext, DecoratorExecution execution);
}
