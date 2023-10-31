/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.util;

import de.hybris.platform.outboundservices.facade.SyncParameters;
import de.hybris.platform.outboundservices.service.DeleteRequestSender;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public class TestDeleteRequestSender extends OutboundInvocationTracker<TestDeleteRequestSender> implements DeleteRequestSender
{
	@Override
	protected ResponseEntity<Map> createDefaultResponse()
	{
		return null;
	}

	/**
	 * Stubs this test sender to simulate a success response from the remote system.
	 *
	 * @return a sender with the success response stubbed
	 */
	public TestDeleteRequestSender respondWithSuccess()
	{
		return respondWith(ResponseEntity.ok());
	}

	/**
	 * Stubs this test sender to simulate an response from the remote system.
	 *
	 * @return a sender with the error response stubbed
	 */
	public TestDeleteRequestSender respondWithError()
	{
		return respondWithError(null);
	}

	@Override
	public TestDeleteRequestSender performRunnableAndRespondWith(final Runnable act, final ResponseEntity.BodyBuilder builder)
	{
		final var response = builder.build();
		if (response.getStatusCodeValue() >= 400)
		{
			return respondWithError(act);
		}
		return super.performRunnableAndRespondWith(act, builder);
	}

	private TestDeleteRequestSender respondWithError(final Runnable act)
	{
		// DeleteRequestSender does not report status back - in case of DELETE request error an exception is thrown
		final var exception = new RuntimeException("IGNORE - Response code is in error range and does not matter");
		return respondWith(new ExceptionResponse(exception, act));
	}

	@Override
	public synchronized void send(final SyncParameters parameters)
	{
		internalSend(parameters);
	}
}
