/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapdigitalpaymentocc.controllers.v2;

import static de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant.SESSION_ID;
import static de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant.SIGNATURE;
import static de.hybris.platform.sapdigitalpaymentocc.constants.DigitalPaymentWebserviceConstants.CURR;
import static de.hybris.platform.sapdigitalpaymentocc.constants.DigitalPaymentWebserviceConstants.LANG;
import static de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper.DEFAULT_LEVEL;
import static javax.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorocc.dto.payment.PaymentRequestWsDTO;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.cissapdigitalpayment.exceptions.DigitalPaymentsUnknownException;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.sapdigitalpaymentocc.facade.impl.DefaultSapDpWebServicesPaymentFacade;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@ApiVersion("v2")
@Tag(name = "Digital Payments")
public class DigitalPaymentsController {
    private static final Logger LOG = Logger.getLogger(DigitalPaymentsController.class);

    @Resource(name = "sapDpWebServicesPaymentFacade")
    private DefaultSapDpWebServicesPaymentFacade sapDpPaymentFacade;
    @Resource(name = "dataMapper")
    private DataMapper dataMapper;
    @Resource(name = "userFacade")
    private UserFacade userFacade;
    @Resource(name = "checkoutFacade")
    private CheckoutFacade checkoutFacade;

    @PostMapping(value = "/{cartId}/payment/digitalPayments/request")
    @ResponseBody
    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @Operation(operationId = "createDigitalPaymentRequest", summary = "Get information needed for create subscription",
            description = "Returns the necessary information for creating a subscription that contacts the "
                    + "payment provider directly. This information contains the payment provider URL and a list of parameters that are needed to create the subscription.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PaymentRequestWsDTO createDigitalPaymentRequest(
            @Parameter(hidden = true) final HttpServletRequest request,
            @PathVariable final String baseSiteId,
            @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_LEVEL) final String fields,
            @Parameter(hidden = true) final HttpServletResponse response
    ) {
        // Create Redirect Url
        Map<String, String> paramMap = getParameterMap(request);
        String lang = paramMap.getOrDefault(LANG, "en");
        String curr = paramMap.getOrDefault(CURR, "USD");
        String responseUrl = getRedirectUrl(baseSiteId, lang, curr);

        try {
            final PaymentData paymentData = sapDpPaymentFacade.beginHopCreateSubscription(responseUrl, null);
            return dataMapper.map(paymentData, PaymentRequestWsDTO.class, fields);
        } catch (DigitalPaymentsUnknownException ex) {
            LOG.error(ex);
            response.setStatus(SC_SERVICE_UNAVAILABLE);
        }

        return null;
    }

    @PostMapping(value = "/{cartId}/payment/digitalPayments/response")
    @ResponseBody
    @Secured({"ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT"})
    @Operation(operationId = "doHandleDigitalPaymentResponse", summary = "Handles response from the Digital Payments system and create payment details",
            description = "Handles the response from the Digital Payments system and creates payment details."
                    + "\n\nNote, the “Try it out” button is not enabled for this method (always returns an error) because the Digital Payment Controller handles parameters differently, depending "
                    + "on which payment provider is used. For more information about this controller, please refer to the “sapdigitalpaymentocc AddOn” documentation on help.hybris.com.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PaymentDetailsWsDTO doHandleDigitalPaymentResponse(
            @Parameter(description = "Signed session id", required = true,
                    example = "f0303ad7a2a3cd799def548733f3664fe0e805f0") @RequestParam("sign") final String signature,
            @Parameter(description = "Digital Payments session id", required = true,
                    example = "176b1e9c9144a634ec914174bfda7370059fe630bf9") @RequestParam("sid") final String sessionId,
            @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_LEVEL) final String fields,
            @Parameter(hidden = true) final HttpServletResponse response) {
        Map<String, String> params = Map.of(SESSION_ID, sessionId, SIGNATURE, signature);

        final PaymentSubscriptionResultData paymentSubscriptionResultData = sapDpPaymentFacade.completeHopCreateSubscription(params, true);

        // Handle Response
        if (paymentSubscriptionResultData.isSuccess()) {
            CCPaymentInfoData paymentInfoData = paymentSubscriptionResultData.getStoredCard();
            if (userFacade.getCCPaymentInfos(true).size() <= 1) {
                userFacade.setDefaultPaymentInfo(paymentInfoData);
            }
            checkoutFacade.setPaymentDetails(paymentInfoData.getId());

            return dataMapper.map(paymentInfoData, PaymentDetailsWsDTO.class, fields);
        } else {
            response.setStatus(SC_ACCEPTED);
            return null;
        }
    }

    // Private Methods
    private Map<String, String> getParameterMap(final HttpServletRequest request) {
        final Map<String, String> map = new HashMap<>();
        final Enumeration myEnum = request.getParameterNames();
        while (myEnum.hasMoreElements()) {
            final String paramName = (String) myEnum.nextElement();
            final String paramValue = request.getParameter(paramName);
            map.put(paramName, paramValue);
        }
        return map;
    }

    private String getRedirectUrl(String baseSite, String lang, String currency) {
        String defaultRedirectUrl = "/checkout/payment/callback";
        String propKey = String.format("website.%s.digitalpayment.redirect.url", baseSite);
        String redirectUrl = Config.getString(propKey, defaultRedirectUrl);
        if (StringUtils.isBlank(propKey)) {
            LOG.warn(String.format("Property '%s' is missing. Defaulting to %s", propKey, defaultRedirectUrl));
        }
        return UriComponentsBuilder.fromPath("/")
                .pathSegment(baseSite, lang, currency)
                .path(redirectUrl)
                .build()
                .toString();
    }
}
