/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapdigitalpaymentocc.facade.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsPollModel;
import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsRegistrationModel;
import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsTransactionModel;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.util.DigitalPaymentsSignatureUtil;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static de.hybris.platform.acceleratorservices.payment.enums.DecisionsEnum.ACCEPT;
import static de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant.SESSION_ID;
import static de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant.SIGNATURE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapDpWebServicesPaymentFacadeTest {

    @Mock
    private AcceleratorCheckoutFacade mockCheckoutFacade;
    @Mock
    private BaseSiteService mockBaseSiteService;
    @Mock
    private SiteBaseUrlResolutionService mockUrlResolutionService;
    @Mock
    private SapDigitalPaymentService mockSapDpService;
    @Mock
    private DigitalPaymentsSignatureUtil mockDpSignatureUtil;
    @Mock
    private Converter<DigitalPaymentsRegistrationModel, PaymentData> mockDpPaymentConverter;
    @Mock
    private Converter<DigitalPaymentsPollModel, CCPaymentInfoData> mockDpCCPaymentInfoConverter;

    @InjectMocks
    private DefaultSapDpWebServicesPaymentFacade dpWebServicesPaymentFacade;

    @Test
    public void shouldBeginHopCreateSubscription(){
        // Input
        String responseUrl = "/payment/callback";

        // Expected
        PaymentData paymentData = new PaymentData();

        // Mocks
        BaseSiteModel mockBaseSite = mock(BaseSiteModel.class);
        when(mockBaseSiteService.getCurrentBaseSite()).thenReturn(mockBaseSite);
        String fullResponseUrl = "https://some.url/electronics-spa/en/US"+responseUrl;
        when(mockUrlResolutionService.getWebsiteUrlForSite(mockBaseSite, true, responseUrl)).thenReturn(fullResponseUrl);
        DigitalPaymentsRegistrationModel mockDpRegistrationModel = mock(DigitalPaymentsRegistrationModel.class);
        when(mockSapDpService.getRegistrationUrl(fullResponseUrl)).thenReturn(mockDpRegistrationModel);
        when(mockDpPaymentConverter.convert(mockDpRegistrationModel)).thenReturn(paymentData);

        // Actual Call
        dpWebServicesPaymentFacade.setDpPaymentDataConverter(mockDpPaymentConverter);
        PaymentData actual = dpWebServicesPaymentFacade.beginHopCreateSubscription(responseUrl, null);

        // Verify
        assertEquals(paymentData, actual);
    }

    @Test
    public void shouldCompleteHopCreateSubscription(){
        // Input
        final String sessionId = "mySessionId";
        final String signature = "mySignature";
        final Map<String, String> parameters = Map.of(SESSION_ID, sessionId, SIGNATURE, signature);
        final boolean saveInAccount = true;

        // Expected
        CCPaymentInfoData ccPaymentInfo = new CCPaymentInfoData();

        //Mocks
        when(mockDpSignatureUtil.isValidSignature(signature, sessionId)).thenReturn(true);
        DigitalPaymentsPollModel mockPollModel = mock(DigitalPaymentsPollModel.class);
        DigitalPaymentsTransactionModel mockTransModel = mock(DigitalPaymentsTransactionModel.class);
        when(mockTransModel.getDigitalPaytTransResult()).thenReturn("01");
        when(mockPollModel.getDigitalPaymentTransaction()).thenReturn(mockTransModel);
        when(mockSapDpService.poll(sessionId)).thenReturn(mockPollModel);
        when(mockDpCCPaymentInfoConverter.convert(mockPollModel)).thenReturn(ccPaymentInfo);
        when(mockCheckoutFacade.createPaymentSubscription(ccPaymentInfo)).thenReturn(ccPaymentInfo);

        // Actual Call
        dpWebServicesPaymentFacade.setDpCCPaymentInfoConverter(mockDpCCPaymentInfoConverter);
        PaymentSubscriptionResultData actual = dpWebServicesPaymentFacade.completeHopCreateSubscription(parameters, saveInAccount);

        // Verify
        assertNotNull(actual);
        assertTrue(actual.isSuccess());
        assertEquals(ACCEPT.toString(), actual.getDecision());
        assertNotNull(actual.getStoredCard());
    }

    @Test
    public void shouldCompleteHopCreateSubscription_invalidSignature(){
        // Input
        final String sessionId = "mySessionId";
        final String signature = "mySignature";
        final Map<String, String> parameters = Map.of(SESSION_ID, sessionId, SIGNATURE, signature);
        final boolean saveInAccount = true;

        //Mocks
        when(mockDpSignatureUtil.isValidSignature(signature, sessionId)).thenReturn(false);

        // Actual Call
        PaymentSubscriptionResultData actual = dpWebServicesPaymentFacade.completeHopCreateSubscription(parameters, saveInAccount);

        // Verify
        assertNotNull(actual);
        assertFalse(actual.isSuccess());
        assertNull(actual.getStoredCard());
    }

    @Test
    public void shouldCompleteHopCreateSubscription_pending(){
        // Input
        final String sessionId = "mySessionId";
        final String signature = "mySignature";
        final Map<String, String> parameters = Map.of(SESSION_ID, sessionId, SIGNATURE, signature);
        final boolean saveInAccount = true;

        //Mocks
        when(mockDpSignatureUtil.isValidSignature(signature, sessionId)).thenReturn(true);
        DigitalPaymentsPollModel mockPollModel = mock(DigitalPaymentsPollModel.class);
        DigitalPaymentsTransactionModel mockTransModel = mock(DigitalPaymentsTransactionModel.class);
        when(mockTransModel.getDigitalPaytTransResult()).thenReturn("04"); // Pending status
        when(mockPollModel.getDigitalPaymentTransaction()).thenReturn(mockTransModel);
        when(mockSapDpService.poll(sessionId)).thenReturn(mockPollModel);

        // Actual Call
        PaymentSubscriptionResultData actual = dpWebServicesPaymentFacade.completeHopCreateSubscription(parameters, saveInAccount);

        // Verify
        assertNotNull(actual);
        assertFalse(actual.isSuccess());
        assertNull(actual.getStoredCard());
    }
}
