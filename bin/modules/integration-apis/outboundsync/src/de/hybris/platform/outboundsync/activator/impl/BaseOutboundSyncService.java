/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.activator.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.service.ItemModelSearchService;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundsync.activator.OutboundItemConsumer;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup;
import de.hybris.platform.outboundsync.events.AbortedOutboundSyncEvent;
import de.hybris.platform.outboundsync.events.CompletedOutboundSyncEvent;
import de.hybris.platform.outboundsync.events.OutboundSyncEvent;
import de.hybris.platform.outboundsync.events.SystemErrorOutboundSyncEvent;
import de.hybris.platform.outboundsync.job.OutboundItemFactory;
import de.hybris.platform.outboundsync.job.impl.OutboundSyncJob;
import de.hybris.platform.outboundsync.job.impl.OutboundSyncJobRegister;
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel;
import de.hybris.platform.servicelayer.event.EventService;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;

/**
 * This base service provides common functionality across outbound sync services
 */
class BaseOutboundSyncService
{
	private static final Logger LOG = Log.getLogger(BaseOutboundSyncService.class);

	private ItemModelSearchService itemModelSearchService;
	private OutboundSyncJobRegister jobRegister;
	private OutboundItemFactory outboundItemFactory;
	private EventService eventService;
	private OutboundItemConsumer outboundItemConsumer;

	/**
	 * Instantiates this base class.
	 *
	 * @param searchService implementation of the service to search for item models
	 * @param factory       implementation of the factory to be used to create {@link de.hybris.platform.outboundsync.dto.OutboundItem}
	 * @param register      implementation of the job registry to get information about currently running outbound sync jobs.
	 */
	protected BaseOutboundSyncService(@NotNull final ItemModelSearchService searchService,
	                                  @NotNull final OutboundItemFactory factory,
	                                  @NotNull final OutboundSyncJobRegister register)
	{
		Preconditions.checkArgument(searchService != null, "ItemModelSearchService cannot be null");
		Preconditions.checkArgument(factory != null, "OutboundItemFactory cannot be null");
		Preconditions.checkArgument(register != null, "OutboundSyncJobRegister cannot be null");

		itemModelSearchService = searchService;
		outboundItemFactory = factory;
		jobRegister = register;
	}

	/**
	 * This method checks the cronjob is in the appropriate state before calling the {@link Synchronizer}.
	 * <p>
	 * Here are the rules:
	 * <ul>
	 *     <li>If the job is aborting, an abort event is published. No synchronization will occur.
	 *     <li>If the job is in system error state, an system error event is published. No synchronization will occur.
	 *     <li>If the job is not aborted and not in system error state, synchronization will occur.
	 * </ul>
	 *
	 * @param cronJobPk    PK of the cronjob being executed
	 * @param groups       collection of groups of items being synchronized
	 * @param synchronizer Synchronizer to execute when the cronjob is in the appropriate state
	 */
	protected void syncInternal(final PK cronJobPk,
	                            final Collection<OutboundItemDTOGroup> groups,
	                            final Synchronizer synchronizer
	)
	{
		getCronJob(cronJobPk).ifPresent(cronJob -> handleSyncInternal(cronJob, groups, synchronizer));
	}

	protected void syncInternal(final PK cronJobPk,
	                            final OutboundItemDTOGroup group,
	                            final Synchronizer synchronizer)
	{
		getCronJob(cronJobPk).ifPresent(cronJob -> handleSyncInternal(cronJob, group, synchronizer));
	}

	private void handleSyncInternal(@NotNull final OutboundSyncCronJobModel job,
	                                final Collection<OutboundItemDTOGroup> groups,
	                                final Synchronizer synchronizer)
	{
		try
		{
			syncInternal(job, groups, synchronizer);
		}
		catch (final RuntimeException e)
		{
			LOG.debug("Job {} failed to process {}", job, groups, e);
			publishUnSuccessfulCompletedEvent(groups);
		}
	}

	private void handleSyncInternal(@NotNull final OutboundSyncCronJobModel job,
	                                final OutboundItemDTOGroup group,
	                                final Synchronizer synchronizer)
	{
		try
		{
			syncInternal(job, group, synchronizer);
		}
		catch (final RuntimeException e)
		{
			LOG.debug("Job {} failed to process {}", job, group, e);
			publishUnSuccessfulCompletedEvent(group);
		}
	}

