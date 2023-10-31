/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsocc.controllers;


import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;

import static de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper.DEFAULT_LEVEL;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.sap.hybris.sapentitlementsfacades.data.EntitlementData;
import com.sap.hybris.sapentitlementsfacades.data.EntitlementDataList;
import com.sap.hybris.sapentitlementsfacades.facade.SapEntitlementFacade;
import com.sap.hybris.sapentitlementsocc.dto.EntitlementInstanceListWsDTO;
import com.sap.hybris.sapentitlementsocc.dto.EntitlementInstanceWsDTO;
import org.apache.log4j.Logger;
import com.sap.hybris.sapentitlementsintegration.exception.SapEntitlementException;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


/**
 *
 */

@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/entitlementInstances")
@ApiVersion("v2")
@Tag(name = "Entitlement Instances")
public class EntitlementInstancesController
{
	private static final Logger LOG = Logger.getLogger(EntitlementInstancesController.class);

	private static final String DEFAULT_FIELD_SET = "DEFAULT";

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "sapEntitlementFacade")
	private SapEntitlementFacade sapEntitlementFacade;

	protected static final String DEFAULT_PAGE_SIZE = "100";
	protected static final String DEFAULT_CURRENT_PAGE = "1";



	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(operationId = "getEntitlementInstances", summary = "Get user's entitlement instances", description = "Returns user's entitlement instances.")
	@ApiBaseSiteIdAndUserIdParam
	@ApiResponse(responseCode = "200", description = "List of user's entitlement instances")
	public EntitlementInstanceListWsDTO getEntitlementsInstances(
			@Parameter(description = "Response configuration. This is the list of fields that should be returned in the response body. Example: BASIC, DEFAULT, FULL.")
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields, @Parameter(description = "The current result page requested.")
			@RequestParam(defaultValue = DEFAULT_CURRENT_PAGE)
			final int currentPage, @Parameter(description = "The number of results returned per page.")
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE)
			final int pageSize)
	{
		final EntitlementDataList entitlementList = new EntitlementDataList();
		try {
			final List<EntitlementData> entitlements = getSapEntitlementFacade().getEntitlementsForCurrentCustomer(currentPage,
					pageSize);
			entitlementList.setEntitlements(entitlements);
			return dataMapper.map(entitlementList, EntitlementInstanceListWsDTO.class, fields);
		} catch( SapEntitlementException ex) {
			LOG.error(ex);
			throw new SapEntitlementException("An Unknown exception has occured");
		}
	}

	@RequestMapping(value = "/{entitlementInstanceId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(operationId = "getEntitlementInstance", summary = "Get entitlement instance details", description = "Returns details of a specific entitlement instance based on the entitlement number.")
	@ApiBaseSiteIdAndUserIdParam
	@ApiResponse(responseCode = "200", description = "Entitlement Details")
	public EntitlementInstanceWsDTO getEntitlementInstance(
			@Parameter(description = "Response configuration. This is the list of fields that should be returned in the response body. Example: BASIC, DEFAULT, FULL.")
			@RequestParam(defaultValue = DEFAULT_FIELD_SET)
			final String fields, @Parameter(description = "Number of the entitlement instance.", required = true)
			@PathVariable(required = true)
			final String entitlementInstanceId)
	{
		try {
			final EntitlementData entitlementData = getSapEntitlementFacade().getEntitlementForNumber(entitlementInstanceId);
			return dataMapper.map(entitlementData, EntitlementInstanceWsDTO.class, fields);
		} catch( SapEntitlementException ex) {
			LOG.error(ex);
			throw new SapEntitlementException("An Unknown Exception Occurred");
		}
	}

	/**
	 * @return the dataMapper
	 */
	public DataMapper getDataMapper()
	{
		return dataMapper;
	}

	/**
	 * @param dataMapper
	 *           the dataMapper to set
	 */
	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	/**
	 * @return the sapEntitlementFacade
	 */
	public SapEntitlementFacade getSapEntitlementFacade()
	{
		return sapEntitlementFacade;
	}

	/**
	 * @param sapEntitlementFacade
	 *           the sapEntitlementFacade to set
	 */
	public void setSapEntitlementFacade(final SapEntitlementFacade sapEntitlementFacade)
	{
		this.sapEntitlementFacade = sapEntitlementFacade;
	}

}
