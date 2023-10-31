/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservicescommons.interceptor;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercewebservicescommons.annotation.CaptchaAware;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaValidationContext;
import de.hybris.platform.commercewebservicescommons.services.CaptchaValidationService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Objects;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static de.hybris.platform.commercewebservicescommons.constants.CommercewebservicescommonsConstants.CAPTCHA_TOKEN_HEADER;


/**
 * To intercept these requests with annotation @CaptchaAware and do the captcha check validation if captchaCheckEnabled is true for the currentSite
 * If captcha token is need to be checked and check failed then exception will be thrown out
 */
public class CaptchaValidationInterceptor implements HandlerInterceptor
{
	private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";
	private static final String REFERER = "referer";
	private final CaptchaValidationService captchaValidationService;
	private final BaseSiteService baseSiteService;
	private final BaseStoreService baseStoreService;

	public CaptchaValidationInterceptor(final CaptchaValidationService captchaValidationService,
			final BaseSiteService baseSiteService, final BaseStoreService baseStoreService)
	{
		this.captchaValidationService = captchaValidationService;
		this.baseSiteService = baseSiteService;
		this.baseStoreService = baseStoreService;
	}

	/**
	 * To intercept these requests with annotation @CaptchaAware and do the captcha check validation if captchaCheckEnabled is true for the currentSite
	 * If captcha token is need to be checked and checked failed then exception will be thrown out
	 *
	 * @param request  current HTTP request
	 * @param response current HTTP response
	 * @param handler  chosen handler to execute, for type and/or instance evaluation
	 * @return true if the execution chain should proceed with the next interceptor or the handler itself. Else, DispatcherServlet assumes that this interceptor has already dealt with the response itself.
	 * @throws IllegalArgumentException
	 * @throws de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException
	 * @throws de.hybris.platform.commercewebservicescommons.errors.exceptions.CaptchaTokenMissingException
	 * @throws de.hybris.platform.commercewebservicescommons.errors.exceptions.CaptchaValidationException
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
	{
		final CaptchaAware captchaAwareAnnotation = this.getCaptchaAwareAnnotation(handler);
		if (captchaAwareAnnotation != null)
		{
			final CaptchaValidationContext context = prepareContext(request);

			//call service to choose strategy and do the validation
			if (context.isCaptchaCheckEnabled())
			{
				this.getCaptchaValidationService().validate(context);
			}
		}
		return true;
	}

	protected CaptchaAware getCaptchaAwareAnnotation(Object handler)
	{
		if (!(handler instanceof HandlerMethod))
		{
			return null;
		}
		else
		{
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			return handlerMethod.getMethodAnnotation(CaptchaAware.class);
		}
	}


	protected CaptchaValidationService getCaptchaValidationService()
	{
		return captchaValidationService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * prepare the validation context, including the remoteIp, baseSiteId, baseStoreId, serverName, and if the baseSite is captchaCheckEnabled
	 *
	 * @param request HttpServletRequest
	 * @return the validation context, including the remoteIp, baseSiteId, baseStoreId, serverName, and if the baseSite is captchaCheckEnabled
	 */
	private CaptchaValidationContext prepareContext(final HttpServletRequest request)
	{
		final CaptchaValidationContext context = new CaptchaValidationContext();

		final String captchaToken = request.getHeader(CAPTCHA_TOKEN_HEADER);
		context.setCaptchaToken(captchaToken);

		final String xForwardedFor = request.getHeader(X_FORWARDED_FOR);
		context.setRemoteIp(xForwardedFor != null ? xForwardedFor : request.getRemoteAddr());

		context.setReferer(request.getHeader(REFERER));

		final BaseSiteModel currentSite = this.getBaseSiteService().getCurrentBaseSite();
		context.setBaseSiteId(currentSite.getUid());

		final BaseStoreModel currentStore = this.getBaseStoreService().getCurrentBaseStore();
		context.setBaseStoreId(currentStore.getUid());

		context.setCaptchaCheckEnabled(isCaptchaCheckEnabledForBaseSite(currentSite));

		return context;
	}

	/**
	 * updated in release 2211 : move captchaCheckEnabled from store to site,
	 * the flag for site = true when any of the related store enabled the switch
	 *
	 * @param currentSite current base site
	 * @return the captcha check enabled result for the base site
	 */
	private boolean isCaptchaCheckEnabledForBaseSite(final BaseSiteModel currentSite)
	{
		return currentSite.getStores().stream()
				.anyMatch(store -> Objects.nonNull(store.getCaptchaCheckEnabled()) && store.getCaptchaCheckEnabled());
	}
}
