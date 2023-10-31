/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cache.relation;

import static de.hybris.platform.cache.Cache.CACHEKEY_ENTITY;
import static de.hybris.platform.cache.Cache.CACHEKEY_HJMP;
import static de.hybris.platform.cache.relation.RelationCacheKey.createInvalidationKey;
import static de.hybris.platform.cache.relation.RelationCacheKey.createInvalidationKeyForRelation;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cache.AdditionalInvalidationData;
import de.hybris.platform.cache.InvalidationTarget;
import de.hybris.platform.core.PK;
import de.hybris.platform.jalo.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import org.junit.Test;

@UnitTest
public class RelationInvalidationListenerUnitTest
{
	private static final int ROOT_CONFIGURED_TYPE_CODE = 201;
	private static final int ROOT_NOT_CONFIGURED_TYPE_CODE = 202;
	private static final int ROOT_MULTIPLE_CONFIGURED_TYPE_CODE = 203;
	private static final int ROOT_MULTIPLE_NOT_CONFIGURED_TYPE_CODE = 204;
	private static final int ROOT_MIXED_TYPE_CODE = 205;
	private static final String STANDALONE_CONFIGURED_TYPE_NAME_1 = "configured1";
	private static final String STANDALONE_CONFIGURED_TYPE_NAME_2 = "configured2";
	private static final String STANDALONE_NOT_CONFIGURED_TYPE_NAME_1 = "notConfigured1";
	private static final String STANDALONE_NOT_CONFIGURED_TYPE_NAME_2 = "notConfigured2";
	private static final int FK1_TYPE_CODE = 24;
	private static final String FK1 = "fk1";
	private static final String FK1_MANY = "fk1Many";
	private static final int FK2_TYPE_CODE = 25;
	private static final String FK2 = "fk2";
	private static final String FK2_MANY = "fk2Many";
	private static final AtomicInteger PK_COUNTER = new AtomicInteger(0);
	private static final PK STANDALONE_CONFIGURED_TYPE_PK_1 = pk(ROOT_CONFIGURED_TYPE_CODE);
	private static final PK STANDALONE_CONFIGURED_TYPE_PK_2 = pk(ROOT_CONFIGURED_TYPE_CODE);
	private static final PK STANDALONE_NOT_CONFIGURED_TYPE_PK_1 = pk(ROOT_NOT_CONFIGURED_TYPE_CODE);
	private static final PK STANDALONE_NOT_CONFIGURED_TYPE_PK_2 = pk(ROOT_NOT_CONFIGURED_TYPE_CODE);
	private static final PK FK1_PK = pk(FK1_TYPE_CODE);
	private static final PK FK2_PK = pk(FK2_TYPE_CODE);

	private static PK pk(final int typeCode)
	{
		return PK.createFixedUUIDPK(typeCode, PK_COUNTER.incrementAndGet());
	}

	@Test
	public void shouldNotFailOnUnexpectedKey()
	{
		final CollectingInvalidationTarget target = givenCollectingTarget();
		final RelationInvalidationListener listener = givenListener(Map.of());
		final String tc = Integer.toString(FK1_TYPE_CODE);
		final PK pk = FK1_PK;

		listener.keyInvalidated(null, 0, target, null);
		listener.keyInvalidated(new Object[]{}, 1, target, null);
		listener.keyInvalidated(new Object[]{ null }, 2, target, null);
		listener.keyInvalidated(new Object[]{ CACHEKEY_HJMP }, 3, target, null);
		listener.keyInvalidated(new Object[]{ CACHEKEY_HJMP, null }, 4, target, null);
		listener.keyInvalidated(new Object[]{ CACHEKEY_HJMP, CACHEKEY_ENTITY }, 5, target, null);
		listener.keyInvalidated(new Object[]{ CACHEKEY_HJMP, CACHEKEY_ENTITY, null }, 6, target, null);
		listener.keyInvalidated(new Object[]{ CACHEKEY_HJMP, CACHEKEY_ENTITY, tc }, 7, target, null);
		listener.keyInvalidated(new Object[]{ CACHEKEY_HJMP, CACHEKEY_ENTITY, tc, null }, 8, target, null);
		listener.keyInvalidated(new Object[]{ CACHEKEY_HJMP, CACHEKEY_ENTITY, tc, pk }, 9, target, null);
		listener.keyInvalidated(new Object[]{ CACHEKEY_HJMP, CACHEKEY_ENTITY, tc, pk, null }, 10, target, null);

		final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
		assertThat(collectedKeys).isEmpty();
	}

