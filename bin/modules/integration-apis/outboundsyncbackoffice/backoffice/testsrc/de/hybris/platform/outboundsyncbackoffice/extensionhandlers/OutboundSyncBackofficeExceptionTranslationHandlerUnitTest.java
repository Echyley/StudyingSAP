/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsyncbackoffice.extensionhandlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationbackoffice.localization.LocalizationService;
import de.hybris.platform.outboundsync.exceptions.CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OutboundSyncBackofficeExceptionTranslationHandlerUnitTest
{
	private static final String IO_CODE = "some IO";
	private static final String OCC_CODE = "some outbound channel config";
	private static final Object[] NO_MSG_PARAMETERS = {};
	private static final Object[] MSG_PARAMETERS = {OCC_CODE};
	private static final Throwable SOME_EXCEPTION = createThrowableWithLocalizedMessage();
	private static final String RESOURCE_BUNDLE_MESSAGE = "Parameterized localized message from localization service";

	@Mock
	private LocalizationService localizationService;
	@InjectMocks
	private OutboundSyncBackofficeExceptionTranslationHandler exceptionHandler;

	@Test
	public void testCanHandleCannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException()
	{
		final var exception = new CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException(IO_CODE, OCC_CODE);

		assertThat(exceptionHandler.canHandle(exception))
				.as("CannotDeleteIntegrationObjectLinkedWithInboundChannelConfigException itself is not handled")
				.isTrue();
		final var asNestedException = new Throwable(exception);
		assertThat(exceptionHandler.canHandle(asNestedException))
				.as("nested CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException is not handled")
				.isTrue();
		final var asDeeplyNestedException = new Throwable(asNestedException);
		assertThat(exceptionHandler.canHandle(asDeeplyNestedException))
				.as("deeply nested CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException is not handled")
				.isTrue();
	}

	@Test
	public void testShowsUnparameterizedMessageForAnyExceptionIfItIsFoundByTheLocalizationService()
	{
		doReturn(RESOURCE_BUNDLE_MESSAGE).when(localizationService)
		                                 .getLocalizedString(getResourceBundleKey(SOME_EXCEPTION), NO_MSG_PARAMETERS);

		final String localizedMessage = exceptionHandler.toString(SOME_EXCEPTION);

		assertThat(localizedMessage)
				.isEqualTo(RESOURCE_BUNDLE_MESSAGE)
				.isNotEqualTo(SOME_EXCEPTION.getLocalizedMessage());
	}

	@Test
	public void testShowsLocalizedMessageInAnyExceptionIfLocalizationServiceReturnedBlankValue()
	{
		doReturn(" ").when(localizationService).getLocalizedString(getResourceBundleKey(SOME_EXCEPTION), NO_MSG_PARAMETERS);

		final String localizedMessage = exceptionHandler.toString(SOME_EXCEPTION);

		assertThat(localizedMessage).isEqualTo(SOME_EXCEPTION.getLocalizedMessage());
	}

	@Test
	public void testShowsLocalizedMessageInAnyExceptionIfLocalizationServiceReturnedNull()
	{
		doReturn(null).when(localizationService).getLocalizedString(getResourceBundleKey(SOME_EXCEPTION), NO_MSG_PARAMETERS);

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
	public void testCannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigExceptionIsParameterizedInTheLocalizedMessage()
	{
		final var ex = new CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException(IO_CODE, OCC_CODE);
		doReturn(RESOURCE_BUNDLE_MESSAGE).when(localizationService)
		                                            .getLocalizedString(getResourceBundleKey(ex), MSG_PARAMETERS);

		final String localizedMessage = exceptionHandler.toString(ex);

		assertThat(localizedMessage).isEqualTo(RESOURCE_BUNDLE_MESSAGE);
	}

	@Test
	public void testKnownExceptionMessagesAreUsedEvenWhenTheyAreNestedInOtherExceptions()
	{
		final var knownTargetException = new CannotDeleteIntegrationObjectLinkedWithOutboundChannelConfigException(IO_CODE, OCC_CODE);
		final var outerException = new Throwable(knownTargetException);
		doReturn(RESOURCE_BUNDLE_MESSAGE).when(localizationService)
		                                           .getLocalizedString(getResourceBundleKey(knownTargetException), MSG_PARAMETERS);

		final String localizedMessage = exceptionHandler.toString(outerException);

		assertThat(localizedMessage).isEqualTo(RESOURCE_BUNDLE_MESSAGE);
	}

	private static String getResourceBundleKey(final Throwable ex)
	{
		return "outboundsyncbackoffice.exceptionTranslation.msg." + ex.getClass().getSimpleName();
	}

	private static Throwable createThrowableWithLocalizedMessage()
	{
		final var ex = Mockito.mock(Throwable.class);
		Mockito.lenient().doReturn("Localized message").when(ex).getLocalizedMessage();
		return ex;
	}
}