	private void syncInternal(@NotNull final OutboundSyncCronJobModel job,
	                          final OutboundItemDTOGroup group,
	                          final Synchronizer synchronizer)
	{
		if (isCronJobAborting(job))
		{
			publishAbortEvent(job.getPk(), group);
		}
		else
		{
			final OutboundSyncJob outboundSyncCronJob = getJobRegister().getJob(job);
			if (!outboundSyncCronJob.getCurrentState().isFinal())
			{
				synchronizer.sync();
			}
		}
	}

	private void syncInternal(@NotNull final OutboundSyncCronJobModel job,
	                          final Collection<OutboundItemDTOGroup> items,
	                          final Synchronizer synchronizer)
	{
		if (isCronJobAborting(job))
		{
			publishAbortEvent(job.getPk(), items);
		}
		else
		{
			final OutboundSyncJob outboundSyncCronJob = getJobRegister().getJob(job);
			if (!outboundSyncCronJob.getCurrentState().isFinal())
			{
				synchronizer.sync();
			}
		}
	}

	private boolean isCronJobAborting(@NotNull final OutboundSyncCronJobModel cronJob)
	{
		return Boolean.TRUE.equals(cronJob.getRequestAbort());
	}

	private void publishAbortEvent(final PK cronJobPk, final OutboundItemDTOGroup group)
	{
		publish(new AbortedOutboundSyncEvent(cronJobPk, group));
	}

	private void publishAbortEvent(final PK cronJobPk, final Collection<OutboundItemDTOGroup> groups)
	{
		publish(new AbortedOutboundSyncEvent(cronJobPk, groups));
	}

	/**
	 * Publish then {@link SystemErrorOutboundSyncEvent} if a system error occurs
	 *
	 * @param cronJobPk PK of the cronjob being executed
	 * @param group     group of the items, for which were not processed because of a systemic problem with the outbound sync.
	 */
	protected void publishSystemErrorEvent(final PK cronJobPk, final OutboundItemDTOGroup group)
	{
		publish(new SystemErrorOutboundSyncEvent(cronJobPk, group));
	}

	protected void publishSystemErrorEvent(final PK cronJobPk, final Collection<OutboundItemDTOGroup> groups)
	{
		publish(new SystemErrorOutboundSyncEvent(cronJobPk, groups));
	}

	/**
	 * Publish the {@link CompletedOutboundSyncEvent} with success set to true when the synchronization is done
	 *
	 * @param group a group of items successfully synchronized
	 */
	protected void publishSuccessfulCompletedEvent(final OutboundItemDTOGroup group)
	{
		publish(new CompletedOutboundSyncEvent(group, true));
	}

	/**
	 * Publish the {@link CompletedOutboundSyncEvent} with success set to false when the synchronization is done
	 *
	 * @param groups a group of items that failed to be synchronized
	 */
	protected void publishUnSuccessfulCompletedEvent(final Collection<OutboundItemDTOGroup> groups)
	{
		publish(new CompletedOutboundSyncEvent(groups, false));
	}

	protected void publishUnSuccessfulCompletedEvent(final OutboundItemDTOGroup group)
	{
		publish(new CompletedOutboundSyncEvent(group, false));
	}

	private void publish(final OutboundSyncEvent event)
	{
		LOG.debug("Publishing {}", event);
		if (eventService != null)
		{
			eventService.publishEvent(event);
		}
	}

	private Optional<OutboundSyncCronJobModel> getCronJob(final PK cronJobPk)
	{
		return findItemByPk(cronJobPk);
	}

	protected <T extends ItemModel> Optional<T> findItemByPk(final PK pk)
	{
		return getItemModelSearchService().nonCachingFindByPk(pk);
	}

	protected OutboundItemDTOGroup asItemGroup(final OutboundItemDTO item)
	{
		final var items = Collections.singleton(item);
		return asItemGroup(items);
	}

	protected OutboundItemDTOGroup asItemGroup(final Collection<OutboundItemDTO> items)
	{
		return OutboundItemDTOGroup.from(items, getOutboundItemFactory());
	}

	protected void consumeChanges(final OutboundItemDTOGroup outboundItemDTOGroup)
	{
		outboundItemDTOGroup.getOutboundItemDTOs().forEach(this::consumeChange);
	}

