/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseprofilefacades.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chineseprofilefacades.data.MobileNumberVerificationData;
import de.hybris.platform.chineseprofileservices.customer.ChineseCustomerAccountService;
import de.hybris.platform.chineseprofileservices.model.MobileNumberVerificationModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserIdDecorationService;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
public class ChineseVerificationCodeStrategyTest
{
	private static final String MOBILE_NUMBER = "13800000000";
	@Mock
	private ModelService modelService;

	@Mock
	private ChineseCustomerAccountService chineseCustomerAccountService;

	@Mock
	private Converter<MobileNumberVerificationData, MobileNumberVerificationModel> verificationCodeReverseConverter;

	@Mock
	private Converter<MobileNumberVerificationModel, MobileNumberVerificationData> verificationConverter;

	@Mock
	private UserIdDecorationService userIdDecorationService;
	private ChineseVerificationCodeStrategy verificationCodeStrategy;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		verificationCodeStrategy = new ChineseVerificationCodeStrategy(modelService, chineseCustomerAccountService,
				verificationCodeReverseConverter, verificationConverter, userIdDecorationService);
	}

	@Test
	public void testSaveVerificationCodeIfVerificationIsNotFound()
	{
		final MobileNumberVerificationData data = new MobileNumberVerificationData();
		data.setMobileNumber(MOBILE_NUMBER);
		data.setVerificationCode("1234");
		when(chineseCustomerAccountService.getMobileNumberVerificationCode(MOBILE_NUMBER)).thenReturn(Optional.empty());
		when(userIdDecorationService.decorateUserId(anyString())).thenReturn(MOBILE_NUMBER + "|isolated-site-a");
		verificationCodeStrategy.saveVerificationCode(data);
		verify(modelService, times(0)).remove(any());
		verify(modelService, times(1)).save(any());
	}

	@Test
	public void testSaveVerificationCodeIfVerificationIsExpired()
	{
		final MobileNumberVerificationData data = new MobileNumberVerificationData();
		data.setMobileNumber(MOBILE_NUMBER);
		data.setVerificationCode("1234");
		final MobileNumberVerificationModel verificationModel = new MobileNumberVerificationModel();
		when(chineseCustomerAccountService.getMobileNumberVerificationCode(MOBILE_NUMBER)).thenReturn(
				Optional.of(verificationModel));
		when(chineseCustomerAccountService.isVerificationCodeExpired(any())).thenReturn(true);
		when(userIdDecorationService.decorateUserId(anyString())).thenReturn(MOBILE_NUMBER + "|isolated-site-a");
		verificationCodeStrategy.saveVerificationCode(data);
		verify(modelService, times(1)).remove(verificationModel);
		verify(modelService, times(1)).save(any());
	}

	@Test
	public void testSaveVerificationCodeIfVerificationIsNotExpired()
	{
		final MobileNumberVerificationData data = new MobileNumberVerificationData();
		data.setMobileNumber(MOBILE_NUMBER);
		data.setVerificationCode("1234");
		final MobileNumberVerificationModel verificationModel = new MobileNumberVerificationModel();
		when(chineseCustomerAccountService.getMobileNumberVerificationCode(MOBILE_NUMBER)).thenReturn(
				Optional.of(verificationModel));
		when(chineseCustomerAccountService.isVerificationCodeExpired(any())).thenReturn(false);
		when(userIdDecorationService.decorateUserId(anyString())).thenReturn(MOBILE_NUMBER + "|isolated-site-a");
		verificationCodeStrategy.saveVerificationCode(data);
		verify(modelService, times(0)).remove(any());
		verify(modelService, times(1)).save(any());
	}

	@Test
	public void testGetVerificationCodeIfVerificationIsFound()
	{
		final MobileNumberVerificationModel verificationModel = new MobileNumberVerificationModel();
		when(chineseCustomerAccountService.getMobileNumberVerificationCode(MOBILE_NUMBER)).thenReturn(
				Optional.of(verificationModel));
		final MobileNumberVerificationData verificationData = new MobileNumberVerificationData();
		when(verificationConverter.convert(verificationModel)).thenReturn(verificationData);
		final Optional<MobileNumberVerificationData> result = verificationCodeStrategy.getVerificationCode(MOBILE_NUMBER);
		assertEquals(Optional.of(verificationData), result);
	}

	@Test
	public void testGetVerificationCodeIfVerificationIsNotFound()
	{
		when(chineseCustomerAccountService.getMobileNumberVerificationCode(MOBILE_NUMBER)).thenReturn(Optional.empty());
		final Optional<MobileNumberVerificationData> result = verificationCodeStrategy.getVerificationCode(MOBILE_NUMBER);
		assertEquals(Optional.empty(), result);
	}

	@Test
	public void testRemoveVerificationCodeIfVerificationIsFound()
	{
		final MobileNumberVerificationModel verificationModel = new MobileNumberVerificationModel();
		when(chineseCustomerAccountService.getMobileNumberVerificationCode(MOBILE_NUMBER)).thenReturn(
				Optional.of(verificationModel));
		verificationCodeStrategy.removeVerificationCode(MOBILE_NUMBER);
		verify(modelService, times(1)).remove(verificationModel);
	}

	@Test
	public void testRemoveVerificationCodeIfVerificationIsNotFound()
	{
		when(chineseCustomerAccountService.getMobileNumberVerificationCode(MOBILE_NUMBER)).thenReturn(Optional.empty());
		verificationCodeStrategy.removeVerificationCode(MOBILE_NUMBER);
		verify(modelService, times(0)).remove(any());
	}
}
