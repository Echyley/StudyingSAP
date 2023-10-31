/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync;

import de.hybris.deltadetection.ChangeDetectionService;
import de.hybris.deltadetection.ItemChangeDTO;
import de.hybris.deltadetection.StreamConfiguration;
import de.hybris.deltadetection.impl.InMemoryChangesCollector;
import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel;
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationContainerModel;
import de.hybris.platform.outboundsync.model.OutboundSyncStreamConfigurationModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.rules.ExternalResource;

import com.google.common.base.Preconditions;

/**
 * A builder for conveniently creating {@link OutboundSyncStreamConfigurationModel}s in integration tests. If this class is used
 * with {@link org.junit.Rule} annotation, then the created models will be also automatically cleaned up after the test(s) execution.
 */
public final class OutboundSyncStreamConfigurationBuilder extends ExternalResource
{
	private static final String DEFAULT_OUTBOUND_SYNC_CONTAINER = "outboundSyncDataStreams";
	private static final String DEFAULT_WHERE_CLAUSE = "";
	private static final String DEFAULT_INFO_EXPRESSION = "";

	private final List<OutboundSyncStreamConfigurationModel> allChangeStreams = new ArrayList<>();
	private ChangeDetectionService deltaDetect;
	private String streamId;
	private String typeCode;
	private String channelCode;
	private String whereClause = DEFAULT_WHERE_CLAUSE;
	private String infoExpression = DEFAULT_INFO_EXPRESSION;
	private Set<String> excludedTypes = new HashSet<>();
	private OutboundSyncStreamConfigurationContainerModel container;
	private OutboundSyncStreamConfigurationContainerBuilder containerBuilder;
	private OutboundChannelConfigurationBuilder channelBuilder;

	private OutboundSyncStreamConfigurationBuilder()
	{
		// non-instantiable outside of this class
	}

	/**
	 * Creates instance of this builder.
	 *
	 * @return new instance of the builder.
	 */
	public static OutboundSyncStreamConfigurationBuilder outboundSyncStreamConfigurationBuilder()
	{
		return new OutboundSyncStreamConfigurationBuilder();
	}

	/**
	 * Retrieves model of a stream configuration matching the ID.
	 *
	 * @param streamId ID of the stream to find and to retrieve.
	 * @return a stream configuration model matching the ID or {@code null}, if such stream configuration is not found.
	 */
	public static OutboundSyncStreamConfigurationModel getOutboundSyncStreamConfigurationById(final String streamId)
	{
		return IntegrationTestUtil.findAny(OutboundSyncStreamConfigurationModel.class, it -> it.getStreamId().equals(streamId))
		                          .orElse(null);
	}

	/**
	 * Specifies ID for the delta stream to create. If this method is not called, default value of the stream ID will be used,
	 * which is value of the item type code plus "Stream" literal. That is, if {@code "Product"} type code was specified, then
	 * default stream ID will be {@code "ProductStream"}.
	 *
	 * @param id ID value to be used for the stream to create.
	 * @return a builder with the ID specified.
	 * @see #withItemType(String)
	 */
	public OutboundSyncStreamConfigurationBuilder withId(final String id)
	{
		streamId = id;
		return this;
	}

	public OutboundSyncStreamConfigurationBuilder withOutboundSyncStreamConfigurationContainer(
			final OutboundSyncStreamConfigurationContainerModel container)
	{
		this.container = container;
		containerBuilder = null;
		return this;
	}

	public OutboundSyncStreamConfigurationBuilder withOutboundSyncStreamConfigurationContainer(
			final OutboundSyncStreamConfigurationContainerBuilder builder)
	{
		this.containerBuilder = builder;
		container = null;
		return this;
	}

	/**
	 * Specifies type code of the item type to collect changes for in the created delta stream.
	 *
	 * @param code item type code to be associated with the change stream
	 * @return a builder with the type code specified.
	 */
	public OutboundSyncStreamConfigurationBuilder withItemType(final String code)
	{
		typeCode = code;
		return this;
	}

	/**
	 * Specifies outbound channel configuration to be associated with the new stream.
	 *
	 * @param model outbound channel to associate with the stream.
	 * @return a builder with the outbound channel configuration specified.
	 */
	public OutboundSyncStreamConfigurationBuilder withOutboundChannelConfiguration(final OutboundChannelConfigurationModel model)
	{
		return withOutboundChannelCode(model.getCode());
	}

	public OutboundSyncStreamConfigurationBuilder withOutboundChannelConfiguration(
			final OutboundChannelConfigurationBuilder builder)
	{
		this.channelBuilder = builder;
		channelCode = null;
		return this;
	}

	/**
	 * Specifies outbound channel configuration to be associated with the new stream.
	 *
	 * @param code of {@link de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel} to associate with the stream.
	 * @return a builder with the outbound channel configuration specified.
	 */
	public OutboundSyncStreamConfigurationBuilder withOutboundChannelCode(final String code)
	{
		channelCode = code;
		channelBuilder = null;
		return this;
	}

	/**
	 * Specifies filter condition to be used for collecting changes in the stream. Once applied, only changes for items matching
	 * the condition will be reported by the stream; all other item changes will be ignored. Filtering is peformed at the database
	 * level.
	 * <p>This setting does not persist between {@code build()} calls and must be explicitly specified for each {@code build()}
	 * invocation.</p>
	 *
	 * @param expr a filter condition expression to be applied to the item changes reported by the stream. Changed item can be
	 *             referenced in the condition as {@code "{item}"}. For example, if an item has {@code code} attribute,
	 *             then condition for that attribute may look like this: {@code "{item.code}!=''"}.
	 * @return a builder with the filter condition specified.
	 */
	public OutboundSyncStreamConfigurationBuilder withWhereClause(final String expr)
	{
		whereClause = expr;
		return this;
	}

