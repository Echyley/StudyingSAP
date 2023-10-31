/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.exceptionhandlers;

import de.hybris.platform.integrationbackoffice.exceptions.IntegrationBackofficeException;
import de.hybris.platform.integrationbackoffice.localization.LocalizationService;
import de.hybris.platform.integrationservices.exception.LocalizedInterceptorException;
import de.hybris.platform.integrationservices.util.ApplicationBeans;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.Ordered;

import com.hybris.cockpitng.service.ExceptionTranslationHandler;


/**
 * An abstract class for translating target exceptions to localized error messages. Its subclass needs to overwrite methods in
 * order to specify the target exceptions. Target exceptions are those that can be handled even if they caused some other exception.
 * For example, if {@code targetException} is an exception known by the handler and there is another exception, e.g.
 * {@code var ex = new Throwable(targetException)}, then the root cause(s) of the {@code ex} will be analyzed and when
 * {@code targetException} is found, the message will be displayed for the {@code targetException} instead of the {@code ex}.
 * <p>The message is retrieved from the localization properties of this extension by the resource bundle key that has the following
 * format: {@code <prefix>.exceptionTranslation.msg.<exception_simple_class_name>}</p>, where {@code <prefix>} is the resource
 * bundle prefix used by the handler or by the extension in general (for all messagges and not only for exception translation).
 */
public abstract class IntegrationApiExceptionTranslationHandler implements ExceptionTranslationHandler, Ordered
{
	private static final String COMMON_ERROR_MSG_KEY_PART = ".exceptionTranslation.msg.";
	private static final Object[] NO_PARAMETERS = new Object[0];

	private LocalizationService localizationService;

	@Override
	public boolean canHandle(final Throwable throwable)
	{
		return isExceptionOrCauseSupported(throwable);
	}

	@Override
	public String toString(final Throwable throwable)
	{
		final Throwable realThrowable = getRealExceptionCause(throwable);
		final String localizedMsg = convertExceptionToResourceMsg(realThrowable);
		return StringUtils.isNotBlank(localizedMsg) ?
				localizedMsg :
				realThrowable.getLocalizedMessage();
	}

	@Override
	public int getOrder()
	{
		return Ordered.HIGHEST_PRECEDENCE;
	}

	private boolean isExceptionOrCauseSupported(final Throwable throwable)
	{
		return throwable != null && (isExceptionSupported(throwable) || isExceptionOrCauseSupported(throwable.getCause()));
	}

	private boolean isExceptionSupported(final Throwable throwable)
	{
		return throwable != null && isTargetedException(throwable);
	}

	private boolean isTargetedException(final Throwable throwable)
	{
		return getTargetedExceptions().contains(throwable.getClass());
	}

	private Throwable getRealExceptionCause(final Throwable throwable)
	{
		if (throwable.getCause() != null && isExceptionSupported(throwable.getCause().getCause()))
		{
			return throwable.getCause().getCause();
		}
		if (isExceptionSupported(throwable.getCause()))
		{
			return throwable.getCause();
		}
		return throwable;
	}

	protected abstract Collection<Class<? extends Throwable>> getTargetedExceptions();

	/**
	 * Try to get bundle resource with the information of the exception that is being handled.
	 *
	 * @param exception The exception that is being translated. Use its class name or other information as key to get bundle resource
	 */
	protected String convertExceptionToResourceMsg(final Throwable exception)
	{
		final var l10NService = getLocalizationService();
		final String bundleKey = getBundleKey(exception);
		final var bundleMsg = l10NService.getLocalizedString(bundleKey, extractParameters(exception));
		return StringUtils.isBlank(bundleMsg) || bundleKey.equalsIgnoreCase(bundleMsg)
				? null
				: bundleMsg;
	}

	/**
	 * Extracts parameters from the given exception.
	 *
	 * @param exception an exception to convert to a parameters array.
	 * @return the parameters array that will be used to format the localized message or an empty array, if the message for the
	 * exception should not have parameters.
	 */
	protected Object[] extractParameters(final Throwable exception)
	{
		if (exception instanceof LocalizedInterceptorException)
		{
			return ((LocalizedInterceptorException) exception).getParameters();
		}
		else if (exception instanceof IntegrationBackofficeException)
		{
			return ((IntegrationBackofficeException) exception).getParameters();
		}
		return NO_PARAMETERS;
	}

	private String getBundleKey(final Throwable ex)
	{
		return getKeyPrefix() + COMMON_ERROR_MSG_KEY_PART + ex.getClass().getSimpleName();
	}

	/**
	 * Retrieves prefix of all property keys in the resource bundle used by this extension/handler. All localization keys should
	 * start with this prefix, then followed by {@code "."} and further key specification.
	 * @return a prefix to be used for localization property keys in the resource bundle.
	 */
	protected abstract String getKeyPrefix();

	/**
	 * Injects localization service implementation to be used for localized message retrieval from the localization resource
	 * bundle.
	 *
	 * @param service a localization service implementation. If {@code null}, then a default service available in the platform will be used.
	 */
	public void setLocalizationService(final LocalizationService service)
	{
		localizationService = service;
	}

	private LocalizationService getLocalizationService()
	{
		if (localizationService == null)
		{
			localizationService = ApplicationBeans.getBean("integrationBackofficeLocalizationService", LocalizationService.class);
		}
		return localizationService;
	}
}
