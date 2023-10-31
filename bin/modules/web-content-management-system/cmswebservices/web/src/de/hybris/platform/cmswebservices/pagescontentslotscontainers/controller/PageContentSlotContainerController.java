/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.pagescontentslotscontainers.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;
import static java.util.Collections.emptyList;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.pagescontentslotscontainers.PageContentSlotContainerFacade;
import de.hybris.platform.cmswebservices.dto.PageContentSlotContainerListWsDTO;
import de.hybris.platform.cmswebservices.dto.PageContentSlotContainerWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Controller that provides an API to retrieve information about containers in content slots in a page.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslotscontainers")
@Tag(name = "page slot containers")
public class PageContentSlotContainerController
{
	@Resource
	private PageContentSlotContainerFacade pageContentSlotContainerFacade;
	@Resource
	private DataMapper dataMapper;

	@RequestMapping(method = RequestMethod.GET, params =
			{ "pageId" })
	@ResponseStatus(value = HttpStatus.OK)
	@Operation(summary = "Gets containers by page.", description = "Fetches a list of available containers on a given page.",
			operationId = "getContainersByPage")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "200", description = "The list of page content slot containers")
	public @ResponseBody
	PageContentSlotContainerListWsDTO getContainersByPage(
			@Parameter(description = "The page identifier", required = true)
			@RequestParam("pageId") final String pageId)
	{
		final PageContentSlotContainerListWsDTO pageContentSlotContainerList = new PageContentSlotContainerListWsDTO();

		try
		{
			final List<de.hybris.platform.cmsfacades.data.PageContentSlotContainerData> pageSlotContainerList = getPageContentSlotContainerFacade()
					.getPageContentSlotContainersByPageId(pageId);

			final List<PageContentSlotContainerWsDTO> convertedList = getDataMapper().mapAsList(pageSlotContainerList,
					PageContentSlotContainerWsDTO.class, null);
			pageContentSlotContainerList.setPageContentSlotContainerList(convertedList);
		}
		catch (final CMSItemNotFoundException e)
		{
			pageContentSlotContainerList.setPageContentSlotContainerList(emptyList());
		}
		return pageContentSlotContainerList;
	}

	protected PageContentSlotContainerFacade getPageContentSlotContainerFacade()
	{
		return pageContentSlotContainerFacade;
	}

	public void setPageContentSlotContainerFacade(final PageContentSlotContainerFacade containerSlotFacade)
	{
		this.pageContentSlotContainerFacade = containerSlotFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

}
