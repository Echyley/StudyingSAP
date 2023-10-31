/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.keymanagement.impl;

import java.io.IOException;
import java.io.InvalidClassException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;

import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.constants.GigyaservicesConstants;
import de.hybris.platform.gigya.gigyaservices.data.GigyaJWTContent;
import de.hybris.platform.gigya.gigyaservices.keymanagement.GigyaKeyService;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaPublicKeyModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * 
 * Default implementation of GigyaKeyService
 */
public class DefaultGigyaKeyService implements GigyaKeyService
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultGigyaKeyService.class);

	private GigyaService gigyaService;

	private ModelService modelService;

	private GenericDao<GigyaPublicKeyModel> gigyaPublicKeyGenericDao;

	@Override
	public PublicKey retreivePublicKeyOfASite(GigyaConfigModel gigyaConfig, final String checkKeyId)
	{

		GigyaPublicKeyModel gigyaPublicKey = gigyaConfig.getGigyaPublicKey();
		String modulus = (gigyaPublicKey == null ? null : gigyaPublicKey.getModulus());
		String exponent = (gigyaPublicKey == null ? null : gigyaPublicKey.getExponent());

		// Verify if Public Key is valid
		if (!(gigyaPublicKey != null && StringUtils.equals(gigyaPublicKey.getKeyId(), checkKeyId)))
		{

			final GigyaPublicKeyModel storedGigyaPublicKey = findPublicKeybyKeyId(checkKeyId);

			if (storedGigyaPublicKey == null)
			{
				gigyaPublicKey = new GigyaPublicKeyModel();
				callRawGigyaApiForPublicKey(gigyaPublicKey, gigyaConfig);
				modulus = gigyaPublicKey.getModulus();
				exponent = gigyaPublicKey.getExponent();
			}
			else
			{
				gigyaConfig.setGigyaPublicKey(storedGigyaPublicKey);
				modelService.save(gigyaConfig);
				modulus = storedGigyaPublicKey.getModulus();
				exponent = storedGigyaPublicKey.getExponent();
			}
		}
		return generatePublicKey(modulus, exponent);
	}

	@Override
	public PublicKey generatePublicKey(final String modulus, final String exponent)
	{

		final var searchListModulus = new String[] { "-", "_" };
		final var replacementListModulus = new String[] { "+", "/" };

		final String decodedModulus = StringUtils.replaceEach(modulus, searchListModulus, replacementListModulus);

		byte[] n = Base64.getDecoder().decode(decodedModulus.getBytes());
		byte[] e = Base64.getDecoder().decode(exponent.getBytes());
		PublicKey generatedPublicKey = null;

		try
		{

			BigInteger nBigInt = new BigInteger(1, n);
			BigInteger eBigInt = new BigInteger(1, e);
			RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(nBigInt, eBigInt);
			var fact = KeyFactory.getInstance(GigyaservicesConstants.RSA);
			generatedPublicKey = fact.generatePublic(rsaPubKey);

		}
		catch (NoSuchAlgorithmException | InvalidKeySpecException e1)
		{
			LOG.error("Error generating public key.", e1);
		}

		return generatedPublicKey;
	}

	/**
	 * @deprecated (use validateTokenExpiryAndSignature instead)
	 */
	@Deprecated(since = "2211", forRemoval = true)
	@Override
	public Boolean validateToken(GigyaConfigModel gigyaConfig, String idToken)
	{
		return validateTokenExpiryAndSignature(gigyaConfig, idToken);
	}

	@Override
	public Boolean validateTokenExpiryAndSignature(final GigyaConfigModel gigyaConfig, final String idToken)
	{

		if (gigyaConfig == null || idToken == null)
		{
			return false;
		}
		final String[] jwtString = idToken.split("\\.");
		final String encodedJwtHeader = jwtString[0];
		final String encodedJwtPayload = jwtString[1];
		final String tokenData = encodedJwtHeader + "." + encodedJwtPayload;
		String encodedKeySignature = jwtString[2];

		final GigyaJWTContent decodedPayload = decodeJWTContent(encodedJwtPayload);

		// Validate the token expiry
		Integer tokenExpiry = decodedPayload.getExp();
		if (tokenExpiry == null)
		{
			LOG.error("ID Token has wrong expiry information.");
			return false;
		}
		var tokenExpiryInstant = Instant.ofEpochSecond(tokenExpiry);
		if (tokenExpiryInstant.compareTo(Instant.now()) < 0)
		{
			LOG.error("ID Token has expired.");
			return false;
		}

		// Validate the token signature
		final String[] searchListKS = { "-", "_" };
		final String[] replacementListKS = { "+", "/" };
		encodedKeySignature = StringUtils.replaceEach(encodedKeySignature, searchListKS, replacementListKS);

		byte[] keySignature = Base64.getDecoder().decode(encodedKeySignature.getBytes());

		try
		{

			final GigyaJWTContent jwtHeader = decodeJWTContent(encodedJwtHeader);

			PublicKey publicJWTKey = retreivePublicKeyOfASite(gigyaConfig, jwtHeader.getKid());

			var rsaSig = Signature.getInstance(GigyaservicesConstants.SHA256_RSA);
			rsaSig.initVerify(publicJWTKey);
			rsaSig.update(tokenData.getBytes(StandardCharsets.UTF_8));
			return rsaSig.verify(keySignature);

		}
		catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e)
		{
			LOG.error("Error validating ID Token.", e);
		}

		return false;
	}

	@Override
	public Boolean validateTokenUID(String jwtToken, String uid)
	{

		final String[] jwtString = jwtToken.split("\\.");
		final String encodedJwtPayload = jwtString[1];
		final GigyaJWTContent decodedPayload = decodeJWTContent(encodedJwtPayload);
		if (decodedPayload.getSub() != null && uid.equals(decodedPayload.getSub()))
		{
			return true;
		}
		LOG.error("ID Token does not contain the specified UID.");
		return false;
	}

	@Override
	public Boolean validateTokenPayloadHash(String jwtToken, String payloadHash)
	{

		final String[] jwtString = jwtToken.split("\\.");
		final String encodedJwtPayload = jwtString[1];
		final GigyaJWTContent decodedPayload = decodeJWTContent(encodedJwtPayload);
		if (decodedPayload.getSub() != null)
		{
			String encodedShaInJWT = decodedPayload.getSub();

			final var searchListKS = new String[] { "-", "_" };
			final var replacementListKS = new String[] { "+", "/" };
			encodedShaInJWT = StringUtils.replaceEach(encodedShaInJWT, searchListKS, replacementListKS);

			byte[] decodedShaInJWT = Base64.getDecoder().decode(encodedShaInJWT);
			final var hashInJWT = String.format("%040x", new BigInteger(1, decodedShaInJWT));
			if (hashInJWT.equals(payloadHash))
			{
				return true;
			}
		}
		LOG.error("ID Token does not match the payload hash.");
		return false;
	}

	protected GigyaJWTContent decodeJWTContent(String encodedJwtContent)
	{

		final var mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		byte[] decodedJWTHeader = Base64.getDecoder().decode(encodedJwtContent);
		try
		{
			return mapper.readValue(decodedJWTHeader, GigyaJWTContent.class);
		}
		catch (IOException e)
		{
			LOG.error("Error parsing ID Token.", e);
		}
		return null;
	}
	
	/**
	 * Retrieve the Public Key of the CDC Site, if stored previously.
	 * 
	 * @param keyId - decoded kid present in the JWT header 
	 * @return GigyaPublicKeyModel
	 */
	public GigyaPublicKeyModel findPublicKeybyKeyId(final String keyId)
	{
		final List<GigyaPublicKeyModel> gigyaPublicKeys = gigyaPublicKeyGenericDao.find(Collections.singletonMap(GigyaPublicKeyModel.KEYID, keyId));
		return CollectionUtils.isNotEmpty(gigyaPublicKeys) ? gigyaPublicKeys.get(0) : null;
	}

	private void callRawGigyaApiForPublicKey(final GigyaPublicKeyModel gigyaPublicKey, final GigyaConfigModel gigyaConfig)
	{

		final LinkedHashMap<String, Object> params = new LinkedHashMap<>();

		try
		{
			final GSObject data = gigyaService.callRawGigyaApiWithConfig("accounts.getJWTPublicKey", params,
			                gigyaConfig, GigyaservicesConstants.MAX_RETRIES, GigyaservicesConstants.TRY_NUM).getData();

			if (data.getInt(GigyaservicesConstants.STATUS_CODE) == HttpStatus.SC_OK && data.getInt(GigyaservicesConstants.ERROR_CODE) == 0)
			{
				gigyaPublicKey.setModulus(data.getString(GigyaservicesConstants.N));
				gigyaPublicKey.setExponent(data.getString(GigyaservicesConstants.E));
				gigyaPublicKey.setKeyId(data.getString(GigyaservicesConstants.KID));

				gigyaConfig.setGigyaPublicKey(gigyaPublicKey);
				modelService.saveAll(gigyaPublicKey, gigyaConfig);
			}
		}
		catch (GSKeyNotFoundException | InvalidClassException | GigyaApiException e)
		{
			LOG.error("Error fetching public key from Gigya", e);
		}
	}

	public void setGigyaService(GigyaService gigyaService)
	{
		this.gigyaService = gigyaService;
	}

	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	public void setGigyaPublicKeyGenericDao(GenericDao<GigyaPublicKeyModel> gigyaPublicKeyGenericDao)
	{
		this.gigyaPublicKeyGenericDao = gigyaPublicKeyGenericDao;
	}

}
