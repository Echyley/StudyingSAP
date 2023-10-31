/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.listeners;

import com.hybris.yprofile.consent.filters.ConsentLayerFilter;
import com.hybris.yprofile.consent.services.ConsentService;
import com.hybris.yprofile.services.ProfileConfigurationService;
import com.hybris.yprofile.services.ProfileTransactionService;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.event.LoginSuccessEvent;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.session.impl.DefaultSessionTokenService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.WebSessionFunctions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class LoginSuccessEventListenerTest {

    private static final String CONSENT_REFERENCE_ID = "1410d071-4efb-44e8-b33c-9ba30a03f24a";

    private LoginSuccessEventListener loginSuccessEventListener;

    @Mock
    private ProfileTransactionService profileTransactionService;

    @Mock
    private DefaultSessionTokenService defaultSessionTokenService;

    @Mock
    private ProfileConfigurationService profileConfigurationService;

    @Mock
    private ConsentService consentService;

    @Mock
    private LoginSuccessEvent loginSuccessEvent;

    @Mock
    private ConsentLayerFilter consentLayerFilter;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private CommerceConsentService commerceConsentService;

    private AutoCloseable closeable;

    @Before
    public void setUp() throws Exception {

        closeable = MockitoAnnotations.openMocks(this);
        loginSuccessEventListener = new LoginSuccessEventListener();
        loginSuccessEventListener.setConsentService(consentService);
        loginSuccessEventListener.setProfileTransactionService(profileTransactionService);
        loginSuccessEventListener.setDefaultSessionTokenService(defaultSessionTokenService);
        loginSuccessEventListener.setProfileConfigurationService(profileConfigurationService);
        loginSuccessEventListener.setConsentLayerFilter(consentLayerFilter);
        loginSuccessEventListener.setCommerceConsentService(commerceConsentService);

        when(defaultSessionTokenService.getOrCreateSessionToken()).thenReturn("someToken");
        BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
        when(baseStoreModel.getUid()).thenReturn("myBaseStore");
        when(loginSuccessEvent.getBaseStore()).thenReturn(baseStoreModel);

    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void verifyLoginEventIsSentWhenConsentReferenceFromSessionIsNotNull() {

        try (MockedStatic<WebSessionFunctions> webSessionFunctionsMockedStatic = Mockito.mockStatic(WebSessionFunctions.class)) {
            webSessionFunctionsMockedStatic.when(WebSessionFunctions::getCurrentHttpServletRequest).thenReturn(httpServletRequest);

            when(consentService.getConsentReferenceFromSession()).thenReturn(CONSENT_REFERENCE_ID);
            CustomerModel customerModel = mock(CustomerModel.class);

            when(loginSuccessEvent.getCustomer()).thenReturn(customerModel);
            when(profileConfigurationService.isProfileTagDebugEnabledInSession()).thenReturn(true);

            loginSuccessEventListener.onSiteEvent(loginSuccessEvent);

            verify(consentLayerFilter, times(1)).runFilterLogic(any(HttpServletRequest.class));
            verify(this.profileTransactionService, times(1)).sendLoginEvent(any(CustomerModel.class), eq(CONSENT_REFERENCE_ID), anyString(), anyString());
        }
    }

    @Test
    public void verifyLoginEventIsSentWhenConsentReferenceFromCustomerModelIsNotNull() {

        try (MockedStatic<WebSessionFunctions> webSessionFunctionsMockedStatic = Mockito.mockStatic(WebSessionFunctions.class)) {
            webSessionFunctionsMockedStatic.when(WebSessionFunctions::getCurrentHttpServletRequest).thenReturn(httpServletRequest);

            when(consentService.getConsentReferenceFromSession()).thenReturn(null);
            CustomerModel customerModel = mock(CustomerModel.class);
            ConsentModel consentModel = mock(ConsentModel.class);
            when(consentModel.getConsentReference()).thenReturn(CONSENT_REFERENCE_ID);
            when(commerceConsentService.getActiveConsent(any(), any())).thenReturn(consentModel);

            when(loginSuccessEvent.getCustomer()).thenReturn(customerModel);
            when(loginSuccessEvent.getSite()).thenReturn(mock(BaseSiteModel.class));
            when(profileConfigurationService.isProfileTagDebugEnabledInSession()).thenReturn(true);

            loginSuccessEventListener.onSiteEvent(loginSuccessEvent);

            verify(consentLayerFilter, times(1)).runFilterLogic(any(HttpServletRequest.class));
            verify(this.profileTransactionService, times(1)).sendLoginEvent(any(CustomerModel.class), eq(CONSENT_REFERENCE_ID), anyString(), anyString());
        }
    }

    @Test
    public void verifyLoginEventIsNotSentWhenConsentReferenceIsNull() {

        try (MockedStatic<WebSessionFunctions> webSessionFunctionsMockedStatic = Mockito.mockStatic(WebSessionFunctions.class)) {
            webSessionFunctionsMockedStatic.when(WebSessionFunctions::getCurrentHttpServletRequest).thenReturn(httpServletRequest);

            when(consentService.getConsentReferenceFromSession()).thenReturn(null);
            CustomerModel customerModel = mock(CustomerModel.class);

            ConsentModel consentModel = mock(ConsentModel.class);
            when(consentModel.getConsentReference()).thenReturn(null);
            when(commerceConsentService.getActiveConsent(any(), any())).thenReturn(consentModel);

            when(loginSuccessEvent.getCustomer()).thenReturn(customerModel);
            when(profileConfigurationService.isProfileTagDebugEnabledInSession()).thenReturn(true);

            loginSuccessEventListener.onSiteEvent(loginSuccessEvent);

            verify(consentLayerFilter, times(1)).runFilterLogic(any(HttpServletRequest.class));
            verify(this.profileTransactionService, never()).sendLoginEvent(any(CustomerModel.class), anyString(), anyString(), anyString());
        }
    }

    @Test
    public void verifyLoginEventShouldUseConsentReferenceFromSession() {

        try (MockedStatic<WebSessionFunctions> webSessionFunctionsMockedStatic = Mockito.mockStatic(WebSessionFunctions.class)) {
            webSessionFunctionsMockedStatic.when(WebSessionFunctions::getCurrentHttpServletRequest).thenReturn(mock(HttpServletRequest.class));

            //the consent reference from the session is normally the anonymous consent ref
            //the login event should always use the one coming from the session and not the one from the model
            //this will trigger the merge then at the end will be one profile.
            when(consentService.getConsentReferenceFromSession()).thenReturn(CONSENT_REFERENCE_ID);
            CustomerModel customerModel = mock(CustomerModel.class);
            final String anotherConsentRefernce = "31ad0c49-53d5-49fb-b2db-b1533a0063b0";

            ConsentModel consentModel = mock(ConsentModel.class);
            when(consentModel.getConsentReference()).thenReturn(anotherConsentRefernce);
            when(commerceConsentService.getActiveConsent(any(), any())).thenReturn(consentModel);

            when(loginSuccessEvent.getCustomer()).thenReturn(customerModel);
            when(profileConfigurationService.isProfileTagDebugEnabledInSession()).thenReturn(true);

            loginSuccessEventListener.onSiteEvent(loginSuccessEvent);

            verify(consentLayerFilter, times(1)).runFilterLogic(any(HttpServletRequest.class));
            verify(this.profileTransactionService, times(1)).sendLoginEvent(any(CustomerModel.class), eq(CONSENT_REFERENCE_ID), anyString(), anyString());
        }
    }
}
