/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cache.relation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.junit.Test;

@UnitTest
public class DefaultRelationsCacheUnitTest
{
	@Test
	public void shouldReturnEmptyCollectionWhenThereAreNoRelationAttributes()
	{
		final TypeId typeId = givenTypeId();
		final DefaultRelationsCache cache = givenCache(Map.of(typeId, List.of()));

		final Collection<RelationAttributes> attributes = cache.getAllCacheableAttributes(typeId);

		assertThat(attributes).isEmpty();
	}

	@Test
	public void shouldReturnEmptyAttributesWhenThereAreNoRelationAttributes()
	{
		final TypeId typeId = givenTypeId();
		final DefaultRelationsCache cache = givenCache(Map.of(typeId, List.of()));

		final RelationAttributes attributes = cache.getSingleCacheableAttributes(typeId);

		assertThat(attributes).isNotNull();
		assertThat(attributes.containsAnyAttribute()).isFalse();
	}

	@Test
	public void shouldReturnSingleElementCollectionWhenThereIsSingleRelationAttributes()
	{
		final TypeId typeId = givenTypeId();
		final RelationAttributes givenAttributes = testRelationAttributes();
		final DefaultRelationsCache cache = givenCache(Map.of(typeId, List.of(givenAttributes)));

		final Collection<RelationAttributes> attributes = cache.getAllCacheableAttributes(typeId);

		assertThat(attributes).containsExactly(givenAttributes);
	}

	@Test
	public void shouldReturnAttributesWhenThereIsSingleRelationAttributes()
	{
		final TypeId typeId = givenTypeId();
		final RelationAttributes givenAttributes = testRelationAttributes();
		final DefaultRelationsCache cache = givenCache(Map.of(typeId, List.of(givenAttributes)));

		final RelationAttributes attributes = cache.getSingleCacheableAttributes(typeId);

		assertThat(attributes).isSameAs(givenAttributes);
	}

	@Test
	public void shouldReturnManyElementsCollectionWhenThereAreManyRelationAttributes()
	{
		final TypeId typeId = givenTypeId();
		final RelationAttributes givenAttributes1 = testRelationAttributes();
		final RelationAttributes givenAttributes2 = testRelationAttributes();
		final DefaultRelationsCache cache = givenCache(Map.of(typeId, List.of(givenAttributes1, givenAttributes2)));

		final Collection<RelationAttributes> attributes = cache.getAllCacheableAttributes(typeId);

		assertThat(attributes).containsExactly(givenAttributes1, givenAttributes2);
	}

	@Test
	public void shouldThrowIllegalStateExceptionOnRetrievingSingleAttributesWhenThereAreManyRelationAttributes()
	{
		final TypeId typeId = givenTypeId();
		final RelationAttributes givenAttributes1 = testRelationAttributes();
		final RelationAttributes givenAttributes2 = testRelationAttributes();
		final DefaultRelationsCache cache = givenCache(Map.of(typeId, List.of(givenAttributes1, givenAttributes2)));

		assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> cache.getSingleCacheableAttributes(typeId))
		                                                      .withNoCause()
		                                                      .withMessageStartingWith("Expected single result for");
	}

	private RelationAttributes testRelationAttributes()
	{
		return new RelationAttributes(UUID.randomUUID().toString())
		{
			@Override
			public boolean containsAnyAttribute()
			{
				return true;
			}

			@Override
			void forEachAttribute(final BiConsumer<? super String, ? super String> consumer)
			{
				consumer.accept("test-fk", "test-many");
			}
		};
	}

	private TypeId givenTypeId()
	{
		return TypeId.fromTypeName(UUID.randomUUID().toString());
	}

	private DefaultRelationsCache givenCache(final Map<TypeId, Collection<RelationAttributes>> typeIdToAttributes)
	{
		return new DefaultRelationsCache(id -> typeIdToAttributes.getOrDefault(id, List.of()).stream());
	}
}