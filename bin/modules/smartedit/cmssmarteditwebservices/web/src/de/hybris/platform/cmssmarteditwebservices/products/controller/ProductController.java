/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.products.controller;

import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.API_VERSION;

import de.hybris.platform.cmssmarteditwebservices.dto.ProductWsDTO;
import de.hybris.platform.cmssmarteditwebservices.products.facade.ProductSearchFacade;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Controller to retrieve Products by its item composed key.
 *
 * For more details about how to generate the item composed key, refer to the documentation about the
 * {@link de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService}.
 */
@Controller
@RequestMapping(API_VERSION + "/sites/{baseSiteId}/products")
@Tag(name = "products")
public class ProductController
{

	@Resource
	private ProductSearchFacade cmsSeProductSearchFacade;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	@Resource
	private DataMapper dataMapper;

	@GetMapping(value = "/{code}")
	@ResponseBody
	@Operation(operationId = "getProductByCode", summary = "Get product by code (uuid)", description = "Endpoint to retrieve a product that matches the given product code uuid.")
	@ApiResponse(responseCode = "400", description = "When the item has not been found (CMSItemNotFoundException) or when there was problem during conversion (ConversionException).")
	@ApiResponse(responseCode = "200", description = "Product data") //
	@ApiBaseSiteIdParam
	public ProductWsDTO getProductByCode( //
			@Parameter(description = "Product code (uuid)", required = true) //
			@PathVariable
			final String code)
	{
		return getDataMapper().map(getCmsSeProductSearchFacade().getProductByUid(code), ProductWsDTO.class);
	}

	protected ProductSearchFacade getCmsSeProductSearchFacade()
	{
		return cmsSeProductSearchFacade;
	}

	public void setCmsSeProductSearchFacade(final ProductSearchFacade cmsSeProductSearchFacade)
	{
		this.cmsSeProductSearchFacade = cmsSeProductSearchFacade;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	public void setWebPaginationUtils(final WebPaginationUtils webPaginationUtils)
	{
		this.webPaginationUtils = webPaginationUtils;
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
