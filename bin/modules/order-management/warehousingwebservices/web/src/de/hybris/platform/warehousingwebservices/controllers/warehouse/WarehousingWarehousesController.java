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
package de.hybris.platform.warehousingwebservices.controllers.warehouse;


import de.hybris.platform.warehousingwebservices.controllers.WarehousingBaseController;
import de.hybris.platform.warehousingwebservices.dto.store.WarehouseWsDto;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseData;
import de.hybris.platform.warehousingfacades.warehouse.WarehousingWarehouseFacade;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import javax.annotation.Resource;


/**
 * WebResource exposing {@link de.hybris.platform.warehousingfacades.warehouse.WarehousingWarehouseFacade}
 * http://host:port/warehousingwebservices/warehouses
 */
@Controller
@RequestMapping(value = "/warehouses")
@Tag(name = "Warehouse's Operations")
public class WarehousingWarehousesController extends WarehousingBaseController
{
	@Resource
	private WarehousingWarehouseFacade warehousingWarehouseFacade;

	/**
	 * Request to get a {@link de.hybris.platform.ordersplitting.model.WarehouseModel} for the code
	 *
	 * @param code
	 * 		the name of the requested {@link de.hybris.platform.ordersplitting.model.WarehouseModel}
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @return the {@link de.hybris.platform.ordersplitting.model.WarehouseModel}
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getWarehouseForCode", summary = "Finds a warehouse by the given code", description = "It returns a warehouse for the given code, the given warehouse code should be valid")
	public WarehouseWsDto getWarehouseForCode(@PathVariable final String code,
			@Parameter(description = "The code of the requested warehouse") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final WarehouseData warehouse = warehousingWarehouseFacade.getWarehouseForCode(code);
		return dataMapper.map(warehouse, WarehouseWsDto.class, fields);
	}

}
