/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.customerticketingocc.validators;

import com.google.common.base.Preconditions;
import de.hybris.platform.webservicescommons.validators.AbstractFieldValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class OptionalObjectValidator extends AbstractFieldValidator
{
	private final Validator validator;

	public OptionalObjectValidator(final Validator validator)
	{
		this.validator = validator;
	}

	public boolean supports(final Class<?> aClass)
	{
		return getValidator().supports(aClass);
	}

	public void validate(final Object object, final Errors errors)
	{
		Preconditions.checkArgument(errors != null, "Errors object must not be null.");
		Object fieldValue = errors.getFieldValue(this.getFieldPath());
		if (fieldValue != null)
		{
			getValidator().validate(object, errors);
		}
	}

	public Validator getValidator()
	{
		return this.validator;
	}
}
