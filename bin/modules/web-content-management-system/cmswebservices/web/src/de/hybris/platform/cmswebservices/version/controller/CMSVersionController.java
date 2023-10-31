/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.version.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.exceptions.CMSVersionNotFoundException;
import de.hybris.platform.cmsfacades.data.CMSVersionData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.version.CMSVersionFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.dto.CMSVersionListWsDTO;
import de.hybris.platform.cmswebservices.dto.CMSVersionWsDTO;
import de.hybris.platform.cmswebservices.dto.PageableWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
 * Controller to deal with versions
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/cmsitems/{itemUUID}/versions")
@Tag(name = "versions")
public class CMSVersionController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CMSVersionController.class);

	private static final String VALIDATION_EXCEPTION = "Validation exception";

	@Resource
	private CMSVersionFacade cmsVersionFacade;

	@Resource
	private DataMapper dataMapper;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	@Resource
	private LocationHeaderResource locationHeaderResource;

	@Resource
	private ObjectFactory<CMSVersionData> cmsVersionDataDataFactory;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation( //
			summary = "Gets all versions for an item filtered by a mask.", description = "Retrieves a list of available CMSVersions "
					+ "by a search mask for the item identified by its itemUUID.", operationId = "getVersionsForItem")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "pageSize", description = "Page size for paging", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "currentPage", description = "The current result page requested", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "itemUUID", description = "The uuid of the item", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "400", description = "When the item has not been found (CMSItemNotFoundException)")
	@ApiResponse(responseCode = "200", description = "DTO which serves as a wrapper object that contains a list of "
			+ "CMSVersionWsDTO; never null")
	public CMSVersionListWsDTO findVersionsForItem(
			@Parameter(description = "The universally unique identifier of the item. The uuid is a composed key formed by "
					+ "the cms item uid + the catalog + the catalog version.", required = true) //
			@PathVariable
			final String itemUUID, //
			@Parameter(description = "Search mask applied to the LABEL field only. Uses partial matching.", required = false) //
			@RequestParam(required = false)
			final String mask, //
			@Parameter(description = "PageableWsDTO", hidden = true) //
			@ModelAttribute
			final PageableWsDTO pageableInfo) throws CMSItemNotFoundException
	{
		final PageableData pageableData = getDataMapper().map(pageableInfo, PageableData.class);
		final SearchResult<CMSVersionData> searchResult = getCmsVersionFacade().findVersionsForItem(itemUUID, mask, pageableData);

		final CMSVersionListWsDTO cmsVersionListWsDTO = new CMSVersionListWsDTO();
		cmsVersionListWsDTO.setResults(getDataMapper().mapAsList(searchResult.getResult(), CMSVersionWsDTO.class, null));
		cmsVersionListWsDTO.setPagination(getWebPaginationUtils().buildPagination(searchResult));

		return cmsVersionListWsDTO;
	}

	@RequestMapping(value = "/{versionId}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation( //
			summary = "Gets a specific version for an item.", description = "Retrieves a CMSVersion identified "
					+ "by its uid and for the item identified by its itemUUID.", operationId = "getVersion")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "itemUUID", description = "The uuid of the item", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "400", description = "When the version has not been found (CMSVersionNotFoundException)")
	@ApiResponse(responseCode = "200", description = "The dto containing version info.")
	public CMSVersionWsDTO getVersion( //
			@Parameter(description = "The uid of the cms version.", required = true) //
			@PathVariable
			final String versionId) throws CMSVersionNotFoundException
	{
		return getDataMapper().map(getCmsVersionFacade().getVersion(versionId), CMSVersionWsDTO.class);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Operation( //
			summary = "Creates a CMSVersion for an item.", //
			description = "Generates a new instance of the CMSVersion for the item identified by its itemUUID.",
			operationId = "createVersion")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "itemUUID", description = "The uuid of the item", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "400", description = "If there is any validation error (WebserviceValidationException).")
	@ApiResponse(responseCode = "201", description = "The dto containing version info.")
	public CMSVersionWsDTO createVersion(
			@Parameter(description = "The universally unique identifier of the item. The uuid is a composed key formed by "
					+ "the cms item uid + the catalog + the catalog version.", required = true) //
			@PathVariable
			final String itemUUID, //
			@Parameter(description = "The DTO object containing the label and description", required = true) //
			@RequestBody
			final CMSVersionWsDTO dto, //
			final HttpServletRequest request, final HttpServletResponse response)
	{
		try
		{
			final CMSVersionData cmsVersionData = getCmsVersionDataDataFactory().getObject();
			cmsVersionData.setItemUUID(itemUUID);
			cmsVersionData.setLabel(dto.getLabel());
			cmsVersionData.setDescription(dto.getDescription());

			final CMSVersionData newVersionData = getCmsVersionFacade().createVersion(cmsVersionData);
			response.addHeader(CmswebservicesConstants.HEADER_LOCATION,
					getLocationHeaderResource().createLocationForChildResource(request, newVersionData.getUid()));

			return getDataMapper().map(newVersionData, CMSVersionWsDTO.class);
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@RequestMapping(value = "/{versionId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation( //
			summary = "Updates a CMSVersion for an item.", description = "Replaces an existing CMSVersion identified by its uid for the item identified\n" +
			"by its itemUUID.", operationId = "replaceVersion")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "itemUUID", description = "The uuid of the item", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "400", description = "If there is any validation error (WebserviceValidationException).")
	@ApiResponse(responseCode = "200", description = "The dto containing version info.")
	public CMSVersionWsDTO updateVersion(
			@Parameter(description = "The universally unique identifier of the item. The uuid is a composed key formed by "
					+ "the cms item uid + the catalog + the catalog version.", required = true) //
			@PathVariable
			final String itemUUID, //
			@Parameter(description = "The uid of the cms version.", required = true) //
			@PathVariable
			final String versionId, //
			@Parameter(description = "The DTO object containing the label and description", required = true) //
			@RequestBody
			final CMSVersionWsDTO dto)
	{
		try
		{
			final CMSVersionData cmsVersionData = getCmsVersionDataDataFactory().getObject();
			cmsVersionData.setUid(versionId);
			cmsVersionData.setItemUUID(itemUUID);
			cmsVersionData.setLabel(dto.getLabel());
			cmsVersionData.setDescription(dto.getDescription());

			return getDataMapper().map(getCmsVersionFacade().updateVersion(cmsVersionData), CMSVersionWsDTO.class);
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@PostMapping(value = "/{versionId}/rollbacks")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation( //
			summary = "Rolls back an item to a specific CMSVersion.", description = "Sets a CMSversion of the item identified by its itemUUID to a previously\n" +
			"saved CMSVersion.", operationId = "doRollbackVersion")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "itemUUID", description = "The uuid of the item", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "400", description = "If there is any validation error (WebserviceValidationException).")
	@ApiResponse(responseCode = "204", description = "No Content")
	public void rollbackVersion(
			@Parameter(description = "The universally unique identifier of the item. The uuid is a composed key formed by "
					+ "the cms item uid + the catalog + the catalog version.", required = true) //
			@PathVariable
			final String itemUUID, //
			@Parameter(description = "The uid of the cms version.", required = true) //
			@PathVariable
			final String versionId)
	{
		try
		{
			final CMSVersionData cmsVersionData = getCmsVersionDataDataFactory().getObject();
			cmsVersionData.setItemUUID(itemUUID);
			cmsVersionData.setUid(versionId);

			getCmsVersionFacade().rollbackVersion(cmsVersionData);
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@RequestMapping(value = "/{versionId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ResponseBody
	@Operation( //
			summary = "Deletes a CMSVersion for an item.", //
			description = "Removes a specific instance of the CMSVersion identified by its uid and for the item identified\n" +
					"by its itemUUID.",
			operationId = "removeVersion")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "itemUUID", description = "The uuid of the item", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	@ApiResponse(responseCode = "400", description = "If there is any validation error (WebserviceValidationException).")
	@ApiResponse(responseCode = "204", description = "No Content")
	public void deleteVersion(@Parameter(description = "The universally unique identifier of the item. The uuid is a composed key formed "
			+ "by the cms item uid + the catalog + the catalog version.", required = true) //
	@PathVariable
	final String itemUUID, //
			@Parameter(description = "The uid of the cms version.", required = true) //
			@PathVariable
			final String versionId)
	{
		try
		{
			final CMSVersionData cmsVersionData = getCmsVersionDataDataFactory().getObject();
			cmsVersionData.setUid(versionId);
			cmsVersionData.setItemUUID(itemUUID);

			getCmsVersionFacade().deleteVersion(cmsVersionData);
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	protected CMSVersionFacade getCmsVersionFacade()
	{
		return cmsVersionFacade;
	}

	public void setCmsVersionFacade(final CMSVersionFacade cmsVersionFacade)
	{
		this.cmsVersionFacade = cmsVersionFacade;
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

	protected LocationHeaderResource getLocationHeaderResource()
	{
		return locationHeaderResource;
	}

	public void setLocationHeaderResource(final LocationHeaderResource locationHeaderResource)
	{
		this.locationHeaderResource = locationHeaderResource;
	}

	protected ObjectFactory<CMSVersionData> getCmsVersionDataDataFactory()
	{
		return cmsVersionDataDataFactory;
	}

	public void setCmsVersionDataDataFactory(final ObjectFactory<CMSVersionData> cmsVersionDataDataFactory)
	{
		this.cmsVersionDataDataFactory = cmsVersionDataDataFactory;
	}

}
