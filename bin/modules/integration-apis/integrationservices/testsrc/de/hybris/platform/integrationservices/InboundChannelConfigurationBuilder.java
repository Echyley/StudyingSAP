/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices;

import de.hybris.platform.core.PK;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.enums.AuthenticationType;
import de.hybris.platform.integrationservices.model.InboundChannelConfigurationModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;

import java.util.HashSet;
import java.util.Set;

import org.junit.rules.ExternalResource;

public class InboundChannelConfigurationBuilder extends ExternalResource
{
	private final Set<PK> createdChannelsForIOs;
	private final Set<IntegrationObjectModelBuilder> createdIntegrationObjects;
	private IntegrationObjectModelBuilder integrationObjectBuilder;
	private String integrationObjectCode;
	private AuthenticationType authType;

	private InboundChannelConfigurationBuilder()
	{
		createdChannelsForIOs = new HashSet<>();
		createdIntegrationObjects = new HashSet<>();
	}

	public static InboundChannelConfigurationBuilder inboundChannelConfigurationBuilder()
	{
		return new InboundChannelConfigurationBuilder();
	}

	public InboundChannelConfigurationBuilder withIntegrationObject(final IntegrationObjectModelBuilder builder)
	{
		integrationObjectBuilder = builder;
		integrationObjectCode = null;
		return this;
	}

	public InboundChannelConfigurationBuilder withIntegrationObjectCode(final String code)
	{
		integrationObjectCode = code;
		integrationObjectBuilder = null;
		return this;
	}

	public InboundChannelConfigurationBuilder withAuthType(final AuthenticationType authType)
	{
		this.authType = authType;
		return this;
	}

	public void build() throws ImpExException
	{
		final var ioPk = deriveIntegrationObject().getPk();
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE InboundChannelConfiguration; integrationObject[unique = true]; authenticationType(code)",
				String.format("                           ; %s                              ; %s", ioPk, authType));
		createdChannelsForIOs.add(ioPk);
	}

	private IntegrationObjectModel deriveIntegrationObject()
	{
		return integrationObjectCode != null
				? findExistingIntegrationObject()
				: buildIntegrationObject();
	}

	private IntegrationObjectModel findExistingIntegrationObject()
	{
		return IntegrationTestUtil.findAny(IntegrationObjectModel.class, m -> m.getCode().equals(integrationObjectCode))
		                          .orElse(null);
	}

	private IntegrationObjectModel buildIntegrationObject()
	{
		if (integrationObjectBuilder == null)
		{
			throw new IllegalStateException("Integration object not specified");
		}
		createdIntegrationObjects.add(integrationObjectBuilder);
		return integrationObjectBuilder.build();
	}

	public void cleanup()
	{
		createdChannelsForIOs.forEach(pk ->
				IntegrationTestUtil.remove(InboundChannelConfigurationModel.class,
						m -> m.getIntegrationObject().getPk().equals(pk)));
		createdChannelsForIOs.clear();
		createdIntegrationObjects.forEach(IntegrationObjectModelBuilder::cleanup);
		createdIntegrationObjects.clear();
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
}
