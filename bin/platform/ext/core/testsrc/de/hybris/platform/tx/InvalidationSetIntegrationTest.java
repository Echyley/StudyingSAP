/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.tx;

import static de.hybris.platform.cache.AbstractCacheUnit.INVALIDATIONTYPE_CREATED;
import static de.hybris.platform.cache.AbstractCacheUnit.INVALIDATIONTYPE_MODIFIED;
import static de.hybris.platform.cache.AbstractCacheUnit.INVALIDATIONTYPE_REMOVED;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cache.AdditionalInvalidationData;
import de.hybris.platform.cache.Cache;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.tx.InvalidationSet.Invalidation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

@IntegrationTest
public class InvalidationSetIntegrationTest extends ServicelayerBaseTest
{
	@Test
	public void shouldTriggerTheSameDerivedInvalidationsOnCreationForFiveAndFourElementKey()
	{
		requireTheSameDerivedInvalidationsForFiveAndFourElementKey(INVALIDATIONTYPE_CREATED);
	}

	@Test
	public void shouldTriggerTheSameDerivedInvalidationsOnModificationForFiveAndFourElementKey()
	{
		requireTheSameDerivedInvalidationsForFiveAndFourElementKey(INVALIDATIONTYPE_MODIFIED);
	}

	@Test
	public void shouldTriggerTheSameDerivedInvalidationsOnRemovalForFiveAndFourElementKey()
	{
		requireTheSameDerivedInvalidationsForFiveAndFourElementKey(INVALIDATIONTYPE_REMOVED);
	}

	@Test
	public void shouldMergeInvalidationsWithAdditionalData()
	{
		final Object[] baseKey = Arrays.copyOf(createFiveElementEntityInvalidationKey(), 4);
		final InvalidationSet invalidationSet = givenInvalidationSet();
		final PK pk1 = PK.createCounterPK(24);
		final PK pk2 = PK.createCounterPK(24);

		final AdditionalInvalidationData emptyData = AdditionalInvalidationData.empty();
		final AdditionalInvalidationData pk1Data = AdditionalInvalidationData.builder().addForeignKey("pk1", pk1).build();
		final AdditionalInvalidationData pk2Data = AdditionalInvalidationData.builder().addForeignKey("pk2", pk2).build();
		final AdditionalInvalidationData pk1AndPk2Data = AdditionalInvalidationData.builder()
		                                                                           .addForeignKey("pk1", pk1)
		                                                                           .addForeignKey("pk2", pk2)
		                                                                           .build();

		for (final int invalidationType : List.of(INVALIDATIONTYPE_CREATED, INVALIDATIONTYPE_MODIFIED, INVALIDATIONTYPE_REMOVED))
		{
			// #1 empty additional data
			Invalidation createdInvalidation = invalidate(invalidationSet, baseKey, invalidationType, emptyData);
			assertThatInvalidationContainsAdditionalData(createdInvalidation, baseKey, invalidationType, emptyData);
			assertThatInvalidationContainsAdditionalData(invalidationSet, baseKey, invalidationType, emptyData);

			// #2 also empty additional data
			createdInvalidation = invalidate(invalidationSet, baseKey, invalidationType, emptyData);
			assertThatInvalidationContainsAdditionalData(createdInvalidation, baseKey, invalidationType, emptyData);
			assertThatInvalidationContainsAdditionalData(invalidationSet, baseKey, invalidationType, emptyData);

			// #3 now with some key
			createdInvalidation = invalidate(invalidationSet, baseKey, invalidationType, pk1Data);
			assertThatInvalidationContainsAdditionalData(createdInvalidation, baseKey, invalidationType, pk1Data);
			assertThatInvalidationContainsAdditionalData(invalidationSet, baseKey, invalidationType, pk1Data);

			// #4 try once again with the same key
			createdInvalidation = invalidate(invalidationSet, baseKey, invalidationType, pk1Data);
			assertThatInvalidationContainsAdditionalData(createdInvalidation, baseKey, invalidationType, pk1Data);
			assertThatInvalidationContainsAdditionalData(invalidationSet, baseKey, invalidationType, pk1Data);

			// #5 new key
			createdInvalidation = invalidate(invalidationSet, baseKey, invalidationType, pk2Data);
			assertThatInvalidationContainsAdditionalData(createdInvalidation, baseKey, invalidationType, pk1AndPk2Data);
			assertThatInvalidationContainsAdditionalData(invalidationSet, baseKey, invalidationType, pk1AndPk2Data);

			// #6 once again the same key
			createdInvalidation = invalidate(invalidationSet, baseKey, invalidationType, pk2Data);
			assertThatInvalidationContainsAdditionalData(createdInvalidation, baseKey, invalidationType, pk1AndPk2Data);
			assertThatInvalidationContainsAdditionalData(invalidationSet, baseKey, invalidationType, pk1AndPk2Data);

			// #7 and empty data to be sure that it doesn't clear the data
			createdInvalidation = invalidate(invalidationSet, baseKey, invalidationType, emptyData);
			assertThatInvalidationContainsAdditionalData(createdInvalidation, baseKey, invalidationType, pk1AndPk2Data);
			assertThatInvalidationContainsAdditionalData(invalidationSet, baseKey, invalidationType, pk1AndPk2Data);
		}
	}

