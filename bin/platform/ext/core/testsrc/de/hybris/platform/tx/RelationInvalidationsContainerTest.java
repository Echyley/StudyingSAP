/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.tx;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.cache.relation.RelationCacheKey;

import org.junit.Test;

@UnitTest
public class RelationInvalidationsContainerTest
{
	public static final RelationCacheKey KEY_1_RELATION_A = new RelationCacheKey("junit", "A", "leftSide", PK.fromLong(1));
	public static final RelationCacheKey KEY_2_RELATION_A = new RelationCacheKey("junit", "A", "leftSide", PK.fromLong(2));
	public static final RelationCacheKey KEY_RELATION_B = new RelationCacheKey("junit", "B", "leftSide", PK.fromLong(3));

	public static final RelationCacheKey RELATION_A = new RelationCacheKey("junit", "A");

	@Test
	public void shouldInvalidateKeyTest()
	{
		final InvalidationSet.RelationInvalidationsContainer container = new InvalidationSet.RelationInvalidationsContainer("A");

		container.invalidate(KEY_1_RELATION_A);
		assertThat(container.isInvalidated(KEY_1_RELATION_A)).isTrue();
	}

	@Test
	public void anotherKeyShouldNotBeInvalidatedTest()
	{
		final InvalidationSet.RelationInvalidationsContainer container = new InvalidationSet.RelationInvalidationsContainer("A");

		container.invalidate(KEY_1_RELATION_A);
		assertThat(container.isInvalidated(KEY_2_RELATION_A)).isFalse();
	}

	@Test
	public void shouldInvalidateRelationTest()
	{
		final InvalidationSet.RelationInvalidationsContainer container = new InvalidationSet.RelationInvalidationsContainer("A");

		container.invalidate(RELATION_A);
		assertThat(container.isInvalidated(KEY_1_RELATION_A)).isTrue();
		assertThat(container.isInvalidated(KEY_2_RELATION_A)).isTrue();
	}

	@Test
	public void shouldNotInvalidateKeyFromAnotherRelationTest()
	{
		final InvalidationSet.RelationInvalidationsContainer container = new InvalidationSet.RelationInvalidationsContainer("A");

		container.invalidate(RELATION_A);
		assertThat(container.isInvalidated(KEY_RELATION_B)).isFalse();
	}
}

