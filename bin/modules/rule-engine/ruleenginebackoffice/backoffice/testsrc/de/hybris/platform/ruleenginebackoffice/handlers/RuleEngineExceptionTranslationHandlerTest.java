/*
 * [y] hybris Platform
 *
 * Copyright (c) 2020 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.ruleenginebackoffice.handlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ValidationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleEngineExceptionTranslationHandlerTest
{
	@InjectMocks
	private RuleEngineExceptionTranslationHandler handler;

	private final String invalidMessage = "model can not saved";
	private final String validMessage = "[de.hybris.platform.couponservices.interceptor.CodeGenerationConfigurationValidateInterceptor@38a0ad66]:coupon code error!";

	@Test
	public void testIsModelExceptionTranslationSupported()
	{
		final List<String> interceptors = new ArrayList<>();
		interceptors.add("SourceRuleValidateInterceptor");
		interceptors.add("DroolsRuleValidateInterceptor");
		interceptors.add("CodeGenerationConfigurationValidateInterceptor");
		handler.setSupportedInterceptors(interceptors);
		assertTrue(handler.isModelExceptionTranslationSupported(validMessage));
		assertFalse(handler.isModelExceptionTranslationSupported(invalidMessage));
	}

	@Test
	public void testIsExceptionTypeSupported()
	{
		final Throwable throwable = new ValidationException("test error");
		final Throwable throwable1 = new ModelSavingException("save model error");
		final Throwable throwable2 = new ValidationException(throwable1);
		assertFalse(handler.isExceptionTypeSupported(throwable));
		assertTrue(handler.isExceptionTypeSupported(throwable1));
		assertTrue(handler.isExceptionTypeSupported(throwable2));
	}

	@Test
	public void testCanHandle()
	{
		final List<String> interceptors = new ArrayList<>();
		interceptors.add("CodeGenerationConfigurationValidateInterceptor");
		handler.setSupportedInterceptors(interceptors);

		final Throwable throwable = new ValidationException(validMessage);
		final Throwable throwable1 = new ModelSavingException("save model error", throwable);

		assertTrue(handler.canHandle(throwable1));
	}

	@Test
	public void testExceptionMessageIsNull()
	{
		final List<String> interceptors = new ArrayList<>();
		interceptors.add("CodeGenerationConfigurationValidateInterceptor");
		handler.setSupportedInterceptors(interceptors);

		final Throwable throwable = new ValidationException();
		final Throwable throwable1 = new ModelSavingException("save model error", throwable);

		assertFalse(handler.canHandle(throwable1));
	}
}
