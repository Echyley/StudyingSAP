/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services.impl;

import static de.hybris.platform.b2b.punchout.services.impl.DefaultPunchOutSessionService.PUNCHOUT_SESSION_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.punchout.Organization;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.PunchOutSessionExpired;
import de.hybris.platform.b2b.punchout.PunchOutSessionNotFoundException;
import de.hybris.platform.b2b.punchout.model.StoredPunchOutSessionModel;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.time.DateUtils;
import org.cxml.CXML;
import org.cxml.Credential;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test cases for {@link DefaultPunchOutSessionService}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutSessionServiceTest
{
	private static final int TEN_HOURS = 10 * 60 * 60 * 1000;
	private static final String PUNCHOUT_CUSTOMER = "punchout.customer2@punchoutorg.com";

	@Mock
	private SessionService sessionService;

	@Mock
	private ConfigurationService configurationService;

	@InjectMocks
	private DefaultPunchOutSessionService punchoutSessionService;

	@Mock
	private Session session;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private StoredPunchOutSessionModel storedSession;

	@Mock
	private SearchResult searchResult;

	@Mock
	private PunchOutSession punchoutSession;

	@Mock
	private Configuration configuration;

	@Mock
	private Populator<CXML, PunchOutSession> punchOutSessionPopulator;

	@Mock
	private CartService cartService;

	@Mock
	private ModelService modelService;

	@Mock
	private CXML request;

	@Mock
	private PunchOutCredentialService punchOutCredentialService;

	@Mock
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;

	@Spy
	private CartModel cart;

	@Mock
	private B2BCustomerModel b2BCustomer;
	@Test
	public void testInitAndActivatePunchOutSession()
	{
		punchoutSessionService.initAndActivatePunchOutSession(request);
		verify(punchOutSessionPopulator, times(1)).populate(eq(request), any(PunchOutSession.class));
		verify(sessionService).setAttribute(eq(PUNCHOUT_SESSION_KEY), any(PunchOutSession.class));
	}

	@Test
	public void testActivate()
	{
		punchoutSessionService.activate(punchoutSession);
		verify(sessionService).setAttribute(PUNCHOUT_SESSION_KEY, punchoutSession);
	}

	@Test
	public void testGeneratePunchoutSessionId()
	{
		final String sessionId = punchoutSessionService.generatePunchoutSessionId();
		assertNotNull(sessionId);
	}

	@Test
	public void testSaveCurrentPunchoutSession()
	{
		final Organization organization = new Organization();
		final List<Organization> organizationList =  new ArrayList<>();
		organizationList.add(organization);
		b2BCustomer.setCustomerID(PUNCHOUT_CUSTOMER);
		when(sessionService.getCurrentSession()).thenReturn(session);
		when(session.getAttribute(PUNCHOUT_SESSION_KEY)).thenReturn(punchoutSession);
		when(punchoutSession.getInitiatedBy()).thenReturn(organizationList);
		when(punchOutCredentialService.getCustomerForCredentialNoAuth(any(Credential.class))).thenReturn(b2BCustomer);
		when(b2bCustomerService.getUserForUID(any())).thenReturn(b2BCustomer);
		when(cartService.getSessionCart()).thenReturn(cart);
		StoredPunchOutSessionModel punchoutSessionModel = mock(StoredPunchOutSessionModel.class);
		when(modelService.create(StoredPunchOutSessionModel.class)).thenReturn(punchoutSessionModel);

		punchoutSessionService.saveCurrentPunchoutSession();
		verify(punchoutSessionModel, times(1)).setSid(anyString());
		verify(punchoutSessionModel, times(1)).setCart(any(CartModel.class));
		verify(punchoutSessionModel, times(1)).setPunchOutSession(any());
		verify(cartService, times(1)).getSessionCart();
		verify(modelService, times(1)).save(punchoutSessionModel);
		assertEquals(b2BCustomer, cartService.getSessionCart().getUser());
	}

	@Test
	public void testSetCurrentCartFromPunchOutSetup()
	{
		when(searchResult.getCount()).thenReturn(1);
		when(searchResult.getResult()).thenReturn(Arrays.asList(storedSession));
		when(flexibleSearchService.search(anyString(), anyMap())).thenReturn(searchResult);
		CartModel cartModel = new CartModel();
		when(storedSession.getCart()).thenReturn(cartModel);
		punchoutSessionService.setCurrentCartFromPunchOutSetup("");
	}

	@Test(expected = PunchOutSessionNotFoundException.class)
	public void testSetCurrentCartFromPunchOutSetupWithResultIsNull()
	{
		when(flexibleSearchService.search(anyString(), anyMap())).thenReturn(null);
		punchoutSessionService.setCurrentCartFromPunchOutSetup("");
	}

	@Test(expected = PunchOutSessionNotFoundException.class)
	public void testSetCurrentCartFromPunchOutSetupResultIsEmpty()
	{
		when(searchResult.getCount()).thenReturn(0);
		when(flexibleSearchService.search(anyString(), anyMap())).thenReturn(searchResult);
		punchoutSessionService.setCurrentCartFromPunchOutSetup("");
	}

	@Test(expected = PunchOutSessionNotFoundException.class)
	public void testSetCurrentCartFromPunchOutSetupHandleNPE()
	{
		when(searchResult.getCount()).thenReturn(1);
		when(searchResult.getResult()).thenReturn(Arrays.asList(storedSession));
		when(flexibleSearchService.search(anyString(), anyMap())).thenReturn(searchResult);
		when(storedSession.getCart()).thenReturn(null);
		punchoutSessionService.setCurrentCartFromPunchOutSetup("");
	}
	/**
	 * Tests that the session will be expired when the timeout kicks in, having in mind the session has lived for longer
	 * than the timeout period.
	 *
	 * @throws Exception
	 *            on error
	 */
	@Test(expected = PunchOutSessionExpired.class)
	public void testLoadExpiredSession()
	{
		final Date referenceDate = new Date();
		final Date sessionDate = DateUtils.addMinutes(referenceDate, -2);

		when(searchResult.getCount()).thenReturn(1);
		when(searchResult.getResult()).thenReturn(Arrays.asList(storedSession));
		when(flexibleSearchService.search(anyString(), anyMap())).thenReturn(searchResult);
		when(storedSession.getPunchOutSession()).thenReturn(punchoutSession);
		when(punchoutSession.getTime()).thenReturn(sessionDate);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getInteger(isA(String.class), isA(Integer.class))).thenReturn(Integer.valueOf(1));

		punchoutSessionService.loadPunchOutSession("testId1");
	}

	/**
	 * Tests that when the session is created and the timeout is later than the current time the session is still not
	 * expired.
	 */
	@Test
	public void testLoadNonExpiredSession()
	{
		final Date sessionDate = new Date();

		when(sessionService.getCurrentSession()).thenReturn(session);
		when(searchResult.getCount()).thenReturn(1);
		when(searchResult.getResult()).thenReturn(Arrays.asList(storedSession));
		when(flexibleSearchService.search(anyString(), anyMap())).thenReturn(searchResult);
		when(storedSession.getPunchOutSession()).thenReturn(punchoutSession);
		when(punchoutSession.getTime()).thenReturn(sessionDate);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getInteger(isA(String.class), isA(Integer.class))).thenReturn(Integer.valueOf(TEN_HOURS));

		assertEquals("The punchout session is expected to be the one we set up in expectations", punchoutSession,
				punchoutSessionService.loadPunchOutSession("testId1"));
		verify(session).setAttribute(PUNCHOUT_SESSION_KEY, punchoutSession);
	}

	@Test(expected = PunchOutSessionNotFoundException.class)
	public void testLoadSessionNoStoredSession()
	{
		punchoutSessionService.loadPunchOutSession("testId1");
	}

	@Test(expected = PunchOutSessionNotFoundException.class)
	public void testLoadSessionNoStoredSession2()
	{
		final List resultList = mock(ArrayList.class);
		when(flexibleSearchService.search(anyString(), anyMap())).thenReturn(searchResult);
		when(searchResult.getCount()).thenReturn(1);
		when(flexibleSearchService.search(anyString(), anyMap())).thenReturn(searchResult);
		when(searchResult.getResult()).thenReturn(resultList);
		when(resultList.get(0)).thenReturn(null);

		punchoutSessionService.loadPunchOutSession("testId1");
	}

	@Test(expected = PunchOutSessionNotFoundException.class)
	public void testLoadSessionMultiStoredSessions()
	{
		when(searchResult.getCount()).thenReturn(2);
		when(flexibleSearchService.search(anyString(), anyMap())).thenReturn(searchResult);
		punchoutSessionService.loadPunchOutSession("testId1");
	}

	@Test(expected = PunchOutSessionNotFoundException.class)
	public void testLoadSessionNotFound()
	{
		final Date sessionDate = new Date();
		when(searchResult.getCount()).thenReturn(1);
		when(searchResult.getResult()).thenReturn(Arrays.asList(storedSession));
		when(flexibleSearchService.search(anyString(), anyMap())).thenReturn(searchResult);
		when(storedSession.getPunchOutSession()).thenReturn(null);

		punchoutSessionService.loadPunchOutSession("testId1");
	}

	@Test(expected = PunchOutException.class)
	public void testRetrieveUserIdWithNoOrgs()
	{
		final List<Organization > organizationList = new ArrayList<>();
		when(punchoutSession.getInitiatedBy()).thenReturn( organizationList);
		punchoutSessionService.retrieveUserId(punchoutSession);
	}

	@Test(expected = PunchOutException.class)
	public void testRetrieveUserIdWithInvalidOrgs()
	{
		final List<Organization > organizationList = new ArrayList<>();
		organizationList.add(new Organization());
		when(punchoutSession.getInitiatedBy()).thenReturn( organizationList);
		when(punchOutCredentialService.getCustomerForCredentialNoAuth(any())).thenReturn(null);
		punchoutSessionService.retrieveUserId(punchoutSession);
	}

	@Test
	public void testRetrieveUserIdWithOrgs()
	{
		final List<Organization> organizationList = new ArrayList<>();
		final B2BCustomerModel customer = mock(B2BCustomerModel.class);
		final String CUSTOMER_ID = "dummy_customer_id";
		when(customer.getUid()).thenReturn(CUSTOMER_ID);
		organizationList.add(new Organization());
		when(punchoutSession.getInitiatedBy()).thenReturn(organizationList);
		when(punchOutCredentialService.getCustomerForCredentialNoAuth(any())).thenReturn(customer);
		assertEquals(CUSTOMER_ID, punchoutSessionService.retrieveUserId(punchoutSession));
	}

}
