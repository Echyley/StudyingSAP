/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundleocc.controllers;

import static de.hybris.platform.configurablebundleocc.constants.ConfigurablebundleoccConstants.DEFAULT_CURRENT_PAGE;
import static de.hybris.platform.configurablebundleocc.constants.ConfigurablebundleoccConstants.DEFAULT_PAGE_SIZE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commercewebservicescommons.dto.order.CartModificationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductSearchPageWsDTO;
import de.hybris.platform.configurablebundlefacades.order.BundleCartFacade;
import de.hybris.platform.configurablebundleocc.dto.BundleStarterWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;


import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts/{cartId}")
@CacheControl(directive = CacheControlDirective.PRIVATE)
@ApiVersion("v2")
@Tag(name = "Bundles")
public class ConfigurableBundleController
{
	@Resource(name = "bundleCartFacade")
	private BundleCartFacade bundleCartFacade;
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	private static final String[] DISALLOWED_FIELDS = new String[] {};

	@InitBinder
	public void initBinder(final WebDataBinder binder)
	{
		binder.setDisallowedFields(DISALLOWED_FIELDS);
	}

	@PostMapping(value = "/bundles", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Operation(summary = "Start a bundle.", description = "Starts a bundle once the productCode, its quantity, and a bundle templateId is provided. A successful result returns a CartModification response.", operationId = "createBundle")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public CartModificationWsDTO createBundle(
			@Parameter(description = "Mandatory data required to start a bundle. This includes the templateId of the bundle, the productCode, and the quantity of the product itself.", required = true) @RequestBody @Nonnull @Valid final BundleStarterWsDTO bundleStarter)
			throws CommerceCartModificationException
	{
		final CartModificationData cartModificationData =
				getBundleCartFacade().startBundle(bundleStarter.getTemplateId(),
						bundleStarter.getProductCode(),
						bundleStarter.getQuantity());

		return getDataMapper().map(cartModificationData, CartModificationWsDTO.class);
	}

	@GetMapping(value = "/entrygroups/{entryGroupNumber}/allowedProductsSearch")
	@ResponseBody
	@Operation(summary = "Retrieve products and additional data available for a given entry group and search query.", description = "Returns products and additional data based on the entry group and search query provided. Examples include available facets, available sorting, and pagination options. It can also include spelling suggestions. To disable spelling suggestions \"enableSpellCheck\" must be set to \"FALSE\" on the SearchQuery. Default is set to \"TRUE\". The configuration of indexed properties is required for spellchecking. Any of the products returned can be added to the specific entry group (bundle).", operationId = "getAvailableProducts")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public ProductSearchPageWsDTO getAllowedProducts(
			@Parameter(description = "Serialized query, free text search, facets. The format of a serialized query: freeTextSearch:sort:facetKey1:facetValue1:facetKey2:facetValue2") @RequestParam(required = false) final String query,
			@Parameter(description = "Each entry group in a cart has a specific entry group number. Entry group numbers are integers starting at one. They are defined in ascending order.", required = true) @PathVariable final Integer entryGroupNumber ,
			@Parameter(description = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "The number of products returned per page.") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "The sorting method applied to the results that are returned.") @RequestParam(required = false) final String sort,
			@Parameter(description = "Response configuration. This is the list of fields that should be returned in the response body. Examples: BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = "DEFAULT") final String fields)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(pageSize);
		pageableData.setCurrentPage(currentPage);
		pageableData.setSort(sort);
		final ProductSearchPageData<SearchStateData, ProductData> searchPageData;
		searchPageData = getBundleCartFacade().getAllowedProducts(entryGroupNumber, query, pageableData);
		return getDataMapper().map(searchPageData, ProductSearchPageWsDTO.class, fields);
	}

	protected BundleCartFacade getBundleCartFacade()
	{
		return bundleCartFacade;
	}

	protected DataMapper getDataMapper()
	{
		return this.dataMapper;
	}
}
