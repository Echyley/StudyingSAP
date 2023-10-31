/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.pages.controller;

import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.CMSPageOperationsData;
import de.hybris.platform.cmsfacades.pages.PageFacade;
import de.hybris.platform.cmssmarteditwebservices.dto.CMSPageOperationWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Controller that provides an API to perform different operations on pages.
 */
@Controller
@RequestMapping(API_VERSION + "/sites/{baseSiteId}/catalogs/{catalogId}/pages/{pageId}/operations")
@Tag(name = "page operations")
public class PageOperationsController
{

	@Resource
	private DataMapper dataMapper;

	@Resource
	private PageFacade pageFacade;

	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(operationId = "performPageItemOperations", summary = "Perform different operations on the page item.", description = "Endpoint to perform different operations on the page item such as delete a page etc.")
	@Parameter(name = "baseSiteId", description = "Base site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "catalogId", description = "The uid of the catalog", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "404", description = "When the item has not been found (UnknownIdentifierException) ")
	@ApiResponse(responseCode = "400", description = "When the payload does not have the 'operation' property. (IllegalArgumentException)")
	@ApiResponse(responseCode = "200", description = "The page operation item.")
	public CMSPageOperationWsDTO perform( //
			@Parameter(description = "The uid of the catalog", required = true)
			@PathVariable
			final String catalogId, //
			@Parameter(description = "The uid of the page to be updated", required = true)
			@PathVariable
			final String pageId,
			@Parameter(description = "The DTO object containing all the information about operation to be performed", required = true)
			@RequestBody
			final CMSPageOperationWsDTO dto) throws CMSItemNotFoundException
	{
		final CMSPageOperationsData data = getDataMapper().map(dto, CMSPageOperationsData.class);
		data.setCatalogId(catalogId);
		final CMSPageOperationsData newPageOperationData = getPageFacade().performOperation(pageId, data);
		return getDataMapper().map(newPageOperationData, CMSPageOperationWsDTO.class);
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	protected PageFacade getPageFacade()
	{
		return pageFacade;
	}

	public void setPageFacade(final PageFacade pageFacade)
	{
		this.pageFacade = pageFacade;
	}

}
