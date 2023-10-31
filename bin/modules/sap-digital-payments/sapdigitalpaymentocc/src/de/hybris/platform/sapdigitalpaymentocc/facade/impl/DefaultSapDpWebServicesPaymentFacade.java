/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapdigitalpaymentocc.facade.impl;

import com.hybris.charon.exp.ClientException;
import com.hybris.charon.exp.ServerException;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorocc.payment.facade.impl.DefaultCommerceWebServicesPaymentFacade;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.enums.DecisionsEnum;
import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsPollModel;
import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsRegistrationModel;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.util.DigitalPaymentsSignatureUtil;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.sapdigitalpaymentocc.facade.SapDpWebServicesPaymentFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.log4j.Logger;

import java.util.Map;

import static de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant.*;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Digital Payments web services payment facade
 */
public class DefaultSapDpWebServicesPaymentFacade extends DefaultCommerceWebServicesPaymentFacade implements SapDpWebServicesPaymentFacade {

    private static final Logger LOG = Logger.getLogger(DefaultSapDpWebServicesPaymentFacade.class);

    private SapDigitalPaymentService sapDpService;
    private DigitalPaymentsSignatureUtil dpSignatureUtil;
    private AcceleratorCheckoutFacade checkoutFacade;

    private Converter<DigitalPaymentsRegistrationModel, PaymentData> dpPaymentDataConverter;
    private Converter<DigitalPaymentsPollModel, CCPaymentInfoData> dpCCPaymentInfoConverter;

    @Override
    public PaymentData beginHopCreateSubscription(String responseUrl, String merchantCallbackUrl) {
        final String fullResponseUrl = getFullResponseUrl(responseUrl, true);

        if (isNotEmpty(merchantCallbackUrl)) {
            throw new NotImplementedException("Digital Payment subscription integration is not yet implement" +
                    " with merchantCallbackUrl. Please use responseUrl only");
        }

        final DigitalPaymentsRegistrationModel registrationModel = sapDpService.getRegistrationUrl(fullResponseUrl);
        return dpPaymentDataConverter.convert(registrationModel);
    }

    @Override
    public PaymentSubscriptionResultData completeHopCreateSubscription(Map<String, String> parameters, boolean saveInAccount) {
        PaymentSubscriptionResultData response = new PaymentSubscriptionResultData();
        String sessionId = parameters.get(SESSION_ID);
        String signature = parameters.get(SIGNATURE);

        // Validate Signature
        if (!dpSignatureUtil.isValidSignature(signature, sessionId)) {
            LOG.error("Cannot create hop subscription. Subscription signature does not match.");
            return response;
        }

        // Call Service
        DigitalPaymentsPollModel pollModel;
        try {
            pollModel = sapDpService.poll(sessionId);
        }
        catch (ClientException | ServerException ex){
            LOG.error(ex);
            return response;
        }

        // Check Transaction state
        final String transactionResult = pollModel.getDigitalPaymentTransaction().getDigitalPaytTransResult();
        if (equalsIgnoreCase(transactionResult, DP_SUCCESS_CODE)) {

            // Save card into the account
            CCPaymentInfoData ccPaymentInfo = dpCCPaymentInfoConverter.convert(pollModel);
            assert ccPaymentInfo != null;
            ccPaymentInfo.setSaved(saveInAccount);
            ccPaymentInfo = checkoutFacade.createPaymentSubscription(ccPaymentInfo);

            // Add card to response
            response.setSuccess(true);
            response.setStoredCard(ccPaymentInfo);
            response.setDecision(DecisionsEnum.ACCEPT.toString());
        } else {
            final String transactionResultDesc = pollModel.getDigitalPaymentTransaction().getDigitalPaytTransRsltDesc();
            LOG.error( String.format("Cannot create subscription. Transaction status is: %s", transactionResultDesc) );
            response.setSuccess(false);
        }
        return response;
    }


    public void setSapDpService(SapDigitalPaymentService sapDpService) {
        this.sapDpService = sapDpService;
    }

    public void setDpCCPaymentInfoConverter(Converter<DigitalPaymentsPollModel, CCPaymentInfoData> dpCCPaymentInfoConverter) {
        this.dpCCPaymentInfoConverter = dpCCPaymentInfoConverter;
    }

    public void setDpPaymentDataConverter(Converter<DigitalPaymentsRegistrationModel, PaymentData> dpPaymentDataConverter) {
        this.dpPaymentDataConverter = dpPaymentDataConverter;
    }

    public void setDpSignatureUtil(DigitalPaymentsSignatureUtil dpSignatureUtil) {
        this.dpSignatureUtil = dpSignatureUtil;
    }

    public void setCheckoutFacade(AcceleratorCheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }
}
