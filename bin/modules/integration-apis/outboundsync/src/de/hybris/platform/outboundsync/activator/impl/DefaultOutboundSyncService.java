/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.activator.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.exception.IntegrationAttributeException;
import de.hybris.platform.integrationservices.exception.IntegrationAttributeProcessingException;
import de.hybris.platform.integrationservices.service.ItemModelSearchService;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundservices.enums.OutboundSource;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.outboundservices.facade.SyncParameters;
import de.hybris.platform.outboundservices.service.OutboundMultiPartResponseParser;
import de.hybris.platform.outboundsync.activator.OutboundItemConsumer;
import de.hybris.platform.outboundsync.activator.OutboundSyncService;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup;
import de.hybris.platform.outboundsync.exceptions.BatchResponseNotFoundException;
import de.hybris.platform.outboundsync.job.OutboundItemFactory;
import de.hybris.platform.outboundsync.job.impl.OutboundSyncJobRegister;
import de.hybris.platform.outboundsync.retry.RetryUpdateException;
import de.hybris.platform.outboundsync.retry.SyncRetryService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Preconditions;

import rx.Observable;

/**
 * Default implementation of {@link OutboundSyncService} that uses {@link OutboundServiceFacade} for sending changes to the
 * destinations.
 */
public class DefaultOutboundSyncService extends BaseOutboundSyncService implements OutboundSyncService
{
	private static final Logger LOG = Log.getLogger(DefaultOutboundSyncService.class);

	private static final String CONTENT_ID = "Content-ID";

	private OutboundServiceFacade outboundServiceFacade;
	private SyncRetryService syncRetryService;
	private final OutboundMultiPartResponseParser batchResponseParser;

	/**
	 * Instantiates this service. Uses default implementations available in the application context for the required dependencies.
	 * Not required dependencies will not be injected after this service is instantiated. They need to be injected separately by
	 * calling the corresponding {@code set...()} methods.
	 *
	 * @see BaseOutboundSyncService#setOutboundItemFactory(OutboundItemFactory)
	 * @see BaseOutboundSyncService#setItemModelSearchService(ItemModelSearchService)
	 * @see BaseOutboundSyncService#setEventService(EventService)
	 * @see BaseOutboundSyncService#setOutboundItemConsumer(OutboundItemConsumer)
	 * @see #setOutboundServiceFacade(OutboundServiceFacade)
	 * @see #setSyncRetryService(SyncRetryService)
	 * @deprecated Use the constructor with dependencies to inject parameters
	 * {@link #DefaultOutboundSyncService(ItemModelSearchService, OutboundItemFactory, OutboundSyncJobRegister, OutboundServiceFacade, OutboundMultiPartResponseParser)}
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public DefaultOutboundSyncService()
	{
		this(BaseOutboundSyncService.getService("itemModelSearchService", ItemModelSearchService.class),
				BaseOutboundSyncService.getService("outboundItemFactory", OutboundItemFactory.class),
				BaseOutboundSyncService.getService("outboundSyncJobRegister", OutboundSyncJobRegister.class),
				BaseOutboundSyncService.getService("outboundServiceFacade", OutboundServiceFacade.class));
	}

	/**
	 * @param searchService Service to search for item models
	 * @param factory       A factory implementation to create instances of {@link de.hybris.platform.outboundsync.dto.OutboundItem}
	 * @param register      Service to use for inquirying about currently running outbound sync jobs
	 * @param facade        An implementation of the facade to send changes through
	 * @deprecated Use {@link #DefaultOutboundSyncService(ItemModelSearchService, OutboundItemFactory, OutboundSyncJobRegister, OutboundServiceFacade, OutboundMultiPartResponseParser)}
	 * to instantiates this outbound sync service.
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public DefaultOutboundSyncService(@NotNull final ItemModelSearchService searchService,
	                                  @NotNull final OutboundItemFactory factory,
	                                  @NotNull final OutboundSyncJobRegister register,
	                                  @NotNull final OutboundServiceFacade facade)
	{
		this(searchService, factory, register, facade,
				BaseOutboundSyncService.getService("outboundMultiPartResponseParser", OutboundMultiPartResponseParser.class));
	}

	/**
	 * Instantiates this outbound sync service.
	 *
	 * @param searchService  Service to search for item models
	 * @param factory        A factory implementation to create instances of {@link de.hybris.platform.outboundsync.dto.OutboundItem}
	 * @param register       Service to use for inquirying about currently running outbound sync jobs
	 * @param facade         An implementation of the facade to send changes through
	 * @param responseParser Batch response parser
	 */
	public DefaultOutboundSyncService(@NotNull final ItemModelSearchService searchService,
	                                  @NotNull final OutboundItemFactory factory,
	                                  @NotNull final OutboundSyncJobRegister register,
	                                  @NotNull final OutboundServiceFacade facade,
	                                  @NotNull final OutboundMultiPartResponseParser responseParser)
	{
		super(searchService, factory, register);
		Preconditions.checkArgument(facade != null, "OutboundServiceFacade cannot be null");
		Preconditions.checkArgument(responseParser != null, "MultiPartResponseParser cannot be null");
		outboundServiceFacade = facade;
		batchResponseParser = responseParser;
	}

