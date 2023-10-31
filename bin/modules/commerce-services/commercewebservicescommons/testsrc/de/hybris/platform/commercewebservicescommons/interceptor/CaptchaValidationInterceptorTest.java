/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservicescommons.interceptor;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercewebservicescommons.annotation.CaptchaAware;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaValidationContext;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CaptchaTokenMissingException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CaptchaValidationException;
import de.hybris.platform.commercewebservicescommons.services.CaptchaValidationService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.method.HandlerMethod;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CaptchaValidationInterceptorTest
{
	@Mock
	private CaptchaValidationService captchaValidationService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private BaseStoreService baseStoreService;

	@InjectMocks
	private CaptchaValidationInterceptor interceptor;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private HandlerMethod handler;
	private BaseSiteModel baseSiteModel;
	private BaseStoreModel baseStoreModel1, baseStoreModel2;
	private List<BaseStoreModel> baseStoreModels;

	private final CaptchaValidationInterceptorTest.UsersController usersController = new CaptchaValidationInterceptorTest.UsersController();
	private final static String CAPTCHA_TOKEN = "03ANYolqv1YE-eCxznv6cWNaBf9VgzFn3mCGWXCQqoroWGk6fdjCj3OYt57wLZ5Wzo9frd6KQ4A2R6amEtYzCjOfDaqLg52M0eDx9eIQmSDRjfnW6nCQqFPv_MYVM5ZgA_08W7RoVFJ38r2kELfGw5p3-X7qs8AYKUH7F28DEtl_ytfeg-leklb7bfKgRfMhQtcuVRxIsp9DShbgWf_FzU809CVowLnbSKe3JhIBf0MQdhsO5Wgsl4_itmVFtnmtMd3ZfDBllc_3yYczsEMrmeewMsYHJ-IKPkK4YEiS0jXZ_Ubc3Cla4sQzrnbrF6GSRbh46G2pAEhmk8gqyUQXmkbdnR7iMLdZ5dZfZVjNQxQqusvzC7H7X66r3Lh3mCliZE2M1ChQejOMMaqGuCMnWX1CYE7JsBT0LZIhFezJsuoCQvQNrK396QdqhiymaQ4uFMlifv5GRa8aYF3XUfZsT6_yQgvsCm3C5edy7gc7KikhQ5HWOgQhq18VY";
	private final static String HEADER_TOKEN_NAME = "sap-commerce-cloud-captcha-token";
	private final static String HEADER_X_FORWARDED_NAME = "X-FORWARDED-FOR";
	private static final String REFERER_NAME="referer";
	private final static String X_FORWARDED_FOR = "127.0.0.1";
	private final static String REFERER = "electronics.local:9002";

	@Before
	public void setUp()
	{
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		baseSiteModel = new BaseSiteModel();
		baseSiteModel.setUid("testSite");

		baseStoreModel1 = new BaseStoreModel();
		baseStoreModel1.setUid("testStore1");

		baseStoreModel2 = new BaseStoreModel();
		baseStoreModel2.setUid("testStore2");
		baseStoreModels = List.of(baseStoreModel1, baseStoreModel2);
		baseSiteModel.setStores(baseStoreModels);

		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel1);
	}

	@Test
	public void testMethodWithAnnotationShouldSuccessWhenSiteHasMultipleStoreAndAllOfThemEnabledSwitch() throws Exception
	{
		// given
		handler = new HandlerMethod(usersController, usersController.getClass().getMethod("register"));
		request.addHeader(HEADER_TOKEN_NAME, CAPTCHA_TOKEN);
		request.addHeader(HEADER_X_FORWARDED_NAME, X_FORWARDED_FOR);
		request.addHeader(REFERER_NAME, REFERER);

		baseStoreModel1.setCaptchaCheckEnabled(true);
		baseStoreModel2.setCaptchaCheckEnabled(true);

		// when
		interceptor.preHandle(request, response, handler);

		// then
		Mockito.verify(baseSiteService, times(1)).getCurrentBaseSite();
		Mockito.verify(baseStoreService, times(1)).getCurrentBaseStore();

		ArgumentCaptor<CaptchaValidationContext> captor = ArgumentCaptor.forClass(CaptchaValidationContext.class);
		Mockito.verify(captchaValidationService, times(1)).validate(captor.capture());
		CaptchaValidationContext context = captor.getValue();
		Assert.assertNotNull(context);
		Assert.assertEquals(CAPTCHA_TOKEN, context.getCaptchaToken());
		Assert.assertEquals(X_FORWARDED_FOR, context.getRemoteIp());
		Assert.assertEquals(REFERER, context.getReferer());
		Assert.assertEquals("testSite", context.getBaseSiteId());
		Assert.assertEquals("testStore1", context.getBaseStoreId());
		Assert.assertTrue(context.isCaptchaCheckEnabled());
	}

	@Test
	public void testMethodWithAnnotationShouldSuccessWhenSiteHasMultipleStoreAndOneOfThemEnabledSwitch() throws Exception
	{
		// given
		handler = new HandlerMethod(usersController, usersController.getClass().getMethod("register"));
		request.addHeader(HEADER_TOKEN_NAME, CAPTCHA_TOKEN);
		request.addHeader(HEADER_X_FORWARDED_NAME, X_FORWARDED_FOR);
		request.addHeader(REFERER_NAME, REFERER);

		baseStoreModel1.setCaptchaCheckEnabled(true);
		baseStoreModel2.setCaptchaCheckEnabled(false);

		// when
		interceptor.preHandle(request, response, handler);

		// then
		Mockito.verify(baseSiteService, times(1)).getCurrentBaseSite();
		Mockito.verify(baseStoreService, times(1)).getCurrentBaseStore();

		ArgumentCaptor<CaptchaValidationContext> captor = ArgumentCaptor.forClass(CaptchaValidationContext.class);
		Mockito.verify(captchaValidationService, times(1)).validate(captor.capture());
		CaptchaValidationContext context = captor.getValue();
		Assert.assertNotNull(context);
		Assert.assertEquals(CAPTCHA_TOKEN, context.getCaptchaToken());
		Assert.assertEquals(X_FORWARDED_FOR, context.getRemoteIp());
		Assert.assertEquals(REFERER, context.getReferer());
		Assert.assertEquals("testSite", context.getBaseSiteId());
		Assert.assertEquals("testStore1", context.getBaseStoreId());
		Assert.assertTrue(context.isCaptchaCheckEnabled());
	}

	@Test
	public void testMethodWithAnnotationShouldDoNothingWhenCaptchaCheckEnabledIsFalse() throws Exception
	{
		// given
		handler = new HandlerMethod(usersController, usersController.getClass().getMethod("register"));
		request.addHeader(HEADER_TOKEN_NAME, CAPTCHA_TOKEN);
		request.addHeader(HEADER_X_FORWARDED_NAME, X_FORWARDED_FOR);
		request.addHeader(REFERER_NAME, REFERER);

		baseStoreModel1.setCaptchaCheckEnabled(false);
		baseStoreModel2.setCaptchaCheckEnabled(null);

		// when
		interceptor.preHandle(request, response, handler);

		// then
		Mockito.verify(baseSiteService, times(1)).getCurrentBaseSite();
		Mockito.verify(baseStoreService, times(1)).getCurrentBaseStore();

		Mockito.verify(captchaValidationService, times(0)).validate(any());
	}

	@Test
	public void testMethodWithAnnotationShouldThrowCaptchaTokenMissingExceptionWhenHeaderIsNull() throws Exception
	{
		// given
		handler = new HandlerMethod(usersController, usersController.getClass().getMethod("register"));
		request.addHeader(HEADER_TOKEN_NAME, CAPTCHA_TOKEN);
		request.addHeader(HEADER_X_FORWARDED_NAME, X_FORWARDED_FOR);
		request.addHeader(REFERER_NAME, REFERER);
		Mockito.doThrow(CaptchaTokenMissingException.class).when(captchaValidationService).validate(any());

		baseStoreModel1.setCaptchaCheckEnabled(true);

		// when
		Assert.assertThrows(CaptchaTokenMissingException.class, () -> interceptor.preHandle(request, response, handler));

		// then
		ArgumentCaptor<CaptchaValidationContext> captor = ArgumentCaptor.forClass(CaptchaValidationContext.class);
		Mockito.verify(captchaValidationService, times(1)).validate(captor.capture());
		CaptchaValidationContext context = captor.getValue();
		Assert.assertNotNull(context);
		Assert.assertEquals(CAPTCHA_TOKEN, context.getCaptchaToken());
	}

	@Test
	public void testMethodWithAnnotationShouldThrowCaptchaValidationExceptionWhenHeaderIsInvalid() throws Exception
	{
		// given
		handler = new HandlerMethod(usersController, usersController.getClass().getMethod("register"));
		request.addHeader(HEADER_TOKEN_NAME, CAPTCHA_TOKEN);
		Mockito.doThrow(CaptchaValidationException.class).when(captchaValidationService).validate(any());

		baseStoreModel1.setCaptchaCheckEnabled(true);

		// when
		Assert.assertThrows(CaptchaValidationException.class, () -> interceptor.preHandle(request, response, handler));

		// then
		ArgumentCaptor<CaptchaValidationContext> captor = ArgumentCaptor.forClass(CaptchaValidationContext.class);
		Mockito.verify(captchaValidationService, times(1)).validate(captor.capture());
		CaptchaValidationContext context = captor.getValue();
		Assert.assertNotNull(context);
		Assert.assertEquals(CAPTCHA_TOKEN, context.getCaptchaToken());
	}

	@Test
	public void testMethodWithoutAnnotationShouldDoNothing() throws Exception
	{
		// given
		handler = new HandlerMethod(usersController, usersController.getClass().getMethod("getUserById"));

		// when
		interceptor.preHandle(request, response, handler);

		// then
		Mockito.verify(captchaValidationService, times(0)).validate(any());
	}

	@Test
	public void testParameter()
	{
		// when
		interceptor.preHandle(request, response, new Object());

		// then
		Mockito.verify(captchaValidationService, times(0)).validate(any());
	}

	@Controller
	private class UsersController
	{
		@CaptchaAware()
		public String register()
		{
			return "";
		}

		public String getUserById()
		{
			return "";
		}
	}

}
