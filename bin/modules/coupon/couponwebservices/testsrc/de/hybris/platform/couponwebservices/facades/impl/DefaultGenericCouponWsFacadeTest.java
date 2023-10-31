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
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.couponservices.service.data.CouponResponse;
import de.hybris.platform.couponservices.services.CouponService;
import de.hybris.platform.couponwebservices.CouponNotFoundException;
import de.hybris.platform.couponwebservices.dto.CouponValidationResponseWsDTO;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGenericCouponWsFacadeTest
{
	@InjectMocks
	private DefaultGenericCouponWsFacades defaultGenericCouponWsFacades;
	@Mock
	private CouponService couponService;
	@Mock
	private UserService userService;
	@Mock
	private Converter<CouponResponse, CouponValidationResponseWsDTO> couponValidationResponseWsDTOConverter;

	@Test(expected = IllegalArgumentException.class)
	public void shouldHaveErrorWhenEmptyCouponCode()
	{
		defaultGenericCouponWsFacades.validateCoupon(null, "");
	}

	@Test(expected = CouponNotFoundException.class)
	public void shouldHaveErrorWhenCouponNotExist()
	{
		Mockito.when(couponService.getCouponForCode("NOT_EXIST_COUPON")).thenReturn(Optional.empty());
		defaultGenericCouponWsFacades.validateCoupon("NOT_EXIST_COUPON", "");
	}

	@Test
	public void shouldWorkForCouponCodeAndEmptyCustomer()
	{
		//given
		final AbstractCouponModel couponModel = new AbstractCouponModel();
		couponModel.setCouponId("COUPON01");
		final Optional<AbstractCouponModel> couponModelOP = Optional.of(couponModel);
		final CouponResponse couponResponse = new CouponResponse();
		couponResponse.setCouponId("COUPON01");
		couponResponse.setSuccess(true);
		final CouponValidationResponseWsDTO couponValidationResponseWsDTO = new CouponValidationResponseWsDTO();
		couponValidationResponseWsDTO.setValid(true);
		Mockito.when(couponService.getCouponForCode("COUPON01")).thenReturn(couponModelOP);
		Mockito.when(couponService.validateCouponCode("COUPON01", null)).thenReturn(couponResponse);
		Mockito.when(couponValidationResponseWsDTOConverter.convert(couponResponse)).thenReturn(couponValidationResponseWsDTO);
		//when
		final CouponValidationResponseWsDTO response = defaultGenericCouponWsFacades.validateCoupon("COUPON01", "");
		//then
		assertEquals("COUPON01", response.getCouponId());
		assertNull(response.getGeneratedCouponCode());
		assertTrue(response.getValid());
	}

	@Test
	public void shouldWorkForCouponCodeAndCustomer()
	{
		//given
		final AbstractCouponModel couponModel = new AbstractCouponModel();
		couponModel.setCouponId("COUPON01");
		final Optional<AbstractCouponModel> couponModelOP = Optional.of(couponModel);
		final UserModel customer = new UserModel();
		final CouponResponse couponResponse = new CouponResponse();
		couponResponse.setCouponId("COUPON01");
		couponResponse.setSuccess(true);
		final CouponValidationResponseWsDTO couponValidationResponseWsDTO = new CouponValidationResponseWsDTO();
		couponValidationResponseWsDTO.setValid(true);

		Mockito.when(couponService.getCouponForCode("COUPON01")).thenReturn(couponModelOP);
		Mockito.when(userService.getUserForUID("USER01")).thenReturn(customer);
		Mockito.when(couponService.validateCouponCode("COUPON01", customer)).thenReturn(couponResponse);
		Mockito.when(couponValidationResponseWsDTOConverter.convert(couponResponse)).thenReturn(couponValidationResponseWsDTO);
		//when
		final CouponValidationResponseWsDTO response = defaultGenericCouponWsFacades.validateCoupon("COUPON01", "USER01");
		//then
		assertEquals("COUPON01", response.getCouponId());
		assertNull(response.getGeneratedCouponCode());
		assertTrue(response.getValid());
	}
}
