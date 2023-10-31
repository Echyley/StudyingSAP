/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices;

import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;
import de.hybris.platform.apiregistryservices.model.EndpointModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.rules.ExternalResource;

/**
 * Consumed destination builder to build a {@link ConsumedDestinationModel}.
 */
public class ConsumedDestinationBuilder extends ExternalResource
{
	private static final EndpointBuilder DEFAULT_ENDPOINT_BUILDER = EndpointBuilder.endpointBuilder();
	private static final DestinationTargetBuilder DEFAULT_TARGET_BUILDER = DestinationTargetBuilder.destinationTarget();
	private static final String DEFAULT_ID = "test-destination";
	private static final String DEFAULT_URL = "https://test.url.that.does.not.matter";
	private final Set<String> createdDestinationIds = new HashSet<>();
	private final Set<EndpointBuilder> createdEndpoints = new HashSet<>();
	private final Set<AbstractCredentialBuilder<?, ?>> createdCredentials = new HashSet<>();
	private final Set<DestinationTargetBuilder> createdTargets = new HashSet<>();
	private final Map<Object, Object> additionalParameters = new HashMap<>();
	private DestinationTargetBuilder targetBuilder;
	private EndpointBuilder endpointBuilder;
	private AbstractCredentialBuilder<?, ?> credentialBuilder;
	private String id;
	private String url;
	private EndpointModel endpoint;
	private AbstractCredentialModel credential;
	private DestinationTargetModel destinationTarget;

	public static ConsumedDestinationBuilder consumedDestinationBuilder()
	{
		return new ConsumedDestinationBuilder();
	}

	public ConsumedDestinationBuilder withId(final String id)
	{
		this.id = id;
		return this;
	}

	public ConsumedDestinationBuilder withUrl(final String url)
	{
		this.url = url;
		return this;
	}

	public ConsumedDestinationBuilder withEndpoint(final EndpointBuilder builder)
	{
		endpointBuilder = builder;
		endpoint = null;
		return this;
	}

	public ConsumedDestinationBuilder withEndpoint(final EndpointModel endpoint)
	{
		this.endpoint = endpoint;
		endpointBuilder = null;
		return this;
	}

	public ConsumedDestinationBuilder withCredential(final AbstractCredentialBuilder<?, ?> builder)
	{
		credentialBuilder = builder;
		credential = null;
		return this;
	}

	public ConsumedDestinationBuilder withCredential(final AbstractCredentialModel credential)
	{

		this.credential = credential;
		credentialBuilder = null;
		return this;
	}

	public ConsumedDestinationBuilder withDestinationTarget(final String id)
	{
		return IntegrationTestUtil.findAny(DestinationTargetModel.class,
				destinationTarget -> destinationTarget.getId().equals(id))
		                          .map(this::withDestinationTarget)
		                          .orElseThrow(() -> new IllegalArgumentException(
				                          String.format("Destination target with ID %s not found", id)));
	}

	public ConsumedDestinationBuilder withDestinationTarget(final DestinationTargetBuilder builder)
	{
		targetBuilder = builder;
		destinationTarget = null;
		return this;
	}

	public ConsumedDestinationBuilder withDestinationTarget(final DestinationTargetModel destination)
	{
		destinationTarget = destination;
		targetBuilder = null;
		return this;
	}

	/**
	 * Specifies additional parameters to be added to the consumed destination. Subsequent calls to this method do not reset the parameters
	 * previously specified.
	 *
	 * @param params the parameter map.
	 * @return a builder with the parameters specified.
	 */
	public ConsumedDestinationBuilder withAdditionalParameters(final Map<String, String> params)
	{

		if (Objects.nonNull(params))
		{
			additionalParameters.putAll(params);
		}

		return this;
	}

	public ConsumedDestinationModel build()
	{
		final String idVal = deriveId();
		final AbstractCredentialModel derivedCredential = deriveCredential();
		final String credentialHeader = Objects.nonNull(derivedCredential) ? "; credential" : "";
		final String credentialValue = Objects.nonNull(derivedCredential) ? "; " + derivedCredential.getPk().toString() : "";
		try
		{
			IntegrationTestUtil.importImpEx(
					"INSERT_UPDATE ConsumedDestination; id[unique = true]; url                ; endpoint             " + credentialHeader + "; destinationTarget               ; additionalProperties(key, value)[map-delimiter = |]",
					"                                        ; " + idVal + "    ; " + deriveUrl() + "; " + deriveEndpoint().getPk() + credentialValue + ";" + deriveTarget()
							.getPk() + "; " + serializeParameters());
		}
		catch (final ImpExException ex)
		{
			throw new RuntimeException(ex);
		}

		createdDestinationIds.add(idVal);
		return getConsumedDestinationById(idVal);
	}

	private String deriveId()
	{
		return StringUtils.isNotBlank(id) ? id : DEFAULT_ID;
	}

	private String deriveUrl()
	{
		return StringUtils.isNotBlank(url) ? url : DEFAULT_URL;
	}

	private EndpointModel deriveEndpoint()
	{
		return Objects.nonNull(endpoint) ? endpoint : buildEndpoint();
	}

	private EndpointModel buildEndpoint()
	{
		final EndpointBuilder builder = Objects.nonNull(endpointBuilder) ? endpointBuilder : DEFAULT_ENDPOINT_BUILDER;
		createdEndpoints.add(builder);
		return builder.build();
	}

	private AbstractCredentialModel deriveCredential()
	{
		return Objects.nonNull(credential) ? credential : buildCredentials();
	}

	private AbstractCredentialModel buildCredentials()
	{
		if (Objects.nonNull(credentialBuilder))
		{
			createdCredentials.add(credentialBuilder);
			return credentialBuilder.build();
		}
		return null;
	}

	private DestinationTargetModel deriveTarget()
	{
		return Objects.nonNull(destinationTarget) ? destinationTarget : buildTarget();
	}

	private DestinationTargetModel buildTarget()
	{
		final DestinationTargetBuilder builder = Objects.nonNull(targetBuilder) ? targetBuilder : DEFAULT_TARGET_BUILDER;
		createdTargets.add(builder);
		try
		{
			return builder.build();
		}
		catch (final ImpExException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private String serializeParameters()
	{
		return additionalParameters.entrySet()
		                           .stream()
		                           .map(entry -> entry.getKey() + "->" + entry.getValue())
		                           .collect(Collectors.joining("|"));
	}

	private static ConsumedDestinationModel getConsumedDestinationById(final String id)
	{
		return IntegrationTestUtil.findAny(ConsumedDestinationModel.class,
				consumedDestination -> consumedDestination.getId().equals(id))
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
		reset();
	}

	public void reset()
	{
		createdDestinationIds.forEach(id -> IntegrationTestUtil.remove(ConsumedDestinationModel.class,
				consumedDestination -> consumedDestination.getId().equals(id)));
		createdDestinationIds.clear();

		createdCredentials.forEach(AbstractCredentialBuilder::cleanup);
		createdCredentials.clear();

		createdEndpoints.forEach(EndpointBuilder::cleanup);
		createdEndpoints.clear();

		createdTargets.forEach(DestinationTargetBuilder::cleanup);
		createdTargets.clear();
	}
}
