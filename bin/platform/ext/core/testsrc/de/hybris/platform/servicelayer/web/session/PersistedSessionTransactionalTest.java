/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.servicelayer.web.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import java.io.ByteArrayInputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.serialization.ValidatingObjectInputStream;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;


public class PersistedSessionTransactionalTest extends HybrisJUnit4TransactionalTest
{
	@Test
	public void testCreatePersistedSession()
	{
		//https://jira.hybris.com/browse/ECP-2554
		final PersistedSession persistedSession = new PersistedSession("test", 99, "test", "testContext");
		persistedSession.setAttribute("dummy", null);
		persistedSession.setAttribute("dummy2", "testValue");
		final byte[] b = SerializationUtils.serialize(persistedSession);

		final PersistedSession s = deserializePersistedSession(b);
		assertNull(s.getAttribute("dummy"));
		assertEquals("testValue", s.getAttribute("dummy2"));
	}

	private PersistedSession deserializePersistedSession(final byte[] data)
	{
		try
		{
			final ValidatingObjectInputStream vois = new ValidatingObjectInputStream(new ByteArrayInputStream(data));
			vois.accept(PersistedSession.class, ConcurrentHashMap.class, ReentrantLock.class, AbstractQueuedSynchronizer.class,
					AbstractOwnableSynchronizer.class);
			vois.accept("[Ljava.util.concurrent.ConcurrentHashMap$Segment;", //
					"java.util.concurrent.ConcurrentHashMap$Segment", //
					"java.util.concurrent.locks.ReentrantLock$NonfairSync", //
					"java.util.concurrent.locks.ReentrantLock$Sync", //
					"de.hybris.platform.servicelayer.web.session.PersistedSession$SerializableNullObject");
			return (PersistedSession) vois.readObject();
		}
		catch (final Exception exc)
		{
			throw new SerializationException("Could not deserialize as PersistedSession Object", exc);
		}
	}

}