	/**
	 * Specifies info expression to be calculated on the changed items in the stream. This setting does not persist between
	 * {@code build()} calls and must be explicitly specified for each {@code build()} invocation.
	 *
	 * @param expr a Groovy expression that will be applied to the changed item; and result of that calculation will be reported
	 *             in the created stream's change DTOs. The item, to which the expression is being applied, is implied in the
	 *             expression and does not need to be explicitly present. Any methods/properties of the items can be referred to
	 *             in Groovy style. For example, if item type has {@code code} and {@code name} properties and we want to capture
	 *             their values as a Groovy map style string, the expression may look like:
	 *             {@code "[code: '#{getCode()', name: '#{getName()}'}]"}.
	 * @return a builder with the info expression specified.
	 */
	public OutboundSyncStreamConfigurationBuilder withInfoExpression(final String expr)
	{
		infoExpression = expr != null ? expr : "";
		return this;
	}

	public OutboundSyncStreamConfigurationBuilder withExcludedTypes(final Set<String> types)
	{
		excludedTypes = types;
		return this;
	}

	/**
	 * Creates a stream configuration based on the specifications done before calling this method.
	 *
	 * @return stream configuration created in this method or {@code null}, if the created stream was not found in the database.
	 * @throws ImpExException if this builder failed to create the stream.
	 */
	public OutboundSyncStreamConfigurationModel build() throws ImpExException
	{
		final var sid = streamId != null ? streamId : typeCode + "Stream";
		final var excTypes = String.join(",", excludedTypes);
		final String derivedContainerId = deriveContainerId();
		final String derivedChannelCode = deriveChannelCode();

		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE OutboundSyncStreamConfiguration; streamId[unique = true]; container(id); itemTypeForStream(code); outboundChannelConfiguration(code); whereClause        ; infoExpression       ; excludedTypes(code)",
				"                                                    ; " + sid + "            ; " + derivedContainerId + "; " + typeCode + "       ; " + derivedChannelCode + "               ; " + whereClause + ";" + infoExpression + ";" + excTypes);
		resetAfterBuild();
		final var createdStream = getOutboundSyncStreamConfigurationById(sid);
		if (createdStream != null)
		{
			allChangeStreams.add(createdStream);
			consumeChangesInStream(createdStream);
		}
		return createdStream;
	}

	private String deriveChannelCode() throws ImpExException
	{
		return channelCode != null ? channelCode : buildChannelCode();
	}

	private String buildChannelCode() throws ImpExException
	{
		Preconditions.checkArgument(channelBuilder != null, "channel cannot be null");
		return channelBuilder.build().getCode();
	}

	private String deriveContainerId() throws ImpExException
	{
		final var derivedContainer = container != null ? container : buildContainer();
		return derivedContainer != null ? derivedContainer.getId() : DEFAULT_OUTBOUND_SYNC_CONTAINER;
	}

	private OutboundSyncStreamConfigurationContainerModel buildContainer() throws ImpExException
	{
		return containerBuilder != null ? containerBuilder.build() : null;
	}

	private void resetAfterBuild()
	{
		infoExpression = DEFAULT_INFO_EXPRESSION;
		whereClause = DEFAULT_WHERE_CLAUSE;
	}

	/**
	 * Retrieves last stream configuration created by this builder.
	 *
	 * @return the last created stream configuration.
	 * @throws IllegalStateException if the {@link #build()} method has not been called yet even once.
	 */
	public OutboundSyncStreamConfigurationModel lastBuiltStreamConfiguration()
	{
		if (allChangeStreams.isEmpty())
		{
			throw new IllegalStateException("No streams have been built yet");
		}
		return allChangeStreams.get(allChangeStreams.size() - 1);
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

	/**
	 * Removes all stream configurations created by this builder since previous {@code cleanup()} invocation and consumes all
	 * changes in those streams.
	 *
	 * @see #consumeAllChanges()
	 */
	public void cleanup()
	{
		consumeAllChanges();
		allChangeStreams.forEach(IntegrationTestUtil::remove);
		allChangeStreams.clear();
	}

	private void consumeChangesInStream(final OutboundSyncStreamConfigurationModel stream)
	{
		getDeltaDetect().consumeChanges(getChangesFromStream(stream));
	}

	/**
	 * Consumes all changes in the delta streams created by this builder.
	 */
	public void consumeAllChanges()
	{
		getDeltaDetect().consumeChanges(getAllChanges());
	}

	/**
	 * Retrieves all changes in the delta streams created by this builder since last {@link #cleanup()} invocation. Retrieving
	 * the changes does not consume them.
	 *
	 * @return a list of all unconsumed changes in all streams created by this builder or an empty list, if there are not changes
	 * in the streams.
	 * @see #consumeAllChanges()
	 */
	public List<ItemChangeDTO> getAllChanges()
	{
		return allChangeStreams.stream()
		                       .map(this::getChangesFromStream)
		                       .flatMap(Collection::stream)
		                       .collect(Collectors.toList());
	}

	private List<ItemChangeDTO> getChangesFromStream(final OutboundSyncStreamConfigurationModel deltaStream)
	{
		final var changesCollector = new InMemoryChangesCollector();
		final var configuration = StreamConfiguration.buildFor(deltaStream.getStreamId())
		                                             .withItemSelector(deltaStream.getWhereClause());

		getDeltaDetect().collectChangesForType(deltaStream.getItemTypeForStream(), configuration, changesCollector);
		return changesCollector.getChanges();
	}

	private ChangeDetectionService getDeltaDetect()
	{
		if (deltaDetect == null)
		{
			deltaDetect = Registry.getApplicationContext().getBean("changeDetectionService", ChangeDetectionService.class);
		}
		return deltaDetect;
	}
}
