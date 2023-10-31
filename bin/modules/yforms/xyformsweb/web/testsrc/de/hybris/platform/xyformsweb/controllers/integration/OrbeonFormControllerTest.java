/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsweb.controllers.integration;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.xyformsfacades.data.YFormDataData;
import de.hybris.platform.xyformsfacades.data.YFormDefinitionData;
import de.hybris.platform.xyformsfacades.form.YFormFacade;
import de.hybris.platform.xyformsservices.enums.YFormDataTypeEnum;
import de.hybris.platform.xyformsservices.exception.YFormServiceException;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.DelegatingServletInputStream;


/**
 * Test class for OrbeonFromController
 */
@UnitTest
public class OrbeonFormControllerTest
{
	@InjectMocks
	private final OrbeonFormController controller = Mockito.spy(new OrbeonFormController());

	@Mock
	private HttpServletRequest httpRequest;

	@Mock
	private HttpServletResponse httpResponse;

	@Mock
	private YFormFacade yFormFacade;

	private final String applicationId = "yforms";
	private final String formId = "contact";
	private final String formDataId = "";
	MockitoSession mockito;

	@Before
	public void setup() {
		//initialize session to start mocking
		mockito = Mockito.mockitoSession()
				.initMocks(this)
				.strictness(Strictness.STRICT_STUBS)
				.startMocking();
	}

	@After
	public void tearDown() {
		//It is necessary to finish the session so that Mockito
		// can detect incorrect stubbing and validate Mockito usage
		//'finishMocking()' is intended to be used in your test framework's 'tear down' method.
		mockito.finishMocking();
	}

	@Test
	public void shouldEmptyBodyIfGetYFormDefinitionReturnedNull() throws ServletException, IOException, YFormServiceException
	{
		final String expect = "";
		Mockito.lenient().when(yFormFacade.getYFormDefinition(anyString(), anyString())).thenReturn(null);
		final String result = controller.getFormDefinition(applicationId, formId, null, httpResponse);
		assertEquals(result, expect);
	}

	@Test
	public void shouldReturnEmptyBodyIfBodyReturnedIsNull() throws ServletException, IOException, YFormServiceException
	{
		final String content = null;
		final String expect = "";
		final YFormDefinitionData yFormDefinition = new YFormDefinitionData();
		yFormDefinition.setContent(content);
		Mockito.lenient().when(yFormFacade.getYFormDefinition(anyString(), anyString())).thenReturn(yFormDefinition);
		final String result = controller.getFormDefinition(applicationId, formId, null, httpResponse);
		assertEquals(result, expect);
	}

	@Test
	public void shouldReturnYFormDefinitionBody() throws ServletException, IOException, YFormServiceException
	{
		final String expect = "abc";
		final YFormDefinitionData yFormDefinition = new YFormDefinitionData();
		yFormDefinition.setContent(expect);
		Mockito.lenient().when(yFormFacade.getYFormDefinition(anyString(), anyString())).thenReturn(yFormDefinition);
		final String result = controller.getFormDefinition(applicationId, formId, null, httpResponse);
		assertEquals(result, expect);
	}

	@Test
	public void shouldResponseWithExpectationFailedWithNullInputStream()
			throws ServletException, IOException, YFormServiceException
	{
		Mockito.lenient().when(httpRequest.getInputStream()).thenReturn(null);
		controller.putFormDefinition(applicationId, formId, null, httpRequest, httpResponse);
		verify(httpRequest, times(1)).getInputStream();
		verify(httpResponse, times(1)).sendError(anyInt(), anyString());
	}

	@Test
	public void shouldResponseWithExpectationFailedWithCreateFailed() throws ServletException, IOException, YFormServiceException
	{
		final String newFormDefinition = "";
		final ServletInputStream inputStream = new DelegatingServletInputStream(IOUtils.toInputStream(newFormDefinition, "UTF-8"));
		Mockito.lenient().when(httpRequest.getInputStream()).thenReturn(inputStream);

		Mockito.lenient().when(yFormFacade.createYFormDefinition(nullable(String.class), nullable(String.class), nullable(String.class),
				nullable(String.class))).thenThrow(new YFormServiceException(""));
		controller.putFormDefinition(applicationId, formId, null, httpRequest, httpResponse);
		verify(httpRequest, times(1)).getInputStream();
		verify(httpResponse, times(1)).sendError(anyInt(), anyString());
	}

