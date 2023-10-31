/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderocc.controllers;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.sap.hybris.sapserviceorderocc.dto.ServiceScheduleSlotWsDTO;

import de.hybris.platform.sap.sapserviceorderfacades.facades.SapServiceOrderCheckoutFacade;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Controller
@RequestMapping(value = "/{baseSiteId}")
@Tag(name = "Service Orders")
public class ServiceOrderController {
	// implement the controller here


	@Resource(name = "sapServiceOrderCheckoutFacade")
	private SapServiceOrderCheckoutFacade sapServiceOrderCheckoutFacade;

	@Resource(name = "serviceScheduleSlotValidator")
	private Validator serviceScheduleSlotValidator;

	protected static final String SERVICE_DATE_FIELD = "serviceDate";
	
	
	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST" })
	@RequestMapping(value = "/users/{userId}/carts/{cartId}/serviceOrder/serviceScheduleSlot", method = RequestMethod.PATCH,  consumes = { MediaType.APPLICATION_JSON_VALUE },  produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "updateCartServiceScheduleSlot", summary = "Updates Requested Service Start Date for all service products in cart.", description = "Updates Requested Service Start Date for all service products in cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	
	public void saveServiceOrderScheduleDetails(
			@Parameter(description = "Cart identifier: cart code for logged in user, cart guid for anonymous user, 'current' for the last modified cart", required = true)  @PathVariable(required = true) final String cartId,
			@Parameter(description = "Schedule request for the service order.", required = true) @RequestBody final ServiceScheduleSlotWsDTO serviceScheduleSlot)
	{
		validate(serviceScheduleSlot.getScheduledAt(), SERVICE_DATE_FIELD, serviceScheduleSlotValidator);
		sapServiceOrderCheckoutFacade.updateCartWithServiceScheduleDate(serviceScheduleSlot.getScheduledAt());
	}
 


	protected void validate(final Object object, final String objectName, final Validator validator) {
		final Errors errors = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, errors);
		if (errors.hasErrors()) {
			throw new WebserviceValidationException(errors);
		}
	}
}
