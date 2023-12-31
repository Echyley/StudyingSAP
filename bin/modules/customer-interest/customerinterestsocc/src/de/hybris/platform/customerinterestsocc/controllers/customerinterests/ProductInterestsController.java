/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerinterestsocc.controllers.customerinterests;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestData;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestRelationData;
import de.hybris.platform.customerinterestsfacades.productinterest.ProductInterestFacade;
import de.hybris.platform.customerinterestsocc.dto.CustomerInterestsSearchResultWsDTO;
import de.hybris.platform.customerinterestsocc.dto.ProductInterestRelationWsDTO;
import de.hybris.platform.customerinterestsocc.validation.ProductInterestsValidator;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;


/**
 * Provides RESTful API for product interests related methods
 */
@Controller
@RequestMapping("/{baseSiteId}/users/{userId}/productinterests")
@Tag(name = "Product Interests")
public class ProductInterestsController
{
	private static final String MAX_PAGE_SIZE_KEY = "webservicescommons.pagination.maxPageSize";
	private static final String REQUESTPARAM = "RequestParam";
	private static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.DEFAULT_LEVEL;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "productInterestFacade")
	private ProductInterestFacade productInterestFacade;

	@Resource(name = "webPaginationUtils")
	private WebPaginationUtils webPaginationUtils;

	@Resource(name = "productInterestsValidator")
	private ProductInterestsValidator productInterestsValidator;

	@Resource(name = "productFacade")
	private ProductFacade productFacade;


	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	@Operation(summary = "Sets product interests for a user.", description = "Sets product interests for a specific user.")
	@ApiBaseSiteIdAndUserIdParam
	public ProductInterestRelationWsDTO createProductInterest(
			@Parameter(description = "Product identifier", required = true) @RequestParam(required = true) final String productCode,
			@Parameter(description = "Notification type", required = true) @RequestParam(required = true) String notificationType,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		notificationType = StringUtils.upperCase(notificationType);
		final Errors errors = new BeanPropertyBindingResult(REQUESTPARAM, REQUESTPARAM);
		getProductInterestsValidator().validateProductInterestCreation(productCode, notificationType, errors);

		final ProductInterestRelationData productInterestRelation = saveProductInterest(productCode,
				NotificationType.valueOf(notificationType));

		return dataMapper.map(productInterestRelation, ProductInterestRelationWsDTO.class, fields);
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Removes product interests by product code and notification type", description = "Removes product interests by product code and notification type.")
	@ApiBaseSiteIdAndUserIdParam
	public void removeProductInterest(
			@Parameter(description = "Product identifier", required = true) @RequestParam(required = true) final String productCode,
			@Parameter(description = "Notification type", required = true) @RequestParam(required = true) String notificationType)
	{
		notificationType = StringUtils.upperCase(notificationType);
		final Errors errors = new BeanPropertyBindingResult(REQUESTPARAM, REQUESTPARAM);
		getProductInterestsValidator().validateProductInterestRemoval(productCode, notificationType, errors);

		final List<ProductInterestData> productInterestsData = getProductInterestFacade()
				.getProductInterestsForNotificationType(productCode, NotificationType.valueOf(notificationType));

		productInterestsData.stream().forEach(x -> getProductInterestFacade().removeAllProductInterests(x.getProduct().getCode()));
	}

	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	@Operation(summary = "Gets product interests for a user.", description = "Gets product interests for a specific user.")
	@Parameter(name = "pageSize", description = "The number of results returned per page.", required = false, schema = @Schema(type = "string", defaultValue = "20"), in = ParameterIn.QUERY)
	@Parameter(name = "currentPage", description = "The current result page requested.", required = false, schema = @Schema(type = "string", defaultValue = "0"), in = ParameterIn.QUERY)
	@Parameter(name = "sort", description = "Sorting method applied to the return results.", required = false, schema = @Schema(type = "string"), in = ParameterIn.QUERY)
	@Parameter(name = "needsTotal", description = "the flag for indicating if total number of results is needed or not", schema = @Schema(type = "string", allowableValues = {"true", "false"}, defaultValue = "true"), required = false, in = ParameterIn.QUERY)
	@Parameter(name = "userId", description = "User identifier or one of the literals : \'current\' for currently authenticated user, \'anonymous\' for anonymous user", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)
	@Parameter(name = "baseSiteId", description = "Base site identifier.", required = true, schema = @Schema(type = "string"), in = ParameterIn.PATH)

	public CustomerInterestsSearchResultWsDTO getProductInterests(final HttpServletRequest request,
			@Parameter(description = "Product identifier", required = false) @RequestParam(required = false) final String productCode,
			@Parameter(description = "Notification type", required = false) @RequestParam(required = false) String notificationType,
			@ApiFieldsParam @RequestParam(defaultValue = "DEFAULT") final String fields)
	{
		notificationType = StringUtils.upperCase(notificationType);
		final Errors errors = new BeanPropertyBindingResult(REQUESTPARAM, REQUESTPARAM);
		getProductInterestsValidator().validateProductInterestRetrieval(productCode, notificationType, errors);

		final Map<String, String> params = getParameterMapFromRequest(request);

		final SearchPageData<Object> searchPageData = webPaginationUtils.buildSearchPageData(params);
		recalculatePageSize(searchPageData);
		getProductInterestsValidator().validatePageSize(searchPageData.getPagination().getPageSize(), errors);

		final SearchPageData<ProductInterestRelationData> paginatedData = productInterestFacade
				.getPaginatedProductInterestsForNotificationType(productCode,
						StringUtils.isNoneBlank(notificationType) ? NotificationType.valueOf(notificationType) : null, searchPageData);

		return dataMapper.map(paginatedData, CustomerInterestsSearchResultWsDTO.class, fields);
	}

	protected ProductInterestRelationData saveProductInterest(final String productCode, final NotificationType notificationType)
	{
		final ProductInterestData productInterestData = getProductInterestFacade()
				.getProductInterestDataForCurrentCustomer(productCode, notificationType).orElse(new ProductInterestData());
		if (ObjectUtils.isEmpty(productInterestData.getProduct()))
		{
			final ProductData product = getProductFacade().getProductForCodeAndOptions(productCode,
					Arrays.asList(ProductOption.BASIC));
			productInterestData.setProduct(product);
			productInterestData.setNotificationType(notificationType);

			getProductInterestFacade().saveProductInterest(productInterestData);
		}
		return getProductInterestFacade().getProductInterestRelation(productCode);
	}

	protected Map<String, String> getParameterMapFromRequest(final HttpServletRequest request)
	{
		final Map<String, String[]> parameterMap = request.getParameterMap();
		final Map<String, String> result = new LinkedHashMap<String, String>();
		if (MapUtils.isEmpty(parameterMap))
		{
			return result;
		}
		for (final Map.Entry<String, String[]> entry : parameterMap.entrySet())
		{
			if (entry.getValue().length > 0)
			{
				result.put(entry.getKey(), entry.getValue()[0]);
			}
		}
		return result;
	}

	protected void recalculatePageSize(final SearchPageData searchPageData)
	{
		int pageSize = searchPageData.getPagination().getPageSize();
		if (pageSize <= 0)
		{
			final int maxPageSize = Config.getInt(MAX_PAGE_SIZE_KEY, 1000);
			pageSize = webPaginationUtils.getDefaultPageSize();
			pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;
			searchPageData.getPagination().setPageSize(pageSize);
		}
	}

	protected ProductInterestFacade getProductInterestFacade()
	{
		return productInterestFacade;
	}

	protected ProductInterestsValidator getProductInterestsValidator()
	{
		return productInterestsValidator;
	}

	protected ProductFacade getProductFacade()
	{
		return productFacade;
	}

	protected void handleErrors(final Errors errors)
	{
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
	}

}
