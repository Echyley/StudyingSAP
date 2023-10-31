/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.pages.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.pages.PageFacade;
import de.hybris.platform.cmswebservices.data.AbstractPageData;
import de.hybris.platform.cmswebservices.data.PageListData;
import de.hybris.platform.cmswebservices.data.UidListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Controller to deal with AbstractPageModel objects
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pages")
@Tag(name = "pages")
public class PageController
{
	@Resource
	private LocationHeaderResource locationHeaderResource;

	@Resource
	private PageFacade cmsPageFacade;

	@Resource
	private DataMapper dataMapper;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	/**
	 * @deprecated since 6.6
	 */
	@Deprecated(since = "6.6", forRemoval = true)
	@RequestMapping(method = RequestMethod.GET, params =
			{ "uids" })
	@ResponseBody
	@Operation( //
			summary = "Finds pages by ids.", description =
			"Finds specific pages for a given site, catalog and version ids. Deprecated since 6.6, please use "
					+ "GET /v1/sites/{siteId}/cmsitems{?uuids} instead.", operationId = "getPagesByIds")

	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "200", description = "List of AbstractPageData")
	public PageListData findPagesByIds(
			@Parameter(description = "List of identifier of the pages that we are looking for", required = true)
			@RequestParam("uids") final List<String> uids)
	{
		final List<de.hybris.platform.cmsfacades.data.AbstractPageData> pages = getCmsPageFacade().findAllPages().stream()
				.filter(page -> uids.contains(page.getUid())).collect(Collectors.toList());

		final PageListData pageListData = new PageListData();
		pageListData.setPages(getDataMapper().mapAsList(pages, AbstractPageData.class, null));
		return pageListData;
	}

	/**
	 * @deprecated since 6.6
	 */
	@Deprecated(since = "6.6", forRemoval = true)
	@RequestMapping(value = "/{pageId}", method = RequestMethod.GET)
	@ResponseBody
	@Operation( //
			summary = "Gets page by uid.", description =
			"Retrieves a specific page instance that matches given page uid. Deprecated since 6.6, please use "
					+ "GET /v1/sites/{siteId}/cmsitems/{uuid} instead.", operationId = "getPageByUid")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "400", description = "When the page cannot be found (CMSItemNotFoundException).")
	@ApiResponse(responseCode = "200", description = "Page data object")
	public AbstractPageData getPageByUid(@Parameter(description = "Page identifier", required = true)
	@PathVariable final String pageId) throws CMSItemNotFoundException
	{
		return getDataMapper().map(getCmsPageFacade().getPageByUid(pageId), AbstractPageData.class);
	}

	/**
	 * @deprecated since 6.6
	 */
	@Deprecated(since = "6.6", forRemoval = true)
	@RequestMapping(method = RequestMethod.GET, params =
			{ "typeCode", "defaultPage" })
	@ResponseBody
	@Operation( //
			summary = "Finds pages by type.", description = "Gets all default or variation pages that matches the given page type. "
			+ "Deprecated since 6.6, please use "
			+ "GET /v1/sites/{siteId}/cmsitems{?mask,typeCode,catalogId,catalogVersion,itemSearchParams,sort,pageSize,currentPage} instead.",
			operationId = "getPagesByType")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "catalogId", description = "The catalog name", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "200", description = "All default or variation pages for a given page type")
	public PageListData findPagesByType(@Parameter(description = "The type code of a page", required = true)
	@RequestParam("typeCode") final String typeCode,
			@Parameter(description = "Setting this to true will find all default pages; otherwise find all variation pages", required = true)
			@RequestParam("defaultPage") final Boolean isDefaultPage)
	{
		final List<de.hybris.platform.cmsfacades.data.AbstractPageData> pages = getCmsPageFacade().findPagesByType(typeCode,
				isDefaultPage);

		final PageListData pageListData = new PageListData();
		pageListData.setPages(getDataMapper().mapAsList(pages, AbstractPageData.class, null));
		return pageListData;
	}

	@RequestMapping(value = "/{pageId}/variations", method = RequestMethod.GET)
	@ResponseBody
	@Operation(summary = "Finds variation pages.", description = "Retrieves a list of available variation pages uid for a given page.",
			operationId = "getVariationPages")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "catalogId", description = "The catalog name", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "400", description = "When the pageId is invalid (CMSItemNotFoundException).")
	@ApiResponse(responseCode = "200", description = "All variation pages uid for a given page; empty if the given page is already a variation page; never null.")
	public UidListData findVariationPages(@Parameter(description = "The page identifier", required = true)
	@PathVariable final String pageId) throws CMSItemNotFoundException
	{
		return convertToUidListData(getCmsPageFacade().findVariationPages(pageId));
	}

	@RequestMapping(value = "/{pageId}/fallbacks", method = RequestMethod.GET)
	@ResponseBody
	@Operation(summary = "Finds fallback pages.", description = "Retrieves a list of available fallback pages for a given page.",
			operationId = "getFallbackPages")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "catalogId", description = "The catalog name", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "400", description = "When the pageId is invalid (CMSItemNotFoundException).")
	@ApiResponse(responseCode = "200", description = "All fallback pages uid for a given page; empty if the given page is already a fallback page; never null")
	public UidListData findFallbackPages(@Parameter(description = "The page identifier", required = true)
	@PathVariable final String pageId) throws CMSItemNotFoundException
	{
		return convertToUidListData(getCmsPageFacade().findFallbackPages(pageId));
	}

	protected UidListData convertToUidListData(final List<String> pageIds)
	{
		final UidListData pageData = new UidListData();
		pageData.setUids(pageIds);
		return pageData;
	}

	protected LocationHeaderResource getLocationHeaderResource()
	{
		return locationHeaderResource;
	}

	public void setLocationHeaderResource(final LocationHeaderResource locationHeaderResource)
	{
		this.locationHeaderResource = locationHeaderResource;
	}

	protected PageFacade getCmsPageFacade()
	{
		return cmsPageFacade;
	}

	public void setCmsPageFacade(final PageFacade pageFacade)
	{
		this.cmsPageFacade = pageFacade;
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
