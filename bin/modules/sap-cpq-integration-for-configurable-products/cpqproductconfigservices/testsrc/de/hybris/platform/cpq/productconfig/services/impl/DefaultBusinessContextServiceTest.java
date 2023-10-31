/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.cpq.productconfig.services.EngineDeterminationService;
import de.hybris.platform.cpq.productconfig.services.client.CpqClient;
import de.hybris.platform.cpq.productconfig.services.client.CpqClientConstants;
import de.hybris.platform.cpq.productconfig.services.client.CpqClientUtil;
import de.hybris.platform.cpq.productconfig.services.data.BusinessContext;
import de.hybris.platform.cpq.productconfig.services.data.BusinessContextRequest;
import de.hybris.platform.cpq.productconfig.services.data.InvolvedParty;
import de.hybris.platform.cpq.productconfig.services.data.SalesArea;
import de.hybris.platform.cpq.productconfig.services.strategies.CPQInteractionStrategy;
import de.hybris.platform.sap.sapmodel.services.SalesAreaService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.charon.RawResponse;
import com.hybris.charon.exp.HttpException;

import rx.Observable;


/**
 * Unit test for {@link DefaultBusinessContextService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultBusinessContextServiceTest
{
	private static final String LANGUAGE_ISO_CODE = "language iso code";
	private static final String OWNER_ID = "owner id";
	private static final String CURRENCY_ISOCODE = "currency isocode";
	private static final String DIVISION = "division";
	private static final String DIST_CHANNEL = "dist channel";
	private static final String SALES_ORG = "sales org";
	private static final String BP_NUMERIC = "1234567890";
	private static final String BP_ALPHANUMERIC = "customer_1234";
	private static final String ACCESS_TOKEN = "625343";
	private static final String AUTHORIZATION_STRING = "Bearer " + ACCESS_TOKEN;

	@InjectMocks
	private DefaultBusinessContextService classUnderTest;

	@InjectMocks
	private ObservableTestHelper helper;

	@Mock
	private CPQInteractionStrategy cpqInteractionStrategy;
	@Mock
	private SalesAreaService commonSalesAreaService;
	@Mock
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;
	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	@Mock
	private CommonI18NService i18NService;
	@Mock
	private EngineDeterminationService engineDeterminationService;
	@Mock
	private CurrencyModel currentCurrencyModel;
	@Mock
	private LanguageModel currentLanguageModel;
	@Mock
	private CpqClient client;
	@Mock
	private CpqClientUtil cpqClientUtil;

	private B2BUnitModel b2bUnit;
	private B2BCustomerModel b2bCustomer;
	private B2BUnitModel rootUnit;


	@Before
	public void setup()
	{
		when(commonSalesAreaService.getSalesOrganization()).thenReturn(SALES_ORG);
		when(commonSalesAreaService.getDistributionChannel()).thenReturn(DIST_CHANNEL);
		when(commonSalesAreaService.getDivision()).thenReturn(DIVISION);
		when(i18NService.getCurrentCurrency()).thenReturn(currentCurrencyModel);
		when(currentCurrencyModel.getIsocode()).thenReturn(CURRENCY_ISOCODE);
		when(i18NService.getCurrentLanguage()).thenReturn(currentLanguageModel);
		when(currentLanguageModel.getIsocode()).thenReturn(LANGUAGE_ISO_CODE);
		when(engineDeterminationService.isMockEngineActive()).thenReturn(false);

		b2bCustomer = new B2BCustomerModel();
		b2bUnit = new B2BUnitModel();
		when(b2bCustomerService.getCurrentB2BCustomer()).thenReturn(b2bCustomer);
		when(b2bUnitService.getParent(b2bCustomer)).thenReturn(b2bUnit);
		rootUnit = new B2BUnitModel();
		rootUnit.setUid(BP_NUMERIC);
		when(b2bUnitService.getRootUnit(b2bUnit)).thenReturn(rootUnit);

		when(cpqInteractionStrategy.getClient()).thenReturn(client);
		when(cpqInteractionStrategy.getAuthorizationString()).thenReturn(AUTHORIZATION_STRING);
		final Observable<RawResponse<Object>> responseObs = helper.mockEmptyResponse(CpqClientConstants.HTTP_STATUS_CREATED);
		when(client.createOrUpdateConfigurationContext(any(), eq(AUTHORIZATION_STRING))).thenReturn(responseObs);
	}

	@Test
	public void testGetBusinessContext()
	{
		final BusinessContext result = classUnderTest.getBusinessContext();
		assertNotNull(result);
		assertNotNull(result.getSalesArea());
		assertNotNull(result.getCurrencyCode());
		assertNotNull(result.getInvolvedParties());
		assertNotNull(result.getLanguage());
	}

	@Test
	public void testGetSalesArea()
	{
		final SalesArea result = classUnderTest.getSalesArea();
		assertNotNull(result);
		assertEquals(SALES_ORG, result.getSalesOrganization());
		assertEquals(DIST_CHANNEL, result.getDistributionChannel());
		assertEquals(DIVISION, result.getDivision());
	}

	@Test
	public void testGetCurrencyCode()
	{
		final String result = classUnderTest.getCurrencyCode();
		assertEquals(CURRENCY_ISOCODE, result);
	}

	@Test
	public void testGetInvolvedParties()
	{
		final List<InvolvedParty> result = classUnderTest.getInvolvedParties();
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(DefaultBusinessContextService.BILL_TO, result.get(0).getKey());
		assertNotNull(result.get(0).getExternalId());
	}

	@Test
	public void testGetBillTo()
	{
		final InvolvedParty result = classUnderTest.getBillTo();
		assertNotNull(result);
		assertEquals(DefaultBusinessContextService.BILL_TO, result.getKey());
		assertEquals(BP_NUMERIC, result.getExternalId());
	}

	@Test
	public void testGetBusinessPartnerIdForNumeric()
	{
		final String result = classUnderTest.getBusinessPartnerId();
		assertEquals(BP_NUMERIC, result);
	}

	@Test
	public void testGetBusinessPartnerIdForAlphanumeric()
	{
		rootUnit.setUid(BP_ALPHANUMERIC);
		when(b2bUnitService.getRootUnit(b2bUnit)).thenReturn(rootUnit);

		final String result = classUnderTest.getBusinessPartnerId();
		assertEquals(BP_ALPHANUMERIC.toUpperCase(), result);
		assertNotEquals(BP_ALPHANUMERIC, result);
	}

	@Test
	public void testGetOwnerId()
	{
		final String result = classUnderTest.getOwnerId();
		assertNotNull(result);
		assertEquals(BP_NUMERIC, result);
	}

	@Test(expected = IllegalStateException.class)
	public void testSendBusinessContextToCPQOwnerIdNull()
	{
		classUnderTest.sendBusinessContextToCPQ(null, new BusinessContext());
	}

	@Test
	public void testSendBusinessContextToCPQ()
	{
		final ArgumentCaptor<BusinessContextRequest> contextCaptor = ArgumentCaptor.forClass(BusinessContextRequest.class);

		classUnderTest.sendBusinessContextToCPQ(OWNER_ID, new BusinessContext());
		verify(client).createOrUpdateConfigurationContext(contextCaptor.capture(), eq(AUTHORIZATION_STRING));
		assertEquals(OWNER_ID, contextCaptor.getValue().getOwnerId());
	}

	@Test
	public void testSendBusinessContextToCPQMock()
	{
		when(engineDeterminationService.isMockEngineActive()).thenReturn(true);
		classUnderTest.sendBusinessContextToCPQ(OWNER_ID, new BusinessContext());
		verify(client, times(0)).createOrUpdateConfigurationContext(any(BusinessContextRequest.class), eq(AUTHORIZATION_STRING));
	}

	@Test
	public void testSendBusinessContextToCPQDefault()
	{
		final ArgumentCaptor<BusinessContextRequest> contextCaptor = ArgumentCaptor.forClass(BusinessContextRequest.class);
		classUnderTest.sendBusinessContextToCPQ();
		verify(client).createOrUpdateConfigurationContext(contextCaptor.capture(), eq(AUTHORIZATION_STRING));
		assertEquals(BP_NUMERIC, contextCaptor.getValue().getOwnerId());
	}

	@Test(expected = IllegalStateException.class)
	public void testSendBusinessContextToCPQHttpError()
	{
		when(client.createOrUpdateConfigurationContext(any(), eq(AUTHORIZATION_STRING)))
				.thenThrow(new HttpException(500, "Error"));
		classUnderTest.sendBusinessContextToCPQ(OWNER_ID, new BusinessContext());
	}

	@Test(expected = IllegalStateException.class)
	public void testSendBusinessContextToCPQWrongHttpStatusCode()
	{
		doThrow(new IllegalStateException("TEST")).when(cpqClientUtil).checkHTTPStatusCode(anyString(),
				eq(CpqClientConstants.HTTP_STATUS_CREATED), any());
		classUnderTest.sendBusinessContextToCPQ(OWNER_ID, new BusinessContext());
	}

	@Test
	public void testGetLanguageCode()
	{
		final String result = classUnderTest.getLanguageCode();
		assertNotNull(result);
		assertEquals(LANGUAGE_ISO_CODE, result);
	}
}
