/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.consent.services;

import com.hybris.charon.RawResponse;
import com.hybris.yprofile.consent.cookie.EnhancedCookieGenerator;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import com.hybris.yprofile.rest.clients.ConsentServiceClient;
import com.hybris.yprofile.services.ProfileConfigurationService;
import com.hybris.yprofile.services.RetrieveRestClientStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.hybris.yprofile.constants.ProfileservicesConstants.PROFILE_CONSENT_GIVEN;

@UnitTest
public class DefaultConsentServiceTest {

    private static final String TENANT_ID = "tenant";
    private static final String CONSENT_REFERENCE = "myConsentReference";
    private static final String SITE_ID = "mySite";
    public static final String CONSENT_REFERENCE_COOKIE_NAME = "mySite-consentReference";
    public static final String CONSENT_REFERENCE_SESSION_KEY = "consent-reference";
    public static final String PROFILE_ID_COOKIE_NAME = SITE_ID + "-profileId";

    private DefaultConsentService defaultConsentService;

    @Mock
    private ConsentServiceClient consentServiceClient;

    @Mock
    private EnhancedCookieGenerator cookieGenerator;

    @Mock
    private SessionService sessionService;

    @Mock
    private BaseSiteService baseSiteService;

    @Mock
    private UserService userService;

    @Mock
    private CommerceConsentService commerceConsentService;

    @Mock
    private ProfileConfigurationService profileConfigurationService;

    @Mock
    private RetrieveRestClientStrategy retrieveRestClientStrategy;

    @Mock
    private BaseSiteModel baseSiteModel;

    @Mock
    private ModelService modelService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private UserModel userModel;

    @Mock
    private CustomerModel customerModel;

    @Mock
    private ConsentModel consentModel;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private Cookie cookie;

    @Mock
    private RawResponse rawResponse;

    @Mock
    private Configuration configuration;

    private AutoCloseable closeable;

    @Before
    public void setUp() throws Exception {

        closeable = MockitoAnnotations.openMocks(this);

        defaultConsentService = new DefaultConsentService();
        defaultConsentService.setSessionService(sessionService);
        defaultConsentService.setUserService(userService);
        defaultConsentService.setProfileConfigurationService(profileConfigurationService);
        defaultConsentService.setRetrieveRestClientStrategy(retrieveRestClientStrategy);
        defaultConsentService.setBaseSiteService(baseSiteService);
        defaultConsentService.setCookieGenerator(cookieGenerator);
        defaultConsentService.setModelService(modelService);
        defaultConsentService.setConfigurationService(configurationService);
        defaultConsentService.setCommerceConsentService(commerceConsentService);

        when(baseSiteModel.getUid()).thenReturn(SITE_ID);
        when(userService.getCurrentUser()).thenReturn(this.userModel);
        when(userService.isAnonymousUser(any(UserModel.class))).thenReturn(false);
        when(retrieveRestClientStrategy.getConsentServiceRestClient()).thenReturn(consentServiceClient);
        when(profileConfigurationService.getTenant(SITE_ID)).thenReturn(TENANT_ID);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
        when(commerceConsentService.getConsent(anyString())).thenReturn(consentModel);
    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void assertWhenProfileAnonymousUserConsentHeaderIsEmptyShouldReturnFalse() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Anonymous-Consents")).thenReturn("%5B%5D");

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForAnonymousUser(request);

        assertFalse(result);
    }

    @Test
    public void assertWhenProfileAnonymousUserConsentCookieIsNullShouldReturnFalse() throws Exception {

        Cookie cookie = new Cookie("anonymous-consents", "%5B%7B%22templateCode%22%3A%22PROFILE%22%2C%22templateVersion%22%3A1%2C%22consentState%22%3Anull%7D%5D");
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = new Cookie[1];
        cookies[0] = cookie;
        when(request.getCookies()).thenReturn(cookies);

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForAnonymousUser(request);

        assertFalse(result);
    }

    @Test
    public void assertWhenProfileAnonymousUserConsentHeaderIsNullShouldReturnFalse() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Anonymous-Consents")).thenReturn("%5B%7B%22templateCode%22%3A%22PROFILE%22%2C%22templateVersion%22%3A1%2C%22consentState%22%3Anull%7D%5D");

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForAnonymousUser(request);

