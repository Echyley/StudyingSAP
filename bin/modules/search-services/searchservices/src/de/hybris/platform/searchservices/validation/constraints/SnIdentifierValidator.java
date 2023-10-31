/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.validation.constraints;

import de.hybris.platform.searchservices.constants.SearchservicesConstants;

import java.util.regex.Matcher;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Validator for {@link SnIdentifier} constraint.
 */
public class SnIdentifierValidator implements ConstraintValidator<SnIdentifier, String>
{

	@Override
	public void initialize(final SnIdentifier identifier)
	{
		// empty
	}

	@Override
	public boolean isValid(final String identifier, final ConstraintValidatorContext constraintValidatorContext)
	{
		if (identifier == null)
		{
			return true;
		}
		final Matcher matcher = SearchservicesConstants.IDENTIFIER_PATTERN.matcher(identifier);
		return matcher.matches();
	}

}
