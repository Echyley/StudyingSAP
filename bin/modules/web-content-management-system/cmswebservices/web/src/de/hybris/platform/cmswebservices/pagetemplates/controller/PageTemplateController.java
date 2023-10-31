/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.pagetemplates.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cmsfacades.pagetemplates.PageTemplateFacade;
import de.hybris.platform.cmswebservices.data.PageTemplateDTO;
import de.hybris.platform.cmswebservices.data.PageTemplateData;
import de.hybris.platform.cmswebservices.data.PageTemplateListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Controller to deal with PageTemplate objects
 */
@Controller
@IsAuthorizedCmsManager
@Tag(name = "page templates")
public class PageTemplateController
{
	@Resource
	private PageTemplateFacade pageTemplateFacade;
	@Resource
	private DataMapper dataMapper;

	@GetMapping(value = API_VERSION
			+ "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagetemplates")
	@ResponseBody
	@Operation(summary = "Finds page templates by page type.", description = "Returns a holder of a collection of PageTemplateData filtered on the given data passed as query string.",
			operationId = "getPageTemplatesByPageType")
	@ApiResponse(responseCode = "200", description = "List of page templates")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "pageTypeCode", description = "Item type of a page", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "active", description = "When set to TRUE, filter the results for active templates", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	public PageTemplateListData findPageTemplatesByPageType(@Parameter(description = "Holder of search filters", required = true)
	@ModelAttribute final PageTemplateDTO pageTemplateInfo)
	{
		final de.hybris.platform.cmsfacades.data.PageTemplateDTO convertedPageTemplateDTO = getDataMapper().map(pageTemplateInfo,
				de.hybris.platform.cmsfacades.data.PageTemplateDTO.class);
		final List<PageTemplateData> pageTemplates = getDataMapper()
				.mapAsList(getPageTemplateFacade().findPageTemplates(convertedPageTemplateDTO), PageTemplateData.class, null);

		final PageTemplateListData pageTemplateListData = new PageTemplateListData();
		pageTemplateListData.setTemplates(pageTemplates);
		return pageTemplateListData;
	}

	@GetMapping(value = API_VERSION + "/pagetemplate")
	@ResponseBody
	@Operation(summary = "Finds page templates by page uuid.", description = "Returns the PageTemplateData filtered on the given data passed as query string.", operationId = "getPageTemplatesByPageUuid")
	@ApiResponse(responseCode = "200", description = "Get a page template")
	@Parameter(name = "pageUuid", description = "Uuid of a page", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	public PageTemplateData findPageTemplatesByPageUuid(
			@Parameter(description = "page uuid", required = true) @RequestParam(value = "pageUuid") final String pageUuid)
	{
		return getDataMapper().map(getPageTemplateFacade().findPageTemplateByPageUuid(pageUuid), PageTemplateData.class);
	}

	public PageTemplateFacade getPageTemplateFacade()
	{
		return pageTemplateFacade;
	}

	public void setPageTemplateFacade(final PageTemplateFacade pageTemplateFacade)
	{
		this.pageTemplateFacade = pageTemplateFacade;
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
