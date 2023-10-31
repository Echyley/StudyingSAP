/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.cache.relation;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class RelationsCacheTest
{
	private RelationsCache cache;

	private static final int DEFAULT_CAPACITY = 2;
	private static final int PGREL_CAPACITY = 3;

	private final PropertyConfigSwitcher propertyDefaultCapacity
			= new PropertyConfigSwitcher(RelationsCacheConfiguration.PROPERTY_DEFAULT_CAPACITY);
	private final PropertyConfigSwitcher propertyPgrelCapacity
			= new PropertyConfigSwitcher(String.format("relation.cache.%s.capacity", PrincipalGroupModel._PRINCIPALGROUPRELATION));

	@Before
	public void setUp()
	{
		propertyDefaultCapacity.switchToValue(String.valueOf(DEFAULT_CAPACITY));
		propertyPgrelCapacity.switchToValue(String.valueOf(PGREL_CAPACITY));

		cache = new RelationsCache(new RelationsCacheConfiguration(), (key, value) -> {});
	}

	@After
	public void tearDown()
	{
		propertyDefaultCapacity.switchBackToDefault();
		propertyPgrelCapacity.switchBackToDefault();

		cache.clear();
	}

	@Test
	public void shouldEvictCacheForExplicitlyConfiguredRelations()
	{
		// given
		final var groupsUnits = createUnits(5, PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS);
		final var membersUnits = createUnits(5, PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.MEMBERS);

		// when
		groupsUnits.forEach(unit -> cache.put(unit.getKey(), unit));
		membersUnits.forEach(unit -> cache.put(unit.getKey(), unit));

		// then
		assertThat(cache.getAllKeys()).hasSize(2 * PGREL_CAPACITY);
		assertThat(getValuesForRelationSide(PrincipalGroupModel.GROUPS)).hasSize(PGREL_CAPACITY);
		assertThat(getValuesForRelationSide(PrincipalGroupModel.MEMBERS)).hasSize(PGREL_CAPACITY);
	}

	@Test
	public void shouldEvictCacheForDefaultConfiguredRelations()
	{
		// given
		final var ordersUnits = createUnits(5, DiscountModel._ORDERDISCOUNTRELATION, DiscountModel.ORDERS);
		final var discountsUnits = createUnits(5, DiscountModel._ORDERDISCOUNTRELATION, AbstractOrderModel.DISCOUNTS);

		// when
		ordersUnits.forEach(unit -> cache.put(unit.getKey(), unit));
		discountsUnits.forEach(unit -> cache.put(unit.getKey(), unit));

		// then
		assertThat(cache.getAllKeys()).hasSize(2 * DEFAULT_CAPACITY);
		assertThat(getValuesForRelationSide(DiscountModel.ORDERS)).hasSize(DEFAULT_CAPACITY);
		assertThat(getValuesForRelationSide(AbstractOrderModel.DISCOUNTS)).hasSize(DEFAULT_CAPACITY);
	}

	@Test
	public void shouldEvictCacheUsingLRU()
	{
		final var groupsUnits = createUnits(5, PrincipalGroupModel._PRINCIPALGROUPRELATION, PrincipalGroupModel.GROUPS);

		cache.put(groupsUnits.get(0).getKey(), "0");
		cache.put(groupsUnits.get(1).getKey(), "1");
		cache.put(groupsUnits.get(2).getKey(), "2");
		assertThat(cache.getAllKeys()).hasSize(PGREL_CAPACITY); // [0, 1, 2]

		cache.put(groupsUnits.get(3).getKey(), "3"); // evicts [0]
		assertThat(cache.getAllKeys()).hasSize(PGREL_CAPACITY); // [1, 2, 3]
		assertThat(cache.containsKey(groupsUnits.get(0).getKey())).isFalse();

		cache.get(groupsUnits.get(1).getKey()); // LRU: access [1] so that [2] will be evicted next instead of [1]
		cache.put(groupsUnits.get(4).getKey(), "4"); // evicts [2]
		assertThat(cache.getAllKeys()).hasSize(PGREL_CAPACITY); // [1, 3, 4]
		assertThat(cache.containsKey(groupsUnits.get(1).getKey())).isTrue();
		assertThat(cache.containsKey(groupsUnits.get(2).getKey())).isFalse();
		assertThat(cache.containsKey(groupsUnits.get(3).getKey())).isTrue();
		assertThat(cache.containsKey(groupsUnits.get(4).getKey())).isTrue();

		cache.clear();
		assertThat(cache.getAllKeys()).isEmpty();
	}

	@Test
	public void testRemovingKeyForRelationRemovesEverythingFromThatRelation()
	{
		final RelationCacheUnit relationWithOnlyId = RelationCacheUnit.createRelationCacheUnit(
				PrincipalGroupModel._PRINCIPALGROUPRELATION, null, null);
		final List<RelationCacheUnit> relationCacheUnitList = createUnits(3, PrincipalGroupModel._PRINCIPALGROUPRELATION,
				PrincipalGroupModel.GROUPS);

		cache.put(relationCacheUnitList.get(0).getKey(), "0");
		cache.put(relationCacheUnitList.get(1).getKey(), "1");
		cache.put(relationCacheUnitList.get(2).getKey(), "2");

		assertThat(cache.getAllKeys().size()).isEqualTo(relationCacheUnitList.size());

		cache.remove(relationWithOnlyId.getKey());
		assertThat(cache.getAllKeys()).isEmpty();
	}

	private List<RelationCacheUnit> createUnits(final int size, final String relationId, final String relationSide)
	{
		final int lowerBoundPK = 22424;
		final int upperBoundPK = lowerBoundPK + size;

		return Stream.iterate(lowerBoundPK, i -> i < upperBoundPK, i -> i + 1)
		             .map(v -> RelationCacheUnit.createRelationCacheUnit(relationId, relationSide, PK.fromLong(v)))
		             .collect(Collectors.toList());
	}

	private List<Object> getValuesForRelationSide(final String relationSide)
	{
		return cache.getThreeLayersCache()
		            .values()
		            .stream()
		            .flatMap(layer2 -> layer2.values().stream())
		            .flatMap(layer3 -> layer3.get(relationSide).values().stream())
		            .collect(Collectors.toList());
	}
}