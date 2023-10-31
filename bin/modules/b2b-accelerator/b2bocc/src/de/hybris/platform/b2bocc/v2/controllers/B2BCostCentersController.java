/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bocc.v2.controllers;

import de.hybris.platform.b2bocc.security.SecuredAccessConstants;
import de.hybris.platform.b2bocc.v2.helper.B2BCostCentersHelper;
import de.hybris.platform.b2bcommercefacades.company.B2BCostCenterFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.b2bwebservicescommons.dto.company.B2BCostCenterListWsDTO;
import de.hybris.platform.b2bwebservicescommons.dto.company.B2BCostCenterWsDTO;
import de.hybris.platform.b2bwebservicescommons.dto.company.B2BSelectionDataWsDTO;
import de.hybris.platform.b2bwebservicescommons.dto.mycompany.BudgetListWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;


@Controller
@RequestMapping(value = "/{baseSiteId}")
@ApiVersion("v2")
@Tag(name = "B2B Cost Centers")
public class B2BCostCentersController extends BaseController
{

	private static final String OBJECT_NAME_COST_CENTER = "costCenter";

	@Resource(name = "b2BCostCentersHelper")
	private B2BCostCentersHelper b2BCostCentersHelper;

	@Resource(name = "costCenterFacade")
	private B2BCostCenterFacade costCenterFacade;

	@Resource(name = "b2BCostCenterWsDTOValidator")
	private Validator b2BCostCenterWsDTOValidator;

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@GetMapping(value = "/costcentersall", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Operation(operationId = "getCostCenters", summary = "Get cost centers.", description = "Returns the list of all cost centers.")
	@ApiBaseSiteIdParam
	public B2BCostCenterListWsDTO getCostCenters(
			@Parameter(description = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "The number of results returned per page.") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Sorting method applied to the return results.") @RequestParam(required = false) final String sort,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		return b2BCostCentersHelper.searchCostCenters(currentPage, pageSize, sort, addPaginationField(fields));
	}

	@Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_GUEST,
			SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@GetMapping(value = "/costcenters")
	@ResponseBody
	@Operation(operationId = "getActiveCostCenters", summary = "Get active cost centers.", description = "Returns the list of all active cost centers.")
	@ApiBaseSiteIdParam
	public B2BCostCenterListWsDTO getActiveCostCenters(
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		return b2BCostCentersHelper.searchActiveCostCenters(fields);
	}

	@Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_GUEST,
			SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@GetMapping(value = "/costcenters/{costCenterCode}")
	@ResponseBody
	@Operation(operationId = "getCostCenter", summary = "Get a cost center.", description = "Returns a specific cost center based on specific code. The response contains detailed cost center information.")
	@ApiBaseSiteIdParam
	public B2BCostCenterWsDTO getCostCenter(
			@Parameter(description = "Cost center identifier.", required = true) @PathVariable final String costCenterCode,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		return b2BCostCentersHelper.searchCostCenter(costCenterCode, fields);
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@PostMapping(value = "/costcenters", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "createCostCenter", summary = "Create a new cost center.", description = "Creates a new cost center.")
	@ApiBaseSiteIdParam
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	public B2BCostCenterWsDTO createCostCenter(
			@Parameter(description = "Cost center object.", required = true) @RequestBody final B2BCostCenterWsDTO costCenter,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		validate(costCenter, OBJECT_NAME_COST_CENTER, b2BCostCenterWsDTOValidator);
		b2BCostCentersHelper.addCostCenter(costCenter);
		return b2BCostCentersHelper.searchCostCenter(costCenter.getCode(), fields);
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@PatchMapping(value = "/costcenters/{costCenterCode}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "updateCostCenter", summary = "Update a cost center.", description = "Updates a cost center. Only attributes provided in the request body will be changed.")
	@ApiBaseSiteIdParam
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public B2BCostCenterWsDTO updateCostCenter(
			@Parameter(description = "Cost center identifier.", required = true) @PathVariable final String costCenterCode,
			@Parameter(description = "Cost center object.", required = true) @RequestBody final B2BCostCenterWsDTO costCenter,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		b2BCostCentersHelper.enableDisableCostCenter(costCenterCode, costCenter);

		final B2BCostCenterData costCenterData = b2BCostCentersHelper.getCostCenterDataForCode(costCenterCode);
		getDataMapper().map(costCenter, costCenterData, false);
		costCenterData.setOriginalCode(costCenterCode);

		final B2BCostCenterWsDTO toBeValidatedCostCenter = getDataMapper().map(costCenterData, B2BCostCenterWsDTO.class);
		validate(toBeValidatedCostCenter, OBJECT_NAME_COST_CENTER, b2BCostCenterWsDTOValidator);

		costCenterFacade.updateCostCenter(costCenterData);
		return b2BCostCentersHelper.searchCostCenter(costCenterData.getCode(), fields);
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@PostMapping(value = "/costcenters/{costCenterCode}/budgets", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "doAddBudgetToCostCenter", summary = "Add a budget to a specific cost center.", description = "Adds a budget to a specific cost center.")
	@ApiBaseSiteIdParam
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public B2BSelectionDataWsDTO addBudgetToCostCenter(
			@Parameter(description = "Identifier of the cost center to which the budget will be added.", required = true) @PathVariable final String costCenterCode,
			@Parameter(description = "The budget which will be added to a specific cost center.", required = true) @RequestParam final String budgetCode,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final B2BSelectionData selectionData = b2BCostCentersHelper.addBudgetToCostCenter(costCenterCode, budgetCode);
		return getDataMapper().map(selectionData, B2BSelectionDataWsDTO.class, fields);
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@DeleteMapping(value = "/costcenters/{costCenterCode}/budgets/{budgetCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "removeBudgetFromCostCenter", summary = "Remove a budget from a specific cost center.", description = "Removes a budget from a specific cost center.")
	@ApiBaseSiteIdParam
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public B2BSelectionDataWsDTO removeBudgetFromCostCenter(
			@Parameter(description = "Identifier of the cost center from which the budget will be removed.", required = true) @PathVariable final String costCenterCode,
			@Parameter(description = "The budget which will be removed from a specific cost center.", required = true) @PathVariable final String budgetCode,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final B2BSelectionData selectionData = b2BCostCentersHelper.removeBudgetFromCostCenter(costCenterCode, budgetCode);
		return getDataMapper().map(selectionData, B2BSelectionDataWsDTO.class, fields);
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@GetMapping(value = "/costcenters/{costCenterCode}/budgets", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Operation(operationId = "getBudgetsForCostCenter", summary = "Get all budgets and select budgets which belong to a specific cost center.", description = "Returns the list of all budgets, where those budgets, which belong to a specific cost center, are selected.")
	@ApiBaseSiteIdParam
	public BudgetListWsDTO getBudgetsForCostCenter(
			@Parameter(description = "Cost center identifier.", required = true) @PathVariable final String costCenterCode,
			@Parameter(description = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "The number of results returned per page.") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Sorting method applied to the return results.") @RequestParam(required = false) final String sort,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		return b2BCostCentersHelper
				.searchBudgetsForCostCenter(costCenterCode, currentPage, pageSize, sort, addPaginationField(fields));
	}
}
