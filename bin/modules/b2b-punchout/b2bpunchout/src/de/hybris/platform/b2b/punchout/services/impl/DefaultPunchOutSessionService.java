/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.punchout.Organization;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.PunchOutSessionExpired;
import de.hybris.platform.b2b.punchout.PunchOutSessionNotFoundException;
import de.hybris.platform.b2b.punchout.model.StoredPunchOutSessionModel;
import de.hybris.platform.b2b.punchout.populators.impl.DefaultPunchOutSessionPopulator;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.oauth2.services.impl.DefaultHybrisOpenIDTokenServices;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.webservicescommons.oauth2.client.impl.DefaultClientDetailsService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.cxml.CXML;
import org.cxml.Credential;
import org.cxml.Identity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;


/**
 * Default implementation of {@link PunchOutSessionService} based on the use of {@link SessionService}.
 */
public class DefaultPunchOutSessionService implements PunchOutSessionService
{
	protected static final String PUNCHOUT_SESSION_KEY = "punchoutSession";
	protected static final String PUNCHOUT_SESSION_ID = "punchoutSessionID";
	protected static final String RANDOM_ALGORITHM = "SHA1PRNG";
	protected static final String MESSAGEDIGEST_ALGORITHM = "SHA-256";
	protected static final int SESSION_ID_BYTES = 16;
	protected static final String IS_PUNCHOUT_SESSION_CART_VAILD = "isPunchoutSessionCartVaild";
	protected static final String PUNCHOUT_OAUTH_CLIENTID = "b2bpunchout.oauthclient.clientId";
	protected static final String PUNCHOUT_OAUTH_ROLE = "ROLE_PUNCHOUTOAUTH2";

	private SessionService sessionService;
	private ConfigurationService configurationService;
	private FlexibleSearchService flexibleSearchService;
	private CartService cartService;
	private ModelService modelService;
	private Populator<CXML, PunchOutSession> punchOutSessionPopulator;
	private PunchOutCredentialService punchOutCredentialService;
	private DefaultHybrisOpenIDTokenServices oauthTokenServices;
	private DefaultClientDetailsService oauthClientDetails;
	private DefaultOAuth2RequestFactory oAuth2RequestFactory;

	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;

	public void initAndActivatePunchOutSession(final CXML input)
	{
		final PunchOutSession punchoutSession = new PunchOutSession();
		//java.util.Date required by punchoutSession
		punchoutSession.setTime(new Date());
		getPunchOutSessionPopulator().populate(input, punchoutSession);
		activate(punchoutSession);
	}

	@Override
	public void activate(final PunchOutSession punchoutSession)
	{
		getSessionService().setAttribute(PUNCHOUT_SESSION_KEY, punchoutSession);
	}

	@Override
	public void saveCurrentPunchoutSession()
	{
		final String punchoutSessionId = generatePunchoutSessionId();
		getSessionService().getCurrentSession().setAttribute(PUNCHOUT_SESSION_ID, punchoutSessionId);
		final PunchOutSession punchOutSession = getCurrentPunchOutSession();
		final CartModel cart = getCartService().getSessionCart();

		final String customerId = retrieveUserId(punchOutSession);
		final B2BCustomerModel b2bcustomer = getB2bCustomerService().getUserForUID(customerId);
		cart.setUser(b2bcustomer);
		getModelService().save(cart);
		final StoredPunchOutSessionModel storedSession = getModelService().create(StoredPunchOutSessionModel.class);
		storedSession.setSid(punchoutSessionId);
		storedSession.setCart(cart);
		storedSession.setPunchOutSession(punchOutSession);

		getModelService().save(storedSession);
	}

