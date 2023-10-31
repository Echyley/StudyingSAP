/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.hook;

import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.retail.oaa.commerce.services.sourcing.strategy.SourcingStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapOaaCartValidationHookTest {
	
	private static final String SOURCING_INVALID = "ordersourcinginvalid";
		
	@InjectMocks
	DefaultSapOaaCartValidationHook defaultSapOaaCartValidationHook = new DefaultSapOaaCartValidationHook();
	
	@Mock
	private SourcingStrategy sourcingStrategy;
	
	@Mock
	private CommerceCartParameter parameter;
	@Mock
	private CartModel cartModel;
	
	@Before()
	public void setup()
	{
		given(parameter.getCart()).willReturn(cartModel);
	}
	
	@Test
	public void successfulSourcingAfterValidateCartMethodTest()
	{
		final List<CommerceCartModification> modifications = new ArrayList<>();
		given(sourcingStrategy.doSourcing(cartModel)).willReturn(true);
		defaultSapOaaCartValidationHook.afterValidateCart(parameter, modifications);
		Assert.assertEquals("Cart modification should not be created if sourcing is successful", 0, modifications.size());
	}
	
	@Test
	public void failedSourcingAfterValidateCartMethodTest()
	{
		final List<CommerceCartModification> modifications = new ArrayList<>();
		given(sourcingStrategy.doSourcing(cartModel)).willReturn(false);
		defaultSapOaaCartValidationHook.afterValidateCart(parameter, modifications);
		Assert.assertEquals("Cart modification should  be created if sourcing fails", 1, modifications.size());
		Assert.assertEquals("Validating the Status code", SOURCING_INVALID, modifications.get(0).getStatusCode());
	}

}
