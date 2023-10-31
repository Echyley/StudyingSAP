/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.samlsinglesignon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@IntegrationTest
public class DefaultSamlLoginServiceTest extends ServicelayerBaseTest
{
	private static final String RETURN_TO = "returnTo";
	private static final String SSO_DOMAIN_WHITELIST_PARAM = "sso.return.url.domain.whitelist";
	private static final String SSO_RETURN_PARAM = "sso.return.url.param.name";
	private static final String SSO_DEFAULT_COOKIE_NAME = "samlPassThroughToken";
	private static final String SSO_COOKIE_NAME = "sso.cookie.name";
	private static final String SAP_URL = "http://www.sap.com";

	@Resource
	private final DefaultSamlLoginService samlLoginService = new DefaultSamlLoginService();

	private final PropertyConfigSwitcher whitelistParam = new PropertyConfigSwitcher(SSO_DOMAIN_WHITELIST_PARAM);
	private final PropertyConfigSwitcher returnToParam = new PropertyConfigSwitcher(SSO_RETURN_PARAM);
	private final PropertyConfigSwitcher ssoCookieParam = new PropertyConfigSwitcher(SSO_COOKIE_NAME);

	@Resource
	private ModelService modelService;

	@Before
	public void setUp()
	{
		returnToParam.switchToValue(RETURN_TO);
		ssoCookieParam.switchToValue(SSO_DEFAULT_COOKIE_NAME);
	}

	@After
	public void tearDown()
	{
		returnToParam.switchBackToDefault();
		ssoCookieParam.switchBackToDefault();
		whitelistParam.switchBackToDefault();
	}

	@Test
	public void shouldNotBeEmptyRedirectionUrlSameHost()
	{
		final String testRedirectionUrl = "http://localhost:9002/monitoring/cache";
		assertThat(samlLoginService.getRedirectionUrl(getSimpleRequest(getFormattedUrl(testRedirectionUrl))))
				.isNotEmpty()
				.isEqualTo(Optional.of(testRedirectionUrl));
	}

	@Test
	public void shouldBeEmptyRedirectionUrlDifferentHost()
	{
		whitelistParam.switchToValue("domain.com");
		assertThat(samlLoginService.getRedirectionUrl(getSimpleRequest(getFormattedUrl(SAP_URL))))
				.isEmpty();
	}

	@Test
	public void shouldBeEmptyRedirectionUrlMalformedRedirection()
	{
		final String redirect = "http://localhost?  " + RETURN_TO + "= " + SAP_URL;
		assertThat(samlLoginService.getRedirectionUrl(getSimpleRequest(redirect)))
				.isEmpty();
	}

	@Test
	public void shouldBeEmptyRedirectionUrlMalformedRequestHost()
	{
		final String requestUrl = "http://:" + SAP_URL + "/";

		assertThat(samlLoginService.getRedirectionUrl(getComplexRequest(requestUrl, getFormattedUrl(SAP_URL), true)))
				.isEmpty();
	}

	@Test
	public void shouldNotBeEmptyRedirectionUrlDifferentHostWhiteList()
	{
		whitelistParam.switchToValue("domain.com,sap.com");
		assertThat(samlLoginService.getRedirectionUrl(getSimpleRequest(getFormattedUrl(SAP_URL))))
				.isNotEmpty()
				.isEqualTo(Optional.of(SAP_URL));
	}

	@Test
	public void shouldNotBeEmptyRedirectionUrlLocalhostWhiteList()
	{
		final String testRedirectionUrl = "http://localhost:9002/";
		whitelistParam.switchToValue("localhost");
		assertThat(samlLoginService.getRedirectionUrl(getComplexRequest(SAP_URL, getFormattedUrl(testRedirectionUrl))))
				.isNotEmpty()
				.isEqualTo(Optional.of(testRedirectionUrl));
	}

	@Test
	public void shouldStoreLoginToken()
	{
		final MockHttpServletResponse response = getDummyResponse();
		samlLoginService.storeLoginToken(response, getUserModel("user"), "en");
		assertThat(response.getCookie(SSO_DEFAULT_COOKIE_NAME)).isNotNull();
	}

	@Test
	public void shouldReturnRedirectionUrlWhenContainsDuplicatedParameters()
	{
		final String testRedirectUrl = "http://localhost:9002/search?param1=&param2=value1&param2=value2&param2=value3";
		assertThat(samlLoginService.getRedirectionUrl(getSimpleRequest(getFormattedUrl(testRedirectUrl))))
				.isNotEmpty()
				.isEqualTo(Optional.of(testRedirectUrl));
	}

	private UserModel getUserModel(final String uid)
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid(uid);
		modelService.save(user);
		return user;
	}

	private String getFormattedUrl(final String redirect)
	{
		return "http://localhost:9002/?" + RETURN_TO + "=" + redirect;
	}

	private HttpServletRequest getSimpleRequest(final String url)
	{
		return savedRequest(url);
	}

	private MockHttpServletResponse getDummyResponse()
	{
		return new MockHttpServletResponse();
	}

	private HttpServletRequest savedRequest(final String url)
	{
		final HttpSessionRequestCache cache = new HttpSessionRequestCache();
		final MockHttpSession session = new MockHttpSession();
		final HttpServletRequest request = getDummyRequest(url, session);
		cache.saveRequest(request, getDummyResponse());
		return request;
	}

	private HttpServletRequest getComplexRequest(final String requestUrl, final String redirectUrl)
	{
		return getComplexRequest(requestUrl, redirectUrl, false);
	}

	private HttpServletRequest getComplexRequest(final String requestUrl, final String redirectUrl, final boolean useMockito)
	{
		if (useMockito)
		{
			final HttpServletRequest request = mock(HttpServletRequest.class);
			when(request.getSession(false)).thenReturn(savedRequest(redirectUrl).getSession());
			when(request.getRequestURL()).thenReturn(new StringBuffer(requestUrl));
			return request;
		}
		else
		{
			return getDummyRequest(requestUrl, savedRequest(redirectUrl).getSession());
		}
	}

	private HttpServletRequest getDummyRequest(final String url, final HttpSession session)
	{
		return MockMvcRequestBuilders
				.get(url)
				.session((MockHttpSession) session)
				.buildRequest(new MockServletContext(url));
	}

}
