/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.occ.v2.controllers;

import de.hybris.platform.b2b.occ.security.SecuredAccessConstants;
import de.hybris.platform.b2b.occ.v2.helper.ReplenishmentOrderHelper;
import de.hybris.platform.b2bacceleratorservices.model.process.ReplenishmentProcessModel;
import de.hybris.platform.b2bwebservicescommons.dto.order.ReplenishmentOrderListWsDTO;
import de.hybris.platform.b2bwebservicescommons.dto.order.ReplenishmentOrderWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderHistoryListWsDTO;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/replenishmentOrders")
@ApiVersion("v2")
@Tag(name = "Replenishment Order")
public class ReplenishmentOrderController extends BaseController
{

	@Resource(name = "replenishmentOrderHelper")
	private ReplenishmentOrderHelper replenishmentOrderHelper;

	@Secured(
	{ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_GUEST,
			SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@ResponseBody
	@ApiBaseSiteIdAndUserIdParam
	@GetMapping(produces = MediaType.APPLICATION_JSON)
	@Operation(operationId = "getReplenishmentOrders",
            summary = "Gets the list of replenishment orders for a specified user.",
            description = "Returns the list of replenishment orders accessible to a specified user.")
	public ReplenishmentOrderListWsDTO getReplenishmentOrders(
			@Parameter(description = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "The number of results returned per page.") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Sorting method applied to the returned results.") @RequestParam(defaultValue = ReplenishmentProcessModel.CODE) final String sort,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		return replenishmentOrderHelper.searchReplenishments(currentPage, pageSize, sort, addPaginationField(fields));
	}

	@Secured(
	{ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_GUEST,
			SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@ResponseBody
	@ApiBaseSiteIdAndUserIdParam
	@GetMapping(value = "/{replenishmentOrderCode}", produces = MediaType.APPLICATION_JSON)
	@Operation(operationId = "getReplenishmentOrder",
            summary = "Gets replenishment order for a specified user and replenishment order code.",
            description = "Returns specific replenishment order details accessible for a specified user. The response contains detailed orders information from the replenishment order.")
	public ReplenishmentOrderWsDTO getReplenishmentOrder(
			@Parameter(description = "Unique code for the replenishment order.", required = true) @PathVariable final String replenishmentOrderCode,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		return replenishmentOrderHelper.searchReplenishment(replenishmentOrderCode, fields);
	}

	@Secured(
	{ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_GUEST,
			SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@ResponseBody
	@ApiBaseSiteIdAndUserIdParam
	@PatchMapping(value = "/{replenishmentOrderCode}", produces = MediaType.APPLICATION_JSON)
	@Operation(operationId = "updateReplenishmentOrder",
            summary = "Updates the replenishment order for a specified user and replenishment order code.",
            description = "Updates the replenishment order. Only cancellation of the replenishment order is supported by setting the attribute 'active' to FALSE. Cancellation of the replenishment order cannot be reverted.")
	public ReplenishmentOrderWsDTO updateReplenishmentOrder(
			@Parameter(description = "Unique code for the replenishment order.", required = true) @PathVariable final String replenishmentOrderCode,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		return replenishmentOrderHelper.cancelReplenishment(replenishmentOrderCode, fields);
	}

	@Secured(
	{ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_GUEST,
			SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@ResponseBody
	@ApiBaseSiteIdAndUserIdParam
	@GetMapping(value = "/{replenishmentOrderCode}/orders", produces = MediaType.APPLICATION_JSON)
	@Operation(operationId = "getReplenishmentOrderHistory",
            summary = "Gets replenishment order history.",
            description = "Returns order history data from a replenishment order placed by a specified user.")
	public OrderHistoryListWsDTO getReplenishmentOrderHistory(
			@Parameter(description = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "The number of results returned per page.") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Sorting method applied to the returned results.") @RequestParam(defaultValue = ReplenishmentProcessModel.CODE) final String sort,
			@Parameter(description = "Unique code for the replenishment order.", required = true) @PathVariable final String replenishmentOrderCode,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		return replenishmentOrderHelper.searchOrderHistories(replenishmentOrderCode, currentPage, pageSize, sort,
				addPaginationField(fields));
	}

}
