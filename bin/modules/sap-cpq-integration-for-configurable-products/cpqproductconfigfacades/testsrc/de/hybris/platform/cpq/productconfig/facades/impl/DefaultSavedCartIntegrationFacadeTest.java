/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cpq.productconfig.services.AbstractOrderIntegrationService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link DefaultSavedCartIntegrationFacade}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSavedCartIntegrationFacadeTest
{
	private static final String CONFIG_ID = "config123";
	private static final String CART_CODE = "cart123";
	private static final Integer ENTRY_NUMBER = Integer.valueOf(3);
	private static final String P_CODE = "product123";
	@Mock
	private AbstractOrderIntegrationService abstractOrderIntegrationService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private UserService userService;

	@InjectMocks
	private DefaultSavedCartIntegrationFacade classUnderTest;
	private final UserModel user = new UserModel();
	private final CartModel cartModel = new CartModel();
	private final AbstractOrderEntryModel cartEntry = new CartEntryModel();

	@Before
	public void setUp()
	{
		cartModel.setEntries(new ArrayList<AbstractOrderEntryModel>());
		cartModel.getEntries().add(cartEntry);
		cartEntry.setEntryNumber(ENTRY_NUMBER);
		cartEntry.setProduct(new ProductModel());
		cartEntry.getProduct().setCode(P_CODE);
		when(commerceCartService.getCartForCodeAndUser(CART_CODE, user)).thenReturn(cartModel);
		when(userService.getCurrentUser()).thenReturn(user);
		when(abstractOrderIntegrationService.getConfigIdForAbstractOrderEntry(cartEntry)).thenReturn(CONFIG_ID);
	}

	@Test
	public void testGetConfigurationId() throws CommerceSaveCartException
	{
		assertEquals(CONFIG_ID, classUnderTest.getConfigurationId(CART_CODE, ENTRY_NUMBER.intValue()));
	}

	@Test(expected = CommerceSaveCartException.class)
	public void testGetConfigurationIdInvalidCart() throws CommerceSaveCartException
	{
		classUnderTest.getConfigurationId("INVALID", ENTRY_NUMBER.intValue());
	}

	@Test(expected = CommerceSaveCartException.class)
	public void testGetConfigurationIdInvalidEntry() throws CommerceSaveCartException
	{
		classUnderTest.getConfigurationId(CART_CODE, -1);
	}

	@Test
	public void testGetProductCode() throws CommerceSaveCartException
	{
		assertEquals(P_CODE, classUnderTest.getProductCode(CART_CODE, ENTRY_NUMBER.intValue()));
	}
}