	@Override
	public void sync(final Collection<OutboundItemDTO> outboundItemDTOs)
	{
		final var itemGroup = asItemGroup(outboundItemDTOs);
		syncInternal(itemGroup.getCronJobPk(), itemGroup, () -> synchronizeItem(itemGroup));
	}

	@Override
	public void syncBatch(final Collection<OutboundItemDTOGroup> outboundItemDTOGroups)
	{
		if (outboundItemDTOGroups == null || outboundItemDTOGroups.isEmpty())
		{
			LOG.error("The collection of items to be synced is empty.");
			return;
		}
		syncInternal(outboundItemDTOGroups.stream().findFirst().orElseThrow().getCronJobPk(),
				outboundItemDTOGroups,
				() -> synchronizeItem(outboundItemDTOGroups));

	}

	private void synchronizeItem(final Collection<OutboundItemDTOGroup> outboundItemDTOGroups)
	{
		final List<SyncParameters> syncParametersList = createSyncParameters(outboundItemDTOGroups).stream().toList();
		if (syncParametersList.isEmpty())
		{
			LOG.debug("No root items found for this synchronization.");
			return;
		}
		try
		{
			final ResponseEntity<String> response = getOutboundServiceFacade().sendBatch(syncParametersList);
			handleBatchResponse(response, outboundItemDTOGroups);
		}
		catch (final RuntimeException e)
		{
			handleBatchError(e, outboundItemDTOGroups);
		}
	}

	private void handleBatchResponse(final ResponseEntity<String> response,
	                                 final Collection<OutboundItemDTOGroup> groups)
	{
		final List<ResponseEntity<Map>> responseParts = getBatchResponseParser()
				.parseMultiPartResponse(response);


		groups.forEach(g -> findResponseAndHandle(g, responseParts));
	}

	private void findResponseAndHandle(final OutboundItemDTOGroup g, final List<ResponseEntity<Map>> responseParts)
	{
		responseParts.stream()
		             .filter(r -> !CollectionUtils.isEmpty(r.getHeaders().get(CONTENT_ID))
				             && r.getHeaders()
				                 .get(CONTENT_ID).get(0).equals(
						             g.getChangeId()))
		             .findFirst()
		             .ifPresentOrElse(
				             r -> handleResponse(r, g),
				             () -> handleError(new BatchResponseNotFoundException(g), g));
	}

	private void synchronizeItem(final OutboundItemDTOGroup dtoGroup)
	{
		findRootItemModel(dtoGroup).ifPresent(item -> synchronizeItem(dtoGroup, item));
	}

	private void synchronizeItem(final OutboundItemDTOGroup dtoGroup, final ItemModel item)
	{
		try
		{
			final var syncParameters = convertToSyncParameters(dtoGroup, item);

			final Observable<ResponseEntity<Map>> outboundResponse = getOutboundServiceFacade().send(syncParameters);
			outboundResponse.subscribe(r -> handleResponse(r, dtoGroup), e -> handleError(e, dtoGroup));
		}
		catch (final RuntimeException e)
		{
			handleError(e, dtoGroup);
		}
	}

	private Optional<ItemModel> findRootItemModel(final OutboundItemDTOGroup outboundItemDTOGroup)
	{
		final Long rootItemPk = outboundItemDTOGroup.getRootItemPk();
		LOG.debug("Synchronizing changes in item with PK={}", rootItemPk);

		final Optional<ItemModel> item = findItemByPk(PK.fromLong(rootItemPk));
		if (item.isEmpty())
		{
			LOG.debug("Cannot find item with PK={}", rootItemPk);
		}
		return item;
	}

	private SyncParameters convertToSyncParameters(final OutboundItemDTOGroup dtoGroup, final ItemModel itemModel)
	{
		return SyncParameters.syncParametersBuilder()
		                     .withPayloadObject(itemModel)
		                     .withIntegrationObjectCode(dtoGroup.getIntegrationObjectCode())
		                     .withDestinationId(dtoGroup.getDestinationId())
		                     .withChangeId(dtoGroup.getChangeId())
		                     .withSource(OutboundSource.OUTBOUNDSYNC)
		                     .build();
	}

