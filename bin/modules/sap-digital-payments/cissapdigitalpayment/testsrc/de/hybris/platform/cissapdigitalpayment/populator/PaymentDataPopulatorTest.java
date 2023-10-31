/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsRegistrationModel;
import de.hybris.platform.cissapdigitalpayment.util.DigitalPaymentsSignatureUtil;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant.SESSION_ID;
import static de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant.SIGNATURE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentDataPopulatorTest {

    @Mock
    private DigitalPaymentsSignatureUtil mockDpSignUtil;

    @InjectMocks
    private PaymentDataPopulator<DigitalPaymentsRegistrationModel, PaymentData> dpWebServicesPaymentFacade;

    @Test
    public void shouldPopulate() throws NoSuchAlgorithmException, InvalidKeyException {
        String registerUrl = "https://my.registration.url/something";
        String sessionId = "1234567890";
        String signature = "asdfghjkl";

        // Input
        DigitalPaymentsRegistrationModel registrationModel = new DigitalPaymentsRegistrationModel();
        registrationModel.setPaymentCardRegistrationURL(registerUrl);
        registrationModel.setPaymentCardRegistrationSession(sessionId);
        PaymentData paymentData = new PaymentData();

        // Mocks
        when(mockDpSignUtil.computeSignature(sessionId)).thenReturn(signature);

        // Actual call
        dpWebServicesPaymentFacade.populate(registrationModel, paymentData);

        // Verify
        assertEquals(registerUrl, paymentData.getPostUrl());
        Map<String, String> parameters = paymentData.getParameters();
        assertThat(parameters, IsMapContaining.hasEntry(SESSION_ID, sessionId));
        assertThat(parameters, IsMapContaining.hasEntry(SIGNATURE, signature));
    }
}
