/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.facade.impl;

import static de.hybris.platform.outboundservices.facade.OutboundBatchRequestPartDTO.OutboundBatchRequestPartDTOBuilder.outboundBatchRequestPartDTO;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.ApplicationBeans;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.outboundservices.decorator.DecoratorContextFactory;
import de.hybris.platform.outboundservices.decorator.OutboundRequestDecorator;
import de.hybris.platform.outboundservices.decorator.RequestDecoratorService;
import de.hybris.platform.outboundservices.facade.ConsumedDestinationNotFoundModel;
import de.hybris.platform.outboundservices.facade.OutboundBatchRequestPartDTO;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.outboundservices.facade.SyncParameters;
import de.hybris.platform.outboundservices.service.MultiPartRequestGenerator;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Preconditions;

import rx.Observable;

/**
 * Default implementation of OutboundServiceFacade.
 */
public class DefaultOutboundServiceFacade implements OutboundServiceFacade
{
	private static final Logger LOG = Log.getLogger(DefaultOutboundServiceFacade.class);
	private static final String BATCH_URI = "/$batch";

	private final RemoteSystemClient remoteSystemClient;
	private RequestDecoratorService requestDecoratorService;
	private MultiPartRequestGenerator multiPartRequestGenerator;
	private IntegrationRestTemplateFactory integrationRestTemplateFactory;
	private OutboundRequestDecorator monitoringDecorator;


	/**
	 * Instantiates this facade with the provided dependencies it relies on.
	 *
	 * @param ctxFactory implementation of the {@code DecoratorContextFactory} to be used
	 * @param client     implementation of the {@code RemoteSystemClient} to be used
	 * @deprecated use {@link #DefaultOutboundServiceFacade(RemoteSystemClient)} instead.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public DefaultOutboundServiceFacade(@NotNull final DecoratorContextFactory ctxFactory,
	                                    @NotNull final RemoteSystemClient client)
	{
		this(client);
	}

	/**
	 * Instantiates this facade with the provided dependencies it relies on.
	 *
	 * @param client implementation of the {@code RemoteSystemClient} to be used
	 */
	public DefaultOutboundServiceFacade(@NotNull final RemoteSystemClient client)
	{
		Preconditions.checkArgument(client != null, "RemoteSystemClient cannot be null");
		remoteSystemClient = client;
	}

	/**
	 * {@inheritDoc}
	 * A {@link ModelNotFoundException} will be thrown if a valid {@link IntegrationObjectModel} cannot be found for the passed
	 * ioCode.
	 *
	 * @deprecated use {@link #send(SyncParameters)} instead.
	 */
	@Override
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public Observable<ResponseEntity<Map>> send(@NotNull final ItemModel itemModel,
	                                            @NotNull final String ioCode,
	                                            @NotNull final String destinationId)
	{
		Preconditions.checkArgument(StringUtils.isNotBlank(destinationId), "destinationId cannot be null or empty");
		Preconditions.checkArgument(StringUtils.isNotBlank(ioCode), "ioCode cannot be null or empty");
		Objects.requireNonNull(itemModel, "ItemModel cannot be null");

		final SyncParameters params = SyncParameters.syncParametersBuilder()
		                                            .withPayloadObject(itemModel)
		                                            .withIntegrationObjectCode(ioCode)
		                                            .withDestinationId(destinationId)
		                                            .build();
		return send(params);
	}

	@Override
	public Observable<ResponseEntity<Map>> send(final SyncParameters params)
	{
		Objects.requireNonNull(params, "SyncParameters should not be null.");
		throwExceptionIfDestinationIsNull(params);

		return params.getIntegrationObject().getRootClass() == null
				? createObservable(params)
				: Observable.just(ResponseEntity.of(Optional.empty()));
	}

	@Override
	public ResponseEntity<String> sendBatch(final List<SyncParameters> params)
	{
		if (!CollectionUtils.isEmpty(params))
		{
			params.forEach(this::throwExceptionIfDestinationIsNull);
			return createBatchObservable(params);
		}
		return ResponseEntity.of(Optional.empty());
	}

	protected Observable<ResponseEntity<Map>> createObservable(final SyncParameters params)
	{
		return Observable.just(params)
		                 .map(p -> {
			                 final var entity = getRequestDecoratorService().createHttpEntity(p);
			                 return remoteSystemClient.post(p.getDestination(), entity, Map.class);
		                 });
	}

