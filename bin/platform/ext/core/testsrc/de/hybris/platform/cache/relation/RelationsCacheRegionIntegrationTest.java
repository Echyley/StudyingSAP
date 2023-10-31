/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cache.relation;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cache.AbstractCacheUnit;
import de.hybris.platform.cache.InvalidationListener;
import de.hybris.platform.cache.InvalidationManager;
import de.hybris.platform.cache.InvalidationTopic;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class RelationsCacheRegionIntegrationTest extends ServicelayerBaseTest
{
	private static final InvalidationListener INVALIDATION_LISTENER = (key, invalidationType, target, remoteSrc) -> target.invalidate(
			key, invalidationType);

	private final InvalidationTopic topic = InvalidationManager.getInstance()
	                                                           .getOrCreateInvalidationTopic(
			                                                           new Object[]{ RelationCacheKey.RELATION, PrincipalGroupModel._PRINCIPALGROUPRELATION });

	@Resource
	private ModelService modelService;

	@Resource
	private RelationCacheRegion relationsCacheRegion;


	@After
	public void after()
	{
		topic.removeInvalidationListener(INVALIDATION_LISTENER);
		this.relationsCacheRegion.clearCache();
	}

	@Before
	public void before()
	{
		topic.addInvalidationListener(INVALIDATION_LISTENER);
	}

	@Test
	public void testKeyShouldEndUpInCorrectRegionCache() throws Exception
	{
		final UserGroupModel group = createGroup();
		final UserModel user = createUser(group);
		final RelationCacheUnit cacheUnit = RelationCacheUnit.createRelationCacheUnit(
				PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS, user.getPk());

		cacheUnit.get();
		assertThat(this.relationsCacheRegion.getAllKeys()).contains(cacheUnit.getKey());
	}

	@Test
	public void testReadingGroupsFromCache() throws Exception
	{
		final UserGroupModel group = createGroup();
		final UserModel user = createUser(group);
		final RelationCacheUnit cacheUnit = RelationCacheUnit.createRelationCacheUnit(
				PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS, user.getPk());
		final List<PK> fromCache = cacheUnit.getCachedPKs();
		assertThat(fromCache).hasSize(1).contains(group.getPk());
	}

	@Test
	public void testReadingUserFromCache() throws Exception
	{
		final UserGroupModel group = createGroup();
		final UserModel user = createUser(group);
		final RelationCacheUnit cacheUnit = RelationCacheUnit.createRelationCacheUnit(
				PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.MEMBERS, group.getPk());
		final List<PK> fromCache = cacheUnit.getCachedPKs();
		assertThat(fromCache).hasSize(1).contains(user.getPk());
	}

	@Test
	public void testInvalidationFromCacheUnit() throws Exception
	{
		final UserGroupModel group = createGroup();
		final UserModel user = createUser(group);
		final RelationCacheUnit cacheUnit = RelationCacheUnit.createRelationCacheUnit(
				PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS, user.getPk());

		final List<PK> fromCache = (List<PK>) cacheUnit.get();
		assertThat(fromCache).hasSize(1);
		assertThat(this.relationsCacheRegion.containsKey(cacheUnit.getKey())).isTrue();

		cacheUnit.invalidate(AbstractCacheUnit.INVALIDATIONTYPE_MODIFIED);
		assertThat(this.relationsCacheRegion.containsKey(cacheUnit.getKey())).isFalse();
	}

	@Test
	public void testInvalidationFromTopic() throws Exception
	{
		final UserGroupModel group = createGroup();
		final UserModel user = createUser(group);
		final RelationCacheUnit cacheUnit = RelationCacheUnit.createRelationCacheUnit(
				PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS, user.getPk());
		final List<PK> fromCache = cacheUnit.getCachedPKs();
		assertThat(fromCache).hasSize(1);
		assertThat(Registry.getApplicationContext()
		                   .getBean("relationsCacheRegion", RelationCacheRegion.class)
		                   .containsKey(cacheUnit.getKey())).isTrue();

		topic.invalidate( RelationCacheKey.createInvalidationKey(PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS, user.getPk()) , AbstractCacheUnit.INVALIDATIONTYPE_MODIFIED);

		assertThat(Registry.getApplicationContext()
		                   .getBean("relationsCacheRegion", RelationCacheRegion.class)
		                   .containsKey(cacheUnit.getKey())).isFalse();
	}

	@Test
	public void testInvalidationOfWholeRelationFromTopic() throws Exception
	{
		final RelationCacheUnit cacheUnit = createCacheUnitAndDoGet();
		final RelationCacheUnit cacheUnit2 = createCacheUnitAndDoGet();

		final RelationCacheRegion cacheRegion = Registry.getApplicationContext().getBean(
				"relationsCacheRegion",
				RelationCacheRegion.class);
		assertThat(cacheRegion.getAllKeys()).contains(cacheUnit.getKey(), cacheUnit2.getKey());

		topic.invalidate( RelationCacheKey.createInvalidationKeyForRelation(PrincipalGroupModel._PRINCIPALGROUPRELATION), AbstractCacheUnit.INVALIDATIONTYPE_MODIFIED);

		assertThat(cacheRegion.getAllKeys()).doesNotContain(cacheUnit.getKey(), cacheUnit2.getKey());
	}

	protected RelationCacheUnit createCacheUnitAndDoGet() throws Exception
	{
		final UserGroupModel group = createGroup();
		final UserModel user = createUser(group);
		final RelationCacheUnit cacheUnit = RelationCacheUnit.createRelationCacheUnit(
				PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS, user.getPk());
		cacheUnit.get();
		return cacheUnit;
	}

	@Test
	public void testKeysShouldBeEqual() throws Exception
	{
		final UserGroupModel group = createGroup();
		final UserModel user = createUser(group);
		final RelationCacheUnit cacheUnit = RelationCacheUnit.createRelationCacheUnit(
				PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS, user.getPk());

		cacheUnit.get();

		final RelationCacheUnit secondCacheUnit = RelationCacheUnit.createRelationCacheUnit(
				PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS, user.getPk());

		assertThat(this.relationsCacheRegion.getAllKeys()).contains(secondCacheUnit.getKey());
	}

	private UserGroupModel createGroup()
	{
		final UserGroupModel root = modelService.create(UserGroupModel.class);
		root.setUid("ROOT" + UUID.randomUUID().toString());
		modelService.save(root);
		return root;
	}

	private UserModel createUser(final UserGroupModel group)
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid("User" + UUID.randomUUID().toString());
		final HashSet<PrincipalGroupModel> userGroups = new HashSet<>();
		userGroups.add(group);
		user.setGroups(userGroups);
		modelService.saveAll();
		return user;
	}
}
