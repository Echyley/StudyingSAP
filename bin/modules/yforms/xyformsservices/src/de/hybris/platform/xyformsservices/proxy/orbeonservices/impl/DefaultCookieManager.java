/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsservices.proxy.orbeonservices.impl;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.xyformsservices.proxy.orbeonservices.CookieManager;
import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;


/**
 * Orbeon specific implementation for {@link CookieManager}
 * <p>
 * Since the class has session scope, only one instance of this class is attached to a client.
 */
public class DefaultCookieManager implements CookieManager
{
	private static final Logger LOG = Logger.getLogger(DefaultCookieManager.class);
	private static final String ORBEON_COOKIE_STORE = "orbeonCookieStore";
	private static final String ORBEON_CACHED_CURRENT_UID = "orbeonCachedCurrentUid";
	
	private SessionService sessionService;
	private UserService userService;

	@Override
	public void processRequest(final HttpServletRequest request, final HttpURLConnection conn, final String url) throws URISyntaxException
	{
		final Session session = getSessionService().getCurrentSession();
		final CookieStore cookieStore = getOrbeonCookieStore();
		final UserModel currentUser = Optional.ofNullable(userService.getCurrentUser()).orElseGet(() -> userService.getAnonymousUser());
		if (!currentUser.getUid().equals(session.getAttribute(ORBEON_CACHED_CURRENT_UID))) {
			cookieStore.clear();
			session.setAttribute(ORBEON_CACHED_CURRENT_UID, currentUser.getUid());
		}
		this.processRequest(conn, url);
	}

	@Override
	public void processRequest(final HttpURLConnection conn, final String url) throws URISyntaxException
	{
		final CookieStore cookieStore =  getOrbeonCookieStore();

		final BrowserCompatSpec cookieSpec = new BrowserCompatSpec(); // because not thread-safe
		final CookieOrigin cookieOrigin = getCookieOrigin(url);
		cookieStore.clearExpired(new Date());

		final List<Cookie> relevantCookies = new ArrayList<>();
		for (final Cookie cookie : cookieStore.getCookies())
		{
			if (cookieSpec.match(cookie, cookieOrigin))
			{
				relevantCookies.add(cookie);
			}
		}

		// NOTE: BrowserCompatSpec always only return a single Cookie header
		if (!relevantCookies.isEmpty())
		{
			final List<Header> headers = cookieSpec.formatCookies(relevantCookies);
			for (final Header h : headers)
			{
				LOG.debug("Cookie:[" + h.getName() + "][" + h.getValue() + "]");
				conn.setRequestProperty(h.getName(), h.getValue());
			}
		}
	}

	@Override
	public void processResponse(final HttpURLConnection conn, final String url) throws URISyntaxException,
			MalformedCookieException
	{
		final BrowserCompatSpec cookieSpec = new BrowserCompatSpec(); // because not thread-safe
		final CookieOrigin cookieOrigin = getCookieOrigin(url);
		final CookieStore cookieStore = getOrbeonCookieStore();

		for (final Map.Entry<String, List<String>> e : conn.getHeaderFields().entrySet())
		{
			final String name = e.getKey();
			if (name != null && "set-cookie".equals(name.toLowerCase(Locale.ENGLISH)))
			{
				final List<String> value = e.getValue();
				this.setCookie(value, name, cookieSpec, cookieOrigin, cookieStore);
			}
		}
	}

	private void setCookie(final List<String> value, final String name, final BrowserCompatSpec cookieSpec, final CookieOrigin cookieOrigin, final CookieStore cookieStore) throws MalformedCookieException
	{
		for (final String s : value)
		{
			LOG.debug("Setting Cookie:[" + name + "][" + s + "]");
			final List<Cookie> cookies = cookieSpec.parse(new BasicHeader(name, s), cookieOrigin);
			for (final Cookie c : cookies)
			{
				cookieStore.addCookie(c);
			}
		}
	}

	public CookieOrigin getCookieOrigin(final String url) throws URISyntaxException
	{
		final URI uri = new URI(url);
		final int defaultPort = "https".equals(uri.getScheme()) ? 443 : 80;
		final int effectivePort = uri.getPort() < 0 ? defaultPort : uri.getPort();
		return new CookieOrigin(uri.getHost(), effectivePort, uri.getPath(), "https".equals(uri.getScheme()));
	}

	private CookieStore getOrbeonCookieStore()
	{
		final Session session = getSessionService().getCurrentSession();
		CookieStore cookieStore = session.getAttribute(ORBEON_COOKIE_STORE);
		if (cookieStore == null)
		{
			cookieStore = new BasicCookieStore();
			session.setAttribute(ORBEON_COOKIE_STORE, cookieStore);
		}
		return cookieStore;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
