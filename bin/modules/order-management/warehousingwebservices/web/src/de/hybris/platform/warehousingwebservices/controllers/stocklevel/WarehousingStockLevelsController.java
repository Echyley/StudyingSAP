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
package de.hybris.platform.warehousingwebservices.controllers.stocklevel;


import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.warehousing.enums.StockLevelAdjustmentReason;
import de.hybris.platform.warehousingfacades.product.data.StockLevelData;
import de.hybris.platform.warehousingfacades.stocklevel.WarehousingStockLevelFacade;
import de.hybris.platform.warehousingfacades.stocklevel.data.StockLevelAdjustmentData;
import de.hybris.platform.warehousingfacades.stocklevel.data.StockLevelAdjustmentReasonDataList;
import de.hybris.platform.warehousingfacades.warehouse.WarehousingWarehouseFacade;
import de.hybris.platform.warehousingwebservices.controllers.WarehousingBaseController;
import de.hybris.platform.warehousingwebservices.dto.product.StockLevelSearchPageWsDto;
import de.hybris.platform.warehousingwebservices.dto.product.StockLevelWsDto;
import de.hybris.platform.warehousingwebservices.dto.stocklevel.StockLevelAdjustmentReasonsWsDTO;
import de.hybris.platform.warehousingwebservices.dto.stocklevel.StockLevelAdjustmentWsDTO;
import de.hybris.platform.warehousingwebservices.dto.stocklevel.StockLevelAdjustmentsWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * WebResource exposing {@link WarehousingStockLevelFacade}
 * http://host:port/warehousingwebservices/stocklevels
 */
@Controller
@RequestMapping(value = "/stocklevels")
@Tag(name = "Stock Level's Operations")
public class WarehousingStockLevelsController extends WarehousingBaseController
{
	@Resource
	private WarehousingStockLevelFacade warehousingStockLevelFacade;
	@Resource
	private WarehousingWarehouseFacade warehousingWarehouseFacade;
	@Resource(name = "warehousingStockLevelValidator")
	private Validator warehousingStockLevelValidator;
	@Resource(name = "stockLevelAdjustmentValidator")
	private Validator stockLevelAdjustmentValidator;
	@Resource(name = "stockLevelAdjustmentReasonValidator")
	private Validator stockLevelAdjustmentReasonValidator;

