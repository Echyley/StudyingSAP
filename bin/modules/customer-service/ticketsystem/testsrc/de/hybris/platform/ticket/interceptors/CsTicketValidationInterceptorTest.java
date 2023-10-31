/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.ticket.interceptors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketPriority;
import de.hybris.platform.ticket.model.CsTicketModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class CsTicketValidationInterceptorTest
{
	@Mock
	UserService userService;

	@InjectMocks
	CsTicketValidationInterceptor validator;

	private CsTicketModel ticket;

	private CustomerModel ticketOwner, anonymousUser;

	@Before
	public void initializeContext()
	{
		ticketOwner = new CustomerModel();
		anonymousUser = new CustomerModel();
		ticket = new CsTicketModel();
		ticket.setCustomer(ticketOwner);
		ticket.setTicketID("123");
		ticket.setPriority(CsTicketPriority.LOW);
		ticket.setCategory(CsTicketCategory.PROBLEM);
		ticket.setHeadline("test ticket");
	}

	@Test
	public void verifyPassedWhenNoOrderWithMandatoryFields() throws InterceptorException
	{
		validator.onValidate(ticket, null);
	}


	@Test(expected = InterceptorException.class)
	public void verifyFailedWhenTicketIdIsNullTest() throws InterceptorException
	{
		ticket.setTicketID(null);
		validator.onValidate(ticket, null);
	}

	@Test(expected = InterceptorException.class)
	public void verifyFailedWhenTicketHeadlineIsNullTest() throws InterceptorException
	{
		ticket.setHeadline(null);
		validator.onValidate(ticket, null);
	}

	@Test
	public void verifyFailedWhenTicketCategoryIsNullTest() throws InterceptorException
	{
		ticket.setCategory(null);
		assertThatThrownBy(() -> validator.onValidate(ticket, null)).hasMessageEndingWith(
				"The ticket must have a category specified").isInstanceOf(InterceptorException.class);
	}

	@Test
	public void verifyFailedWhenTicketPriorityIsNullTest() throws InterceptorException
	{
		ticket.setPriority(null);
		assertThatThrownBy(() -> validator.onValidate(ticket, null)).hasMessageEndingWith(
				"The ticket must have a priority specified").isInstanceOf(InterceptorException.class);
	}

	@Test
	public void verifyPassedWhenAssignAnonymousCartToTicketTest() throws InterceptorException
	{
		ticket.setOrder(createCartWithOwner(anonymousUser));
		assertThatThrownBy(() -> validator.onValidate(ticket, null)).hasMessageEndingWith(
				"If an customer and order is specified on a ticket then the customer must own the order").isInstanceOf(InterceptorException.class);
	}

	@Test
	public void verifyPassedWhenAssignSameCartToTicketTest() throws InterceptorException
	{
		ticket.setOrder(createCartWithOwner(ticketOwner));
		validator.onValidate(ticket, null);
	}

	@Test
	public void verifyFailedOneCustomerCartToAnotherCustomerToTicketTest() throws InterceptorException
	{
		ticket.setOrder(createCartWithOwner(new CustomerModel()));
		assertThatThrownBy(() -> validator.onValidate(ticket, null)).hasMessageEndingWith(
				"If an customer and order is specified on a ticket then the customer must own the order").isInstanceOf(InterceptorException.class);
	}

	private CartModel createCartWithOwner(UserModel user)
	{
		final var cart = new CartModel();
		cart.setUser(user);
		return cart;
	}
}