	protected String generatePunchoutSessionId()
	{
		try
		{
			byte[] bytes = new byte[SESSION_ID_BYTES];
			final SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);
			long seed = System.currentTimeMillis();
			seed ^= Runtime.getRuntime().freeMemory();
			random.setSeed(seed);
			random.nextBytes(bytes);
			bytes = MessageDigest.getInstance(MESSAGEDIGEST_ALGORITHM).digest(bytes);
			final Encoder encoder = Base64.getUrlEncoder().withoutPadding();
			return encoder.encodeToString(bytes);
		}
		catch(final NoSuchAlgorithmException e)
		{
			throw new SystemException(e.toString(), e);
		}
	}

	@Override
	public StoredPunchOutSessionModel loadStoredPunchOutSessionModel(final String punchoutSessionId)
	{
		StoredPunchOutSessionModel storedSession = null;
		final SearchResult<StoredPunchOutSessionModel> result = getFlexibleSearchService().search(
				"SELECT {pk} FROM {StoredPunchOutSession} WHERE {sid} = ?sid", Collections.singletonMap("sid", punchoutSessionId));
		if (result != null && result.getCount() == 1)
		{
			storedSession = result.getResult().get(0);
		}
		return storedSession;
	}

	@Override
	public PunchOutSession loadPunchOutSession(final String punchoutSessionId)
	{
		final StoredPunchOutSessionModel storedSession = loadStoredPunchOutSessionModel(punchoutSessionId);
		if (storedSession == null)
		{
			throw new PunchOutSessionNotFoundException("Session not found");
		}

		final PunchOutSession punchoutSession = (PunchOutSession) storedSession.getPunchOutSession();
		checkAndActivatePunchOutSession(punchoutSession);
		return punchoutSession;
	}

	@Override
	public void checkAndActivatePunchOutSession(final PunchOutSession punchoutSession)
	{
		if (isPunchOutSessionExpired(punchoutSession)) {
			throw new PunchOutSessionExpired("PunchOut session has expired");
		}
		getSessionService().getCurrentSession().setAttribute(PUNCHOUT_SESSION_KEY, punchoutSession);
	}

	@Override
	public boolean isPunchOutSessionExpired(final PunchOutSession punchoutSession) {
		if (punchoutSession == null)
		{
			throw new PunchOutSessionNotFoundException("PunchOut session not found");
		}
		return new Date().after(calculateCutOutTime(punchoutSession.getTime()));
	}

	@Override
	public void setCurrentCartFromPunchOutSetup(final String punchoutSessionId)
	{
		final StoredPunchOutSessionModel storedSession = loadStoredPunchOutSessionModel(punchoutSessionId);
		if (storedSession == null)
		{
			throw new PunchOutSessionNotFoundException("Session not found");
		}

		final CartModel cart = storedSession.getCart();
		if (cart == null)
		{
			throw new PunchOutSessionNotFoundException("Cart could not be retrieved.");
		}
		// set punchout cart (created while punchout setup request) into current session
		getCartService().setSessionCart(cart);
	}

	public Boolean isPunchOutSessionCartValid()
	{
		return sessionService.getAttribute(IS_PUNCHOUT_SESSION_CART_VAILD);
	}

	public void setPunchOutSessionCartIsValid(Boolean cartIsValid)
	{
		 sessionService.setAttribute(IS_PUNCHOUT_SESSION_CART_VAILD, cartIsValid);
	}

	@Override
	public String retrieveUserId(final PunchOutSession punchoutSession) throws PunchOutException
	{
		return loadB2BCustomerModel(punchoutSession).getUid();
	}

	@Override
	public B2BCustomerModel loadB2BCustomerModel(final PunchOutSession punchoutSession) throws PunchOutException
	{
		if (punchoutSession == null) {
			throw new PunchOutException(HttpStatus.INTERNAL_SERVER_ERROR, "PunchOut session is null");
		}
		final List<Organization> organizationList = punchoutSession.getInitiatedBy();
		final B2BCustomerModel customer = getCustomerFromOrganizations(organizationList);
		if (customer == null) {
			throw new PunchOutException(HttpStatus.INTERNAL_SERVER_ERROR, getNonexistingCustomerMessage(organizationList));
		}
		return customer;
	}

	@Override
	public OAuth2AccessToken getPunchOutTokenByUidAndSid(final String uid, final String sid)
	{
		final BaseClientDetails punchoutClient = (BaseClientDetails) getOauthClientDetails().loadClientByClientId(
				configurationService.getConfiguration().getString(PUNCHOUT_OAUTH_CLIENTID));
		final TokenRequest tokenRequest = getoAuth2RequestFactory().createTokenRequest(Collections.emptyMap(), punchoutClient);
		final Set<String> scopeSetTemp = new HashSet<>(tokenRequest.getScope());
		scopeSetTemp.add(sid);
		tokenRequest.setScope(scopeSetTemp);
		final Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();

		final List<GrantedAuthority> grantedAuthorities = new ArrayList<>(oldAuthentication.getAuthorities()) ;
		grantedAuthorities.add(new SimpleGrantedAuthority(PUNCHOUT_OAUTH_ROLE));
		final OAuth2Authentication authentication = new OAuth2Authentication(tokenRequest.createOAuth2Request(punchoutClient),
				new UsernamePasswordAuthenticationToken(uid, null, grantedAuthorities));
		return getOauthTokenServices().createAccessToken(authentication);
	}

	protected B2BCustomerModel getCustomerFromOrganizations(final List<Organization> organizationList)
	{
		B2BCustomerModel customer = null;

		for (int i = 0; i < organizationList.size(); i++)
		{
			final Credential credential = new Credential();
			credential.setDomain(organizationList.get(i).getDomain());
			final Identity identity = new Identity();
			identity.getContent().add(organizationList.get(i).getIdentity());
			credential.setIdentity(identity);

			customer = getPunchOutCredentialService().getCustomerForCredentialNoAuth(credential);

			if (customer != null)
			{
				break;
			}
		}
		return customer;
	}

	protected String getNonexistingCustomerMessage(final List<Organization> organizationList)
	{
		final StringBuilder sb = new StringBuilder("Unable to find customer for given credentials [");
		for (int i = 0; i < organizationList.size(); i++)
		{
			sb.append("[" + organizationList.get(i).getDomain() + "," + organizationList.get(i).getIdentity() + "]");
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * @param sessionCreationDate the creating time of the punchout session
	 * @return the time the session should have expired
	 */
	protected Date calculateCutOutTime(final Date sessionCreationDate)
	{
		final int timeoutDuration = 5;
		return DateUtils.addMilliseconds(sessionCreationDate,
				getConfigurationService().getConfiguration().getInteger("b2bpunchout.timeout", Integer.valueOf(timeoutDuration)));
	}

	@Override
	public String getCurrentPunchOutSessionId()
	{
		return getSessionService().getCurrentSession().getAttribute(PUNCHOUT_SESSION_ID);
	}

	@Override
	public PunchOutSession getCurrentPunchOutSession()
	{
		return getSessionService().getCurrentSession().getAttribute(PUNCHOUT_SESSION_KEY);
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected Populator<CXML, PunchOutSession> getPunchOutSessionPopulator()
	{
		return punchOutSessionPopulator;
	}

	public void setPunchOutSessionPopulator(final DefaultPunchOutSessionPopulator punchOutSessionPoulator)
	{
		this.punchOutSessionPopulator = punchOutSessionPoulator;
	}


	protected PunchOutCredentialService getPunchOutCredentialService()
	{
		return punchOutCredentialService;
	}

	public void setPunchOutCredentialService(final PunchOutCredentialService punchOutCredentialService)
	{
		this.punchOutCredentialService = punchOutCredentialService;
	}

	protected DefaultHybrisOpenIDTokenServices getOauthTokenServices()
	{
		return oauthTokenServices;
	}

	public void setOauthTokenServices(final DefaultHybrisOpenIDTokenServices oauthTokenServices)
	{
		this.oauthTokenServices = oauthTokenServices;
	}

	protected DefaultClientDetailsService getOauthClientDetails()
	{
		return oauthClientDetails;
	}

	public void setOauthClientDetails(final DefaultClientDetailsService oauthClientDetails)
	{
		this.oauthClientDetails = oauthClientDetails;
	}

	protected DefaultOAuth2RequestFactory getoAuth2RequestFactory()
	{
		return oAuth2RequestFactory;
	}

	public void setoAuth2RequestFactory(final DefaultOAuth2RequestFactory oAuth2RequestFactory)
	{
		this.oAuth2RequestFactory = oAuth2RequestFactory;
	}

	protected B2BCustomerService<B2BCustomerModel, B2BUnitModel> getB2bCustomerService() {
		return b2bCustomerService;
	}

	public void setB2bCustomerService(B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService) {
		this.b2bCustomerService = b2bCustomerService;
	}
}