	/**
	 * Request to get a {@link de.hybris.platform.ordersplitting.model.StockLevelModel} for its {@value de.hybris.platform.ordersplitting.model.WarehouseModel#CODE}
	 *
	 * @param code
	 * 		the code of the requested {@link de.hybris.platform.ordersplitting.model.WarehouseModel}
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return the list of stocklevels
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/warehouses/{code}", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getStockLevelsForWarehouseCode", summary = "Finds a paginated list of stock levels by a given warehouse code", description = "Finds a paginated list of stock levels by a given warehouse code.")
	public StockLevelSearchPageWsDto getStockLevelsForWarehouseCode(
			@Parameter(description = "The code for the warehouse", required = true) @PathVariable final String code,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@Parameter(description = "Current page") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "Page size") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Sort parameter") @RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
	{
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<StockLevelData> stockLevelSearchPageData = warehousingStockLevelFacade
				.getStockLevelsForWarehouseCode(code, pageableData);
		return dataMapper.map(stockLevelSearchPageData, StockLevelSearchPageWsDto.class, fields);
	}

	/**
	 * Request to create a {@link de.hybris.platform.ordersplitting.model.StockLevelModel} in the system
	 *
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param stockLevelWsDto
	 * 		object representing {@link StockLevelWsDto}
	 * @return created stockLevel
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Operation(operationId = "createStockLevel", summary = "Creates a stocklevel", description = "Creates a stocklevel.")
	public StockLevelWsDto createStockLevel(
			@Parameter(description = "The stocklevel object to be created", required = true) @RequestBody final StockLevelWsDto stockLevelWsDto,
			@Parameter(description = "Fields mapping level") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws WebserviceValidationException
	{
		validate(stockLevelWsDto, "stockLevelWsDto", warehousingStockLevelValidator);
		final StockLevelData stockLevelData = dataMapper.map(stockLevelWsDto, StockLevelData.class);
		final StockLevelData createdStockLevelData = warehousingStockLevelFacade.createStockLevel(stockLevelData);

		return dataMapper.map(createdStockLevelData, StockLevelWsDto.class, fields);
	}

	/**
	 * Request to get return stock level adjustment reasons
	 *
	 * @return list of stock level adjustment reason
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/adjustment-reasons", method = RequestMethod.GET)
	@ResponseBody
	@Operation(operationId = "getStockLevelAdjustmentReasons", summary = "Finds all adjustment reasons", description = "Finds all adjustment reasons.")
	public StockLevelAdjustmentReasonsWsDTO getStockLevelAdjustmentReasons()
	{
		final List<StockLevelAdjustmentReason> stockLevelAdjustmentReasons = warehousingStockLevelFacade
				.getStockLevelAdjustmentReasons();
		final StockLevelAdjustmentReasonDataList stockLevelAdjustmentReasonDataList = new StockLevelAdjustmentReasonDataList();
		stockLevelAdjustmentReasonDataList.setReasons(stockLevelAdjustmentReasons);
		return dataMapper.map(stockLevelAdjustmentReasonDataList, StockLevelAdjustmentReasonsWsDTO.class);
	}

	/**
	 * Request to create a {@link de.hybris.platform.warehousing.model.InventoryEventModel} in the system to adjust a specific {@link de.hybris.platform.ordersplitting.model.StockLevelModel}
	 *
	 * @param productCode
	 * 		the product code for which an adjustment is required
	 * @param warehouseCode
	 * 		the warehouse code for which an adjustment is required
	 * @param binCode
	 * 		the bin code of the stock level to adjust (optional)
	 * @param releaseDate
	 * 		the release date of the stock level to adjust (optional)
	 * @param stockLevelAdjustmentsWsDTO
	 * 		list of stock level adjustment to be created
	 * @return created stockLevel
	 */
	@Secured({ WAREHOUSE_AGENT_GROUP, WAREHOUSE_MANAGER_GROUP, WAREHOUSE_ADMINISTRATOR_GROUP })
	@RequestMapping(value = "/product/{productCode}/warehouse/{warehouseCode}/adjustment", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Operation(operationId = "createStockLevelAdjustment", summary = "Creates an inventoryEvent to adjust a specific stocklevel", description = "Creates an inventoryEvent to adjust a specific stocklevel.")
	public StockLevelAdjustmentsWsDTO createStockLevelAdjustment(
			@Parameter(description = "Product Code", required = true) @PathVariable final String productCode,
			@Parameter(description = "Warehouse Code", required = true) @PathVariable final String warehouseCode,
			@Parameter(description = "Bin Code") @RequestParam(required = false) final String binCode,
			@Parameter(description = "Release Date") @RequestParam(required = false) final String releaseDate,
			@Parameter(description = "List of stockLevel Adjustments", required = true) @RequestBody final StockLevelAdjustmentsWsDTO stockLevelAdjustmentsWsDTO)
			throws WebserviceValidationException
	{
		if (warehousingWarehouseFacade.getWarehouseForCode(warehouseCode).isExternal())
		{
			throw new WebserviceValidationException(
					Localization.getLocalizedString("warehousingwebservices.stocklevels.error.externalwarehouse"));
		}

		final List<StockLevelAdjustmentData> stockLevelAdjustmentDatas = new ArrayList<>();
		stockLevelAdjustmentsWsDTO.getStockLevelAdjustments().stream().forEach(stockLevelAdjustmentWsDto -> {
			validate(stockLevelAdjustmentWsDto, "stockLevelAdjustmentWsDto", stockLevelAdjustmentValidator);
			if (stockLevelAdjustmentWsDto.getReason() != null)
			{
				stockLevelAdjustmentWsDto.setReason(stockLevelAdjustmentWsDto.getReason().toUpperCase());
				validate(new String[] { stockLevelAdjustmentWsDto.getReason() }, "reason", stockLevelAdjustmentReasonValidator);
			}
			stockLevelAdjustmentDatas.add(dataMapper.map(stockLevelAdjustmentWsDto, StockLevelAdjustmentData.class));
		});

		final List<StockLevelAdjustmentData> createdStockLevelAdjustmentsData = warehousingStockLevelFacade
				.createStockLevelAdjustements(productCode, warehouseCode, binCode, releaseDate, stockLevelAdjustmentDatas);

		final List<StockLevelAdjustmentWsDTO> stockLevelAdjustmentWsDTOs = new ArrayList<>();
		createdStockLevelAdjustmentsData.stream().forEach(stockLevelAdjustmentData -> stockLevelAdjustmentWsDTOs
				.add(dataMapper.map(stockLevelAdjustmentData, StockLevelAdjustmentWsDTO.class)));

		final StockLevelAdjustmentsWsDTO returnedStockLevelAdjustmentsWsDTO = new StockLevelAdjustmentsWsDTO();
		returnedStockLevelAdjustmentsWsDTO.setStockLevelAdjustments(stockLevelAdjustmentWsDTOs);
		return returnedStockLevelAdjustmentsWsDTO;
	}
}
