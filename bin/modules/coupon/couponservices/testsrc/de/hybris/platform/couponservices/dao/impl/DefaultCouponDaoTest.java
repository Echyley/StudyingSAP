/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.couponservices.dao.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapperException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultCouponDaoTest
{
	private static final String ANY_STRING = "anyString";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@InjectMocks
	private final DefaultCouponDao dao = new DefaultCouponDao();


	@Test
	public void nullTestGetCouponById() throws RuleParameterValueMapperException
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		dao.findCouponById(null);
	}

	@Test
	public void noCouponFoundTest() throws RuleParameterValueMapperException
	{
		Mockito.doThrow(ModelNotFoundException.class).when(flexibleSearchService).searchUnique(Mockito.anyObject());

		//expect
		expectedException.expect(ModelNotFoundException.class);

		//when
		dao.findCouponById(ANY_STRING);
	}

	@Test
	public void couponFoundTest() throws RuleParameterValueMapperException
	{
		//given
		final AbstractCouponModel coupon = Mockito.mock(AbstractCouponModel.class);

		Mockito.lenient().when(flexibleSearchService.searchUnique(Mockito.anyObject())).thenReturn(coupon);

		//when
		final AbstractCouponModel couponAsResult = dao.findCouponById(ANY_STRING);

		//then
		Assert.assertEquals(coupon, couponAsResult);
	}
}
