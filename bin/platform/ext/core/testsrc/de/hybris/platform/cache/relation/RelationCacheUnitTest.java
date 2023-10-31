/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.cache.relation;

import static de.hybris.platform.cache.relation.RelationCacheKey.RELATION;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import java.util.Collection;

import org.junit.Test;

@IntegrationTest
public class RelationCacheUnitTest extends HybrisJUnit4Test
{
	private static final RelationCacheUnit unit = RelationCacheUnit.createRelationCacheUnit(
			PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS, PK.fromLong(22324));

	@Test
	public void testTable(){
		assertThat(unit.getTable()).isEqualToIgnoringCase("junit_PGRels");
	}

	@Test
	public void testCreateKey()
	{
		assertThat(unit.createKey()).isEqualTo(new Object[] {RELATION, PrincipalGroupModel._PRINCIPALGROUPRELATION,PrincipalGroupModel.GROUPS,PK.fromLong(22324)});
	}

	@Test
	public void testCompute() throws Exception
	{
		final Object computed = unit.compute();
		assertThat(((Collection) computed)).isEmpty();
	}

	@Test
	public void testGetKey()
	{
		assertThat(unit.getKey()).isEqualTo(new RelationCacheKey("junit",PrincipalGroupModel._PRINCIPALGROUPRELATION,PrincipalGroupModel.GROUPS, PK.fromLong(22324)));
	}
}
