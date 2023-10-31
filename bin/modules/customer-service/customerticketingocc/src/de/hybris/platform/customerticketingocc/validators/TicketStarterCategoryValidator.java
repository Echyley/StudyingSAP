/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.customerticketingocc.validators;

import de.hybris.platform.customerticketingfacades.data.TicketCategory;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketStarterWsDTO;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.google.common.base.Preconditions;

public class TicketStarterCategoryValidator implements Validator
{
	private static final String FIELD_REQUIRED_ERROR_CODE = "field.required";

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return TicketStarterWsDTO.class.equals(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final TicketStarterWsDTO ticketStarter = (TicketStarterWsDTO) object;
		Preconditions.checkArgument(errors != null, "Errors object must not be null.");

		if (ticketStarter != null && ticketStarter.getTicketCategory() != null)
		{
			try
			{
				TicketCategory.valueOf(ticketStarter.getTicketCategory().getId());
			}
			catch (final RuntimeException e)
			{
				errors.rejectValue("ticketCategory.id", FIELD_REQUIRED_ERROR_CODE);
			}
		}
	}
}
