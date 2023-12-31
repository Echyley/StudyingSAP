/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenuecloudproduct.inbound;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.subscriptionservices.jalo.SubscriptionPricePlan;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;



/**
 * JUnit test suite for {@link SapRevenueCloudSubscriptionPricePlanEndDateTranslatorTest}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapRevenueCloudSubscriptionPricePlanEndDateTranslatorTest
{
	@Mock
	private SapRevenueCloudProductInboudHelper sapRevenueCloudProductInboudHelper;

	@InjectMocks
	private SapRevenueCloudSubscriptionPricePlanEndDateTranslator endDateTranslator;

	@Before
	public void setUp()
	{
		endDateTranslator.setSapRevenueCloudProductInboudHelper(sapRevenueCloudProductInboudHelper);
		lenient().doNothing().when(sapRevenueCloudProductInboudHelper).processSubscriptionPricePlanEndDate(any(String.class),
				any(CatalogVersion.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwExceptionIfParametersAreInvalid() throws ImpExException
	{
		lenient().when(sapRevenueCloudProductInboudHelper.getAttributeValue(any(Item.class), eq(SubscriptionPricePlanModel.CATALOGVERSION)))
				.thenReturn(new CatalogVersion());
		endDateTranslator.performImport(null, new SubscriptionPricePlan());
	}

}
