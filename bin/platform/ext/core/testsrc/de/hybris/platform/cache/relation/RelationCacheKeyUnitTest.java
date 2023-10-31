/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.cache.relation;


import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.security.PrincipalGroupModel;

import org.junit.Test;

@UnitTest
public class RelationCacheKeyUnitTest
{
	private final RelationCacheKey theSameKey = new RelationCacheKey("junit", PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS,PK.fromLong(22324));

	private final RelationCacheKey differentPK = new RelationCacheKey("junit", PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS,PK.fromLong(22325));
	private final RelationCacheKey differentSide = new RelationCacheKey("junit", PrincipalGroupModel._PRINCIPALGROUPRELATION, "relSide",PK.fromLong(22324));
	private final RelationCacheKey differentRelation = new RelationCacheKey("junit", "SomeOtherRelation", PrincipalGroupModel.GROUPS,PK.fromLong(22324));
	private final RelationCacheKey differentTenant = new RelationCacheKey("master", PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS,PK.fromLong(22324));

	private final RelationCacheKey completelyDifferent = new RelationCacheKey("master", "SomeOtherRelation", "relSide",PK.fromLong(121));


	@Test
	public void keysShouldBeEqual()
	{
		final RelationCacheKey key = new RelationCacheKey("junit", PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS,PK.fromLong(22324));
		assertThat(key).isEqualTo(theSameKey);

	}

	@Test
	public void keysHashcodeShouldBeEqual()
	{
		final RelationCacheKey key = new RelationCacheKey("junit", PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS,PK.fromLong(22324));
		assertThat(key.hashCode()).isEqualTo(theSameKey.hashCode());

	}

	@Test
	public void keysShouldNotBeEqual()
	{
		final RelationCacheKey key = new RelationCacheKey("junit", PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS,PK.fromLong(22324));
		assertThat(key).isNotEqualTo(differentPK);
		assertThat(key).isNotEqualTo(differentSide);
		assertThat(key).isNotEqualTo(differentRelation);
		assertThat(key).isNotEqualTo(differentTenant);
		assertThat(key).isNotEqualTo(completelyDifferent);
	}

	@Test
	public void keysHashcodeShouldNotBeEqual()
	{
		final RelationCacheKey key = new RelationCacheKey("junit", PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS,PK.fromLong(22324));
		assertThat(key.hashCode()).isNotEqualTo(differentPK.hashCode());
		assertThat(key.hashCode()).isNotEqualTo(differentSide.hashCode());
		assertThat(key.hashCode()).isNotEqualTo(differentRelation.hashCode());
		assertThat(key.hashCode()).isNotEqualTo(differentTenant.hashCode());
		assertThat(key.hashCode()).isNotEqualTo(completelyDifferent.hashCode());
	}

	@Test
	public void shouldCorrectlyAssembleFullyQualifiedRelationSideId()
	{
		final RelationCacheKey key = new RelationCacheKey("junit", PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS, PK.fromLong(22324));
		assertThat(key.getFullyQualifiedRelationSideId()).isEqualTo(String.format("%s.%s", PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS));
	}

	@Test
	public void shouldCorrectlyAssembleFullyQualifiedRelationSideIdWhenSimpleKeyConstructor()
	{
		final RelationCacheKey key = new RelationCacheKey("junit", PrincipalGroupModel._PRINCIPALGROUPRELATION);
		assertThat(key.getFullyQualifiedRelationSideId()).isEqualTo(PrincipalGroupModel._PRINCIPALGROUPRELATION);
	}

	@Test
	public void shouldCorrectlyAssembleInvalidationIdentifier()
	{
		final RelationCacheKey key = new RelationCacheKey("junit", PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS, PK.fromLong(22324));
		assertThat(key.getInvalidationIdentifier()).isEqualTo(PrincipalGroupModel._PRINCIPALGROUPRELATION + ".RELATION");
	}

	@Test
	public void shouldCorrectlyAssembleInvalidationIdentifierWhenSimpleKeyConstructor()
	{
		final RelationCacheKey key = new RelationCacheKey("junit", PrincipalGroupModel._PRINCIPALGROUPRELATION);
		assertThat(key.getInvalidationIdentifier()).isEqualTo(PrincipalGroupModel._PRINCIPALGROUPRELATION + ".RELATION");
	}
}
