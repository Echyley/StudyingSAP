/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderocc.controllers;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelEntryData;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelRequestData;
import de.hybris.platform.ordermanagementfacades.order.OmsOrderFacade;
import de.hybris.platform.sap.sapserviceorderfacades.util.SapServiceOrderFacadesUtil;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

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

import de.hybris.platform.commercewebservicescommons.dto.order.CancellationRequestEntryInputListWsDTO;
import com.sap.hybris.sapserviceorderocc.security.SecuredAccessConstants;
import com.sap.hybris.sapserviceorderocc.validators.CancelServiceEntryInputListWsDTOValidator;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Controller
@RequestMapping(value = "/{baseSiteId}")
@Tag(description = "Service Orders Controller", name = "Service Orders")
public class CancelServiceOrderController
{

	private static final Logger LOG = LoggerFactory.getLogger(CancelServiceOrderController.class);

	private static final String OBJECT_NAME_CANCEL_SERVICE_ENTRY_INPUT_LIST = "CancelServiceEntryInputList";

	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;

	@Resource(name = "omsOrderFacade")
	private OmsOrderFacade omsOrderFacade;

	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "cancelServiceEntryInputListWsDTOValidator")
	private CancelServiceEntryInputListWsDTOValidator cancelServiceEntryInputListWsDTOValidator;


	@Secured(
	{ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_GUEST,
			SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@RequestMapping(value = "/users/{userId}/orders/{code}/serviceOrder/cancellation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "cancelServiceOrder", summary = "Cancel a service order.", description = "Cancels a service order completely")
	@ApiBaseSiteIdAndUserIdParam
	public void cancelServiceOrder(@Parameter(description = "Order code", required = true)
	@PathVariable
	final String code, @Parameter(description = "Cancellation request input list for the current service order.", required = true)
	@RequestBody
	final CancellationRequestEntryInputListWsDTO cancellationRequestEntryInputList)
	{
		validateUserForOrder(code);
		validateCancelServiceOrderInput(cancellationRequestEntryInputList);
		final OrderData orderData = orderFacade.getOrderDetailsForCode(code);
		final List<OrderEntryData> orderEntriesData = orderData.getEntries().stream().filter(SapServiceOrderFacadesUtil::isServiceEntry)
				.collect(Collectors.toList());
		orderData.setEntries(orderEntriesData);
		omsOrderFacade.createRequestOrderCancel(prepareServiceOrderCancelRequestData(orderData, cancellationRequestEntryInputList));


	}


	/**
	 * @param cancelServiceEntryInputList
	 */
	private void validateCancelServiceOrderInput(final CancellationRequestEntryInputListWsDTO cancelServiceEntryInputList)
	{
		validate(cancelServiceEntryInputList, OBJECT_NAME_CANCEL_SERVICE_ENTRY_INPUT_LIST,
				cancelServiceEntryInputListWsDTOValidator);

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



	/**
	 * @param orderData
	 * @param cancelServiceEntryInputList
	 * @return
	 */
	private OrderCancelRequestData prepareServiceOrderCancelRequestData(final OrderData orderData,
			final CancellationRequestEntryInputListWsDTO cancelServiceEntryInputList)
	{
		final OrderCancelRequestData result = new OrderCancelRequestData();
		result.setOrderCode(orderData.getCode());
		result.setEntries(prepareServiceOrderCancelEntryData(orderData, cancelServiceEntryInputList));
		result.setUserId(userService.getCurrentUser().getUid());
		return result;
	}

	/**
	 * It prepares a list of {@link OrderCancelEntryData} object to be set in the entries of
	 * {@link OrderCancelRequestData}
	 *
	 * @param orderData
	 * @param cancelServiceEntryInputList
	 * @return list of {@link OrderCancelEntryData} representing the map of order entry and cancel quantity
	 */
	protected List<OrderCancelEntryData> prepareServiceOrderCancelEntryData(final OrderData orderData,
			final CancellationRequestEntryInputListWsDTO cancelServiceEntryInputList)
	{
		final List<OrderCancelEntryData> result = new ArrayList<>();

		orderData.getEntries().forEach(oed -> {
			if (SapServiceOrderFacadesUtil.isServiceEntry(oed))
			{
				final OrderCancelEntryData orderCancelEntryData = new OrderCancelEntryData();
				orderCancelEntryData.setOrderEntryNumber(oed.getEntryNumber());
				orderCancelEntryData.setCancelQuantity(oed.getQuantity());
				orderCancelEntryData.setCancelReason(CancelReason.CUSTOMERREQUEST);
				orderCancelEntryData.setNotes(cancelServiceEntryInputList.getCancelReason());
				result.add(orderCancelEntryData);
			}
		});
		return result;
	}


}
