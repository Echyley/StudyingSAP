/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bservices.auth.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigya.socialize.GSArray;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.gigya.gigyab2bservices.auth.GigyaAuthService;
import de.hybris.platform.gigya.gigyab2bservices.constants.Gigyab2bservicesConstants;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaAuthAllowedAssetsData;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.constants.GigyaservicesConstants;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.gigya.gigyaservices.utils.GigyaUtils;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.site.BaseSiteService;

/**
 * Default implementation of GigyaAuthService which calls the SAP CDC's
 * accounts.b2b.auth.getAssets API to fetch Authorizations for a Customer belonging to an
 * organization
 */
public class GigyaAssetsAuthService implements GigyaAuthService
{

	private static final Logger LOG = LoggerFactory.getLogger(GigyaAssetsAuthService.class);
	private static final List<String> USER_AUTHORIZATIONS = Arrays.asList("b2bcustomergroup", "b2bmanagergroup",
	                "b2bapprovergroup", "b2badmingroup");

	private BaseSiteService baseSiteService;

	private UserService userService;

	private GigyaService gigyaService;

	@Override
	public void assignAuthorisationsToCustomer(final CustomerModel customer)
	{
		final BaseSiteModel baseSite = getBaseSiteService().getCurrentBaseSite();
		final GigyaConfigModel gigyaConfig = baseSite.getGigyaConfig();

		final var mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

		try
		{
			final B2BCustomerModel b2bCustomer = (B2BCustomerModel)customer;
			final var gsResponse = getAssets(gigyaConfig, mapper, b2bCustomer);
			final GSArray gsArray = gsResponse != null && gsResponse.hasData() ? (GSArray)gsResponse.getData().get(Gigyab2bservicesConstants.ALLOWED_ASSETS_ATTR) : null;

			final List<GigyaAuthAllowedAssetsData> gigyaAllowedAssetsList = gsArray != null
			                ? mapper.readValue(gsArray.toJsonString(),
			                                mapper.getTypeFactory().constructCollectionType(List.class, GigyaAuthAllowedAssetsData.class))
			                : null;
			if (gigyaAllowedAssetsList != null)
			{
				customer.setGroups(getCustomerUpdatedGroupsFromGetAssetsResponse(gigyaAllowedAssetsList, customer));
			}
		}
		catch (final GSKeyNotFoundException e)
		{
			LOG.error("Error invoking from SAP CDC getAssets API ", e);
		}
		catch (final JsonParseException e)
		{
			LOG.error("Error parsing the response from SAP CDC getAssets API ", e);
		}
		catch (final JsonMappingException e)
		{
			LOG.error("Error mapping asset data from SAP CDC ", e);
		}
		catch (final IOException e)
		{
			LOG.error("IOException while invoking from SAP CDC getAssets API ", e);
		}
		catch (final ClassCastException e)
		{
			LOG.error("ClassCastException invoking from SAP CDC getAssets API ", e);
		}
	}

	protected GSResponse getAssets(final GigyaConfigModel gigyaConfig, final ObjectMapper mapper,
	                final B2BCustomerModel b2bCustomer)
	{
		try
		{
			final Map<String, Object> params = new LinkedHashMap<>();
			params.put("UID", b2bCustomer.getGyUID());
			params.put("bpid", b2bCustomer.getDefaultB2BUnit().getUid());
			params.put("appid", gigyaConfig.getApplicationClientId());
			// Add environment details to getAccountInfo call
			params.put("environment", GigyaUtils.getEnvironmentDetails());

			final var gigyaParams = convertMapToGsObject(mapper, params);

			return getGigyaService().callRawGigyaApiWithConfigAndObject("accounts.b2b.auth.getAssets",
			                gigyaParams, gigyaConfig, GigyaservicesConstants.MAX_RETRIES, GigyaservicesConstants.TRY_NUM);
		}
		catch (final GigyaApiException e)
		{
			LOG.error("Error while invoking CDC B2B getAssets API for the user {} ", b2bCustomer.getUid(), e);
		}
		return null;
	}

	private Set<PrincipalGroupModel> getCustomerUpdatedGroupsFromGetAssetsResponse(final List<GigyaAuthAllowedAssetsData> gigyaAssetsList,
	                final CustomerModel customer)
	{
		final Set<PrincipalGroupModel> groups = new HashSet<>(customer.getGroups());
		gigyaAssetsList.forEach(assetItem -> {
			if ((Gigyab2bservicesConstants.COMMERCE_ASSET_TYPE).equals(assetItem.getType()) && assetItem.getAttributes().containsKey(Gigyab2bservicesConstants.ROLE_NAME_ATTR))
			{
				final List<Object> roles = assetItem.getAttributes().get(Gigyab2bservicesConstants.ROLE_NAME_ATTR);
				roles.forEach(role -> {
					try
					{
						final UserGroupModel userGroup = getUserService().getUserGroupForUID((String)role);
						groups.add(userGroup);
					}
					catch (final IllegalArgumentException | UnknownIdentifierException e)
					{
						LOG.error("No such usergroup {} ", role, e);
					}
				});
			}
		});
		return groups;
	}

	protected GSObject convertMapToGsObject(final ObjectMapper mapper, final Map<String, Object> parms)
	{
		var gigyaParams = new GSObject();
		try
		{
			gigyaParams = new GSObject(mapper.writeValueAsString(parms));
		}
		catch (final Exception e)
		{
			final var msg = "Error creating gigya request parmeters";
			LOG.error(msg, e);
			throw new GigyaApiException(msg);
		}
		return gigyaParams;
	}

	@Override
	public void removeAuthorisationsOfCustomer(final CustomerModel customer)
	{
		ServicesUtil.validateParameterNotNull(customer, "Customer cannot be null.");

		final Set<PrincipalGroupModel> groups = customer.getGroups().stream()
		                .filter(item -> !USER_AUTHORIZATIONS.contains(item.getUid())).collect(Collectors.toSet());
		customer.setGroups(groups);
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public GigyaService getGigyaService()
	{
		return gigyaService;
	}

	public void setGigyaService(GigyaService gigyaService)
	{
		this.gigyaService = gigyaService;
	}
}
