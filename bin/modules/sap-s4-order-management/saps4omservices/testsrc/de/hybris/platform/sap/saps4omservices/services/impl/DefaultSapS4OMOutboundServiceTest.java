/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.BasicCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.apiregistryservices.utils.DefaultRestTemplateFactory;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.sap.sapmodel.enums.SAPDestinationKey;
import de.hybris.platform.sap.sapmodel.services.SapMappedDestinationService;
import de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException;
import de.hybris.platform.sap.saps4omservices.filter.dto.FilterData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMItemData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMItemsData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMOrderResult;
import de.hybris.platform.saps4omservices.dto.SAPS4OMOrders;
import de.hybris.platform.saps4omservices.dto.SAPS4OMRequestData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMResponseData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.localization.Localization;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4OMOutboundServiceTest {

	@Spy
	@InjectMocks
	DefaultSapS4OMOutboundService defaultSapS4OMOutboundService;
	
	@Mock
	DestinationService<ConsumedDestinationModel> destinationService;

	@Mock
	BasicCredentialModel basicCredentialModel;

	@Mock
	SapMappedDestinationService<ConsumedDestinationModel> sapMappedDestinationService;

	@Mock
	RestTemplate restTemplates;

	@Mock
	SessionService sessionService;
	
	@Mock
	private Session session;
	
	@Mock
	private CommonI18NService commonI18NService;
	
	@Mock
	private ConfigurationService configurationService;
	
	@Mock
	private DefaultRestTemplateFactory restTemplateFactory;
	
	@Mock
	private BaseStoreService baseStoreService;
	
	@Mock
	BaseStoreModel mockBaseStoreModel;
	
	
	private static final String DESTINATION_ID_SIMULATION = "testSimulationDestinationID";
	private static final String DESTINATION_TARGET_ID_SIMULATION = "testSimulationTargetID";
	private static final String DESTINATION_ID_ORDER = "testOrderDestinationID";
	private static final String DESTINATION_TARGET_ID_ORDER = "testOrderTargetID";
	private static final String DESTINATION_ID = "testDestinationID";
	private static final String TEST_CREDENTIAL_ID = "testCredentialID";
	private static final String BASIC_CREDENTIAL_ITEMTYPE = "BasicCredential";

	private static final String TEST_USERNAME = "username";
	private static final String TEST_PASSWORD = "password";
	private static final String X_CSRF_TOKEN = "testURL";
	private static final String ORDER_ID = "testOrderID";
	private static final String CSRF_URL = "csrfURL";
	private static final String URL = "url";
	private static final String ORDER_SRV_URL = "<protocol>://<host>:<port>/sap/opu/odata/SAP/API_SALES_ORDER_SRV/A_SalesOrder";
	private static final String ORDER_SRV_URL_WITH_PATH_VAR = "<protocol>://<host>:<port>/sap/opu/odata/SAP/API_SALES_ORDER_SRV/A_SalesOrder('testOrderID')?&$expand=to_Item/to_PricingElement,to_PricingElement,to_Partner";
	
	private static final String EXPAND_PARAM_ITEM = "to_Item";
	private static final String EXPAND_PARAM_PRICING_ELEMENT = "to_PricingElement";
	private static final String EXPAND_PARAM_PARTNER = "to_Partner";
	private static final String EXPAND_SUFFIX = "$expand=";
	private static final String LANGUAGE_ISO_CODE = "en";
	private static final String ERROR_MESSAGE_LOCALE = "message.error.backendcallfailure";
	private static final String REST_CLIENT_ERROR_LOCALE = "message.error.destinationconnectionfailure";

	
	@Before
	public void init() {
		defaultSapS4OMOutboundService.setS4omRestTemplate(restTemplates);
		defaultSapS4OMOutboundService.setDestinationService(destinationService);
		defaultSapS4OMOutboundService.setCommonI18NService(commonI18NService);
		defaultSapS4OMOutboundService.setRestTemplateFactory(restTemplateFactory);
		defaultSapS4OMOutboundService.setSapMappedDestinationService(sapMappedDestinationService);
		defaultSapS4OMOutboundService.setBaseStoreService(baseStoreService);
		
		HttpHeaders header = new HttpHeaders();
		sessionService.setAttribute("S4OM_AUTH_RESULT_HTTP_HEADERS", header);
		defaultSapS4OMOutboundService.setSessionService(sessionService);
		when(sessionService.getCurrentSession()).thenReturn(session);
		LanguageModel languageModel = spy(LanguageModel.class);
		when(defaultSapS4OMOutboundService.getCommonI18NService().getCurrentLanguage()).thenReturn(languageModel);
		when(defaultSapS4OMOutboundService.getCommonI18NService().getCurrentLanguage().getIsocode()).thenReturn(LANGUAGE_ISO_CODE);
		doNothing().when(defaultSapS4OMOutboundService).setTimeouts(restTemplates);
	} 
	
	@Test
	public void testOrderSimulationSuccessWithTokenInSession() { 
		SAPS4OMRequestData sapS4OMRequestData = mock(SAPS4OMRequestData.class);
		ConsumedDestinationModel consumedDestinationModelMock = mock(ConsumedDestinationModel.class);
		BasicCredentialModel credential = mock(BasicCredentialModel.class);
		lenient().when(consumedDestinationModelMock.getCredential()).thenReturn(credential);
		lenient().when(credential.getId()).thenReturn(TEST_CREDENTIAL_ID);
		lenient().when(credential.getItemtype()).thenReturn(BASIC_CREDENTIAL_ITEMTYPE);
 		
		HttpHeaders headerFromSession = new HttpHeaders();
		headerFromSession.add(DESTINATION_ID_SIMULATION, DESTINATION_ID);
		
		HttpEntity<SAPS4OMRequestData> requestEntity = new HttpEntity<>(sapS4OMRequestData, headerFromSession);
		SAPS4OMData responseData = getResponseData();

		ResponseEntity<SAPS4OMData> responseEntityData = new ResponseEntity<>(responseData, headerFromSession,HttpStatus.ACCEPTED);
		
		when(consumedDestinationModelMock.getId()).thenReturn(DESTINATION_ID_SIMULATION);
		when(consumedDestinationModelMock.getUrl()).thenReturn(URL);
		when(defaultSapS4OMOutboundService.getHttpHeaderFromSession(LANGUAGE_ISO_CODE + DESTINATION_ID_SIMULATION)).thenReturn(headerFromSession);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(mockBaseStoreModel);
		when(defaultSapS4OMOutboundService.getSapMappedDestinationService().getDestinationByKeyForBaseStore(mockBaseStoreModel, SAPDestinationKey.S4OMORDERSIMULATEDESTINATION)).thenReturn(Optional.of(consumedDestinationModelMock));
		when(defaultSapS4OMOutboundService.getS4omRestTemplate().exchange(URL,HttpMethod.POST, requestEntity, SAPS4OMData.class)).thenReturn(responseEntityData);
		when(defaultSapS4OMOutboundService.sendRequest(consumedDestinationModelMock, URL, HttpMethod.POST, sapS4OMRequestData, SAPS4OMData.class)).thenReturn(responseEntityData);
		SAPS4OMData result = defaultSapS4OMOutboundService.simulateOrder(DESTINATION_ID_SIMULATION,DESTINATION_TARGET_ID_SIMULATION , sapS4OMRequestData);
		
		assertEquals("00",result.getResult().getDivision());
		assertEquals("30",result.getResult().getDistributionChannel());
	}
	
	@Test
	public void testCreateOrderSuccessWithTokenInSession() { 
		SAPS4OMRequestData sapS4OMRequestData = mock(SAPS4OMRequestData.class);
		ConsumedDestinationModel consumedDestinationModelMock = mock(ConsumedDestinationModel.class);
		BasicCredentialModel credential = mock(BasicCredentialModel.class);
		lenient().when(consumedDestinationModelMock.getCredential()).thenReturn(credential);
		lenient().when(credential.getId()).thenReturn(TEST_CREDENTIAL_ID);
		lenient().when(credential.getItemtype()).thenReturn(BASIC_CREDENTIAL_ITEMTYPE);
		
		HttpHeaders header = new HttpHeaders();
		header.add(DESTINATION_ID_ORDER, DESTINATION_ID);
		HttpEntity<SAPS4OMRequestData> requestEntity = new HttpEntity<>(sapS4OMRequestData, header);
		SAPS4OMData responseData = getResponseData();

		ResponseEntity<SAPS4OMData> responseEntityData = new ResponseEntity<>(responseData, header,HttpStatus.ACCEPTED);
		
		when(consumedDestinationModelMock.getId()).thenReturn(DESTINATION_ID_ORDER);
		when(consumedDestinationModelMock.getUrl()).thenReturn(URL);

		when(defaultSapS4OMOutboundService.getHttpHeaderFromSession(LANGUAGE_ISO_CODE + DESTINATION_ID_ORDER)).thenReturn(header);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(mockBaseStoreModel);
		when(defaultSapS4OMOutboundService.getSapMappedDestinationService().getDestinationByKeyForBaseStore(mockBaseStoreModel, SAPDestinationKey.S4OMORDERDESTINATION)).thenReturn(Optional.of(consumedDestinationModelMock));
		when(defaultSapS4OMOutboundService.getS4omRestTemplate().exchange(URL,HttpMethod.POST, requestEntity, SAPS4OMData.class)).thenReturn(responseEntityData);
		when(defaultSapS4OMOutboundService.sendRequest(consumedDestinationModelMock, URL, HttpMethod.POST, sapS4OMRequestData, SAPS4OMData.class)).thenReturn(responseEntityData);
		SAPS4OMData result = defaultSapS4OMOutboundService.createOrder(DESTINATION_ID_ORDER,DESTINATION_TARGET_ID_ORDER , sapS4OMRequestData);

		assertNotNull(result);
	}
	
	@Test
	public void testgetCSRFTokenFromHeaderSuccess() { 
		
		ConsumedDestinationModel consumedDestinationModelMock = mock(ConsumedDestinationModel.class);
		BasicCredentialModel credential = mock(BasicCredentialModel.class);
		lenient().when(consumedDestinationModelMock.getCredential()).thenReturn(credential);
		lenient().when(credential.getId()).thenReturn(TEST_CREDENTIAL_ID);
		lenient().when(credential.getItemtype()).thenReturn(BASIC_CREDENTIAL_ITEMTYPE);
		
		when(consumedDestinationModelMock.getUrl()).thenReturn(CSRF_URL);
				
		HttpHeaders responseHeader = new HttpHeaders();
		responseHeader.set(X_CSRF_TOKEN, "token");
		
		ResponseEntity<String> csrfTokenExchange = new ResponseEntity<> (responseHeader,HttpStatus.OK);
		try {
			when(defaultSapS4OMOutboundService.getRestTemplateFactory().getRestTemplate(consumedDestinationModelMock)).thenReturn(restTemplates);
		} catch (CredentialException e) {
			assertNotNull(e);
		}
		lenient().when(restTemplates.exchange(any(String.class),any(HttpMethod.class), any(HttpEntity.class), (Class<String>) any())).thenReturn(csrfTokenExchange);

		HttpHeaders result = defaultSapS4OMOutboundService.getAuthenticationDetails(consumedDestinationModelMock);
	
		assertEquals(5, result.size());
	}
	
	@Test
	public void testSimulateOrderSuccessWhenSessionReturnsNoHeaders() { 
		
		SAPS4OMRequestData sapS4OMRequestData = mock(SAPS4OMRequestData.class);
		
		
		ConsumedDestinationModel consumedDestinationModelMock = mock(ConsumedDestinationModel.class);
		BasicCredentialModel credential = mock(BasicCredentialModel.class);
		lenient().when(consumedDestinationModelMock.getCredential()).thenReturn(credential);
		lenient().when(credential.getId()).thenReturn(TEST_CREDENTIAL_ID);
		lenient().when(credential.getItemtype()).thenReturn(BASIC_CREDENTIAL_ITEMTYPE);
		
		when(consumedDestinationModelMock.getUrl()).thenReturn(CSRF_URL);
		
		HttpHeaders header = new HttpHeaders();
		header.add(DESTINATION_ID_SIMULATION, DESTINATION_ID);
		SAPS4OMData responseData = getResponseData();
		
		ResponseEntity<String> csrfTokenExchange = new ResponseEntity<> (header,HttpStatus.OK);
		ResponseEntity<SAPS4OMData> responseEntityData = new ResponseEntity<>(responseData, header,HttpStatus.ACCEPTED);
		
		when(baseStoreService.getCurrentBaseStore()).thenReturn(mockBaseStoreModel);
		when(defaultSapS4OMOutboundService.getSapMappedDestinationService().getDestinationByKeyForBaseStore(mockBaseStoreModel, SAPDestinationKey.S4OMORDERSIMULATEDESTINATION)).thenReturn(Optional.of(consumedDestinationModelMock));
		when(defaultSapS4OMOutboundService.getHttpHeaderFromSession(DESTINATION_ID_SIMULATION)).thenReturn(null);
		try {
			when(defaultSapS4OMOutboundService.getRestTemplateFactory().getRestTemplate(consumedDestinationModelMock)).thenReturn(restTemplates);
		} catch (CredentialException e) {
			assertNotNull(e);
		}
		lenient().when(restTemplates.exchange(any(String.class),any(HttpMethod.class), any(HttpEntity.class), (Class<String>) any())).thenReturn(csrfTokenExchange);
		when(defaultSapS4OMOutboundService.getS4omRestTemplate().exchange(any(String.class),any(HttpMethod.class), any(HttpEntity.class), (Class<SAPS4OMData>) any())).thenReturn(responseEntityData);
		
		when(defaultSapS4OMOutboundService.sendRequest(consumedDestinationModelMock, URL, HttpMethod.POST, sapS4OMRequestData, SAPS4OMData.class)).thenReturn(responseEntityData);

		SAPS4OMData result = defaultSapS4OMOutboundService.simulateOrder(DESTINATION_ID_SIMULATION, DESTINATION_TARGET_ID_SIMULATION , sapS4OMRequestData);
		
		assertEquals("3020",result.getResult().getSalesOrganization());
	}
	
	@Test
	public void testSimulateOrderWithException() { 
		SAPS4OMRequestData sapS4OMRequestData = mock(SAPS4OMRequestData.class);
		ConsumedDestinationModel consumedDestinationModelMock = mock(ConsumedDestinationModel.class);
		BasicCredentialModel credential = mock(BasicCredentialModel.class);
		lenient().when(consumedDestinationModelMock.getCredential()).thenReturn(credential);
		lenient().when(credential.getId()).thenReturn(TEST_CREDENTIAL_ID);
		lenient().when(credential.getItemtype()).thenReturn(BASIC_CREDENTIAL_ITEMTYPE);

		HttpHeaders header = new HttpHeaders();
	
		header.add(DESTINATION_ID_SIMULATION, DESTINATION_ID);
		when(consumedDestinationModelMock.getId()).thenReturn(DESTINATION_ID);
		when(consumedDestinationModelMock.getUrl()).thenReturn(URL);

		when(defaultSapS4OMOutboundService.getHttpHeaderFromSession(LANGUAGE_ISO_CODE + DESTINATION_ID)).thenReturn(header);


		when(baseStoreService.getCurrentBaseStore()).thenReturn(mockBaseStoreModel);
		when(defaultSapS4OMOutboundService.getSapMappedDestinationService().getDestinationByKeyForBaseStore(mockBaseStoreModel, SAPDestinationKey.S4OMORDERSIMULATEDESTINATION)).thenReturn(Optional.of(consumedDestinationModelMock));
		when(defaultSapS4OMOutboundService.getS4omRestTemplate().exchange(any(String.class),any(HttpMethod.class), any(HttpEntity.class), (Class<SAPS4OMData>) any())).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST,"Failed to connect to the destination"));
		try {
			defaultSapS4OMOutboundService.simulateOrder(DESTINATION_ID_SIMULATION,DESTINATION_TARGET_ID_SIMULATION, sapS4OMRequestData);			
		} catch (final OutboundServiceException e) {
			assertNotNull(e.getStackTrace());
		}
		
	}
	
	
	@Test
	public void testSimulateOrderWithUnavalableResourceException() { 
		SAPS4OMRequestData sapS4OMRequestData = mock(SAPS4OMRequestData.class);
		ConsumedDestinationModel consumedDestinationModelMock = mock(ConsumedDestinationModel.class);
		BasicCredentialModel credential = mock(BasicCredentialModel.class);
		lenient().when(consumedDestinationModelMock.getCredential()).thenReturn(credential);
		lenient().when(credential.getId()).thenReturn(TEST_CREDENTIAL_ID);
		lenient().when(credential.getItemtype()).thenReturn(BASIC_CREDENTIAL_ITEMTYPE);

		HttpHeaders header = new HttpHeaders();
		header.add(DESTINATION_ID_SIMULATION, DESTINATION_ID);
		
		when(consumedDestinationModelMock.getId()).thenReturn(DESTINATION_ID_SIMULATION);
		when(consumedDestinationModelMock.getUrl()).thenReturn(URL);
		when(defaultSapS4OMOutboundService.getHttpHeaderFromSession(LANGUAGE_ISO_CODE + DESTINATION_ID_SIMULATION)).thenReturn(header);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(mockBaseStoreModel);
		when(defaultSapS4OMOutboundService.getSapMappedDestinationService().getDestinationByKeyForBaseStore(mockBaseStoreModel, SAPDestinationKey.S4OMORDERSIMULATEDESTINATION)).thenReturn(Optional.of(consumedDestinationModelMock));
		when(defaultSapS4OMOutboundService.getS4omRestTemplate().exchange(any(String.class),any(HttpMethod.class), any(HttpEntity.class), (Class<SAPS4OMData>) any())).thenThrow(new ResourceAccessException(Localization.getLocalizedString(ERROR_MESSAGE_LOCALE)));
		try {
		defaultSapS4OMOutboundService.simulateOrder(DESTINATION_ID_SIMULATION,DESTINATION_TARGET_ID_SIMULATION, sapS4OMRequestData);
		} catch (final OutboundServiceException e) {
			assertNotNull(e.getMessage());
		}
	}
	
	@Test
	public void testGetAuthenticationDetailsWithException() { 
		ConsumedDestinationModel consumedDestinationModelMock = mock(ConsumedDestinationModel.class);
		BasicCredentialModel credential = mock(BasicCredentialModel.class);
		lenient().when(consumedDestinationModelMock.getCredential()).thenReturn(credential);
		lenient().when(credential.getId()).thenReturn(TEST_CREDENTIAL_ID);
		lenient().when(credential.getItemtype()).thenReturn(BASIC_CREDENTIAL_ITEMTYPE);

		when(consumedDestinationModelMock.getUrl()).thenReturn(CSRF_URL);
		try {
			when(defaultSapS4OMOutboundService.getRestTemplateFactory().getRestTemplate(consumedDestinationModelMock)).thenReturn(restTemplates);
		} catch (CredentialException e) {
			assertNotNull(e);
		}				
		lenient().when(restTemplates.exchange(any(String.class),any(HttpMethod.class), any(HttpEntity.class), (Class<String>) any())).thenThrow(new ResourceAccessException(Localization.getLocalizedString(ERROR_MESSAGE_LOCALE)));
		try {
		defaultSapS4OMOutboundService.getAuthenticationDetails(consumedDestinationModelMock);
		} catch (final OutboundServiceException e) {
			assertNotNull(e.getMessage());		}
	}
	
	@Test
	public void testGetAuthenticationDetailsWithUnauthorizedException() { 
		ConsumedDestinationModel consumedDestinationModelMock = mock(ConsumedDestinationModel.class);
		BasicCredentialModel credential = mock(BasicCredentialModel.class);
		lenient().when(consumedDestinationModelMock.getCredential()).thenReturn(credential);
		lenient().when(credential.getId()).thenReturn(TEST_CREDENTIAL_ID);
		lenient().when(credential.getItemtype()).thenReturn(BASIC_CREDENTIAL_ITEMTYPE);

		when(consumedDestinationModelMock.getUrl()).thenReturn(CSRF_URL);
		try {
			when(defaultSapS4OMOutboundService.getRestTemplateFactory().getRestTemplate(consumedDestinationModelMock)).thenReturn(restTemplates);
		} catch (CredentialException e) {
			assertNotNull(e);
		}			
		lenient().when(restTemplates.exchange(any(String.class),any(HttpMethod.class), any(HttpEntity.class), (Class<String>) any())).thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED,Localization.getLocalizedString("message.error.invalidcredentials")));
		try {
		defaultSapS4OMOutboundService.getAuthenticationDetails(consumedDestinationModelMock);
		} catch (final OutboundServiceException e) {
			assertNotNull(e.getMessage());
		}
	}
	
	
	@Test
	public void testFetchOrderDetailsSuccessWithTokenInSession() { 
		SAPS4OMRequestData sapS4OMRequestData = mock(SAPS4OMRequestData.class);
		ConsumedDestinationModel consumedDestinationModelMock = mock(ConsumedDestinationModel.class);
		BasicCredentialModel credential = mock(BasicCredentialModel.class);
		lenient().when(consumedDestinationModelMock.getCredential()).thenReturn(credential);
		lenient().when(credential.getId()).thenReturn(TEST_CREDENTIAL_ID);
		lenient().when(credential.getItemtype()).thenReturn(BASIC_CREDENTIAL_ITEMTYPE);
		
		HttpHeaders headerFromSession = new HttpHeaders();
		headerFromSession.add(DESTINATION_ID_ORDER, DESTINATION_ID);
		
		SAPS4OMData responseData = getResponseData();

		ResponseEntity<SAPS4OMData> responseEntityData = new ResponseEntity<>(responseData, headerFromSession, HttpStatus.ACCEPTED);
		
		Map<String, List<FilterData>> orderDetailFilter = getOrderDetailsFilter();
		
		when(consumedDestinationModelMock.getId()).thenReturn(DESTINATION_ID_ORDER);
		when(consumedDestinationModelMock.getUrl()).thenReturn(ORDER_SRV_URL);
		when(defaultSapS4OMOutboundService.getHttpHeaderFromSession(LANGUAGE_ISO_CODE + DESTINATION_ID_ORDER)).thenReturn(headerFromSession);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(mockBaseStoreModel);
		when(defaultSapS4OMOutboundService.getSapMappedDestinationService().getDestinationByKeyForBaseStore(mockBaseStoreModel, SAPDestinationKey.S4OMORDERDESTINATION)).thenReturn(Optional.of(consumedDestinationModelMock));
		when(defaultSapS4OMOutboundService.getS4omRestTemplate().exchange(any(String.class),any(HttpMethod.class), any(HttpEntity.class), (Class<SAPS4OMData>) any())).thenReturn(responseEntityData);
		when(defaultSapS4OMOutboundService.sendRequest(consumedDestinationModelMock, ORDER_SRV_URL_WITH_PATH_VAR, HttpMethod.GET, sapS4OMRequestData, SAPS4OMData.class)).thenReturn(responseEntityData);
		
		SAPS4OMData result = defaultSapS4OMOutboundService.fetchOrderDetails(DESTINATION_ID_ORDER, DESTINATION_TARGET_ID_ORDER, ORDER_ID, orderDetailFilter);
		
		assertEquals("00",result.getResult().getDivision());
		assertEquals("30",result.getResult().getDistributionChannel());
	}
	

	@Test
	public void testFetchOrderDetailsWithException() { 
		ConsumedDestinationModel consumedDestinationModelMock = mock(ConsumedDestinationModel.class);
		BasicCredentialModel credential = mock(BasicCredentialModel.class);
		lenient().when(consumedDestinationModelMock.getCredential()).thenReturn(credential);
		lenient().when(credential.getId()).thenReturn(TEST_CREDENTIAL_ID);
		lenient().when(credential.getItemtype()).thenReturn(BASIC_CREDENTIAL_ITEMTYPE);

		HttpHeaders header = new HttpHeaders();
	
		header.add(DESTINATION_ID_ORDER, DESTINATION_ID);
		when(consumedDestinationModelMock.getId()).thenReturn(DESTINATION_ID);
		when(consumedDestinationModelMock.getUrl()).thenReturn(ORDER_SRV_URL);

		when(defaultSapS4OMOutboundService.getHttpHeaderFromSession(LANGUAGE_ISO_CODE + DESTINATION_ID)).thenReturn(header);
		
		Map<String, List<FilterData>> orderDetailFilter = new HashMap<>();

		when(defaultSapS4OMOutboundService.getDestinationService().getDestinationByIdAndByDestinationTargetId(DESTINATION_ID_ORDER, DESTINATION_TARGET_ID_ORDER)).thenReturn(consumedDestinationModelMock);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(mockBaseStoreModel);
		when(defaultSapS4OMOutboundService.getSapMappedDestinationService().getDestinationByKeyForBaseStore(mockBaseStoreModel, SAPDestinationKey.S4OMORDERDESTINATION)).thenReturn(Optional.empty());
		when(defaultSapS4OMOutboundService.getS4omRestTemplate().exchange(any(String.class),any(HttpMethod.class), any(HttpEntity.class), (Class<SAPS4OMData>) any())).thenThrow(new RestClientException("Failed to connect to the destination"));
		try {
			defaultSapS4OMOutboundService.fetchOrderDetails(DESTINATION_ID_ORDER, DESTINATION_TARGET_ID_ORDER, ORDER_ID, orderDetailFilter);			
		} catch (final OutboundServiceException e) {
			assertNotNull(e.getStackTrace());
		}
		
	}
	
	@Test
	public void testFetchOrderHistorySuccessWithTokenInSession() { 
		SAPS4OMRequestData sapS4OMRequestData = mock(SAPS4OMRequestData.class);
		ConsumedDestinationModel consumedDestinationModelMock = mock(ConsumedDestinationModel.class);
		BasicCredentialModel credential = mock(BasicCredentialModel.class);
		lenient().when(consumedDestinationModelMock.getCredential()).thenReturn(credential);
		lenient().when(credential.getId()).thenReturn(TEST_CREDENTIAL_ID);
		lenient().when(credential.getItemtype()).thenReturn(BASIC_CREDENTIAL_ITEMTYPE);
		
		HttpHeaders headerFromSession = new HttpHeaders();
		headerFromSession.add(DESTINATION_ID_ORDER, DESTINATION_ID);
		
		SAPS4OMOrders responseData = getOrderHistoryResponseData();

		ResponseEntity<SAPS4OMOrders> responseEntityData = new ResponseEntity<>(responseData, headerFromSession, HttpStatus.ACCEPTED);
		
		Map<String, List<FilterData>> orderHistoryFilter = new HashMap<>();
		
		when(consumedDestinationModelMock.getId()).thenReturn(DESTINATION_ID_ORDER);
		when(consumedDestinationModelMock.getUrl()).thenReturn(URL);
		when(defaultSapS4OMOutboundService.getHttpHeaderFromSession(LANGUAGE_ISO_CODE + DESTINATION_ID_ORDER)).thenReturn(headerFromSession);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(mockBaseStoreModel);
		when(defaultSapS4OMOutboundService.getSapMappedDestinationService().getDestinationByKeyForBaseStore(mockBaseStoreModel, SAPDestinationKey.S4OMORDERDESTINATION)).thenReturn(Optional.of(consumedDestinationModelMock));
		when(defaultSapS4OMOutboundService.getS4omRestTemplate().exchange(any(String.class),any(HttpMethod.class), any(HttpEntity.class), (Class<SAPS4OMOrders>) any())).thenReturn(responseEntityData);
		when(defaultSapS4OMOutboundService.sendRequest(consumedDestinationModelMock, URL, HttpMethod.GET, sapS4OMRequestData, SAPS4OMOrders.class)).thenReturn(responseEntityData);
		
		SAPS4OMOrders result = defaultSapS4OMOutboundService.fetchOrders(DESTINATION_ID_ORDER, DESTINATION_TARGET_ID_ORDER, orderHistoryFilter);
		assertEquals("01",result.getSapS4OMOrderResult().getOrderCount());
	}
	
	private SAPS4OMData getResponseData() {
		SAPS4OMData sapS4OMData = new SAPS4OMData();
	
		SAPS4OMResponseData sapS4OMResponseData = new SAPS4OMResponseData();
		sapS4OMResponseData.setSalesOrderType("OR");
		sapS4OMResponseData.setSalesOrganization("3020");
		sapS4OMResponseData.setDistributionChannel("30");
		sapS4OMResponseData.setDivision("00");

		SAPS4OMItemsData sapS4OMItemsData = new SAPS4OMItemsData();
		SAPS4OMItemData sapS4OMItemData = new SAPS4OMItemData();
		sapS4OMItemData.setMaterial("SAP-8302");

		List<SAPS4OMItemData> sapS4OMItemDataList = new ArrayList<>();
		sapS4OMItemDataList.add(sapS4OMItemData);
		sapS4OMItemsData.setSalesOrderItems(sapS4OMItemDataList);
		sapS4OMResponseData.setItems(sapS4OMItemsData);
		sapS4OMData.setResult(sapS4OMResponseData);
		return sapS4OMData;
	}
	
	private Map<String, List<FilterData>> getOrderDetailsFilter() {
		Map<String,List<FilterData>> expandFilters = new HashMap<>();
		List<FilterData> orderDetailsExpandFilter = new ArrayList<>();
		FilterData expandOrderItemPrice = new FilterData.FilterDataBuilder(EXPAND_PARAM_ITEM + "/" + EXPAND_PARAM_PRICING_ELEMENT)
				.filterDataOperator(",")
				.build();
		orderDetailsExpandFilter.add(expandOrderItemPrice);
		FilterData expandOrderPrice = new FilterData.FilterDataBuilder(EXPAND_PARAM_PRICING_ELEMENT)
				.filterDataOperator(",")
				.build();
		orderDetailsExpandFilter.add(expandOrderPrice);
		FilterData expandOrderPartner = new FilterData.FilterDataBuilder(EXPAND_PARAM_PARTNER)
				.build();
		orderDetailsExpandFilter.add(expandOrderPartner);
		
		expandFilters.put(EXPAND_SUFFIX, orderDetailsExpandFilter);
		
		return expandFilters;
	}
	
	private SAPS4OMOrders getOrderHistoryResponseData() {
		SAPS4OMOrders orders = new SAPS4OMOrders();
		SAPS4OMOrderResult sapS4OMOrderResult = new SAPS4OMOrderResult();
		List<SAPS4OMResponseData> sapS4OMResponseDataList = new ArrayList<>();
		
		SAPS4OMResponseData sapS4OMResponseData = new SAPS4OMResponseData();
		sapS4OMResponseData.setSalesOrderType("OR");
		sapS4OMResponseData.setSalesOrganization("3020");
		sapS4OMResponseData.setDistributionChannel("30");
		sapS4OMResponseData.setDivision("00");
		
		sapS4OMResponseDataList.add(sapS4OMResponseData);
		sapS4OMOrderResult.setResults(sapS4OMResponseDataList);
		sapS4OMOrderResult.setOrderCount("01");
		orders.setSapS4OMOrderResult(sapS4OMOrderResult);
		return orders;
	}

}