        assertFalse(result);
    }

    @Test
    public void assertWhenProfileAnonymousUserConsentCookieIsWithdrawnShouldReturnFalse() throws Exception {

        Cookie cookie = new Cookie("anonymous-consents", "%5B%7B%22templateCode%22%3A%22PROFILE%22%2C%22templateVersion%22%3A1%2C%22consentState%22%3A%22WITHDRAWN%22%7D%5D");

        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = new Cookie[1];
        cookies[0] = cookie;
        when(request.getCookies()).thenReturn(cookies);

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForAnonymousUser(request);

        assertFalse(result);
    }

    @Test
    public void assertWhenProfileAnonymousUserConsentHeaderIsWithdrawnShouldReturnFalse() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Anonymous-Consents")).thenReturn("%5B%7B%22templateCode%22%3A%22PROFILE%22%2C%22templateVersion%22%3A1%2C%22consentState%22%3A%22WITHDRAWN%22%7D%5D");

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForAnonymousUser(request);

        assertFalse(result);
    }

    @Test
    public void assertWhenProfileAnonymousUserConsentCookieIsGivenShouldReturnTrue() throws Exception {

        Cookie cookie = new Cookie("anonymous-consents", "%5B%7B%22templateCode%22%3A%22PROFILE%22%2C%22templateVersion%22%3A1%2C%22consentState%22%3A%22GIVEN%22%7D%5D");

        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = new Cookie[1];
        cookies[0] = cookie;
        when(request.getCookies()).thenReturn(cookies);

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForAnonymousUser(request);

        assertTrue(result);
    }

    @Test
    public void assertWhenProfileAnonymousUserConsentHeaderIsGivenShouldReturnTrue() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Anonymous-Consents")).thenReturn("%5B%7B%22templateCode%22%3A%22PROFILE%22%2C%22templateVersion%22%3A1%2C%22consentState%22%3A%22GIVEN%22%7D%5D");

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForAnonymousUser(request);

        assertTrue(result);
    }


    @Test
    public void assertWhenProfileLoggedInUserConsentIsNullShouldReturnFalse() throws Exception {

        Map<String, String> userConsents = new HashMap<>();
        userConsents.put("PROFILE", null);

        when(sessionService.getAttribute("user-consents")).thenReturn(userConsents);

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForLoggedInUser();

        assertFalse(result);
    }

    @Test
    public void assertWhenProfileLoggedInUserConsentIsWithdrawnShouldReturnFalse() throws Exception {

        Map<String, String> userConsents = new HashMap<>();
        userConsents.put("PROFILE", "WITHDRAWN");

        when(sessionService.getAttribute("user-consents")).thenReturn(userConsents);

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForLoggedInUser();

        assertFalse(result);
    }

    @Test
    public void assertWhenProfileLoggedInUserConsentIsGivenShouldReturnTrue() throws Exception {


        Map<String, String> userConsents = new HashMap<>();
        userConsents.put("PROFILE", "GIVEN");

        when(sessionService.getAttribute("user-consents")).thenReturn(userConsents);

        boolean result  = defaultConsentService.isProfileTrackingConsentGivenForLoggedInUser();

        assertTrue(result);
    }

    @Test
    public void verifyConsentReferenceFromCookieIsSavedInSessionAndConsentModel() {

        when(cookie.getName()).thenReturn(CONSENT_REFERENCE_COOKIE_NAME);
        when(cookie.getValue()).thenReturn(CONSENT_REFERENCE);
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{cookie});
        when(userService.getCurrentUser()).thenReturn(customerModel);
        when(userService.isAnonymousUser(any(UserModel.class))).thenReturn(false);
        when(consentModel.getConsentReference()).thenReturn("anotherConsentReference");
        when(commerceConsentService.getActiveConsent(any(), any())).thenReturn(consentModel);

        defaultConsentService.saveConsentReferenceInSessionAndConsentModel(httpServletRequest);

        verify(sessionService, times(1)).setAttribute(matches(CONSENT_REFERENCE_SESSION_KEY), matches(CONSENT_REFERENCE));
        verify(modelService, times(1)).save(any(UserModel.class));
        verify(modelService, times(1)).refresh(any(UserModel.class));
        verify(modelService, times(1)).save(any(ConsentModel.class));
        verify(modelService, times(1)).refresh(any(ConsentModel.class));
    }

    @Test
    public void verifyConsentReferenceFromHeaderIsSavedInSessionAndConsentModel() {
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString(anyString(), any())).thenReturn("x-consent-reference");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(anyString())).thenReturn(CONSENT_REFERENCE);
        when(userService.getCurrentUser()).thenReturn(customerModel);
        when(userService.isAnonymousUser(any(UserModel.class))).thenReturn(false);
        when(userService.isAnonymousUser(any())).thenReturn(false);
        when(consentModel.getConsentReference()).thenReturn("anotherConsentReference");
        when(commerceConsentService.getActiveConsent(any(), any())).thenReturn(consentModel);

        defaultConsentService.saveConsentReferenceInSessionAndConsentModel(request);

         verify(sessionService, times(1)).setAttribute(matches(CONSENT_REFERENCE_SESSION_KEY), matches(CONSENT_REFERENCE));
         verify(modelService, times(1)).save(any(UserModel.class));
         verify(modelService, times(1)).refresh(any(UserModel.class));
         verify(modelService, times(1)).save(any(ConsentModel.class));
         verify(modelService, times(1)).refresh(any(ConsentModel.class));
    }
    
    @Test
    public void verifyConsentReferenceIsSavedInSession() {
        defaultConsentService.saveConsentReferenceInSession(CONSENT_REFERENCE);

        verify(sessionService, times(1)).setAttribute(matches(CONSENT_REFERENCE_SESSION_KEY), matches(CONSENT_REFERENCE));
    }

    @Test
    public void verifyDeleteConsentReferenceRequestIsSentToProfileAndDeletedFromConsentModel() {

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(consentModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(consentModel.getCustomer()).thenReturn(customerModel);
        when(consentServiceClient.deleteConsentReference(anyString(), anyString())).thenReturn(Observable.just(rawResponse));
        when(rawResponse.getStatusCode()).thenReturn(204);
        when(rawResponse.header(anyString())).thenReturn(Optional.empty());

        defaultConsentService.deleteConsentReferenceInConsentServiceAndInConsentModel(consentModel, CONSENT_REFERENCE);

        verify(consentServiceClient, times(1)).deleteConsentReference(matches(CONSENT_REFERENCE), anyString());
        verify(modelService, times(1)).save(any(ConsentModel.class));

    }

    @Test
    public void verifyDeleteConsentReferenceRequestIsNotSentToProfileWhenProfileTrackingIsPaused() {
        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(true);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);

        defaultConsentService.deleteConsentReferenceInConsentServiceAndInConsentModel(consentModel, CONSENT_REFERENCE);

        verify(consentServiceClient, never()).deleteConsentReference(anyString(), anyString());
        verify(modelService, never()).save(any(UserModel.class));
    }

    @Test
    public void verifyDeleteConsentReferenceRequestIsNotSentToProfileWhenYaasConfigurationIsNotPresent() {

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(false);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(false);

        defaultConsentService.deleteConsentReferenceInConsentServiceAndInConsentModel(consentModel, CONSENT_REFERENCE);

        verify(consentServiceClient, never()).deleteConsentReference(anyString(), anyString());
        verify(modelService, never()).save(any(UserModel.class));

    }

    @Test
    public void verifyDeleteConsentReferenceRequestIsNotSentToProfileWhenConsentReferenceIsNotPresent() {

        when(profileConfigurationService.isProfileTrackingPaused()).thenReturn(true);
        when(profileConfigurationService.isConfigurationPresent()).thenReturn(true);
        when(consentServiceClient.deleteConsentReference(anyString(), anyString())).thenReturn(Observable.just(rawResponse));
        when(rawResponse.getStatusCode()).thenReturn(204);
        when(rawResponse.header(anyString())).thenReturn(Optional.empty());

        defaultConsentService.deleteConsentReferenceInConsentServiceAndInConsentModel(consentModel, null);

        verify(consentServiceClient, never()).deleteConsentReference(anyString(), anyString());
        verify(modelService, never()).save(any(UserModel.class));

    }

    @Test
    public void testSetProfileIdCookieWhenRequestHasNoCRCookie() {
        // given
        final String consentReferenceId = "7bdc6562-abf6-4dbb-8267-c20232bdadf5";
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{});
        // when
        defaultConsentService.setProfileIdCookie(httpServletRequest, httpServletResponse, consentReferenceId);
        // verify
        verify(cookieGenerator, times(1)).addCookie(httpServletResponse, consentReferenceId, true);
    }

    @Test
    public void testSetProfileIdCookieWhenRequestAlreadyHasCRCookie() {
        // given
        final String consentReferenceId = "7bdc6562-abf6-4dbb-8267-c20232bdadf5";
        when(cookie.getName()).thenReturn(PROFILE_ID_COOKIE_NAME);
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{cookie});
        // when
        defaultConsentService.setProfileIdCookie(httpServletRequest,httpServletResponse,consentReferenceId);
        // verify
        verify(httpServletResponse, never()).addCookie(any(Cookie.class));
    }

    @Test
    public void testSetProfileConsentCookieAndSession() {
        // given
        final boolean consent = true;
        when(cookie.getName()).thenReturn(PROFILE_CONSENT_GIVEN);
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{cookie});
        // when
        defaultConsentService.setProfileConsentCookieAndSession(httpServletRequest, httpServletResponse, consent);
        // then
        verify(cookieGenerator, times(2)).setCookieName(anyString());
        verify(cookieGenerator, times(1)).addCookie(any(HttpServletResponse.class), eq(String.valueOf(consent)), eq(!consent));
        verify(cookieGenerator, times(1)).removeCookie(any(HttpServletResponse.class));
    }

    @Test
    public void testSetProfileConsentCookieAndSessionWhenCookieExists() {
        // given
        final boolean consent = true;
        when(cookie.getName()).thenReturn(SITE_ID + "-" + PROFILE_CONSENT_GIVEN);
        when(cookie.getValue()).thenReturn(String.valueOf(consent));
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{cookie});
        // when
        defaultConsentService.setProfileConsentCookieAndSession(httpServletRequest, httpServletResponse, consent);
        // then
        verify(cookieGenerator, never()).setCookieName(anyString());
        verify(cookieGenerator, never()).addCookie(any(HttpServletResponse.class), eq(String.valueOf(consent)), eq(!consent));
        verify(cookieGenerator, never()).removeCookie(any(HttpServletResponse.class));
    }

}
