/*
 * [y] hybris Platform
 *
 * Copyright (c) 2021 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.couponwebservices.facades.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.couponservices.model.CouponRedemptionModel;
import de.hybris.platform.couponservices.services.CouponService;
import de.hybris.platform.couponwebservices.CouponNotFoundException;
import de.hybris.platform.couponwebservices.dto.CouponRedemptionWsDTO;
import de.hybris.platform.servicelayer.model.ModelService;



import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCouponRedemptionWsFacadeTest
{
	@InjectMocks
	private DefaultCouponRedemptionWsFacade defaultCouponRedemptionWsFacade;
	@Mock
	private CouponService couponService;
	@Mock
	private ModelService modelService;

	@Test
	public void shouldWorkForValidCouponCode()
	{
		//given
		final CouponRedemptionWsDTO createRedemptionWsDTO = new CouponRedemptionWsDTO();
		createRedemptionWsDTO.setCouponCode("COUPON01");
		final AbstractCouponModel couponModel = new AbstractCouponModel();
		couponModel.setCouponId("COUPON01");
		final Optional<AbstractCouponModel> couponModelOP = Optional.of(couponModel);
		final CouponRedemptionModel couponRedemption = new CouponRedemptionModel();
		couponRedemption.setCouponCode("COUPON01");
		couponRedemption.setCoupon(couponModel);
		Mockito.when(couponService.getValidatedCouponForCode("COUPON01")).thenReturn(couponModelOP);
		Mockito.when(modelService.create(CouponRedemptionModel.class)).thenReturn(couponRedemption);
		//when
		final CouponRedemptionWsDTO response = defaultCouponRedemptionWsFacade.createCouponRedemption(createRedemptionWsDTO);
		//then
		verify(modelService).save(couponRedemption);
		assertEquals("COUPON01", response.getCouponCode());
		assertEquals("COUPON01", response.getCouponId());
	}

	@Test
	public void shouldNotWorkForInValidCouponCode()
	{
		//given
		final CouponRedemptionWsDTO createRedemptionWsDTO = new CouponRedemptionWsDTO();
		createRedemptionWsDTO.setCouponCode("COUPON01");

		final Optional<AbstractCouponModel> couponModelOP = Optional.empty();

		Mockito.when(couponService.getValidatedCouponForCode("COUPON01")).thenReturn(couponModelOP);

		//when
		final Throwable exception = catchThrowable(() -> defaultCouponRedemptionWsFacade.createCouponRedemption(createRedemptionWsDTO));
		//then
		assertThat(exception).isExactlyInstanceOf(CouponNotFoundException.class);

	}




}
