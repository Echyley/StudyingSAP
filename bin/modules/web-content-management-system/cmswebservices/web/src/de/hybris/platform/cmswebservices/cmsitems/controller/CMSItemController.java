/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.cmsitems.controller;

import static com.google.common.collect.Maps.newHashMap;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_UUID;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade;
import de.hybris.platform.cmsfacades.data.CMSItemSearchData;
import de.hybris.platform.cmsfacades.data.CMSVersionData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.version.CMSVersionFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.dto.CMSItemSearchWsDTO;
import de.hybris.platform.cmswebservices.dto.CMSItemUuidListWsDTO;
import de.hybris.platform.cmswebservices.dto.PageableWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.dto.PaginationWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;
import de.hybris.platform.audit.AuditableActions;
import de.hybris.platform.audit.AuditableActions.Action;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;


/**
 * Generic controller to deal with CMS items (Components, Pages, Restrictions, etc...). Any item that extends CMSItem is
 * supported using this interface.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/sites/{siteId}/cmsitems")
@Tag(name = "cmsitems")
public class CMSItemController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CMSItemController.class);
	public static final String VALIDATION_EXCEPTION = "Validation exception";

	@Resource
	private CMSItemFacade cmsItemFacade;

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

	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(summary = "Gets CMS Item by uuid.", description = "Retrieves an item that matches the given item uuid (Universally Unique Identifier).",
			operationId = "getCMSItemByUUId")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	//
	@ApiResponse(responseCode = "400", description = "When the item has not been found (CMSItemNotFoundException) "
			+ "or if there is a conversion error (ConversionException)")
	@ApiResponse(responseCode = "200", description = "Map&lt;String, Object&gt; representation of the CMS Item object.")
	public Map<String, Object> getCMSItemByUUid(
			@Parameter(description = "The universally unique identifier of the item. The uuid is a composed key formed by "
					+ "the cms item uid + the catalog + the catalog version.", required = true) //
			@PathVariable final String uuid,
			@Parameter(description = "Response configuration (list of fields, which should be returned in response)")
			@RequestParam(required = false) final String fields) throws CMSItemNotFoundException
	{
		return getCmsItemFacade().getCMSItemByUuid(uuid, fields);
	}

	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET, params =
			{ "versionId" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(summary = "Gets CMS Item by uuid.", description = "Retrieves an item that matches the given item uuid (Universally Unique Identifier).",
			operationId = "getCMSItemByUUIdAndVersion")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "400", description = " If there is any validation error (WebserviceValidationException).")
	@ApiResponse(responseCode = "200", description = "Map&lt;String, Object&gt; representation of the CMS Item object.")
	public Map<String, Object> getCMSItemByUUidAndVersion(@Parameter(description = "The uid of the cms version.", required = false)
	@RequestParam("versionId") final String versionId,
			@Parameter(description = "The universally unique identifier of the item. The uuid is a composed key formed by "
					+ "the cms item uid + the catalog + the catalog version.", required = true) //
			@PathVariable final String uuid)
	{
		try
		{
			final CMSVersionData cmsVersionData = getCmsVersionDataDataFactory().getObject();
			cmsVersionData.setItemUUID(uuid);
			cmsVersionData.setUid(versionId);

			return getCmsVersionFacade().getItemByVersion(cmsVersionData);
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationErrors());
		}
	}

	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Removes CMS Item.", description = "Deletes a specific instance of the content item (CMSItem) from the system by uuid.",
			operationId = "removeCMSItemByUUId")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "400", description = "If it cannot find the content item (CMSItemNotFoundException).")
	public void removeCMSItembyUUid( //
			@Parameter(description = "The universally unique identifier of the item", required = true) //
			@PathVariable final String uuid) throws CMSItemNotFoundException
	{
		getCmsItemFacade().deleteCMSItemByUuid(uuid);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Operation(summary = "Creates CMS Item.", description = "Creates a new CMS Item for a given map.",
			operationId = "createCMSItem")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "400", description = "If it cannot find the content item (CMSItemNotFoundException) "
			+ "or if there is any validation error (WebserviceValidationException).")
	@ApiResponse(responseCode = "201", description = "The multi-level Map representing the newly created CMS Item.")
	public Map<String, Object> createCMSItem( //
			@Parameter(description = "Map representing the CMS item to create", required = true) //
			@RequestBody final Map<String, Object> inputMap, //
			final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{
		try
		{
			final Map<String, Object> outputMap = getCmsItemFacade().createItem(inputMap);
			response.addHeader(CmswebservicesConstants.HEADER_LOCATION,
					getLocationHeaderResource().createLocationForChildResource(request, outputMap.get(FIELD_UUID)));
			return outputMap;
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationErrors());
		}
	}

	@RequestMapping(method = RequestMethod.POST, params =
			{ "dryRun=true" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(summary = "Validates CMS Item for creation.", description = "Performs validation on the specific instance of the CMS Item in a Dry Run mode.",
			operationId = "validateCMSItemForCreation")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "400", description = "If it cannot find the content item (CMSItemNotFoundException) "
			+ "or if there is any validation error (WebserviceValidationException).")
	@ApiResponse(responseCode = "200", description = "The multi-level Map representing the validated CMS Item.")
	public Map<String, Object> validateCMSItemForCreation(
			@Parameter(description = "When set to TRUE, the request is executed in Dry Run mode", required = true) //
			@RequestParam("dryRun") final Boolean dryRun,
			@Parameter(description = "Map representing the CMS item to create in Dry Run mode", required = true) //
			@RequestBody final Map<String, Object> inputMap, //
			final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{
		try
		{
			return getCmsItemFacade().validateItemForCreate(inputMap);
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationErrors());
		}
	}

	@RequestMapping(value = "/{uuid}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(summary = "Updates CMS Item.", description = "Updates a CMS Item for a given site id.",
			operationId = "replaceCMSItem")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "400", description = "If it cannot find the content item (CMSItemNotFoundException) "
			+ "or if there is any validation error (WebserviceValidationException).")
	@ApiResponse(responseCode = "200", description = "The multi-level Map representing the updated CMS Item.")
	public Map<String, Object> updateCMSItem( //
			@Parameter(description = "Unique Identifier of a CMS Item", required = true) //
			@PathVariable final String uuid, //
			@Parameter(description = "Map representing the CMS item to update", required = true) //
			@RequestBody final Map<String, Object> inputMap) throws CMSItemNotFoundException
	{
		try
		{
			final Map<String, Object> validMap = getCmsItemFacade().updateItem(uuid, inputMap);
			final User user = JaloSession.getCurrentSession().getSessionContext().getUser();
			Action currentAction = AuditableActions.withAttribute("actionUUID", uuid)
					.withAttribute("userId", user.getPK())
					.action("SmartEdit-CMSItem-Change-Audit");
			AuditableActions.audit(currentAction);
			return validMap;
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationErrors());
		}
	}

	@RequestMapping(value = "/{uuid}", method = RequestMethod.PUT, params =
			{ "dryRun=true" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(summary = "Validates CMS Item for update.", description = "Performs validation steps on a CMS Item in a Dry Run mod.",
			operationId = "validateCMSItemForUpdate")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "400", description = "If it cannot find the content item (CMSItemNotFoundException) "
			+ "or if there is any validation error (WebserviceValidationException).")
	@ApiResponse(responseCode = "200", description = "The multi-level Map representing the validated CMS Item.")
	public Map<String, Object> validateCMSItemForUpdate( //
			@Parameter(description = "When set to TRUE, the request is executed in Dry Run mode", required = true) //
			@RequestParam("dryRun") final Boolean dryRun,
			@Parameter(description = "Unique Identifier of a CMS Item", required = true) //
			@PathVariable final String uuid,
			@Parameter(description = "Map representing the CMS item to validate", required = true) //
			@RequestBody final Map<String, Object> inputMap) throws CMSItemNotFoundException
	{
		try
		{
			return getCmsItemFacade().validateItemForUpdate(uuid, inputMap);
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationErrors());
		}
	}

	@RequestMapping(value = "/uuids", method = RequestMethod.POST)
	@ResponseBody
	@Operation(summary = "Finds cms items by uuids in body.", description = "Retrieves a list of available CMSItems matching the given uuids by POSTing the uuids in the request body.",
			operationId = "searchCMSItemByUUId")
	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "400", description = "If it cannot find one of the items (CMSItemNotFoundException).")
	@ApiResponse(responseCode = "200", description =
			"A map list of elements in the form of Map&lt;String, Object&gt;, "
					+ "each representing a CMSItem.")
	public Map<String, Object> findCmsItemsByUuidsInBody(
			@Parameter(description = "CMSItemUuidListWsDTO", required = true)
			@RequestBody final CMSItemUuidListWsDTO dto) throws CMSItemNotFoundException
	{
		final List<Map<String, Object>> searchResults = getCmsItemFacade().findCMSItems(dto.getUuids(), dto.getFields());
		return Collections.singletonMap(CmswebservicesConstants.WSDTO_RESPONSE_PARAM_RESULTS, searchResults);
	}

	@RequestMapping(method = RequestMethod.GET, params =
			{ "pageSize", "currentPage" })
	@ResponseBody
	@Operation(summary = "Finds CMS items.", description = "Retrieves a list of available CMS items that match pages search.", operationId = "getCMSItemByPagedSearch")
	@ApiResponse(responseCode = "400", description = "If the required fields are missing (WebserviceValidationException).")
	@ApiResponse(responseCode = "200", description = "A map of paging info and results. Results are in the form of "
			+ "Map&lt;String, Object&gt;, each representing a CMSItem. Never null.")

	@Parameter(name = "siteId", description = "The site identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "pageSize", description = "Page size for paging", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "currentPage", description = "The current result page requested", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "catalogVersion", description = "CatalogVersion on which to search", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "mask", description = "Search mask applied to the UID and NAME fields, Uses partial matching", schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "typeCode", description = "TypeCode filter. Exact matches only. Either typeCode or typeCodes can be set.", schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "typeCodes", description = "Search using a comma separated list of type code. Either typeCode or typeCodes can be set.", schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "itemSearchParams", description = "Search on additional fields using a comma separated list of field name "
			+ "and value pairs which are separated by a colon. Exact matches only. You can use {@code null} as value.", schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "sort", description = "The requested ordering for the search results.", schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "fields", description = "Response configuration (list of fields, which should be returned in response", schema = @Schema(type = "string"), in = ParameterIn.QUERY)

	public Map<String, Object> findCmsItems( //
			@Parameter(description = "CMS Item search DTO", required = true) //
			@ModelAttribute final CMSItemSearchWsDTO cmsItemSearchInfo, //
			@Parameter(description = "Pageable DTO", required = true) //
			@ModelAttribute final PageableWsDTO pageableInfo)
	{
		final Map<String, Object> results = newHashMap();
		try
		{
			final PageableData pageableData = getDataMapper().map(pageableInfo, PageableData.class);
			final CMSItemSearchData cmsItemSearchData = getDataMapper().map(cmsItemSearchInfo, CMSItemSearchData.class);

			final SearchResult<Map<String, Object>> searchResults = getCmsItemFacade().findCMSItems(cmsItemSearchData, pageableData);

			final PaginationWsDTO paginationInfo = getWebPaginationUtils().buildPagination(searchResults);

			results.put(CmswebservicesConstants.WSDTO_RESPONSE_PARAM_RESULTS, searchResults.getResult());
			results.put(CmswebservicesConstants.WSDTO_RESPONSE_PARAM_PAGINATION, paginationInfo);
		}
		catch (final ValidationException e)
		{
			LOGGER.info(VALIDATION_EXCEPTION, e);
			throw new WebserviceValidationException(e.getValidationObject());
		}

		return results;
	}

	protected CMSItemFacade getCmsItemFacade()
	{
		return cmsItemFacade;
	}

	public void setCmsItemFacade(final CMSItemFacade cmsItemFacade)
	{
		this.cmsItemFacade = cmsItemFacade;
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

	protected CMSVersionFacade getCmsVersionFacade()
	{
		return cmsVersionFacade;
	}

	public void setCmsVersionFacade(final CMSVersionFacade cmsVersionFacade)
	{
		this.cmsVersionFacade = cmsVersionFacade;
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
