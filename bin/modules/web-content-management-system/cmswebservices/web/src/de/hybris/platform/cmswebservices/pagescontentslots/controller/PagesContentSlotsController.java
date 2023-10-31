/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.pagescontentslots.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.pagescontentslots.PageContentSlotFacade;
import de.hybris.platform.cmswebservices.data.PageContentSlotData;
import de.hybris.platform.cmswebservices.data.PageContentSlotListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.Collections;
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
 * Controller that provides an API to retrieve all pages where a given content slot is present.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslots")
@Tag(name = "page slots")
public class PagesContentSlotsController
{
	@Resource
	private PageContentSlotFacade pageContentSlotFacade;
	@Resource
	private DataMapper dataMapper;

	@RequestMapping(method = RequestMethod.GET, params =
			{ "pageId" })
	@ResponseStatus(value = HttpStatus.OK)
	@Operation(summary = "Gets content slots by page.", description = "Retrieves a list of available content slots defined on the page specified by the page id.",
			operationId = "getContentSlotByPage")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "200", description = "DTO which serves as a wrapper object that contains a list of PageContentSlotListData, never null.")
	public @ResponseBody
	PageContentSlotListData getContentSlotsByPage(@Parameter(description = "Identifier of the page", required = true)
	@RequestParam("pageId") final String pageId)
	{
		final PageContentSlotListData pageContentSlotList = new PageContentSlotListData();

		try
		{
			final List<PageContentSlotData> convertedSlots = getDataMapper()
					.mapAsList(getPageContentSlotFacade().getContentSlotsByPage(pageId), PageContentSlotData.class, null);
			pageContentSlotList.setPageContentSlotList(convertedSlots);
		}
		catch (final CMSItemNotFoundException e)
		{
			pageContentSlotList.setPageContentSlotList(Collections.emptyList());
		}
		return pageContentSlotList;
	}

	protected PageContentSlotFacade getPageContentSlotFacade()
	{
		return pageContentSlotFacade;
	}

	public void setPageContentSlotFacade(final PageContentSlotFacade pageContentSlotFacade)
	{
		this.pageContentSlotFacade = pageContentSlotFacade;
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
