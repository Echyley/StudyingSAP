/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.hook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants;
import de.hybris.platform.assistedserviceservices.utils.AssistedServiceSession;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class AssistedServicePlaceOrderMethodHookTest
{

	@Mock
	private AssistedServiceFacade assistedServiceFacade;

	@Mock
	private ModelService modelService;

	@Mock
	private UserService userService;

	@Mock
	private SessionService sessionService;


	@InjectMocks
	private AssistedServicePlaceOrderMethodHook orderMethodHook = new AssistedServicePlaceOrderMethodHook();

	@Test
	public void afterLoggedOutPlaceOrderTest() throws InvalidCartException
	{
		final Session mockSession = mock(Session.class);
		final String CUSTOMER_ID = "customer";
		final UserModel customer = mock(UserModel.class);

		when(assistedServiceFacade.isAssistedServiceAgentLoggedIn()).thenReturn(false);
		when(sessionService.getCurrentSession()).thenReturn(mockSession);
		when(mockSession.getAttribute("ACTING_USER_UID")).thenReturn(CUSTOMER_ID);
		when(userService.getCurrentUser()).thenReturn(customer);
		when(customer.getUid()).thenReturn(CUSTOMER_ID);

		orderMethodHook.afterPlaceOrder(null, null);
		verify(modelService, never()).save(any());
		verify(modelService, never()).refresh(any());
	}

	@Test
	public void afterPlaceOrderTestWithStoreFrontASMMode() throws InvalidCartException
	{
		final AssistedServiceSession asmSession = mock(AssistedServiceSession.class);
		final UserModel agent = mock(UserModel.class);

		when(assistedServiceFacade.isAssistedServiceAgentLoggedIn()).thenReturn(true);
		when(asmSession.getAgent()).thenReturn(agent);
		when(assistedServiceFacade.getAsmSession()).thenReturn(asmSession);
		final CommerceOrderResult orderResult = new CommerceOrderResult();
		final OrderModel order = new OrderModel();
		orderResult.setOrder(order);
		final CommerceCheckoutParameter checkoutParameter = new CommerceCheckoutParameter();

		doNothing().when(modelService).save(any());
		doNothing().when(modelService).refresh(any());

		orderMethodHook.afterPlaceOrder(checkoutParameter, orderResult);

		assertEquals(order.getPlacedBy(), agent);

		verify(modelService).save(order);
		verify(modelService).refresh(order);
	}

	@Test
	public void afterPlaceOrderTestWithStoreFrontCustomerMode() throws InvalidCartException
	{
		final String AGENT_ID = "ASAgent", CUSTOMER_ID = "customer";
		// ensure we are run to new logic
		when(assistedServiceFacade.isAssistedServiceAgentLoggedIn()).thenReturn(false);
		final UserModel agent = mock(UserModel.class);
		final UserModel customer = mock(UserModel.class);
		final Session mockSession = mock(Session.class);
		when(customer.getUid()).thenReturn(CUSTOMER_ID);
		when(sessionService.getCurrentSession()).thenReturn(mockSession);
		// acting is null in storefront mode
		when(mockSession.getAttribute(AssistedservicefacadesConstants.ACTING_USER_UID)).thenReturn(null);
		when(userService.getCurrentUser()).thenReturn(customer);

		final CommerceOrderResult orderResult = new CommerceOrderResult();
		final OrderModel order = new OrderModel();
		orderResult.setOrder(order);
		final CommerceCheckoutParameter checkoutParameter = new CommerceCheckoutParameter();


		orderMethodHook.afterPlaceOrder(checkoutParameter, orderResult);

		// ensure nothing changed
		verify(modelService, never()).save(any());
		verify(modelService, never()).refresh(any());
		assertNull(order.getPlacedBy());
	}

	@Test
	public void afterPlaceOrderTestWithAgentSession() throws InvalidCartException
	{
		final String AGENT_ID = "ASAgent", CUSTOMER_ID = "customer";
		// ensure we are run to new logic
		when(assistedServiceFacade.isAssistedServiceAgentLoggedIn()).thenReturn(false);
		final UserModel agent = mock(UserModel.class);
		final UserModel customer = mock(UserModel.class);
		final Session mockSession = mock(Session.class);
		when(customer.getUid()).thenReturn(CUSTOMER_ID);

		when(sessionService.getCurrentSession()).thenReturn(mockSession);
		// acting is agent
		when(mockSession.getAttribute(AssistedservicefacadesConstants.ACTING_USER_UID)).thenReturn(AGENT_ID);
		when(userService.getCurrentUser()).thenReturn(customer);
		when(userService.getUserForUID(AGENT_ID)).thenReturn(agent);

		final CommerceOrderResult orderResult = new CommerceOrderResult();
		final OrderModel order = new OrderModel();
		orderResult.setOrder(order);
		final CommerceCheckoutParameter checkoutParameter = new CommerceCheckoutParameter();

		doNothing().when(modelService).save(any());
		doNothing().when(modelService).refresh(any());

		orderMethodHook.afterPlaceOrder(checkoutParameter, orderResult);

		assertEquals(order.getPlacedBy(), agent);
		verify(modelService).save(order);
		verify(modelService).refresh(order);
	}

	@Test
	public void afterPlaceOrderTestWithCustomerSession() throws InvalidCartException
	{
		final String AGENT_ID = "ASAgent", CUSTOMER_ID = "customer";
		// ensure we are run to new logic
		when(assistedServiceFacade.isAssistedServiceAgentLoggedIn()).thenReturn(false);
		final UserModel agent = mock(UserModel.class);
		final UserModel customer = mock(UserModel.class);
		final Session mockSession = mock(Session.class);
		when(customer.getUid()).thenReturn(CUSTOMER_ID);
		when(sessionService.getCurrentSession()).thenReturn(mockSession);
		// acting is customer
		when(mockSession.getAttribute(AssistedservicefacadesConstants.ACTING_USER_UID)).thenReturn(CUSTOMER_ID);
		when(userService.getCurrentUser()).thenReturn(customer);

		final CommerceOrderResult orderResult = new CommerceOrderResult();
		final OrderModel order = new OrderModel();
		orderResult.setOrder(order);
		final CommerceCheckoutParameter checkoutParameter = new CommerceCheckoutParameter();


		orderMethodHook.afterPlaceOrder(checkoutParameter, orderResult);

		// ensure nothing changed
		verify(modelService, never()).save(any());
		verify(modelService, never()).refresh(any());
		assertNull(order.getPlacedBy());
	}
}
