/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.cache.relation;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.tx.TransactionBody;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;

@IntegrationTest
public abstract class PrincipalGroupRelationScenariosTest extends ServicelayerBaseTest
{

	@Resource
	ModelService modelService;

	@Before
	public abstract void setUp();

	@After
	public abstract void tearDown();

	@Test
	public void shouldReturnUpdatedDataWhenAddingGroup()
	{
		//given
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();
		modelService.saveAll();
		assertThatThereIsNoRelation(user, group);
		//when
		user.setGroups(Set.of(group));
		modelService.saveAll();
		modelService.refresh(group);

		//then
		assertThat(user.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user);
	}

	@Test
	public void shouldReturnUpdatedDataWhenAddingGroupInTransaction() throws Exception
	{
		//given
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();
		modelService.saveAll();

		//when
		Transaction.current().execute(new TransactionBody()
		{
			@Override
			public <T> T execute() throws Exception
			{
				user.setGroups(Set.of(group));
				modelService.saveAll();
				modelService.refresh(group);

				return null;
			}
		});

		//then
		assertThat(user.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user);
	}

	@Test
	public void shouldReturnUpdatedDataWhenAddingGroupInSeparateThread() throws Exception
	{
		//given
		final Tenant tenant = Registry.getCurrentTenant();
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();
		modelService.saveAll();

		//when
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			final UserModel userModel = modelService.get(user.getPk());
			final UserGroupModel groupModel = modelService.get(group.getPk());
			userModel.setGroups(Set.of(groupModel));
			modelService.saveAll();
			modelService.refresh(groupModel);
		});

		future.get(5, TimeUnit.SECONDS);
		executor.shutdown();

		//then
		assertThat(user.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user);
	}

	@Test
	public void shouldReturnUpdatedDataWhenAddingManyGroups()
	{
		//given
		final UserModel user = givenUser();
		final Set<PrincipalGroupModel> groups = Set.of(givenGroup(), givenGroup());
		modelService.saveAll();
		assertThat(user.getGroups()).isEmpty();
		assertThat(groups).allMatch(group -> group.getMembers().isEmpty());

		//when
		user.setGroups(groups);
		modelService.saveAll();
		groups.forEach(modelService::refresh);

		//then
		assertThat(user.getGroups()).hasSize(2).containsExactlyInAnyOrder(Iterables.toArray(groups, PrincipalGroupModel.class));
		assertThat(groups).allMatch(group -> group.getMembers().size() == 1 && group.getMembers().contains(user));
	}

	@Test
	public void shouldReturnUpdatedDataWhenAddingManyGroupsAllInTransaction() throws Exception
	{
		Transaction.current().execute(new TransactionBody()
		{
			@Override
			public <T> T execute() throws Exception
			{
				//given
				final UserModel user = givenUser();
				final Set<PrincipalGroupModel> groups = Set.of(givenGroup(), givenGroup());
				modelService.saveAll();
				assertThat(user.getGroups()).isEmpty();
				assertThat(groups).allMatch(group -> group.getMembers().isEmpty());

				//when
				user.setGroups(groups);
				modelService.saveAll();
				groups.forEach(modelService::refresh);

				//then
				assertThat(user.getGroups()).hasSize(2)
				                            .containsExactlyInAnyOrder(Iterables.toArray(groups, PrincipalGroupModel.class));
				assertThat(groups).allMatch(group -> group.getMembers().size() == 1 && group.getMembers().contains(user));

				return null;
			}
		});
	}

	@Test
	public void shouldReturnUpdatedDataWhenAddingManyGroupsInSeparateThread() throws Exception
	{
		//given
		final Tenant tenant = Registry.getCurrentTenant();
		final UserModel user = givenUser();
		final UserGroupModel group1 = givenGroup();
		final UserGroupModel group2 = givenGroup();
		modelService.saveAll();

		//when
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			final UserModel userModel = modelService.get(user.getPk());
			final UserGroupModel groupModel1 = modelService.get(group1.getPk());
			final UserGroupModel groupModel2 = modelService.get(group2.getPk());
			userModel.setGroups(Set.of(groupModel1, groupModel2));
			modelService.saveAll();
			modelService.refresh(groupModel1);
			modelService.refresh(groupModel2);
		});

		future.get(5, TimeUnit.SECONDS);
		executor.shutdown();

		//then
		assertThat(user.getGroups()).hasSize(2).containsExactlyInAnyOrder(group1, group2);
		assertThat(group1.getMembers()).hasSize(1).containsExactly(user);
		assertThat(group2.getMembers()).hasSize(1).containsExactly(user);
	}

	@Test
	public void shouldReturnUpdatedDataWhenSettingGroupsToNull()
	{
		//given
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();
		user.setGroups(Set.of(group));
		modelService.saveAll();
		assertThat(user.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user);

		//when
		user.setGroups(null);
		modelService.saveAll();
		modelService.refresh(group);

		//then
		assertThatThereIsNoRelation(user, group);
	}

	@Test
	public void shouldReturnUpdatedDataWhenSettingGroupsToNullAfterTransaction() throws Exception
	{
		//given
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();
		Transaction.current().execute(new TransactionBody()
		{
			@Override
			public <T> T execute() throws Exception
			{
				user.setGroups(Set.of(group));
				modelService.saveAll();
				assertThat(user.getGroups()).hasSize(1).containsExactly(group);
				assertThat(group.getMembers()).hasSize(1).containsExactly(user);
				return null;
			}
		});

		//when
		user.setGroups(null);
		modelService.saveAll();
		modelService.refresh(group);

		//then
		assertThatThereIsNoRelation(user, group);
	}

	@Test
	public void shouldReturnUpdatedDataWhenSettingGroupsToNullInSeparateThread() throws Exception
	{
		//given
		final Tenant tenant = Registry.getCurrentTenant();
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();
		user.setGroups(Set.of(group));
		modelService.saveAll();

		//when
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			final UserModel userModel = modelService.get(user.getPk());
			final UserGroupModel userGroupModel = modelService.get(group.getPk());
			userModel.setGroups(null);
			modelService.saveAll();
			modelService.refresh(userGroupModel);
		});

		future.get(5, TimeUnit.SECONDS);
		executor.shutdown();

		//then
		assertThatThereIsNoRelation(user, group);
	}

	@Test
	public void shouldReturnUpdatedDataWhenRemovingGroup()
	{
		//given
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();
		final UserGroupModel groupToRemove = givenGroup();
		user.setGroups(Set.of(group, groupToRemove));
		modelService.saveAll();
		assertThat(user.getGroups()).hasSize(2).containsExactlyInAnyOrder(group, groupToRemove);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user);
		assertThat(groupToRemove.getMembers()).hasSize(1).containsExactly(user);

		//when
		user.setGroups(Set.of(group));
		modelService.saveAll();
		modelService.refresh(group);
		modelService.refresh(groupToRemove);

		//then
		assertThat(user.getGroups()).hasSize(1).containsExactlyInAnyOrder(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user);
		assertThat(groupToRemove.getMembers()).isEmpty();
	}

	@Test
	public void shouldReturnUpdatedDataWhenRemovingGroupInTransaction() throws Exception
	{
		//given
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();
		final UserGroupModel groupToRemove = givenGroup();
		user.setGroups(Set.of(group, groupToRemove));
		modelService.saveAll();
		assertThat(user.getGroups()).hasSize(2).containsExactlyInAnyOrder(group, groupToRemove);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user);
		assertThat(groupToRemove.getMembers()).hasSize(1).containsExactly(user);

		//when
		Transaction.current().execute(new TransactionBody()
		{
			@Override
			public <T> T execute() throws Exception
			{
				user.setGroups(Set.of(group));
				modelService.saveAll();
				modelService.refresh(group);
				modelService.refresh(groupToRemove);
				return null;
			}
		});

		//then
		assertThat(user.getGroups()).hasSize(1).containsExactlyInAnyOrder(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user);
		assertThat(groupToRemove.getMembers()).isEmpty();
	}

	@Test
	public void shouldReturnUpdatedDataWhenRemovingGroupInSeparateThread() throws Exception
	{
		//given
		final Tenant tenant = Registry.getCurrentTenant();
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();
		final UserGroupModel groupToRemove = givenGroup();
		user.setGroups(Set.of(group, groupToRemove));
		modelService.saveAll();

		//when
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			final UserModel userModel = modelService.get(user.getPk());
			final UserGroupModel userGroupModel = modelService.get(group.getPk());
			final UserGroupModel userGroupModelToRemove = modelService.get(groupToRemove.getPk());
			userModel.setGroups(Set.of(userGroupModel));
			modelService.saveAll();
			modelService.refresh(userGroupModel);
			modelService.refresh(userGroupModelToRemove);
		});

		future.get(5, TimeUnit.SECONDS);
		executor.shutdown();

		//then
		assertThat(user.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user);
		assertThat(groupToRemove.getMembers()).isEmpty();
	}

	@Test
	public void shouldReturnUpdatedDataWhenAddingGroupToDb()
	{
		//given
		final UserModel user = givenUser();
		modelService.saveAll();
		assertThat(user.getGroups()).isEmpty();

		//when
		final UserGroupModel group = givenGroup();
		user.setGroups(Set.of(group));
		modelService.saveAll();

		//then
		assertThat(user.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user);
	}

	@Test
	public void shouldReturnUpdatedDataWhenAddingGroupToDbAllInTransaction() throws Exception
	{
		Transaction.current().execute(new TransactionBody()
		{
			@Override
			public <T> T execute() throws Exception
			{
				//given
				final UserModel user = givenUser();
				modelService.saveAll();
				assertThat(user.getGroups()).isEmpty();

				//when
				final UserGroupModel group = givenGroup();
				user.setGroups(Set.of(group));
				modelService.saveAll();

				//then
				assertThat(user.getGroups()).hasSize(1).containsExactly(group);
				assertThat(group.getMembers()).hasSize(1).containsExactly(user);
				return null;
			}
		});
	}

	@Test
	public void shouldReturnUpdatedDataWhenAddingGroupToDbInSeparateThread() throws Exception
	{
		//given
		final Tenant tenant = Registry.getCurrentTenant();
		final UserModel user = givenUser();
		modelService.saveAll();

		//when
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<PK> future = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			final UserModel userModel = modelService.get(user.getPk());
			final UserGroupModel userGroupModel = givenGroup();
			userModel.setGroups(Set.of(userGroupModel));
			modelService.saveAll();
			return userGroupModel.getPk();
		});

		final UserGroupModel group = modelService.get(future.get(5, TimeUnit.SECONDS));
		executor.shutdown();

		//then
		assertThat(user.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user);
	}

	@Test
	public void shouldReturnUpdatedDataWhenRemovingGroupFromDb()
	{
		//given
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();
		user.setGroups(Set.of(group));
		modelService.saveAll();
		assertThat(user.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user);

		//when
		modelService.remove(group);
		modelService.refresh(user);

		//then
		assertThat(user.getGroups()).isEmpty();
	}

	@Test
	public void shouldReturnUpdatedDataWhenRemovingGroupFromDbAfterTransaction() throws Exception
	{
		//given
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();
		Transaction.current().execute(new TransactionBody()
		{
			@Override
			public <T> T execute() throws Exception
			{
				user.setGroups(Set.of(group));
				modelService.saveAll();
				assertThat(user.getGroups()).hasSize(1).containsExactly(group);
				assertThat(group.getMembers()).hasSize(1).containsExactly(user);
				return null;
			}
		});

		//when
		modelService.remove(group);
		modelService.refresh(user);

		//then
		assertThat(user.getGroups()).isEmpty();
	}

	@Test
	public void shouldReturnUpdatedDataWhenRemovingGroupFromDbInSeparateThread() throws Exception
	{
		//given
		final Tenant tenant = Registry.getCurrentTenant();
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();
		user.setGroups(Set.of(group));
		modelService.saveAll();

		//when
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			final UserModel userModel = modelService.get(user.getPk());
			final UserGroupModel userGroupModel = modelService.get(group.getPk());
			modelService.remove(userGroupModel);
			modelService.refresh(userModel);
		});

		future.get(5, TimeUnit.SECONDS);
		executor.shutdown();

		//then
		assertThat(user.getGroups()).isEmpty();
	}

	@Test
	public void shouldReturnUpdatedDataWhenRemovingGroupFromUserAndAddingToAnother()
	{
		//given
		final UserModel user1 = givenUser();
		final UserModel user2 = givenUser();
		final UserGroupModel group = givenGroup();
		user1.setGroups(Set.of(group));
		modelService.saveAll();
		assertThat(user1.getGroups()).hasSize(1).containsExactly(group);
		assertThat(user2.getGroups()).isEmpty();
		assertThat(group.getMembers()).hasSize(1).containsExactly(user1);

		//when
		user1.setGroups(null);
		user2.setGroups(Set.of(group));
		modelService.saveAll();
		modelService.refresh(group);

		//then
		assertThat(user1.getGroups()).isEmpty();
		assertThat(user2.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user2);
	}

	@Test
	public void shouldReturnUpdatedDataWhenRemovingGroupFromUserAndAddingToAnotherInTransaction() throws Exception
	{
		//given
		final UserModel user1 = givenUser();
		final UserModel user2 = givenUser();
		final UserGroupModel group = givenGroup();
		user1.setGroups(Set.of(group));
		modelService.saveAll();
		assertThat(user1.getGroups()).hasSize(1).containsExactly(group);
		assertThat(user2.getGroups()).isEmpty();
		assertThat(group.getMembers()).hasSize(1).containsExactly(user1);

		//when
		Transaction.current().execute(new TransactionBody()
		{
			@Override
			public <T> T execute() throws Exception
			{
				user1.setGroups(null);
				user2.setGroups(Set.of(group));
				modelService.saveAll();
				modelService.refresh(group);
				return null;
			}
		});

		//then
		assertThat(user1.getGroups()).isEmpty();
		assertThat(user2.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user2);
	}

	@Test
	public void shouldReturnUpdatedDataWhenRemovingGroupFromUserAndAddingItToAnotherInSeparateThread() throws Exception
	{
		//given
		final Tenant tenant = Registry.getCurrentTenant();
		final UserModel user1 = givenUser();
		final UserModel user2 = givenUser();
		final UserGroupModel group = givenGroup();
		user1.setGroups(Set.of(group));
		modelService.saveAll();

		//when
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			final UserModel userModel1 = modelService.get(user1.getPk());
			final UserModel userModel2 = modelService.get(user2.getPk());
			final UserGroupModel userGroupModel = modelService.get(group.getPk());
			userModel1.setGroups(null);
			userModel2.setGroups(Set.of(userGroupModel));
			modelService.saveAll();
			modelService.refresh(userGroupModel);
		});

		future.get(5, TimeUnit.SECONDS);
		executor.shutdown();

		//then
		assertThat(user1.getGroups()).isEmpty();
		assertThat(user2.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user2);
	}

	@Test
	public void shouldReturnUpdatedDataWhenRemovingGroupThenAddingAnotherGroup()
	{
		//given
		final UserModel user = givenUser();
		final UserGroupModel groupToRemove = givenGroup();
		final UserGroupModel groupToAdd = givenGroup();
		user.setGroups(Set.of(groupToRemove));
		modelService.saveAll();
		assertThat(user.getGroups()).hasSize(1).containsExactly(groupToRemove);
		assertThat(groupToRemove.getMembers()).hasSize(1).containsExactly(user);
		assertThat(groupToAdd.getMembers()).isEmpty();

		//when
		user.setGroups(Set.of(groupToAdd));
		modelService.saveAll();
		modelService.refresh(groupToRemove);
		modelService.refresh(groupToAdd);

		//then
		assertThat(user.getGroups()).hasSize(1).containsExactly(groupToAdd);
		assertThat(groupToRemove.getMembers()).isEmpty();
		assertThat(groupToAdd.getMembers()).hasSize(1).containsExactly(user);
	}

	@Test
	public void shouldReturnUpdatedDataWhenRemovingGroupThenAddingAnotherGroupAllInTransaction() throws Exception
	{
		Transaction.current().execute(new TransactionBody()
		{
			@Override
			public <T> T execute() throws Exception
			{
				final UserModel user = givenUser();
				final UserGroupModel groupToRemove = givenGroup();
				final UserGroupModel groupToAdd = givenGroup();
				user.setGroups(Set.of(groupToRemove));
				modelService.saveAll();
				assertThat(user.getGroups()).hasSize(1).containsExactly(groupToRemove);
				assertThat(groupToRemove.getMembers()).hasSize(1).containsExactly(user);
				assertThat(groupToAdd.getMembers()).isEmpty();

				//when
				user.setGroups(Set.of(groupToAdd));
				modelService.saveAll();
				modelService.refresh(groupToRemove);
				modelService.refresh(groupToAdd);

				//then
				assertThat(user.getGroups()).hasSize(1).containsExactly(groupToAdd);
				assertThat(groupToRemove.getMembers()).isEmpty();
				assertThat(groupToAdd.getMembers()).hasSize(1).containsExactly(user);
				return null;
			}
		});
	}

	@Test
	public void shouldReturnUpdatedDataWhenRemovingGroupThenAddingAnotherGroupInSeparateThread() throws Exception
	{
		//given
		final Tenant tenant = Registry.getCurrentTenant();
		final UserModel user = givenUser();
		final UserGroupModel groupToRemove = givenGroup();
		final UserGroupModel groupToAdd = givenGroup();
		user.setGroups(Set.of(groupToRemove));
		modelService.saveAll();

		//when
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			final UserModel userModel = modelService.get(user.getPk());
			final UserGroupModel userGroupModelToRemove = modelService.get(groupToRemove.getPk());
			final UserGroupModel userGroupModelToAdd = modelService.get(groupToAdd.getPk());
			userModel.setGroups(Set.of(userGroupModelToAdd));
			modelService.saveAll();
			modelService.refresh(userGroupModelToRemove);
			modelService.refresh(userGroupModelToAdd);
		});

		future.get(5, TimeUnit.SECONDS);
		executor.shutdown();

		//then
		assertThat(user.getGroups()).hasSize(1).containsExactly(groupToAdd);
		assertThat(groupToRemove.getMembers()).isEmpty();
		assertThat(groupToAdd.getMembers()).hasSize(1).containsExactly(user);
	}

	@Test
	public void shouldReturnUpdatedDataWhenAddingGroupToUsers()
	{
		//given
		final UserModel user1 = givenUser();
		final UserModel user2 = givenUser();
		final UserGroupModel group = givenGroup();
		modelService.saveAll();
		assertThat(user1.getGroups()).isEmpty();
		assertThat(user2.getGroups()).isEmpty();
		assertThat(group.getMembers()).isEmpty();

		//when
		user1.setGroups(Set.of(group));
		user2.setGroups(Set.of(group));
		modelService.saveAll();
		modelService.refresh(group);

		//then
		assertThat(user1.getGroups()).hasSize(1).containsExactly(group);
		assertThat(user2.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(2).containsExactlyInAnyOrder(user1, user2);
	}

	@Test
	public void shouldReturnUpdatedDataWhenAddingGroupToUsersOutsideTransactionAndThenInside() throws Exception
	{
		//given
		final UserModel user1 = givenUser();
		final UserModel user2 = givenUser();
		final UserGroupModel group = givenGroup();
		modelService.saveAll();
		assertThat(user1.getGroups()).isEmpty();
		assertThat(user2.getGroups()).isEmpty();
		assertThat(group.getMembers()).isEmpty();

		//when
		user1.setGroups(Set.of(group));
		modelService.saveAll();

		Transaction.current().execute(new TransactionBody()
		{
			@Override
			public <T> T execute() throws Exception
			{
				user2.setGroups(Set.of(group));
				modelService.saveAll();
				return null;
			}
		});

		modelService.refresh(group);

		//then
		assertThat(user1.getGroups()).hasSize(1).containsExactly(group);
		assertThat(user2.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(2).containsExactlyInAnyOrder(user1, user2);
	}

	@Test
	public void shouldReturnUpdatedDataWhenAddingGroupToUsersOutsideSeparateThreadAndThenInside() throws Exception
	{
		//given
		final Tenant tenant = Registry.getCurrentTenant();
		final UserModel user1 = givenUser();
		final UserModel user2 = givenUser();
		final UserGroupModel group = givenGroup();
		modelService.saveAll();

		//when
		user1.setGroups(Set.of(group));
		modelService.saveAll();
		modelService.refresh(group);

		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			final UserModel userModel2 = modelService.get(user2.getPk());
			final UserGroupModel userGroupModel = modelService.get(group.getPk());
			userModel2.setGroups(Set.of(userGroupModel));
			modelService.saveAll();
			modelService.refresh(userGroupModel);
		});

		future.get(5, TimeUnit.SECONDS);
		executor.shutdown();

		//then
		assertThat(user1.getGroups()).hasSize(1).containsExactly(group);
		assertThat(user2.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(2).containsExactlyInAnyOrder(user1, user2);
	}

	@Test
	public void shouldReturnUpdatedDataWhenRemovingGroupThenAddingGroupInTransaction() throws Exception
	{
		//given
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();
		user.setGroups(Set.of(group));
		modelService.saveAll();
		assertThat(user.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user);

		//when
		user.setGroups(null);
		modelService.saveAll();
		modelService.refresh(group);
		assertThatThereIsNoRelation(user, group);

		Transaction.current().execute(new TransactionBody()
		{
			@Override
			public <T> T execute() throws Exception
			{
				user.setGroups(Set.of(group));
				modelService.saveAll();
				modelService.refresh(group);
				return null;
			}
		});

		//then
		assertThat(user.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user);
	}

	@Test
	public void shouldReturnUpdatedDataWhenRemovingGroupThenAddingGroupInSeparateThread() throws Exception
	{
		//given
		final Tenant tenant = Registry.getCurrentTenant();
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();
		user.setGroups(Set.of(group));
		modelService.saveAll();

		//when
		user.setGroups(null);
		modelService.saveAll();
		modelService.refresh(group);

		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			final UserModel userModel = modelService.get(user.getPk());
			final UserGroupModel userGroupModel = modelService.get(group.getPk());
			userModel.setGroups(Set.of(userGroupModel));
			modelService.saveAll();
			modelService.refresh(userGroupModel);
		});

		future.get(5, TimeUnit.SECONDS);
		executor.shutdown();

		//then
		assertThat(user.getGroups()).hasSize(1).containsExactly(group);
		assertThat(group.getMembers()).hasSize(1).containsExactly(user);
	}


	@Test
	public void userHasAGroupAfterBecomingAMember()
	{
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();

		modelService.saveAll();
		assertThat(user.getGroups()).isEmpty();
		assertThat(group.getMembers()).isEmpty();

		group.setMembers(Set.of(user));
		modelService.saveAll();
		modelService.refresh(user);

		assertThat(user.getGroups()).containsExactly(group);
		assertThat(group.getMembers()).containsExactly(user);
	}

	@Test
	public void userHasAGroupAfterBecomingAMemberInATransaction() throws Exception
	{
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();

		final PK userPk = (PK) Transaction.current().execute(new TransactionBody()
		{
			@Override
			public Object execute()
			{
				modelService.saveAll();
				assertThat(user.getGroups()).isEmpty();
				assertThat(group.getMembers()).isEmpty();
				group.setMembers(Set.of(user));
				modelService.saveAll();
				modelService.refresh(user);
				assertThat(user.getGroups()).containsExactly(group);
				assertThat(group.getMembers()).containsExactly(user);
				return user.getPk();
			}
		});
		final UserModel userModel = modelService.get(userPk);
		modelService.detachAll();

		assertThat(userModel.getGroups()).containsExactly(group);
		assertThat(group.getMembers()).containsExactly(user);
	}

	@Test
	public void userHasAGroupAfterBecomingAMemberInAnotherThread() throws Exception
	{
		final Tenant tenant = Registry.getCurrentTenant();
		final UserGroupModel group = givenGroup();
		modelService.saveAll();

		final PK groupPk = group.getPk();
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<PK> userPkFuture = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			final UserModel user = givenUser();
			modelService.<UserGroupModel>get(groupPk).setMembers(Set.of(user));
			modelService.saveAll();
			modelService.refresh(user);
			return user.getPk();
		});

		final UserModel userModel = modelService.get(userPkFuture.get(5, TimeUnit.SECONDS));
		executor.shutdown();

		assertThat(userModel.getGroups()).containsExactly(group);
		assertThat(group.getMembers()).containsExactly(userModel);
	}

	@Test
	public void userHasNoGroupsAfterSettingItsMembersToNull()
	{
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();

		modelService.saveAll();
		assertThat(user.getGroups()).isEmpty();
		assertThat(group.getMembers()).isEmpty();

		group.setMembers(Set.of(user));
		modelService.saveAll();
		modelService.refresh(user);

		assertThat(user.getGroups()).containsExactly(group);
		assertThat(group.getMembers()).containsExactly(user);

		group.setMembers(null);
		modelService.saveAll();
		modelService.refresh(user);

		assertThat(user.getGroups()).isEmpty();
		assertThat(group.getMembers()).isEmpty();
	}

	@Test
	public void userHasNoGroupsAfterSettingItsMembersToNullInATransaction() throws Exception
	{
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();

		modelService.saveAll();
		assertThat(user.getGroups()).isEmpty();
		assertThat(group.getMembers()).isEmpty();

		group.setMembers(Set.of(user));
		modelService.saveAll();
		modelService.refresh(user);

		assertThat(user.getGroups()).containsExactly(group);
		assertThat(group.getMembers()).containsExactly(user);

		final PK userPk = (PK) Transaction.current().execute(new TransactionBody()
		{
			@Override
			public Object execute()
			{
				group.setMembers(null);
				modelService.saveAll();
				modelService.refresh(user);
				assertThat(user.getGroups()).isEmpty();
				assertThat(group.getMembers()).isEmpty();
				return user.getPk();
			}

		});
		final UserModel userModel = modelService.get(userPk);
		modelService.refresh(group);

		assertThat(userModel.getGroups()).isEmpty();
		assertThat(group.getMembers()).isEmpty();
	}

	@Test
	public void userHasNoGroupsAfterSettingItsMembersToNullInAnotherThread() throws Exception
	{
		final Tenant tenant = Registry.getCurrentTenant();
		final UserGroupModel group = givenGroup();
		final UserModel user = givenUser();

		group.setMembers(Set.of(user));
		modelService.saveAll();
		modelService.refresh(user);
		assertThat(user.getGroups()).containsExactly(group);

		final PK groupPk = group.getPk();
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			modelService.<UserGroupModel>get(groupPk).setMembers(null);
			modelService.saveAll();
		});

		future.get(5, TimeUnit.SECONDS);
		executor.shutdown();
		modelService.refresh(user);
		modelService.refresh(group);

		assertThat(user.getGroups()).isEmpty();
		assertThat(group.getMembers()).isEmpty();
	}

	@Test
	public void groupHasNoMembersAfterDeletingUserFromDb()
	{
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();

		modelService.saveAll();
		assertThat(user.getGroups()).isEmpty();
		assertThat(group.getMembers()).isEmpty();

		group.setMembers(Set.of(user));
		modelService.saveAll();
		modelService.refresh(user);

		assertThat(user.getGroups()).containsExactly(group);
		assertThat(group.getMembers()).containsExactly(user);

		modelService.remove(user);
		modelService.saveAll();
		modelService.refresh(group);
		assertThat(group.getMembers()).isEmpty();
	}

	@Test
	public void groupHasNoMembersAfterDeletingUserFromDbInATransaction() throws Exception
	{
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();

		modelService.saveAll();
		assertThat(user.getGroups()).isEmpty();
		assertThat(group.getMembers()).isEmpty();

		group.setMembers(Set.of(user));
		modelService.saveAll();
		modelService.refresh(user);

		assertThat(user.getGroups()).containsExactly(group);
		assertThat(group.getMembers()).containsExactly(user);
		Transaction.current().execute(new TransactionBody()
		{
			@Override
			public Object execute()
			{
				modelService.remove(user);
				modelService.saveAll();
				return null;
			}
		});

		modelService.refresh(group);
		assertThat(group.getMembers()).isEmpty();
	}

	@Test
	public void groupHasNoMembersAfterDeletingUserFromDbInAnotherThread() throws Exception
	{
		final UserModel user = givenUser();
		final UserGroupModel group = givenGroup();
		final Tenant tenant = Registry.getCurrentTenant();

		modelService.saveAll();
		assertThat(user.getGroups()).isEmpty();
		assertThat(group.getMembers()).isEmpty();

		group.setMembers(Set.of(user));
		modelService.saveAll();
		modelService.refresh(user);
		final PK pkOfUserToBeDeleted = user.getPk();

		assertThat(user.getGroups()).containsExactly(group);
		assertThat(group.getMembers()).containsExactly(user);

		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			modelService.remove(pkOfUserToBeDeleted);
			modelService.saveAll();
		});

		future.get(5, TimeUnit.SECONDS);
		executor.shutdown();

		modelService.refresh(group);
		assertThat(group.getMembers()).isEmpty();
	}

	@Test
	public void allUsersHaveProperGroupAfterBeingSetAsMembers()
	{
		final Set<PrincipalModel> usersSet = Set.of(givenUser(), givenUser(), givenUser());
		final UserGroupModel group = givenGroup();

		modelService.saveAll();
		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
		assertThat(group.getMembers()).isEmpty();

		group.setMembers(usersSet);
		modelService.saveAll();
		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).containsExactly(group));
		assertThat(group.getMembers()).isEqualTo(usersSet);
	}

	@Test
	public void allUsersHaveProperGroupAfterBeingSetAsMembersInATransaction() throws Exception
	{
		final Set<PrincipalModel> usersSet = Set.of(givenUser(), givenUser(), givenUser());
		final UserGroupModel group = givenGroup();

		modelService.saveAll();
		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
		assertThat(group.getMembers()).isEmpty();

		Transaction.current().execute(new TransactionBody()
		{
			@Override
			public Object execute()
			{
				group.setMembers(usersSet);
				modelService.saveAll();
				return null;
			}
		});

		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).containsExactly(group));
		assertThat(group.getMembers()).isEqualTo(usersSet);
	}

	@Test
	public void allUsersHaveProperGroupAfterBeingSetAsMembersInAnotherThread() throws Exception
	{
		final UserGroupModel group = givenGroup();
		final Tenant tenant = Registry.getCurrentTenant();

		modelService.saveAll();
		assertThat(group.getMembers()).isEmpty();

		final PK groupPk = group.getPk();
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<Set<PK>> futureUsersSet = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			final Set<PrincipalModel> usersSet = Set.of(givenUser(), givenUser(), givenUser());
			modelService.<UserGroupModel>get(groupPk).setMembers(usersSet);
			modelService.saveAll();
			return usersSet.stream().map(AbstractItemModel::getPk).collect(Collectors.toSet());
		});

		final Set<PK> finalUserPKsSet = futureUsersSet.get(5, TimeUnit.SECONDS);
		executor.shutdown();
		final Set<PrincipalModel> finalUsersSet = finalUserPKsSet.stream().map(PK -> modelService.<UserModel>get(PK)).collect(
				Collectors.toSet());
		modelService.refresh(group);
		finalUsersSet.forEach((user) -> modelService.refresh(user));
		finalUsersSet.forEach((user) -> assertThat(user.getGroups()).containsExactly(group));
		assertThat(group.getMembers()).isEqualTo(finalUsersSet);
	}

	@Test
	public void allUsersLooseTheirGroupAfterSettingItsMembersToNull()
	{
		final Set<PrincipalModel> usersSet = Set.of(givenUser(), givenUser(), givenUser());
		final UserGroupModel group = givenGroup();

		modelService.saveAll();

		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
		assertThat(group.getMembers()).isEmpty();

		group.setMembers(usersSet);
		modelService.saveAll();
		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).containsExactly(group));
		assertThat(group.getMembers()).isEqualTo(usersSet);

		group.setMembers(null);
		modelService.saveAll();
		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
		assertThat(group.getMembers()).isEmpty();
	}

	@Test
	public void allUsersLooseTheirGroupAfterSettingItsMembersToNullInATransaction() throws Exception
	{
		final Set<PrincipalModel> usersSet = Set.of(givenUser(), givenUser(), givenUser());
		final UserGroupModel group = givenGroup();

		modelService.saveAll();

		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
		assertThat(group.getMembers()).isEmpty();

		group.setMembers(usersSet);
		modelService.saveAll();
		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).containsExactly(group));
		assertThat(group.getMembers()).isEqualTo(usersSet);

		Transaction.current().execute(new TransactionBody()
		{
			@Override
			public Object execute()
			{
				group.setMembers(null);
				modelService.saveAll();
				return null;
			}
		});

		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
		assertThat(group.getMembers()).isEmpty();
	}

	@Test
	public void allUsersLooseTheirGroupAfterSettingItsMembersToNullInAnotherThread() throws Exception
	{
		final Tenant tenant = Registry.getCurrentTenant();
		final Set<PrincipalModel> usersSet = Set.of(givenUser(), givenUser(), givenUser());
		final UserGroupModel group = givenGroup();

		modelService.saveAll();

		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
		assertThat(group.getMembers()).isEmpty();

		group.setMembers(usersSet);
		modelService.saveAll();
		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).containsExactly(group));
		assertThat(group.getMembers()).isEqualTo(usersSet);

		final PK groupPk = group.getPk();
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			modelService.<UserGroupModel>get(groupPk).setMembers(null);
			modelService.saveAll();
		});

		future.get(5, TimeUnit.SECONDS);
		executor.shutdown();
		modelService.refresh(group);

		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
		assertThat(group.getMembers()).isEmpty();
	}

	@Test
	public void usersHaveNoGroupAfterDeletingTheirGroupFromDb()
	{
		final Set<PrincipalModel> usersSet = Set.of(givenUser(), givenUser(), givenUser());
		final UserGroupModel group = givenGroup();

		modelService.saveAll();

		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
		assertThat(group.getMembers()).isEmpty();

		group.setMembers(usersSet);
		modelService.saveAll();
		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).containsExactly(group));
		assertThat(group.getMembers()).isEqualTo(usersSet);

		modelService.remove(group);
		modelService.saveAll();
		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
	}

	@Test
	public void usersHaveNoGroupAfterDeletingTheirGroupFromDbInATransaction() throws Exception
	{
		final Set<PrincipalModel> usersSet = Set.of(givenUser(), givenUser(), givenUser());
		final UserGroupModel group = givenGroup();

		modelService.saveAll();

		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
		assertThat(group.getMembers()).isEmpty();

		group.setMembers(usersSet);
		modelService.saveAll();
		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).containsExactly(group));
		assertThat(group.getMembers()).isEqualTo(usersSet);

		Transaction.current().execute(new TransactionBody()
		{
			@Override
			public Object execute()
			{
				modelService.remove(group);
				modelService.saveAll();
				return null;
			}
		});

		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
	}

	@Test
	public void usersHaveNoGroupAfterDeletingTheirGroupFromDbInAnotherThread() throws Exception
	{
		final Set<PrincipalModel> usersSet = Set.of(givenUser(), givenUser(), givenUser());
		final UserGroupModel group = givenGroup();
		final Tenant tenant = Registry.getCurrentTenant();

		modelService.saveAll();

		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
		assertThat(group.getMembers()).isEmpty();

		group.setMembers(usersSet);
		modelService.saveAll();
		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).containsExactly(group));
		assertThat(group.getMembers()).isEqualTo(usersSet);


		final PK groupPk = group.getPk();
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			modelService.remove(groupPk);
			modelService.saveAll();
		});
		future.get(5, TimeUnit.SECONDS);
		executor.shutdown();
		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
	}

	@Test
	public void usersHaveProperGroupLeftAfterRemovingMembersOfOneOfTheirGroups()
	{
		final Set<PrincipalModel> usersSet = Set.of(givenUser(), givenUser(), givenUser());

		final UserGroupModel groupToHaveRemovedMembers = givenGroup();
		final UserGroupModel groupToSaveItsMembers = givenGroup();

		modelService.saveAll();
		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
		assertThat(groupToHaveRemovedMembers.getMembers()).isEmpty();
		assertThat(groupToSaveItsMembers.getMembers()).isEmpty();

		groupToHaveRemovedMembers.setMembers(usersSet);
		groupToSaveItsMembers.setMembers(usersSet);
		modelService.saveAll();

		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach(
				(user) -> assertThat(user.getGroups()).isEqualTo(Set.of(groupToHaveRemovedMembers, groupToSaveItsMembers)));

		groupToHaveRemovedMembers.setMembers(null);
		modelService.saveAll();

		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).containsExactly(groupToSaveItsMembers));
	}

	@Test
	public void usersHaveProperGroupLeftAfterRemovingMembersOfOneOfTheirGroupsInATransaction() throws Exception
	{
		final Set<PrincipalModel> usersSet = Set.of(givenUser(), givenUser(), givenUser());

		final UserGroupModel groupToHaveRemovedMembers = givenGroup();
		final UserGroupModel groupToSaveItsMembers = givenGroup();

		modelService.saveAll();
		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
		assertThat(groupToHaveRemovedMembers.getMembers()).isEmpty();
		assertThat(groupToSaveItsMembers.getMembers()).isEmpty();

		groupToHaveRemovedMembers.setMembers(usersSet);
		groupToSaveItsMembers.setMembers(usersSet);
		modelService.saveAll();

		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach(
				(user) -> assertThat(user.getGroups()).isEqualTo(Set.of(groupToHaveRemovedMembers, groupToSaveItsMembers)));

		Transaction.current().execute(new TransactionBody()
		{
			@Override
			public Object execute()
			{
				groupToHaveRemovedMembers.setMembers(null);
				modelService.saveAll();
				return null;
			}
		});

		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).containsExactly(groupToSaveItsMembers));
	}

	@Test
	public void usersHaveProperGroupLeftAfterRemovingMembersOfOneOfTheirGroupsInAnotherThread() throws Exception
	{
		final Set<PrincipalModel> usersSet = Set.of(givenUser(), givenUser(), givenUser());
		final Tenant tenant = Registry.getCurrentTenant();

		final UserGroupModel groupToHaveRemovedMembers = givenGroup();
		final UserGroupModel groupToSaveItsMembers = givenGroup();

		modelService.saveAll();
		usersSet.forEach((user) -> assertThat(user.getGroups()).isEmpty());
		assertThat(groupToHaveRemovedMembers.getMembers()).isEmpty();
		assertThat(groupToSaveItsMembers.getMembers()).isEmpty();

		groupToHaveRemovedMembers.setMembers(usersSet);
		groupToSaveItsMembers.setMembers(usersSet);
		modelService.saveAll();

		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach(
				(user) -> assertThat(user.getGroups()).isEqualTo(Set.of(groupToHaveRemovedMembers, groupToSaveItsMembers)));

		final PK groupPk = groupToHaveRemovedMembers.getPk();
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			modelService.<UserGroupModel>get(groupPk).setMembers(null);
			modelService.saveAll();
		});

		future.get(5, TimeUnit.SECONDS);
		executor.shutdown();
		usersSet.forEach((user) -> modelService.refresh(user));
		usersSet.forEach((user) -> assertThat(user.getGroups()).containsExactly(groupToSaveItsMembers));
	}

	@Test
	public void userHasProperGroupsWhenAddedAsAMemberToTwoGroupsInSeparateThreads() throws Exception
	{
		final Tenant tenant = Registry.getCurrentTenant();
		final UserGroupModel group1 = givenGroup();
		final UserGroupModel group2 = givenGroup();
		final UserModel user = givenUser();

		modelService.saveAll();

		final PK group1Pk = group1.getPk();
		final PK group2Pk = group2.getPk();
		final PK userPk = user.getPk();
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<?> future1 = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			modelService.<UserGroupModel>get(group1Pk).setMembers(Set.of(modelService.<UserModel>get(userPk)));
			modelService.saveAll();
		});

		final Future<?> future2 = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			modelService.<UserGroupModel>get(group2Pk).setMembers(Set.of(modelService.<UserModel>get(userPk)));
			modelService.saveAll();
		});

		future1.get(5, TimeUnit.SECONDS);
		future2.get(5, TimeUnit.SECONDS);
		executor.shutdown();
		modelService.refresh(group1);
		modelService.refresh(group2);
		modelService.refresh(user);
		assertThat(user.getGroups()).containsExactlyInAnyOrder(group1, group2);
	}

	@Test
	public void userHasAGroupAfterAddingHimAsAMemberInATransactionInASeparateThread() throws Exception
	{
		final Tenant tenant = Registry.getCurrentTenant();
		final UserGroupModel group = givenGroup();

		modelService.saveAll();

		final PK groupPk = group.getPk();
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<PK> userPkFuture = executor.submit(() -> {
			Registry.setCurrentTenant(tenant);
			final PK userPk = (PK) Transaction.current().execute(new TransactionBody()
			{
				@Override
				public Object execute()
				{
					final UserModel user = givenUser();
					modelService.<UserGroupModel>get(groupPk).setMembers(Set.of(user));
					modelService.saveAll();
					return user.getPk();
				}
			});
			return userPk;
		});

		assertThat(userPkFuture).isNotNull();

		final UserModel userModel = modelService.get(userPkFuture.get(5, TimeUnit.SECONDS));
		executor.shutdown();
		assertThat(userModel.getGroups()).containsExactly(group);
	}

	private void assertThatThereIsNoRelation(final UserModel user, final UserGroupModel group)
	{
		assertThat(user.getGroups()).isEmpty();
		assertThat(group.getMembers()).isEmpty();
	}

	private UserGroupModel givenGroup()
	{
		final UserGroupModel group = modelService.create(UserGroupModel.class);
		group.setUid(uniqueUid());
		return group;
	}

	private UserModel givenUser()
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid(uniqueUid());
		return user;
	}

	private String uniqueUid()
	{
		return UUID.randomUUID().toString();
	}
}