	protected void consumeChange(final OutboundItemDTO item)
	{
		if (outboundItemConsumer != null)
		{
			outboundItemConsumer.consume(item);
		}
	}

	private OutboundItemFactory getOutboundItemFactory()
	{
		return outboundItemFactory == null ? getService("outboundItemFactory", OutboundItemFactory.class) : outboundItemFactory;
	}

	/**
	 * Injects implementation of the {@link OutboundItemFactory} to be passed into the {@link OutboundItemDTOGroup} created
	 * by this service.
	 *
	 * @param factory an implementation of the outbound item factory to be used in this service
	 * @deprecated not preferred anymore. If the dependency is needed it should be injected into the concrete service class.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setOutboundItemFactory(@NotNull final OutboundItemFactory factory)
	{
		outboundItemFactory = factory;
	}

	/**
	 * Retrieves the change consumer used to clear changes in the delta detect module when the change is successfully synchronized
	 * or should be ignored.
	 *
	 * @return changes consumer user by this service
	 */
	public OutboundItemConsumer getOutboundItemConsumer()
	{
		return outboundItemConsumer;
	}

	/**
	 * Injects a change consumer, which clears changes in the delta detect module when the change is successfully synchronized or
	 * should be ignored. If item consumer is not injected, the changes won't be consumed and therefore custom implementation need
	 * to manage delta detect changes themselves.
	 *
	 * @param consumer an implementation of the change consumer to use.
	 */
	public void setOutboundItemConsumer(final OutboundItemConsumer consumer)
	{
		outboundItemConsumer = consumer;
	}

	/**
	 * Injects implementation of the {@link EventService} to use for publishing outbound sync job events. If the event services is
	 * not injected, the outbound sync events won't be fired. Although, this dependency is optional, current implementation of the
	 * outbound sync heavily relies on the outbound sync events being fired. If a customization, decides not to
	 * inject this service, it has to manage events delivery to {@link de.hybris.platform.outboundsync.job.impl.OutboundSyncJobStateAggregator}
	 * in a customer way to maintain correct functionality.
	 *
	 * @param service a service to use for publishing events
	 */
	public void setEventService(final EventService service)
	{
		eventService = service;
	}

	private ItemModelSearchService getItemModelSearchService()
	{
		return itemModelSearchService == null
				? getService("itemModelSearchService", ItemModelSearchService.class)
				: itemModelSearchService;
	}

	/**
	 * Injects implementation of the {@link ItemModelSearchService} to be used for searching model instances by PK. If this method
	 * is not called then, {@code ItemModelSearchService} configured in {@code "itemModelSearchService"} Spring bean will be used
	 * by default.
	 *
	 * @param service an implementation to use.
	 * @deprecated not preferred anymore. Inject the {@code ItemModelSearchService} through constructor
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setItemModelSearchService(@NotNull final ItemModelSearchService service)
	{
		itemModelSearchService = service;
	}

	static <T> T getService(final String id, final Class<T> type)
	{
		return Registry.getApplicationContext().getBean(id, type);
	}

	private OutboundSyncJobRegister getJobRegister()
	{
		return jobRegister == null ? getService("jobRegister", OutboundSyncJobRegister.class) : jobRegister;
	}

	/**
	 * Injects specific {@link OutboundSyncJobRegister} implementation.
	 *
	 * @param register an implementation to use
	 * @deprecated not preferred anymore. Inject the {@code OutboundSyncJobRegister} through constructor
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setJobRegister(@NotNull final OutboundSyncJobRegister register)
	{
		this.jobRegister = register;
	}

	/**
	 * The Synchronizer interface is defined to be used with the {@link #syncInternal(PK, OutboundItemDTOGroup, Synchronizer)}
	 * or {@link #handleSyncInternal(OutboundSyncCronJobModel, Collection, Synchronizer)} methods.
	 * Because subclasses may have different synchronize method that takes different input parameters, this
	 * interface provides a common method to call when synchronizing.
	 */
	protected interface Synchronizer
	{
		/**
		 * Synchronizes the change.
		 * The user can supply an implementation by using lambda expression {@code () -> synchronizeAnItem(args1,...,argsN)},
		 * where {@code synchronizeAnItem(...)} is a method in the subclass of this base class.
		 */
		void sync();
	}
}
