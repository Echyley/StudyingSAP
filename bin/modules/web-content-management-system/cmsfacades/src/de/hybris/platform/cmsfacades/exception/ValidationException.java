/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.exception;

import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;

import org.springframework.validation.Errors;

/**
 * Exception thrown when there is any problem when validating request data.
 */
public class ValidationException extends RuntimeException
{
	private static final long serialVersionUID = 5922002536003254842L;

	protected final transient Errors validationObject;
	private final ValidationErrors validationErrors;

	public ValidationException(final Errors validationObject)
	{
		super("Validation error");
		this.validationObject = validationObject;
		this.validationErrors = null;
	}

	public ValidationException(final ValidationErrors validationErrors)
	{
		super("Validation error");
		this.validationErrors = validationErrors;
		this.validationObject = null;
	}

	public Errors getValidationObject()
	{
		return validationObject;
	}

	public ValidationErrors getValidationErrors()
	{
		return validationErrors;
	}
}
