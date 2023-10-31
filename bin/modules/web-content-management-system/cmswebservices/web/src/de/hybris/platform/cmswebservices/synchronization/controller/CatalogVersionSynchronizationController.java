/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.synchronization.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.synchronization.SynchronizationFacade;
import de.hybris.platform.cmswebservices.data.SyncJobData;
import de.hybris.platform.cmswebservices.data.SyncJobRequestData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller that handles synchronization of catalogs
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/catalogs/{catalogId}")
@Tag(name = "catalog version synchronization")
public class CatalogVersionSynchronizationController
{

	@Resource
	private SynchronizationFacade synchronizationFacade;
	@Resource
	private DataMapper dataMapper;

	@GetMapping(value = "/versions/{sourceVersionId}/synchronizations/versions/{targetVersionId}")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@Operation(summary = "Gets synchronization status.", description = "Retrieves the status of the last synchronization for a catalog. Information is\n" +
			"retrieved based on a given catalog, source version and target version ids.",
					operationId = "getSynchronizationByCatalog")
	@ApiResponse(responseCode = "200", description = "The synchronization status")
	@Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "sourceVersionId", description = "Catalog version used as a starting point in this synchronization", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "targetVersionId", description = "Catalog version destination to be synchronized", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	public SyncJobData getSynchronizationByCatalogSourceTarget(
			@Parameter(description = "Contains the synchronization request data", required = true)
			@ModelAttribute
			final SyncJobRequestData syncJobRequest)
	{
		try
		{
			final SyncRequestData convertedSyncJobRequest = getDataMapper().map(syncJobRequest, SyncRequestData.class);

			final de.hybris.platform.cmsfacades.data.SyncJobData syncJobResult = getSynchronizationFacade()
					.getSynchronizationByCatalogSourceTarget(convertedSyncJobRequest);

			return getDataMapper().map(syncJobResult, SyncJobData.class);
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	/**
	 * @deprecated since 2005, please use {@link CatalogVersionSynchronizationController#requestNewSynchronizationByCatalogSourceTarget(SyncJobRequestData)} instead.
	 */
	@Deprecated(since = "2005", forRemoval = true)
	@PutMapping(value = "/versions/{sourceVersionId}/synchronizations/versions/{targetVersionId}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(summary = "Creates a catalog synchronization.", description = "Generates a brand new synchronization status. The status is generated based on a given catalog, source version and target version ids.",
			operationId = "replaceSynchronizationByCatalog")
	@ApiResponse(responseCode = "400", description = "When one of the catalogs does not exist (CMSItemNotFoundException).")
	@ApiResponse(responseCode = "200", description = "The synchronization status")
	@Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "sourceVersionId", description = "Catalog version used as a starting point in this synchronization", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "targetVersionId", description = "Catalog version destination to be synchronized", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	public SyncJobData createSynchronizationByCatalogSourceTarget(
			@Parameter(description = "Contains the synchronization request data", required = true)
			@ModelAttribute
			final SyncJobRequestData syncJobRequest) throws CMSItemNotFoundException
	{
		return requestNewSynchronizationByCatalogSourceTarget(syncJobRequest);
	}

	@PostMapping(value = "/versions/{sourceVersionId}/synchronizations/versions/{targetVersionId}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Operation(summary = "Creates a catalog synchronization.", description = "Generates a brand new synchronization status. The status is generated based on a given catalog, source version and target version ids.",
					operationId = "createNewSynchronizationByCatalog")
	@ApiResponse(responseCode = "400", description = "When one of the catalogs does not exist (CMSItemNotFoundException).")
	@ApiResponse(responseCode = "200", description = "The synchronization status")
	@Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "sourceVersionId", description = "Catalog version used as a starting point in this synchronization", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "targetVersionId", description = "Catalog version destination to be synchronized", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	public SyncJobData requestNewSynchronizationByCatalogSourceTarget(
			@Parameter(description = "Contains the synchronization request data", required = true)
			@ModelAttribute
			final SyncJobRequestData syncJobRequest) throws CMSItemNotFoundException
	{
		try
		{
			final SyncRequestData convertedSyncJobRequest = getDataMapper().map(syncJobRequest, SyncRequestData.class);

			final de.hybris.platform.cmsfacades.data.SyncJobData syncJobResult = getSynchronizationFacade()
					.createCatalogSynchronization(convertedSyncJobRequest);

			return getDataMapper().map(syncJobResult, SyncJobData.class);
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@GetMapping(value = "/synchronizations/targetversions/{targetVersionId}")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@Operation(summary = "Gets last synchronization by target catalog.", description = "Retrieves the status of the last synchronization job. Information is retrieved based on the catalog version target.",
					operationId = "getLastSynchronizationByCatalog")
	@ApiResponse(responseCode = "200", description = "The synchronization status")
	@Parameter(name = "catalogId", description = "The catalog id", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "targetVersionId", description = "Catalog version destination to be synchronized", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	public SyncJobData getLastSynchronizationByCatalogTarget(
			@Parameter(description = "Contains the synchronization request data", hidden = true)
			@ModelAttribute
			final SyncJobRequestData syncJobRequest)
	{
		try
		{
			final SyncRequestData convertedSyncJobRequest = getDataMapper().map(syncJobRequest, SyncRequestData.class);

			final de.hybris.platform.cmsfacades.data.SyncJobData syncJobResult = getSynchronizationFacade()
					.getLastSynchronizationByCatalogTarget(convertedSyncJobRequest);

			return getDataMapper().map(syncJobResult, SyncJobData.class);
		}
		catch (final ValidationException e)
		{
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	public SynchronizationFacade getSynchronizationFacade()
	{
		return synchronizationFacade;
	}

	public void setSynchronizationFacade(final SynchronizationFacade synchronizationFacade)
	{
		this.synchronizationFacade = synchronizationFacade;
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
