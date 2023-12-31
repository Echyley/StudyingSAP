/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customercouponocc.controllers.customercoupon;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customercouponfacades.CustomerCouponFacade;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponNotificationData;
import de.hybris.platform.customercouponfacades.emums.AssignCouponResult;
import de.hybris.platform.customercouponfacades.strategies.CustomerNotificationPreferenceCheckStrategy;
import de.hybris.platform.customercouponocc.dto.CustomerCoupon2CustomerWsDTO;
import de.hybris.platform.customercouponocc.dto.CustomerCouponNotificationWsDTO;
import de.hybris.platform.customercouponocc.dto.CustomerCouponSearchResultWsDTO;
import de.hybris.platform.customercouponocc.dto.CustomerCouponWsDTO;
import de.hybris.platform.customercouponocc.errors.exceptions.CustomerCouponClaimException;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * APIs for my coupons.
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/customercoupons")
@Tag(name = "Customer Coupons")
public class CustomerCouponsController
{

	private static final String MAX_PAGE_SIZE_KEY = "webservicescommons.pagination.maxPageSize";
	public static final String COUPON_CODE = "couponCode";

	@Resource(name = "customerCouponFacade")
	private CustomerCouponFacade customerCouponFacade;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "customerNotificationPreferenceCheckStrategy")
	private CustomerNotificationPreferenceCheckStrategy customerNotificationPreferenceCheckStrategy;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "webPaginationUtils")
	private WebPaginationUtils webPaginationUtils;

	@Resource(name = "customerCouponCodeValidator")
	private Validator customerCouponCodeValidator;

	@Resource(name = "customerCouponValidator")
	private Validator customerCouponValidator;

	@Resource(name = "customerCouponAssignResultValidator")
	private Validator customerCouponAssignResultValidator;

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@ResponseBody
	@GetMapping
	@Operation(summary = "Gets all customer coupons of the current customer.", description = "Gets the customer coupon list of the current customer.")
	@ApiBaseSiteIdAndUserIdParam
	public CustomerCouponSearchResultWsDTO getCustomerCoupons(
			@Parameter(description = "The current result page requested.") @RequestParam(name = "currentPage", required = false, defaultValue = "0") final int currentPage,
			@Parameter(description = "The number of results returned per page.") @RequestParam(name = "pageSize", required = false, defaultValue = "10") final int pageSize,
			@Parameter(description = "The sorting method applied to the return results.") @RequestParam(name = "sort", required = false) final String sort,
			@Parameter(description = "The flag for indicating if total number of results is needed or not.", schema = @Schema(allowableValues = {
					"true", "false" })) @RequestParam(name = "needsTotal", required = false, defaultValue = "true") final boolean needsTotal,
			@ApiFieldsParam @RequestParam(defaultValue = "DEFAULT") final String fields)
	{
		final SearchPageData searchPageData = getWebPaginationUtils().buildSearchPageData(sort, currentPage, pageSize, needsTotal);
		recalculatePageSize(searchPageData);

		return getDataMapper().map(getCustomerCouponFacade().getPaginatedCoupons(searchPageData),
				CustomerCouponSearchResultWsDTO.class, fields);
	}

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@PostMapping("/{couponCode}/claim")
	@Operation(summary = "Claims and remembers a customer coupon.", description = "Claims and remembers a customer coupon by coupon code.")
	@ApiBaseSiteIdAndUserIdParam
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	public CustomerCoupon2CustomerWsDTO doClaimCustomerCoupon(
			@Parameter(description = "Coupon code", required = true) @PathVariable final String couponCode,
			@ApiFieldsParam @RequestParam(defaultValue = "DEFAULT") final String fields)
	{
		final AssignCouponResult result = getCustomerCouponFacade().grantCouponAccessForCurrentUser(couponCode);
		validateCoupon(customerCouponAssignResultValidator, result, "couponAssignResult");

		final CustomerCouponData coupon = getCustomerCouponFacade().getCustomerCouponForCode(couponCode);
		final CustomerCoupon2CustomerWsDTO relation = new CustomerCoupon2CustomerWsDTO();
		relation.setCustomer(getCustomerWsDTO(fields));
		relation.setCoupon(getDataMapper().map(coupon, CustomerCouponWsDTO.class, fields));

		return relation;
	}

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@DeleteMapping("/{couponCode}/claim")
	@Operation(operationId = "unassignCustomerCoupon", summary = "Disclaims a customer coupon.", description = "Disclaims a customer coupon by coupon code.")
	@ApiBaseSiteIdAndUserIdParam
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public void unassignCustomerCoupon(
			@Parameter(description = "Coupon code", required = true) @PathVariable final String couponCode)
	{
		validateCoupon(customerCouponCodeValidator, couponCode, COUPON_CODE);
		try {
			getCustomerCouponFacade().releaseCoupon(couponCode);
		} catch (VoucherOperationException e) {
			throw new CustomerCouponClaimException("Coupon wasn't released", e.getMessage());
		}
	}

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@PostMapping(value = "/{couponCode}/notification")
	@Operation(summary = "Subscribes to a coupon notification.", description = "Make a subscription to a customer coupon to receive notifications when it will be in effect soon, or will expire soon.")
	@ApiBaseSiteIdAndUserIdParam
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	public CustomerCouponNotificationWsDTO doSubscribeToCustomerCoupon(
			@Parameter(description = "Coupon code", required = true) @PathVariable final String couponCode,
			@ApiFieldsParam @RequestParam(defaultValue = "DEFAULT") final String fields)
	{

		validateCoupon(customerCouponValidator, couponCode, COUPON_CODE);

		final CustomerCouponNotificationData notification = getCustomerCouponFacade().saveCouponNotification(couponCode);
		getCustomerNotificationPreferenceCheckStrategy().checkCustomerNotificationPreference();

		return getDataMapper().map(notification, CustomerCouponNotificationWsDTO.class, fields);
	}


	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@ResponseStatus(code = HttpStatus.OK)
	@DeleteMapping(value = "/{couponCode}/notification")
	@Operation(summary = "Unsubscribes from the coupon notification.", description = "Removes notification subscription from the specific customer coupon.")
	@ApiBaseSiteIdAndUserIdParam
	public void doUnsubscribeFromCustomerCoupon(
			@Parameter(description = "Coupon code", required = true) @PathVariable final String couponCode)
	{
		validateCoupon(customerCouponCodeValidator, couponCode, COUPON_CODE);
		getCustomerCouponFacade().removeCouponNotificationByCode(couponCode);
	}

	protected void validateCoupon(final Validator validator, final Object obj, final String objName)
	{
		final Errors errors = new BeanPropertyBindingResult(obj, objName);
		validator.validate(obj, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
	}

	protected void recalculatePageSize(final SearchPageData searchPageData)
	{
		int pageSize = searchPageData.getPagination().getPageSize();
		if (pageSize <= 0)
		{
			final int maxPageSize = Config.getInt(MAX_PAGE_SIZE_KEY, 1000);
			pageSize = getWebPaginationUtils().getDefaultPageSize();
			pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;
			searchPageData.getPagination().setPageSize(pageSize);
		}
	}

	protected UserWsDTO getCustomerWsDTO(final String fields)
	{
		return getDataMapper().map(getCustomerFacade().getCurrentCustomer(), UserWsDTO.class, fields);
	}

	protected CustomerCouponFacade getCustomerCouponFacade()
	{
		return customerCouponFacade;
	}

	public void setCustomerCouponFacade(final CustomerCouponFacade customerCouponFacade)
	{
		this.customerCouponFacade = customerCouponFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	protected CustomerNotificationPreferenceCheckStrategy getCustomerNotificationPreferenceCheckStrategy()
	{
		return customerNotificationPreferenceCheckStrategy;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	protected CustomerFacade getCustomerFacade()
	{
		return customerFacade;
	}

	protected Validator getCustomerCouponCodeValidator()
	{
		return customerCouponCodeValidator;
	}

	public void setCustomerCouponCodeValidator(final Validator customerCouponCodeValidator)
	{
		this.customerCouponCodeValidator = customerCouponCodeValidator;
	}
}
