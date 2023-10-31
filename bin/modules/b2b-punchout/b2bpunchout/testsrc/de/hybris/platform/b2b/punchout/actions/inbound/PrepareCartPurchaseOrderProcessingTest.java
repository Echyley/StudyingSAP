/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.services.PunchOutConfigurationService;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.data.CartData;

import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PrepareCartPurchaseOrderProcessingTest
{
	private final static String COST_CENTER = "Test123445";

	@InjectMocks
	private PrepareCartPurchaseOrderProcessing prepareCartPurchaseOrderProcessing;

	@Mock
	private CartFacade cartFacade;
	@Mock
	private PunchOutConfigurationService punchOutConfigurationService;

	private CartData cartData;

	@Before
	public void setUp() throws FileNotFoundException
	{
		cartData = new CartData();
		when(cartFacade.getCurrentCart()).thenReturn(cartData);
		when(punchOutConfigurationService.getDefaultCostCenter()).thenReturn(COST_CENTER);
	}

	@Test
	public void testGenerateCxmlResponse()
	{
		prepareCartPurchaseOrderProcessing.process();

		assertThat(cartData).hasFieldOrPropertyWithValue("paymentType.code", CheckoutPaymentType.ACCOUNT.getCode())
							.hasFieldOrPropertyWithValue("costCenter.code", cartData.getCostCenter().getCode());
		verify(cartFacade).update(cartData);
	}
}
