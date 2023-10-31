/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service.impl;

import de.hybris.platform.outboundservices.event.EventType;
import de.hybris.platform.outboundservices.event.impl.DefaultEventType;
import de.hybris.platform.webhookservices.config.WebhookServicesConfiguration;
import de.hybris.platform.webhookservices.event.ItemCreatedEvent;
import de.hybris.platform.webhookservices.event.ItemDeletedEvent;
import de.hybris.platform.webhookservices.event.ItemSavedEvent;
import de.hybris.platform.webhookservices.event.ItemUpdatedEvent;
import de.hybris.platform.webhookservices.event.WebhookEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.base.Preconditions;

/**
 * An aggregator to aggregate {@link WebhookEvent}s related to an item having WebhookConfiguration linked to it
 */
public class WebhookEventAggregator
{
	private final WebhookServicesConfiguration webhookServicesConfiguration;

	/**
	 * Instantiates the {@link WebhookEventAggregator} with the webhook services configuration
	 *
	 * @param config {@link WebhookServicesConfiguration} with access methods to configurations related to the webhookservices
	 *               module
	 */
	public WebhookEventAggregator(final WebhookServicesConfiguration config)
	{
		Preconditions.checkArgument(config != null, "WebhookServicesConfiguration cannot be null.");
		this.webhookServicesConfiguration = config;
	}

	/**
	 * Aggregates the provided webhook events for an item to unique events only.
	 * Events created by nested items are removed when an event that is not created by any nested item is present.
	 * If webhook event consolidation is enabled, updated events will be consolidated into the created events.
	 * <p>
	 * Event consolidation is squashing or merging of {@link ItemUpdatedEvent} into the {@link ItemCreatedEvent} when the latter is
	 * present. {@code project.properties#webhookservices.aggregating.group.timeout} specifies the time frame for which events
	 * will be collected to consolidate.
	 * For example, when an item is created and then updated in different request but before the aggregation times out,
	 * ItemCreatedEvent and ItemUpdatedEvent are created for the item. If
	 * {@code project.properties#webhookservices.event.consolidation.enabled} is {@code true}, ItemUpdatedEvent will be squashed
	 * into the ItemCreatedEvent, to return ItemCreatedEvent only.
	 * </p>
	 * <p>
	 * {@link ItemDeletedEvent} is not considered for removing nested events, and consolidation.
	 *
	 * @param webhookEvents a collection of webhook events to be aggregated
	 * @return a set of unique webhook events after aggregation
	 */
	public Set<WebhookEvent> aggregate(final Collection<WebhookEvent> webhookEvents)
	{
		if (!CollectionUtils.isEmpty(webhookEvents))
		{
			final var aggregatedEvents = new HashSet<WebhookEvent>();
			final var uniqueEvents = new HashSet<>(webhookEvents);

			// Segregating deleted events out.
			final Map<Boolean, List<WebhookEvent>> partitionedEvents =
					uniqueEvents.stream()
					            .collect(Collectors.partitioningBy(ItemDeletedEvent.class::isInstance));

			final var deletedEvents = partitionedEvents.get(Boolean.TRUE);
			final var nonDeletedEvents = new HashSet<>(partitionedEvents.get(Boolean.FALSE));

			final Set<WebhookEvent> consolidatedEvents = collapseAndConsolidate(nonDeletedEvents);

			aggregatedEvents.addAll(deletedEvents);
			aggregatedEvents.addAll(consolidatedEvents);
			return aggregatedEvents;
		}

		return Collections.emptySet();
	}

	private Set<WebhookEvent> collapseAndConsolidate(final HashSet<WebhookEvent> nonDeletedEvents)
	{
		// Removing nested events if non-nested events are present.
		final Set<WebhookEvent> collapsedEvents = collapse(nonDeletedEvents);

		// Consolidating updates events into created events.
		return webhookServicesConfiguration.isEventConsolidationEnabled()
				? squash(collapsedEvents)
				: collapsedEvents;
	}

	private Set<WebhookEvent> squash(final Set<WebhookEvent> events)
	{
		final Set<WebhookEvent> squashedEvents = new HashSet<>();
		squashedEvents.addAll(getSquashedNonSaveEvents(events));
		squashedEvents.addAll(getSquashedSaveEvents(events));
		return squashedEvents;
	}

	private Set<? extends WebhookEvent> getSquashedNonSaveEvents(final Set<WebhookEvent> events)
	{
		return getEventsByCondition(events,
				ItemCreatedEvent.class::isInstance,
				ItemUpdatedEvent.class::isInstance);
	}

	private Set<WebhookEvent> getSquashedSaveEvents(final Set<WebhookEvent> events)
	{
		return getEventsByCondition(events,
				e -> isItemSavedEventOfType(e, DefaultEventType.CREATED),
				e -> isItemSavedEventOfType(e, DefaultEventType.UPDATED));
	}

	private HashSet<WebhookEvent> getEventsByCondition(final Set<WebhookEvent> events,
	                                                   final Predicate<WebhookEvent> primaryEvent,
	                                                   final Predicate<WebhookEvent> secondaryEvent)
	{
		final var squashedNonSaveEvents = new HashSet<WebhookEvent>();
		events.stream()
		      .filter(primaryEvent)
		      .findFirst()
		      .ifPresentOrElse(squashedNonSaveEvents::add,
				      () -> events.stream()
				                  .filter(secondaryEvent)
				                  .findFirst().ifPresent(squashedNonSaveEvents::add));
		return squashedNonSaveEvents;
	}

	private boolean isItemSavedEventOfType(final WebhookEvent e, final EventType type)
	{
		return e instanceof ItemSavedEvent && e.getEventType().equals(type);
	}

	private Set<WebhookEvent> collapse(final Set<WebhookEvent> events)
	{
		return originalEventPresent(events) ? removeCreatedFromNestedItemEvents(events) : events;
	}

	private boolean originalEventPresent(final Set<WebhookEvent> events)
	{
		return events.stream().anyMatch(e -> !e.isCreatedFromNestedItemEvent());
	}

	private Set<WebhookEvent> removeCreatedFromNestedItemEvents(final Set<WebhookEvent> events)
	{
		return events.stream()
		             .filter(e -> !e.isCreatedFromNestedItemEvent())
		             .collect(Collectors.toSet());
	}
}
