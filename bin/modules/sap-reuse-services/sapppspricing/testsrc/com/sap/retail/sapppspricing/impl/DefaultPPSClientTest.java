/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.ARTSCommonHeaderType;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.ARTSCommonHeaderType.ActionCodeEnum;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.ARTSCommonHeaderType.MessageTypeEnum;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.BusinessUnitCommonData;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.BusinessUnitCommonData.TypeCodeEnum;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.DateTimeCommonData;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.HeaderDateTime;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.IDCommonData;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.ItemID;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.LineItemDomainSpecific;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.LoyaltyAccountCommonData;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.LoyaltyAccountType;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.LoyaltyProgramIDType;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.MessageID;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculate;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculateBase;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculateBase.TransactionTypeEnum;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculateResponse;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.QuantityCommonData;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.SaleBase;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.ShoppingBasketBase;
import com.sap.retail.sapppspricing.PPSConfigService;
import com.sap.retail.sapppspricing.SapPPSPricingRuntimeException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

@UnitTest
public class DefaultPPSClientTest {
	private static final String MY_STORE = "myStore";
	private DefaultPPSClient cut;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private HttpMessageConverter<?> jsonConverter;
	@Mock
	private RestTemplate restTemplateMock;
	@Mock
	private PPSConfigService configService;
	@Mock
	private DefaultPPSClient client;
	private BaseStoreModel baseStoreModel;
	private SAPHTTPDestinationModel httpDestination;
	private SAPConfigurationModel sapConfig;
	@Mock
	private DestinationService destinationService;
	@Mock
	ConsumedDestinationModel consumedDestinationModel;
	@Mock
	ConsumedOAuthCredentialModel consumedOAuthCredentialModel;
	protected static final String DESTINATIONTARGET="scpOPPSDestinationTarget";
	protected static final String DESTINATIONID = "scpOPPServiceDestination";

	@Before
	public void setUp() {
		httpDestination = new SAPHTTPDestinationModel();
		MockitoAnnotations.initMocks(this);
		baseStoreModel = new BaseStoreModel();
		sapConfig = new SAPConfigurationModel();
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		sapConfig.setSappps_active(Boolean.TRUE);
		cut = new DefaultPPSClient();
		cut.setDestinationService(destinationService);
		httpDestination.setTargetURL("myUri");
		cut.setBaseStoreService(baseStoreService);
		baseStoreModel.setUid(MY_STORE);
	}

	@Test
	public void testSetGetBaseStoreService() {
		cut = new DefaultPPSClient();
		assertNull(cut.getBaseStoreService());
		cut.setBaseStoreService(baseStoreService);
		assertSame(baseStoreService, cut.getBaseStoreService());
	}

	@Test
	public void testCallPPSRemote() {
		final PriceCalculate priceCalculate = new PriceCalculate();
		final PriceCalculateResponse body = new PriceCalculateResponse();
		final MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, "returnedToken");
		consumedDestinationModel.setUrl("myUri");
		Mockito.when(destinationService.getDestinationByIdAndByDestinationTargetId(DESTINATIONID, DESTINATIONTARGET))
				.thenReturn(consumedDestinationModel);
		final ResponseEntity<PriceCalculateResponse> expectedResponse = new ResponseEntity<PriceCalculateResponse>(body,
				headers, HttpStatus.ACCEPTED);
		Mockito.when(restTemplateMock.exchange(Mockito.eq("myUri"), Mockito.eq(HttpMethod.POST),
				Mockito.any(HttpEntity.class), Mockito.eq(PriceCalculateResponse.class))).thenReturn(expectedResponse);
		client.callPPS(priceCalculate, sapConfig);
		final PriceCalculateResponse result = expectedResponse.getBody();
		assertSame(body, result);
	}

	@Test(expected = ModelNotFoundException.class)
	public void testCallPPSRemoteNoSuccess() {
		final PriceCalculate priceCalculate = new PriceCalculate();
		final PriceCalculateResponse body = new PriceCalculateResponse();
		final MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, "returnedToken");
		final ResponseEntity<PriceCalculateResponse> expectedResponse = new ResponseEntity<PriceCalculateResponse>(body,
				HttpStatus.FORBIDDEN);
		Mockito.when(restTemplateMock.exchange(Mockito.eq("myUri"), Mockito.eq(HttpMethod.POST),
				Mockito.any(HttpEntity.class), Mockito.eq(PriceCalculateResponse.class))).thenReturn(expectedResponse);
		cut.callPPS(priceCalculate, sapConfig);
		fail("Exception expected");
	}

	@Test
	public void testCallPPSRemoteException() throws Exception {
		final PriceCalculate priceCalculate = new PriceCalculate();
		final RuntimeException exception = new RestClientException("mist!");
		Mockito.when(restTemplateMock.exchange(Mockito.eq("myUri"), Mockito.eq(HttpMethod.POST),
				Mockito.any(HttpEntity.class), Mockito.eq(PriceCalculateResponse.class))).thenThrow(exception);
		try {
			cut.callPPS(priceCalculate, sapConfig);
		} catch (final Exception e) {
			assertTrue(e != null);
		}
	}
}
