/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.tx;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cache.AbstractCacheUnit;
import de.hybris.platform.cache.Cache;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class TransactionIntegrationTest extends ServicelayerBaseTest
{
	@Resource
	AfterSaveListenerRegistry afterSaveListenerRegistry;

	AfterSaveEventRecorder recorder;

	@Before
	public void setUp()
	{
		recorder = new AfterSaveEventRecorder(Duration.ofSeconds(5));
		((DefaultAfterSaveListenerRegistry) afterSaveListenerRegistry).addListener(recorder);
	}

	@After
	public void tearDown()
	{
		((DefaultAfterSaveListenerRegistry) afterSaveListenerRegistry).removeListener(recorder);
		recorder = null;
	}

	@Test
	public void shouldRaiseAfterSaveEventsForFiveElementInvalidationOutsideTxUsingInvalidateMethod()
	{
		requireAllKindOfAfterSaveEventsAfterFiveElementInvalidation(false, (creation, modification, removal) -> {
			final Transaction tx = Transaction.current();
			assertThat(tx.isRunning()).isFalse();

			tx.invalidate(creation, 3, AbstractCacheUnit.INVALIDATIONTYPE_CREATED);
			tx.invalidate(modification, 3, AbstractCacheUnit.INVALIDATIONTYPE_MODIFIED);
			tx.invalidate(removal, 3, AbstractCacheUnit.INVALIDATIONTYPE_REMOVED);

			tx.notifyCommit();
		});
	}

	@Test
	public void shouldRaiseAfterSaveEventsForFiveElementInvalidationOutsideTxUsingInvalidateAndNotifyCommitMethod()
	{
		requireAllKindOfAfterSaveEventsAfterFiveElementInvalidation(false, (creation, modification, removal) -> {
			final Transaction tx = Transaction.current();
			assertThat(tx.isRunning()).isFalse();

			tx.invalidateAndNotifyCommit(creation, 3, AbstractCacheUnit.INVALIDATIONTYPE_CREATED);
			tx.invalidateAndNotifyCommit(modification, 3, AbstractCacheUnit.INVALIDATIONTYPE_MODIFIED);
			tx.invalidateAndNotifyCommit(removal, 3, AbstractCacheUnit.INVALIDATIONTYPE_REMOVED);
		});
	}

	@Test
	public void shouldRaiseAfterSaveEventsForFiveElementInvalidationOutsideTxUsingInvalidateFromDirectPersistenceMethod()
	{
		requireAllKindOfAfterSaveEventsAfterFiveElementInvalidation(false, (creation, modification, removal) -> {
			final Transaction tx = Transaction.current();
			assertThat(tx.isRunning()).isFalse();

			tx.invalidateFromDirectPersistence(creation, (PK) creation[3], AbstractCacheUnit.INVALIDATIONTYPE_CREATED);
			tx.invalidateFromDirectPersistence(modification, (PK) modification[3], AbstractCacheUnit.INVALIDATIONTYPE_MODIFIED);
			tx.invalidateFromDirectPersistence(removal, (PK) removal[3], AbstractCacheUnit.INVALIDATIONTYPE_REMOVED);

			tx.notifyCommit();
		});
	}

	@Test
	public void shouldRaiseAfterSaveEventsForFiveElementInvalidationInsideTxUsingInvalidateMethod()
	{
		requireAllKindOfAfterSaveEventsAfterFiveElementInvalidation(true, (creation, modification, removal) -> {
			final Transaction tx = Transaction.current();
			assertThat(tx.isRunning()).isTrue();

			tx.invalidate(creation, 3, AbstractCacheUnit.INVALIDATIONTYPE_CREATED);
			tx.invalidate(modification, 3, AbstractCacheUnit.INVALIDATIONTYPE_MODIFIED);
			tx.invalidate(removal, 3, AbstractCacheUnit.INVALIDATIONTYPE_REMOVED);
		});
	}

	@Test
	public void shouldRaiseAfterSaveEventsForFiveElementInvalidationInsideTxUsingInvalidateAndNotifyCommitMethod()
	{
		requireAllKindOfAfterSaveEventsAfterFiveElementInvalidation(true, (creation, modification, removal) -> {
			final Transaction tx = Transaction.current();
			assertThat(tx.isRunning()).isTrue();

			tx.invalidateAndNotifyCommit(creation, 3, AbstractCacheUnit.INVALIDATIONTYPE_CREATED);
			tx.invalidateAndNotifyCommit(modification, 3, AbstractCacheUnit.INVALIDATIONTYPE_MODIFIED);
			tx.invalidateAndNotifyCommit(removal, 3, AbstractCacheUnit.INVALIDATIONTYPE_REMOVED);
		});
	}

	@Test
	public void shouldRaiseAfterSaveEventsForFiveElementInvalidationInsideTxUsingInvalidateFromDirectPersistenceMethod()
	{
		requireAllKindOfAfterSaveEventsAfterFiveElementInvalidation(true, (creation, modification, removal) -> {
			final Transaction tx = Transaction.current();
			assertThat(tx.isRunning()).isTrue();

			tx.invalidateFromDirectPersistence(creation, (PK) creation[3], AbstractCacheUnit.INVALIDATIONTYPE_CREATED);
			tx.invalidateFromDirectPersistence(modification, (PK) modification[3], AbstractCacheUnit.INVALIDATIONTYPE_MODIFIED);
			tx.invalidateFromDirectPersistence(removal, (PK) removal[3], AbstractCacheUnit.INVALIDATIONTYPE_REMOVED);
		});
	}

	public void requireAllKindOfAfterSaveEventsAfterFiveElementInvalidation(final boolean wrapInTx, final Invalidator invalidator)
	{
		final Object[] creationKey = createFiveElementEntityInvalidationKey();
		final AfterSaveEvent creationEvent = createEvent(creationKey, AfterSaveEvent.CREATE);

		final Object[] modificationKey = createFiveElementEntityInvalidationKey();
		final AfterSaveEvent modificationEvent = createEvent(modificationKey, AfterSaveEvent.UPDATE);

		final Object[] removalKey = createFiveElementEntityInvalidationKey();
		final AfterSaveEvent removalEvent = createEvent(removalKey, AfterSaveEvent.REMOVE);

		assertThat(recorder.doesNotContain(creationEvent)).isTrue();
		assertThat(recorder.doesNotContain(modificationEvent)).isTrue();
		assertThat(recorder.doesNotContain(removalEvent)).isTrue();

		if (wrapInTx)
		{
			try
			{
				Transaction.current().execute(new TransactionBody()
				{
					@Override
					public <T> T execute()
					{
						invalidator.invalidate(creationKey, modificationKey, removalKey);
						return null;
					}
				});
			}
			catch (final Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			invalidator.invalidate(creationKey, modificationKey, removalKey);
		}

		assertThat(recorder.contains(creationEvent)).isTrue();
		assertThat(recorder.contains(modificationEvent)).isTrue();
		assertThat(recorder.contains(removalEvent)).isTrue();
	}

	private AfterSaveEvent createEvent(final Object[] invalidationKey, final int type)
	{
		return new AfterSaveEvent((PK) invalidationKey[3], type);
	}

	private Object[] createFiveElementEntityInvalidationKey()
	{
		final PK pk = PK.createCounterPK(24);
		return new Object[]{ Cache.CACHEKEY_HJMP, Cache.CACHEKEY_ENTITY, pk.getTypeCodeAsString(), pk, "fifth" };
	}

	@FunctionalInterface
	private interface Invalidator
	{
		void invalidate(Object[] creationKey, Object[] modificationKey, Object[] removalKey);
	}

	private static class AfterSaveEventRecorder implements AfterSaveListener
	{
		private final long maxWaitMs;
		private final Set<AfterSaveEvent> recordedEvents = new HashSet<>();

		private AfterSaveEventRecorder(final Duration maxWait)
		{
			this.maxWaitMs = maxWait.toMillis();
		}

		@Override
		public void afterSave(final Collection<AfterSaveEvent> events)
		{
			recordedEvents.addAll(events);
		}

		public boolean doesNotContain(final AfterSaveEvent event)
		{
			return !recordedEvents.contains(event);
		}

		public boolean contains(final AfterSaveEvent event)
		{
			return waitFor(() -> recordedEvents.contains(event));
		}

		private boolean waitFor(final BooleanSupplier condition)
		{
			long toWait = maxWaitMs;
			while (toWait > 0)
			{
				if (condition.getAsBoolean())
				{
					return true;
				}

				try
				{
					Thread.sleep(100);
				}
				catch (final InterruptedException e)
				{
					Thread.currentThread().interrupt();
					break;
				}
				toWait -= 100;
			}
			return condition.getAsBoolean();
		}
	}
}
