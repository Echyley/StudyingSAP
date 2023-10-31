/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.cache.relation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cache.Cache;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.jalo.type.RelationDescriptor;
import de.hybris.platform.jalo.type.RelationType;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Unit test for {@link RelationCacheUnit}, that verifies TABLE_NAME caching
 */
@IntegrationTest
public class RelationCacheUnitTableNameTest extends HybrisJUnit4Test
{
	private static final String TENANT_ONE = "t1";
	private static final String TENANT_TWO = "t2";
	private static final String RELATION_ONE = "rel1";
	private static final String RELATION_TWO = "rel2";
	private static final String RELATION_THREE = "rel3";
	private static final String RELATION_ONE_QUALIFIER = "qualifier1";
	private static final String RELATION_TWO_QUALIFIER = "qualifier2";
	private static final String RELATION_THREE_QUALIFIER = "qualifier3";
	private static final String TENANT_ONE_RELATION_ONE_TABLE_NAME = "t1_rel1";
	private static final String TENANT_ONE_RELATION_TWO_TABLE_NAME = "t1_rel2";
	private static final String TENANT_ONE_RELATION_THREE_TABLE_NAME = "t1_rel3";
	private static final String TENANT_TWO_RELATION_ONE_TABLE_NAME = "t2_rel1";

	@Mock
	private RelationDescriptor relationDescriptor1;
	@Mock
	private RelationType type1;
	@Mock
	private RelationDescriptor relationDescriptor2;
	@Mock
	private RelationType type2;
	@Mock
	private RelationDescriptor relationDescriptor3;
	@Mock
	private RelationType type3;
	@Mock
	private TypeManager typeManager;
	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Override
	public void finish() throws JaloSecurityException
	{
		RelationCacheUnit.clearCacheUnitForTenant(TENANT_ONE);
		RelationCacheUnit.clearCacheUnitForTenant(TENANT_TWO);
		super.finish();
	}

	@Test
	public void testGetTableWhenTenantChangesFlow()
	{
		try (final MockedStatic<TypeManager> typeManagerStatic = mockStatic(TypeManager.class))
		{
			// given
			typeManagerStatic.when(TypeManager::getInstance).thenReturn(this.typeManager);
			when(relationDescriptor1.getQualifier()).thenReturn(RELATION_ONE_QUALIFIER);
			when(type1.getSourceAttributeDescriptor()).thenReturn(relationDescriptor1);
			when(this.typeManager.getType(RELATION_ONE)).thenReturn(type1);

			//when
			when(type1.getTable()).thenReturn(TENANT_ONE_RELATION_ONE_TABLE_NAME);
			final RelationCacheUnit tenant1CacheUnit = createRelationCacheUnit(TENANT_ONE, RELATION_ONE);
			when(type1.getTable()).thenReturn(TENANT_TWO_RELATION_ONE_TABLE_NAME);
			final RelationCacheUnit tenant2CacheUnit = createRelationCacheUnit(TENANT_TWO, RELATION_ONE);

			//then
			assertThat(tenant1CacheUnit.getTable()).isEqualTo(TENANT_ONE_RELATION_ONE_TABLE_NAME);
			assertThat(tenant2CacheUnit.getTable()).isEqualTo(TENANT_TWO_RELATION_ONE_TABLE_NAME);
			typeManagerStatic.verify(TypeManager::getInstance, times(4));
			// TypeManager is invoked in isSourceOfRelation and getTableName methods once for every tenant
			verify(this.typeManager, times(4)).getType(RELATION_ONE);
			verify(type1, times(2)).getTable();
			verify(type1, times(2)).getSourceAttributeDescriptor();
			verify(relationDescriptor1, times(2)).getQualifier();
		}
	}

