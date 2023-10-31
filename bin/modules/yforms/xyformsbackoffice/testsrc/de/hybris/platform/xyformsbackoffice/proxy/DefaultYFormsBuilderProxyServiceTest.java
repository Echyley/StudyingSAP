/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsbackoffice.proxy;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.xyformsbackoffice.proxy.impl.DefaultYFormsBuilderProxyService;
import de.hybris.platform.xyformsservices.proxy.orbeonservices.CookieManager;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.user.BackofficeRoleService;
import com.hybris.backoffice.user.BackofficeUserService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultYFormsBuilderProxyServiceTest
{
	private static final String proxyHeaderKey = "hybris-Proxy-e46d2928-cec3-4fd6-87dd-40af5991f34d";

	private static final String proxyHeaderValue = "a557faca-6bdd-484b-bc5f-8a49238b3dd8";

	@InjectMocks
	private DefaultYFormsBuilderProxyServiceTestMock defaultYFormsBuilderProxyService = new DefaultYFormsBuilderProxyServiceTestMock();

	@Mock
	private	BackofficeUserService backofficeUserService;

	@Mock
	private BackofficeRoleService backofficeRoleService;

	@Mock
	private	HttpURLConnection connection;

	@Mock
	private	HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private ServletOutputStream outputStream;

	@Mock
	private ServletInputStream inputStream;

	@Mock
	private CookieManager cookieManager;

	@Before
	public void setUp()
	{
		defaultYFormsBuilderProxyService.setOrbeonWebAddress("http://localhost:9002/web-orbeon");
		defaultYFormsBuilderProxyService.setYFormsProxyHeader(proxyHeaderKey);
		defaultYFormsBuilderProxyService.setYFormsProxyValue(proxyHeaderValue);
	}

	@Test
	public void rewriteURLTest()
	{
		String newUrl = defaultYFormsBuilderProxyService.rewriteURLMock("https://localhost:9002/backoffice/orbeon/fr/orbeon/builder/edit/66ba893a657df5595c228bc60c0a195baa5e8be6");
		assertEquals("http://localhost:9002/web-orbeon/fr/orbeon/builder/edit/66ba893a657df5595c228bc60c0a195baa5e8be6", newUrl);
	}

	@Test
	public void setResponseHeadersTest()
	{
		UserModel userModel = new UserModel();
		userModel.setUid("orbeon.user.test@sap.com");
		when(backofficeRoleService.getActiveRole()).thenReturn(Optional.of("backofficeadministratorrole"));
		when(backofficeUserService.getCurrentUser()).thenReturn(userModel);
		when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
		defaultYFormsBuilderProxyService.setRequestHeadersMock(request, connection);
		verify(connection).setRequestProperty(proxyHeaderKey, proxyHeaderValue);
		verify(connection).setRequestProperty("hybris-Username", "orbeon.user.test@sap.com");
		verify(connection).setRequestProperty("hybris-Roles", "backofficeadministratorrole");
	}

	@Test
	public void setRequestHeadersTest()
	{
		Map<String, List<String>> headerMap = new HashMap<>();
		List<String> list = new ArrayList<>();
		list.add("JSESSIONID=07EA34A954F4067280170E82B9B43C1D");
		headerMap.put("SetCookie", list);
		when(connection.getHeaderFields()).thenReturn(headerMap);
		when(connection.getHeaderField("SetCookie")).thenReturn("JSESSIONID=07EA34A954F4067280170E82B9B43C1D");
		defaultYFormsBuilderProxyService.setResponseHeadersMock(connection, response);
		verify(response).setHeader("SetCookie", "JSESSIONID=07EA34A954F4067280170E82B9B43C1D");
	}

	@Test
	public void proxyGetSuccessTest() throws IOException
	{
		UserModel userModel = new UserModel();
		userModel.setUid("orbeon.user.test@sap.com");
		when(backofficeRoleService.getActiveRole()).thenReturn(Optional.of("backofficeadministratorrole"));
		when(backofficeUserService.getCurrentUser()).thenReturn(userModel);
		when(request.getRequestURI()).thenReturn("backoffice/orbeon/fr/orbeon/builder/edit/123");
		when(request.getMethod()).thenReturn(HttpMethod.GET);

		try (MockedConstruction<URL> mocked = Mockito.mockConstruction(URL.class,
				(mock, context) -> {
					when(mock.openConnection()).thenReturn(connection);
				})) {
			Map<String, List<String>> headerMap = new HashMap<>();
			headerMap.put("X-Frame-Options", Collections.singletonList("DENY"));
			when(connection.getHeaderFields()).thenReturn(headerMap);
			when(connection.getHeaderField("X-Frame-Options")).thenReturn("DENY");
			when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
			when(response.getOutputStream()).thenReturn(outputStream);
			response.setHeader("X-Frame-Options", "SAMEORIGIN");
			defaultYFormsBuilderProxyService.proxy(request, response);
			verify(response).setHeader("X-Frame-Options", "DENY");
			verify(connection).getInputStream();
		}
	}

	@Test
	public void proxyPostSuccessTest() throws IOException
	{
		UserModel userModel = new UserModel();
		userModel.setUid("orbeon.user.test@sap.com");
		when(backofficeRoleService.getActiveRole()).thenReturn(Optional.of("backofficeadministratorrole"));
		when(backofficeUserService.getCurrentUser()).thenReturn(userModel);
		when(request.getRequestURI()).thenReturn("backoffice/orbeon/fr/orbeon/builder/edit/123");
		when(request.getMethod()).thenReturn(HttpMethod.POST);
		when(request.getInputStream()).thenReturn(inputStream);

		try (MockedConstruction<URL> mocked = Mockito.mockConstruction(URL.class,
				(mock, context) -> {
					when(mock.openConnection()).thenReturn(connection);
				})) {
			Map<String, List<String>> headerMap = new HashMap<>();
			when(connection.getHeaderFields()).thenReturn(headerMap);
			when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
			when(response.getOutputStream()).thenReturn(outputStream);
			defaultYFormsBuilderProxyService.proxy(request, response);
			verify(connection).getOutputStream();
			verify(connection).getInputStream();
		}
	}

	@Test
	public void proxyFailTest() throws IOException
	{
		UserModel userModel = new UserModel();
		userModel.setUid("orbeon.user.test@sap.com");
		when(backofficeRoleService.getActiveRole()).thenReturn(Optional.of("backofficeadministratorrole"));
		when(backofficeUserService.getCurrentUser()).thenReturn(userModel);
		when(request.getRequestURI()).thenReturn("backoffice/orbeon/fr/orbeon/builder/edit/123");

		try (MockedConstruction<URL> mocked = Mockito.mockConstruction(URL.class,
				(mock, context) -> {
					when(mock.openConnection()).thenReturn(connection);
				})) {
			Map<String, List<String>> headerMap = new HashMap<>();
			when(connection.getHeaderFields()).thenReturn(headerMap);
			when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
			when(response.getOutputStream()).thenReturn(outputStream);
			defaultYFormsBuilderProxyService.proxy(request, response);
			verify(connection).getErrorStream();
		}
	}


	public static class DefaultYFormsBuilderProxyServiceTestMock extends DefaultYFormsBuilderProxyService
	{
		public String rewriteURLMock(String uri)
		{
			return this.rewriteURL(uri);
		}

		public void setResponseHeadersMock(final HttpURLConnection conn, final HttpServletResponse response)
		{
			this.setResponseHeaders(conn, response);
		}

		public void setRequestHeadersMock(final HttpServletRequest request, final HttpURLConnection conn)
		{
			this.setRequestHeaders(request, conn);
		}
	}
}
