/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.catalogs.controller;

import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.API_VERSION;

import de.hybris.platform.cmssmarteditwebservices.catalogs.CatalogFacade;
import de.hybris.platform.cmssmarteditwebservices.dto.CatalogListWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.CatalogWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;


/**
 * Controller to retrieve catalog information related to a given site.
 */
@RestController
@RequestMapping(API_VERSION + "/sites/{baseSiteId}")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@Tag(name = "catalogs")
public class CatalogController
{
	@Resource(name = "cmsSeCatalogFacade")
	private CatalogFacade catalogFacade;
	@Resource
	private DataMapper dataMapper;

	@GetMapping(value = "/contentcatalogs")
	@Operation(operationId = "getContentCatalogs", summary = "Get content catalogs", description = "Endpoint to retrieve content catalog information including the related catalog versions for all catalogs for a given site and its parents.")
	public CatalogListWsDTO getContentCatalogs( //
			@Parameter(description = "The site identifier", required = true)
			@PathVariable
			final String baseSiteId)
	{
		final List<CatalogWsDTO> catalogs = getDataMapper() //
				.mapAsList(getCatalogFacade().getContentCatalogs(baseSiteId), CatalogWsDTO.class, null);

		final CatalogListWsDTO catalogList = new CatalogListWsDTO();
		catalogList.setCatalogs(catalogs);
		return catalogList;
	}

	@GetMapping(value = "/productcatalogs")
	@Operation(operationId = "getProductCatalogs", summary = "Get product catalogs", description = "Endpoint to retrieve product catalog information including the related catalog versions for all catalogs for a given site")
	public CatalogListWsDTO getProductCatalogs( //
			@Parameter(description = "The site identifier", required = true)
			@PathVariable
			final String baseSiteId)
	{
		final List<CatalogWsDTO> catalogs = getDataMapper() //
				.mapAsList(getCatalogFacade().getProductCatalogs(baseSiteId), CatalogWsDTO.class, null);

		final CatalogListWsDTO catalogList = new CatalogListWsDTO();
		catalogList.setCatalogs(catalogs);
		return catalogList;
	}

	protected CatalogFacade getCatalogFacade()
	{
		return catalogFacade;
	}

	public void setCatalogFacade(final CatalogFacade catalogFacade)
	{
		this.catalogFacade = catalogFacade;
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
