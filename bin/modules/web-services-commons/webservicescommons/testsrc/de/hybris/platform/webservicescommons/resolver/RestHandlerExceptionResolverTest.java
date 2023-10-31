/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webservicescommons.resolver;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.testframework.TestUtils;
import de.hybris.platform.webservicescommons.errors.factory.WebserviceErrorFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Vector;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.converter.HttpMessageConverter;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RestHandlerExceptionResolverTest
{
	private RestHandlerExceptionResolver resolver;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Object handler;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private Configuration configuration;

	@Mock
	private HttpMessageConverter<?> messageConverter;

	@Mock
	private WebserviceErrorFactory webserviceErrorFactory;



	@Before
	public void initMocks()
	{
		resolver = new RestHandlerExceptionResolver();
		resolver.setConfigurationService(configurationService);
		resolver.setMessageConverters(new HttpMessageConverter[] { messageConverter });
		resolver.setWebserviceErrorFactory(webserviceErrorFactory);
		resolver.setPropertySpecificKey("testkey");
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getInteger(Mockito.anyString(), Mockito.any())).thenReturn(null);
		when(configuration.getBoolean(Mockito.anyString(), Mockito.any())).thenReturn(null);
		when(request.getHeaderNames()).thenReturn(new Vector<String>().elements());

	}

	@Test
	public void shouldReturnExceptionGlobalProperty()
	{
		when(configuration.getInteger(RestHandlerExceptionResolver.PROPERTY_ROOT_KEY + "RuntimeException.status", null))
				.thenReturn(Integer.valueOf(123));
		final RuntimeException ex = new RuntimeException();
		TestUtils.disableFileAnalyzer("Mapped exception is going to be logged in this case.");

		try
		{
			resolver.doResolveException(request, response, handler, ex);
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
		Mockito.verify(response).setStatus(123);
	}

	@Test
	public void shouldWorkWithoutConfigurationService()
	{
		resolver.setConfigurationService(null);
		final RuntimeException ex = new RuntimeException();
		TestUtils.disableFileAnalyzer("Mapped exception is going to be logged in this case.");

		try
		{
			resolver.doResolveException(request, response, handler, ex);
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
		Mockito.verify(response).setStatus(400);
	}

	@Test
	public void shouldReturnExceptionSpecificProperty()
	{
		when(configuration.getInteger(RestHandlerExceptionResolver.PROPERTY_ROOT_KEY + "testkey.RuntimeException.status", null))
				.thenReturn(Integer.valueOf(456));
		lenient().when(configuration.getInteger(RestHandlerExceptionResolver.PROPERTY_ROOT_KEY + "RuntimeException.status", null))
				.thenReturn(Integer.valueOf(123));
		final RuntimeException ex = new RuntimeException();
		TestUtils.disableFileAnalyzer("Mapped exception is going to be logged in this case.");

		try
		{
			resolver.doResolveException(request, response, handler, ex);
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
		Mockito.verify(response).setStatus(456);
	}

	@Test
	public void shouldReturnExceptionDefaultProperty()
	{
		when(configuration.getInteger(RestHandlerExceptionResolver.DEFAULT_STATUS_PROPERTY, null)).thenReturn(Integer.valueOf(789));

		final RuntimeException ex = new RuntimeException();
		TestUtils.disableFileAnalyzer("Mapped exception is going to be logged in this case.");

		try
		{
			resolver.doResolveException(request, response, handler, ex);
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
		Mockito.verify(response).setStatus(789);
	}

	@Test
	public void shouldDisplayStackByDefault()
	{
		Assert.assertTrue(resolver.shouldDisplayStack(new RuntimeException()));
	}

	@Test
	public void shouldDisplayStackFromDefaultProperty()
	{
		when(configuration.getBoolean(RestHandlerExceptionResolver.DEFAULT_LOGSTACK_PROPERTY, null)).thenReturn(Boolean.FALSE);

		Assert.assertFalse(resolver.shouldDisplayStack(new RuntimeException()));
	}

	@Test
	public void shouldDisplayStackFromGlobalProperty()
	{
		when(configuration.getBoolean(RestHandlerExceptionResolver.PROPERTY_ROOT_KEY + "RuntimeException.logstack", null))
				.thenReturn(Boolean.FALSE);

		Assert.assertFalse(resolver.shouldDisplayStack(new RuntimeException()));
	}

	@Test
	public void shouldDisplayStackFromSpecificProperty()
	{
		when(configuration.getBoolean(RestHandlerExceptionResolver.PROPERTY_ROOT_KEY + "testkey.RuntimeException.logstack", null))
				.thenReturn(Boolean.FALSE);

		Assert.assertFalse(resolver.shouldDisplayStack(new RuntimeException()));
	}
}
