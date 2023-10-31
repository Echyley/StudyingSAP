package com.hybris.yprofile.common;

import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import org.junit.Test;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import static com.hybris.yprofile.constants.ProfileservicesConstants.PROFILE_CONSENT;

@UnitTest
public class UtilsTest {

    @Test
    public void shouldGetActiveConsentModelFromEvent() {
        // given
        final AbstractCommerceUserEvent mockEvent = mock(AbstractCommerceUserEvent.class);
        final CommerceConsentService mockService = mock(CommerceConsentService.class);
        final ConsentModel mockConsentModel = mock(ConsentModel.class);
        when(mockEvent.getSite()).thenReturn(mock(BaseSiteModel.class));
        when(mockEvent.getCustomer()).thenReturn(mock(CustomerModel.class));
        when(mockService.getLatestConsentTemplate(anyString(), any(BaseSiteModel.class))).thenReturn(mock(ConsentTemplateModel.class));
        when(mockService.getActiveConsent(any(CustomerModel.class), any(ConsentTemplateModel.class))).thenReturn(mockConsentModel);
        // when
        final Optional<ConsentModel> consentModel = Utils.getActiveConsentModelFromEvent(mockEvent, mockService);
        // then
        assertTrue(consentModel.isPresent());
        assertEquals(mockConsentModel, consentModel.get());
        verify(mockEvent).getSite();
        verify(mockEvent).getCustomer();
        verify(mockService).getLatestConsentTemplate(anyString(), any(BaseSiteModel.class));
    }

    @Test
    public void shouldGetEmptyConsentReferenceFromEvent() {
        // given
        final AbstractCommerceUserEvent mockEvent = mock(AbstractCommerceUserEvent.class);
        final CommerceConsentService mockService = mock(CommerceConsentService.class);
        when(mockEvent.getCustomer()).thenReturn(mock(CustomerModel.class));
        when(mockEvent.getSite()).thenReturn(mock(BaseSiteModel.class));
        when(mockService.getActiveConsent(any(CustomerModel.class), any(ConsentTemplateModel.class))).thenReturn(null);
        // when
        final String consent = Utils.getActiveConsentReferenceFromEvent(mockEvent, mockService);
        // then
        assertNull(consent);
        verify(mockEvent).getSite();
        verify(mockEvent).getCustomer();
        verify(mockService).getLatestConsentTemplate(anyString(), any(BaseSiteModel.class));
    }

    @Test
    public void shouldGetNonEmptyConsentReferenceFromEvent() {
        // given
        final AbstractCommerceUserEvent mockEvent = mock(AbstractCommerceUserEvent.class);
        final CommerceConsentService mockService = mock(CommerceConsentService.class);
        final ConsentModel mockConsentModel = mock(ConsentModel.class);
        final String consentRef = "test-cr";
        when(mockEvent.getSite()).thenReturn(mock(BaseSiteModel.class));
        when(mockEvent.getCustomer()).thenReturn(mock(CustomerModel.class));
        when(mockConsentModel.getConsentReference()).thenReturn(consentRef);
        when(mockService.getLatestConsentTemplate(anyString(), any(BaseSiteModel.class))).thenReturn(mock(ConsentTemplateModel.class));
        when(mockService.getActiveConsent(any(CustomerModel.class), any(ConsentTemplateModel.class))).thenReturn(mockConsentModel);
        // when
        final String consent = Utils.getActiveConsentReferenceFromEvent(mockEvent, mockService);
        // then
        assertNotNull(consent);
        assertEquals(consentRef, consent);
        verify(mockEvent).getSite();
        verify(mockEvent).getCustomer();
        verify(mockService).getLatestConsentTemplate(anyString(), any(BaseSiteModel.class));
    }

    @Test
    public void shouldGetEmptyConsentReferenceForCustomerAndBaseSite() {
        // given
        final CommerceConsentService mockService = mock(CommerceConsentService.class);
        final BaseSiteModel mockBaseSite = mock(BaseSiteModel.class);
        final CustomerModel mockCustomer = mock(CustomerModel.class);
        when(mockService.getActiveConsent(any(CustomerModel.class), any(ConsentTemplateModel.class))).thenReturn(null);
        // when
        final String consent = Utils.getActiveConsentReferenceForCustomerAndBaseSite(mockBaseSite, mockCustomer, mockService);
        // then
        assertNull(consent);
        verify(mockService).getLatestConsentTemplate(anyString(), any(BaseSiteModel.class));
    }

    @Test
    public void shouldGetNonEmptyConsentReferenceForCustomerAndBaseSite() {
        // given
        final CommerceConsentService mockService = mock(CommerceConsentService.class);
        final BaseSiteModel mockBaseSite = mock(BaseSiteModel.class);
        final CustomerModel mockCustomer = mock(CustomerModel.class);
        final ConsentModel mockConsentModel = mock(ConsentModel.class);
        final String consentRef = "test-cr";
        when(mockConsentModel.getConsentReference()).thenReturn(consentRef);
        when(mockService.getLatestConsentTemplate(anyString(), any(BaseSiteModel.class))).thenReturn(mock(ConsentTemplateModel.class));
        when(mockService.getActiveConsent(any(CustomerModel.class), any(ConsentTemplateModel.class))).thenReturn(mockConsentModel);
        // when
        final String consent = Utils.getActiveConsentReferenceForCustomerAndBaseSite(mockBaseSite, mockCustomer, mockService);
        // then
        assertNotNull(consent);
        assertEquals(consentRef, consent);
        verify(mockService).getLatestConsentTemplate(anyString(), any(BaseSiteModel.class));
    }

    @Test
    public void shouldNotFailWhenModelNotFound() {
        final CommerceConsentService mockService = mock(CommerceConsentService.class);
        final BaseSiteModel mockBaseSite = mock(BaseSiteModel.class);
        final CustomerModel mockCustomer = mock(CustomerModel.class);
        when(mockService.getLatestConsentTemplate(PROFILE_CONSENT, mockBaseSite)).thenThrow(ModelNotFoundException.class);
        // when
        final Optional<ConsentModel> consentModel = Utils.getActiveConsentModelForCustomerAndBaseSite(mockBaseSite, mockCustomer, mockService);
        // then
        assertEquals(consentModel, Optional.empty());
    }

    @Test
    public void shouldNotThrowIllegalArgumentExceptionWhenBaseSiteModelIsNull() {
        // given
        final CommerceConsentService mockService = mock(CommerceConsentService.class);
        final CustomerModel mockCustomer = mock(CustomerModel.class);
        // when
        final Optional<ConsentModel> consentModel = Utils.getActiveConsentModelForCustomerAndBaseSite(null, mockCustomer, mockService);
        // then
        assertEquals(consentModel, Optional.empty());
    }

    @Test
    public void shouldNotThrowIllegalArgumentExceptionWhenCustomerModelIsNull() {
        // given
        final CommerceConsentService mockService = mock(CommerceConsentService.class);
        final BaseSiteModel mockBaseSite = mock(BaseSiteModel.class);
        // when
        final Optional<ConsentModel> consentModel = Utils.getActiveConsentModelForCustomerAndBaseSite(mockBaseSite, null, mockService);
        // then
        assertEquals(consentModel, Optional.empty());
    }
}