	@Test
	public void shouldNotPutAdditionalDataWhenThereWasAnEarlierInvalidationWithoutAdditionalData()
	{
		final Object[] baseKey = Arrays.copyOf(createFiveElementEntityInvalidationKey(), 4);
		final InvalidationSet invalidationSet = givenInvalidationSet();
		final AdditionalInvalidationData someData = AdditionalInvalidationData.builder()
		                                                                      .addForeignKey("pk1", PK.createCounterPK(24))
		                                                                      .build();

		for (final int invalidationType : List.of(INVALIDATIONTYPE_CREATED, INVALIDATIONTYPE_MODIFIED, INVALIDATIONTYPE_REMOVED))
		{
			// #1 four element invalidation first
			Invalidation createdInvalidation = invalidationSet.addInvalidation(baseKey, 3, invalidationType);
			assertThatInvalidationDoesNotContainAdditionalData(createdInvalidation, baseKey, invalidationType);
			assertThatInvalidationDoesNotContainAdditionalData(invalidationSet, baseKey, invalidationType);

			// #2 five element invalidation with Additional Data - shouldn't override the previous invalidation
			createdInvalidation = invalidate(invalidationSet, baseKey, invalidationType, someData);
			assertThatInvalidationDoesNotContainAdditionalData(createdInvalidation, baseKey, invalidationType);
			assertThatInvalidationDoesNotContainAdditionalData(invalidationSet, baseKey, invalidationType);
		}
	}

	@Test
	public void shouldInvalidationWithoutAdditionalDataOverrideTheOneWithAdditionalData()
	{
		final Object[] baseKey = Arrays.copyOf(createFiveElementEntityInvalidationKey(), 4);
		final InvalidationSet invalidationSet = givenInvalidationSet();
		final AdditionalInvalidationData someData = AdditionalInvalidationData.builder()
		                                                                      .addForeignKey("pk1", PK.createCounterPK(24))
		                                                                      .build();

		for (final int invalidationType : List.of(INVALIDATIONTYPE_CREATED, INVALIDATIONTYPE_MODIFIED, INVALIDATIONTYPE_REMOVED))
		{
			// #1 five element invalidation with Additional Data
			Invalidation createdInvalidation = invalidate(invalidationSet, baseKey, invalidationType, someData);
			assertThatInvalidationContainsAdditionalData(createdInvalidation, baseKey, invalidationType, someData);
			assertThatInvalidationContainsAdditionalData(invalidationSet, baseKey, invalidationType, someData);

			// #1 four element invalidation - should override previous one with the Additional Data
			createdInvalidation = invalidationSet.addInvalidation(baseKey, 3, invalidationType);
			assertThatInvalidationDoesNotContainAdditionalData(createdInvalidation, baseKey, invalidationType);
			assertThatInvalidationDoesNotContainAdditionalData(invalidationSet, baseKey, invalidationType);
		}
	}

