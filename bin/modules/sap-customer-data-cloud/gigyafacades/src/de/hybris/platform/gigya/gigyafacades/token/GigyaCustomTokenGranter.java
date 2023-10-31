/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyafacades.token;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyafacades.constants.GigyafacadesConstants;
import de.hybris.platform.gigya.gigyafacades.login.GigyaLoginFacade;
import de.hybris.platform.gigya.gigyaservices.data.GigyaJsOnLoginInfo;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.site.BaseSiteService;

/**
 * A custom token granter class which gets invoked by the oauth server to
 * replicate customer from CDC to commerce and return a valid token for such
 * customers
 *
 */
public class GigyaCustomTokenGranter extends AbstractTokenGranter
{

	private GigyaLoginService gigyaLoginService;

	private GigyaLoginFacade gigyaLoginFacade;

	private BaseSiteService baseSiteService;

	private UserDetailsService userDetailsService;

	protected GigyaCustomTokenGranter(final AuthorizationServerTokenServices tokenServices,
	                final ClientDetailsService clientDetailsService, final OAuth2RequestFactory requestFactory,
	                final GigyaLoginService gigyaLoginService, final GigyaLoginFacade gigyaLoginFacade,
	                final BaseSiteService baseSiteService, final UserDetailsService userDetailsService)
	{
		super(tokenServices, clientDetailsService, requestFactory, GigyafacadesConstants.GRANT_TYPE);
		this.gigyaLoginService = gigyaLoginService;
		this.gigyaLoginFacade = gigyaLoginFacade;
		this.baseSiteService = baseSiteService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(final ClientDetails client,
	                final TokenRequest tokenRequest)
	{

		final GigyaJsOnLoginInfo jsInfo = initializeGigyaJsInfoObject(tokenRequest.getRequestParameters());
		final String uid = jsInfo.getUID();
		final String baseSite = jsInfo.getBaseSite();
		final BaseSiteModel currentBaseSite = configureBaseSiteInSession(baseSite);

		if (currentBaseSite != null && currentBaseSite.getGigyaConfig() != null
		                && gigyaLoginFacade.processGigyaLogin(jsInfo, currentBaseSite.getGigyaConfig()))
		{
			final UserModel user = gigyaLoginService.findCustomerByGigyaUid(uid);
			final UserDetails loadedUser = userDetailsService.loadUserByUsername(user.getUid());

			final Authentication userAuth = new UsernamePasswordAuthenticationToken(user.getUid(), null,
			                loadedUser.getAuthorities());
			final OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);

			return new OAuth2Authentication(storedOAuth2Request, userAuth);
		}
		else
		{
			throw new InvalidRequestException("Invalid request received");
		}
	}

	private BaseSiteModel configureBaseSiteInSession(final String baseSite)
	{
		final BaseSiteModel currentBaseSite = baseSiteService.getBaseSiteForUID(baseSite);
		baseSiteService.setCurrentBaseSite(currentBaseSite, false);
		return currentBaseSite;
	}

	private GigyaJsOnLoginInfo initializeGigyaJsInfoObject(final Map<String, String> parameters)
	{

		final var mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		GigyaJsOnLoginInfo jsInfo = null;

		try
		{
			jsInfo = mapper.convertValue(parameters, GigyaJsOnLoginInfo.class);
			// Decode the parameters
			if (jsInfo.getUID() != null && StringUtils.isNotBlank(jsInfo.getUID()))
			{
				jsInfo.setUID(URLDecoder.decode(jsInfo.getUID(), GigyafacadesConstants.UTF8));
			}
			if (jsInfo.getUIDSignature() != null && StringUtils.isNotBlank(jsInfo.getUIDSignature()))
			{
				jsInfo.setUIDSignature(URLDecoder.decode(jsInfo.getUIDSignature(), GigyafacadesConstants.UTF8));
			}
			if (jsInfo.getSignatureTimestamp() != null && StringUtils.isNotBlank(jsInfo.getSignatureTimestamp()))
			{
				jsInfo.setSignatureTimestamp(URLDecoder.decode(jsInfo.getSignatureTimestamp(), GigyafacadesConstants.UTF8));
			}
			if (jsInfo.getIdToken() != null && StringUtils.isNotBlank(jsInfo.getIdToken()))
			{
				jsInfo.setIdToken(URLDecoder.decode(jsInfo.getIdToken(), GigyafacadesConstants.UTF8));
			}
		}
		catch (UnsupportedEncodingException | IllegalArgumentException e)
		{
			throw new BadRequestException("Invalid request received");
		}

		return jsInfo;
	}

}