	private List<SyncParameters> createSyncParameters(final Collection<OutboundItemDTOGroup> outboundItemDTOGroups)
	{
		final var syncParams = new ArrayList<SyncParameters>();
		outboundItemDTOGroups.forEach(dtoGroup ->
				findRootItemModel(dtoGroup).ifPresent(
						item -> syncParams.add(convertToSyncParameters(dtoGroup, item))
				)
		);
		return syncParams;
	}

	private void handleError(final Throwable throwable, final OutboundItemDTOGroup outboundItemDTOGroup)
	{
		LOG.error("Failed to send item with PK={}", outboundItemDTOGroup.getRootItemPk(), throwable);
		if (throwable instanceof IntegrationAttributeProcessingException)
		{
			handleError(outboundItemDTOGroup);
		}
		else if (throwable instanceof IntegrationAttributeException)
		{
			publishSystemErrorEvent(outboundItemDTOGroup.getCronJobPk(), outboundItemDTOGroup);
		}
		else
		{
			handleError(outboundItemDTOGroup);
		}
	}

	private void handleBatchError(final Throwable throwable, final Collection<OutboundItemDTOGroup> outboundItemDTOGroups)
	{
		LOG.error("Failed to send batch request for synchronization", throwable);
		if (throwable instanceof IntegrationAttributeProcessingException)
		{
			outboundItemDTOGroups.forEach(this::handleError);
		}
		else if (throwable instanceof IntegrationAttributeException)
		{
			publishSystemErrorEvent(outboundItemDTOGroups.stream().findFirst().orElseThrow().getCronJobPk(),
					outboundItemDTOGroups);
		}
		else
		{
			outboundItemDTOGroups.forEach(this::handleError);
		}
	}

	protected void handleError(final OutboundItemDTOGroup outboundItemDTOGroup)
	{
		LOG.warn("The item with PK={} couldn't be synchronized", outboundItemDTOGroup.getRootItemPk());
		try
		{
			if (syncRetryService.handleSyncFailure(outboundItemDTOGroup))
			{
				consumeChanges(outboundItemDTOGroup);
			}
		}
		// Due to the observable.onerror flow, we'll never get to this catch block. The plan is to get rid of the Observable in
		// the facade invocation, so this code block will then be correct
		catch (final RetryUpdateException e)
		{
			LOG.debug("Retry could not be updated", e);
		}
		finally
		{
			publishUnSuccessfulCompletedEvent(outboundItemDTOGroup);
		}
	}

	protected void handleResponse(final ResponseEntity<Map> responseEntity, final OutboundItemDTOGroup outboundItemDTOGroup)
	{
		if (responseEntity.getStatusCode().is2xxSuccessful())
		{
			handleSuccessfulSync(outboundItemDTOGroup);
		}
		else
		{
			handleError(outboundItemDTOGroup);
		}
	}

	protected void handleSuccessfulSync(final OutboundItemDTOGroup outboundItemDTOGroup)
	{
		LOG.debug("The product with PK={} has been synchronized", outboundItemDTOGroup.getRootItemPk());
		try
		{
			syncRetryService.handleSyncSuccess(outboundItemDTOGroup);
			consumeChanges(outboundItemDTOGroup);
			publishSuccessfulCompletedEvent(outboundItemDTOGroup);
		}
		catch (final RetryUpdateException e)
		{
			LOG.debug("Retry could not be updated", e);
		}
	}

	/**
	 * Retrieves implementation of the {@link OutboundServiceFacade} being used by this service.
	 *
	 * @return implementation of the outbound facade being used to send items to an external system.
	 */
	public OutboundServiceFacade getOutboundServiceFacade()
	{
		return outboundServiceFacade;
	}

	/**
	 * Injects an outbound service facade to be used for sending the items to an external system.
	 *
	 * @param facade facade implementation to use.
	 */
	public void setOutboundServiceFacade(@NotNull final OutboundServiceFacade facade)
	{
		outboundServiceFacade = facade;
	}

	/**
	 * Injects implementation of the retry service to manage retries when outbound sync was not successful.
	 *
	 * @param service an implementation of the service to use.
	 */
	public void setSyncRetryService(@NotNull final SyncRetryService service)
	{
		syncRetryService = service;
	}

	private OutboundMultiPartResponseParser getBatchResponseParser()
	{
		return batchResponseParser;
	}
}
