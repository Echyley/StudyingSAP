/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.util;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.outboundservices.enums.OutboundSource;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.outboundservices.facade.SyncParameters;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import rx.Observable;

public class TestOutboundFacade extends OutboundInvocationTracker<TestOutboundFacade> implements OutboundServiceFacade
{
	private static final URI SOME_URI = createUri();
	private static final ResponseEntity<Map> DEFAULT_RESPONSE = ResponseEntity.created(SOME_URI).build();

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
	protected ResponseEntity<Map> createDefaultResponse()
	{
		return DEFAULT_RESPONSE;
	}

	/**
	 * Specifies to respond with HTTP 201 Created to all requests it receives, unless this spec is overridden then by a subsequent
	 * call to a {@code respondWith...} or {@code throwException} method
	 *
	 * @return test facade instance stubbed for CREATED response.
	 */
	public TestOutboundFacade respondWithCreated()
	{
		return respondWith(ResponseEntity.created(SOME_URI));
	}

	/**
	 * Specifies to respond with HTTP 404 Not Found to all requests it receives, unless this spec is not overridden then by a
	 * subsequent call to a {@code respondWith...} or {@code throwException} method.
	 *
	 * @return facade with the response specification applied.
	 */
	public TestOutboundFacade respondWithNotFound()
	{
		return respondWith(ResponseEntity.notFound());
	}

	/**
	 * Specifies to respond with HTTP 400 Bad Request to all requests it receives, unless this spec is not overridden then by a
	 * subsequent call to a {@code respondWith...} or {@code throwException} method.
	 *
	 * @return facade with the response specification applied.
	 */
	public TestOutboundFacade respondWithBadRequest()
	{
		return respondWith(ResponseEntity.badRequest());
	}

	/**
	 * Specifies to respond with HTTP 500 Internal Server Error to all requests it receives, unless this spec is not overridden then by a
	 * subsequent call to a {@code respondWith...} or {@code throwException} method.
	 *
	 * @return facade with the response specification applied.
	 */
	public TestOutboundFacade respondWithServerError()
	{
		return respondWith(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@Override
	public TestOutboundFacade throwException(final RuntimeException e)
	{
		return super.throwException(e);
	}

	/**
	 * Specifies to perform an action before return the specified response from the {@link #send(ItemModel, String, String)} method.
	 *
	 * @param builder a builder for the response to return.
	 * @param act     an action to perform before the response is returned. This allows to inject time-sensitive behavior
	 *                to the test to call certain code when a desired item is being processed instead of relying on guessing the
	 *                right time to execute that code in parallel.
	 * @return a facade with the response specified.
	 */
	public TestOutboundFacade doAndRespondWith(final ResponseEntity.HeadersBuilder builder, final Runnable act)
	{
		final ResponseEntity<Map> entity = builder.build();
		return performRunnableAndRespondWith(act, entity);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated use {@link #send(SyncParameters)} instead.
	 */
	@Override
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public synchronized Observable<ResponseEntity<Map>> send(final ItemModel itemModel, final String integrationObjectCode,
	                                                         final String destination)
	{
		final SyncParameters syncParameters = SyncParameters.syncParametersBuilder().withItem(itemModel)
		                                                    .withIntegrationObjectCode(integrationObjectCode)
		                                                    .withDestinationId(destination)
		                                                    .withSource(OutboundSource.OUTBOUNDSYNC)
		                                                    .build();
		return this.send(syncParameters);
	}

	@Override
	public synchronized Observable<ResponseEntity<Map>> send(final SyncParameters parameters)
	{
		final var response = internalSend(parameters);
		return Observable.just(response);
	}
}
