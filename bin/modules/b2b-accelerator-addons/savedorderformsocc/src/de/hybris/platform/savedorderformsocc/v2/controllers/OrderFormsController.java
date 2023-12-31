/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.savedorderformsocc.v2.controllers;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.savedorderforms.api.orderform.OrderFormFacade;
import de.hybris.platform.savedorderforms.orderform.data.OrderFormData;
import de.hybris.platform.savedorderformsocc.dto.OrderFormListWsDTO;
import de.hybris.platform.savedorderformsocc.dto.OrderFormWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.tags.Tag;


@Controller
@RequestMapping(value = "/{baseSiteId}/orderforms")
@ApiVersion("v2")
@Tag(name = "Order Forms")
public class OrderFormsController
{
	@Resource(name = "orderFormFacade")
	protected OrderFormFacade orderFormFacade;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	/**
	 * Gets a saved Order Form
	 *
	 * @param orderFormCode
	 *           the order form code to be fetched
	 * @return a representation of {@link de.hybris.platform.savedorderformsocc.dto.OrderFormWsDTO}
	 */
	@ResponseBody
	@GetMapping(value = "/{orderFormCode}")
	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@ApiBaseSiteIdParam
	public OrderFormWsDTO getOrderFormForCode(@PathVariable final String orderFormCode,
			@RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		final OrderFormData orderFormData = orderFormFacade.getOrderFormForCode(orderFormCode);

		final OrderFormWsDTO dto = dataMapper.map(orderFormData, OrderFormWsDTO.class, fields);

		return dto;
	}

	/**
	 * Gets a list of saved Order Form for the current user.
	 *
	 * @return a representation of {@link de.hybris.platform.savedorderformsocc.dto.OrderFormListWsDTO}
	 */
	@ResponseBody
	@GetMapping
	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@ApiBaseSiteIdParam
	public OrderFormListWsDTO getOrderFormsForCurrentUser(
			@RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		final List<OrderFormData> orderFormDatas = orderFormFacade.getOrderFormsForCurrentUser();

		final OrderFormListWsDTO orderFormListWsDTO = new OrderFormListWsDTO();
		orderFormListWsDTO.setOrderForms(dataMapper.mapAsList(orderFormDatas, OrderFormWsDTO.class, fields));

		return orderFormListWsDTO;
	}

	/**
	 * Creates an Order Form
	 */
	@ResponseStatus(value = HttpStatus.CREATED)
	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ApiBaseSiteIdParam
	public void createOrderForm(@RequestBody final OrderFormWsDTO orderFormWsDTO)
	{

		final OrderFormData orderFormData = dataMapper.map(orderFormWsDTO, OrderFormData.class,
				"code,description,currency,entries");

		orderFormFacade.createOrderForm(orderFormData);
	}

	/**
	 * Updates an order form.
	 *
	 * @param orderFormCode
	 *           the order form code to be updated
	 * @return a representation of {@link de.hybris.platform.savedorderformsocc.dto.OrderFormWsDTO}
	 */
	@ResponseBody
	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@PutMapping(value = "/{orderFormCode}", consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ApiBaseSiteIdParam
	public OrderFormWsDTO updateOrderForm(@PathVariable final String orderFormCode,
			@RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields,
			@RequestBody(required = true) final OrderFormWsDTO orderFormWsDTO)
	{

		final OrderFormData orderFormData = dataMapper.map(orderFormWsDTO, OrderFormData.class, fields);

		final OrderFormData orderFormDataRet = orderFormFacade.updateOrderForm(orderFormCode, orderFormData);

		return dataMapper.map(orderFormDataRet, OrderFormWsDTO.class, fields);
	}

	/**
	 * Deletes an order form.
	 *
	 * @param orderFormCode
	 *           the order form code to be deleted
	 * @return a representation of {@link de.hybris.platform.savedorderformsocc.dto.OrderFormWsDTO}
	 */
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@DeleteMapping(value = "/{orderFormCode}")
	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@ApiBaseSiteIdParam
	public void deleteOrderForm(@PathVariable final String orderFormCode)
	{

		orderFormFacade.removeOrderForm(orderFormCode);

	}

	/**
	 * Adds an order form to the cart.
	 *
	 * @param orderFormCode
	 *           the order form code to be updated
	 * @return a representation of {@link de.hybris.platform.savedorderformsocc.dto.OrderFormWsDTO}
	 */
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@PostMapping(value = "/{orderFormCode}/cart")
	@ApiBaseSiteIdParam
	public void orderForm(@PathVariable final String orderFormCode,
			@RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
			throws CommerceCartModificationException
	{
		orderFormFacade.addOrderFormToCart(orderFormCode, "");
	}

}
