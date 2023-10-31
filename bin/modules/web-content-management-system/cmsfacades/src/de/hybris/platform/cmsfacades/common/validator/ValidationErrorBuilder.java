/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.validator;

import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.validation.services.impl.LocalizedHybrisConstraintViolation;
import de.hybris.platform.validation.exceptions.HybrisConstraintViolation;

/**
 * Builder class for {@link ValidationError}
 */
public class ValidationErrorBuilder
{
	private String field;
	private Object rejectedValue;
	private String language;
	private String errorCode;
	private Object[] errorArgs;
	private String exceptionMessage;
	private Integer position;
	private String defaultMessage;

	private ValidationErrorBuilder()
	{
	}

	public ValidationError build()
	{
		final ValidationError validationError = new ValidationError();
		validationError.setField(field);
		validationError.setErrorArgs(errorArgs);
		validationError.setErrorCode(errorCode);
		validationError.setExceptionMessage(exceptionMessage);
		validationError.setLanguage(language);
		validationError.setPosition(position);
		validationError.setRejectedValue(rejectedValue);
		validationError.setDefaultMessage(defaultMessage);
		return validationError;
	}

	public ValidationError build(HybrisConstraintViolation violation) {
		this.field = violation.getConstraintViolation().getPropertyPath().toString();
		this.defaultMessage=violation.getLocalizedMessage();
		if (violation instanceof LocalizedHybrisConstraintViolation) {
			this.language = ((LocalizedHybrisConstraintViolation)violation).getViolationLanguage().getLanguage();
		}

		return build();
	}

	public static ValidationErrorBuilder newValidationErrorBuilder()
	{
		return new ValidationErrorBuilder();
	}


	public ValidationErrorBuilder field(String field)
	{
		this.field = field;
		return this;
	}

	public ValidationErrorBuilder rejectedValue(Object rejectedValue)
	{
		this.rejectedValue = rejectedValue;
		return this;
	}

	public ValidationErrorBuilder language(String language)
	{
		this.language = language;
		return this;
	}

	public ValidationErrorBuilder errorCode(String errorCode)
	{
		this.errorCode = errorCode;
		return this;
	}

	public ValidationErrorBuilder errorArgs(Object[] errorArgs)
	{
		this.errorArgs = errorArgs;
		return this;
	}

	public ValidationErrorBuilder exceptionMessage(String exceptionMessage)
	{
		this.exceptionMessage = exceptionMessage;
		return this;
	}

	public ValidationErrorBuilder defaultMessage(String defaultMessage)
	{
		this.defaultMessage = defaultMessage;
		return this;
	}

	public ValidationErrorBuilder position(Integer position)
	{
		this.position = position;
		return this;
	}


}
