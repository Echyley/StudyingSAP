/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsfacades.form.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.xyformsfacades.proxy.ProxyFacade;
import de.hybris.platform.xyformsservices.enums.YFormDataActionEnum;
import de.hybris.platform.xyformsservices.enums.YFormDataTypeEnum;
import de.hybris.platform.xyformsservices.exception.YFormServiceException;
import de.hybris.platform.xyformsservices.form.YFormService;
import de.hybris.platform.xyformsservices.model.YFormDataModel;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test class for DefaultYFormFacade
 */
@UnitTest
public class DefaultYFormFacadeTest extends ServicelayerTest
{
	@Resource(name = "yFormFacade")
	private DefaultYFormFacade yformFacade;

	@Resource(name = "yformService")
	private YFormService yformService;

	@Mock
	private ProxyFacade proxyFacade;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void validateFormDataWithDraftTypeShouldReturnFalse() throws YFormServiceException
	{
		final String applicationId = "dummy";
		final String formId = "dummy";
		final String formDataId = "dummy";
		final String content = "class=\"xforms-invalid\"";

		final YFormDataModel yformData = new YFormDataModel();
		yformData.setApplicationId(applicationId);
		yformData.setFormId(formId);
		yformData.setId(formDataId);
		yformData.setType(YFormDataTypeEnum.DRAFT);

		yformService.createYFormDefinition(applicationId, formId, "test", "test", null, null);
		yformService.createYFormData(applicationId, formId, formDataId, YFormDataTypeEnum.DRAFT, null, content);
		yformService.createYFormData(applicationId, formId, formDataId, YFormDataTypeEnum.DATA, null, content);

		given(proxyFacade.getInlineFormHtml(applicationId, formId, YFormDataActionEnum.EDIT, formDataId)).willReturn(content);
		yformFacade.setProxyFacade(proxyFacade);

		final boolean result = yformFacade.validate(applicationId, formId, formDataId);
		assertFalse(result);
	}

	@Test
	public void validateFormDataWithDataTypeAndInvalidContentShouldReturnFalse() throws YFormServiceException
	{
		final String applicationId = "dummy";
		final String formId = "dummy";
		final String formDataId = "dummy";
		final String content = "class=\"xforms-invalid\"";

		final YFormDataModel yformData = new YFormDataModel();
		yformData.setApplicationId(applicationId);
		yformData.setFormId(formId);
		yformData.setId(formDataId);
		yformData.setType(YFormDataTypeEnum.DATA);

		yformService.createYFormDefinition(applicationId, formId, "test", "test", null, null);
		yformService.createYFormData(applicationId, formId, formDataId, YFormDataTypeEnum.DRAFT, null, content);
		yformService.createYFormData(applicationId, formId, formDataId, YFormDataTypeEnum.DATA, null, content);

		given(proxyFacade.getInlineFormHtml(applicationId, formId, YFormDataActionEnum.EDIT, formDataId)).willReturn(content);
		yformFacade.setProxyFacade(proxyFacade);

		final boolean result = yformFacade.validate(applicationId, formId, formDataId);
		assertFalse(result);
	}

	@Test
	public void validateFormDataWithDataTypeAndValidContentShouldReturnTrue() throws YFormServiceException
	{
		final String applicationId = "dummy";
		final String formId = "dummy";
		final String formDataId = "dummy";
		final String content = "class=\"foo bar\"";

		final YFormDataModel yformData = new YFormDataModel();
		yformData.setApplicationId(applicationId);
		yformData.setFormId(formId);
		yformData.setId(formDataId);
		yformData.setType(YFormDataTypeEnum.DATA);

		yformService.createYFormDefinition(applicationId, formId, "test", "test", null, null);
		yformService.createYFormData(applicationId, formId, formDataId, YFormDataTypeEnum.DATA, null, content);

		given(proxyFacade.getInlineFormHtml(applicationId, formId, YFormDataActionEnum.EDIT, formDataId)).willReturn(content);
		yformFacade.setProxyFacade(proxyFacade);

		final boolean result = yformFacade.validate(applicationId, formId, formDataId);
		assertTrue(result);
	}
}
