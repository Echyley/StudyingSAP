/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.media.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmsfacades.data.MediaFolderData;
import de.hybris.platform.cmsfacades.media.CMSMediaFolderFacade;
import de.hybris.platform.cmswebservices.dto.MediaFolderListWsDTO;
import de.hybris.platform.cmswebservices.dto.MediaFolderWsDTO;
import de.hybris.platform.cmswebservices.dto.PageableWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Controller that provides media folder.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/mediafolders")
@Tag(name = "media folder")
public class MediaFolderController
{
	@Resource
	private CMSMediaFolderFacade cmsMediaFolderFacade;

	@Resource
	private DataMapper dataMapper;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	@GetMapping(params = { "pageSize" })
	@ResponseBody
	@Operation(summary = "Finds media folders by partial to full qualifier matching.", description = "Retrieves a list of available media folders using a free text search field.", operationId = "getMediaFoldersByText")

	@ApiResponse(
			responseCode = "200",
			description = "Item which serves as a wrapper object that contains a list of MediaFolderData; never null")

	@Parameter(name = "pageSize", description = "The maximum number of elements in the result list.", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "currentPage", description = "The requested page number", schema = @Schema(type = "string"), in = ParameterIn.QUERY)

	public MediaFolderListWsDTO findFoldersByText(
			@Parameter(description = "The string value on which media folders will be filtered")
			@RequestParam(required = false) final String mask,
			@Parameter(description = "PageableWsDTO", required = true)
			@ModelAttribute final PageableWsDTO pageableInfo)
	{
		final SearchResult<MediaFolderData> mediaFolderSearchResult = getCmsMediaFolderFacade().findMediaFolders(mask,
				getDataMapper().map(pageableInfo, PageableData.class));

		final MediaFolderListWsDTO mediaFolders = new MediaFolderListWsDTO();
		mediaFolders.setMediaFolders(mediaFolderSearchResult //
				.getResult() //
				.stream() //
				.map(folderData -> getDataMapper().map(folderData, MediaFolderWsDTO.class)) //
				.collect(Collectors.toList()));
		mediaFolders.setPagination(getWebPaginationUtils().buildPagination(mediaFolderSearchResult));

		return mediaFolders;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	public void setDataMapper(DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	public void setWebPaginationUtils(WebPaginationUtils webPaginationUtils)
	{
		this.webPaginationUtils = webPaginationUtils;
	}

	protected CMSMediaFolderFacade getCmsMediaFolderFacade()
	{
		return cmsMediaFolderFacade;
	}

	public void setCmsMediaFolderFacade(CMSMediaFolderFacade cmsMediaFolderFacade)
	{
		this.cmsMediaFolderFacade = cmsMediaFolderFacade;
	}
}
