/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorwebservicesaddon.controllers.v2;


import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorservices.payment.PaymentService;
import de.hybris.platform.acceleratorwebservicesaddon.payment.facade.CommerceWebServicesPaymentFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * Controller to handle merchant callbacks from a subscription provider
 */
@Controller
@Tag(name = "Merchant Callback")
public class MerchantCallbackController
{
	@Resource(name = "acceleratorPaymentService")
	private PaymentService acceleratorPaymentService;
	@Resource(name = "commerceWebServicesPaymentFacade")
	private CommerceWebServicesPaymentFacade commerceWebServicesPaymentFacade;
	@Resource(name = "cartLoaderStrategy")
	private CartLoaderStrategy cartLoaderStrategy;
	@Resource(name = "wsUserFacade")
	private UserFacade userFacade;
	@Resource(name = "checkoutFacade")
	private CheckoutFacade checkoutFacade;

	@RequestMapping(value = "/{baseSiteId}/integration/merchant_callback", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	@Operation(operationId = "doHandleMerchantCallback", summary = "Verify the decision of the Merchant", description =
			"Verifies the decision of the merchant. \n\nNote, "
					+ "the “Try it out” button is not enabled for this method (always returns an error) because the Merchant Callback "
					+ "Controller handles parameters differently, depending on which payment provider is used. For more information about "
					+ "this controller, please refer to the “acceleratorwebservicesaddon AddOn” documentation on help.hybris.com.")
	@ApiBaseSiteIdParam
	public void doHandleMerchantCallback(@Parameter(hidden = true) final HttpServletRequest request)
	{
		acceleratorPaymentService.handleCreateSubscriptionCallback(getParameterMap(request));
	}

	protected Map<String, String> getParameterMap(final HttpServletRequest request)
	{
		final Map<String, String> map = new HashMap<>();
		final Enumeration myEnum = request.getParameterNames();
		while (myEnum.hasMoreElements())
		{
			final String paramName = (String) myEnum.nextElement();
			final String paramValue = request.getParameter(paramName);
			map.put(paramName, paramValue);
		}
		return map;
	}

	@RequestMapping(value = "/{baseSiteId}/integration/users/{userId}/carts/{cartId}/payment/sop/response", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	@Operation(operationId = "doHandleCartMerchantCallback", summary = "Verify the decision of the Merchant for a cart", description =
			"Verifies the decision of the merchant for a specified cart, and stores information of the PaymentSubscriptionResult "
					+ "for the cart. \n\nNote, the “Try it out” button is not enabled for this method (always returns an error) because the Merchant Callback Controller handles parameters differently, depending "
					+ "on which payment provider is used. For more information about this controller, please refer to the “acceleratorwebservicesaddon AddOn” documentation on help.hybris.com.")
	@ApiBaseSiteIdParam
	public void doHandleCartMerchantCallback(@Parameter(hidden = true) final HttpServletRequest request,
			@Parameter(description = "User identifier or one of the literals : 'current' for currently authenticated user, 'anonymous' for anonymous user", required = true) @PathVariable final String userId,
			@Parameter(description = "Cart identifier: cart code for logged in user, cart guid for anonymous user, 'current' for the last modified cart", required = true) @PathVariable final String cartId)
	{
		userFacade.setCurrentUser(userId);
		loadCart(cartId);
		final Map<String, String> parameterMap = getParameterMap(request);
		final PaymentSubscriptionResultData paymentSubscriptionResultData = commerceWebServicesPaymentFacade
				.completeSopCreateSubscription(parameterMap, true, false);

		commerceWebServicesPaymentFacade.savePaymentSubscriptionResult(paymentSubscriptionResultData, cartId);

		if (paymentSubscriptionResultData.isSuccess() && paymentSubscriptionResultData.getStoredCard() != null && StringUtils
				.isNotBlank(paymentSubscriptionResultData.getStoredCard().getSubscriptionId()))
		{
			final CCPaymentInfoData newPaymentSubscription = paymentSubscriptionResultData.getStoredCard();
			if (userFacade.getCCPaymentInfos(true).size() <= 1)
			{
				userFacade.setDefaultPaymentInfo(newPaymentSubscription);
			}
			checkoutFacade.setPaymentDetails(newPaymentSubscription.getId());
		}
		acceleratorPaymentService.handleCreateSubscriptionCallback(parameterMap);
	}

	protected void loadCart(final String cartId)
	{
		cartLoaderStrategy.loadCart(cartId);
	}
}
