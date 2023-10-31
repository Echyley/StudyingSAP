/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.tx;

import static de.hybris.platform.cache.AbstractCacheUnit.INVALIDATIONTYPE_MODIFIED;
import static de.hybris.platform.cache.AbstractCacheUnit.INVALIDATIONTYPE_REMOVED;
import static de.hybris.platform.core.model.security.PrincipalGroupModel.GROUPS;
import static de.hybris.platform.core.model.security.PrincipalGroupModel._PRINCIPALGROUPRELATION;
import static de.hybris.platform.cache.relation.RelationCacheKey.RELATION;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cache.InvalidationListener;
import de.hybris.platform.cache.InvalidationManager;
import de.hybris.platform.cache.InvalidationTopic;
import de.hybris.platform.cache.relation.RelationCacheUnit;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.threadregistry.RegistrableThread;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

@IntegrationTest
public class CacheInfrastructureGlobalAndTxIsolationIntegrationTest extends ServicelayerBaseTest
{
	private static final Object[] RELATION_TOPIC_KEY = new Object[]{ RELATION, _PRINCIPALGROUPRELATION };
	private static final InvalidationListener INVALIDATION_LISTENER = (key, invalidationType, target, remoteSrc) -> target.invalidate(
			key, invalidationType);

	private final PropertyConfigSwitcher enableTxCacheProperty = new PropertyConfigSwitcher(Transaction.CFG_ENABLE_TX_CACHE);

	@Resource
	ModelService modelService;
	@Resource
	JdbcTemplate jdbcTemplate;

	private InvalidationManager invalidationManager;

	@Before
	public void setUp()
	{
		invalidationManager = Registry.getCurrentTenant().getInvalidationManager();
		final InvalidationTopic invalidationTopic = InvalidationManager.getInstance().getOrCreateInvalidationTopic(
				RELATION_TOPIC_KEY);
		invalidationTopic.addInvalidationListener(INVALIDATION_LISTENER);
		Registry.getCurrentTenant().getCache().clear();
	}

	@After
	public void tearDown()
	{
		enableTxCacheProperty.switchBackToDefault();
		final InvalidationTopic topic = invalidationManager.getInvalidationTopic(RELATION_TOPIC_KEY);
		topic.removeInvalidationListener(INVALIDATION_LISTENER);
	}


	@Test
	public void shouldReturnCachedValuesIfRemovedByNonPersistenceLayer()
	{
		final RelationCachePopulator cacheChecker = new RelationCachePopulatorImpl(modelService, jdbcTemplate);
		final PK sourcePK = cacheChecker.createSourceItem();

		final PK targetPK1 = cacheChecker.createTargetItem(sourcePK);
		final PK targetPK2 = cacheChecker.createTargetItem(sourcePK);

		assertThat(cacheChecker.getTargetSide(sourcePK)).containsExactlyInAnyOrder(targetPK1, targetPK2);

		cacheChecker.deleteRelationInstanceNonPersistenceLayer(sourcePK, targetPK1);

		assertThat(cacheChecker.getTargetSide(sourcePK)).containsExactlyInAnyOrder(targetPK1, targetPK2);
	}

	@Test
	public void shouldReturnNewValuesIfRemovedByPersistenceLayer()
	{
		final RelationCachePopulator cacheChecker = new RelationCachePopulatorImpl(modelService, jdbcTemplate);
		final PK sourcePK = cacheChecker.createSourceItem();

		final PK targetPK1 = cacheChecker.createTargetItem(sourcePK);
		final PK targetPK2 = cacheChecker.createTargetItem(sourcePK);

		assertThat(cacheChecker.getTargetSide(sourcePK)).containsExactlyInAnyOrder(targetPK1, targetPK2);

		cacheChecker.deleteRelationInstancePersistenceLayer(sourcePK, targetPK1);

		assertThat(cacheChecker.getTargetSide(sourcePK)).containsExactlyInAnyOrder(targetPK2);
	}

