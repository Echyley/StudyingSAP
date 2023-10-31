/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.businessobject.impl.BasketImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings(
{ "javadoc", "rawtypes", "unchecked" })
public class DefaultCartPopulatorTest
{

	DefaultCartPopulator classUnderTest = new DefaultCartPopulator();

	@Before
	public void setUp()
	{
		DefaultAbstractOrderPopulatorTest.injectMockedBeans(classUnderTest);
	}

	@Test
	public void testPopulateHeaderAttributesFromSuper()
	{
		final Basket source = new BasketImpl();
		DefaultAbstractOrderPopulatorTest.setupCart(source);

		final CartData target = new CartData();
		classUnderTest.populateHeader(source, target);
		Assert.assertEquals(DefaultAbstractOrderPopulatorTest.NET_VALUE_WO_FREIGHT_EXAMPLE, target.getSubTotal().getValue());
	}

	@Test
	public void testPopulateHeaderPurchaseOrderNo()
	{
		final Basket source = new BasketImpl();
		DefaultAbstractOrderPopulatorTest.setupCart(source);
		final String purchaseOrderExt = "A";
		source.getHeader().setPurchaseOrderExt(purchaseOrderExt);
		final CartData target = new CartData();
		classUnderTest.populateHeader(source, target);
		Assert.assertEquals(purchaseOrderExt, target.getPurchaseOrderNumber());
	}
}
