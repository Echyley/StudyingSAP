/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.login.impl;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.gigya.socialize.SigUtils;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.constants.GigyaservicesConstants;
import de.hybris.platform.gigya.gigyaservices.data.GigyaAccount;
import de.hybris.platform.gigya.gigyaservices.data.GigyaUserObject;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaUserManagementMode;
import de.hybris.platform.gigya.gigyaservices.keymanagement.GigyaKeyService;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaFieldMappingModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.gigya.gigyaservices.utils.GigyaUtils;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

/**
 * Default implementation of GigyaLoginService
 */
public class DefaultGigyaLoginService implements GigyaLoginService
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultGigyaLoginService.class);
	private static final int STATUS_CODE_200 = 200;

	private GigyaService gigyaService;

	private GenericDao<GigyaConfigModel> gigyaConfigGenericDao;

	private GenericDao<CustomerModel> gigyaUserGenericDao;

	private ModelService modelService;

	private GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao;

	private Converter<CustomerModel, GSObject> gigyaUserConverter;

	private GigyaKeyService gigyaKeyService;

	@Override
	public boolean verifyGigyaCall(final GigyaConfigModel gigyaConfig, final String uid, final String uidSignature,
	                final String signatureTimeStamp)
	{
		if (gigyaConfig != null)
		{
			if (StringUtils.isNotBlank(gigyaConfig.getGigyaSiteSecret()))
			{
				return verifyGigyaCallSiteSecret(uid, uidSignature, signatureTimeStamp,
				                gigyaConfig.getGigyaSiteSecret());
			}
			else if (StringUtils.isNotBlank(gigyaConfig.getGigyaPrivateKey()) || StringUtils.isNotBlank(gigyaConfig.getGigyaUserSecret()))
			{
				return verifyGigyaCallApiUser(uid, uidSignature, signatureTimeStamp, gigyaConfig);
			}
		}

		return false;
	}

	/**
	 * @deprecated (use verifyGigyaCallIdTokenExpiryAndSignature instead)
	 */
	@Override
	@Deprecated(since = "2211", forRemoval = true)
	public boolean verifyGigyaCallWithIdToken(GigyaConfigModel gigyaConfig, String idToken)
	{
		return verifyGigyaCallIdTokenExpiryAndSignature(gigyaConfig, idToken);
	}

	@Override
	public boolean verifyGigyaCallIdTokenExpiryAndSignature(GigyaConfigModel gigyaConfig, String idToken)
	{
		if (gigyaConfig != null && idToken != null)
		{
			return gigyaKeyService.validateTokenExpiryAndSignature(gigyaConfig, idToken);
		}

		return false;
	}

	@Override
	public boolean verifyGigyaIdTokenContainsUID(String idToken, String uid)
	{
		if (idToken != null && uid != null)
		{
			return gigyaKeyService.validateTokenUID(idToken, uid);
		}
		return false;
	}

	@Override
	public boolean verifyGigyaIdTokenContainsPayloadHash(String idToken, String payloadHash)
	{
		if (idToken != null && payloadHash != null)
		{
			return gigyaKeyService.validateTokenPayloadHash(idToken, payloadHash);
		}
		return false;
	}

	@Override
	public UserModel findCustomerByGigyaUid(final String uid)
	{
		final List<CustomerModel> gigyaUsers = getGigyaUserGenericDao()
		                .find(Collections.singletonMap(CustomerModel.GYUID, uid));
		return CollectionUtils.isNotEmpty(gigyaUsers) ? gigyaUsers.get(0) : null;
	}

	@Override
	public GigyaUserObject fetchGigyaInfo(final GigyaConfigModel gigyaConfig, final String uid)
	{
		try
		{
			if (gigyaConfig != null && gigyaConfig.getMode() != null
			                && gigyaConfig.getMode() == GigyaUserManagementMode.RAAS)
			{
				return fetchRaasAccount(gigyaConfig, uid);
			}
			else
			{
				return null;
			}
		}
		catch (final IOException e)
		{
			throw new GigyaApiException(e.getMessage(), e);
		}
	}

	protected GigyaUserObject fetchRaasAccount(final GigyaConfigModel gigyaConfig, final String guid)
	                throws IOException
	{
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

		final Map<String, Object> params = new LinkedHashMap<>();
		params.put("UID", guid);
		params.put("extraProfileFields", Config.getString("gigya.extra.fields",
		                "languages,address,phones,education,honors,publications,patents,certifications,professionalHeadline,bio,industry,specialties,work,skills,religion,politicalView,interestedIn,relationshipStatus,hometown,favorites,followersCount,followingCount,username,locale,verified,timezone,likes"));
		params.put("include",
		                Config.getString("gigya.basic.include", "loginIDs,emails,profile,data,preferences,groups"));

		// Add environment details to getAccountInfo call
		params.put("environment", GigyaUtils.getEnvironmentDetails());
		final GSObject gigyaParams = convertMapToGsObject(mapper, params);
		final GSResponse res = gigyaService.callRawGigyaApiWithConfigAndObject("accounts.getAccountInfo", gigyaParams,
		                gigyaConfig, GigyaservicesConstants.MAX_RETRIES, GigyaservicesConstants.TRY_NUM);

		return prepareAndGetUserObject(mapper, res);
	}

	protected GigyaUserObject prepareAndGetUserObject(final ObjectMapper mapper, final GSResponse res)
	                throws IOException
	{
		final GigyaAccount accObj = mapper.readValue(res.getData().toJsonString(), GigyaAccount.class);
		final GigyaUserObject usrObj = accObj.getProfile();
		usrObj.setLoginIDs(accObj.getLoginIDs());
		usrObj.setUID(accObj.getUID());
		usrObj.setPreferences(accObj.getPreferences());
		usrObj.setDataCenter(accObj.getDataCenter());
		return usrObj;
	}

	private GSObject convertMapToGsObject(final ObjectMapper mapper, final Map<String, Object> parms)
	{
		GSObject gigyaParams = new GSObject();
		try
		{
			gigyaParams = new GSObject(mapper.writeValueAsString(parms));
		}
		catch (final Exception e)
		{
			final String msg = "Error creating gigya request parmeters";
			LOG.error(msg, e);
			throw new GigyaApiException(msg);
		}
		return gigyaParams;
	}

	@Override
	public void notifyGigyaOfLogout(final GigyaConfigModel gigyaConfig, final String uid)
	{
		if (gigyaConfig != null)
		{
			final LinkedHashMap<String, Object> params = new LinkedHashMap<>();
			params.put("UID", uid);

			gigyaService.callRawGigyaApiWithConfig("accounts.logout", params, gigyaConfig,
			                GigyaservicesConstants.MAX_RETRIES, GigyaservicesConstants.TRY_NUM);
		}
	}

	protected boolean verifyGigyaCallSiteSecret(final String uid, final String signature,
	                final String signatureTimeStamp, final String siteSecret)
	{
		try
		{
			return SigUtils.validateUserSignature(uid, signatureTimeStamp, siteSecret, signature);
		}
		catch (InvalidKeyException | UnsupportedEncodingException e)
		{
			LOG.error("Error validateing gigya UID " + e.getMessage(), e);
			return false;
		}
	}

	protected boolean verifyGigyaCallApiUser(final String uid, final String sig, final String timestamp,
	                final GigyaConfigModel gigyaConfig)
	{
		final LinkedHashMap<String, Object> params = new LinkedHashMap<>();
		params.put("UID", uid);
		params.put("UIDSignature", sig);
		params.put("signatureTimestamp", timestamp);
		params.put("userKey", gigyaConfig.getGigyaUserKey());
		try
		{
			final GSObject data = gigyaService.callRawGigyaApiWithConfig("accounts.exchangeUIDSignature", params,
			                gigyaConfig, GigyaservicesConstants.MAX_RETRIES, GigyaservicesConstants.TRY_NUM).getData();
			if (data.getInt("statusCode") == STATUS_CODE_200 && data.getInt("errorCode") == 0)
			{
				return true;
			}
		}
		catch (GSKeyNotFoundException | InvalidClassException | GigyaApiException e)
		{
			LOG.error("Error verifying call with gigya", e);
		}
		return false;
	}

	@Override
	public boolean sendUserToGigya(final UserModel userModel)
	{
		boolean success = false;
		if (userModel instanceof CustomerModel)
		{

			final CustomerModel gigyaUser = (CustomerModel)userModel;
			final List<GigyaConfigModel> gigyaConfigs = gigyaConfigGenericDao
			                .find(Collections.singletonMap(GigyaConfigModel.GIGYAAPIKEY, gigyaUser.getGyApiKey()));
			final GigyaConfigModel gigyaConfig = CollectionUtils.isNotEmpty(gigyaConfigs) ? gigyaConfigs.get(0) : null;

			if (gigyaConfig != null)
			{
				success = sendDataToGigya(gigyaUser, gigyaConfig);
			}
		}
		return success;
	}

	/**
	 * Sends data to gigya
	 *
	 * @param gigyaUser
	 * @param config
	 * @return boolean, if successfully sent to gigya
	 */
	private boolean sendDataToGigya(final CustomerModel gigyaUser, final GigyaConfigModel config)
	{
		try
		{
			final GSObject gigyaUserConverted = gigyaUserConverter.convert(gigyaUser, new GSObject());
			final GSResponse gsResponse = gigyaService.callRawGigyaApiWithConfigAndObject("accounts.setAccountInfo",
			                gigyaUserConverted, config, GigyaservicesConstants.MAX_RETRIES, GigyaservicesConstants.TRY_NUM);
			if (gsResponse.getErrorCode() == 0)
			{
				LOG.debug("User successfully sent to gigya.");
				return true;
			}
		}
		catch (final GigyaApiException e)
		{
			LOG.error("Error while updating profile information to gigya ", e);
		}
		return false;
	}

	public GigyaService getGigyaService()
	{
		return gigyaService;
	}

	@Required
	public void setGigyaService(final GigyaService gigyaService)
	{
		this.gigyaService = gigyaService;
	}

	public GenericDao<GigyaConfigModel> getGigyaConfigGenericDao()
	{
		return gigyaConfigGenericDao;
	}

	@Required
	public void setGigyaConfigGenericDao(final GenericDao<GigyaConfigModel> gigyaConfigGenericDao)
	{
		this.gigyaConfigGenericDao = gigyaConfigGenericDao;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public GenericDao<GigyaFieldMappingModel> getGigyaFieldMappingGenericDao()
	{
		return gigyaFieldMappingGenericDao;
	}

	@Required
	public void setGigyaFieldMappingGenericDao(final GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao)
	{
		this.gigyaFieldMappingGenericDao = gigyaFieldMappingGenericDao;
	}

	public GenericDao<CustomerModel> getGigyaUserGenericDao()
	{
		return gigyaUserGenericDao;
	}

	public Converter<CustomerModel, GSObject> getGigyaUserConverter()
	{
		return gigyaUserConverter;
	}

	@Required
	public void setGigyaUserGenericDao(final GenericDao<CustomerModel> gigyaUserGenericDao)
	{
		this.gigyaUserGenericDao = gigyaUserGenericDao;
	}

	@Required
	public void setGigyaUserConverter(final Converter<CustomerModel, GSObject> gigyaUserConverter)
	{
		this.gigyaUserConverter = gigyaUserConverter;
	}

	public void setGigyaKeyService(final GigyaKeyService gigyaKeyService)
	{
		this.gigyaKeyService = gigyaKeyService;
	}

}
