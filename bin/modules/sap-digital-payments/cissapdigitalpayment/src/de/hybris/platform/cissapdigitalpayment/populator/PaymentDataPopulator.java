/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.populator;

import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsRegistrationModel;
import de.hybris.platform.cissapdigitalpayment.util.DigitalPaymentsSignatureUtil;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.log4j.Logger;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant.SESSION_ID;
import static de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant.SIGNATURE;

public class PaymentDataPopulator<S extends DigitalPaymentsRegistrationModel, T extends PaymentData> implements Populator<S, T> {

    private static final Logger LOG = Logger.getLogger(PaymentDataPopulator.class);

    private DigitalPaymentsSignatureUtil dpSignatureUtil;

    @Override
    public void populate(S s, T t){
        t.setPostUrl(s.getPaymentCardRegistrationURL());

        String sessionId = s.getPaymentCardRegistrationSession();
        String sign = computeSign(sessionId);

        Map<String, String> additionParam = new HashMap<>();
        additionParam.put(SESSION_ID, sessionId);
        additionParam.put(SIGNATURE, sign);
        t.setParameters(additionParam);
    }


    private String computeSign(String data) {
        try {
            return dpSignatureUtil.computeSignature(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            LOG.error("Error computing signature for session id", e);
            throw new ConversionException("Error computing signature for session id", e);
        }
    }


    public void setDpSignatureUtil(DigitalPaymentsSignatureUtil dpSignatureUtil) {
        this.dpSignatureUtil = dpSignatureUtil;
    }


}
