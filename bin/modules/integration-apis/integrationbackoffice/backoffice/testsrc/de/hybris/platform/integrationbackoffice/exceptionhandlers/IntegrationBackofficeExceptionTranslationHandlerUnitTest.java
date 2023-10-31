/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.exceptionhandlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.inboundservices.exceptions.CannotCreateIntegrationClientCredentialsDetailWithAdminException;
import de.hybris.platform.inboundservices.exceptions.CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException;
import de.hybris.platform.integrationbackoffice.exceptions.ExportConfigurationEntityNotSelectedException;
import de.hybris.platform.integrationbackoffice.exceptions.ExportConfigurationModelNotFoundException;
import de.hybris.platform.integrationbackoffice.exceptions.ModelingAbstractAttributeAutoCreateOrPartOfException;
import de.hybris.platform.integrationbackoffice.localization.LocalizationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class IntegrationBackofficeExceptionTranslationHandlerUnitTest
{
	private static final Object[] NO_MSG_PARAMETERS = {};
	private static final String EXCEPTION_LOCALIZED_MESSAGE = "Localized message";
	private static final Throwable SOME_EXCEPTION = createThrowableWithLocalizedMessage();

	@Mock
	private LocalizationService localizationService;
	@InjectMocks
	private IntegrationBackofficeExceptionTranslationHandler exceptionHandler;

	@Test
	public void testCanHandleCannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException()
	{
		final var exception = new CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException("does not matter");

		assertThat(exceptionHandler.canHandle(exception))
				.as("CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException itself is not handled")
				.isTrue();
		final var asNestedException = new Throwable(exception);
		assertThat(exceptionHandler.canHandle(asNestedException))
				.as("nested CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException is not handled")
				.isTrue();
		final var asDeeplyNestedException = new Throwable(asNestedException);
		assertThat(exceptionHandler.canHandle(asDeeplyNestedException))
				.as("deeply nested CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException is not handled")
				.isTrue();
	}

	@Test
	public void testCanHandleExportConfigurationModelNotFoundException()
	{
		final var exception = new ExportConfigurationModelNotFoundException(new ModelNotFoundException("does not matter"));

		assertThat(exceptionHandler.canHandle(exception))
				.as("ExportConfigurationModelNotFoundException itself is not handled")
				.isTrue();
		final var asNestedException = new Throwable(exception);
		assertThat(exceptionHandler.canHandle(asNestedException))
				.as("nested ExportConfigurationModelNotFoundException is not handled")
				.isTrue();
		final var asDeeplyNestedException = new Throwable(asNestedException);
		assertThat(exceptionHandler.canHandle(asDeeplyNestedException))
				.as("deeply nested ExportConfigurationModelNotFoundException is not handled")
				.isTrue();
	}

	@Test
	public void testCanHandleExportConfigurationEntityNotSelectedException()
	{
		final var exception = new ExportConfigurationEntityNotSelectedException();

		assertThat(exceptionHandler.canHandle(exception))
				.as("ExportConfigurationEntityNotSelectedException itself is not handled")
				.isTrue();
		final var asNestedException = new Throwable(exception);
		assertThat(exceptionHandler.canHandle(asNestedException))
				.as("nested ExportConfigurationEntityNotSelectedException is not handled")
				.isTrue();
		final var asDeeplyNestedException = new Throwable(asNestedException);
		assertThat(exceptionHandler.canHandle(asDeeplyNestedException))
				.as("deeply nested ExportConfigurationEntityNotSelectedException is not handled")
				.isTrue();
	}

	@Test
	public void testCanHandleCannotCreateIntegrationClientCredentialsDetailWithAdminException()
	{
		final var exception = new CannotCreateIntegrationClientCredentialsDetailWithAdminException();

		assertThat(exceptionHandler.canHandle(exception))
				.as("CannotCreateIntegrationClientCredentialsDetailWithAdminException itself is not handled")
				.isTrue();
		final var asNestedException = new Throwable(exception);
		assertThat(exceptionHandler.canHandle(asNestedException))
				.as("nested CannotCreateIntegrationClientCredentialsDetailWithAdminException is not handled")
				.isTrue();
		final var asDeeplyNestedException = new Throwable(asNestedException);
		assertThat(exceptionHandler.canHandle(asDeeplyNestedException))
				.as("deeply nested CannotCreateIntegrationClientCredentialsDetailWithAdminException is not handled")
				.isTrue();
	}

	@Test
	public void testCanHandleModelingAbstractAttributeAutoCreateOrPartOfException()
	{
		final var exception = new ModelingAbstractAttributeAutoCreateOrPartOfException(
				new Throwable(), "type - does not matter", "attribute - does not matter");

		assertThat(exceptionHandler.canHandle(exception))
				.as("ModelingAbstractAttributeAutoCreateOrPartOfException itself is not handled")
				.isTrue();
		final var asNestedException = new Throwable(exception);
		assertThat(exceptionHandler.canHandle(asNestedException))
				.as("nested ModelingAbstractAttributeAutoCreateOrPartOfException is not handled")
				.isTrue();
		final var asDeeplyNestedException = new Throwable(asNestedException);
		assertThat(exceptionHandler.canHandle(asDeeplyNestedException))
				.as("deeply nested ModelingAbstractAttributeAutoCreateOrPartOfException is not handled")
				.isTrue();
	}

	@Test
	public void testShowsUnparameterizedMessageForAnyExceptionIfItIsFoundByTheLocalizationService()
	{
		doReturn("Message from localization service").when(localizationService)
		                                             .getLocalizedString(getResourceBundleKey(SOME_EXCEPTION), NO_MSG_PARAMETERS);

		final String localizedMessage = exceptionHandler.toString(SOME_EXCEPTION);

		assertThat(localizedMessage)
				.isEqualTo("Message from localization service")
				.isNotEqualTo(SOME_EXCEPTION.getLocalizedMessage());
	}

	@Test
	public void testShowsExceptionLocalizedMessageWhenLocalizationServiceReturnsBlankValueForTheExceptionKey()
	{
		doReturn(" ").when(localizationService).getLocalizedString(getResourceBundleKey(SOME_EXCEPTION), NO_MSG_PARAMETERS);

		final String localizedMessage = exceptionHandler.toString(SOME_EXCEPTION);

		assertThat(localizedMessage).isEqualTo(SOME_EXCEPTION.getLocalizedMessage());
	}

	@Test
	public void testShowsExceptionLocalizedMessageWhenLocalizationServiceReturnsNullValueForTheExceptionKey()
	{
		doReturn(null).when(localizationService).getLocalizedString(getResourceBundleKey(SOME_EXCEPTION), NO_MSG_PARAMETERS);

		final String localizedMessage = exceptionHandler.toString(SOME_EXCEPTION);

		assertThat(localizedMessage).isEqualTo(SOME_EXCEPTION.getLocalizedMessage());
	}

	@Test
	public void testShowsExceptionLocalizedMessageWhenLocalizationServiceReturnsKeyValueForTheExceptionKey()
	{
		final String bundleKey = getResourceBundleKey(SOME_EXCEPTION);
		doReturn(bundleKey).when(localizationService).getLocalizedString(bundleKey, NO_MSG_PARAMETERS);

		final String localizedMessage = exceptionHandler.toString(SOME_EXCEPTION);

		assertThat(localizedMessage).isEqualTo(SOME_EXCEPTION.getLocalizedMessage());
	}

	@Test
	public void testShowsLocalizedMessageInAnyExceptionIfLocalizationServiceReturnsTheBundleKeyValue()
	{
		final String resourceBundleKey = getResourceBundleKey(SOME_EXCEPTION);
		doReturn(resourceBundleKey).when(localizationService).getLocalizedString(resourceBundleKey, NO_MSG_PARAMETERS);

		final String localizedMessage = exceptionHandler.toString(SOME_EXCEPTION);

		assertThat(localizedMessage).isEqualTo(SOME_EXCEPTION.getLocalizedMessage());
	}

	@Test
	public void testModelingAbstractAttributeAutoCreateOrPartOfExceptionIsParameterizedInTheLocalizedMessage()
	{
		final Object[] msgParameters = { "ItemType", "itemAttribute" };
		final var ex = new ModelingAbstractAttributeAutoCreateOrPartOfException(
				SOME_EXCEPTION, (String) msgParameters[0], (String) msgParameters[1]);
		doReturn("Message with parameters included").when(localizationService)
		                                            .getLocalizedString(getResourceBundleKey(ex), msgParameters);

		final String localizedMessage = exceptionHandler.toString(ex);

		assertThat(localizedMessage).isEqualTo("Message with parameters included");
	}

	@Test
	public void testKnownExceptionMessagesAreUsedEvenWhenTheyAreNestedInOtherExceptions()
	{
		final var knownTargetException = new ExportConfigurationEntityNotSelectedException();
		final var outerException = new Throwable(knownTargetException);
		doReturn("Message for the known exception").when(localizationService)
		                                            .getLocalizedString(getResourceBundleKey(knownTargetException), NO_MSG_PARAMETERS);

		final String localizedMessage = exceptionHandler.toString(outerException);

		assertThat(localizedMessage).isEqualTo("Message for the known exception");
	}

	private static String getResourceBundleKey(final Throwable ex)
	{
		return "integrationbackoffice.exceptionTranslation.msg." + ex.getClass().getSimpleName();
	}

	private static Throwable createThrowableWithLocalizedMessage()
	{
		final var ex = Mockito.mock(Throwable.class);
		Mockito.lenient().doReturn(EXCEPTION_LOCALIZED_MESSAGE).when(ex).getLocalizedMessage();
		return ex;
	}
}