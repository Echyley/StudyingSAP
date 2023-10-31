/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.media.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cmsfacades.dto.MediaFileDto;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.media.MediaFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.MediaData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;

/**
 * Controller that provides media.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/catalogs/{catalogId}/versions/{versionId}" + "/media")
@Tag(name = "catalog version media")
public class CatalogVersionMediaController
{
	@Resource
	private MediaFacade mediaFacade;

	@Resource
	private LocationHeaderResource locationHeaderResource;

	@Resource
	private DataMapper dataMapper;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Operation(summary = "Uploads media.", description = "Provides a new multipart media item for a given catalogId.", operationId = "doUploadMultipartMedia")

	@ApiResponse(responseCode = "400", description = "When an error occurs parsing the MultipartFile (IOException) or when the media query parameters provided contain validation errors (WebserviceValidationException)")
	@ApiResponse(responseCode = "200", description = "The newly created Media item")

	@Parameter(name = "altText", description = "The alternative text to use for the newly created media.", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "code", description = "The code to use for the newly created media.", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "description", description = "The description to use for the newly created media.", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "mime", description = "Internet Media Type for the media file.", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "folder", description = "The folder that media will be update to", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	public MediaData uploadMultipartMedia(
			@Parameter(description = "The unique identifier of the catalog for which to link the new media.", required = true) //
			@PathVariable("catalogId") final String catalogId,
			@Parameter(description = "The specific catalog version to which the new media will be associated to.", required = true) //
			@PathVariable("versionId") final String versionId,
			@Parameter(description = "The MediaData containing the data for the associated media item to be created.", hidden = true) //
			@ModelAttribute("media") final MediaData media,
			@Parameter(description = "The file representing the actual binary contents of the media to be created.", required = true) //
			@RequestParam("file") final MultipartFile multiPart,
			@Parameter(description = "The folder that media will be update to", required = true) //
			@RequestParam("folder") final String folder,
			final HttpServletRequest request, final HttpServletResponse response) throws IOException
	{
		media.setCatalogId(catalogId);
		media.setCatalogVersion(versionId);

		if (!StringUtils.isBlank(request.getHeader("fileSize")) && Long.valueOf(request.getHeader("fileSize")) != multiPart.getSize()) {
			throw new IllegalArgumentException("An unexpected error occurred in the upload. Please try again later.");
		}

		try
		{
			final de.hybris.platform.cmsfacades.data.MediaData convertedMediaData = //
					getDataMapper().map(media, de.hybris.platform.cmsfacades.data.MediaData.class);
			final de.hybris.platform.cmsfacades.data.MediaData newMedia = //
					getMediaFacade()
							.addMediaToFolder(convertedMediaData, getFile(multiPart, multiPart.getInputStream()), folder);

			response.addHeader(CmswebservicesConstants.HEADER_LOCATION,
					getLocationHeaderResource().createLocationForChildResource(request, newMedia.getCode()));
			IOUtils.closeQuietly(multiPart.getInputStream());
			return getDataMapper().map(newMedia, MediaData.class);
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	/**
	 * Create a new media file DTO from the {@code MultipartFile}.
	 *
	 * @param file
	 * 		- a Spring {@code MultipartFile}
	 * @param inputStream
	 * 		- an input stream used to read the file
	 * @return a media file DTO
	 */
	public MediaFileDto getFile(final MultipartFile file, final InputStream inputStream)
	{
		final MediaFileDto mediaFile = new MediaFileDto();
		mediaFile.setInputStream(inputStream);
		mediaFile.setName(file.getOriginalFilename());
		mediaFile.setSize(file.getSize());
		mediaFile.setMime(file.getContentType());
		return mediaFile;
	}

	protected MediaFacade getMediaFacade()
	{
		return mediaFacade;
	}

	public void setMediaFacade(final MediaFacade mediaFacade)
	{
		this.mediaFacade = mediaFacade;
	}

	protected LocationHeaderResource getLocationHeaderResource()
	{
		return locationHeaderResource;
	}

	public void setLocationHeaderResource(final LocationHeaderResource locationHeaderResource)
	{
		this.locationHeaderResource = locationHeaderResource;
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
