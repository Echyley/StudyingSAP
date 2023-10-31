/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.pagesrestrictions.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.pagesrestrictions.PageRestrictionFacade;
import de.hybris.platform.cmswebservices.cmsitems.controller.CMSItemController;
import de.hybris.platform.cmswebservices.data.PageRestrictionData;
import de.hybris.platform.cmswebservices.data.PageRestrictionListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
 * Controller that provides an API to retrieve all pages and their restrictions.
 *
 * @deprecated since 1905, please use {@link CMSItemController} instead. When passing a page UUID, the response payload
 *             will contain the related restrictions as well.
 */
@Deprecated(since = "1905", forRemoval = true)
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagesrestrictions")
@Tag(name = "page restrictions")
public class PagesRestrictionsController
{
	private static final Logger LOG = LoggerFactory.getLogger(PagesRestrictionsController.class);

	@Resource
	private PageRestrictionFacade pageRestrictionFacade;
	@Resource
	private DataMapper dataMapper;

	/**
	 * @deprecated since 1905, please use {@link CMSItemController} instead.
	 */
	@Deprecated(since = "1905", forRemoval = true)
	@GetMapping
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@Operation(summary = "Gets restrictions for all pages.", description = "Finds restrictions for all pages.", operationId = "getRestrictionsForAllPage")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "catalogId", description = "The catalog name", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "200", //
			description = "DTO which serves as a wrapper object that contains a list of PageRestrictionListData, never null.") //
	public PageRestrictionListData getAllPagesRestrictions()
	{
		final List<PageRestrictionData> convertedPageRestrictions = getDataMapper()
				.mapAsList(getPageRestrictionFacade().getAllPagesRestrictions(), PageRestrictionData.class, null);

		final PageRestrictionListData pageRestrictionList = new PageRestrictionListData();
		pageRestrictionList.setPageRestrictionList(convertedPageRestrictions);
		return pageRestrictionList;
	}

	/**
	 * @deprecated since 1905, please use {@link CMSItemController} instead.
	 */
	@Deprecated(since = "1905", forRemoval = true)
	@GetMapping(params =
	{ "pageIds" })
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@Operation(summary = "Gets restrictions for page ids.", description = "Retrieves a list of available restrictions that belong to the page for the given page ids.",
				operationId = "getAllPagesRestrictionsByPageIds")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "catalogId", description = "The catalog name", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "200", //
			description = "DTO which serves as a wrapper object that contains a list of PageRestrictionListData, never null") //
	public PageRestrictionListData getAllPagesRestrictionsByPageIds(
			@Parameter(description = "The list of page identifiers", required = true)
			@RequestParam("pageIds")
			final List<String> pageIds)
	{
		final List<de.hybris.platform.cmsfacades.data.PageRestrictionData> pageRestrictions = getPageRestrictionFacade()
				.getAllPagesRestrictions().stream().filter(pageRestriction -> pageIds.contains(pageRestriction.getPageId()))
				.collect(Collectors.toList());
		final List<PageRestrictionData> convertedResult = getDataMapper().mapAsList(pageRestrictions, PageRestrictionData.class,
				null);

		final PageRestrictionListData pageRestrictionList = new PageRestrictionListData();
		pageRestrictionList.setPageRestrictionList(convertedResult);
		return pageRestrictionList;
	}

	/**
	 * @deprecated since 1905, please use {@link CMSItemController#getCMSItemByUUid(String)} using the page UUID instead.
	 */
	@Deprecated(since = "1905", forRemoval = true)
	@GetMapping(params =
	{ "pageId" })
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@Operation(summary = "Gets restrictions by page.", description = "Retrieves all restrictions that belong to the page for the given page id.", operationId = "getRestrictionsByPage")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "catalogId", description = "The catalog name", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "200", //
			description = "DTO which serves as a wrapper object that contains a list of PageRestrictionListData, never null") //
	public PageRestrictionListData getRestrictionsByPage( //
			@Parameter(description = "Identifier of the page", required = true)
			@RequestParam("pageId")
			final String pageId)
	{
		return getAllRestrictionsByPage(pageId);
	}

	protected PageRestrictionListData getAllRestrictionsByPage(final String pageId)
	{
		final PageRestrictionListData pageRestrictionList = new PageRestrictionListData();

		try
		{
			final List<PageRestrictionData> convertedRestrictions = getDataMapper()
					.mapAsList(getPageRestrictionFacade().getRestrictionsByPage(pageId), PageRestrictionData.class, null);
			pageRestrictionList.setPageRestrictionList(convertedRestrictions);
		}
		catch (final CMSItemNotFoundException e)
		{
			pageRestrictionList.setPageRestrictionList(Collections.emptyList());
			LOG.info(e.getMessage());
		}
		return pageRestrictionList;
	}

	/**
	 * @deprecated since 1905, please use {@link CMSItemController#getCMSItemByUUid(String)} using the page UUID instead.
	 */
	@Deprecated(since = "1905", forRemoval = true)
	@PutMapping(value = "/pages/{pageId}")
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@Operation(summary = "Updates page restrictions.", description = "Replaces an existing list of page-restriction relations with a new relations for the given page id.",
					operationId = "replacePageRestrictionListData")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "catalogId", description = "The catalog name", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "400", description = "If it cannot find the component (CMSItemNotFoundException) or if there is any validation error (WebserviceValidationException).")
	@ApiResponse(responseCode = "200", description = "DTO which serves as a wrapper object that contains the updated list of PageRestrictionListData, never null.") //
	public PageRestrictionListData updatePageRestrictionListData( //
			@Parameter(description = "Page identifier", required = true)
			@PathVariable("pageId")
			final String pageId, //
			@Parameter(description = "List of PageRestrictionData", required = true)
			@RequestBody
			final PageRestrictionListData pageRestrictionListData //
	) throws CMSItemNotFoundException
	{
		try
		{
			final List<de.hybris.platform.cmsfacades.data.PageRestrictionData> convertedPageRestrictions = //
					getDataMapper().mapAsList(pageRestrictionListData.getPageRestrictionList(),
							de.hybris.platform.cmsfacades.data.PageRestrictionData.class, null);
			getPageRestrictionFacade().updateRestrictionRelationsByPage(pageId, convertedPageRestrictions);
			return getAllRestrictionsByPage(pageId);
		}
		catch (final ValidationException e)
		{
			LOG.debug(e.getMessage(), e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	protected PageRestrictionFacade getPageRestrictionFacade()
	{
		return pageRestrictionFacade;
	}

	public void setPageRestrictionFacade(final PageRestrictionFacade pageRestrictionFacade)
	{
		this.pageRestrictionFacade = pageRestrictionFacade;
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