	@Test
	public void testIfTableNameAreCached()
	{
		try (final MockedStatic<TypeManager> typeManagerStatic = mockStatic(TypeManager.class))
		{
			// given
			typeManagerStatic.when(TypeManager::getInstance).thenReturn(typeManager);
			when(relationDescriptor2.getQualifier()).thenReturn(RELATION_TWO_QUALIFIER);
			when(type2.getSourceAttributeDescriptor()).thenReturn(relationDescriptor2);
			when(typeManager.getType(RELATION_TWO)).thenReturn(type2);
			when(relationDescriptor3.getQualifier()).thenReturn(RELATION_THREE_QUALIFIER);
			when(type3.getSourceAttributeDescriptor()).thenReturn(relationDescriptor3);
			when(typeManager.getType(RELATION_THREE)).thenReturn(type3);

			//when
			when(type2.getTable()).thenReturn(TENANT_ONE_RELATION_TWO_TABLE_NAME);
			when(type3.getTable()).thenReturn(TENANT_ONE_RELATION_THREE_TABLE_NAME);
			final RelationCacheUnit relationCacheUnit1 = createRelationCacheUnit(TENANT_ONE, RELATION_TWO);
			final RelationCacheUnit relationCacheUnit2 = createRelationCacheUnit(TENANT_ONE, RELATION_TWO);
			final RelationCacheUnit relationCacheUnit3 = createRelationCacheUnit(TENANT_ONE, RELATION_THREE);
			final RelationCacheUnit relationCacheUnit4 = createRelationCacheUnit(TENANT_ONE, RELATION_THREE);

			//then
			assertThat(relationCacheUnit1.getTable()).isEqualTo(TENANT_ONE_RELATION_TWO_TABLE_NAME);
			assertThat(relationCacheUnit2.getTable()).isEqualTo(TENANT_ONE_RELATION_TWO_TABLE_NAME);
			assertThat(relationCacheUnit3.getTable()).isEqualTo(TENANT_ONE_RELATION_THREE_TABLE_NAME);
			assertThat(relationCacheUnit4.getTable()).isEqualTo(TENANT_ONE_RELATION_THREE_TABLE_NAME);
			typeManagerStatic.verify(TypeManager::getInstance, times(4));
			verify(typeManager, times(2)).getType(RELATION_TWO);
			verify(typeManager, times(2)).getType(RELATION_THREE);
			verify(type2, times(1)).getTable();
			verify(type2, times(1)).getSourceAttributeDescriptor();
			verify(relationDescriptor2, times(1)).getQualifier();
			verify(type3, times(1)).getTable();
			verify(type3, times(1)).getSourceAttributeDescriptor();
			verify(relationDescriptor3, times(1)).getQualifier();
		}
	}

	@Test
	public void testClearCacheUnitForTenant()
	{
		try (final MockedStatic<TypeManager> typeManagerStatic = mockStatic(TypeManager.class))
		{
			// given
			typeManagerStatic.when(TypeManager::getInstance).thenReturn(typeManager);
			when(relationDescriptor1.getQualifier()).thenReturn(RELATION_ONE_QUALIFIER);
			when(type1.getSourceAttributeDescriptor()).thenReturn(relationDescriptor1);
			when(typeManager.getType(RELATION_ONE)).thenReturn(type1);
			when(type1.getTable()).thenReturn(TENANT_ONE_RELATION_ONE_TABLE_NAME);

			//when
			final RelationCacheUnit cacheUnit1 = createRelationCacheUnit(TENANT_ONE, RELATION_ONE);
			RelationCacheUnit.clearCacheUnitForTenant(TENANT_ONE);
			final RelationCacheUnit cacheUnit2 = createRelationCacheUnit(TENANT_ONE, RELATION_ONE);

			//then
			assertThat(cacheUnit1.getTable()).isEqualTo(TENANT_ONE_RELATION_ONE_TABLE_NAME);
			assertThat(cacheUnit2.getTable()).isEqualTo(TENANT_ONE_RELATION_ONE_TABLE_NAME);
			typeManagerStatic.verify(TypeManager::getInstance, times(4));
			// TypeManager is invoked in isSourceOfRelation and getTableName methods twice for one tenant
			verify(typeManager, times(4)).getType(RELATION_ONE);
			verify(type1, times(2)).getTable();
			verify(type1, times(2)).getSourceAttributeDescriptor();
			verify(relationDescriptor1, times(2)).getQualifier();
		}
	}

	private RelationCacheUnit createRelationCacheUnit(final String tenantId, final String relationId)
	{
		final Cache tenantCache = mock(Cache.class);
		final RelationCacheKey cacheKey = mock(RelationCacheKey.class);
		when(tenantCache.getTenantId()).thenReturn(tenantId);
		when(cacheKey.getTenantId()).thenReturn(tenantId);
		when(cacheKey.getRelationId()).thenReturn(relationId);
		return new RelationCacheUnit(tenantCache, cacheKey);
	}
}