	protected ResponseEntity<String> createBatchObservable(final List<SyncParameters> params)
	{
		final List<OutboundBatchRequestPartDTO> requestParts = generateRequestParts(params);
		if (!requestParts.isEmpty())
		{
			final HttpEntity<?> batchRequest = generateHttpEntity(requestParts);
			return remoteSystemClient.post(params.get(0).getDestination(), batchRequest, String.class, BATCH_URI);
		}
		return ResponseEntity.of(Optional.empty());
	}

	private List<OutboundBatchRequestPartDTO> generateRequestParts(final List<SyncParameters> params)
	{
		return params.stream()
		             .filter(SyncParameters::isBatchSupported)
		             .map(this::buildBatchRequestDTO)
		             .filter(Objects::nonNull)
		             .toList();
	}

	private void throwExceptionIfDestinationIsNull(final SyncParameters params)
	{
		if (params.getDestination() instanceof ConsumedDestinationNotFoundModel)
		{
			persistOutboundRequestAndThrowExceptionByCreatingHttpEntity(params);
		}
	}

	private void persistOutboundRequestAndThrowExceptionByCreatingHttpEntity(final SyncParameters params)
	{
		getRequestDecoratorService().createHttpEntity(params);
	}

	private HttpEntity<?> generateHttpEntity(final List<OutboundBatchRequestPartDTO> requestParts)
	{
		if (requestParts.isEmpty())
		{
			LOG.warn(
					"Batch Request does not contain any payloads associated to ItemModels. No request will be sent to the destination");
			return HttpEntity.EMPTY;
		}
		return getMultiPartRequestGenerator().generate(requestParts);
	}

	private OutboundBatchRequestPartDTO buildBatchRequestDTO(final SyncParameters p)
	{
		final HttpEntity<Map<String, Object>> httpEntity = getRequestDecoratorService().createHttpEntity(p);
		return p.getPayloadObject() instanceof final ItemModel item
				? outboundBatchRequestPartDTO().withRequestType(HttpMethod.POST)
				                               .withHttpEntity(httpEntity)
				                               .withItemType(item.getItemtype())
				                               .withChangeId(p.getChangeId())
				                               .build()
				: null;
	}

	/**
	 * @deprecated this property is not required anymore.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public void setContextFactory(final DecoratorContextFactory factory)
	{
		// this method does nothing.
	}

	private RequestDecoratorService getRequestDecoratorService()
	{
		if (requestDecoratorService == null)
		{
			requestDecoratorService = ApplicationBeans.getBean("requestDecoratorService", RequestDecoratorService.class);
		}
		return requestDecoratorService;
	}

	public void setRequestDecoratorService(final RequestDecoratorService requestDecoratorService)
	{
		this.requestDecoratorService = requestDecoratorService;
	}

	private MultiPartRequestGenerator getMultiPartRequestGenerator()
	{
		if (multiPartRequestGenerator == null)
		{
			multiPartRequestGenerator = ApplicationBeans.getBean("multiPartRequestGenerator", MultiPartRequestGenerator.class);
		}
		return multiPartRequestGenerator;
	}

	public void setMultiPartRequestGenerator(final MultiPartRequestGenerator multiPartRequestGenerator)
	{
		this.multiPartRequestGenerator = multiPartRequestGenerator;
	}


	/**
	 * Injects rest template factory to be used.
	 *
	 * @param integrationRestTemplateFactory factory implementation to use for rest templates creation
	 * @deprecated {@link IntegrationRestTemplateFactory} is not needed anymore in this class and will be removed
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public void setIntegrationRestTemplateFactory(final IntegrationRestTemplateFactory integrationRestTemplateFactory)
	{
		this.integrationRestTemplateFactory = integrationRestTemplateFactory;
	}

	/**
	 * Getter for the {@link IntegrationRestTemplateFactory}
	 *
	 * @return {@link IntegrationRestTemplateFactory}
	 * @deprecated {@link IntegrationRestTemplateFactory} is not needed anymore in this class and will be removed
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	protected IntegrationRestTemplateFactory getIntegrationRestTemplateFactory()
	{
		return integrationRestTemplateFactory;
	}

	/**
	 * Injects rest template factory to be used.
	 *
	 * @param decorator
	 * @deprecated {@link OutboundRequestDecorator} is not required anymore and will be removed.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public void setMonitoringDecorator(final OutboundRequestDecorator decorator)
	{
		monitoringDecorator = decorator;
	}

	/**
	 * Getter for the {@link OutboundRequestDecorator}
	 *
	 * @return {@link OutboundRequestDecorator}
	 * @deprecated {@link OutboundRequestDecorator} is not needed anymore in this class and will be removed
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	protected OutboundRequestDecorator getMonitoringDecorator()
	{
		return monitoringDecorator;
	}
}
