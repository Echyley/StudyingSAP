/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.synchronization.controller;

import static de.hybris.platform.cms2.model.pages.AbstractPageModel._TYPECODE;
import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.data.ItemSynchronizationData;
import de.hybris.platform.cmsfacades.data.SyncItemStatusConfig;
import de.hybris.platform.cmsfacades.data.SyncItemStatusData;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.data.SynchronizationData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.synchronization.ItemSynchronizationFacade;
import de.hybris.platform.cmssmarteditwebservices.data.SyncItemStatusWsDTO;
import de.hybris.platform.cmssmarteditwebservices.data.SynchronizationWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Controller to retrieve complex synchronization status for and to perform a synchronization on a given
 * {@link AbstractPageModel}
 */
@Controller
@RequestMapping(API_VERSION
		+ "/sites/{baseSiteId}/catalogs/{catalogId}/versions/{versionId}/synchronizations/versions/{targetCatalogVersion}")
@Tag(name = "synchronizations")
public class SynchronizationController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizationController.class);

	@Resource
	private ItemSynchronizationFacade itemSynchronizationFacade;

	@Resource
	private SyncItemStatusConfig cmsSyncItemStatusConfig;

	@Resource
	private DataMapper dataMapper;

	@GetMapping(value = "/pages/{pageId}")
	@ResponseBody
	@Operation(operationId = "getSynchronizationStatus", summary = "Get synchronization status", description = "Will build the synchronization status of a page including detailed status of its content slots and their cms components.")
	@ApiResponse(responseCode = "200", description = "DTO containing the complex synchronization status of the AbstractPageModel page") //
	@ApiBaseSiteIdParam
	public SyncItemStatusWsDTO getSyncStatus( //
			@Parameter(description = "The catalog uid", required = true)
			@PathVariable
			final String catalogId,
			@Parameter(description = "The source catalog version from a synchronization perspective", required = true)
			@PathVariable
			final String versionId,
			@Parameter(description = "The target catalog version from a synchronization perspective", required = true)
			@PathVariable
			final String targetCatalogVersion,
			@Parameter(description = "The uid of the page from which to retrieve the synchronization status", required = true)
			@PathVariable
			final String pageId)
	{
		final SyncRequestData syncRequestData = buildSyncRequestData(catalogId, versionId, targetCatalogVersion);
		final ItemSynchronizationData itemSynchronizationData = new ItemSynchronizationData();
		itemSynchronizationData.setItemId(pageId);
		itemSynchronizationData.setItemType(_TYPECODE);

		final SyncItemStatusData syncItemStatus = getItemSynchronizationFacade().getSynchronizationItemStatus(syncRequestData,
				itemSynchronizationData, getCmsSyncItemStatusConfig());
		return getDataMapper().map(syncItemStatus, SyncItemStatusWsDTO.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ResponseBody
	@Operation(operationId = "performSynchronization", summary = "Perform synchronization", description = "Will perform synchronization status on a list of item identifier by their ItemSynchronizationWsDTO.")
	@ApiBaseSiteIdParam
	public void performSync(
			@Parameter(description = "The SynchronizationWsDTO containing the list of requested synchronizations", required = true)
			@RequestBody
			final SynchronizationWsDTO synchronizationWsDTO,
			@Parameter(description = "The catalog uid", required = true)
			@PathVariable
			final String catalogId,
			@Parameter(description = "The source catalog version from a synchronization perspective", required = true)
			@PathVariable
			final String versionId,
			@Parameter(description = "The target catalog version from a synchronization perspective", required = true)
			@PathVariable
			final String targetCatalogVersion)
	{
		try
		{
			final SyncRequestData syncRequestData = buildSyncRequestData(catalogId, versionId, targetCatalogVersion);
			final SynchronizationData synchronizationData = getDataMapper().map(synchronizationWsDTO, SynchronizationData.class);

			getItemSynchronizationFacade().performItemSynchronization(syncRequestData, synchronizationData);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	protected SyncRequestData buildSyncRequestData(final String catalogId, final String versionId,
			final String targetCatalogVersion)
	{
		final SyncRequestData syncRequestData = new SyncRequestData();
		syncRequestData.setCatalogId(catalogId);
		syncRequestData.setSourceVersionId(versionId);
		syncRequestData.setTargetVersionId(targetCatalogVersion);
		return syncRequestData;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	protected SyncItemStatusConfig getCmsSyncItemStatusConfig()
	{
		return cmsSyncItemStatusConfig;
	}

	protected ItemSynchronizationFacade getItemSynchronizationFacade()
	{
		return itemSynchronizationFacade;
	}
}
