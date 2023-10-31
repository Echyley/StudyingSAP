/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.media.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.MediaContainerData;
import de.hybris.platform.cmsfacades.mediacontainers.MediaContainerFacade;
import de.hybris.platform.cmswebservices.dto.MediaContainerListWsDTO;
import de.hybris.platform.cmswebservices.dto.MediaContainerWsDTO;
import de.hybris.platform.cmswebservices.dto.PageableWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Controller that handles searching and creating media container
 */
@RestController
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/catalogs/{catalogId}/versions/{versionId}/mediacontainers")
@Tag(name = "media containers")
public class MediaContainerController
{
	@Resource
	private MediaContainerFacade mediaContainerFacade;

	@Resource
	private DataMapper dataMapper;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	@GetMapping(value = "/{code}")
	@ResponseBody
	@Operation(summary = "Gets media container by code.", description = "Retrieves a specific media container that matches the given id.", operationId = "getMediaContainerByCode")

	@ApiResponse(responseCode = "400", description = "When the media container was not found (CMSItemNotFoundException) or when there was a problem during conversion (ConversionException).")
	@ApiResponse(responseCode = "200", description = "MediaContainerWsDTO")

	@Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	public MediaContainerWsDTO getMediaContainerByCode(
			@Parameter(description = "The unique identifier of the media container", required = true)
			@PathVariable
			final String code) throws CMSItemNotFoundException
	{
		return getDataMapper().map(getMediaContainerFacade().getMediaContainerForQualifier(code), MediaContainerWsDTO.class);
	}

	@GetMapping(params =	{ "pageSize" })
	@ResponseBody
	@Operation(summary = "Finds media container by partial to full code matching.", description = "Retrieves a list of available media containers using a free text search field.", operationId = "getMediaContainersByText")

	@ApiResponse(responseCode = "200", description = "Item which serves as a wrapper object that contains a list of MediaContainerData; never null")

	@Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "pageSize", description = "The maximum number of elements in the result list.", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "currentPage", description = "The requested page number", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "sort", description = "The string field the results will be sorted with", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY) //
	public MediaContainerListWsDTO findMediaContainersByText(
			@Parameter(description = "The string value on which media containers will be filtered", required = false)
			@RequestParam(required = false)
			final String mask, @Parameter(description = "PageableWsDTO", required = true)
			@ModelAttribute
			final PageableWsDTO pageableInfo)
	{
		final SearchResult<MediaContainerData> mediaContainerSearchResult = getMediaContainerFacade().findMediaContainers(mask,
				getDataMapper().map(pageableInfo, PageableData.class));

		final MediaContainerListWsDTO mediaContainers = new MediaContainerListWsDTO();
		mediaContainers.setMediaContainers(mediaContainerSearchResult //
				.getResult() //
				.stream() //
				.map(containerData -> getDataMapper().map(containerData, MediaContainerWsDTO.class)) //
				.collect(Collectors.toList()));
		mediaContainers.setPagination(getWebPaginationUtils().buildPagination(mediaContainerSearchResult));
		return mediaContainers;
	}

	protected MediaContainerFacade getMediaContainerFacade()
	{
		return mediaContainerFacade;
	}

	public void setMediaContainerFacade(final MediaContainerFacade mediaContainerFacade)
	{
		this.mediaContainerFacade = mediaContainerFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	public void setWebPaginationUtils(final WebPaginationUtils webPaginationUtils)
	{
		this.webPaginationUtils = webPaginationUtils;
	}
}