	@Test
	public void shouldResponseWithOkeyWithCreateSuccessed() throws YFormServiceException, IOException, ServletException
	{
		final String newFormDefinition = "";
		final YFormDefinitionData yFormData = new YFormDefinitionData();
		final ServletInputStream inputStream = new DelegatingServletInputStream(IOUtils.toInputStream(newFormDefinition, "UTF-8"));
		Mockito.lenient().when(httpRequest.getInputStream()).thenReturn(inputStream);
		Mockito.lenient().when(yFormFacade.createYFormDefinition(applicationId, formId, newFormDefinition, null)).thenReturn(yFormData);
		
		controller.putFormDefinition(applicationId, formId, null, httpRequest, httpResponse);
		verify(yFormFacade, times(1)).createYFormDefinition(anyString(), anyString(), anyString(), nullable(String.class));
		verify(httpRequest, times(1)).getInputStream();
		verify(httpResponse, times(1)).setStatus(HttpServletResponse.SC_OK);
	}

	@Test
	public void shouldResponseEmptyStringIfYFormDataBodyEmpty() throws YFormServiceException, ServletException, IOException
	{
		final String expected = "";
		final String content = "";
		final YFormDataData yFormData = new YFormDataData();
		yFormData.setContent(content);
		Mockito.lenient().when(yFormFacade.getYFormData(anyString(), eq(YFormDataTypeEnum.DATA))).thenReturn(yFormData);
		Mockito.lenient().when(yFormFacade.getYFormData(anyString(), eq(YFormDataTypeEnum.DRAFT))).thenReturn(yFormData);
		final String response = controller.getFormDataData(applicationId, formId, formDataId, httpResponse);
		assertEquals(expected, response);
	}

	@Test
	public void shouldResponseWithExistedYFormDataBody() throws ServletException, YFormServiceException, IOException
	{
		final String content = "abc";
		final YFormDataData yFormData = new YFormDataData();
		yFormData.setContent(content);
		Mockito.lenient().when(yFormFacade.getYFormData(anyString(), eq(YFormDataTypeEnum.DATA))).thenReturn(yFormData);
		Mockito.lenient().when(yFormFacade.getYFormData(anyString(), eq(YFormDataTypeEnum.DRAFT))).thenReturn(yFormData);
		final String response = controller.getFormDataData(applicationId, formId, formDataId, httpResponse);
		assertEquals(content, response);
	}

	@Test
	public void shouldReturnYFormServiceExceptionWhenFailedCreateOrUpdatedYFormData()
			throws IOException, YFormServiceException, ServletException
	{
		final String newFormDefinition = "";
		final ServletInputStream inputStream = new DelegatingServletInputStream(IOUtils.toInputStream(newFormDefinition, "UTF-8"));
		Mockito.lenient().when(httpRequest.getInputStream()).thenReturn(inputStream);

		doThrow(new YFormServiceException("")).when(yFormFacade).createOrUpdateYFormData(anyString(), anyString(), anyString(),
				eq(YFormDataTypeEnum.DATA), anyString());
		controller.putFormData(applicationId, formId, YFormDataTypeEnum.DATA.toString().toLowerCase(), formDataId, httpRequest,
				httpResponse);
		verify(httpResponse, times(1)).sendError(anyInt(), anyString());
	}

	@Test
	public void shouldReturnOkWhenSuccessCreateOrUpdatedYFormData() throws IOException, YFormServiceException, ServletException
	{
		final String newFormDefinition = "";
		final ServletInputStream inputStream = new DelegatingServletInputStream(IOUtils.toInputStream(newFormDefinition, "UTF-8"));
		Mockito.lenient().when(httpRequest.getInputStream()).thenReturn(inputStream);

		controller.putFormData(applicationId, formId, YFormDataTypeEnum.DATA.toString().toLowerCase(), formDataId, httpRequest,
				httpResponse);

		verify(controller, times(1)).putFormData(applicationId, formId, YFormDataTypeEnum.DATA.toString().toLowerCase(), formDataId,
				httpRequest, httpResponse);
		verify(yFormFacade, times(1)).createOrUpdateYFormData(applicationId, formId, formDataId, YFormDataTypeEnum.DATA,
				newFormDefinition);

		verify(httpResponse, times(1)).setStatus(HttpServletResponse.SC_OK);
	}
}
