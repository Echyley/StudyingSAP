/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.payment.cybersource.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionInfoData;
import de.hybris.platform.acceleratorservices.payment.utils.AcceleratorDigestUtils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSignatureValidationStrategyTest
{

	@Mock
	private SiteConfigService siteConfigService;

	@Mock
	private AcceleratorDigestUtils digestUtils;

	@Spy
	@InjectMocks
	private DefaultSignatureValidationStrategy defaultSignatureValidationStrategy;

	private SubscriptionInfoData subscriptionInfoData;

	@Before
	public void setUp() throws Exception
	{
		doReturn("dummyDigest").when(digestUtils).getPublicDigest(nullable(String.class), nullable(String.class));

		subscriptionInfoData = new SubscriptionInfoData();
		subscriptionInfoData.setSubscriptionID("id");
		subscriptionInfoData.setSubscriptionIDPublicSignature("dummyDigest");
		subscriptionInfoData.setSubscriptionSignedValue("data");
	}

	@Test
	public void testValidateSignatureValid()
	{
		assertThat(defaultSignatureValidationStrategy.validateSignature(subscriptionInfoData)).isTrue();
	}


	@Test
	public void testValidateSignatureInValid()
	{
		subscriptionInfoData.setSubscriptionIDPublicSignature("invalidDummyDigest");
		assertThat(defaultSignatureValidationStrategy.validateSignature(subscriptionInfoData)).isFalse();
	}

	@Test
	public void testValidateSignatureReturnsFalseWhenNulls()
	{
		subscriptionInfoData.setSubscriptionIDPublicSignature(null);
		subscriptionInfoData.setSubscriptionSignedValue("data");
		assertThat(defaultSignatureValidationStrategy.validateSignature(subscriptionInfoData)).isFalse();

		subscriptionInfoData.setSubscriptionIDPublicSignature("dummyDigest");
		subscriptionInfoData.setSubscriptionSignedValue(null);
		assertThat(defaultSignatureValidationStrategy.validateSignature(subscriptionInfoData)).isFalse();

		subscriptionInfoData.setSubscriptionIDPublicSignature(null);
		subscriptionInfoData.setSubscriptionSignedValue(null);
		assertThat(defaultSignatureValidationStrategy.validateSignature(subscriptionInfoData)).isFalse();
	}

	@Test
	public void testValidateSignatureReturnsFalseWhenException() throws Exception
	{
		doThrow(NoSuchAlgorithmException.class).when(digestUtils).getPublicDigest(nullable(String.class), nullable(String.class));
		assertThat(defaultSignatureValidationStrategy.validateSignature(subscriptionInfoData)).isFalse();

		doThrow(InvalidKeyException.class).when(digestUtils).getPublicDigest(nullable(String.class), nullable(String.class));
		assertThat(defaultSignatureValidationStrategy.validateSignature(subscriptionInfoData)).isFalse();
	}

}