	private void assertThatInvalidationDoesNotContainAdditionalData(final InvalidationSet invalidationSet, final Object[] baseKey,
	                                                                final int invalidationType)
	{
		final List<Invalidation> invalidations = invalidationSet.recordedInvalidations()
		                                                        .stream()
		                                                        .filter(i -> i.getInvalidationType() == invalidationType && Arrays
				                                                        .equals(Arrays.copyOf(i.getKey(), 4), baseKey))
		                                                        .collect(Collectors.toList());

		assertThat(invalidations).hasSize(1);
		assertThatInvalidationDoesNotContainAdditionalData(invalidations.get(0), baseKey, invalidationType);
	}

	private void assertThatInvalidationContainsAdditionalData(final InvalidationSet invalidationSet, final Object[] baseKey,
	                                                          final int invalidationType,
	                                                          final AdditionalInvalidationData additionalInvalidationData)
	{
		final List<Invalidation> invalidations = invalidationSet.recordedInvalidations()
		                                                        .stream()
		                                                        .filter(i -> i.getInvalidationType() == invalidationType && Arrays
				                                                        .equals(Arrays.copyOf(i.getKey(), 4), baseKey))
		                                                        .collect(Collectors.toList());

		assertThat(invalidations).hasSize(1);
		assertThatInvalidationContainsAdditionalData(invalidations.get(0), baseKey, invalidationType, additionalInvalidationData);
	}

	private void assertThatInvalidationDoesNotContainAdditionalData(final Invalidation invalidation, final Object[] baseKey,
	                                                                final int invalidationType)
	{
		assertThat(invalidation).isNotNull();
		assertThat(invalidation.getInvalidationType()).isEqualTo(invalidationType);
		assertThat(invalidation.getKey()).hasSize(4).isEqualTo(baseKey);
		assertThat(invalidation.getTopicDepth()).isEqualTo(3);
	}

	private void assertThatInvalidationContainsAdditionalData(final Invalidation invalidation, final Object[] baseKey,
	                                                          final int invalidationType,
	                                                          final AdditionalInvalidationData additionalInvalidationData)
	{
		assertThat(invalidation).isNotNull();
		assertThat(invalidation.getInvalidationType()).isEqualTo(invalidationType);
		assertThat(invalidation.getKey()).hasSize(5).startsWith(baseKey);
		assertThat(invalidation.getTopicDepth()).isEqualTo(3);
		assertThat(invalidation.getKey()[4]).isEqualTo(additionalInvalidationData);
	}

	private Invalidation invalidate(final InvalidationSet invalidationSet, final Object[] baseKey, final int invalidationType,
	                                final AdditionalInvalidationData additionalInvalidationData)
	{
		final Object[] key = Arrays.copyOf(baseKey, 5);
		key[4] = additionalInvalidationData;

		return invalidationSet.addInvalidation(key, 3, invalidationType);
	}

	private void requireTheSameDerivedInvalidationsForFiveAndFourElementKey(final int invalidationType)
	{
		final Object[] fiveElementKey = createFiveElementEntityInvalidationKey();
		final Object[] fourElementKey = Arrays.copyOf(fiveElementKey, 4);

		final HashMap<Object, Object> invalidationsFromFiveElementKey = collectDerivedInvalidations(fiveElementKey,
				invalidationType);
		final HashMap<Object, Object> invalidationsFromFourElementKey = collectDerivedInvalidations(fourElementKey,
				invalidationType);

		assertThat(invalidationsFromFiveElementKey).isNotNull().isNotEmpty();
		assertThat(invalidationsFromFourElementKey).isNotNull().isNotEmpty();

		assertThat(invalidationsFromFiveElementKey).isEqualTo(invalidationsFromFourElementKey);
	}

	private HashMap<Object, Object> collectDerivedInvalidations(final Object[] key, final int invalidationType)
	{
		final InvalidationSet invalidationSet = givenInvalidationSet();
		invalidationSet.delayInvalidation(key, 3, invalidationType);
		return new HashMap<>(invalidationSet.effectiveInvalidations(false));
	}

	private Object[] createFiveElementEntityInvalidationKey()
	{
		final PK pk = PK.createCounterPK(24);
		return new Object[]{ Cache.CACHEKEY_HJMP, Cache.CACHEKEY_ENTITY, pk.getTypeCodeAsString(), pk, AdditionalInvalidationData.empty() };
	}

	private InvalidationSet givenInvalidationSet()
	{
		return new InvalidationSet();
	}
}
