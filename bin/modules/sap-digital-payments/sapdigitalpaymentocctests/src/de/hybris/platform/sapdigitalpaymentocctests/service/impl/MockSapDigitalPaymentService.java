/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapdigitalpaymentocctests.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationResult;
import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsPollModel;
import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsRegistrationModel;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentCaptureException;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentRefundException;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Mock for digital payment service
 */
public class MockSapDigitalPaymentService implements SapDigitalPaymentService {
    private static final Logger LOG = Logger.getLogger(MockSapDigitalPaymentService.class);

    private static final ObjectMapper mapper = new ObjectMapper();
    private Resource pollResource;
    private Resource registerUrlResource;

    @Override
    public DigitalPaymentsRegistrationModel getRegistrationUrl(String redirectUrl) {
        return getApiResponse(registerUrlResource, DigitalPaymentsRegistrationModel.class);
    }

    @Override
    public DigitalPaymentsPollModel poll(String sessionId) {
        return getApiResponse(pollResource, DigitalPaymentsPollModel.class);
    }

    // <editor-fold desc="Not implemented methods">
    @Override
    public PaymentTransactionEntryModel authorize(String merchantTransactionCode, String paymentProvider, AddressModel deliveryAddress, CisSapDigitalPaymentAuthorizationResult cisSapDigitalPaymentAuthorizationResult) {
        throw new NotImplementedException("authorization is not implemented yet in mock");
    }

    @Override
    public PaymentTransactionEntryModel capture(PaymentTransactionModel transaction) throws SapDigitalPaymentCaptureException {
        throw new NotImplementedException("capture is not implemented yet in mock");
    }

    @Override
    public PaymentTransactionEntryModel refund(PaymentTransactionModel transaction, BigDecimal amountToRefund) throws SapDigitalPaymentRefundException {
        throw new NotImplementedException("refund is not implemented yet in mock");
    }

    @Override
    public String getCardRegistrationUrl() {
        throw new NotImplementedException("this is not implemented yet in mock");
    }

    @Override
    public void createPollRegisteredCardProcess(String sessionId) {
        throw new NotImplementedException("this method is not implemented yet in mock");
    }

    @Override
    public CreditCardPaymentInfoModel createPaymentSubscription(CCPaymentInfoData paymentInfoData, Map<String, Object> params) {
        throw new NotImplementedException("create payment subscription is not implemented yet in mock");
    }

    @Override
    public boolean saveCreditCardPaymentDetailsToCart(String paymentInfoId, Map<String, Object> params) {
        throw new NotImplementedException("save cc is not implemented yet in mock");
    }

    @Override
    public boolean isSapDigitalPaymentTransaction(PaymentTransactionModel txn) {
        throw new NotImplementedException("is dp transaction is not implemented yet in mock");
    }

    @Override
    public DigitalPaymentsPollModel pollAndSave(String sessionId) {
        throw new NotImplementedException("poll and save is not implemented yet in mock");
    }

    @Override
    public DigitalPaymentsRegistrationModel getRegistrationUrl() {
        throw new NotImplementedException("create payment subscription is not implemented yet in mock");
    }
    // </editor-fold>

    // <editor-fold desc="Private Methods">
    private String loadResource(final Resource r) {
        String json = EMPTY;
        try (InputStream is = r.getInputStream()) {
            json = IOUtils.toString(is, StandardCharsets.UTF_8); // IOUtils don't close stream
        } catch (final IOException e) {
            LOG.warn(e);
        }
        return json;
    }

    private <T> T getApiResponse(Resource resource, Class<T> clazz){
        String apiResponse = loadResource(resource);
        try {
            return mapper.readValue(apiResponse, clazz);
        } catch (JsonProcessingException e) {
            throw new SystemException("Unable to parse json from resource: "+resource.getFilename());
        }
    }

    // </editor-fold>

    // <editor-fold desc="Getters and Setters">

    public void setPollResource(Resource pollResource) {
        this.pollResource = pollResource;
    }

    public void setRegisterUrlResource(Resource registerUrlResource) {
        this.registerUrlResource = registerUrlResource;
    }

    // </editor-fold>

}
