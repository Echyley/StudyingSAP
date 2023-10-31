/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.azure.dtu.impl;

/**
 * Exception for validation errors on any jdbc connection.
 */
public class AzureDatabaseConnectionValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AzureDatabaseConnectionValidationException(final String message) {
		super(message);
	}

	public AzureDatabaseConnectionValidationException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
