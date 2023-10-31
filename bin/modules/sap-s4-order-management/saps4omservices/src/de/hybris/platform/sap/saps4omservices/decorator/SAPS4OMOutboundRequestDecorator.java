/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.decorator;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import de.hybris.platform.outboundservices.decorator.DecoratorContext;

public interface SAPS4OMOutboundRequestDecorator {

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
	default boolean isApplicable() {
		return true;
	}
	/**
	 * Decorates an Outbound request
	 *
	 * @param httpHeaders The headers to be used for the outgoing request.
	 * @return An {@link HttpEntity} containing the result of the decoration. Normally by calling {@code execution.createHttpEntity()}
	 */
	public void decorate(HttpHeaders header, String action);
}
