/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.media.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.media.MediaFacade;
import de.hybris.platform.cmswebservices.controller.AbstractSearchableController;
import de.hybris.platform.cmswebservices.data.MediaData;
import de.hybris.platform.cmswebservices.data.MediaListData;
import de.hybris.platform.cmswebservices.data.NamedQueryData;
import de.hybris.platform.cmswebservices.dto.CMSItemUuidListWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller that handles searching for media.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/media")
@Tag(name = "media")
public class MediaController extends AbstractSearchableController
{
	private static Logger LOGGER = LoggerFactory.getLogger(MediaController.class);

	@Resource
	private MediaFacade mediaFacade;

	@Resource
	private DataMapper dataMapper;

	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	@Operation(summary = "Gets media by uuid.", description = "Finds a specific media item that matches given uuid.",
					operationId = "getMediaByUuid")

	@ApiResponse(responseCode = "404", description = "When no media is found matching the given uuid (MediaNotFoundException).")
	@ApiResponse(responseCode = "200", description = "Media data")
	public MediaData getMediaByUuid( //
			@Parameter(description = "The universally unique identifier of the media item", required = true) //
			@PathVariable final String uuid)
	{
		final de.hybris.platform.cmsfacades.data.MediaData media = getMediaFacade().getMediaByUUID(uuid);

		return getDataMapper().map(media, MediaData.class);
	}

	@PostMapping(value = "/uuids")
	@ResponseBody
	@Operation(summary = "Gets a list of media by uuids.", description = "Retrieves a list of media items that match the given uuids by POSTing the uuids in the request body",
					operationId = "getMediaByUuids")

	@ApiResponse(responseCode = "404", description = "When one of the media cannot be found (MediaNotFoundException).")
	@ApiResponse(responseCode = "200", description = "A list of media data")
	public MediaListData getMediaByUuids( //
			@Parameter(description = "List of uuids representing the media to retrieve", required = true) //
			@RequestBody final CMSItemUuidListWsDTO uuids)
	{
		final MediaListData mediaList = new MediaListData();
		final List<MediaData> mediaDataList = getDataMapper().mapAsList(getMediaFacade().getMediaByUUIDs(uuids.getUuids()),
				MediaData.class, null);
		mediaList.setMedia(mediaDataList);

		return mediaList;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@Operation(summary = "Gets media by query.", description = "Gets a specific media item by named query.", operationId = "getMediaByQuery")

	@ApiResponse(responseCode = "400", description = "When the named query parameters provide contain validation errors")
	@ApiResponse(responseCode = "200", description = "A single page of query results as a list of media or an empty list (WebserviceValidationException).")

	@Parameter(name = "params",
			description = "The list of the filtering parameters for the namedQuery.\nEx:\"catalogId:catalogIdValue,catalogVersion:catalogVersionValue,code:codeValue\"",
			required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "currentPage", description = "The index of the requested page (index 0 means page 1).", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "pageSize", description = "The number of results per page.", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "namedQuery", description = "The name of the named query to use for the search.", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "sort", description = "The requested ordering for the search results.", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	public MediaListData getMediaByQuery(//
			@Parameter(description = "The name of the named query to use for the search.", hidden = true) //
			@ModelAttribute("namedQuery") final NamedQueryData namedQuery)
	{
		final MediaListData mediaList = new MediaListData();

		try
		{
			final de.hybris.platform.cmsfacades.data.NamedQueryData convertedNamedQuery = //
					getDataMapper().map(namedQuery, de.hybris.platform.cmsfacades.data.NamedQueryData.class);
			final List<MediaData> mediaDataList = getMediaFacade().getMediaByNamedQuery(convertedNamedQuery).stream()
					.map(media -> getDataMapper().map(media, MediaData.class)) //
					.collect(Collectors.toList());
			mediaList.setMedia(mediaDataList);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
		return mediaList;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	protected MediaFacade getMediaFacade()
	{
		return mediaFacade;
	}

	public void setMediaFacade(final MediaFacade mediaFacade)
	{
		this.mediaFacade = mediaFacade;
	}
}
