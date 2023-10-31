/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.pagescontentslotstyperestrictions.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.CMSPageContentSlotListData;
import de.hybris.platform.cmsfacades.data.ContentSlotTypeRestrictionsData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.pagescontentslotstyperestrictions.PageContentSlotTypeRestrictionsFacade;
import de.hybris.platform.cmswebservices.dto.CMSContentSlotIdListWsDTO;
import de.hybris.platform.cmswebservices.dto.ContentSlotTypeRestrictionsWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


/**
 * Controller that provides type restrictions for CMS content slots.
 *
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/catalogs/{catalogId}/versions/{versionId}/pages/{pageId}")
@Tag(name = "page slot restrictions")
public class PageContentSlotTypeRestrictionController
{
	@Resource
	private PageContentSlotTypeRestrictionsFacade pageContentSlotTypeRestrictionsFacade;

	@Resource
	private DataMapper dataMapper;

	@RequestMapping(value = "/contentslots/{slotId}/typerestrictions", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Gets type restrictions for content slot.", description = "Retrieves a list of available type restrictions for a given page id and content slot id.",
					operationId = "getTypeRestrictionForContentSlot")

	@ApiResponse(responseCode = "400", description = "When the page/slot cannot be found (CMSItemNotFoundException)")
	@ApiResponse(responseCode = "200", description = "DTO providing the mapping")

	@Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "pageId", description = "The page identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "slotId", description = "The slot identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	public ContentSlotTypeRestrictionsWsDTO getTypeRestrictionsForContentSlot(
			@Parameter(description = "Page identifier", required = true) final @PathVariable String pageId,
			@Parameter(description = "Content slot identifier", required = true) final @PathVariable String slotId)
			throws CMSItemNotFoundException
	{
		final ContentSlotTypeRestrictionsData data = getPageContentSlotTypeRestrictionsFacade()
				.getTypeRestrictionsForContentSlotUID(pageId, slotId);

		return getDataMapper().map(data, ContentSlotTypeRestrictionsWsDTO.class);
	}

	@RequestMapping(value = "/typerestrictions", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Gets type restrictions for the provided content slots.", description = "Retrieves a list of available type restrictions for the given slotId's.",
			operationId = "searchTypeRestrictionBySlotIds")

	@ApiResponse(responseCode = "400", description = "When slot(s) for slotId(s) cannot be found (CMSItemNotFoundException)")
	@ApiResponse(responseCode = "200", description = "DTO providing the mapping")

	@Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "pageId", description = "The page identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	public List<ContentSlotTypeRestrictionsWsDTO> searchTypeRestrictionBySlotIds(
			@Parameter(description = "Page identifier", required = true) //
			final @PathVariable String pageId, //
			@Parameter(description = "Map representing the content slots for which to retrieve type restrictions", required = true) //
			@RequestBody CMSContentSlotIdListWsDTO data)
			throws CMSItemNotFoundException
	{
		try
		{
			final CMSPageContentSlotListData contentSlotListData = getDataMapper().map(data, CMSPageContentSlotListData.class);
			contentSlotListData.setPageId(pageId);

			return getPageContentSlotTypeRestrictionsFacade()
					.getTypeRestrictionsForContentSlots(contentSlotListData)
					.stream()
					.map(restriction -> getDataMapper().map(restriction, ContentSlotTypeRestrictionsWsDTO.class))
					.collect(Collectors.toList());
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}


	protected PageContentSlotTypeRestrictionsFacade getPageContentSlotTypeRestrictionsFacade()
	{
		return pageContentSlotTypeRestrictionsFacade;
	}

	public void setPageContentSlotTypeRestrictionsFacade(
			final PageContentSlotTypeRestrictionsFacade pageContentSlotTypeRestrictionsFacade)
	{
		this.pageContentSlotTypeRestrictionsFacade = pageContentSlotTypeRestrictionsFacade;
	}
}
