/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderocc.validators;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import de.hybris.platform.sap.sapserviceorderfacades.facades.SapServiceOrderCheckoutFacade;


@Component
public class ServiceScheduleSlotValidator implements Validator {
	
	private static final String SERVICE_DATE_FIELD = "serviceDate";
	
	@Resource(name = "sapServiceOrderCheckoutFacade")
	private SapServiceOrderCheckoutFacade sapServiceOrderCheckoutFacade;
	

	@Override
	public boolean supports(Class<?> clazz) {
		return String.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		final Date servicedAt = (Date) target;
		
		if (servicedAt == null)
		{
			errors.rejectValue(SERVICE_DATE_FIELD, "general.required");
			return;
		}
		
		final Date leadDate = sapServiceOrderCheckoutFacade.getServiceLeadDate();
		if (servicedAt.before(leadDate))
		{
			errors.rejectValue(SERVICE_DATE_FIELD, "checkout.multi.servicedatevalid.error");
			return;
		}
		
	}

}
