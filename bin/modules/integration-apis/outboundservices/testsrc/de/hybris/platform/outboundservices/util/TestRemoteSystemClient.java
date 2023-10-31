/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.util;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundservices.facade.impl.RemoteSystemClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

public class TestRemoteSystemClient extends ExternalResource implements RemoteSystemClient
{
	private static final URI SOME_URI = createUri();
	private static final ResponseEntity<String> DEFAULT_RESPONSE = ResponseEntity.created(SOME_URI).build();
	private static final Logger LOG = Log.getLogger(TestRemoteSystemClient.class);
	private static final String CONTENT_TYPE = "Content-Type";

	private final Queue<Supplier<ResponseEntity<String>>> responseQueue;
	private final Collection<Pair<ConsumedDestinationModel, Object>> invocations;
	private Supplier<ResponseEntity<String>> defaultResponseSupplier;
	private Function<List<String>, ResponseEntity<String>> responseBodyGenerator;

	public TestRemoteSystemClient()
	{
		defaultResponseSupplier = this::createDefaultResponse;
		invocations = Collections.synchronizedList(new ArrayList<>());
		responseQueue = new ConcurrentLinkedQueue<>();
	}

	private static URI createUri()
	{
		try
		{
			return new URI("//does.not/matter");
		}
		catch (final URISyntaxException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public ResponseEntity<Map> post(final ConsumedDestinationModel destination, final HttpEntity<Map<String, Object>> entity)
	{
		return post(destination, entity, Map.class);
	}

	@Override
	public <T, U> ResponseEntity<T> post(final ConsumedDestinationModel destination,
	                                     final HttpEntity<U> entity,
	                                     final Class<T> responseType)
	{
		return getResponse(destination, entity);
	}

	@Override
	public synchronized <T, U> ResponseEntity<T> post(final ConsumedDestinationModel destination,
	                                                  final HttpEntity<U> entity,
	                                                  final Class<T> responseType,
	                                                  final String additionalPath)
	{
		if (responseBodyGenerator != null &&
				entity.getHeaders().get(CONTENT_TYPE) != null &&
				String.join(",", entity.getHeaders().get(CONTENT_TYPE)).contains("batch"))
		// If it's a batch request and a body generator is given, that means to use the generator for this batch request.
		{
			List<String> contentIdsFromBatchRequest = extractContentId((String) Objects.requireNonNull(entity.getBody()));
			respondWith(() -> responseBodyGenerator.apply(contentIdsFromBatchRequest));
		}
		return getResponse(destination, entity);
	}

	private <T, U> ResponseEntity<T> getResponse(final ConsumedDestinationModel destination, final HttpEntity<U> entity)
	{
		LOG.info("Sending destination {} with entity", destination.getId());
		invocations.add(Pair.of(destination, entity));
		final var responseSupplier = nextResponseSupplier();
		final ResponseEntity response = responseSupplier.get();
		LOG.info("*****For request*****:\n{}\n*****Responding with:*****\n{}\n", entity.getBody(), response);

		return (ResponseEntity<T>) response;
	}

	private Supplier<ResponseEntity<String>> nextResponseSupplier()
	{
		final var response = responseQueue.poll();
		return response != null ? response : defaultResponseSupplier;
	}

	/**
	 * Retrieves number of invocations captured by this tracker.
	 *
	 * @return number of times the {@link #post(ConsumedDestinationModel, HttpEntity, Class)} (ConsumedDestinationModel, HttpEntity, Class)} method has been invoked. This is the same value
	 * as {@code getAllInvocations().size()}
	 */
	public int invocations()
	{
		return invocations.size();
	}

	/**
	 * Retrieves all invocations of the {@link #post(ConsumedDestinationModel, HttpEntity, Class)} (ConsumedDestinationModel, HttpEntity, Class)} (SyncParameters)} method
	 *
	 * @return a list of parameters passed to this tracker invocations in their chronological order or an empty list,
	 * if the {@link #post(ConsumedDestinationModel, HttpEntity, Class)} (ConsumedDestinationModel, HttpEntity, Class)} (SyncParameters)} was not invoked.
	 */
	public List<Pair<ConsumedDestinationModel, Object>> getAllInvocations()
	{
		return List.copyOf(invocations);
	}

	public TestRemoteSystemClient respondWith(final ResponseEntity<String> response)
	{
		return respondWith(new OutboundInvocationTracker.EntityResponse<>(response));
	}

	private TestRemoteSystemClient respondWith(final Supplier<ResponseEntity<String>> response)
	{
		responseQueue.add(response);
		defaultResponseSupplier = response;
		return this;
	}

	/**
	 * For the batch request, a code block will be input showing how the mocked batch response is generated. It takes a list of
	 * content-id as input parameters. The contentIds of a batchResponse can be known and mocked only after the facade calls
	 * the batch generator and send the generated batch request to the {@link RemoteSystemClient} otherwise response's content-id
	 * won't match the randomly generated content-id of a request. This method postpones the creation of mocked batch response to
	 * that time it's sent to the client in order to get randomly generated content-id.
	 *
	 * @param generatorBlock the code block showing how will the batch response be mocked along with the contentIds
	 */
	public void respondWithBatch(final Function<List<String>, ResponseEntity<String>> generatorBlock)
	{
		responseBodyGenerator = generatorBlock;
	}

	private List<String> extractContentId(final String body)
	{
		List<String> contentIds = new ArrayList<>();
		final String contentId = "Content-Id:";
		int index = body.indexOf(contentId);
		while (index > 0)
		{
			contentIds.add(body.substring(index + 12, index + 48));  // extract the content-id in requests
			index = body.indexOf("Content-Id:", index + 48);
		}
		return contentIds;
	}

	protected ResponseEntity<String> createDefaultResponse()
	{
		return DEFAULT_RESPONSE;
	}

	@Override
	protected void after()
	{
		responseQueue.clear();
		invocations.clear();
		responseBodyGenerator = null;
	}
}
