/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookbackoffice.handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationbackoffice.localization.LocalizationService;
import de.hybris.platform.servicelayer.interceptor.Interceptor;
import de.hybris.platform.webhookservices.exceptions.CannotDeleteIntegrationObjectLinkedWithWebhookConfigException;
import de.hybris.platform.webhookservices.exceptions.DestinationTargetNoSupportedEventConfigException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigInvalidChannelException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNoEventConfigException;
import de.hybris.platform.webhookservices.exceptions.WebhookConfigNotValidLocationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WebhookExceptionTranslationHandlerUnitTest
{
	private static final String ERROR_MESSAGE_KEY_PREFIX = "webhookbackoffice.exceptionTranslation.msg.";
	private static final String FILTER_LOCATION = "not matters";
	private static final String IO_CODE = "IO Code - does not matter";
	private static final Interceptor NO_INTERCEPTOR = null;
	private static final Object[] NO_PARAMETERS = {};

	@Mock
	private LocalizationService localizationService;
	@InjectMocks
	private WebhookExceptionTranslationHandler exceptionHandler;
	private final Properties prop = new Properties();

	@Before
	public void setUp()
	{
		try
		{
			final ClassLoader classLoader = WebhookExceptionTranslationHandlerUnitTest.class.getClassLoader();
			final URL url = classLoader.getResource("webhookbackoffice-backoffice-labels/labels_en.properties");
			assert url != null;
			prop.load(new FileInputStream(url.getFile()));
		}
		catch (final IOException | NullPointerException ex)
		{
			ex.printStackTrace();
		}
	}

	@Test
	public void testCannotHandleNullException()
	{
		assertThat(exceptionHandler.canHandle(null)).isFalse();
	}

	@Test
	public void testCannotHandleUnknownExceptions()
	{
		final var anUnknownException = new Throwable();
		assertThat(exceptionHandler.canHandle(anUnknownException)).isFalse();

	}

	@Test
	public void testCanHandleWebhookConfigNotValidLocationException()
	{
		final var knownException = new WebhookConfigNotValidLocationException(FILTER_LOCATION, NO_INTERCEPTOR);
		assertThat(exceptionHandler.canHandle(knownException))
				.as("Cannot handle WebhookConfigNotValidLocationException")
				.isTrue();

		final var nestedIntoAnotherException = new Throwable(knownException);
		assertThat(exceptionHandler.canHandle(nestedIntoAnotherException))
				.as("Cannot handle WebhookConfigNotValidLocationException when it's nested in an unknown exception")
				.isTrue();

		final var deeplyNestedIntoAnotherException = new Throwable(nestedIntoAnotherException);
		assertThat(exceptionHandler.canHandle(deeplyNestedIntoAnotherException))
				.as("Cannot handle WebhookConfigNotValidLocationException when it's deeply nested in unknown exceptions")
				.isTrue();
	}

	@Test
	public void testCanHandleWebhookConfigInvalidChannelException()
	{
		final var knownException = new WebhookConfigInvalidChannelException(NO_INTERCEPTOR);
		assertThat(exceptionHandler.canHandle(knownException))
				.as("Cannot handle WebhookConfigInvalidChannelException")
				.isTrue();

		final var nestedIntoAnotherException = new Throwable(knownException);
		assertThat(exceptionHandler.canHandle(nestedIntoAnotherException))
				.as("Cannot handle WebhookConfigInvalidChannelException when it's nested in an unknown exception")
				.isTrue();

		final var deeplyNestedIntoAnotherException = new Throwable(nestedIntoAnotherException);
		assertThat(exceptionHandler.canHandle(deeplyNestedIntoAnotherException))
				.as("Cannot handle WebhookConfigInvalidChannelException when it's deeply nested in unknown exceptions")
				.isTrue();
	}

	@Test
	public void testCanHandleDestinationTargetNoSupportedEventConfigException()
	{
		final var knownException = new DestinationTargetNoSupportedEventConfigException();
		assertThat(exceptionHandler.canHandle(knownException))
				.as("Cannot handle DestinationTargetNoSupportedEventConfigException")
				.isTrue();

		final var nestedIntoAnotherException = new Throwable(knownException);
		assertThat(exceptionHandler.canHandle(nestedIntoAnotherException))
				.as("Cannot handle DestinationTargetNoSupportedEventConfigException when it's nested in an unknown exception")
				.isTrue();

		final var deeplyNestedIntoAnotherException = new Throwable(nestedIntoAnotherException);
		assertThat(exceptionHandler.canHandle(deeplyNestedIntoAnotherException))
				.as("Cannot handle DestinationTargetNoSupportedEventConfigException when it's deeply nested in unknown exceptions")
				.isTrue();
	}

	@Test
	public void testCanHandleWebhookConfigNoEventConfigException()
	{
		final var knownException = new WebhookConfigNoEventConfigException(NO_INTERCEPTOR);
		assertThat(exceptionHandler.canHandle(knownException))
				.as("Cannot handle WebhookConfigNoEventConfigException")
				.isTrue();

		final var nestedIntoAnotherException = new Throwable(knownException);
		assertThat(exceptionHandler.canHandle(nestedIntoAnotherException))
				.as("Cannot handle WebhookConfigNoEventConfigException when it's nested in an unknown exception")
				.isTrue();

		final var deeplyNestedIntoAnotherException = new Throwable(nestedIntoAnotherException);
		assertThat(exceptionHandler.canHandle(deeplyNestedIntoAnotherException))
				.as("Cannot handle WebhookConfigNoEventConfigException when it's deeply nested in unknown exceptions")
				.isTrue();
	}

	@Test
	public void testCanHandleCannotDeleteIntegrationObjectLinkedWithWebhookConfigException()
	{
		final var knownException = new CannotDeleteIntegrationObjectLinkedWithWebhookConfigException(IO_CODE);
		assertThat(exceptionHandler.canHandle(knownException))
				.as("Cannot handle CannotDeleteIntegrationObjectLinkedWithWebhookConfigException")
				.isTrue();

		final var nestedIntoAnotherException = new Throwable(knownException);
		assertThat(exceptionHandler.canHandle(nestedIntoAnotherException))
				.as("Cannot handle CannotDeleteIntegrationObjectLinkedWithWebhookConfigException when it's nested in an unknown exception")
				.isTrue();

		final var deeplyNestedIntoAnotherException = new Throwable(nestedIntoAnotherException);
		assertThat(exceptionHandler.canHandle(deeplyNestedIntoAnotherException))
				.as("Cannot handle CannotDeleteIntegrationObjectLinkedWithWebhookConfigException when it's deeply nested in unknown exceptions")
				.isTrue();
	}

	@Test
	public void testToStringReturnsLocalizationLabelFromTheResourceBundleForSupportedException()
	{
		final String expectedLabel = "An expected error message.";
		final var supportedException = new WebhookConfigInvalidChannelException(null);
		doReturn(expectedLabel).when(localizationService).getLocalizedString(getResourceKey(supportedException), NO_PARAMETERS);

		assertThat(exceptionHandler.toString(supportedException)).isEqualTo(expectedLabel);
	}

	@Test
	public void testToStringReturnsLocalizationLabelFromTheResourceBundleForSupportedExceptionWhenItIsNestedInAnotherException()
	{
		final String expectedLabel = "An expected error message.";
		final var supportedException = new WebhookConfigInvalidChannelException(null);
		final var outerException = new Throwable(supportedException);
		doReturn(expectedLabel).when(localizationService).getLocalizedString(getResourceKey(supportedException), NO_PARAMETERS);

		assertThat(exceptionHandler.toString(outerException)).isEqualTo(expectedLabel);

		final var causeCanBeHandled2 = new Throwable(outerException);
		assertThat(exceptionHandler.toString(causeCanBeHandled2)).isEqualTo(expectedLabel);

		// If the supported exception can't be converted to resource labels, error message falls back to exception's message.
		doReturn(null).when(localizationService).getLocalizedString(getResourceKey(supportedException), NO_PARAMETERS);
		assertThat(exceptionHandler.toString(supportedException)).isSubstringOf(supportedException.getLocalizedMessage());
	}

	@Test
	public void testToStringReturnsLocalizationLabelFromTheResourceBundleForSupportedExceptionWhenItIsDeeplyNestedInAnotherException()
	{
		final String expectedLabel = "An expected error message.";
		final var supportedException = new WebhookConfigInvalidChannelException(null);
		final var outerException = new Throwable(supportedException);
		final var mostOuterException = new Throwable(outerException);
		doReturn(expectedLabel).when(localizationService).getLocalizedString(getResourceKey(supportedException), NO_PARAMETERS);

		assertThat(exceptionHandler.toString(mostOuterException)).isEqualTo(expectedLabel);
	}

	@Test
	public void testToStringReturnsLocalizedMessageFromSupportedExceptionWhenLabelIsNotFoundInResourceBundle()
	{
		final var supportedException = new WebhookConfigInvalidChannelException(null);
		final String resourceKey = getResourceKey(supportedException);
		doReturn(resourceKey).when(localizationService).getLocalizedString(resourceKey, NO_PARAMETERS);

		assertThat(exceptionHandler.toString(supportedException)).isEqualTo(supportedException.getLocalizedMessage());
	}

	@Test
	public void testResourceBundleHasLabelForCannotDeleteIntegrationObjectLinkedWithWebhookConfigException()
	{
		final var ex = new CannotDeleteIntegrationObjectLinkedWithWebhookConfigException(IO_CODE);
		assertThat(getMessageFromResource(ex)).isNotNull();
	}

	@Test
	public void testResourceBundleHasLabelForWebhookConfigNotValidLocationException()
	{
		final var ex = new WebhookConfigNotValidLocationException(FILTER_LOCATION, NO_INTERCEPTOR);
		assertThat(getMessageFromResource(ex)).isNotNull();
	}

	@Test
	public void testResourceBundleHasLabelForWebhookConfigInvalidChannelException()
	{
		final var ex = new WebhookConfigInvalidChannelException(NO_INTERCEPTOR);
		assertThat(getMessageFromResource(ex)).isNotNull();
	}

	@Test
	public void testResourceBundleHasLabelForDestinationTargetNoSupportedEventConfigException()
	{
		final var ex = new DestinationTargetNoSupportedEventConfigException();
		assertThat(getMessageFromResource(ex)).isNotNull();
	}

	@Test
	public void testResourceBundleHasLabelForWebhookConfigNoEventConfigException()
	{
		final var ex = new WebhookConfigNoEventConfigException(NO_INTERCEPTOR);
		assertThat(getMessageFromResource(ex)).isNotNull();
	}

	private String getMessageFromResource(final Throwable ex)
	{
		return prop.getProperty(getResourceKey(ex));
	}

	private String getResourceKey(final Throwable ex)
	{
		return ERROR_MESSAGE_KEY_PREFIX + ex.getClass().getSimpleName();
	}
}
