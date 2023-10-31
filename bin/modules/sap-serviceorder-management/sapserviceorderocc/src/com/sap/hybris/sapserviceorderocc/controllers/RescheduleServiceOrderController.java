/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderocc.controllers;

import javax.annotation.Resource;
import org.springframework.http.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.sap.hybris.sapserviceorderocc.dto.ServiceScheduleSlotWsDTO;
import com.sap.hybris.sapserviceorderocc.security.SecuredAccessConstants;
import com.sap.hybris.sapserviceorderocc.validators.RescheduleServiceOrderScheduleDetailsWsDTOValidator;

import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.sap.sapserviceorderfacades.facades.SapServiceOrderCheckoutFacade;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Controller
@RequestMapping(value = "/{baseSiteId}")
@Tag(name = "Service Orders")
public class RescheduleServiceOrderController
{
	private static final Logger LOG = LoggerFactory.getLogger(RescheduleServiceOrderController.class);

	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;


	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;


	@Resource(name = "sapServiceOrderCheckoutFacade")
	private SapServiceOrderCheckoutFacade sapServiceOrderCheckoutFacade;

	@Resource(name = "rescheduleServiceOrderScheduleDetailsWsDTOValidator")
	private RescheduleServiceOrderScheduleDetailsWsDTOValidator rescheduleServiceOrderScheduleDetailsWsDTOValidator;


	private static final String OBJECT_NAME_SERVICE_ORDER_SCHEDULE_DETAILS = "RescheduleServiceOrderScheduleDetails";


	@Secured(
	{ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_GUEST,
			SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@RequestMapping(value = "/users/{userId}/orders/{code}/serviceOrder/serviceScheduleSlot", method = RequestMethod.PATCH,  consumes = { MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "updateOrderServiceScheduleSlot", summary = "Reschedule a service order.", description = "Reschedule a service order")
	@ApiBaseSiteIdAndUserIdParam
	public void rescheduleServiceOrder(@Parameter(description = "Order code", required = true)
	@PathVariable
	final String code, @Parameter(description = "Reschedule request for the service order.", required = true)
	@RequestBody
	final ServiceScheduleSlotWsDTO serviceScheduleSlot)
	{
		validateUserForOrder(code);
		validateRescheduleServiceOrderInput(serviceScheduleSlot);

		try
		{
			sapServiceOrderCheckoutFacade.rescheduleServiceRequestDate(code,serviceScheduleSlot.getScheduledAt());
		}
		catch (final Exception e)
		{
			throw new IllegalArgumentException("Unable to reschedule service order", e);
		}

	}



	/**
	 * @param rescheduleServiceOrderScheduleDetails
	 */
	private void validateRescheduleServiceOrderInput(
			final ServiceScheduleSlotWsDTO rescheduleServiceOrderScheduleDetails)
	{
		validate(rescheduleServiceOrderScheduleDetails, OBJECT_NAME_SERVICE_ORDER_SCHEDULE_DETAILS,
				rescheduleServiceOrderScheduleDetailsWsDTOValidator);

	}


	/**
	 * Validates if the current user has access to the order
	 *
	 * @param code
	 *           the order code
	 * @throws NotFoundException
	 *            if current user has no access to the order
	 */
	protected void validateUserForOrder(final String code)
	{
		try
		{
			orderFacade.getOrderDetailsForCode(code);
		}
		catch (final UnknownIdentifierException ex)
		{
			LOG.warn("Order not found for the current user in current BaseStore", ex);
			throw new NotFoundException("Resource not found");
		}
	}


	protected void validate(final Object object, final String objectName, final Validator validator)
	{
		final Errors errors = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
	}





}
