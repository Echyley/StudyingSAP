/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.reservation.impl;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertThrows;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;
import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.retail.oaa.commerce.services.common.util.impl.DefaultServiceUtils;
import com.sap.retail.oaa.commerce.services.constants.SapoaacommerceservicesConstants;
import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.request.ReservationAbapRequest;
import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response.ReservationResponse;
import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response.ReservationResultResponse;
import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.commerce.services.rest.util.exception.AuthenticationServiceException;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.sapoaacarintegration.services.reservation.ReservationResultHandler;
import com.sap.sapoaacarintegration.services.reservation.exception.ReservationException;
import com.sap.sapoaacarintegration.services.rest.impl.DefaultRestServiceConfiguration;
import com.sap.sapoaacarintegration.services.rest.util.impl.AuthenticationResult;
import com.sap.sapoaacarintegration.services.rest.util.impl.AuthenticationService;
import com.sap.sapoaacarintegration.services.rest.util.impl.DefaultHttpEntityBuilder;
import com.sap.sapoaacarintegration.services.rest.util.impl.DefaultHttpHeaderProvider;
import com.sap.sapoaacarintegration.services.rest.util.impl.DefaultURLProvider;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultReservationServiceTest {
    private static final String GUID = "123-456";
    private static final String ORDER_ID = "1234567890";
    private static final String TARGET_URL = "http://www.sap.com";
    private static final String PASSWORD = "password";
    private static final String USER = "username";
    private static final String SAPCARCLIENT = "sapCarClient";

    private static final String SERVICE_PATH = "service.resource.path.reservation";

    private static final String SERVICE_PATH_URL = "/sap/car/rest/oaa/reservation/";

    private static final String SERVICE_PATH_ITEMS = "service.resource.path.items";

    private static final String SERVICE_PATH_ITEMS_URL = "/items/";

    private DefaultReservationService classUnderTest;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Before
    public void setup() {
        classUnderTest = EasyMock.createMockBuilder(DefaultReservationService.class).addMockedMethod("initialize").createMock();

        // Mock rest service configuration
        final RestServiceConfiguration restServiceConfiguration = EasyMock.createMockBuilder(DefaultRestServiceConfiguration.class)
                .addMockedMethod("initializeConfiguration").createMock();
        restServiceConfiguration.setPassword(PASSWORD);
        restServiceConfiguration.setUser(USER);
        restServiceConfiguration.setTargetUrl(TARGET_URL);
        restServiceConfiguration.setSapCarClient(SAPCARCLIENT);

        final ServiceUtils serviceUtils = EasyMock.createNiceMock(DefaultServiceUtils.class);
        final ReservationResultHandler reservationResultHandler = EasyMock.createNiceMock(DefaultReservationResultHandler.class);
        final SessionService sessionService = EasyMock.createNiceMock(SessionService.class);

        classUnderTest.setConfigurationService(configurationService);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString(SERVICE_PATH)).thenReturn(SERVICE_PATH_URL);
        when(configuration.getString(SERVICE_PATH_ITEMS)).thenReturn(SERVICE_PATH_ITEMS_URL);


        //Mock Commonuutils
        final CommonUtils commonUtilsMock = EasyMock.createNiceMock(CommonUtils.class);
        EasyMock.expect(commonUtilsMock.isCAREnabled()).andReturn(true);
        EasyMock.expect(commonUtilsMock.isCOSEnabled()).andReturn(false);
        EasyMock.replay(commonUtilsMock);
        classUnderTest.setCommonUtils(commonUtilsMock);
        Assert.assertNotNull(classUnderTest.getCommonUtils());

        classUnderTest.setReservationResultHandler(reservationResultHandler);
        classUnderTest.setSessionService(sessionService);
        classUnderTest.setRestServiceConfiguration(restServiceConfiguration);
        classUnderTest.setServiceUtils(serviceUtils);
    }

    @Test
 	public void updateReservationExceptionTest()
 	{
 		final OrderModel orderModel = new OrderModel();
 		orderModel.setGuid(GUID);

 		final AuthenticationService authenticationServiceMock = EasyMock.createNiceMock(AuthenticationService.class);
 		EasyMock
 				.expect(authenticationServiceMock.execute(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
 						EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
 				.andThrow(new AuthenticationServiceException());
 		EasyMock.replay(authenticationServiceMock);
 		classUnderTest.setAuthenticationService(authenticationServiceMock);
 		Assert.assertNotNull(classUnderTest.getAuthenticationService());
 		assertThrows(ReservationException.class, () -> classUnderTest
 				.updateReservation(orderModel, SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER));
 	}

 	@Test
 	public void updateReservationBadGatewayExceptionTest()
 	{
 		final OrderModel orderModel = new OrderModel();
 		orderModel.setGuid(GUID);

 		classUnderTest.setAuthenticationService(
 				createAuthenticationServiceMockThrowingException(new HttpClientErrorException(HttpStatus.BAD_GATEWAY)));
 		assertThrows(BackendDownException.class, () -> classUnderTest
 				.updateReservation(orderModel, SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER));
 	}

 	@Test
 	public void updateReservationUnavailableExceptionTest()
 	{
 		final OrderModel orderModel = new OrderModel();
 		orderModel.setGuid(GUID);

 		classUnderTest.setAuthenticationService(
 				createAuthenticationServiceMockThrowingException(new HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE)));
 		assertThrows(BackendDownException.class, () -> classUnderTest
 				.updateReservation(orderModel, SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER));
 	}

 	@Test
 	public void updateReservationTooManyRequestsExceptionTest()
 	{
 		final OrderModel orderModel = new OrderModel();
 		orderModel.setGuid(GUID);

 		classUnderTest.setAuthenticationService(
 				createAuthenticationServiceMockThrowingException(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS)));

 		assertThrows(BackendDownException.class, () -> classUnderTest
 				.updateReservation(orderModel, SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER));
 	}

 	@Test
 	public void updateReservationBadRequestExceptionTest()
 	{
 		final OrderModel orderModel = new OrderModel();
 		orderModel.setGuid(GUID);

 		classUnderTest.setAuthenticationService(
 				createAuthenticationServiceMockThrowingException(new HttpClientErrorException(HttpStatus.BAD_REQUEST)));

 		assertThrows(ReservationException.class, () -> classUnderTest
 				.updateReservation(orderModel, SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER));
 	}

 	@Test
 	public void updateReservationResourceAccessExceptionTest()
 	{
 		final OrderModel orderModel = new OrderModel();
 		orderModel.setGuid(GUID);

 		classUnderTest.setAuthenticationService(createAuthenticationServiceMockThrowingException(new ResourceAccessException("")));

 		assertThrows(BackendDownException.class, () -> classUnderTest
 				.updateReservation(orderModel, SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER));
 	}

 	@Test
 	public void updateReservationGatewayTimeoutExceptionTest()
 	{
 		final OrderModel orderModel = new OrderModel();
 		orderModel.setGuid(GUID);

 		final AuthenticationService authenticationServiceMock = EasyMock.createNiceMock(AuthenticationService.class);
 		EasyMock
 				.expect(authenticationServiceMock.execute(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
 						EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
 				.andThrow(new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT));
 		EasyMock.replay(authenticationServiceMock);
 		classUnderTest.setAuthenticationService(authenticationServiceMock);

 		assertThrows(BackendDownException.class, () -> classUnderTest
 				.updateReservation(orderModel, SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER));
 	}

 	@Test
 	public void updateReservationBackendDownExceptionTest()
 	{
 		final OrderModel orderModel = new OrderModel();
 		orderModel.setGuid(GUID);

 		final AuthenticationService authenticationServiceMock = EasyMock.createNiceMock(AuthenticationService.class);

 		EasyMock
 				.expect(authenticationServiceMock.execute(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
 						EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
 				.andThrow(new BackendDownException());
 		EasyMock.replay(authenticationServiceMock);
 		classUnderTest.setAuthenticationService(authenticationServiceMock);
 		Assert.assertNotNull(classUnderTest.getAuthenticationService());

 		assertThrows(BackendDownException.class, () -> classUnderTest
 				.updateReservation(orderModel, SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER));
 	}

    @Test
    public void deleteReservationTest() throws URISyntaxException {
        // Mock AuthenticationService
        final AuthenticationService authenticationServiceMock = EasyMock.createNiceMock(AuthenticationService.class);
        EasyMock
                .expect(authenticationServiceMock.execute(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
                        EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
                .andReturn(new AuthenticationResult());
        EasyMock.replay(authenticationServiceMock);
        classUnderTest.setAuthenticationService(authenticationServiceMock);
        Assert.assertNotNull(classUnderTest.getAuthenticationService());

        // Mock DefaultHttpHeaderProvider
        final DefaultHttpHeaderProvider httpHeaderProviderMock = EasyMock.createNiceMock(DefaultHttpHeaderProvider.class);
        EasyMock
                .expect(httpHeaderProviderMock.compileHttpHeader(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
                .andReturn(new HttpHeaders());
        EasyMock.expect(httpHeaderProviderMock.appendCsrfToHeader(EasyMock.anyObject(HttpHeaders.class),
                EasyMock.anyObject(HttpHeaders.class))).andReturn(new HttpHeaders());
        EasyMock.expect(httpHeaderProviderMock.appendCookieToHeader(EasyMock.anyObject(HttpHeaders.class),
                EasyMock.anyObject(HttpHeaders.class))).andReturn(new HttpHeaders());
        EasyMock.replay(httpHeaderProviderMock);
        classUnderTest.setHttpHeaderProvider(httpHeaderProviderMock);
        Assert.assertNotNull(classUnderTest.getHttpHeaderProvider());

        final OrderModel order = new OrderModel();
        order.setGuid(GUID);

        // Mock DefaultHttpEntityBuilder
        final HttpEntity<ReservationAbapRequest> entity = new HttpEntity<ReservationAbapRequest>(null, null);
        final DefaultHttpEntityBuilder httpEntityBuilderMock = EasyMock.createNiceMock(DefaultHttpEntityBuilder.class);
        EasyMock.expect(httpEntityBuilderMock.createHttpEntity(EasyMock.anyObject(HttpHeaders.class),
                EasyMock.anyObject(ReservationAbapRequest.class))).andReturn(entity).anyTimes();
        EasyMock.replay(httpEntityBuilderMock);
        classUnderTest.setHttpEntityBuilder(httpEntityBuilderMock);
        Assert.assertNotNull(classUnderTest.getHttpEntityBuilder());

        final URI targetUri = new URI(TARGET_URL);

        // Mock RestTemplate
        final RestTemplate restTemplateMock = EasyMock.createNiceMock(RestTemplate.class);

        classUnderTest.setRestTemplate(restTemplateMock);
        Assert.assertNotNull(classUnderTest.getRestTemplate());

        // Mock DefaultURLProvider
        final DefaultURLProvider urlProviderMock = EasyMock.createNiceMock(DefaultURLProvider.class);
        EasyMock.expect(urlProviderMock.compileURI(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
                EasyMock.anyObject(String.class))).andReturn(targetUri);
        EasyMock.replay(urlProviderMock);
        classUnderTest.setUrlProvider(urlProviderMock);
        Assert.assertNotNull(classUnderTest.getUrlProvider());

        classUnderTest.deleteReservation(order);
    }

    @Test
    public void deleteReservationItemTest() throws URISyntaxException {
        // Mock AuthenticationService
        final AuthenticationService authenticationServiceMock = EasyMock.createNiceMock(AuthenticationService.class);
        EasyMock
                .expect(authenticationServiceMock.execute(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
                        EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
                .andReturn(new AuthenticationResult());
        EasyMock.replay(authenticationServiceMock);
        classUnderTest.setAuthenticationService(authenticationServiceMock);
        Assert.assertNotNull(classUnderTest.getAuthenticationService());

        // Mock DefaultHttpHeaderProvider
        final DefaultHttpHeaderProvider httpHeaderProviderMock = EasyMock.createNiceMock(DefaultHttpHeaderProvider.class);
        EasyMock
                .expect(httpHeaderProviderMock.compileHttpHeader(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
                .andReturn(new HttpHeaders());
        EasyMock.expect(httpHeaderProviderMock.appendCsrfToHeader(EasyMock.anyObject(HttpHeaders.class),
                EasyMock.anyObject(HttpHeaders.class))).andReturn(new HttpHeaders());
        EasyMock.expect(httpHeaderProviderMock.appendCookieToHeader(EasyMock.anyObject(HttpHeaders.class),
                EasyMock.anyObject(HttpHeaders.class))).andReturn(new HttpHeaders());
        EasyMock.replay(httpHeaderProviderMock);
        classUnderTest.setHttpHeaderProvider(httpHeaderProviderMock);
        Assert.assertNotNull(classUnderTest.getHttpHeaderProvider());

        final OrderModel order = new OrderModel();
        order.setGuid(GUID);
        final OrderEntryModel orderEntry = new OrderEntryModel();
        orderEntry.setEntryNumber(Integer.valueOf(0));

        // Mock DefaultHttpEntityBuilder
        final HttpEntity<ReservationAbapRequest> entity = new HttpEntity<ReservationAbapRequest>(null, null);
        final DefaultHttpEntityBuilder httpEntityBuilderMock = EasyMock.createNiceMock(DefaultHttpEntityBuilder.class);
        EasyMock.expect(httpEntityBuilderMock.createHttpEntity(EasyMock.anyObject(HttpHeaders.class),
                EasyMock.anyObject(ReservationAbapRequest.class))).andReturn(entity).anyTimes();
        EasyMock.replay(httpEntityBuilderMock);
        classUnderTest.setHttpEntityBuilder(httpEntityBuilderMock);
        Assert.assertNotNull(classUnderTest.getHttpEntityBuilder());

        final URI targetUri = new URI(TARGET_URL);

        // Mock RestTemplate
        final RestTemplate restTemplateMock = EasyMock.createNiceMock(RestTemplate.class);

        classUnderTest.setRestTemplate(restTemplateMock);
        Assert.assertNotNull(classUnderTest.getRestTemplate());

        // Mock DefaultURLProvider
        final DefaultURLProvider urlProviderMock = EasyMock.createNiceMock(DefaultURLProvider.class);
        EasyMock.expect(urlProviderMock.compileURI(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
                EasyMock.anyObject(String.class))).andReturn(targetUri);
        EasyMock.replay(urlProviderMock);
        classUnderTest.setUrlProvider(urlProviderMock);
        Assert.assertNotNull(classUnderTest.getUrlProvider());

        classUnderTest.deleteReservationItem(order, orderEntry);
    }

    @Test
 	public void deleteReservationExceptionTest()
 	{
 		final OrderModel orderModel = new OrderModel();
 		orderModel.setGuid(GUID);

 		classUnderTest
 				.setAuthenticationService(createAuthenticationServiceMockThrowingException(new AuthenticationServiceException()));
 		Assert.assertNotNull(classUnderTest.getAuthenticationService());
 		assertThrows(ReservationException.class, () -> classUnderTest.deleteReservation(orderModel));
 	}

 	@Test
 	public void deleteReservationItemExceptionTest()
 	{
 		final OrderModel orderModel = new OrderModel();
 		orderModel.setGuid(GUID);
 		final OrderEntryModel orderEntry = new OrderEntryModel();
 		orderEntry.setEntryNumber(Integer.valueOf(0));

 		classUnderTest
 				.setAuthenticationService(createAuthenticationServiceMockThrowingException(new AuthenticationServiceException()));
 		Assert.assertNotNull(classUnderTest.getAuthenticationService());

 		assertThrows(ReservationException.class, () -> classUnderTest.deleteReservationItem(orderModel, orderEntry));
 	}

 	@Test
 	public void deleteReservationHttpClientExceptionTest()
 	{
 		final OrderModel orderModel = new OrderModel();
 		orderModel.setGuid(GUID);

 		classUnderTest.setAuthenticationService(
 				createAuthenticationServiceMockThrowingException(new HttpClientErrorException(HttpStatus.BAD_GATEWAY)));
 		Assert.assertNotNull(classUnderTest.getAuthenticationService());
 		assertThrows(BackendDownException.class, () -> classUnderTest.deleteReservation(orderModel));
 	}

 	@Test
 	public void deleteReservationItemHttpClientExceptionTest()
 	{
 		final OrderModel orderModel = new OrderModel();
 		orderModel.setGuid(GUID);
 		final OrderEntryModel orderEntry = new OrderEntryModel();
 		orderEntry.setEntryNumber(Integer.valueOf(0));

 		classUnderTest.setAuthenticationService(
 				createAuthenticationServiceMockThrowingException(new HttpClientErrorException(HttpStatus.BAD_GATEWAY)));
 		Assert.assertNotNull(classUnderTest.getAuthenticationService());
 		assertThrows(BackendDownException.class, () -> classUnderTest.deleteReservationItem(orderModel, orderEntry));
 	}

 	@Test
 	public void deleteReservationResourceAccessExceptionTest()
 	{
 		final OrderModel orderModel = new OrderModel();
 		orderModel.setGuid(GUID);

 		classUnderTest.setAuthenticationService(createAuthenticationServiceMockThrowingException(new ResourceAccessException("")));
 		Assert.assertNotNull(classUnderTest.getAuthenticationService());
 		assertThrows(BackendDownException.class, () -> classUnderTest.deleteReservation(orderModel));
 	}

 	@Test
 	public void deleteReservationItemResourceAccessExceptionTest()
 	{
 		final OrderModel orderModel = new OrderModel();
 		orderModel.setGuid(GUID);
 		final OrderEntryModel orderEntry = new OrderEntryModel();
 		orderEntry.setEntryNumber(Integer.valueOf(0));

 		classUnderTest.setAuthenticationService(createAuthenticationServiceMockThrowingException(new ResourceAccessException("")));
 		Assert.assertNotNull(classUnderTest.getAuthenticationService());
 		assertThrows(BackendDownException.class, () -> classUnderTest.deleteReservationItem(orderModel, orderEntry));
 	}

    private AuthenticationService createAuthenticationServiceMockThrowingException(final Exception e) {
        final AuthenticationService authenticationServiceMock = EasyMock.createNiceMock(AuthenticationService.class);
        EasyMock
                .expect(authenticationServiceMock.execute(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
                        EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
                .andThrow(e).anyTimes();
        EasyMock.replay(authenticationServiceMock);
        return authenticationServiceMock;
    }
}
