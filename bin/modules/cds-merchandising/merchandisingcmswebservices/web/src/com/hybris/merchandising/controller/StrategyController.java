/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.controller;

import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.dto.PaginationWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hybris.merchandising.dto.DropdownElement;
import com.hybris.merchandising.dto.StrategyListWsDTO;
import com.hybris.merchandising.model.Strategy;
import com.hybris.merchandising.service.StrategyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * StrategyController is a simple REST controller exposing an end point to allow
 * us to retrieve the configured Strategies for a given tenant.
 */
@RestController
@IsAuthorizedCmsManager
@Tag(name = "Strategies", description = "Operations for strategies configured in CDS")
@ApiResponses(value = {
		@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorListWsDTO.class), examples = @ExampleObject(value = "{ \"errors\": [{ \"message\": \"No site with id [electronics] found.\", \"type\": \"UnknownIdentifierError\" }]}"))),
		@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorListWsDTO.class), examples = @ExampleObject(value = "{ \"errors\": [{ \"message\": \"Invalid access token\", \"type\": \"InvalidTokenError\" }]}"))),
		@ApiResponse(responseCode = "403", description = "Forbidden. Have no access to this method", content = @Content(schema = @Schema(implementation = ErrorListWsDTO.class))) })
public class StrategyController
{
	private static final Integer DEFAULT_PAGE_SIZE = Integer.valueOf(10);
	private static final Integer FIRST_PAGE = Integer.valueOf(0);
	@Autowired
	protected StrategyService strategyService;

	/**
	 * Retrieves a list of configured {@link Strategy} objects from Strategy
	 * service.
	 *
	 * @param currentPage - optional page number (e.g. 1).
	 * @param pageSize    - optional page size (e.g. 10).
	 * @return a list of configured {@link Strategy}.
	 */
	@GetMapping(value = "/v1/{siteId}/strategies", produces = "application/json")
	@ResponseBody
	@Operation(operationId = "getStrategies", summary = "List available strategies", description = "Retrieves a list of configured strategy objects from CDS Strategy Service.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List of strategy objects", content = @Content(schema = @Schema(implementation = StrategyListWsDTO.class)))
	})
	@Parameter(name = "siteId", description = "Base site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	public StrategyListWsDTO getStrategies(
			@Parameter(description = "Current page number", schema = @Schema(defaultValue = "0")) @RequestParam(value = "currentPage", defaultValue = "0", required = false) Integer currentPage,
			@Parameter(description = "Page size", schema = @Schema(defaultValue = "10")) @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize)
	{
		if (currentPage == null)
		{
			currentPage = FIRST_PAGE;
		}
		if (pageSize == null)
		{
			pageSize = DEFAULT_PAGE_SIZE;
		}

		final List<DropdownElement> strategies = strategyService.getStrategies(currentPage + 1, pageSize)
		                                                        .stream()
		                                                        .filter(Objects::nonNull)
		                                                        .map(strategy -> new DropdownElement(strategy.getId(), strategy.getName()))
		                                                        .collect(Collectors.toList());
		return buildResponse(strategies, pageSize, currentPage);
	}

	protected StrategyListWsDTO buildResponse(final List<DropdownElement> strategies, final int pageSize, final int currentPage)
	{
		final StrategyListWsDTO response = new StrategyListWsDTO();
		response.setOptions(strategies);

		final PaginationWsDTO pagination = new PaginationWsDTO();
		pagination.setCount(strategies.size());
		pagination.setPage(currentPage);
		final boolean hasNext = strategies.size() >= pageSize;
		pagination.setHasNext(hasNext);
		pagination.setHasPrevious(currentPage > 0);
		if (!hasNext)
		{
			pagination.setTotalCount(Long.valueOf((long) currentPage * pageSize + strategies.size()));
		}
		response.setPagination(pagination);
		return response;

	}

	@GetMapping(value = "/v1/{siteId}/strategies/{id}", produces = "application/json")
	@ResponseBody
	@Operation(operationId = "getStrategy", summary = "Get Strategy details.", description = "Retrieves information about strategy from CDS Strategy Service.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Information about strategy", content = @Content(schema = @Schema(implementation = DropdownElement.class))),
			@ApiResponse(responseCode = "404", description = "Strategy with given id doesn't exist", content = @Content(schema = @Schema(implementation = ErrorListWsDTO.class), examples = @ExampleObject(value = "{ \"errors\": [{ \"message\": \"Not Found\", \"type\": \"NotFoundError\" }]}")))
	})
	@Parameter(name = "siteId", description = "Base site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	public DropdownElement getStrategy(@Parameter(description = "Strategy identifier", required = true) @PathVariable final String id)
	{
		final Strategy strategy = strategyService.getStrategy(id);
		if (strategy != null)
		{
			return new DropdownElement(strategy.getId(), strategy.getName());
		}
		return new DropdownElement("", "");
	}
}
