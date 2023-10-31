/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.outboundservices.ConsumedDestinationBuilder;
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.rules.ExternalResource;

import com.google.common.base.Preconditions;

public class OutboundChannelConfigurationBuilder extends ExternalResource
{
	private final Set<String> createdChannelCodes = new HashSet<>();
	private final Set<ConsumedDestinationBuilder> createdDestinations = new HashSet<>();
	private final Set<IntegrationObjectModelBuilder> createdIntegrationObjects = new HashSet<>();
	private String code;
	private String integrationObjectCode;
	private IntegrationObjectModelBuilder integrationObjectBuilder;
	private ConsumedDestinationModel destination;
	private ConsumedDestinationBuilder destinationBuilder;
	private boolean autoGenerate = false;
	private boolean syncDeletes = false;
	private Boolean batch = null;

	public static OutboundChannelConfigurationBuilder outboundChannelConfigurationBuilder()
	{
		return new OutboundChannelConfigurationBuilder();
	}

	public OutboundChannelConfigurationBuilder withCode(final String code)
	{
		this.code = code;
		return this;
	}

	public OutboundChannelConfigurationBuilder withIntegrationObjectCode(final String ioCode)
	{
		integrationObjectCode = ioCode;
		integrationObjectBuilder = null;
		return this;
	}

	public OutboundChannelConfigurationBuilder withIntegrationObject(final IntegrationObjectModelBuilder builder)
	{
		integrationObjectBuilder = builder;
		integrationObjectCode = null;
		return this;
	}

	public OutboundChannelConfigurationBuilder withConsumedDestination(final ConsumedDestinationBuilder builder)
	{
		destinationBuilder = builder;
		destination = null;
		return this;
	}

	public OutboundChannelConfigurationBuilder withConsumedDestination(final ConsumedDestinationModel destination)
	{
		this.destination = destination;
		destinationBuilder = null;
		return this;
	}

	public OutboundChannelConfigurationBuilder withAutoGenerate()
	{
		autoGenerate = true;
		return this;
	}

	public OutboundChannelConfigurationBuilder withBatch()
	{
		batch = true;
		return this;
	}

	public OutboundChannelConfigurationBuilder withoutAutoGenerate()
	{
		autoGenerate = false;
		return this;
	}

	public OutboundChannelConfigurationBuilder withoutBatch()
	{
		batch = false;
		return this;
	}

	public OutboundChannelConfigurationBuilder withDeleteSynchronization()
	{
		syncDeletes = true;
		return this;
	}

	public OutboundChannelConfigurationBuilder withoutDeleteSynchronization()
	{
		syncDeletes = false;
		return this;
	}

	public OutboundChannelConfigurationModel build() throws ImpExException
	{
		Preconditions.checkState(code != null, "code cannot be null");

		final var io = deriveIntegrationObject();
		final var dest = deriveDestination();

		var impexHeader = "INSERT_UPDATE OutboundChannelConfiguration; code[unique = true]; integrationObject ; destination         ; synchronizeDelete  ; autoGenerate";
		var impexBody = "                                            ; " + code + "       ; " + io.getPk() + "; " + dest.getPk() + "; " + syncDeletes + "; " + autoGenerate;
		if (batch != null)
		{
			impexHeader += "; batch";
			impexBody = impexBody + "; " + batch;
		}
		importImpEx(impexHeader, impexBody);
		createdChannelCodes.add(code);
		return getOutboundChannelConfigurationByCode(code);
	}

	private IntegrationObjectModel deriveIntegrationObject()
	{
		return integrationObjectCode == null
				? buildIntegrationObject()
				: IntegrationTestUtil.findAny(IntegrationObjectModel.class, it -> it.getCode().equals(integrationObjectCode))
				                     .orElse(null);
	}

	private IntegrationObjectModel buildIntegrationObject()
	{
		Preconditions.checkState(integrationObjectBuilder != null, "integrationObject must be specified");
		createdIntegrationObjects.add(integrationObjectBuilder);
		return integrationObjectBuilder.build();
	}

	private ConsumedDestinationModel deriveDestination()
	{
		return destination != null ? destination : buildDestination();
	}

	private ConsumedDestinationModel buildDestination()
	{
		Preconditions.checkArgument(destinationBuilder != null, "destination cannot be null");
		createdDestinations.add(destinationBuilder);
		withConsumedDestination(destinationBuilder.build());
		return destination;
	}

	public static OutboundChannelConfigurationModel getOutboundChannelConfigurationByCode(final String code)
	{
		return IntegrationTestUtil.findAny(OutboundChannelConfigurationModel.class, it -> it.getCode().equals(code))
		                          .orElse(null);
	}

	@Override
	protected void before() throws ImpExException
	{
		build();
	}

	@Override
	protected void after()
	{
		cleanup();
	}

	public void cleanup()
	{
		IntegrationTestUtil.remove(OutboundChannelConfigurationModel.class, it -> createdChannelCodes.contains(it.getCode()));
		createdChannelCodes.clear();

		createdDestinations.forEach(ConsumedDestinationBuilder::reset);
		createdDestinations.clear();

		createdIntegrationObjects.forEach(IntegrationObjectModelBuilder::cleanup);
		createdIntegrationObjects.clear();
	}
}
