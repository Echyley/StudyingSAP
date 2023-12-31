/*
 * [y] hybris Platform
 *
 * Copyright (c) 2022 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousingwebservices.controllers.order;

import de.hybris.platform.warehousingfacades.order.WarehousingConsignmentFacade;
import de.hybris.platform.warehousingfacades.order.WarehousingOrderFacade;
import de.hybris.platform.warehousingwebservices.controllers.WarehousingBaseController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * WebResource exposing {@link WarehousingConsignmentFacade}
 * http://host:port/warehousingwebservices/orders
 */
@Controller
@RequestMapping(value = "/orders")
@Tag(name = "Order's Operations")
public class WarehousingOrdersController extends WarehousingBaseController
{
	@Resource
	private WarehousingOrderFacade warehousingOrderFacade;

	/**
	 * Request to put order on hold
	 *
	 * @param code
	 * 		order's code to be put on hold
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/on-hold", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "putOrderOnHold", summary = "Puts an order on hold", description = "Puts an order on hold.")
	public void putOrderOnHold(@Parameter(description = "Order code", required = true) @PathVariable @NotNull final String code)
	{
		warehousingOrderFacade.putOrderOnHold(code);
	}

	/**
	 * Request to re-Source an order
	 *
	 * @param code
	 * 		order's code to be resourced
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}/re-source", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "reSourceOrder", summary = "Resources an order", description = "Resources an order.")
	public void reSource(@Parameter(description = "Order code", required = true) @PathVariable @NotNull final String code)
	{
		warehousingOrderFacade.reSource(code);
	}
}
