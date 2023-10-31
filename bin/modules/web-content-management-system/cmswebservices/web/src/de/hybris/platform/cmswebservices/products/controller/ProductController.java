/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmswebservices.products.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmsfacades.data.ProductData;
import de.hybris.platform.cmsfacades.products.ProductSearchFacade;
import de.hybris.platform.cmswebservices.dto.PageableWsDTO;
import de.hybris.platform.cmswebservices.dto.ProductDataListWsDTO;
import de.hybris.platform.cmswebservices.dto.ProductWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Controller to retrieve and search Products within a Product Catalog Version.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/productcatalogs/{catalogId}/versions/{versionId}/products")
@Tag(name = "products")
public class ProductController
{

	@Resource
	private ProductSearchFacade cmsProductSearchFacade;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	@Resource
	private DataMapper dataMapper;

	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@ResponseBody
	@Operation(summary = "Gets product by code.", description = "Retrieves a specific product item that matches given product code.",
			operationId = "getProductByCode")
	@Parameter(name = "catalogId", description = "The catalog name", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@ApiResponse(responseCode = "400", description = "When the item has not been found (CMSItemNotFoundException) or when there was problem during conversion (ConversionException).")
	@ApiResponse(responseCode = "200", description = "ProductData")
	public ProductWsDTO getProductByCode(@Parameter(description = "Product code", required = true)
	@PathVariable final String code) throws ConversionException
	{
		return getDataMapper().map(getCmsProductSearchFacade().getProductByCode(code), ProductWsDTO.class);
	}

	@RequestMapping(method = RequestMethod.GET, params =
			{ "pageSize" })
	@ResponseBody
	@Operation(summary = "Gets products by text.", description = "Finds a list of available products that match a free text search field.",
			operationId = "getProductsByText")
	@ApiResponse(responseCode = "200", description = "DTO which serves as a wrapper object that contains a list of ProductData, never null.")
	@Parameter(name = "catalogId", description = "The catalog name", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "versionId", description = "The catalog version identifier", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "pageSize", description = "The maximum number of elements in the result list.", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "currentPage", description = "The requested page number", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "sort", description = "The string field the results will be sorted with", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	public ProductDataListWsDTO findProductsByText(
			@Parameter(description = "The string value on which products will be filtered. Deprecated, use mask instead!", required = false)
			@RequestParam(required = false) final String text,
			@Parameter(description = "The string value on which products will be filtered", required = false)
			@RequestParam(required = false) final String mask,
			@Parameter(description = "Defines the pageSize, currentPage and sorting order", required = true)
			@ModelAttribute final PageableWsDTO pageableInfo)
	{
		if (Objects.nonNull(text) && Objects.nonNull(mask))
		{
			throw new IllegalArgumentException("Invalid query parameters. text and mask cannot be used together, use mask instead.");
		}
		final String filterAttribute = Optional.ofNullable(text).orElse(mask);

		final SearchResult<ProductData> productSearchResult = getCmsProductSearchFacade().findProducts(filterAttribute,
				getDataMapper().map(pageableInfo, PageableData.class));

		final ProductDataListWsDTO productList = new ProductDataListWsDTO();
		productList.setProducts(productSearchResult //
				.getResult() //
				.stream() //
				.map(productData -> getDataMapper().map(productData, ProductWsDTO.class)) //
				.collect(Collectors.toList()));
		productList.setPagination(getWebPaginationUtils().buildPagination(productSearchResult));
		return productList;
	}

	protected ProductSearchFacade getCmsProductSearchFacade()
	{
		return cmsProductSearchFacade;
	}

	public void setCmsProductSearchFacade(final ProductSearchFacade productSearchFacade)
	{
		this.cmsProductSearchFacade = productSearchFacade;
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
