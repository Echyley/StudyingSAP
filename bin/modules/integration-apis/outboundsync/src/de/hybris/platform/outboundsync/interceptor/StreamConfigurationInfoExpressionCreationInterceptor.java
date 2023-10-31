/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.interceptor;

import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.outboundsync.OutboundSyncFeature;
import de.hybris.platform.outboundsync.config.impl.OutboundSyncConfiguration;
import de.hybris.platform.outboundsync.job.InfoExpressionGenerator;
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel;
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.LoadInterceptor;
import de.hybris.platform.servicelayer.interceptor.PersistenceOperation;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Creates an info expression for a newly created {@link OutboundSyncStreamConfigurationModel} based on the {@link TypeDescriptor}
 * derived from the associated {@link IntegrationObjectModel} associated via the {@link OutboundChannelConfigurationModel}
 */
public class StreamConfigurationInfoExpressionCreationInterceptor implements LoadInterceptor<OutboundSyncStreamConfigurationModel>
{
	private final DescriptorFactory descriptorFactory;
	private final InfoExpressionGenerator infoExpressionGenerator;
	private final OutboundSyncConfiguration outboundSyncConfiguration;

	/**
	 * Constructor that instantiates and populates the required class dependencies
	 *
	 * @param factory   {@link DescriptorFactory}
	 * @param generator {@link InfoExpressionGenerator}
	 */
	public StreamConfigurationInfoExpressionCreationInterceptor(@NotNull final DescriptorFactory factory,
	                                                            @NotNull final InfoExpressionGenerator generator,
	                                                            @NotNull final OutboundSyncConfiguration configuration)
	{
		Preconditions.checkArgument(factory != null, "DescriptorFactory cannot be null");
		Preconditions.checkArgument(generator != null, "InfoExpressionGenerator cannot be null");
		Preconditions.checkArgument(configuration != null, "OutboundSyncConfiguration cannot be null");

		descriptorFactory = factory;
		infoExpressionGenerator = generator;
		outboundSyncConfiguration = configuration;
	}

	@Override
	public void onLoad(final OutboundSyncStreamConfigurationModel streamModel, final InterceptorContext context)
	{
		if (streamModel.getOutboundChannelConfiguration() != null
				&& isDeleteFeatureEnabled()
				&& outboundSyncConfiguration.isInfoGenerationEnabledForStream(streamModel))
		{
			final String infoExpression = calculateInfoExpression(streamModel);
			streamModel.setInfoExpression(infoExpression);
			context.registerElementFor(streamModel, PersistenceOperation.SAVE);
		}
	}

	// Remove when feature is fully implemented
	protected boolean isDeleteFeatureEnabled()
	{
		return OutboundSyncFeature.DELETE.isEnabled();
	}

	private String calculateInfoExpression(final OutboundSyncStreamConfigurationModel streamModel)
	{
		final Optional<TypeDescriptor> typeDescriptor = findTypeDescriptor(streamModel);
		return infoExpressionGenerator.generateInfoExpression(typeDescriptor.orElse(null));
	}

	private Optional<TypeDescriptor> findTypeDescriptor(final OutboundSyncStreamConfigurationModel stream)
	{
		final IntegrationObjectModel io = stream.getOutboundChannelConfiguration().getIntegrationObject();
		final IntegrationObjectDescriptor ioDescriptor = descriptorFactory.createIntegrationObjectDescriptor(io);
		return ioDescriptor.getTypeDescriptor(stream.getItemTypeForStream());
	}
}