	@Test
	public void shouldReturnAll4ValuesIfRunFromTransaction() throws Exception
	{
		final RelationCachePopulator cacheChecker = new RelationCachePopulatorImpl(modelService, jdbcTemplate);
		final PK sourcePK = cacheChecker.createSourceItem();

		final Set<PK> createdTargetPKs = new HashSet<>();

		createdTargetPKs.add(cacheChecker.createTargetItem(sourcePK));
		createdTargetPKs.add(cacheChecker.createTargetItem(sourcePK));

		assertThat(cacheChecker.getTargetSide(sourcePK)).containsOnlyElementsOf(createdTargetPKs).hasSize(2);

		final List<PK> result =  executeInTransaction(true, () -> {

			createdTargetPKs.add(cacheChecker.createTargetItem(sourcePK));
			createdTargetPKs.add(cacheChecker.createTargetItem(sourcePK));

			return cacheChecker.getTargetSide(sourcePK);
		});

		assertThat(result).containsOnlyElementsOf(createdTargetPKs).hasSize(4);
	}


	@Test
	public void shouldReturnOnly2ValuesIfRunFromOutsideOfTransaction() throws Exception
	{
		final RelationCachePopulator cacheChecker = new RelationCachePopulatorImpl(modelService, jdbcTemplate);
		final PK sourcePK = cacheChecker.createSourceItem();

		final Set<PK> createdTargetPKs = new HashSet<>();

		createdTargetPKs.add(cacheChecker.createTargetItem(sourcePK));
		createdTargetPKs.add(cacheChecker.createTargetItem(sourcePK));

		assertThat(cacheChecker.getTargetSide(sourcePK)).containsOnlyElementsOf(createdTargetPKs).hasSize(2);

		final CountDownLatch transactionFinished = new CountDownLatch(1);
		executeInTransactionAwaitForBarrier(true, () -> {
			cacheChecker.createTargetItem(sourcePK);
			cacheChecker.createTargetItem(sourcePK);
			return null;
		}, transactionFinished);

		final Set<PK> targetPKsFromCache = Set.copyOf(cacheChecker.getTargetSide(sourcePK));
		awaitBarrier(transactionFinished);
		assertThat(targetPKsFromCache).containsOnlyElementsOf(createdTargetPKs).hasSize(2);
	}

	@Test
	public void shouldReturnDifferent2ValuesInBothTransactions() throws Exception
	{
		final RelationCachePopulator cacheChecker = new RelationCachePopulatorImpl(modelService, jdbcTemplate);
		final PK sourcePK = cacheChecker.createSourceItem();

		final Set<PK> createdTargetPKsFromTransA = new HashSet<>();

		createdTargetPKsFromTransA.add(cacheChecker.createTargetItem(sourcePK));
		createdTargetPKsFromTransA.add(cacheChecker.createTargetItem(sourcePK));

		assertThat(cacheChecker.getTargetSide(sourcePK)).containsOnlyElementsOf(createdTargetPKsFromTransA).hasSize(2);

		final Set<PK> createdTargetPKsFromTransB = new HashSet<>(createdTargetPKsFromTransA);
		final CountDownLatch transactionBLatch = new CountDownLatch(1);
		final CountDownLatch transactionFinished = new CountDownLatch(1);

		final List<PK> transA =  executeInTransactionAwaitForBarrierWithTimeout(true, () -> {
			createdTargetPKsFromTransA.add(cacheChecker.createTargetItem(sourcePK));
			createdTargetPKsFromTransA.add(cacheChecker.createTargetItem(sourcePK));
			transactionBLatch.countDown();
			return cacheChecker.getTargetSide(sourcePK);
		}, transactionFinished);

		final List<PK> transB = executeInTransactionAwaitForBarrierWithTimeout(true, () -> {
			transactionBLatch.await(20, TimeUnit.SECONDS);
			createdTargetPKsFromTransB.add(cacheChecker.createTargetItem(sourcePK));
			createdTargetPKsFromTransB.add(cacheChecker.createTargetItem(sourcePK));
			return cacheChecker.getTargetSide(sourcePK);
		}, transactionFinished);

		assertThat(transA).containsOnlyElementsOf(createdTargetPKsFromTransA).hasSize(4);
		assertThat(transB).containsOnlyElementsOf(createdTargetPKsFromTransB).hasSize(4);
	}

