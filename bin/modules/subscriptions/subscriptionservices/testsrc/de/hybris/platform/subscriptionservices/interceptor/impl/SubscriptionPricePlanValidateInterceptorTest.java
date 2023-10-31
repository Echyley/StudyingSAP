/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.subscriptionservices.interceptor.impl;

/**
 * JUnit test suite for {@link de.hybris.platform.subscriptionservices.interceptor.impl.SubscriptionPricePlanValidateInterceptor}
 */

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.UsageChargeModel;
import de.hybris.platform.subscriptionservices.model.UsageUnitModel;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;


@UnitTest
public class SubscriptionPricePlanValidateInterceptorTest
{

	private AbstractParentChildValidateInterceptor validator;
	private SubscriptionPricePlanModel pricePlanModel;

	@Before
	public void setUp() throws ImpExException
	{
		validator = new SubscriptionPricePlanValidateInterceptor();
		pricePlanModel = new SubscriptionPricePlanModel();
	}

	@Test
	public void testValidatePricePlanWithoutUsagePrice() throws InterceptorException
	{
		validator.doValidate(pricePlanModel, null);
	}

	@Test()
	public void testValidatePricePlanWithValidUsagePriceSet() throws InterceptorException
	{
		UsageUnitModel unitModel1 = new UsageUnitModel(), unitModel2 = new UsageUnitModel();
		pricePlanModel.setUsageCharges(Arrays.asList(new UsageChargeModel(unitModel1), new UsageChargeModel(unitModel2)));

		validator.doValidate(pricePlanModel, null);
	}

	@Test(expected = InterceptorException.class)
	public void testValidatePricePlanWithInvalidValidUsagePriceSet() throws InterceptorException
	{
		UsageUnitModel unitModel = mock(UsageUnitModel.class);
		given(unitModel.getName(any())).willReturn("fakeName");
		pricePlanModel.setUsageCharges(Arrays.asList(new UsageChargeModel(unitModel), new UsageChargeModel(unitModel)));

		validator.doValidate(pricePlanModel, null);
	}
}
