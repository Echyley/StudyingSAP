/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmssmarteditwebservices.products.controller;

import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.API_VERSION;
import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.DEFAULT_CURRENT_PAGE;
import static de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants.DEFAULT_PAGE_SIZE;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmssmarteditwebservices.data.ProductData;
import de.hybris.platform.cmssmarteditwebservices.dto.PageableWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.ProductSearchResultWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.ProductWsDTO;
import de.hybris.platform.cmssmarteditwebservices.products.facade.ProductSearchFacade;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Controller to search Products within a Product Catalog Version.
 */
@Controller
@RequestMapping(API_VERSION + "/productcatalogs/{catalogId}/versions/{versionId}/products")
@Tag(name = "products")
public class ProductSearchController
{
	@Resource
	private ProductSearchFacade cmsSeProductSearchFacade;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	@Resource
	private DataMapper dataMapper;

	@GetMapping
	@ResponseBody
	@Operation(operationId = "getProductsByTextOrMask", summary = "Find products by text or mask", description = "Endpoint to retrieve products using a free text search field.")
	@ApiResponse(responseCode = "200", description = "DTO which serves as a wrapper object that contains a list of ProductData, never null") //
	@Parameter(name = "catalogId", description = "The uid of the catalog", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The uid of the catalog version", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "pageSize", description = "The maximum number of elements in the result list.", schema = @Schema(type = "string", defaultValue = DEFAULT_PAGE_SIZE), in = ParameterIn.QUERY)
	@Parameter(name = "currentPage", description = "The requested page number", schema = @Schema(type = "string", defaultValue = DEFAULT_CURRENT_PAGE), in = ParameterIn.QUERY)
	@Parameter(name = "sort", description = "The string field the results will be sorted with", schema = @Schema(type = "string"), in = ParameterIn.QUERY) //

	public ProductSearchResultWsDTO findProductsByTextOrMask(
			@Parameter(description = "The string value on which products will be filtered")
			@RequestParam(required = false)
			final String text, //
			@Parameter(description = "The string value on which products will be filtered if no text value is provided")
			@RequestParam(required = false)
			final String mask, //
			@Parameter(description = "The language iso code by which products will be filtered")
			@RequestParam(required = false)
			final String langIsoCode, //
			@ModelAttribute
			final PageableWsDTO pageableDto)
	{
		final String searchText = Strings.isNullOrEmpty(text) ? mask : text;
		final PageableData pageableData = Optional.of(pageableDto) //
				.map(pageableWsDTO -> getDataMapper().map(pageableWsDTO, PageableData.class)) //
				.orElse(null);
		final SearchResult<ProductData> productSearchResult = getCmsSeProductSearchFacade().findProducts(searchText, pageableData,
				langIsoCode);

		final ProductSearchResultWsDTO productList = new ProductSearchResultWsDTO();
		productList.setProducts(productSearchResult //
				.getResult() //
				.stream() //
				.map(productData -> getDataMapper().map(productData, ProductWsDTO.class)) //
				.collect(Collectors.toList()));
		productList.setPagination(getWebPaginationUtils().buildPagination(productSearchResult));
		return productList;
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