	@Test
	public void shouldReturnAll4ValuesAfterCommit() throws Exception
	{
		final RelationCachePopulator cacheChecker = new RelationCachePopulatorImpl(modelService, jdbcTemplate);
		final PK sourcePK = cacheChecker.createSourceItem();

		final Set<PK> createdTargetPKs = new HashSet<>();

		createdTargetPKs.add(cacheChecker.createTargetItem(sourcePK));
		createdTargetPKs.add(cacheChecker.createTargetItem(sourcePK));

		assertThat(cacheChecker.getTargetSide(sourcePK)).containsOnlyElementsOf(createdTargetPKs).hasSize(2);

		final List<PK> result = executeInTransaction(false, () -> {
			createdTargetPKs.add(cacheChecker.createTargetItem(sourcePK));
			createdTargetPKs.add(cacheChecker.createTargetItem(sourcePK));
			return cacheChecker.getTargetSide(sourcePK);
		});

		assertThat(result).containsOnlyElementsOf(createdTargetPKs).hasSize(4);
	}

	@Test
	public void shouldReturnOnly2ValuesAfterRollback() throws Exception
	{
		final RelationCachePopulator cacheChecker = new RelationCachePopulatorImpl(modelService, jdbcTemplate);
		final PK sourcePK = cacheChecker.createSourceItem();

		final Set<PK> createdTargetPKs = new HashSet<>();

		createdTargetPKs.add(cacheChecker.createTargetItem(sourcePK));
		createdTargetPKs.add(cacheChecker.createTargetItem(sourcePK));

		assertThat(cacheChecker.getTargetSide(sourcePK)).containsOnlyElementsOf(createdTargetPKs).hasSize(2);

		executeInTransaction(true, () -> {

			cacheChecker.createTargetItem(sourcePK);
			cacheChecker.createTargetItem(sourcePK);
			return null;
		});

		assertThat(cacheChecker.getTargetSide(sourcePK)).containsOnlyElementsOf(createdTargetPKs).hasSize(2);
	}

