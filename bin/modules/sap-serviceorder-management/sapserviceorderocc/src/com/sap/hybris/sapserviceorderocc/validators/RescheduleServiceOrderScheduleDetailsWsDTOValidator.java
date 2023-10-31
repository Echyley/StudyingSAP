/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderocc.validators;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.sap.hybris.sapserviceorderocc.dto.ServiceScheduleSlotWsDTO;

import de.hybris.platform.sap.sapserviceorderfacades.facades.SapServiceOrderCheckoutFacade;


public class RescheduleServiceOrderScheduleDetailsWsDTOValidator implements Validator
{
	private static final String FIELD_REQUIRED_MESSAGE_ID = "field.required";
	private static final String SERVICE_DATE_FIELD = "serviceDate";

	@Resource(name = "sapServiceOrderCheckoutFacade")
	private SapServiceOrderCheckoutFacade sapServiceOrderCheckoutFacade;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return ServiceScheduleSlotWsDTO.class.equals(clazz);
	}


	@Override
	public void validate(final Object target, final Errors errors)
	{
		final ServiceScheduleSlotWsDTO rescheduleServiceOrderScheduleDetails = (ServiceScheduleSlotWsDTO) target;
		if (rescheduleServiceOrderScheduleDetails == null)
		{
			errors.reject("error.rescheduleServiceOrderScheduleDetails.notFound");
		}
		else
		{
			Date serviceDate = rescheduleServiceOrderScheduleDetails.getScheduledAt();
			if (serviceDate == null)
			{
				errors.rejectValue(SERVICE_DATE_FIELD, FIELD_REQUIRED_MESSAGE_ID);
				return;
			}


			final Date leadDate = sapServiceOrderCheckoutFacade.getServiceLeadDate();

			 ;

			if (serviceDate.before(leadDate))
			{
				errors.rejectValue(SERVICE_DATE_FIELD, "reschedule.servicedatevalid.error");
			}
		}



	}

}