	@Test
	public void shouldSendInvalidationForTheWholeTypeWhenThereIsNoAdditionalDataAndTypeIsConfiguredForCaching()
	{
		final CollectingInvalidationTarget target = givenCollectingTarget();
		final RelationInvalidationListener listener = givenListener(testSetup());

		{
			final PK singleConfiguredPk = pk(ROOT_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(singleConfiguredPk), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(1)
			                         .contains(invalidationKey(
					                         createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_1)));
			target.clear();
		}

		{
			final PK multipleConfiguredPk = pk(ROOT_MULTIPLE_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(multipleConfiguredPk), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(2).contains(
					invalidationKey(createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_1)),
					invalidationKey(createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_2)));
			target.clear();
		}

		{
			final PK mixedConfiguredPk = pk(ROOT_MIXED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(mixedConfiguredPk), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(1)
			                         .contains(invalidationKey(
					                         createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_1)));
			target.clear();
		}
	}

	@Test
	public void shouldSendInvalidationForTheWholeTypeWhenThereIsEmptyAdditionalDataAndTypeIsConfiguredForCaching()
	{
		final CollectingInvalidationTarget target = givenCollectingTarget();
		final RelationInvalidationListener listener = givenListener(testSetup());
		final AdditionalInvalidationData emptyData = AdditionalInvalidationData.empty();

		{
			final PK singleConfiguredPk = pk(ROOT_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(singleConfiguredPk, emptyData), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(1)
			                         .contains(invalidationKey(
					                         createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_1)));
			target.clear();
		}

		{
			final PK multipleConfiguredPk = pk(ROOT_MULTIPLE_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(multipleConfiguredPk, emptyData), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(2).contains(
					invalidationKey(createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_1)),
					invalidationKey(createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_2)));
			target.clear();
		}

		{
			final PK mixedConfiguredPk = pk(ROOT_MIXED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(mixedConfiguredPk, emptyData), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(1)
			                         .contains(invalidationKey(
					                         createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_1)));
			target.clear();
		}
	}

	@Test
	public void shouldSendInvalidationForTheWholeTypeWhenThereIsNoTypeInTheAdditionalDataAndTypeIsConfiguredForCaching()
	{
		final CollectingInvalidationTarget target = givenCollectingTarget();
		final RelationInvalidationListener listener = givenListener(testSetup());
		final AdditionalInvalidationData dataWithoutType = AdditionalInvalidationData.builder()
		                                                                             .addForeignKey(FK1, FK1_PK)
		                                                                             .addForeignKey(FK2, FK2_PK)
		                                                                             .build();
		{
			final PK singleConfiguredPk = pk(ROOT_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(singleConfiguredPk, dataWithoutType), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(1)
			                         .contains(invalidationKey(
					                         createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_1)));
			target.clear();
		}

		{
			final PK multipleConfiguredPk = pk(ROOT_MULTIPLE_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(multipleConfiguredPk, dataWithoutType), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(2).contains(
					invalidationKey(createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_1)),
					invalidationKey(createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_2)));
			target.clear();
		}

		{
			final PK mixedConfiguredPk = pk(ROOT_MIXED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(mixedConfiguredPk, dataWithoutType), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(1)
			                         .contains(invalidationKey(
					                         createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_1)));
			target.clear();
		}
	}

	@Test
	public void shouldSendInvalidationForTheWholeTypeWhenThereIsNoFkValuesInTheAdditionalDataAndTypeIsConfiguredForCaching()
	{
		final CollectingInvalidationTarget target = givenCollectingTarget();
		final RelationInvalidationListener listener = givenListener(testSetup());

		final AdditionalInvalidationData dataWithoutFkValuesBothTypes = AdditionalInvalidationData.builder()
		                                                                                          .addForeignKey(Item.TYPE,
				                                                                                          STANDALONE_CONFIGURED_TYPE_PK_1)
		                                                                                          .addForeignKey(Item.TYPE,
				                                                                                          STANDALONE_CONFIGURED_TYPE_PK_2)
		                                                                                          .build();

		{
			final PK singleConfiguredPk = pk(ROOT_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(singleConfiguredPk, dataWithoutFkValuesBothTypes), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(2).contains(
					invalidationKey(createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_1)),
					invalidationKey(createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_2)));
			target.clear();
		}

		{
			final PK multipleConfiguredPk = pk(ROOT_MULTIPLE_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(multipleConfiguredPk, dataWithoutFkValuesBothTypes), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(2).contains(
					invalidationKey(createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_1)),
					invalidationKey(createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_2)));
			target.clear();
		}

		{
			final PK mixedConfiguredPk = pk(ROOT_MIXED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(mixedConfiguredPk, dataWithoutFkValuesBothTypes), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(2).contains(
					invalidationKey(createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_1)),
					invalidationKey(createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_2)));
			target.clear();
		}

		final AdditionalInvalidationData dataWithoutFkValuesFirstType = AdditionalInvalidationData.builder()
		                                                                                          .addForeignKey(Item.TYPE,
				                                                                                          STANDALONE_CONFIGURED_TYPE_PK_1)
		                                                                                          .build();
		{
			final PK singleConfiguredPk = pk(ROOT_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(singleConfiguredPk, dataWithoutFkValuesFirstType), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(1)
			                         .contains(invalidationKey(
					                         createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_1)));
			target.clear();
		}

		{
			final PK multipleConfiguredPk = pk(ROOT_MULTIPLE_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(multipleConfiguredPk, dataWithoutFkValuesFirstType), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(1)
			                         .contains(invalidationKey(
					                         createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_1)));
			target.clear();
		}

		{
			final PK mixedConfiguredPk = pk(ROOT_MIXED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(mixedConfiguredPk, dataWithoutFkValuesFirstType), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(1)
			                         .contains(invalidationKey(
					                         createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_1)));
			target.clear();
		}

		final AdditionalInvalidationData dataWithoutFkValuesSecondType = AdditionalInvalidationData.builder()
		                                                                                           .addForeignKey(Item.TYPE,
				                                                                                           STANDALONE_CONFIGURED_TYPE_PK_2)
		                                                                                           .build();
		{
			final PK singleConfiguredPk = pk(ROOT_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(singleConfiguredPk, dataWithoutFkValuesSecondType), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(1)
			                         .contains(invalidationKey(
					                         createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_2)));
			target.clear();
		}

		{
			final PK multipleConfiguredPk = pk(ROOT_MULTIPLE_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(multipleConfiguredPk, dataWithoutFkValuesSecondType), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(1)
			                         .contains(invalidationKey(
					                         createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_2)));
			target.clear();
		}

		{
			final PK mixedConfiguredPk = pk(ROOT_MIXED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(mixedConfiguredPk, dataWithoutFkValuesSecondType), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(1)
			                         .contains(invalidationKey(
					                         createInvalidationKeyForRelation(STANDALONE_CONFIGURED_TYPE_NAME_2)));
			target.clear();
		}
	}

	@Test
	public void shouldNotSendInvalidationWhenTypeIsNotConfiguredForCaching()
	{
		final CollectingInvalidationTarget target = givenCollectingTarget();
		final RelationInvalidationListener listener = givenListener(testSetup());

		{
			final PK singleNotConfiguredPk = pk(ROOT_NOT_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(singleNotConfiguredPk), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).isEmpty();
			target.clear();
		}

		{
			final PK multipleNotConfiguredPk = pk(ROOT_MULTIPLE_NOT_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(multipleNotConfiguredPk), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).isEmpty();
			target.clear();
		}

		final AdditionalInvalidationData validData = AdditionalInvalidationData.builder()
		                                                                       .addForeignKey(Item.TYPE,
				                                                                       STANDALONE_NOT_CONFIGURED_TYPE_PK_1)
		                                                                       .addForeignKey(Item.TYPE,
				                                                                       STANDALONE_NOT_CONFIGURED_TYPE_PK_1)
		                                                                       .addForeignKey(FK1, FK1_PK)
		                                                                       .addForeignKey(FK2, FK2_PK)
		                                                                       .build();
		{
			final PK singleNotConfiguredPk = pk(ROOT_NOT_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(singleNotConfiguredPk, validData), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).isEmpty();
			target.clear();
		}

		{
			final PK multipleNotConfiguredPk = pk(ROOT_MULTIPLE_NOT_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(multipleNotConfiguredPk, validData), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).isEmpty();
			target.clear();
		}
	}

	@Test
	public void shouldSendSpecificInvalidationForConfiguredTypes()
	{
		final CollectingInvalidationTarget target = givenCollectingTarget();
		final RelationInvalidationListener listener = givenListener(testSetup());

		{
			final AdditionalInvalidationData fullData = AdditionalInvalidationData.builder()
			                                                                      .addForeignKey(Item.TYPE,
					                                                                      STANDALONE_CONFIGURED_TYPE_PK_1)
			                                                                      .addForeignKey(Item.TYPE,
					                                                                      STANDALONE_CONFIGURED_TYPE_PK_2)
			                                                                      .addForeignKey(FK1, FK1_PK)
			                                                                      .addForeignKey(FK2, FK2_PK)
			                                                                      .build();
			final PK singleConfiguredPk = pk(ROOT_CONFIGURED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(singleConfiguredPk, fullData), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(2).contains(
					invalidationKey(createInvalidationKey(STANDALONE_CONFIGURED_TYPE_NAME_1, FK1_MANY, FK1_PK)),
					invalidationKey(createInvalidationKey(STANDALONE_CONFIGURED_TYPE_NAME_2, FK2_MANY, FK2_PK)));
			target.clear();
		}

		{
			final AdditionalInvalidationData mixedData = AdditionalInvalidationData.builder()
			                                                                       .addForeignKey(Item.TYPE,
					                                                                       STANDALONE_CONFIGURED_TYPE_PK_1)
			                                                                       .addForeignKey(Item.TYPE,
					                                                                       STANDALONE_NOT_CONFIGURED_TYPE_PK_1)
			                                                                       .addForeignKey(FK1, FK1_PK)
			                                                                       .addForeignKey(FK2, FK2_PK)
			                                                                       .build();
			final PK mixedConfiguredPk = pk(ROOT_MIXED_TYPE_CODE);
			listener.keyInvalidated(entityInvalidation(mixedConfiguredPk, mixedData), 1, target, null);
			final List<InvalidationKey> collectedKeys = target.getCollectedKeys();
			assertThat(collectedKeys).hasSize(1)
			                         .contains(invalidationKey(
					                         createInvalidationKey(STANDALONE_CONFIGURED_TYPE_NAME_1, FK1_MANY, FK1_PK)));
			target.clear();
		}


	}

	private Object[] entityInvalidation(final PK pk, final AdditionalInvalidationData data)
	{
		final Object[] key = Arrays.copyOf(entityInvalidation(pk), 5);
		key[4] = data;
		return key;
	}

	private Object[] entityInvalidation(final PK pk)
	{
		return new Object[]{ CACHEKEY_HJMP, CACHEKEY_ENTITY, pk.getTypeCodeAsString(), pk };
	}

	private Map<TypeId, Collection<RelationAttributes>> testSetup()
	{
		final RelationAttributes configured1 = new ConfiguredRelationAttributes(STANDALONE_CONFIGURED_TYPE_NAME_1, FK1, FK1_MANY);
		final RelationAttributes configured2 = new ConfiguredRelationAttributes(STANDALONE_CONFIGURED_TYPE_NAME_2, FK2, FK2_MANY);
		final RelationAttributes notConfigured1 = new NotConfiguredRelationAttributes(STANDALONE_NOT_CONFIGURED_TYPE_NAME_1);
		final RelationAttributes notConfigured2 = new NotConfiguredRelationAttributes(STANDALONE_NOT_CONFIGURED_TYPE_NAME_2);

		return Map.of(
				TypeId.fromTypePk(STANDALONE_CONFIGURED_TYPE_PK_1), List.of(configured1),
				TypeId.fromTypePk(STANDALONE_CONFIGURED_TYPE_PK_2), List.of(configured2),
				TypeId.fromTypePk(STANDALONE_NOT_CONFIGURED_TYPE_PK_1), List.of(notConfigured1),
				TypeId.fromTypePk(STANDALONE_NOT_CONFIGURED_TYPE_PK_2), List.of(notConfigured2),
				TypeId.fromTypeCode(ROOT_CONFIGURED_TYPE_CODE), List.of(configured1),
				TypeId.fromTypeCode(ROOT_NOT_CONFIGURED_TYPE_CODE), List.of(notConfigured1),
				TypeId.fromTypeCode(ROOT_MULTIPLE_CONFIGURED_TYPE_CODE), List.of(configured1, configured2),
				TypeId.fromTypeCode(ROOT_MULTIPLE_NOT_CONFIGURED_TYPE_CODE), List.of(notConfigured1, notConfigured2),
				TypeId.fromTypeCode(ROOT_MIXED_TYPE_CODE), List.of(configured1, notConfigured1));
	}

	private RelationInvalidationListener givenListener(final Map<TypeId, Collection<RelationAttributes>> cacheableAttributes)
	{
		return new RelationInvalidationListener(cacheableAttributes::get);
	}

	private CollectingInvalidationTarget givenCollectingTarget()
	{
		return new CollectingInvalidationTarget();
	}

	private InvalidationKey invalidationKey(final Object... key)
	{
		return new InvalidationKey(key);
	}

	private static class InvalidationKey
	{
		final Object[] key;

		private InvalidationKey(final Object[] key)
		{
			this.key = Arrays.copyOf(key, key.length);
		}

		@Override
		public String toString()
		{
			return Arrays.toString(key);
		}

		@Override
		public boolean equals(final Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			final InvalidationKey that = (InvalidationKey) o;
			return Arrays.equals(key, that.key);
		}

		@Override
		public int hashCode()
		{
			return Arrays.hashCode(key);
		}
	}

	private static class CollectingInvalidationTarget implements InvalidationTarget
	{
		final List<InvalidationKey> collectedKeys = new ArrayList<>();

		@Override
		public void invalidate(final Object[] key, final int invalidationType)
		{
			collectedKeys.add(new InvalidationKey(key));
		}

		public List<InvalidationKey> getCollectedKeys()
		{
			return List.copyOf(collectedKeys);
		}

		public void clear()
		{
			collectedKeys.clear();
		}
	}

	private static class NotConfiguredRelationAttributes extends RelationAttributes
	{
		private NotConfiguredRelationAttributes(final String typeName)
		{
			super(typeName);
		}

		@Override
		public boolean containsAnyAttribute()
		{
			return false;
		}

		@Override
		void forEachAttribute(final BiConsumer<? super String, ? super String> consumer)
		{
			//do nothing
		}
	}

	private static class ConfiguredRelationAttributes extends RelationAttributes
	{
		private final String fkName;
		private final String manyName;

		public ConfiguredRelationAttributes(final String typeName, final String fkName, final String manyName)
		{
			super(typeName);
			this.fkName = fkName;
			this.manyName = manyName;
		}

		@Override
		public boolean containsAnyAttribute()
		{
			return true;
		}

		@Override
		void forEachAttribute(final BiConsumer<? super String, ? super String> consumer)
		{
			consumer.accept(fkName, manyName);
		}
	}

}