	private void awaitBarrier(final CountDownLatch finishActionsInSync)
	{
		try
		{
			finishActionsInSync.await(20, TimeUnit.SECONDS);
		}
		catch (final InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}

	private List<PK> executeInTransaction(final boolean rollback, final Callable action) throws Exception
	{
		final CountDownLatch transactionFinished = new CountDownLatch(1);
		final List<PK> pks = executeInTransactionAwaitForBarrierWithTimeout(rollback, action, transactionFinished);
		awaitBarrier(transactionFinished);
		return pks;
	}

	private List<PK> executeInTransactionAwaitForBarrierWithTimeout(final boolean rollback, final Callable action, final CountDownLatch transactionFinished)
			throws InterruptedException, ExecutionException, TimeoutException
	{
		return (List<PK>)executeInTransactionAwaitForBarrier(rollback, action, transactionFinished).get(20,
				TimeUnit.SECONDS);
	}

	private Future<?> executeInTransactionAwaitForBarrier(final boolean rollback, final Callable action, final CountDownLatch transactionFinished)
	{
		return startNewTenantThread(() ->
		{
			final Transaction transaction = Transaction.current();
			try
			{
				transaction.begin();

				return action.call();
			}
			catch (final Exception e)
			{
				transaction.rollback();
				throw e;
			}
			finally
			{
				if (rollback)
				{
					transaction.rollback();
				}
				else
				{
					transaction.commit();
				}

				transactionFinished.countDown();
			}
		});
	}

	private Future<?> startNewTenantThread(final Callable<?> callable)
	{
		final Tenant currentTenant = Registry.getCurrentTenantNoFallback();
		return Executors.newCachedThreadPool(RegistrableThread::new).submit(() -> {
			Registry.setCurrentTenant(currentTenant);
			return callable.call();
		});
	}

	private String unique(final String prefix)
	{
		return prefix + "_" + UUID.randomUUID();
	}

	private RelationCacheUnit createCacheUnit(final PK sourcePK)
	{
		return RelationCacheUnit.createRelationCacheUnit(_PRINCIPALGROUPRELATION, GROUPS,
				sourcePK);
	}


	public class RelationCachePopulatorImpl implements RelationCachePopulator
	{
		private static final String SOURCE_COLUMN_NAME = "sourcepk";
		private static final String TARGET_COLUMN_NAME = "targetpk";
		private static final String DELETE_FROM_PGRELS_TABLE_QUERY = "DELETE FROM %s WHERE %s = ? AND %s = ?";

		private final ModelService modelService;
		private final JdbcTemplate jdbcTemplate;

		public RelationCachePopulatorImpl(final ModelService modelService, final JdbcTemplate jdbcTemplate)
		{
			this.modelService = modelService;
			this.jdbcTemplate = jdbcTemplate;
		}

		@Override
		public PK createSourceItem()
		{
			final UserModel principal = modelService.create(UserModel.class);
			principal.setUid(unique("Principal"));
			modelService.save(principal);

			return principal.getPk();
		}

		@Override
		public PK createTargetItem(final PK sourcePK)
		{
			final UserGroupModel principalGroup = modelService.create(UserGroupModel.class);
			principalGroup.setUid(unique("PrincipalGroup"));

			final UserModel principal = modelService.get(sourcePK);
			principalGroup.setMembers(Set.of(principal));

			modelService.save(principalGroup);

			createCacheUnit(sourcePK).invalidate(INVALIDATIONTYPE_MODIFIED);

			return principalGroup.getPk();
		}

		@Override
		public List<PK> getTargetSide(final PK sourcePK)
		{

			try
			{
				return createCacheUnit(sourcePK).getCachedPKs();
			}
			catch (final Exception e)
			{
				Assert.fail("CacheUnit get value failed!");
				e.printStackTrace();
			}
			return List.of();
		}

		@Override
		public boolean deleteItem(final PK sourcePK)
		{
			try
			{
				modelService.remove(sourcePK);
				createCacheUnit(sourcePK).invalidate(INVALIDATIONTYPE_REMOVED);
			}
			catch (final ModelRemovalException e)
			{
				return false;
			}
			return true;
		}

		@Override
		public boolean deleteTargetSideItems(final PK sourcePK)
		{
			try
			{
				final UserModel principal = modelService.get(sourcePK);
				modelService.removeAll(principal.getGroups());
				createCacheUnit(sourcePK).invalidate(INVALIDATIONTYPE_REMOVED);
			}
			catch (final ModelRemovalException e)
			{
				return false;
			}
			return true;
		}

		@Override
		public boolean deleteRelationInstanceNonPersistenceLayer(final PK sourcePK, final PK targetPK)
		{
			final String relationTableName = createCacheUnit(sourcePK).getTable();
			final String deleteQuery = String.format(DELETE_FROM_PGRELS_TABLE_QUERY, relationTableName,
					SOURCE_COLUMN_NAME,
					TARGET_COLUMN_NAME);
			return jdbcTemplate.update(deleteQuery, sourcePK.getLong(), targetPK.getLong()) > 0;
		}

		@Override
		public boolean deleteRelationInstancePersistenceLayer(final PK sourcePK, final PK targetPK)
		{
			try
			{
				final UserModel principal = modelService.get(sourcePK);
				final Set<PrincipalGroupModel> groupsWithTargetRemoved = principal.getGroups()
				                                                                  .stream()
				                                                                  .filter(group -> !group.getPk()
				                                                                                         .equals(targetPK))
				                                                                  .collect(Collectors.toSet());
				principal.setGroups(groupsWithTargetRemoved);
				modelService.save(principal);
				createCacheUnit(sourcePK).invalidate(INVALIDATIONTYPE_MODIFIED);
			}
			catch (final SystemException e)
			{
				return false;
			}
			return true;
		}
	}
}
