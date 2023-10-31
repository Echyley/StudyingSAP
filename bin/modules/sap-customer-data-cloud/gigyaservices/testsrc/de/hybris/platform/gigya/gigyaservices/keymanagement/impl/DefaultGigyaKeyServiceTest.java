/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.keymanagement.impl;

import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.gigya.gigyaservices.data.GigyaJWTContent;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaPublicKeyModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

/**
 * Test class for GigyaKeyService
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGigyaKeyServiceTest
{

	private static final String SAMPLE_ID_TOKEN_HEADER = "sample";
	private static final String SAMPLE_ID_TOKEN_PAYLOAD = "payload";
	private static final String SAMPLE_ID_TOKEN_SIGNATURE = "signature";
	private static final String SAMPLE_ID_TOKEN_KID = "kid";
	private static final Integer SAMPLE_EXP_TIMESTAMP = 1657250895;
	private static final Integer SAMPLE_ACTIVE_TIMESTAMP = 1757250895;

	@InjectMocks
	private final DefaultGigyaKeyService gigyKeyService = new DefaultGigyaKeyService();

	@InjectMocks
	private final DefaultGigyaKeyService gigyKeyServiceSpy = Mockito.spy(gigyKeyService);

	@Mock
	private GenericDao<GigyaConfigModel> gigyaConfigGenericDao;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Mock
	private GenericDao<GigyaPublicKeyModel> gigyaPublicKeyGenericDao;

	@Mock
	GigyaPublicKeyModel gigyaPublicKeyModel;

	@Mock
	private GigyaJWTContent gigyaJwtHeader;

	@Mock
	private GigyaJWTContent gigyaJwtPayload;

	@Mock
	PublicKey mockPublicKey;

	@Mock
	Signature mockSignature;

	private StringBuilder idToken;

	@Before
	public void setUp()
	{

		idToken = new StringBuilder();
		idToken.append(SAMPLE_ID_TOKEN_HEADER).append('.').append(SAMPLE_ID_TOKEN_PAYLOAD).append('.')
		                .append(Base64.getEncoder().encodeToString(SAMPLE_ID_TOKEN_SIGNATURE.getBytes()));
	}

	@Test
	public void testValidateTokenExpiryAndSignatureWhenNeitherConfigOrIdTokenExists()
	{

		stubGigyaConfig();

		Assert.assertFalse(gigyKeyService.validateTokenExpiryAndSignature(gigyaConfig, null));
	}

	@Test
	public void testValidateTokenExpiryAndSignatureWhenIdTokenExpired()
	{

		stubGigyaConfig();
		Mockito.doReturn(gigyaJwtPayload).when(gigyKeyServiceSpy).decodeJWTContent(Mockito.anyString());
		Mockito.lenient().when(gigyaJwtPayload.getExp()).thenReturn(SAMPLE_EXP_TIMESTAMP);

		Assert.assertFalse(gigyKeyServiceSpy.validateTokenExpiryAndSignature(gigyaConfig, idToken.toString()));
	}

	@Test
	public void testValidateTokenExpiryAndSignatureWhenSignatureInvalid() throws SignatureException
	{

		stubGigyaConfig();
		Mockito.doReturn(gigyaJwtHeader).when(gigyKeyServiceSpy).decodeJWTContent(SAMPLE_ID_TOKEN_HEADER);
		Mockito.lenient().when(gigyaJwtHeader.getKid()).thenReturn(SAMPLE_ID_TOKEN_KID);
		Mockito.lenient().when(mockSignature.verify(Mockito.any())).thenReturn(Boolean.FALSE);

		Mockito.doReturn(gigyaJwtPayload).when(gigyKeyServiceSpy).decodeJWTContent(SAMPLE_ID_TOKEN_PAYLOAD);
		Mockito.lenient().when(gigyaJwtPayload.getExp()).thenReturn(SAMPLE_ACTIVE_TIMESTAMP);

		Mockito.doReturn(mockPublicKey).when(gigyKeyServiceSpy).retreivePublicKeyOfASite(Mockito.eq(gigyaConfig),
		                Mockito.anyString());

		try (MockedStatic mockedSig = Mockito.mockStatic(Signature.class))
		{
			mockedSig.when(() -> Signature.getInstance("SHA256withRSA")).thenReturn(mockSignature);
			Assert.assertFalse(gigyKeyServiceSpy.validateTokenExpiryAndSignature(gigyaConfig, idToken.toString()));
		}
	}

	@Test
	public void testValidateTokenExpiryAndSignatureWhenSignatureVerificationExceptionOccurs()
	                throws SignatureException
	{

		stubGigyaConfig();
		Mockito.doReturn(gigyaJwtHeader).when(gigyKeyServiceSpy).decodeJWTContent(SAMPLE_ID_TOKEN_HEADER);
		Mockito.lenient().when(gigyaJwtHeader.getKid()).thenReturn(SAMPLE_ID_TOKEN_KID);
		Mockito.lenient().when(mockSignature.verify(Mockito.any())).thenThrow(SignatureException.class);

		Mockito.doReturn(gigyaJwtPayload).when(gigyKeyServiceSpy).decodeJWTContent(SAMPLE_ID_TOKEN_PAYLOAD);
		Mockito.lenient().when(gigyaJwtPayload.getExp()).thenReturn(SAMPLE_ACTIVE_TIMESTAMP);

		Mockito.doReturn(mockPublicKey).when(gigyKeyServiceSpy).retreivePublicKeyOfASite(Mockito.eq(gigyaConfig),
		                Mockito.anyString());

		try (MockedStatic mockedSig = Mockito.mockStatic(Signature.class))
		{
			mockedSig.when(() -> Signature.getInstance("SHA256withRSA")).thenReturn(mockSignature);
			Assert.assertFalse(gigyKeyServiceSpy.validateTokenExpiryAndSignature(gigyaConfig, idToken.toString()));
		}
	}

	@Test
	public void testValidateTokenExpiryAndSignatureWhenBothValid() throws SignatureException
	{

		stubGigyaConfig();
		Mockito.lenient().when(gigyaPublicKeyGenericDao.find(Mockito.anyMap()))
		                .thenReturn(Collections.singletonList(gigyaPublicKeyModel));

		Mockito.doReturn(gigyaJwtHeader).when(gigyKeyServiceSpy).decodeJWTContent(SAMPLE_ID_TOKEN_HEADER);
		Mockito.lenient().when(gigyaJwtHeader.getKid()).thenReturn(SAMPLE_ID_TOKEN_KID);
		Mockito.lenient().when(mockSignature.verify(Mockito.any())).thenReturn(Boolean.TRUE);

		Mockito.doReturn(gigyaJwtPayload).when(gigyKeyServiceSpy).decodeJWTContent(SAMPLE_ID_TOKEN_PAYLOAD);
		Mockito.lenient().when(gigyaJwtPayload.getExp()).thenReturn(SAMPLE_ACTIVE_TIMESTAMP);
		Mockito.doReturn(mockPublicKey).when(gigyKeyServiceSpy).retreivePublicKeyOfASite(Mockito.eq(gigyaConfig),
		                Mockito.anyString());

		try (MockedStatic mockedSig = Mockito.mockStatic(Signature.class))
		{
			mockedSig.when(() -> Signature.getInstance("SHA256withRSA")).thenReturn(mockSignature);
			Assert.assertTrue(gigyKeyServiceSpy.validateTokenExpiryAndSignature(gigyaConfig, idToken.toString()));
		}
	}

	void stubGigyaConfig()
	{
		Mockito.lenient().when(gigyaConfigGenericDao.find(Mockito.anyMap()))
		                .thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.lenient().when(gigyaConfig.getGigyaSiteSecret()).thenReturn(null);
		Mockito.lenient().when(gigyaConfig.getGigyaUserSecret()).thenReturn(null);
		Mockito.lenient().when(gigyaConfigGenericDao.find(Mockito.anyMap()))
		                .thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.lenient().when(gigyaConfig.getGigyaSiteSecret()).thenReturn(null);
		Mockito.lenient().when(gigyaConfig.getGigyaUserSecret()).thenReturn(null);
		Mockito.lenient().when(gigyaPublicKeyGenericDao.find(Mockito.anyMap()))
		                .thenReturn(Collections.singletonList(gigyaPublicKeyModel));
	}
}
