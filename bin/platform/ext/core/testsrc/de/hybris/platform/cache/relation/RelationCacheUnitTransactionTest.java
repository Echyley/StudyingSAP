/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cache.relation;

import static de.hybris.platform.cache.relation.RelationCacheKey.RELATION;
import static de.hybris.platform.core.model.security.PrincipalGroupModel.MEMBERS;
import static de.hybris.platform.core.model.security.PrincipalGroupModel._PRINCIPALGROUPRELATION;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cache.AbstractCacheUnit;
import de.hybris.platform.cache.Cache;
import de.hybris.platform.cache.InvalidationListener;
import de.hybris.platform.cache.InvalidationTopic;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.SlaveTenant;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.threadregistry.OperationInfo;
import de.hybris.platform.core.threadregistry.RegistrableThread;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.tx.Transaction;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class RelationCacheUnitTransactionTest extends ServicelayerBaseTest
{
	private static final int itemCreatedBeforeTxNrOne = 1234;
	private static final int itemCreatedInTx = 5678;
	private static final int itemCreatedBeforeTxNrTwo = 8901;

	private static final PK itemCreatedBeforeTxNrOnePK = PK.fromLong(itemCreatedBeforeTxNrOne);
	private static final PK itemCreatedInTxPK = PK.fromLong(itemCreatedInTx);
	private static final PK itemCreatedBeforeTxNrTwoPK = PK.fromLong(itemCreatedBeforeTxNrTwo);
	private static final PK artificialPk = PK.fromLong(123489019);

	public static final String TYPECODE = "4";
	private static final Object[] KEY_1234 = { Cache.CACHEKEY_HJMP, Cache.CACHEKEY_ENTITY, TYPECODE, itemCreatedBeforeTxNrOnePK };
	private static final Object[] KEY_RELATION = { Cache.CACHEKEY_HJMP, Cache.CACHEKEY_ENTITY, TYPECODE, artificialPk };

	private static final InvalidationListener INVALIDATION_LISTENER_HJMP_ENTITY = (key, invalidationType, target, remoteSrc) -> {
		if (key.length > 2 && TYPECODE.equals(key[2]))
		{
			if (itemCreatedBeforeTxNrOnePK.equals(key[3]))
			{
				target.invalidate(new Object[]{ RELATION, _PRINCIPALGROUPRELATION, MEMBERS, itemCreatedBeforeTxNrOnePK },
						invalidationType);
				return;
			}

			if (artificialPk.equals(key[3]))
			{
				target.invalidate(new Object[]{ RELATION, _PRINCIPALGROUPRELATION }, invalidationType);
				return;
			}
		}
	};

	@Before
	public void before()
	{

		final InvalidationTopic topicHjmp = Registry.getCurrentTenant().getInvalidationManager()
		                                            .getInvalidationTopic(new String[]
				                                            { Cache.CACHEKEY_HJMP, Cache.CACHEKEY_ENTITY });
		topicHjmp.addInvalidationListener(INVALIDATION_LISTENER_HJMP_ENTITY);
	}

	@After
	public void after()
	{

		final InvalidationTopic topicHjmp = Registry.getCurrentTenant().getInvalidationManager()
		                                            .getInvalidationTopic(new String[]
				                                            { Cache.CACHEKEY_HJMP, Cache.CACHEKEY_ENTITY });
		topicHjmp.removeInvalidationListener(INVALIDATION_LISTENER_HJMP_ENTITY);
	}

	@Test
	public void testScenarionInTransaction() throws Exception
	{
		final String globalCacheId = "globalCache";
		final TestCacheUnit globalCache = new TestCacheUnit(itemCreatedBeforeTxNrOnePK, globalCacheId);
		final Object globalCacheResult = globalCache.get();
		assertThat(globalCache.getCounter()).isEqualTo(1);
		assertThat(globalCacheResult).isEqualTo(globalCacheId);
		assertThat(globalCache.internalValue()).isEqualTo(globalCacheId);

		final String getFromGlobalCacheId =  "getFromGlobalCache";
		final TestCacheUnit getFromGlobalCache = new TestCacheUnit(itemCreatedBeforeTxNrOnePK, getFromGlobalCacheId);
		final Object getFromGlobalCacheResult = getFromGlobalCache.get();
		assertThat(getFromGlobalCache.getCounter()).isZero();
		assertThat(globalCache.getCounter()).isEqualTo(1);
		assertThat(getFromGlobalCacheResult).isEqualTo(globalCacheResult).isEqualTo(globalCacheId);
		assertThat(getFromGlobalCache.internalValue()).isEqualTo(getFromGlobalCacheId);

		final String globalCacheItemTwoId =  "globalCacheItemTwo";
		final TestCacheUnit globalCacheItemTwo = new TestCacheUnit(itemCreatedBeforeTxNrTwoPK, globalCacheItemTwoId);
		final Object globalCacheItemTwoResult = globalCacheItemTwo.get();
		assertThat(globalCacheItemTwo.getCounter()).isEqualTo(1);
		assertThat(globalCacheItemTwoResult).isEqualTo(globalCacheItemTwoId);
		assertThat(globalCacheItemTwo.internalValue()).isEqualTo(globalCacheItemTwoId);

		final String getFromGlobalCacheIdItemTwo = "getFromGlobalCacheItemTwo";
		final TestCacheUnit getFromGlobalCacheItemTwo = new TestCacheUnit(itemCreatedBeforeTxNrTwoPK, getFromGlobalCacheIdItemTwo);
		final Object getFromGlobalCacheResultItemTwo = getFromGlobalCacheItemTwo.get();
		assertThat(getFromGlobalCacheItemTwo.getCounter()).isZero();
		assertThat(globalCacheItemTwo.getCounter()).isEqualTo(1);
		assertThat(getFromGlobalCacheResultItemTwo).isEqualTo(globalCacheItemTwoResult).isEqualTo(globalCacheItemTwoId);
		assertThat(getFromGlobalCacheItemTwo.internalValue()).isEqualTo(getFromGlobalCacheIdItemTwo);

		final Transaction transaction = Transaction.current();
		transaction.begin();

		final String getFromGlobalCacheInTxId = "getFromGlobalCacheInTx";
		final TestCacheUnit getFromGlobalCacheInTx = new TestCacheUnit(itemCreatedBeforeTxNrOnePK, getFromGlobalCacheInTxId);
		final Object getFromGlobalCacheInTxResult = getFromGlobalCacheInTx.get();
		assertThat(getFromGlobalCacheInTx.getCounter()).isZero();
		assertThat(globalCache.getCounter()).isEqualTo(1);
		assertThat(getFromGlobalCacheInTxResult).isEqualTo(globalCacheResult).isEqualTo(globalCacheId);
		assertThat(getFromGlobalCacheInTx.internalValue()).isEqualTo(getFromGlobalCacheInTxId);

		final String getFromGlobalCacheInTxSecondTimeId = "getFromGlobalCacheInTxSecondTimeId";
		final TestCacheUnit getFromGlobalCacheInTxSecondTime = new TestCacheUnit(itemCreatedBeforeTxNrOnePK, getFromGlobalCacheInTxSecondTimeId);
		final Object getFromGlobalCacheInTxSecondTimeResult = getFromGlobalCacheInTxSecondTime.get();
		assertThat(getFromGlobalCacheInTxSecondTime.getCounter()).isZero();
		assertThat(globalCache.getCounter()).isEqualTo(1);
		assertThat(getFromGlobalCacheInTxSecondTimeResult).isEqualTo(globalCacheResult).isEqualTo(globalCacheId);
		assertThat(getFromGlobalCacheInTxSecondTime.internalValue()).isEqualTo(getFromGlobalCacheInTxSecondTimeId);

		transaction.invalidate(KEY_1234, 3, AbstractCacheUnit.INVALIDATIONTYPE_MODIFIED);

		final String txCacheId = "txCache";
		final TestCacheUnit txCache = new TestCacheUnit(itemCreatedBeforeTxNrOnePK, txCacheId);
		final Object txCacheResult = txCache.get();
		assertThat(txCache.getCounter()).isEqualTo(1);
		assertThat(globalCache.getCounter()).isEqualTo(1);
		assertThat(txCacheResult).isEqualTo(txCacheId).isNotEqualTo(globalCacheResult);
		assertThat(txCache.internalValue()).isEqualTo(txCacheId);

		final String txCacheOnlyId = "txCacheOnly";
		final TestCacheUnit txCacheOnly = new TestCacheUnit(itemCreatedInTxPK, txCacheOnlyId);
		final Object txCacheOnlyResult = txCacheOnly.get();
		assertThat(txCacheOnly.getCounter()).isEqualTo(1);
		assertThat(txCacheOnlyResult).isEqualTo(txCacheOnlyId);
		assertThat(txCacheOnly.internalValue()).isEqualTo(txCacheOnlyId);

		transaction.invalidate(KEY_RELATION, 3, AbstractCacheUnit.INVALIDATIONTYPE_MODIFIED);

		final String getFromTxCacheId = "getFromTxCache";
		final TestCacheUnit getFromTxCache = new TestCacheUnit(itemCreatedBeforeTxNrOnePK, getFromTxCacheId);
		final Object getFromTxCacheResult = getFromTxCache.get();
		assertThat(getFromTxCache.getCounter()).isZero();
		assertThat(txCache.getCounter()).isEqualTo(2);
		assertThat(getFromTxCacheResult).isEqualTo(txCacheResult).isEqualTo(txCacheId);
		assertThat(getFromTxCache.internalValue()).isEqualTo(getFromTxCacheId);

		final String getFromTxCacheOnlyId = "getFromTxCacheOnly";
		final TestCacheUnit getFromTxCacheOnly = new TestCacheUnit(itemCreatedInTxPK, getFromTxCacheOnlyId);
		final Object getFromTxCacheOnlyResult = getFromTxCacheOnly.get();
		assertThat(getFromTxCacheOnly.getCounter()).isEqualTo(1);
		assertThat(txCacheOnly.getCounter()).isEqualTo(1);
		assertThat(getFromTxCacheOnlyResult).isEqualTo(getFromTxCacheOnlyId).isNotEqualTo(txCacheOnlyId);
		assertThat(getFromTxCacheOnly.internalValue()).isEqualTo(getFromTxCacheOnlyId);

		final String getFromTxCacheSecondTimeId = "getFromTxCacheSecondTime";
		final TestCacheUnit getFromTxCacheSecondTime = new TestCacheUnit(itemCreatedBeforeTxNrOnePK, getFromTxCacheSecondTimeId);
		final Object getFromTxCacheSecondTimeResult = getFromTxCacheSecondTime.get();
		assertThat(getFromTxCacheSecondTime.getCounter()).isZero();
		assertThat(txCache.getCounter()).isEqualTo(2);
		assertThat(getFromTxCacheSecondTimeResult).isEqualTo(txCacheResult).isEqualTo(txCacheId);
		assertThat(getFromTxCacheSecondTime.internalValue()).isEqualTo(getFromTxCacheSecondTimeId);

		executeInNewThread(() -> {
			final String getFromGlobalCacheOutsideTxId = "getFromGlobalCacheOutsideTx";
			final TestCacheUnit getFromGlobalCacheOutsideTx = new TestCacheUnit(itemCreatedBeforeTxNrOnePK, getFromGlobalCacheOutsideTxId);
			final Object getFromGlobalCacheOutsideTxResult = getFromGlobalCacheOutsideTx.get();
			assertThat(getFromGlobalCacheOutsideTx.getCounter()).isZero();
			assertThat(getFromGlobalCacheOutsideTxResult).isEqualTo(globalCacheResult).isEqualTo(globalCacheId);
			assertThat(getFromGlobalCacheOutsideTx.internalValue()).isEqualTo(getFromGlobalCacheOutsideTxId);

			final String getFromGlobalCacheOutsideTxSecondItemId = "getFromGlobalCacheOutsideTxSecondItem";
			final TestCacheUnit getFromGlobalCacheOutsideTxSecondItem = new TestCacheUnit(itemCreatedBeforeTxNrTwoPK, getFromGlobalCacheOutsideTxSecondItemId);
			final Object getFromGlobalCacheOutsideTxSecondItemResult = getFromGlobalCacheOutsideTxSecondItem.get();
			assertThat(getFromGlobalCacheOutsideTxSecondItem.getCounter()).isZero();
			assertThat(getFromGlobalCacheOutsideTxSecondItemResult).isEqualTo(globalCacheItemTwoResult).isEqualTo(globalCacheItemTwoId);
			assertThat(getFromGlobalCacheOutsideTxSecondItem.internalValue()).isEqualTo(getFromGlobalCacheOutsideTxSecondItemId);
			return null;
		});

		final String getFromTxCacheOnlySecondTimeId = "getFromTxCacheOnlySecondTime";
		final TestCacheUnit getFromTxCacheOnlySecondTime = new TestCacheUnit(itemCreatedInTxPK, getFromTxCacheOnlySecondTimeId);
		final Object getFromTxCacheOnlySecondTimeResult = getFromTxCacheOnlySecondTime.get();
		assertThat(getFromTxCacheOnlySecondTime.getCounter()).isZero();
		assertThat(getFromTxCacheOnly.getCounter()).isEqualTo(1);
		assertThat(getFromTxCacheOnlySecondTimeResult).isEqualTo(getFromTxCacheOnlyResult).isEqualTo(getFromTxCacheOnlyId);
		assertThat(getFromTxCacheOnlySecondTime.internalValue()).isEqualTo(getFromTxCacheOnlySecondTimeId);

		final String txCacheItemTwoId = "txCacheItemTwo";
		final TestCacheUnit txCacheItemTwo = new TestCacheUnit(itemCreatedBeforeTxNrTwoPK, txCacheItemTwoId);
		final Object txCacheItemTwoResult = txCacheItemTwo.get();
		assertThat(txCacheItemTwo.getCounter()).isEqualTo(1);
		assertThat(txCacheItemTwoResult).isEqualTo(txCacheItemTwoId).isNotEqualTo(globalCacheItemTwoResult);
		assertThat(txCacheItemTwo.internalValue()).isEqualTo(txCacheItemTwoId);

		final String getFromTxCacheItemTwoId = "getFromTxCacheItemTwo";
		final TestCacheUnit getFromTxCacheItemTwo = new TestCacheUnit(itemCreatedBeforeTxNrTwoPK, getFromTxCacheItemTwoId);
		final Object getFromTxCacheItemTwoResult = getFromTxCacheItemTwo.get();
		assertThat(getFromTxCacheItemTwo.getCounter()).isZero();
		assertThat(txCacheItemTwo.getCounter()).isEqualTo(1);
		assertThat(getFromTxCacheItemTwoResult).isEqualTo(txCacheItemTwoResult).isEqualTo(txCacheItemTwoId);
		assertThat(getFromTxCacheItemTwo.internalValue()).isEqualTo(getFromTxCacheItemTwoId);

		transaction.commit();

		final String afterTxId = "afterTx";
		final TestCacheUnit afterTx = new TestCacheUnit(itemCreatedBeforeTxNrOnePK, afterTxId);
		final Object afterTxResult = afterTx.get();
		assertThat(afterTx.getCounter()).isEqualTo(1);
		assertThat(afterTxResult).isEqualTo(afterTxId).isNotEqualTo(globalCacheResult);
		assertThat(afterTx.internalValue()).isEqualTo(afterTxId);

		final String afterTxItemTwoId = "afterTxItemTwo";
		final TestCacheUnit afterTxItemTwo = new TestCacheUnit(itemCreatedBeforeTxNrTwoPK, afterTxItemTwoId);
		final Object afterTxItemTwoResult = afterTxItemTwo.get();
		assertThat(afterTxItemTwo.getCounter()).isEqualTo(1);
		assertThat(afterTxItemTwoResult).isEqualTo(afterTxItemTwoId).isNotEqualTo(globalCacheItemTwoResult);
		assertThat(afterTxItemTwo.internalValue()).isEqualTo(afterTxItemTwoId);

		final String globalCacheAfterTxId = "globalCacheAfterTx";
		final TestCacheUnit globalCacheAfterTx = new TestCacheUnit(itemCreatedInTxPK, globalCacheAfterTxId);
		final Object globalCacheAfterTxResult = globalCacheAfterTx.get();
		assertThat(globalCacheAfterTx.getCounter()).isEqualTo(1);
		assertThat(getFromTxCacheOnly.getCounter()).isEqualTo(1);
		assertThat(globalCacheAfterTxResult).isEqualTo(globalCacheAfterTxId).isNotEqualTo(getFromTxCacheOnlyResult);
		assertThat(globalCacheAfterTx.internalValue()).isEqualTo(globalCacheAfterTxId);
	}

	private void executeInNewThread(final Callable action) throws Exception
	{
		final CountDownLatch actionFinished = new CountDownLatch(1);
		executeAndCountDown(action, actionFinished).get(20, TimeUnit.SECONDS);
		actionFinished.await(25, TimeUnit.SECONDS);
	}

	private Future<?> executeAndCountDown(final Callable action, final CountDownLatch transactionFinished)
	{
		return startNewTenantThread(() ->
		{
			try
			{
				return action.call();
			}
			finally
			{
				transactionFinished.countDown();
			}
		});
	}

	private Future<?> startNewTenantThread(final Callable<?> callable)
	{
		final Tenant currentTenant = Registry.getCurrentTenantNoFallback();
		return Executors.newSingleThreadExecutor().submit(() -> {
			RegistrableThread.registerThread(OperationInfo.builder().withCategory(OperationInfo.Category.TEST).asNotSuspendableOperation().build());
			Registry.setCurrentTenant(currentTenant);
			try
			{
				return callable.call();
			}
			finally
			{
				RegistrableThread.unregisterThread();
			}
		});
	}

	private static class TestCacheUnit extends RelationCacheUnit
	{

		private final String marker;
		private volatile int counter = 0;

		protected TestCacheUnit(final PK sourcePK, final String marker)
		{
			super(Registry.getCurrentTenant().getCache(),
					new RelationCacheKey(SlaveTenant.JUNIT_TENANT_ID, _PRINCIPALGROUPRELATION, MEMBERS, sourcePK));
			this.marker = marker;
		}

		public String internalValue()
		{
			return this.marker;
		}

		protected int getCounter()
		{
			return this.counter;
		}

		@Override
		public Object compute()
		{
			synchronized (this)
			{
				counter++;
				return this.marker;
			}
		}
	}
}