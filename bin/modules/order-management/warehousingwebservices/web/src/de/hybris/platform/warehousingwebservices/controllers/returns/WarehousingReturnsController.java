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
 */
package de.hybris.platform.warehousingwebservices.controllers.returns;

import de.hybris.platform.warehousingfacades.returns.WarehousingReturnFacade;
import de.hybris.platform.warehousingwebservices.controllers.WarehousingBaseController;

import javax.annotation.Resource;

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
 * WebResource exposing {@link de.hybris.platform.warehousingfacades.returns.WarehousingReturnFacade}
 * http://host:port/warehousingwebservices/returns
 */
@Controller
@RequestMapping(value = "/returns")
@Tag(name = "Return's Operations")
public class WarehousingReturnsController extends WarehousingBaseController
{
	@Resource
	private WarehousingReturnFacade warehousingReturnFacade;

	/**
	 * Request to accept goods.
	 *
	 * @param code
	 * 		code for the requested returnRequest.
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "{code}/accept-goods", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "acceptReturnedGoods", summary = "Request to accept returned goods", description = "Request to accept returned goods.")
	public void acceptReturnedGoods(
			@Parameter(description = "Return code to be accepted", required = true) @PathVariable final String code)
	{
		warehousingReturnFacade.acceptGoods(code);
	}
}
