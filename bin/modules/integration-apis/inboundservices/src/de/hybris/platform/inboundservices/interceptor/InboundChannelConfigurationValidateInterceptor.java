/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.inboundservices.interceptor;

import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.apiregistryservices.model.BasicCredentialModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.integrationservices.enums.AuthenticationType;
import de.hybris.platform.integrationservices.model.InboundChannelConfigurationModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import org.apache.commons.collections.CollectionUtils;

/**
 * An interceptor to validate {@link AuthenticationType} of {@link InboundChannelConfigurationModel}
 * matches assigned credential type of {@link ExposedDestinationModel}
 */
public class InboundChannelConfigurationValidateInterceptor implements ValidateInterceptor<InboundChannelConfigurationModel>
{
	private static final String ERROR_MESSAGE =
			"Authentication Type [%s] of InboundChannelConfiguration does not match assigned credential type [%s] of "
					+ "ExposedDestinationModel [%s] ";

	@Override
	public void onValidate(final InboundChannelConfigurationModel configuration,
	                       final InterceptorContext ctx) throws InterceptorException
	{
		final var destinations = configuration.getExposedDestinations();

		if (CollectionUtils.isNotEmpty(destinations))
		{
			final var iccAuthType = configuration.getAuthenticationType();
			final var notMatchedDestination = destinations.stream()
			                                              .filter(ex -> !isMatchedCredentials(ex.getCredential(), iccAuthType))
			                                              .findAny();

			if (notMatchedDestination.isPresent())
			{
				final var destination = notMatchedDestination.get();

				throw new InterceptorException(
						String.format(ERROR_MESSAGE, iccAuthType, destination.getCredential().getItemtype(),
								destination.getId()));
			}
		}
	}

	private boolean isMatchedCredentials(final AbstractCredentialModel destinationCredential,
	                                  final AuthenticationType iccAuthenticationType)
	{
		return isMatchedBasicCredentials(destinationCredential, iccAuthenticationType)
				|| isMatchedOAuthCredentials(destinationCredential, iccAuthenticationType);
	}

	private boolean isMatchedBasicCredentials(final AbstractCredentialModel destinationCredential,
	                                          final AuthenticationType iccAuthenticationType)
	{
		return destinationCredential instanceof BasicCredentialModel && AuthenticationType.BASIC == iccAuthenticationType;
	}

	private boolean isMatchedOAuthCredentials(final AbstractCredentialModel destinationCredential,
	                                          final AuthenticationType iccAuthenticationType)
	{
		return destinationCredential instanceof ExposedOAuthCredentialModel && AuthenticationType.OAUTH == iccAuthenticationType;
	}
}
